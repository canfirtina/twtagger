package cs464.model;

import cs464.Tuple2;
import cs464.Tuple3;
import cs464.ds.Step1_ClarifyTweets;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class Step4_CountTweetsByYear {
    public static void main(String[] args) throws IOException {
        final String inputFileName = "/Users/ebkahveci/Dev/CS464/250K_User_Splitted_Tweets/english_tweets_only_hashtags_no_slang_stemmed_no_stopwords_sortedbydate.txt";

        final InputStream fis = new FileInputStream(inputFileName);
        final BufferedReader br = new BufferedReader(new InputStreamReader(fis,
                Charset.forName("UTF-8")));

        String line;

        int lineCount = 0;

        // <hashTag,word> -> count (how many times <hashTag,word> occurs)
        Map<String, Integer> yearCount = new HashMap<String, Integer>();
        yearCount.put("2007.01", 0);
        yearCount.put("2007.02", 0);
        yearCount.put("2007.03", 0);
        yearCount.put("2007.04", 0);
        yearCount.put("2007.05", 0);
        yearCount.put("2007.06", 0);
        yearCount.put("2007.07", 0);
        yearCount.put("2007.08", 0);
        yearCount.put("2007.09", 0);
        yearCount.put("2007.10", 0);
        yearCount.put("2007.11", 0);
        yearCount.put("2007.12", 0);
        yearCount.put("2008.01", 0);
        yearCount.put("2008.02", 0);
        yearCount.put("2008.03", 0);
        yearCount.put("2008.04", 0);
        yearCount.put("2008.05", 0);
        yearCount.put("2008.06", 0);
        yearCount.put("2008.07", 0);
        yearCount.put("2008.08", 0);
        yearCount.put("2008.09", 0);
        yearCount.put("2008.10", 0);
        yearCount.put("2008.11", 0);
        yearCount.put("2008.12", 0);
        yearCount.put("2009.01", 0);
        yearCount.put("2009.02", 0);
        yearCount.put("2009.03", 0);
        yearCount.put("2009.04", 0);
        yearCount.put("2009.05", 0);
        yearCount.put("2009.06", 0);
        yearCount.put("2009.07", 0);
        yearCount.put("2009.08", 0);
        yearCount.put("2009.09", 0);
        yearCount.put("2009.10", 0);
        yearCount.put("2009.11", 0);
        yearCount.put("2009.12", 0);
        yearCount.put("2010.01", 0);
        yearCount.put("2010.02", 0);
        yearCount.put("2010.03", 0);
        yearCount.put("2010.04", 0);
        yearCount.put("2010.05", 0);
        yearCount.put("2010.06", 0);
        yearCount.put("2010.07", 0);
        yearCount.put("2010.08", 0);
        yearCount.put("2010.09", 0);
        yearCount.put("2010.10", 0);
        yearCount.put("2010.11", 0);
        yearCount.put("2010.12", 0);
        yearCount.put("2011.01", 0);
        yearCount.put("2011.02", 0);
        yearCount.put("2011.03", 0);
        yearCount.put("2011.04", 0);
        yearCount.put("2011.05", 0);
        yearCount.put("2011.06", 0);
        yearCount.put("2011.07", 0);
        yearCount.put("2011.08", 0);
        yearCount.put("2011.09", 0);
        yearCount.put("2011.10", 0);
        yearCount.put("2011.11", 0);
        yearCount.put("2011.12", 0);
        yearCount.put("2012.01", 0);
        yearCount.put("2012.02", 0);
        yearCount.put("2012.03", 0);
        yearCount.put("2012.04", 0);
        yearCount.put("2012.05", 0);
        yearCount.put("2012.06", 0);
        yearCount.put("2012.07", 0);
        yearCount.put("2012.08", 0);
        yearCount.put("2012.09", 0);
        yearCount.put("2012.10", 0);
        yearCount.put("2012.11", 0);
        yearCount.put("2012.12", 0);
        yearCount.put("2013.01", 0);
        yearCount.put("2013.02", 0);
        yearCount.put("2013.03", 0);
        yearCount.put("2013.04", 0);
        yearCount.put("2013.05", 0);
        yearCount.put("2013.06", 0);
        yearCount.put("2013.07", 0);
        yearCount.put("2013.08", 0);
        yearCount.put("2013.09", 0);
        yearCount.put("2013.10", 0);
        yearCount.put("2013.11", 0);
        yearCount.put("2013.12", 0);

        while ((line = br.readLine()) != null) {

            final Tuple3<String, String, String> tweet = Step1_ClarifyTweets
                    .parseTweet(line);
            if (tweet != null) {
                final String[] tweetDateTokens = tweet._2.split("\\.");
                final String tweetYearAndMonth = tweetDateTokens[0] + "." + tweetDateTokens[1];
                yearCount.put(tweetYearAndMonth, yearCount.get(tweetYearAndMonth) + 1);
            }

            if (++lineCount % 50000 == 0) {
                System.out.println("Line #: " + lineCount);
            }
        }

        System.out.println("Years: " + yearCount);

        final List<Tuple2<String, Integer>> counts = new ArrayList<Tuple2<String, Integer>>();
        for (Map.Entry<String, Integer> entry : yearCount.entrySet()) {
            counts.add(new Tuple2<String, Integer>(entry.getKey(), entry.getValue()));
        }

        Collections.sort(counts, new Comparator<Tuple2<String, Integer>>() {
            public int compare(Tuple2<String, Integer> tuple1, Tuple2<String, Integer> tuple2) {
                return tuple1._1.compareTo(tuple2._1);
            }
        });

        br.close();

        for (Tuple2<String, Integer> count : counts) {
            System.out.println(count);
        }
    }
}
