package cs464;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.CharStreams;
import cs464.config.AppConfig;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.io.*;
import java.util.*;

public class TwitterApi {

    private static final Logger logger = Logger.getLogger(TwitterApi.class);

    public static final String GET_FRIEND_IDS_URL = "https://api.twitter.com/1.1/friends/ids.json?";

    public static final String GET_FOLLOWER_IDS_BY_URL = "https://api.twitter.com/1.1/followers/ids.json?";

    public static final String GET_FRIEND_SCREEN_NAMES_URL = "https://api.twitter.com/1.1/friends/list.json?";

    public static final String GET_FOLLOWER_SCREEN_NAMES_URL = "https://api.twitter.com/1.1/followers/list.json?";

    public static final String SCREEN_NAME_ARG = "screen_name";

    public static final String USER_ID_ARG = "user_id";

    public static final String BEARER_TOKEN_URL = "https://api.twitter.com/oauth2/token";

    private static final Integer OK = Integer.valueOf(200);

    private static final Long ZERO = Long.valueOf(0);

    private final AppConfig config;

    private final CloseableHttpClient httpClient;

    private final ObjectMapper objectMapper;

    private static final int NUMBER_OF_TWEET_EACH_TIME = 200;

    private static final int TIME_TO_SLEEP = 180000;// milliseconds

    public TwitterApi(AppConfig config, final CloseableHttpClient httpClient,
                      ObjectMapper objectMapper) {
        this.config = config;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public Collection<String> friendIdsByUserId(final String userId) {
        return getUsersIds(userIdToFriendIdsUrl(userId));
    }

    public Collection<String> followerIdsByUserId(final String userId) {
        return getUsersIds(userIdToFollowerIdsUrl(userId));
    }

    public Collection<String> friendsIdsByScreenName(final String screenName) {
        return getUsersIds(screenNameToFriendIdsUrl(screenName));
    }

    public Collection<String> followerIdsByScreenName(final String screenName) {
        return getUsersIds(screenNameToFollowerIdsUrl(screenName));
    }

    public Collection<String> friendScreenNamesByUserId(final String userId) {
        return getScreenNames(userIdToFriendScreenNamesUrl(userId));
    }

    public Collection<String> followerScreenNamesByUserId(final String userId) {
        return getScreenNames(userIdToFollowerScreenNamesUrl(userId));
    }

    public Collection<String> friendsScreenNamesByScreenName(
            final String screenName) {
        return getScreenNames(screenNameToFriendScreenNamesUrl(screenName));
    }

    public Collection<String> followerScreenNamesByScreenName(
            final String screenName) {
        return getScreenNames(screenNameToFollowerScreenNamesUrl(screenName));
    }

    private Collection<String> getUsersIds(final ArgsToUrl argsToUrl) {
        final Collection<String> friends = new ArrayList<String>();

        Tuple2<Long, Collection<String>> result = getFriendsIdsByUrl(argsToUrl
                .url());

        if (result != null) {

            friends.addAll(result._2);
            Long nextCursor = result._1;

            while (nextCursor.compareTo(ZERO) > 0) {
                result = getFriendsIdsByUrl(argsToUrl
                        .urlForNextCursor(nextCursor));
                if (result != null) {
                    friends.addAll(result._2);
                    nextCursor = result._1;
                } else {
                    nextCursor = ZERO;
                }
            }
        }

        return friends;
    }

    private Collection<String> getScreenNames(final ArgsToUrl argsToUrl) {
        final Collection<String> friends = new ArrayList<String>();

        Tuple2<Long, Collection<String>> result = getFriendNamesByUrl(argsToUrl
                .url());

        if (result != null) {

            friends.addAll(result._2);
            Long nextCursor = result._1;

            while (nextCursor.compareTo(ZERO) > 0) {
                result = getFriendNamesByUrl(argsToUrl
                        .urlForNextCursor(nextCursor));
                if (result != null) {
                    friends.addAll(result._2);
                    nextCursor = result._1;
                } else {
                    nextCursor = ZERO;
                }
            }
        }

        return friends;
    }

    private Tuple2<Integer, String> doSignedGetRequestToTwitter(final String url) {
        final HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Authorization",
                "Bearer " + config.bearerAccessToken());

        return executeHttpRequest(httpGet);
    }

