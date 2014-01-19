package cs464;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static Twitter myTwitter;

    // 1857089228
    // 1241808492
    public static void main(String[] args) {
        System.out.println("Hello World!");
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("Q0jbm9O60NgTh4c4wZieKg")
                .setOAuthConsumerSecret(
                        "Ie8yMCZs5Wd1cmyMW6lR5KHU5u5m1db3wRfY9QKAE1I")
                .setOAuthAccessToken(
                        "478880573-7czGGUkrvCPSe6rE6M4VymCzayQ8WRqCg4Impcnc")
                .setOAuthAccessTokenSecret(
                        "YDqwAOEc9hdNBniyxmYfIdKXBV2hi9JmlBRXgZs");
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        ArrayList<String> listOfNames = new ArrayList<String>();
        listOfNames.add("@hilalcebeci");
        listOfNames.add("ceyhunyilmaz");
        System.out.println(getUserTimeLineByName(twitter, listOfNames)
                .toString());

        // List<Status> statuses;
        // try {
        // Query query = new Query("Cocaine");
        // QueryResult result;
        // result = twitter.search(query);
        // statuses = result.getTweets();
        // Query query = new Query();
        // query.setSinceId(1241808492);
        // query.setQuery("");
        // statuses = twitter.search(query).getTweets();
        // for (Status status : statuses) {
        // System.out.println(status.getUser().getScreenName() + ":" +
        // status.getText());
        // }
        // statuses = twitter.getHomeTimeline();
        // System.out.println("Showing home timeline.");
        // for (Status status : statuses) {
        // System.out.println(status.getUser().getName() + ":" +
        // status.getText());
        // }
        // statuses = twitter.getUserTimeline("@hilalcebeciii");
        // for (Status status : statuses) {
        // System.out.println(status.getUser().getScreenName() + ":"
        // + status.getText());
        // }
        // } catch (TwitterException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        //
        // }
    }

    public static HashMap getUserTimeLineByName(Twitter twitter,
                                                ArrayList<String> names) {

        HashMap mp;
        mp = new HashMap<String, List<Status>>();

        for (String nm : names) {
            System.out.println("Start Getting Tweets for user: " + nm);
            mp.put(nm, getMoreTweetsFromUser(twitter, nm, 2));
        }
        return mp;
    }

    public static List<Status> getMoreTweetsFromUser(Twitter tw, String name,
                                                     int numberOfPage) {

        List<Status> result = null;
        Paging pg = new Paging(1, 40);

        try {
            for (int i = 1; i < numberOfPage; i++) {
                if (i == 1) {
                    result = tw.getUserTimeline(name, pg);
                    System.out.println("Page " + i + " : " + result.toString());
                } else if (result != null) {
                    List<Status> temp = tw.getUserTimeline(name, pg);
                    System.out.println("Page " + i + " : " + temp.toString());
                    result.addAll(temp);
                    pg = new Paging(i, 40);
                }
            }
        } catch (TwitterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;

    }
}
