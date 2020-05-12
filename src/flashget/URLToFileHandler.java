package flashget;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * handle the url and the file before download
 *
 * @author Anusid Wachiracharoenwong
 */
public class URLToFileHandler {
    private String urlName;
    private URL url;
    private long fileLength;

    // Download check for cancel
    private boolean downloadCancel;

    /**
     * this method set up the url connection and get urlName
     * @param URLField textfield that contain URL
     */
    public void URLConnection(TextField URLField) {
        try {
            urlName = URLField.getText().trim();

            url = new URL(urlName);
            // URL connection;
            URLConnection connection = url.openConnection();
            // get the size of file at URL
            fileLength = connection.getContentLengthLong();

        } catch (NullPointerException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR); // throw an error alert box
            alert.setContentText("Please input a valid URL");
            alert.show();
            URLField.clear();

        }

    }

    /**
     * this method
     * @param outputFile as the initial directory
     * @param Filename label to set filename
     * @param URLField URL field that contains url
     * @return output file to write down
     */
    public File FileChooser(File outputFile, Label Filename , TextField URLField) {

        // create the output file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Destination"); // set destination of fileChooser

        if (outputFile != null) { // check output is not null
            fileChooser.setInitialDirectory(outputFile.getParentFile());
        }

        // get file extension
        String fileExtension = urlName.substring(urlName.lastIndexOf("."));

        // set file type
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*" + fileExtension));

        // set initial file name
        fileChooser.setInitialFileName(urlName.substring(urlName.lastIndexOf('/') + 1, urlName.length()));

        // check for cancel select in save dialog
        try {
            outputFile = fileChooser.showSaveDialog(null);
            //  set file name at filename label
            Filename.setText(outputFile.getName());

        } catch (NullPointerException e) { // when the cancel on FileChooser is pressed
            // download is cancel
            downloadCancel = true;
            // clear URLField
            URLField.clear();
        }
        return outputFile;
    }

    /**
     * this method return file length
     * @return fileLength
     */
    public long getFileLength() {
        return fileLength;
    }

    /**
     * this method return URL
     * @return url
     */
    public URL getURL(){
        return url;
    }

    /**
     * this method return continuity of download
     * if the cancel is press on FileChooser then the download won't start
     * @return downloadCancel
     */
    public boolean getDownloadStatus() {
        return downloadCancel;
    }
}
