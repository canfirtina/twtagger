package cs464;

import com.google.common.base.Strings;
import cs464.config.AppConfig;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SeleniumTwitterApi {

    private static final Logger logger = Logger.getLogger(SeleniumTwitterApi.class);

    public static final Tuple2<Boolean, Collection<Tuple2<String, String>>> FAIL = new Tuple2<Boolean, Collection<Tuple2<String, String>>>(false, Collections.<Tuple2<String, String>>emptyList());

    private final AppConfig config;

    private final WebDriver webDriver;

    private final JavascriptExecutor jsExecutor;

    public SeleniumTwitterApi(AppConfig config, WebDriver webDriver) {
        this.config = config;
        this.webDriver = webDriver;
        this.jsExecutor = (JavascriptExecutor) webDriver;
    }

    public boolean init() {
        boolean success = true;

        logger.info("Signing in to twitter...");

        try {
            webDriver.get("https://twitter.com");

            final WebElement signInElement = webDriver.findElement(By.className("js-front-signin"));

            final WebElement userName = signInElement.findElement(By.id("signin-email"));
            userName.sendKeys(config.twitterEmail());

            sleep(1000);

            final WebElement password = signInElement.findElement(By.id("signin-password"));
            password.sendKeys(config.twitterPassword());

            sleep(1000);

            final WebElement submitButton = signInElement.findElement(By.tagName("button"));
            submitButton.submit();

            webDriver.findElement(By.className("mini-profile"));

            logger.info("Sign-in completed.");
        } catch (Exception e) {
            // We received captcha response.
            // Enter captcha manually.

            try {
                sleep(config.sleepTimeOnSignInInMillis());

                final WebElement signInElement = webDriver.findElement(By.className("signin-wrapper"));

                final WebElement password = signInElement.findElement(By.className("js-password-field"));
                password.sendKeys(config.twitterPassword());

                final WebElement signInButton = signInElement.findElement(By.tagName("button"));
                signInButton.submit();

                logger.info("Sign-in completed.");
            } catch (Exception e2) {
                logger.error("[captcha-failed]", e2);
                success = false;
            }
        }

        return success;
    }

    public Collection<Tuple2<String, String>> followersByScreenName(final String screenName) {
        return usersByUrl(toFollowersUrl(screenName));
    }

    public Collection<Tuple2<String, String>> friendsByScreenName(final String screenName) {
        return usersByUrl(toFriendsUrl(screenName));
    }

    public Collection<Tuple2<String, String>> usersByUrl(final String url) {

        final Collection<Tuple2<String, String>> users = new ArrayList<Tuple2<String, String>>();

        try {

            webDriver.get(url);

            final int numberOfUsersToFetch = config.numberOfUsersToFetchForEachUser();
            int numberOfUsersCurrentlyFetched = 0;
            int numberOfUsersPreviouslyFetched;

            do {
                numberOfUsersPreviouslyFetched = numberOfUsersCurrentlyFetched;

                final List<WebElement> usersElements = webDriver.findElements(By.className("user-actions"));
                numberOfUsersCurrentlyFetched = usersElements.size();

                logger.debug("CurrentlyFetched: " + numberOfUsersCurrentlyFetched + ", PreviouslyFetched: " + numberOfUsersPreviouslyFetched);

                scrollDownOnPage();
            }
            while (numberOfUsersCurrentlyFetched < numberOfUsersToFetch && numberOfUsersCurrentlyFetched != numberOfUsersPreviouslyFetched);


            for (WebElement eachUserElement : webDriver.findElements(By.className("user-actions"))) {
                if (hasPublicProfile(eachUserElement)) {
                    final String userTwitterId = eachUserElement.getAttribute("data-user-id");
                    final String userScreenName = eachUserElement.getAttribute("data-screen-name");

                    if (userTwitterId != null && userScreenName != null) {
                        users.add(new Tuple2<String, String>(userTwitterId, userScreenName));
                    }
                }
            }

            return users;
        } catch (Exception e) {
            logger.error("[get-users-failed] URL: " + url, e);

            throw new RuntimeException("[get-users-failed] URL: " + url, e);
        }
    }

    public List<Tuple2<String, String>> tweetsOf(final String screenName) {
        final List<Tuple2<String, String>> tweets = new ArrayList<Tuple2<String, String>>();

        final String url = "https://twitter.com/" + screenName;

        boolean isPublic = false;

        try {
            webDriver.get(url);

            webDriver.findElement(By.className("stream-protected"));

        } catch (Exception e) {
            if (e instanceof NoSuchElementException) {
                isPublic = true;
            } else {
                final String errMsg = "[get-tweets-failed] URL: " + url;
                logger.error(errMsg, e);
                throw new RuntimeException(errMsg, e);
            }
        }

        if (isPublic) {
            try {
                final int numberOfTweetsToFetch = config.numberOfTweetsToFetchPerUser();
                int numberOfTweetsCurrentlyFetched = 0;
                int numberOfTweetsPreviouslyFetched;

                do {
                    numberOfTweetsPreviouslyFetched = numberOfTweetsCurrentlyFetched;

                    final List<WebElement> tweetElements = webDriver.findElements(By.className("js-tweet-text"));
                    numberOfTweetsCurrentlyFetched = tweetElements.size();

                    logger.debug("CurrentlyFetched: " + numberOfTweetsCurrentlyFetched + ", PreviouslyFetched: " + numberOfTweetsPreviouslyFetched);

                    scrollDownOnPage();
                }
                while (numberOfTweetsCurrentlyFetched < numberOfTweetsToFetch && numberOfTweetsCurrentlyFetched != numberOfTweetsPreviouslyFetched);

                for (WebElement eachTweetElement : webDriver.findElements(By.className("js-stream-item"))) {

                    boolean retweet = true;
                    try {
                        eachTweetElement.findElement(By.className("retweeted"));
                    } catch (NoSuchElementException e) {
                        retweet = false;
                    }

                    if (!retweet) {
                        final String tweetDate = eachTweetElement.findElement(By.className("tweet-timestamp")).getAttribute("title");
                        final String tweetText = eachTweetElement.findElement(By.className("js-tweet-text")).getText();
                        if (!Strings.isNullOrEmpty(tweetText)) {
                            tweets.add(new Tuple2<String, String>(tweetDate, tweetText));
                        }
                    }
                }

            } catch (Exception e) {
                final String errMsg = "[get-tweets-failed] URL: " + url;
                logger.error(errMsg, e);
                throw new RuntimeException(errMsg, e);
            }
        }

        return tweets;
    }

    private boolean hasPublicProfile(WebElement element) {
        return Boolean.valueOf(element.getAttribute("data-protected")).equals(Boolean.FALSE);
    }

    private void scrollDownOnPage() {
        jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight);");

        sleep(config.sleepTimeOnReloadInMillis());
    }

    private void sleep(final long duration) {
        try {
            Thread.sleep(duration);
        } catch (Exception e) {
            logger.error("[sleep-interrupted]");
        }
    }

    private static String toFollowersUrl(final String screenName) {
        return "https://twitter.com/" + screenName + "/followers";
    }

    private static String toFriendsUrl(final String screenName) {
        return "https://twitter.com/" + screenName + "/following";
    }
}