    private Tuple2<Long, Collection<String>> getFriendsIdsByUrl(final String url) {

        Tuple2<Long, Collection<String>> result = new Tuple2<Long, Collection<String>>(
                ZERO, Collections.<String>emptyList());

        final Collection<String> friends = new ArrayList<String>();

        try {
            final Tuple2<Integer, String> responseTuple = doSignedGetRequestToTwitter(url);

            if (responseTuple._1.equals(OK)) {

                final String responseJSON = responseTuple._2;

                final JsonNode jsonNode = jsonStringToJSONNode(responseJSON);

                final Iterator<JsonNode> idJsonIterator = jsonNode.get("ids")
                        .iterator();

                while (idJsonIterator.hasNext()) {
                    final String userId = idJsonIterator.next().asText();
                    friends.add(userId);
                }

                final long nextCursor = jsonNode.get("next_cursor").asLong();

                result = new Tuple2<Long, Collection<String>>(nextCursor,
                        friends);

            }
        } catch (Exception e) {
            logger.error("Getting friends by url failed. URL: " + url, e);
        }

        return result;
    }

    private Tuple2<Long, Collection<String>> getFriendNamesByUrl(
            final String url) {

        Tuple2<Long, Collection<String>> result = new Tuple2<Long, Collection<String>>(
                ZERO, Collections.<String>emptyList());

        final Collection<String> friends = new ArrayList<String>();

        try {
            final Tuple2<Integer, String> responseTuple = doSignedGetRequestToTwitter(url);

            if (responseTuple._1.equals(OK)) {

                final String responseJSON = responseTuple._2;

                final JsonNode jsonNode = jsonStringToJSONNode(responseJSON);

                final Iterator<JsonNode> usersJSON = jsonNode.get("users")
                        .iterator();

                while (usersJSON.hasNext()) {
                    JsonNode temp = usersJSON.next();
                    final String screenName = temp.get("screen_name").asText();
                    // check if user is public and english
                    if (temp.get("protected").asText()
                            .equalsIgnoreCase("false")
                            && temp.get("lang").asText().equalsIgnoreCase("en")
                            && temp.get("followers_count").asInt() > 50)
                        friends.add(screenName);
                }

                final long nextCursor = jsonNode.get("next_cursor").asLong();

                result = new Tuple2<Long, Collection<String>>(nextCursor,
                        friends);

            }
        } catch (Exception e) {
            logger.error("Getting friends by url failed. URL: " + url, e);
        }

        return result;
    }

