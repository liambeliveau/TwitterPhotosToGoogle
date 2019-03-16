package liambeliveau.twitterphotostogoogle;

import liambeliveau.twitterphotostogoogle.services.GoogleDriveService;
import liambeliveau.twitterphotostogoogle.services.ImageDownloaderService;
import liambeliveau.twitterphotostogoogle.services.TwitterService;
import liambeliveau.twitterphotostogoogle.util.TwitterCredentials;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.List;

public class TwitterPhotosToGoogleApplication {
    private static final String IMAGE_FOLDER = "/home/liam/Documents/testDownload/";
    //private static final String TWITTER_CREDENTIALS_FILE_PATH = "src/main/resources/twitterCredentials.json";

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        TwitterCredentials twitterCredentials = new TwitterCredentials(
                System.getenv("twitterConsumerKey"),
                System.getenv("twitterConsumerSecret"),
                System.getenv("twitterAccessToken"),
                System.getenv("twitterSecretToken"),
                System.getenv("twitterScreenName")
                );

        //downloadFromTwitter(twitterCredentials);
        //uploadImagesToGoogle(IMAGE_FOLDER, getFileNames(IMAGE_FOLDER));
        GoogleDriveService.listDriveFiles();
    }

    /**
     * downloads all images from the twitter account to IMAGE_FOLDER
     */
    private static void downloadFromTwitter(TwitterCredentials tc) {
        TwitterService twitterService = new TwitterService(tc.getTwitterConsumerKey(), tc.getTwitterConsumerSecret(),
                tc.getTwitterAccessToken(), tc.getTwitterSecretToken());
        ImageDownloaderService imageDownloaderService = new ImageDownloaderService();
        try {
            //get a list of all image URLs posted by the twitter account
            List<String> imageUrls = twitterService.getImageUrls(tc.getTwitterScreenName());
            //download all images in the list to IMAGE_FOLDER
            imageDownloaderService.downloadImages(imageUrls, IMAGE_FOLDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * uploads the specified image to drive
     *
     * @param imageFolder folder that the image is in
     * @param imageName   name of the image
     * @throws GeneralSecurityException
     * @throws IOException
     */
    private static void uploadImageToGoogle(String imageFolder, String imageName)
            throws GeneralSecurityException, IOException {
        GoogleDriveService.uploadImage(imageFolder + imageName, imageName);
    }

    /**
     * uploads every file named in imageNames within folder to drive
     *
     * @param folder     path to upload files from
     * @param imageNames names of every file to upload
     * @throws GeneralSecurityException
     * @throws IOException
     */
    private static void uploadImagesToGoogle(String folder, String[] imageNames)
            throws GeneralSecurityException, IOException {
        for (String s : imageNames) {
            uploadImageToGoogle(folder, s);
        }
    }

    /**
     * gets the names of all files in the folder at path f
     *
     * @return array of file names
     */
    private static String[] getFileNames(String f) {
        File folder = new File(f);
        File[] listOfFiles = folder.listFiles();
        assert listOfFiles != null;
        String[] fileNames = new String[listOfFiles.length];

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                fileNames[i] = listOfFiles[i].getName();
            }
        }

        return fileNames;
    }

    static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path).toAbsolutePath());
        return new String(encoded, encoding);
    }
}
