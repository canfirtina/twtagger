package cs464;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import cs464.config.AppConfig;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class SeleniumDemo {

    private static final Logger logger = Logger.getLogger(SeleniumDemo.class);

    public static final Function<Tuple2<String, String>, String> TWITTER_ID_SCREEN_NAME_JOINER = new Function<Tuple2<String, String>, String>() {
        public String apply(Tuple2<String, String> input) {
            return input._1 + ":" + input._2;
        }
    };

    public static final String FETCHED_USERS_FILE_NAME = "fetchedUsers.txt";

    public static final String PROCESSED_SCREEN_NAMES_FILE_NAME = "processedScreenNames.txt";

    public static final String SCREEN_NAMES_QUEUE_FILE_NAME = "screenNamesQueue.txt";

    public static void main(String[] args) throws InterruptedException {

        final AppConfig appConfig = new AppConfig();

        final WebDriver webDriver = new ChromeDriver();

        final SeleniumTwitterApi seleniumTwitterApi = new SeleniumTwitterApi(appConfig, webDriver);

        final boolean initSuccessful = seleniumTwitterApi.init();

        if (initSuccessful) {

            final Queue<String> screenNamesQueue = new LinkedList<String>();
            final Set<String> processedScreenNames = new HashSet<String>();

            screenNamesQueue.add("fogus");
            screenNamesQueue.add("SpaceX");
            screenNamesQueue.add("googleresearch");
            screenNamesQueue.add("venkat_s");
            screenNamesQueue.add("AmericaTimes");
            screenNamesQueue.add("elonmusk");
            screenNamesQueue.add("arstechnica");
            screenNamesQueue.add("dickc");
            screenNamesQueue.add("newscientist");
            screenNamesQueue.add("WIRED");
            screenNamesQueue.add("TheEconomist");
            screenNamesQueue.add("NewYorker");
            screenNamesQueue.add("msnbc");
            screenNamesQueue.add("WashTimes");
            screenNamesQueue.add("nytimesworld");
            screenNamesQueue.add("USATODAY");
            screenNamesQueue.add("WSJ");
            screenNamesQueue.add("BreakingNews");
            screenNamesQueue.add("science");
            screenNamesQueue.add("bbcscitech");
            screenNamesQueue.add("wiredscience");
            screenNamesQueue.add("NatGeo");
            screenNamesQueue.add("businessnews");
            screenNamesQueue.add("CNBC");
            screenNamesQueue.add("georgezachary");
            screenNamesQueue.add("SouthPark");
            screenNamesQueue.add("jayleno");
            screenNamesQueue.add("FoodNetwork");
            screenNamesQueue.add("Schwarzenegger");
            screenNamesQueue.add("HISTORY");
            screenNamesQueue.add("JimCameron");
            screenNamesQueue.add("WhiteHouse");
            screenNamesQueue.add("Telegraph");
            screenNamesQueue.add("Insidesocialgam");
            screenNamesQueue.add("ipaananen");
            screenNamesQueue.add("bantonsson");
            screenNamesQueue.add("jboner");
            screenNamesQueue.add("KentBeck");
            screenNamesQueue.add("debasishg");
            screenNamesQueue.add("allthingsd");
            screenNamesQueue.add("GamesRadar");
            screenNamesQueue.add("BillGates");
            screenNamesQueue.add("HillaryClinton");
            screenNamesQueue.add("BenAffleck");
            screenNamesQueue.add("timoreilly");
            screenNamesQueue.add("biz");
            screenNamesQueue.add("FastCompany");
            screenNamesQueue.add("WalterIsaacson");
            screenNamesQueue.add("LeoDiCaprio");
            screenNamesQueue.add("JimCameron");
            screenNamesQueue.add("YaoMing");
            screenNamesQueue.add("linkinpark");
            screenNamesQueue.add("TomCruise");
            screenNamesQueue.add("BarackObama");
            screenNamesQueue.add("martinfowler");
            screenNamesQueue.add("timberners_lee");
            screenNamesQueue.add("ggreenwald");
            screenNamesQueue.add("jeremyscahill");
            screenNamesQueue.add("marissamayer");
            screenNamesQueue.add("jeffweiner");
            screenNamesQueue.add("tfadell");
            screenNamesQueue.add("stevewoz");
            screenNamesQueue.add("tomhanks");
            screenNamesQueue.add("TEDNews");
            screenNamesQueue.add("thomashawk");
            screenNamesQueue.add("ForbesTech");
            screenNamesQueue.add("harryglaser");
            screenNamesQueue.add("spolsky");
            screenNamesQueue.add("Yahoo");
            screenNamesQueue.add("drewhouston");
            screenNamesQueue.add("internetsociety");
            screenNamesQueue.add("stevevinoski");
            screenNamesQueue.add("tastapod");
            screenNamesQueue.add("codinghorror");
            screenNamesQueue.add("ersiner");
            screenNamesQueue.add("google");
            screenNamesQueue.add("PubNub");
            screenNamesQueue.add("joshbloch");
            screenNamesQueue.add("Dropbox");
            screenNamesQueue.add("slashdot");
            screenNamesQueue.add("DZone");
            screenNamesQueue.add("Android");
            screenNamesQueue.add("MongoDB");
            screenNamesQueue.add("fuadm");
            screenNamesQueue.add("thoughtworks");
            screenNamesQueue.add("engadget");
            screenNamesQueue.add("TechCrunch");
            screenNamesQueue.add("dr_dobbs");
            screenNamesQueue.add("TED_TALKS");
            screenNamesQueue.add("CompSciFact");
            screenNamesQueue.add("InsideNetwork");
            screenNamesQueue.add("GamesRadar");
            screenNamesQueue.add("jboner");

            int i = 0;

            final long startTime = System.currentTimeMillis();

            while (processedScreenNames.size() < appConfig.totalNumberOfUsersToFetch() && !screenNamesQueue.isEmpty()) {
                final String screenName = screenNamesQueue.remove();

                if (processedScreenNames.contains(screenName)) {
                    continue;
                }

                try {

                    final Collection<Tuple2<String, String>> followers = seleniumTwitterApi.followersByScreenName(screenName);
                    final Collection<Tuple2<String, String>> friends = seleniumTwitterApi.friendsByScreenName(screenName);

                    for (Tuple2<String, String> eachFollower : followers) {
                        final String followerScreenName = eachFollower._2;

                        if (!processedScreenNames.contains(followerScreenName)) {
                            screenNamesQueue.add(followerScreenName);
                        }
                    }

                    for (Tuple2<String, String> eachFriend : friends) {
                        final String friendScreenName = eachFriend._2;

                        if (!processedScreenNames.contains(friendScreenName)) {
                            screenNamesQueue.add(friendScreenName);
                        }
                    }

                    processedScreenNames.add(screenName);
                    persist(screenName, followers, friends);

                    logger.info("[processed] " + ++i + ": " + screenName);

                } catch (Exception e) {
                    logger.error("[processing-failed] ScreenName: " + screenName + " ExceptionMessage: " + e.getMessage(), e);
                    break;
                }
            }

            final String processedScreenNamesJoined = Joiner.on("\n").join(processedScreenNames);
            writeToFile(PROCESSED_SCREEN_NAMES_FILE_NAME, processedScreenNamesJoined);

            final String screenNamesQueueJoined = Joiner.on("\n").join(screenNamesQueue);
            writeToFile(SCREEN_NAMES_QUEUE_FILE_NAME, screenNamesQueueJoined);

            final long endTime = System.currentTimeMillis();
            System.out.println("Completed in " + ((endTime - startTime) / 1000) + " seconds");
        } else {
            System.out.println("Init failed.");
        }

        webDriver.close();
    }

    private static void persist(String screenName, Collection<Tuple2<String, String>> followers, Collection<Tuple2<String, String>> friends) {

        final Collection<String> followersJoined = Collections2.transform(followers, TWITTER_ID_SCREEN_NAME_JOINER);
        final Collection<String> friendsJoined = Collections2.transform(friends, TWITTER_ID_SCREEN_NAME_JOINER);

        final String followersJoinedStr = Joiner.on("|").join(followersJoined);
        final String friendsJoinedStr = Joiner.on("|").join(friendsJoined);

        final String line = new StringBuilder(screenName).append(",").append(followersJoinedStr).append(",").append(friendsJoinedStr).toString();

        writeToFile(FETCHED_USERS_FILE_NAME, line);
    }

    private static void writeToFile(String fileName, String line) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
            out.println(line);
        } catch (IOException e) {
            System.out.println("Line not written: " + line);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
