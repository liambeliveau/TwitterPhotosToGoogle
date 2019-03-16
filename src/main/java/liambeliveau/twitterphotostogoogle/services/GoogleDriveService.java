package liambeliveau.twitterphotostogoogle.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GoogleDriveService {
    private static final String APPLICATION_NAME = "Google Drive Image Uploader";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

    /**
     * creates authorized credentials for google drive
     * @param HTTP_TRANSPORT the network HTTP transport
     * @return an authorized credentials object
     * @throws IOException if credentials.json can't be found
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        String credentialsJson = System.getenv("googleServiceCredentials");
        return GoogleCredential.fromStream(new ByteArrayInputStream(credentialsJson.getBytes())).createScoped(SCOPES);
    }

    /**
     * lists files in the drive
     * @throws IOException if credentials.json can't be found
     * @throws GeneralSecurityException
     */
    public static void listDriveFiles() throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

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
     * @param path the path to the image file (including name)
     * @param name the file name
     * @throws GeneralSecurityException
     * @throws IOException if credentials.json can't be found
     */
    public static void uploadImage(String path, String name) throws GeneralSecurityException, IOException {
        String folderID = System.getenv("driveFolderId");
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        //check if a file with the same name already exists in drive
        boolean duplicate = false;
        FileList fileList = service.files().list()
                .setFields("nextPageToken, files(id, name)")
                .execute();
        for(File f : fileList.getFiles()){
            if(Objects.equals(name, f.getName())){
                duplicate = true;
            }
        }

        //upload if a duplicate wasn't found
        if (!duplicate) {
            File fileMetaData = new File();
            fileMetaData.setName(name);
            fileMetaData.setParents(Collections.singletonList(folderID));
            java.io.File filePath = new java.io.File(path);
            FileContent mediaContent = new FileContent("image/jpeg", filePath);
            File file = service.files().create(fileMetaData, mediaContent)
                    .setFields("id, parents")
                    .execute();
            System.out.println("Uploaded file: " + file.getName());
        } else {
            System.out.println("Did not upload file; already in Drive");
        }
    }
}
