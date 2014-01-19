package cs464.model;

import cs464.Tuple3;
import cs464.ds.Step1_ClarifyTweets;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Step3_SortTweetsByDate {

    public static void main(String[] args) throws IOException {

        final String inputFileName = "/Users/CanFirtina/Developer/250K_User_Splitted_Tweets/english_tweets_only_hashtags_noslang_stemmed_nostopword.txt";
        final String outputFileName = "/Users/CanFirtina/Developer/250K_User_Splitted_Tweets/english_tweets_only_hashtags_noslang_stemmed_nostopword_dateformatted.txt";
        //final String outputFileName2 = "/Users/CanFirtina/Developer/250K_User_Splitted_Tweets/english_tweets_only_hashtags_noslang_stemmed_nostopword_dateformatted_onlytweets.txt";

        final InputStream fis = new FileInputStream(inputFileName);
        final BufferedReader br = new BufferedReader(new InputStreamReader(fis,
                Charset.forName("UTF-8")));

        final PrintWriter out = new PrintWriter(new BufferedWriter(
                new FileWriter(outputFileName, true)));
        
        //final PrintWriter out2 = new PrintWriter(new BufferedWriter(
                //new FileWriter(outputFileName2, true)));

        String line;

        int lineCount = 0;

        List<Tuple3<String, String, String>> tweets = new ArrayList<Tuple3<String, String, String>>();

        while ((line = br.readLine()) != null) {

            final Tuple3<String, String, String> tweet = Step1_ClarifyTweets
                    .parseTweet(line);
            if (tweet != null) {
                try {
                    final String[] tweetDayTokens = tweet._2.split("-")[1].trim().split(" ");
                    String month = null;
                    if ("Jan".equals(tweetDayTokens[1])) {
                        month = "01";
                    } else if ("Feb".equals(tweetDayTokens[1])) {
                        month = "02";
                    } else if ("Mar".equals(tweetDayTokens[1])) {
                        month = "03";
                    } else if ("Apr".equals(tweetDayTokens[1])) {
                        month = "04";
                    } else if ("May".equals(tweetDayTokens[1])) {
                        month = "05";
                    } else if ("Jun".equals(tweetDayTokens[1])) {
                        month = "06";
                    } else if ("Jul".equals(tweetDayTokens[1])) {
                        month = "07";
                    } else if ("Aug".equals(tweetDayTokens[1])) {
                        month = "08";
                    } else if ("Sep".equals(tweetDayTokens[1])) {
                        month = "09";
                    } else if ("Oct".equals(tweetDayTokens[1])) {
                        month = "10";
                    } else if ("Nov".equals(tweetDayTokens[1])) {
                        month = "11";
                    } else if ("Dec".equals(tweetDayTokens[1])) {
                        month = "12";
                    } else {
                        System.out.println("Fail: " + tweet);
                    }

                    if (month != null) {
                        try {
                            final String tweetDate = "20" + tweetDayTokens[2] + "." + month + "." + tweetDayTokens[1];
                            tweets.add(new Tuple3<String, String, String>(tweet._1, tweetDate, tweet._3));
                        } catch (Exception e) {
                            System.out.println("Fail: " + e);
                        }

                    }
                } catch (Exception e) {
                    try {
                        final String[] tweetDayTokens = tweet._2.split(" ");
                        String month = null;
                        if ("Jan".equals(tweetDayTokens[1])) {
                            month = "01";
                        } else if ("Feb".equals(tweetDayTokens[1])) {
                            month = "02";
                        } else if ("Mar".equals(tweetDayTokens[1])) {
                            month = "03";
                        } else if ("Apr".equals(tweetDayTokens[1])) {
                            month = "04";
                        } else if ("May".equals(tweetDayTokens[1])) {
                            month = "05";
                        } else if ("Jun".equals(tweetDayTokens[1])) {
                            month = "06";
                        } else if ("Jul".equals(tweetDayTokens[1])) {
                            month = "07";
                        } else if ("Aug".equals(tweetDayTokens[1])) {
                            month = "08";
                        } else if ("Sep".equals(tweetDayTokens[1])) {
                            month = "09";
                        } else if ("Oct".equals(tweetDayTokens[1])) {
                            month = "10";
                        } else if ("Nov".equals(tweetDayTokens[1])) {
                            month = "11";
                        } else if ("Dec".equals(tweetDayTokens[1])) {
                            month = "12";
                        } else {
                            System.out.println("Fail: " + tweet);
                        }

                        if (month != null) {

                            final String tweetDate = tweetDayTokens[5] + "." + month + "." + tweetDayTokens[2];
                            tweets.add(new Tuple3<String, String, String>(tweet._1, tweetDate, tweet._3));


                        }
                    } catch (Exception e2) {
                        System.out.println("Fail: " + tweet);
                    }
                }
            }

            if (++lineCount % 50000 == 0) {
                System.out.println("Line #: " + lineCount);
            }
        }

        Collections.sort(tweets, new Comparator<Tuple3<String, String, String>>() {
            public int compare(Tuple3<String, String, String> tuple1, Tuple3<String, String, String> tuple2) {
                return tuple1._2.compareTo(tuple2._2);
            }
        });

        for (Tuple3<String, String, String> tweet : tweets) {
            out.println(tweet._1 + "," + tweet._2 + "," + tweet._3);
            //out2.println(tweet._3);
        }

        br.close();
        out.close();

    }

}
