package cs464.model;

import cs464.Tuple3;
import cs464.ds.Step1_ClarifyTweets;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Step7_SeparateTweetsByDate {

    public static void main(String[] args) throws IOException {
        final String inputFileName = "/Users/CanFirtina/Developer/250K_User_Splitted_Tweets/english_tweets_only_hashtags_noslang_stemmed_nostopword_dateformatted.txt";

        final InputStream fis = new FileInputStream(inputFileName);
        final BufferedReader br = new BufferedReader(new InputStreamReader(fis,
                Charset.forName("UTF-8")));

        String line;

        final List<Tuple3<String, String, String>> tweets201310 = new ArrayList<Tuple3<String, String, String>>();
        final List<Tuple3<String, String, String>> tweets2013 = new ArrayList<Tuple3<String, String, String>>();
        final List<Tuple3<String, String, String>> tweets2012 = new ArrayList<Tuple3<String, String, String>>();
        final List<Tuple3<String, String, String>> tweets2011 = new ArrayList<Tuple3<String, String, String>>();

        while ((line = br.readLine()) != null) {

            final Tuple3<String, String, String> tweet = Step1_ClarifyTweets
                    .parseTweet(line);

            final String date = tweet._2;

            if (date.startsWith("2013.10")) //{
                tweets201310.add(tweet);
            //} else if (date.startsWith("2013")) {
                tweets2013.add(tweet);
            //} else if (date.startsWith("2012")) {
                tweets2012.add(tweet);
            //} else if (date.startsWith("2011") || date.startsWith("2010") || date.startsWith("2009") || date.startsWith("2008") || date.startsWith("2007")) {
                tweets2011.add(tweet);
            //}

        }

        System.out.println("File read completed.");

        br.close();

        //writeToFile("/Users/CanFirtina/Developer/250K_User_Splitted_Tweets/english_tweets_only_hashtags_noslang_stemmed_nostopword_dateformatted_201310.txt", tweets201310);
        //writeToFile("/Users/CanFirtina/Developer/250K_User_Splitted_Tweets/english_tweets_only_hashtags_noslang_stemmed_nostopword_dateformatted_2013.txt", tweets2013);
        //writeToFile("/Users/CanFirtina/Developer/250K_User_Splitted_Tweets/english_tweets_only_hashtags_noslang_stemmed_nostopword_dateformatted_2012.txt", tweets2012);
        //writeToFile("/Users/CanFirtina/Developer/250K_User_Splitted_Tweets/english_tweets_only_hashtags_noslang_stemmed_nostopword_dateformatted_2011.txt", tweets2011);
        
        int day = 1;
        int count = 0;
        while( count < tweets201310.size()){
        	
        	List<Tuple3<String, String, String>> tweets201310date = new ArrayList<Tuple3<String, String, String>>();
            String initialDate = tweets201310.get(count)._2;
        	tweets201310date.add(tweets201310.get(count));
        	
        	count++;
        	while( count < tweets201310.size() && tweets201310.get(count)._2.equalsIgnoreCase(initialDate)){
        		
        		tweets201310date.add(tweets201310.get(count));
        		count++;
        	}
        	writeToFile("/Users/CanFirtina/Developer/250K_User_Splitted_Tweets/english_tweets_only_hashtags_noslang_stemmed_nostopword_dateformatted_" + initialDate +".txt", tweets201310date);
        }

    }

    public static void writeToFile(final String fileName, final List<Tuple3<String, String, String>> tweets) throws IOException {
        final PrintWriter out = new PrintWriter(new BufferedWriter(
                new FileWriter(fileName, true)));

        for (Tuple3<String, String, String> tweet : tweets) {
            out.println(tweet._1 + "," + tweet._2 + "," + tweet._3);
        }

        out.close();
    }

}
