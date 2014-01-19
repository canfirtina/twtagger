package cs464.model;

import cs464.Tuple2;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class Step2_CountHashTagWordPairs {

    public static void main(String[] args) throws IOException {
        final String inputFileName = "/Users/ebkahveci/Dev/CS464/250K_User_Splitted_Tweets/english_tweets_only_hashtags_noslang_stemmed_nostopword_dateformatted_201310-training-1_hashtag_word_pairs.txt";
        final String outputFileName = "/Users/ebkahveci/Dev/CS464/250K_User_Splitted_Tweets/english_tweets_only_hashtags_noslang_stemmed_nostopword_dateformatted_201310-training-1_hashtag_word_pair_counts.txt";

        final InputStream fis = new FileInputStream(inputFileName);
        final BufferedReader br = new BufferedReader(new InputStreamReader(fis,
                Charset.forName("UTF-8")));

        final PrintWriter out = new PrintWriter(new BufferedWriter(
                new FileWriter(outputFileName, true)));

        String line;

        int lineCount = 0;

        // <hashTag,word> -> count (how many times <hashTag,word> occurs)
        Map<String, Integer> hashTagWordCounts = new HashMap<String, Integer>();

        final Integer zero = Integer.valueOf(0);

        while ((line = br.readLine()) != null) {

            Integer count = hashTagWordCounts.get(line);
            count = count != null ? count : zero;
            hashTagWordCounts.put(line, count + 1);

            if (++lineCount % 50000 == 0) {
                System.out.println("Line #: " + lineCount);
            }
        }

        System.out.println("File read completed.");

        final List<Tuple2<String, Integer>> hashTagWordCountTuples = new ArrayList<Tuple2<String, Integer>>();
        for (Map.Entry<String, Integer> entry : hashTagWordCounts.entrySet()) {
            hashTagWordCountTuples.add(new Tuple2<String, Integer>(entry.getKey(), entry.getValue()));
        }

        System.out.println("After sort.");

        hashTagWordCounts = null;
        System.gc();
        System.out.println("After gc.");

        Collections.sort(hashTagWordCountTuples, new Comparator<Tuple2<String, Integer>>() {
            public int compare(Tuple2<String, Integer> tuple1, Tuple2<String, Integer> tuple2) {
                return tuple1._2.compareTo(tuple2._2);
            }
        });

        // count -> frequency (how many <hashTag,word> pairs exists with each count value)
        final Map<Integer, Integer> hashTagWordCountFrequencies = new HashMap<Integer, Integer>();
        for (Tuple2<String, Integer> tuple : hashTagWordCountTuples) {
            Integer count = hashTagWordCountFrequencies.get(tuple._2);
            count = count != null ? count : zero;
            hashTagWordCountFrequencies.put(tuple._2, count + 1);
        }

        final List<Tuple2<Integer, Integer>> hashTagWordCountFrequencyTuples = new ArrayList<Tuple2<Integer, Integer>>();
        for (Map.Entry<Integer, Integer> entry : hashTagWordCountFrequencies.entrySet()) {
            hashTagWordCountFrequencyTuples.add(new Tuple2<Integer, Integer>(entry.getKey(), entry.getValue()));
        }

        Collections.sort(hashTagWordCountFrequencyTuples, new Comparator<Tuple2<Integer, Integer>>() {
            public int compare(Tuple2<Integer, Integer> tuple1, Tuple2<Integer, Integer> tuple2) {
                return tuple1._2.compareTo(tuple2._2);
            }
        });

        for (Tuple2<Integer, Integer> tuple : hashTagWordCountFrequencyTuples) {
            System.out.println(tuple._2 + " <HashTag,Word> pairs occur " + tuple._1 + " times.");
            out.println(tuple._2 + "," + tuple._1);
        }

        br.close();
        out.close();
    }
}
