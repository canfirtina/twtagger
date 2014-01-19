package cs464.model;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.StringTokenizer;

import cs464.Tuple3;
import cs464.ds.Step1_ClarifyTweets;

public class Step10_PrepareDataForLibShortText {

	public static void main(String[] args) throws IOException{
		
		final String inputFileName = "/Users/CanFirtina/Developer/250K_User_Splitted_Tweets/english_tweets_only_hashtags_noslang_stemmed_nostopword_dateformatted_20131007-test-1.txt";
		final String outputFileName = "/Users/Canfirtina/Developer/250K_User_Splitted_Tweets/english_tweets_only_hashtags_noslang_stemmed_nostopword_dateformatted_20131007-test-1_libshorttextformat.txt";
		
		final InputStream fis = new FileInputStream(inputFileName);
        final BufferedReader br = new BufferedReader(new InputStreamReader(fis,Charset.forName("UTF-8")));
        final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFileName, true)));
        
        String line;
        int lineCount = 0;
        
        while ((line = br.readLine()) != null) {
            final Tuple3<String, String, String> tweet = Step1_ClarifyTweets.parseTweet(line);
            StringTokenizer tokens = new StringTokenizer(tweet._3);
            String onlyTweet = "";
            ArrayList<String> hashtagsInTweet = new ArrayList<String>();
            
            while (tokens.hasMoreTokens()) {
                String word = tokens.nextToken();
                
                if (word.charAt(0) == '#') {
                    if( !hashtagsInTweet.contains(word))
                    	hashtagsInTweet.add(word);
                } else {
                    onlyTweet += word + " ";
                }
            }
            
            if( hashtagsInTweet.size() != 0 && onlyTweet.length() != 0){
            	onlyTweet = onlyTweet.substring(0, onlyTweet.length()-1); //removing the white space at the end of the character
            	for( String hashtag : hashtagsInTweet)
            		out.println(hashtag + "\t" + onlyTweet);
            }
            
            lineCount++;
            if (lineCount % 50000 == 0)
                System.out.println("Line Count: " + lineCount);
        }
        
        br.close();
        out.close();
	}

}
