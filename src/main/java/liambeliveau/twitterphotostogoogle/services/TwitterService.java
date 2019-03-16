package liambeliveau.twitterphotostogoogle.services;

import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.List;

public class TwitterService {
    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String secretToken;
    private Twitter twitter;


    public TwitterService(String consumerKey, String consumerSecret, String accessToken, String secretToken) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.accessToken = accessToken;
        this.secretToken = secretToken;

    }

    /**
     * gets the Twitter object associated with the service
     * if none exists, create it
     *
     * @return a twitter object
     * @throws TwitterException
     */
    private Twitter getTwitter() throws TwitterException {
        if (twitter == null) {
            Configuration configuration = new ConfigurationBuilder()
                    .setOAuthConsumerKey(consumerKey)
                    .setOAuthConsumerSecret(consumerSecret)
                    .setOAuthAccessToken(accessToken)
                    .setOAuthAccessTokenSecret(secretToken)
                    .build();
            TwitterFactory tf = new TwitterFactory(configuration);
            twitter = tf.getInstance();
            twitter.verifyCredentials();

        }
        return twitter;

    }

    /**
     * gets the URLs of all images posted by the specified twitter account
     *
     * @param screenName the account handle to get images from
     * @return a list of URLs of images
     * @throws TwitterException
     */
    public List<String> getImageUrls(String screenName) throws TwitterException {
        List<Status> tweets = getTweets(screenName);

        List<String> imageUrls = new ArrayList<String>();

        for (Status status : tweets) {
            MediaEntity[] mediaEntities = status.getMediaEntities();
            for (MediaEntity mediaEntity : mediaEntities) {
                imageUrls.add(mediaEntity.getMediaURL());
            }

        }

        return imageUrls;
    }

    /**
     * gets all tweets posted by the specified twitter account
     *
     * @param screenName the account handle to get tweets from
     * @return a list of Status objects
     * @throws TwitterException
     */
    public List<Status> getTweets(String screenName) throws TwitterException {
        return getTwitter().timelines().getUserTimeline(screenName);
    }

}
