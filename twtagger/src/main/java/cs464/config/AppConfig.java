package cs464.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class AppConfig {

    public static final String TWITTER_CONFIG_FILE_NAME = "twitter.properties";

    private final Config config;

    public AppConfig() {
        final Config applicationConfig = ConfigFactory.load(); // Loads the default application.properties file.
        final Config twitterConfig = ConfigFactory.load(TWITTER_CONFIG_FILE_NAME);
        config = applicationConfig.withFallback(twitterConfig);
    }

    public String twitterConsumerKey() {
        return config.getString("consumerKey");
    }

    public String twitterConsumerSecret() {
        return config.getString("consumerSecret");
    }

    public String bearerAccessToken() {
        return config.getString("bearerAccessToken");
    }

    public String twitterEmail() {
        return config.getString("email");
    }

    public String twitterPassword() {
        return config.getString("password");
    }

    public long sleepTimeOnSignInInMillis() {
        return config.getLong("sleepTimeOnSignInInMillis");
    }

    public long sleepTimeOnReloadInMillis() {
        return config.getLong("sleepTimeOnReloadInMillis");
    }

    public int numberOfUsersToFetchForEachUser() {
        return config.getInt("numberOfUsersToFetchForEachUser");
    }

    public int totalNumberOfUsersToFetch() {
        return config.getInt("totalNumberOfUsersToFetch");
    }

    public int numberOfTweetsToFetchPerUser() {
        return config.getInt("numberOfTweetsToFetchPerUser");
    }

}
