package cs464.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import cs464.Tuple3;
import cs464.ds.Step1_ClarifyTweets;

public class Step9_ClearData {
	
	public static final int Number_Of_Occurence = 2;
	
	public static void main(String[] args) throws IOException {
		
		final String inputFileName = "/Users/ahmetkucuk/Documents/Bilkent/CS464/Project_Helper/250K_User_Splitted_Tweets/english_tweets_only_hashtags_noslang_stemmed_nostopword_sortedbydate_201310.txt";
		final String outputFileName = "/Users/ahmetkucuk/Documents/Bilkent/CS464/Project_Helper/250K_User_Splitted_Tweets/english_tweets_only_hashtags_noslang_stemmed_nostopword_sortedbydate_201310_cleaned.txt";
		
        final InputStream fis = new FileInputStream(inputFileName);
        final BufferedReader br = new BufferedReader(new InputStreamReader(fis,
                Charset.forName("UTF-8")));
        
        final PrintWriter out = new PrintWriter(new BufferedWriter(
                new FileWriter(outputFileName, true)));

        String line;
        
        final HashMap<String, Integer> hashTagCount = new HashMap<String, Integer>(); 
        
        ArrayList<Tuple3<String, String, String>> allTweets = new ArrayList<Tuple3<String,String,String>>();
        
        while ((line = br.readLine()) != null) {

            final Tuple3<String, String, String> tweet = Step1_ClarifyTweets
                    .parseTweet(line);
            
            String tweetText = tweet._3;
            String[] tokens = tweetText.split(" ");
            for(String word:tokens) {
            	if(word.charAt(0) == '#') {
            		if(hashTagCount.containsKey(word)) {
            			hashTagCount.put(word, hashTagCount.get(word) + 1);
            		} else {
            			hashTagCount.put(word, 1);
            		}
            	}
            }
            allTweets.add(tweet);
        }
        
        System.out.println("Reading Done");
        
        for(Tuple3<String, String, String> eachTweet: allTweets) {
        	
        	String tweetText = eachTweet._3;
        	String[] tokens = tweetText.split(" ");
        	for(String token: tokens) {
        		if(token.charAt(0) == '#') {
        			if(hashTagCount.get(token) > Number_Of_Occurence) {
        				out.println(eachTweet._1 + "," + eachTweet._2 + "," + eachTweet._3);
        				break;
        			}
        		}
        	}
        	
        }
        System.out.println("Done");
        
        br.close();
        out.close();
	}


}
