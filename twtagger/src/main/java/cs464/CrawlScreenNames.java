package cs464;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.io.Files;
import cs464.config.AppConfig;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class CrawlScreenNames {

    private static final Logger logger = Logger.getLogger(CrawlScreenNames.class);

    public static final Function<Tuple2<String, String>, String> TWITTER_ID_SCREEN_NAME_JOINER = new Function<Tuple2<String, String>, String>() {
       
        public String apply(Tuple2<String, String> input) {
            return input._1 + ":" + input._2;
        }
    };

    public static final String FETCHED_USERS_FILE_NAME = "fetchedUsers.txt";

    public static final String PROCESSED_SCREEN_NAMES_FILE_NAME = "processedScreenNames.txt";

    public static final String SCREEN_NAMES_QUEUE_FILE_NAME = "screenNamesQueue.txt";

    public static void main(String[] args) throws InterruptedException, IOException {

        final AppConfig appConfig = new AppConfig();

        final WebDriver webDriver = new ChromeDriver();

        final SeleniumTwitterApi seleniumTwitterApi = new SeleniumTwitterApi(appConfig, webDriver);

        final boolean initSuccessful = seleniumTwitterApi.init();

        if (initSuccessful) {

            final Queue<String> screenNamesQueue = new LinkedList<String>();
            final Set<String> processedScreenNames = new HashSet<String>();

            final List<String> persistedScreenNames = Files.readLines(new File(SCREEN_NAMES_QUEUE_FILE_NAME), Charset.defaultCharset());
            final Set<String> uniqueScreenNames = new HashSet<String>();
            uniqueScreenNames.addAll(persistedScreenNames);
            screenNamesQueue.addAll(uniqueScreenNames);

            final List<String> persistedProcessedScreenNames = Files.readLines(new File(PROCESSED_SCREEN_NAMES_FILE_NAME), Charset.defaultCharset());
            processedScreenNames.addAll(persistedProcessedScreenNames);

            int i = 0;

            final long startTime = System.currentTimeMillis();

            int failedCount = 0;

            while (processedScreenNames.size() < appConfig.totalNumberOfUsersToFetch() && !screenNamesQueue.isEmpty()) {
                final String screenName = screenNamesQueue.remove();

                if (processedScreenNames.contains(screenName)) {
                    continue;
                }

                try {

                    final Collection<Tuple2<String, String>> followers = seleniumTwitterApi.followersByScreenName(screenName);
                    final Collection<Tuple2<String, String>> friends = seleniumTwitterApi.friendsByScreenName(screenName);

                    for (Tuple2<String, String> eachFollower : followers) {
                        final String followerScreenName = eachFollower._2;

                        if (!processedScreenNames.contains(followerScreenName)) {
                            if (!uniqueScreenNames.contains(followerScreenName)) {
                                uniqueScreenNames.add(followerScreenName);
                                screenNamesQueue.add(followerScreenName);
                            }
                        }
                    }

                    for (Tuple2<String, String> eachFriend : friends) {
                        final String friendScreenName = eachFriend._2;

                        if (!processedScreenNames.contains(friendScreenName)) {
                            if (!uniqueScreenNames.contains(friendScreenName)) {
                                uniqueScreenNames.add(friendScreenName);
                                screenNamesQueue.add(friendScreenName);
                            }
                        }
                    }

                    processedScreenNames.add(screenName);
                    persist(screenName, followers, friends);

                    logger.info("[processed] " + ++i + ": " + screenName + " ScreenNamesQueueSize: " + screenNamesQueue.size());

                } catch (Exception e) {
                    logger.error("[processing-failed] ScreenName: " + screenName + " ExceptionMessage: " + e.getMessage(), e);
                    if (++failedCount >= 100) {
                        break;
                    }
                }
            }

            final String processedScreenNamesJoined = Joiner.on("\n").join(processedScreenNames);
            writeToFile(PROCESSED_SCREEN_NAMES_FILE_NAME + "2", processedScreenNamesJoined);

            final String screenNamesQueueJoined = Joiner.on("\n").join(screenNamesQueue);
            writeToFile(SCREEN_NAMES_QUEUE_FILE_NAME + "2", screenNamesQueueJoined);

            final long endTime = System.currentTimeMillis();
            System.out.println("Completed in " + ((endTime - startTime) / 1000) + " seconds");
        } else {
            System.out.println("Init failed.");
        }

        webDriver.close();
    }

    private static void persist(String screenName, Collection<Tuple2<String, String>> followers, Collection<Tuple2<String, String>> friends) {

        final Collection<String> followersJoined = Collections2.transform(followers, TWITTER_ID_SCREEN_NAME_JOINER);
        final Collection<String> friendsJoined = Collections2.transform(friends, TWITTER_ID_SCREEN_NAME_JOINER);

        final String followersJoinedStr = Joiner.on("|").join(followersJoined);
        final String friendsJoinedStr = Joiner.on("|").join(friendsJoined);

        final String line = new StringBuilder(screenName).append(",").append(followersJoinedStr).append(",").append(friendsJoinedStr).toString();

        writeToFile(FETCHED_USERS_FILE_NAME, line);
    }

    private static void writeToFile(String fileName, String line) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
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

