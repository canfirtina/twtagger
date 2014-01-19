package cs464.model;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import cs464.Tuple2;
import cs464.Tuple3;
import cs464.ds.Step1_ClarifyTweets;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

public class Step6_CalculatePXC {

    public static void main(String[] args) throws IOException {

        final int numberOfCandidatesToConsider = 20;
        final boolean enableIdf = false;
        final double ALPHA = 0.000001;


        final String trainingTweetsFileName = "/Users/ebkahveci/Dev/CS464/250K_User_Splitted_Tweets/english_tweets_only_hashtags_noslang_stemmed_nostopword_dateformatted_201310-training-1.txt";
        final BufferedReader trainingTweetsBr = new BufferedReader(new InputStreamReader(new FileInputStream(trainingTweetsFileName),
                Charset.forName("UTF-8")));


        final String pcFileName = "/Users/ebkahveci/Dev/CS464/250K_User_Splitted_Tweets/english_tweets_only_hashtags_noslang_stemmed_nostopword_dateformatted_201310-training-1_pc.txt";
        final BufferedReader pcBr = new BufferedReader(new InputStreamReader(new FileInputStream(pcFileName),
                Charset.forName("UTF-8")));

        final String testTweetsFileName = "/Users/ebkahveci/Dev/CS464/250K_User_Splitted_Tweets/english_tweets_only_hashtags_noslang_stemmed_nostopword_dateformatted_201310-test-1.txt";
        final BufferedReader testTweetsBr = new BufferedReader(new InputStreamReader(new FileInputStream(testTweetsFileName),
                Charset.forName("UTF-8")));


        String line;

        final Map<Tuple2<String, String>, Integer> hashTagWordPairCounts = new HashMap<Tuple2<String, String>, Integer>();
        final Map<String, Integer> hashTagTotalWordCounts = new HashMap<String, Integer>();

        final Multimap<String, String> hashTagWords = HashMultimap.create();

        final Set<String> allHashTags = new HashSet<String>();
        final Set<String> allWords = new HashSet<String>();
        final Map<String, Integer> wordOccurences = new HashMap<String, Integer>();

        int lineCount = 0;

        final Integer zeroInt = Integer.valueOf(0);
        final Integer oneInt = Integer.valueOf(1);
        final double zeroDouble = zeroInt.doubleValue();
        final double oneDouble = oneInt.doubleValue();

        int numberOfTrainingTweets = 0;

        final Map<String, Integer> hashTagCountsOnTraining = new HashMap<String, Integer>();

        while ((line = trainingTweetsBr.readLine()) != null) {
            numberOfTrainingTweets++;

            final Tuple3<String, String, String> tweet = Step1_ClarifyTweets
                    .parseTweet(line);
            if (tweet != null) {

                final String tweetText = tweet._3;
                final String[] tokens = tweetText.split(" ");
                final Set<String> tweetWords = new HashSet<String>();
                final Set<String> tweetHashTags = new HashSet<String>();

                for (String token : tokens) {
                    if (token.charAt(0) == '#') {
                        tweetHashTags.add(token);
                    } else {
                        tweetWords.add(token);
                    }
                }

                if (tweetWords.isEmpty() || tweetHashTags.isEmpty()) {
                    continue;
                }

                for (String word : tweetWords) {
                    Integer wordOccurence = wordOccurences.get(word);
                    wordOccurence = wordOccurence != null ? wordOccurence : zeroInt;
                    wordOccurences.put(word, wordOccurence + 1);
                }

                for (String hashTag : tweetHashTags) {

                    Integer hashTagCount = hashTagCountsOnTraining.get(hashTag);
                    hashTagCount = hashTagCount != null ? hashTagCount : zeroInt;
                    hashTagCountsOnTraining.put(hashTag, hashTagCount + 1);

                    for (String word : tweetWords) {

                        final Tuple2<String, String> hashTagWordPair = new Tuple2<String, String>(hashTag, word);

                        Integer hashTagWordPairCount = hashTagWordPairCounts.get(hashTagWordPair);
                        hashTagWordPairCount = hashTagWordPairCount != null ? hashTagWordPairCount : zeroInt;
                        hashTagWordPairCounts.put(hashTagWordPair, hashTagWordPairCount + 1);


                        Integer hashTagTotalWordCount = hashTagTotalWordCounts.get(hashTag);
                        hashTagTotalWordCount = hashTagTotalWordCount != null ? hashTagTotalWordCount : zeroInt;
                        hashTagTotalWordCounts.put(hashTag, hashTagTotalWordCount + 1);
                        hashTagWords.put(hashTag, word);


                        allWords.add(word);
                    }

                    allHashTags.add(hashTag);
                }
            }

            if (++lineCount % 50000 == 0) {
                System.out.println("Training Line #: " + lineCount);
            }
        }

        final Map<String, Double> idfs = new HashMap<String, Double>();

        for (String eachWord : allWords) {
            final double idf = Math.log(((double) numberOfTrainingTweets) / wordOccurences.get(eachWord).doubleValue());
            idfs.put(eachWord, idf);
        }

        final List<Double> idfValues = new ArrayList<Double>(idfs.values());
        final Double minIdf = Collections.min(idfValues);
        final Double maxIdf = Collections.max(idfValues);
        final Double idfRange = maxIdf - minIdf;

        for (String eachWord : allWords) {
            final double idf = idfs.get(eachWord);
            final double normalizedIdf = (idf - minIdf) / idfRange;
            idfs.put(eachWord, normalizedIdf);
        }

        trainingTweetsBr.close();

        final Map<String, Double> hashTagProbs = new HashMap<String, Double>();

        while ((line = pcBr.readLine()) != null) {
            final String[] tokens = line.split(",");
            final String hashTag = tokens[0];
            final Double prob = Double.valueOf(tokens[1]);

            hashTagProbs.put(hashTag, prob);
        }

        pcBr.close();


        int hit = 0;
        int miss = 0;

        lineCount = 0;

        final List<Integer> ranks = new ArrayList<Integer>();

        while ((line = testTweetsBr.readLine()) != null) {

            final Tuple3<String, String, String> tweet = Step1_ClarifyTweets
                    .parseTweet(line);
            if (tweet != null) {

                final String tweetText = tweet._3;

                final String[] tokens = tweetText.split(" ");
                final Set<String> tweetWords = new HashSet<String>();
                final Set<String> tweetHashTags = new HashSet<String>();

                for (String token : tokens) {
                    if (token.charAt(0) == '#') {
                        tweetHashTags.add(token);
                    } else {
                        tweetWords.add(token);
                    }
                }

                if (tweetWords.isEmpty() || tweetHashTags.isEmpty()) {
                    continue;
                }

                boolean found = false;
                for (String tweetHashTag : tweetHashTags) {
                    final Integer hashTagCount = hashTagCountsOnTraining.get(tweetHashTag);
                    if (hashTagCount != null && hashTagCount.compareTo(zeroInt) > 0) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    continue;
                }

                final List<Tuple2<String, Double>> candidateHashTagProbs = new ArrayList<Tuple2<String, Double>>();

                for (String candidateHashTag : allHashTags) {

                    double candidateHashTagTotalProb = hashTagProbs.get(candidateHashTag);

                    for (String tweetWord : tweetWords) {

                        Integer a = hashTagWordPairCounts.get(new Tuple2<String, String>(candidateHashTag, tweetWord));
                        a = a != null ? a : zeroInt;

                        final double b = ALPHA;

                        final Integer c = hashTagTotalWordCounts.get(candidateHashTag);
                        final double d = b * hashTagWords.get(candidateHashTag).size();

                        double probXGivenC = (a.doubleValue() + b) / (c.doubleValue() + d);

                        if (enableIdf) {
                            Double idf = idfs.get(tweetWord);
                            idf = idf != null ? idf : zeroDouble;

                            final double reverseIdf = oneDouble - idf;
                            probXGivenC = Math.pow(probXGivenC, reverseIdf);
                        }

                        candidateHashTagTotalProb += Math.log10(probXGivenC);
                    }

                    candidateHashTagProbs.add(new Tuple2<String, Double>(candidateHashTag, candidateHashTagTotalProb));
                }

                Collections.sort(candidateHashTagProbs, new Comparator<Tuple2<String, Double>>() {

                    public int compare(Tuple2<String, Double> tuple1, Tuple2<String, Double> tuple2) {
                        return -tuple1._2.compareTo(tuple2._2);
                    }
                });

                final List<Tuple2<String, Double>> foundHashTagsWithProbs = candidateHashTagProbs.subList(0, numberOfCandidatesToConsider);

                final List<String> foundHashTags = new ArrayList<String>(Collections2.transform(foundHashTagsWithProbs, new Function<Tuple2<String, Double>, String>() {

                    public String apply(cs464.Tuple2<String, Double> input) {
                        return input._1;
                    }
                }));

                for (String tweetHashTag : tweetHashTags) {
                    int foundRank = foundHashTags.indexOf(tweetHashTag);
                    if (foundRank < 0) {
                        foundRank = numberOfCandidatesToConsider;
                    }

                    System.out.println("FoundRank: " + foundRank);
                    ranks.add(foundRank);
                }

                boolean match = false;

                for (Tuple2<String, Double> eachFoundHashTag : foundHashTagsWithProbs) {
                    if (tweetHashTags.contains(eachFoundHashTag._1)) {
                        match = true;
                        break;
                    }
                }

                if (match) {
                    hit++;
                } else {
                    miss++;
                }
            }

            System.out.println("Test Line #: " + ++lineCount + " Hit: " + hit + " Miss: " + miss + " Accuracy: " + ((double) hit / ((double) hit + miss)));
        }


        final Statistics stats = new Statistics(ranks);
        System.out.println("Mean: " + stats.getMean() + " Median: " + stats.median() + " Standard Deviation.: " + stats.getStdDev());
        System.out.println("Accuracy: " + ((double) hit / ((double) hit + miss)));
    }

    public static class Statistics {
        private final List<Integer> data;

        public Statistics(List<Integer> data) {
            this.data = new ArrayList<Integer>(data);
            Collections.sort(data);
        }

        public double getMean() {
            double sum = 0.0;
            for (double a : data)
                sum += a;
            return sum / data.size();
        }

        public double getVariance() {
            double mean = getMean();
            double temp = 0;
            for (double a : data)
                temp += (mean - a) * (mean - a);
            return temp / data.size();
        }

        public double getStdDev() {
            return Math.sqrt(getVariance());
        }

        public double median() {
            if (data.size() % 2 == 0) {
                return (data.get((data.size() / 2) - 1) + data.get(data.size() / 2)) / 2.0;
            } else {
                return data.get(data.size() / 2);
            }
        }
    }

}
