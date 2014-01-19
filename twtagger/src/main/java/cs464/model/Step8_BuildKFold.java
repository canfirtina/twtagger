package cs464.model;

import cs464.Tuple3;
import cs464.ds.Step1_ClarifyTweets;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Step8_BuildKFold {

    public static void main(String[] args) throws IOException {
        int k = 5;

        final String inputFileName = "/Users/CanFirtina/Developer/250K_User_Splitted_Tweets/english_tweets_only_hashtags_noslang_stemmed_nostopword_dateformatted_2013.10.07.txt";

        final InputStream fis = new FileInputStream(inputFileName);
        final BufferedReader br = new BufferedReader(new InputStreamReader(fis,
                Charset.forName("UTF-8")));

        String line;

        final List<Tuple3<String, String, String>> tweets = new ArrayList<Tuple3<String, String, String>>();

        while ((line = br.readLine()) != null) {

            final Tuple3<String, String, String> tweet = Step1_ClarifyTweets
                    .parseTweet(line);

            tweets.add(tweet);
        }

        System.out.println("File read completed.");

        br.close();

        for (int i = 0; i < 20; i++) {
            Collections.shuffle(tweets);
        }

        int testSize = tweets.size() / k;

        for (int i = 0; i < k; i++) {
            final int testStart = i * testSize;
            final int testEnd = testStart + testSize;

            final List<Tuple3<String, String, String>> testTweets = new ArrayList<Tuple3<String, String, String>>();
            final List<Tuple3<String, String, String>> trainingTweets = new ArrayList<Tuple3<String, String, String>>();

            for (int j = 0; j < tweets.size(); j++) {
                if (j >= testStart && j < testEnd) {
                    testTweets.add(tweets.get(j));
                } else {
                    trainingTweets.add(tweets.get(j));
                }
            }

            Step7_SeparateTweetsByDate.writeToFile("/Users/CanFirtina/Developer/250K_User_Splitted_Tweets/english_tweets_only_hashtags_noslang_stemmed_nostopword_dateformatted_20131007-training-" + (i + 1) + ".txt", trainingTweets);
            Step7_SeparateTweetsByDate.writeToFile("/Users/CanFirtina/Developer/250K_User_Splitted_Tweets/english_tweets_only_hashtags_noslang_stemmed_nostopword_dateformatted_20131007-test-" + (i + 1) + ".txt", testTweets);
        }

        System.out.println("Done");
    }


}
