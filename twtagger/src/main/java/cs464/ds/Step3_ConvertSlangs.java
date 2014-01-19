package cs464.ds;

import cs464.Tuple3;

import java.io.*;
import java.nio.charset.Charset;
import java.util.StringTokenizer;

public class Step3_ConvertSlangs {

    public static void main(String[] args) throws IOException {
        final String inputFileName = "/Users/ebkahveci/Dev/CS464/250K_User_Splitted_Tweets/english_tweets.txt";
        final String outputFileName = "/Users/ebkahveci/Dev/CS464/250K_User_Splitted_Tweets/english_tweets_only_hashtags_noslang.txt";

        final InputStream fis = new FileInputStream(inputFileName);
        final BufferedReader br = new BufferedReader(new InputStreamReader(fis,
                Charset.forName("UTF-8")));

        final PrintWriter out = new PrintWriter(new BufferedWriter(
                new FileWriter(outputFileName, true)));

        String line;
        ConvertSlang slangs = new ConvertSlang();

        int lineCount = 0;

        while ((line = br.readLine()) != null) {

            final Tuple3<String, String, String> tweet = Step1_ClarifyTweets
                    .parseTweet(line);
            if (tweet != null) {

                final String tweetText = tweet._3;
                StringTokenizer tokens = new StringTokenizer(tweetText, " .?!:;,&=");
                StringBuilder clearText = new StringBuilder();
                while (tokens.hasMoreTokens()) {
                    String temp = tokens.nextToken();
                    if (temp.charAt(0) == '#') {
                        if (temp.length() > 1)
                            clearText.append(temp).append(" ");
                    } else {
                        clearText.append(slangs.getRealMeaning(temp)).append(" ");
                    }
                }
                if (clearText.length() > 0 && clearText.indexOf("#") > -1)
                    out.println(tweet._1 + "," + tweet._2 + "," + clearText.toString().trim());

            }

            if (++lineCount % 50000 == 0) {
                System.out.println("Line #: " + lineCount);
            }
        }

        br.close();
        out.close();
    }
}
