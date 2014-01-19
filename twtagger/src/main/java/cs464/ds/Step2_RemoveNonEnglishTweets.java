package cs464.ds;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import cs464.Tuple3;

import java.io.*;
import java.nio.charset.Charset;

public class Step2_RemoveNonEnglishTweets {

    public static void main(String[] args) throws IOException, LangDetectException {
        final String inputFileName = "/Users/ahmetkucuk/Desktop/Masaustu/Bilkent/CS464/Project_Helper/250K_User_Splitted_Tweets/all_tweets_no_links_no_usernames.txt";
        final String outputFileName = "/Users/ahmetkucuk/Desktop/Masaustu/Bilkent/CS464/Project_Helper/250K_User_Splitted_Tweets/english_tweets.txt";

        final InputStream fis = new FileInputStream(inputFileName);
        final BufferedReader br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));

        final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFileName, true)));

        String line;

        int lineCount = 0;

        DetectorFactory.loadProfile("profiles");
        int failCount = 0;

        while ((line = br.readLine()) != null) {

            final Tuple3<String, String, String> tweet = Step1_ClarifyTweets.parseTweet(line);
            if (tweet != null) {

                try {
                    final String tweetText = tweet._3;

                    Detector detector = DetectorFactory.create();
                    detector.append(tweetText);

                    final String lang = detector.detect();

                    if ("en".equals(lang)) {
                        out.println(tweet._1 + "," + tweet._2 + "," + tweetText);
                    }
                } catch (Exception e) {
//                    System.out.println(tweet);
//                    System.out.println(e.getMessage());
                    ++failCount;
                }


            }

            if (++lineCount % 50000 == 0) {
                System.out.println("Line #: " + lineCount);
            }
        }

        br.close();
        out.close();

        System.out.println("# of failed tweets: " + failCount);
    }
}
