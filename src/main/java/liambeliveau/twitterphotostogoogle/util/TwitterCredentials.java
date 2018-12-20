package liambeliveau.twitterphotostogoogle.util;

/**
 * instantiable class to hold twitter credentials from JSON
 */
public class TwitterCredentials {
    private String twitterConsumerKey;
    private String twitterConsumerSecret;
    private String twitterAccessToken;
    private String twitterSecretToken;
    private String twitterScreenName;

    public TwitterCredentials(String twitterConsumerKey, String twitterConsumerSecret,
                              String twitterAccessToken, String twitterSecretToken,
                              String twitterScreenName) {
        this.twitterConsumerKey = twitterConsumerKey;
        this.twitterConsumerSecret = twitterConsumerSecret;
        this.twitterAccessToken = twitterAccessToken;
        this.twitterSecretToken = twitterSecretToken;
        this.twitterScreenName = twitterScreenName;
    }

    public String getTwitterConsumerKey() {
        return twitterConsumerKey;
    }

    public String getTwitterConsumerSecret() {
        return twitterConsumerSecret;
    }

    public String getTwitterAccessToken() {
        return twitterAccessToken;
    }

    public String getTwitterSecretToken() {
        return twitterSecretToken;
    }

    public String getTwitterScreenName() {
        return twitterScreenName;
    }
}
