package cs464.ds;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import cs464.Tuple3;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Step1_ClarifyTweets {

    public static void main(String[] args) throws IOException {
        final String inputFileName = "/Users/ahmetkucuk/Desktop/Masa�st�/Bilkent/CS464/Project_Helper/250K_User_Splitted_Tweets/all_tweets.txt";
        final String outputFileName = "/Users/ahmetkucuk/Desktop/Masa�st�/Bilkent/CS464/Project_Helper/250K_User_Splitted_Tweets/all_tweets_no_links_no_usernames.txt";

        final InputStream fis = new FileInputStream(inputFileName);
        final BufferedReader br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));

        final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFileName, true)));

        String line;

        int lineCount = 0;

        final UrlRemover urlRemover = new UrlRemover();

        while ((line = br.readLine()) != null) {

            final Tuple3<String, String, String> tweet = parseTweet(line);
            if (tweet != null) {

                String cleanTweet = urlRemover.removeUrls(tweet._3);
                if (!Strings.isNullOrEmpty(cleanTweet)) {

                    cleanTweet = removePunctuationAndUserNames(cleanTweet);
                    if (!Strings.isNullOrEmpty(cleanTweet)) {
                        out.println(tweet._1 + "," + tweet._2 + "," + cleanTweet);
                    }
                }
            }

            if (++lineCount % 50000 == 0) {
                System.out.println("Line #: " + lineCount);
            }
        }

        br.close();
        out.close();
    }

    private static final List<String> words = new ArrayList<String>();

    private static final Joiner joiner = Joiner.on(" ");

    private static String removePunctuationAndUserNames(String text) {

        String cleanText = text;

        if (text.contains("pic.twitter.com")) {
            for (String eachToken : text.split(" ")) {
                if (!eachToken.contains("pic.twitter.com")) {
                    words.add(eachToken);
                }
            }

            cleanText = joiner.join(words);
            words.clear();
        }

        cleanText = cleanText.replaceAll("[^a-zA-Z0-9 @#]", "");

        if (cleanText.contains("@")) {
            for (String eachToken : cleanText.split(" ")) {

                final int index = eachToken.indexOf("@");
                if (index >= 0) {
                    if (index == 0) {
                        continue;
                    }

                    words.add(eachToken.substring(0, index));
                } else {
                    words.add(eachToken);
                }
            }

            cleanText = joiner.join(words);
            words.clear();
        }

        return cleanText.trim();
    }

    public static Tuple3<String, String, String> parseTweet(final String line) {
        try {
            final int userNameEndIndex = line.indexOf(",");
            final int tweetTimeEndIndex = line.indexOf(",", userNameEndIndex + 1);

            final String userName = line.substring(0, userNameEndIndex);
            final String tweetTime = line.substring(userNameEndIndex + 1, tweetTimeEndIndex);
            final String tweetText = line.substring(tweetTimeEndIndex + 1);

            return new Tuple3<String, String, String>(userName, tweetTime, tweetText);
        } catch (Exception e) {
            System.out.println("FAILED: " + line);
            return null;
        }
    }

}
