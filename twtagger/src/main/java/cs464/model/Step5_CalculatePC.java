package cs464.model;

import cs464.Tuple2;
import cs464.Tuple3;
import cs464.ds.Step1_ClarifyTweets;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class Step5_CalculatePC {

    public static void main(String[] args) throws IOException {

        final String inputFileName = "/Users/ebkahveci/Dev/CS464/250K_User_Splitted_Tweets/english_tweets_only_hashtags_noslang_stemmed_nostopword_dateformatted_201310-training-1.txt";
        final String outputFileName = "/Users/ebkahveci/Dev/CS464/250K_User_Splitted_Tweets/english_tweets_only_hashtags_noslang_stemmed_nostopword_dateformatted_201310-training-1_pc.txt";
        final String outputFileName2 = "english_tweets_only_hashtags_noslang_stemmed_nostopword_dateformatted_201310-training-1_word_counts.txt";

        final InputStream fis = new FileInputStream(inputFileName);
        final BufferedReader br = new BufferedReader(new InputStreamReader(fis,
                Charset.forName("UTF-8")));

        final PrintWriter out = new PrintWriter(new BufferedWriter(
                new FileWriter(outputFileName, true)));
        final PrintWriter out2 = new PrintWriter(new BufferedWriter(
                new FileWriter(outputFileName2, true)));

        HashMap<String, Integer> PCcount = new HashMap<String, Integer>();
        HashMap<String, Integer> wordCount = new HashMap<String, Integer>();
        String line;
        int lineCount = 0;
        while ((line = br.readLine()) != null) {
            final Tuple3<String, String, String> tweet = Step1_ClarifyTweets
                    .parseTweet(line);
            StringTokenizer tokens = new StringTokenizer(tweet._3);
            while (tokens.hasMoreTokens()) {
                String word = tokens.nextToken();
                if (word.charAt(0) == '#') {
                    Integer count = PCcount.get(word);
                    count = (count != null) ? count : 0;
                    PCcount.put(word, count + 1);
                } else {
                    Integer count = wordCount.get(word);
                    count = (count != null) ? count : 0;
                    wordCount.put(word, count + 1);
                }
            }
            lineCount++;
            if (lineCount % 50000 == 0)
                System.out.println("Line Count: " + lineCount);
        }

        int totalCount = 0;
        List<Tuple2<String, Integer>> hashtagCounts = new ArrayList<Tuple2<String, Integer>>();
        for (Map.Entry<String, Integer> entry : PCcount.entrySet()) {
            hashtagCounts.add(new Tuple2<String, Integer>(entry.getKey(), entry
                    .getValue()));
            totalCount += entry.getValue();
        }
        Collections.sort(hashtagCounts,
                new Comparator<Tuple2<String, Integer>>() {

                    public int compare(Tuple2<String, Integer> o1,
                                       Tuple2<String, Integer> o2) {
                        // TODO Auto-generated method stub
                        return (-1) * (o1._2.compareTo(o2._2));
                    }

                });
        double result = 0;
        for (Tuple2<String, Integer> s : hashtagCounts) {
            result += (double) s._2 / totalCount;
            out.println(s._1 + "," + Math.log10((double) s._2 / totalCount));
        }
        System.out.println("Total Number of Tweets: " + result);

        totalCount = 0;
        List<Tuple2<String, Integer>> wordCounts = new ArrayList<Tuple2<String, Integer>>();
        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            wordCounts.add(new Tuple2<String, Integer>(entry.getKey(), entry
                    .getValue()));
            totalCount += entry.getValue();
        }
        Collections.sort(wordCounts,
                new Comparator<Tuple2<String, Integer>>() {

                    public int compare(Tuple2<String, Integer> o1,
                                       Tuple2<String, Integer> o2) {
                        // TODO Auto-generated method stub
                        return (-1) * (o1._2.compareTo(o2._2));
                    }

                });
        for (Tuple2<String, Integer> s : wordCounts) {
            out2.println(s._1 + "," + s._2);
        }

        br.close();
        out.close();
        out2.close();
    }

}
