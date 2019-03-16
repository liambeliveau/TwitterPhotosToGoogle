package liambeliveau.twitterphotostogoogle;

import liambeliveau.twitterphotostogoogle.services.GoogleDriveService;
import liambeliveau.twitterphotostogoogle.services.ImageDownloaderService;
import liambeliveau.twitterphotostogoogle.services.TwitterService;
import liambeliveau.twitterphotostogoogle.util.TwitterCredentials;
import org.apache.commons.io.FilenameUtils;
import twitter4j.TwitterException;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.List;

public class TwitterPhotosToGoogleApplication {

    public static void main(String[] args) throws TwitterException, IOException, GeneralSecurityException {
        transferAllPhotos();
    }

    private static void transferAllPhotos() throws TwitterException, IOException, GeneralSecurityException {
        //initialize services
        TwitterCredentials twitterCredentials = new TwitterCredentials(
                System.getenv("twitterConsumerKey"),
                System.getenv("twitterConsumerSecret"),
                System.getenv("twitterAccessToken"),
                System.getenv("twitterSecretToken"),
                System.getenv("twitterScreenName")
        );
        TwitterService twitterService = new TwitterService(twitterCredentials.getTwitterConsumerKey(),
                twitterCredentials.getTwitterConsumerSecret(), twitterCredentials.getTwitterAccessToken(),
                twitterCredentials.getTwitterSecretToken());
        GoogleDriveService driveService = new GoogleDriveService();
        ImageDownloaderService imageDownloaderService = new ImageDownloaderService();

        //get all tweets, image urls from tweets
        List<String> imageUrls = twitterService.getImageUrls(System.getenv("twitterScreenName"));

        //for each image url
        for (String imageUrl : imageUrls) {
            URL url = new URL(imageUrl);
            String imageName = FilenameUtils.getName(url.getPath());
            //if a file of the same name does NOT exist in drive
            if (!driveService.fileExists(imageName)) {
                //download/upload it
                driveService.uploadImage(imageName, imageDownloaderService.downloadImage(imageUrl));
            } else {
                System.out.println("Image already exists: " + imageName);
            }

        }
    }
}