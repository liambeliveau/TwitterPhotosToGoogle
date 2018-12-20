package liambeliveau.twitterphotostogoogle.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * runnable class used to get authentication with twitter
 */
public class Authenticate {
    private static final String TWITTER_CREDENTIALS_FILE_PATH = "src/main/resources/twitterCredentials.json";
    public static void main(String args[]) throws Exception {
        // The factory instance is re-useable and thread safe.
        Twitter twitter = TwitterFactory.getSingleton();

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        TwitterCredentials twitterCredentials = gson.fromJson(readFile(TWITTER_CREDENTIALS_FILE_PATH,
                StandardCharsets.US_ASCII), TwitterCredentials.class);
        twitter.setOAuthConsumer(twitterCredentials.getTwitterConsumerKey(),
                twitterCredentials.getTwitterConsumerSecret());

        RequestToken requestToken = twitter.getOAuthRequestToken();
        AccessToken accessToken = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (null == accessToken) {
            System.out.println("Open the following URL and grant access to your account:");
            System.out.println(requestToken.getAuthorizationURL());
            System.out.print("Enter the PIN(if available) or just hit enter.[PIN]:");
            String pin = br.readLine();
            try {
                if (pin.length() > 0) {
                    accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                } else {
                    accessToken = twitter.getOAuthAccessToken();
                }
            } catch (TwitterException te) {
                if (401 == te.getStatusCode()) {
                    System.out.println("Unable to get the access token.");
                } else {
                    te.printStackTrace();
                }
            }
        }
        //persist to the accessToken for future reference.
        storeAccessToken(twitter.verifyCredentials().getId(), accessToken);
        System.exit(0);
    }

    private static void storeAccessToken(long useId, AccessToken accessToken) {
        System.out.println("Access token: "+accessToken.getToken());
        System.out.println("Secret token: "+accessToken.getTokenSecret());
    }

    static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path).toAbsolutePath());
        return new String(encoded, encoding);
    }
}