    private Tuple2<Integer, String> executeHttpRequest(
            final HttpUriRequest request) {
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(request);
            final int statusCode = httpResponse.getStatusLine().getStatusCode();
            final String responseStr = CharStreams
                    .toString(new InputStreamReader(httpResponse.getEntity()
                            .getContent()));

            return new Tuple2<Integer, String>(statusCode, responseStr);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Executing HttpRequest failed. Request: " + request, e);
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    // Ebesininki...
                }
            }
        }
    }

    public String getBearerToken() {
        String bearerToken = null;

        final String bearerTokenCredentials = config.twitterConsumerKey() + ":"
                + config.twitterConsumerSecret();
        final String base64BearerTokenCredentials = Base64
                .encodeBase64String(bearerTokenCredentials.getBytes());

        // http post
        final HttpPost httpPost = new HttpPost(BEARER_TOKEN_URL);
        final List<NameValuePair> formParams = new ArrayList<NameValuePair>();
        formParams.add(new BasicNameValuePair("grant_type",
                "client_credentials"));
        final UrlEncodedFormEntity entity;
        try {
            entity = new UrlEncodedFormEntity(formParams, "UTF-8");
            httpPost.setEntity(entity);
            httpPost.addHeader("Authorization", "Basic "
                    + base64BearerTokenCredentials);

            final Tuple2<Integer, String> responseTuple = executeHttpRequest(httpPost);
            if (responseTuple._1.equals(OK)) {
                final JsonNode tokensJSONNode = jsonStringToJSONNode(responseTuple._2);
                bearerToken = tokensJSONNode.get("access_token").asText();
            }
        } catch (Exception e) {
            logger.error("get bearer token failed.", e);
        }

        return bearerToken;
    }

    private JsonNode jsonStringToJSONNode(final String json) {
        try {
            return objectMapper.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException("Parsing of json failed. JSON:" + json,
                    e);
        }
    }

    private ArgsToUrl screenNameToFollowerIdsUrl(final String screenName) {
        return new ParamsToUrlImpl(GET_FOLLOWER_IDS_BY_URL, SCREEN_NAME_ARG,
                screenName);
    }

    private ArgsToUrl screenNameToFriendIdsUrl(final String screenName) {
        return new ParamsToUrlImpl(GET_FRIEND_IDS_URL, SCREEN_NAME_ARG,
                screenName);
    }

    private ArgsToUrl userIdToFollowerIdsUrl(final String userId) {
        return new ParamsToUrlImpl(GET_FOLLOWER_IDS_BY_URL, USER_ID_ARG, userId);
    }

    private ArgsToUrl userIdToFriendIdsUrl(final String userId) {
        return new ParamsToUrlImpl(GET_FRIEND_IDS_URL, USER_ID_ARG, userId);
    }

    private ArgsToUrl screenNameToFollowerScreenNamesUrl(final String screenName) {
        return new ParamsToUrlImpl(GET_FOLLOWER_SCREEN_NAMES_URL,
                SCREEN_NAME_ARG, screenName);
    }

    private ArgsToUrl screenNameToFriendScreenNamesUrl(final String screenName) {
        return new ParamsToUrlImpl(GET_FRIEND_SCREEN_NAMES_URL,
                SCREEN_NAME_ARG, screenName);
    }

    private ArgsToUrl userIdToFollowerScreenNamesUrl(final String userId) {
        return new ParamsToUrlImpl(GET_FOLLOWER_SCREEN_NAMES_URL, USER_ID_ARG,
                userId);
    }

    private ArgsToUrl userIdToFriendScreenNamesUrl(final String userId) {
        return new ParamsToUrlImpl(GET_FRIEND_SCREEN_NAMES_URL, USER_ID_ARG,
                userId);
    }

    private static interface ArgsToUrl {

        String url();

        String urlForNextCursor(final Long cursor);

    }

    private static class ParamsToUrlImpl implements ArgsToUrl {

        private final String apiUrl;

        private final String argName;

        private final String val;

        private ParamsToUrlImpl(String apiUrl, final String argName, String val) {
            this.apiUrl = apiUrl;
            this.argName = argName;
            this.val = val;
        }

        public String url() {
            return apiUrl + argName + "=" + val
                    + "&stringfy_ids=true&count=5000";
        }

        public String urlForNextCursor(Long cursor) {
            return apiUrl + argName + "=" + val + "&cursor=" + cursor
                    + "&stringfy_ids=true&count=5000";
        }
    }

    public void getUserTimeLineByName(Twitter twitter, List<String> names,
                                      String fileName) {

        // HashMap<String, List<String>> mp;
        // mp = new HashMap<String, List<String>>();

        int count = 0;
        int numberOfTweets = 0;
        for (int i = 0; i < names.size(); i++) {
            String nm = names.get(i);
            System.out.println("Start Getting Tweets for user: " + nm);
            List<String> st = getMoreTweetsFromUser(twitter, nm);
            count++;
            System.out.println("Number of User: " + count);
            if (st != null) {
                writeToFile(fileName, listToString(st));
                numberOfTweets += st.size();
                System.out.print("Number of tweets are recorded: "
                        + numberOfTweets);
            } else {
                System.out.println("st is null");
                i--;
            }
        }
        // return mp;
    }

    public static String listToString(List<String> list) {

        String result = "";
        for (String l : list) {
            result += l + "\n";
        }
        return result;
    }

    public List<String> getMoreTweetsFromUser(Twitter tw, String name) {

        List<Status> result = null;
        Paging pg = new Paging(1, NUMBER_OF_TWEET_EACH_TIME);

        try {
            result = tw.getUserTimeline(name, pg);
        } catch (TwitterException e) {
            System.out.println("Exception Occured: " + e);
            // e.printStackTrace();
            // if account is not public
            if (e.getErrorCode() != 88) {
                System.out.println("Error code is -1");
                return new ArrayList<String>();
            }
            try {
                System.out.println("Try to sleep thread for " + TIME_TO_SLEEP
                        / 60000 + " minutes");
                Thread.sleep(TIME_TO_SLEEP);
                return null;
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                System.out.println("Could not sleep thread");
                e1.printStackTrace();
            }

        }

//		if (result.size() > 0) {
//			System.out.println(result.get(0).toString());
//		}

        List<String> tempTweet = new ArrayList<String>();
        for (Status s : result) {
            if (!s.isRetweet())
                tempTweet.add(s.getUser().getScreenName() + "," + s.getCreatedAt()
                        + "," + s.getText().replace('\n', ' '));
        }
        return tempTweet;
    }

    private static void writeToFile(String fileName, String line) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(fileName,
                    true)));
            out.println(line);
        } catch (IOException e) {
            System.out.println("Line not written: " + line);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
