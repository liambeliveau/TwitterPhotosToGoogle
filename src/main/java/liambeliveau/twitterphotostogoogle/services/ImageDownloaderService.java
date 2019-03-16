package liambeliveau.twitterphotostogoogle.services;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageDownloaderService {
    private static final OkHttpClient client = new OkHttpClient();

    /**
     * downloads the specified image
     *
     * @param imageUrl URL of the image
     * @throws IOException
     */
    public InputStream downloadImage(String imageUrl) throws IOException {
        Request request = new Request.Builder().url(imageUrl).build();
        Response response = client.newCall(request).execute();
        assert response.body() != null;
        byte[] bytes = response.body().bytes();
        return new ByteArrayInputStream(bytes);
    }
}
