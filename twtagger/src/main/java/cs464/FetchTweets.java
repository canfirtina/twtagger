package cs464;

import com.google.common.io.Files;
import cs464.config.AppConfig;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class FetchTweets {

    private static final Logger logger = Logger.getLogger(FetchTweets.class);

    public static void main(String[] args) throws IOException {
        final AppConfig appConfig = new AppConfig();

        final WebDriver webDriver = new ChromeDriver();

        final SeleniumTwitterApi seleniumTwitterApi = new SeleniumTwitterApi(appConfig, webDriver);

        final boolean initSuccessful = seleniumTwitterApi.init();

        if (initSuccessful) {

            int failedCount = 0;

            final List<String> allScreenNames = Files.readLines(new File("02"), Charset.defaultCharset());
            final Set<String> readScreenNames = new HashSet<String>(Files.readLines(new File("readScreenNames.txt"), Charset.defaultCharset()));

            int i = 0;

            int totalNumberOfTweets = 0;

            for (final String eachScreenName : allScreenNames) {
                if (!readScreenNames.contains(eachScreenName)) {
                    try {
                        final List<Tuple2<String, String>> tweets = seleniumTwitterApi.tweetsOf(eachScreenName);

                        if (!tweets.isEmpty()) {
                            persistTweets(eachScreenName, tweets);
                            totalNumberOfTweets += tweets.size();
                        }

                        readScreenNames.add(eachScreenName);
                        writeToFile("readScreenNames.txt", Arrays.asList(eachScreenName));

                        logger.info("[processed] " + ++i + ": " + eachScreenName + " #OfUserTweets: " + tweets.size() + " Total#OfTweets: " + totalNumberOfTweets);
                    } catch (Exception e) {
                        logger.error("[processing-failed] ScreenName: " + eachScreenName + " ExceptionMessage: " + e.getMessage(), e);
                        if (++failedCount >= 100) {
                            break;
                        }
                    }
                }
            }

            logger.info("All tweets are fetched.");
        } else {
            logger.error("Init failed.");
        }

        webDriver.close();
    }

    private static void persistTweets(final String screenName, List<Tuple2<String, String>> tweets) {
        final List<String> screenNameAndTweets = new ArrayList<String>();
        for (Tuple2<String, String> eachTweet : tweets) {
            screenNameAndTweets.add(screenName + "," + eachTweet._1 + "," + eachTweet._2.replace('\n', ' '));
        }

        writeToFile("tweets.txt", screenNameAndTweets);
    }

    private static void writeToFile(String fileName, List<String> lines) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
            for (String eachLine : lines) {
                out.println(eachLine);
            }
        } catch (IOException e) {
            System.out.println("Lines not written: " + lines);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
