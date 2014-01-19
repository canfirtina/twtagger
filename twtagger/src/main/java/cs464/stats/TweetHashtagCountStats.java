package cs464.stats;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashSet;

import cs464.Tuple3;
import cs464.ds.Step1_ClarifyTweets;

public class TweetHashtagCountStats {
	public static void main(String[] args) throws IOException {

		final String inputFileName = "/Users/ahmetkucuk/Documents/Bilkent/CS464/Project_Helper/Data/english_tweets_only_hashtags_noslang_stemmed_nostopword_dateformatted_201310.txt";

		final InputStream fis = new FileInputStream(inputFileName);
		final BufferedReader br = new BufferedReader(new InputStreamReader(fis,
				Charset.forName("UTF-8")));

		String line;

		final HashSet<String> uniqueUserNames = new HashSet<String>();

		int lineCount = 0;
		int numberOfHashTags = 0;
		while ((line = br.readLine()) != null) {
			final Tuple3<String, String, String> tweet = Step1_ClarifyTweets
					.parseTweet(line);
			String userName = tweet._1;
			uniqueUserNames.add(userName);
			lineCount++;
			String tweetText = tweet._3;
			String[] tokens = tweetText.split(" ");
			for (String token : tokens) {
				if (token.charAt(0) == '#')
					numberOfHashTags++;
			}
		}

		System.out.println("Number Of Tweets: " + lineCount
				+ " Number Of User: " + uniqueUserNames.size() + " Percentage TweetCount/UserCount: " + (double)lineCount/uniqueUserNames.size());
		System.out.println("Number Of HashTags: " + numberOfHashTags
				+ " Number Of Tweets: " + lineCount + " Percentage HashTagCount/TweetCount:  "
				+ (double) numberOfHashTags / lineCount);

		br.close();
	}
}
