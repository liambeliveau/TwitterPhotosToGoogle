package liambeliveau.twitterphotostogoogle.services;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

public class ImageDownloaderService {
    private static final OkHttpClient client = new OkHttpClient();

    /**
     * downloads all images in the list to the specified folder
     * @param imageUrls list of URLs of all images to download
     * @param downloadFolder where to download images to
     * @throws IOException
     */
    public void downloadImages(List<String> imageUrls, String downloadFolder) throws IOException{
        for (String imageUrl : imageUrls) {
            downloadImage(imageUrl,downloadFolder);
        }

    }

    /**
     * downloads the specified image
     * @param imageUrl URL of the image
     * @param downloadFolder where to download the image to
     * @throws IOException
     */
    public void downloadImage(String imageUrl, String downloadFolder) throws IOException{
        Request request = new Request.Builder().url(imageUrl).build();
        Response response = client.newCall(request).execute();
        byte[] bytes = response.body().bytes();
        URL url = new URL(imageUrl);
        File targetFile = new File(downloadFolder,FilenameUtils.getName(url.getPath()));
        try(OutputStream outStream = new FileOutputStream(targetFile)) {
            outStream.write(bytes);
            System.out.println("Downloaded file: " + targetFile.getName());
        }


    }
}
