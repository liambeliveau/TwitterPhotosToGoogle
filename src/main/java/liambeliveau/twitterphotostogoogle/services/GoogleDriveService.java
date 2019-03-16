package liambeliveau.twitterphotostogoogle.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GoogleDriveService {
    private static final String APPLICATION_NAME = "Google Drive Image Uploader";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private Drive service;
    private NetHttpTransport HTTP_TRANSPORT;

    public GoogleDriveService() throws IOException, GeneralSecurityException {
        HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * creates authorized credentials for google drive
     *
     * @return an authorized credentials object
     * @throws IOException if credentials.json can't be found
     */
    private Credential getCredentials() throws IOException {
        String credentialsJson = System.getenv("googleServiceCredentials");
        return GoogleCredential.fromStream(new ByteArrayInputStream(credentialsJson.getBytes())).createScoped(SCOPES);
    }

    /**
     * lists files in the drive
     *
     * @throws IOException              if credentials.json can't be found
     * @throws GeneralSecurityException
     */
    public void listDriveFiles() throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.

        // Print the names and IDs for up to 100 files.
        FileList result = service.files().list()
                .setFields("nextPageToken, files(id, name)")
                .execute();
        List<File> files = result.getFiles();
        if (files == null || files.isEmpty()) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (File file : files) {
                System.out.printf("%s (%s)\n", file.getName(), file.getId());
            }
        }
    }

    /**
     * uploads the specified image to google drive
     *
     * @param name    the file name
     * @param content the file content
     * @throws IOException              if credentials.json can't be found
     */
    public void uploadImage(String name, InputStream content) throws IOException {
        String folderID = System.getenv("driveFolderId");
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();

        //upload
        File fileMetaData = new File();
        fileMetaData.setName(name);
        fileMetaData.setParents(Collections.singletonList(folderID));
        File file = service.files().create(fileMetaData, new InputStreamContent("image/jpeg", content))
                .setFields("id, parents")
                .execute();
        System.out.println("Uploaded file: " + fileMetaData.getName());
    }


    public boolean fileExists(String name) throws IOException {
        boolean duplicate = false;
        FileList fileList = service.files().list()
                .setFields("nextPageToken, files(id, name)")
                .execute();
        for (File f : fileList.getFiles()) {
            if (Objects.equals(name, f.getName())) {
                duplicate = true;
            }
        }
        return duplicate;
    }
}
