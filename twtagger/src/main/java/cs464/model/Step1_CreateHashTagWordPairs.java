package cs464.model;

import cs464.Tuple3;
import cs464.ds.Step1_ClarifyTweets;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Step1_CreateHashTagWordPairs {

    public static void main(String[] args) throws IOException {
        final String inputFileName = "/Users/ebkahveci/Dev/CS464/250K_User_Splitted_Tweets/english_tweets_only_hashtags_noslang_stemmed_nostopword_dateformatted_201310-training-1.txt";
        final String outputFileName1 = "/Users/ebkahveci/Dev/CS464/250K_User_Splitted_Tweets/english_tweets_only_hashtags_noslang_stemmed_nostopword_dateformatted_201310-training-1_hashtag_word_pairs.txt";
        final String outputFileName2 = "/Users/ebkahveci/Dev/CS464/250K_User_Splitted_Tweets/english_tweets_only_hashtags_noslang_stemmed_nostopword_dateformatted_201310-training-1_hashtag_word_pairs_detailed.txt";

        final InputStream fis = new FileInputStream(inputFileName);
        final BufferedReader br = new BufferedReader(new InputStreamReader(fis,
                Charset.forName("UTF-8")));

        final PrintWriter out1 = new PrintWriter(new BufferedWriter(
                new FileWriter(outputFileName1, true)));
        final PrintWriter out2 = new PrintWriter(new BufferedWriter(
                new FileWriter(outputFileName2, true)));

        String line;

        int lineCount = 0;

        while ((line = br.readLine()) != null) {

            final Tuple3<String, String, String> tweet = Step1_ClarifyTweets
                    .parseTweet(line);
            if (tweet != null) {

                final String tweetText = tweet._3;
                final String[] tokens = tweetText.split(" ");
                final List<String> words = new ArrayList<String>();
                final List<String> hashTags = new ArrayList<String>();
                int totalWordForHashtag = 0;

                for (String token : tokens) {
                    if (token.charAt(0) == '#') {
                        hashTags.add(token);
                    } else {
                        words.add(token);
                    }
                }

                for (String hashTag : hashTags) {
                    for (String word : words) {
                        out1.println(hashTag + "," + word);
                        out2.println(tweet._1 + "," + tweet._2 + "," + hashTag + "," + word);
                    }
                }
            }

            if (++lineCount % 50000 == 0) {
                System.out.println("Line #: " + lineCount);
            }
        }

        br.close();
        out1.close();
        out2.close();
    }
}
