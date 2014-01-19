package cs464.ds;

import cs464.Tuple3;

import java.io.*;
import java.nio.charset.Charset;
import java.util.StringTokenizer;

public class Step4_GetStems {

    /**
     * Test program for demonstrating the Stemmer. It reads text from a a list
     * of files, stems each word, and writes the result to standard output. Note
     * that the word stemmed is expected to be in lower case: forcing lower case
     * must be done outside the Stemmer class. Usage: Stemmer file-name
     * file-name ...
     *
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        final String inputFileName = "/Users/ebkahveci/Dev/CS464/250K_User_Splitted_Tweets/english_tweets_only_hashtags_noslang.txt";
        final String outputFileName = "/Users/ebkahveci/Dev/CS464/250K_User_Splitted_Tweets/english_tweets_only_hashtags_noslang_stemmed.txt";

        final InputStream fis = new FileInputStream(inputFileName);
        final BufferedReader br = new BufferedReader(new InputStreamReader(fis,
                Charset.forName("UTF-8")));

        final PrintWriter out = new PrintWriter(new BufferedWriter(
                new FileWriter(outputFileName, true)));
        final Stemmer stemmer = new Stemmer();

        String line;

        int lineCount = 0;

        while ((line = br.readLine()) != null) {

            final Tuple3<String, String, String> tweet = Step1_ClarifyTweets
                    .parseTweet(line);
            if (tweet != null) {

                final String tweetText = tweet._3;
                StringTokenizer tokens = new StringTokenizer(tweetText, " .?!:;,&=");
                StringBuilder stemmedText = new StringBuilder();
                while (tokens.hasMoreTokens()) {
                    String temp = tokens.nextToken();
                    char[] tempArr = temp.toCharArray();
                    if (tempArr.length > 0 && tempArr[0] == '#') {
                        stemmedText.append(temp.toLowerCase()).append(" ");
//                        System.out.println("hashtag: " + temp);
                        continue;
                    }
                    for (char c : tempArr)
                        stemmer.add(Character.toLowerCase(c));
                    stemmer.stem();
                    stemmedText.append(stemmer.toString()).append(" ");
//                    System.out.println("Stem: " + stemmer.toString());
                }
                if (stemmedText.length() > 0)
                    out.println(tweet._1 + "," + tweet._2 + "," + stemmedText.toString());

            }

            if (++lineCount % 50000 == 0) {
                System.out.println("Line #: " + lineCount);
            }
        }

        br.close();
        out.close();
    }
}
