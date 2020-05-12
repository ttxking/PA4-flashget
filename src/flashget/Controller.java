package flashget;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;


import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


/**
 * Controller that contains all the fxml component and UI control
 *
 * @author Anusid Wachiracharoenwong
 */
public class Controller {

    // TextField to enter URL
    @FXML
    private TextField URLField;

    // Button to clear TextField
    @FXML
    private Button clearButton;

    // Button to download file
    @FXML
    private Button downloadButton;

    // Progress bar to show download process
    @FXML
    private ProgressBar progressBar;

    // Button to cancel download
    @FXML
    private Button cancelButton;

    // Label showing bytes downloaded
    @FXML
    private Label downloadLabel;

    // Label showing filename
    @FXML
    private Label Filename;

    // ProgressBar of thread1
    @FXML
    private ProgressBar threadPB1;

    // ProgressBar of thread2
    @FXML
    private ProgressBar threadPB2;

    // ProgressBar of thread3
    @FXML
    private ProgressBar threadPB3;

    // ProgressBar of thread4
    @FXML
    private ProgressBar threadPB4;

    // ProgressBar of thread5
    @FXML
    private ProgressBar threadPB5;

    // Label of threads
    @FXML
    private Label threadsLabel;

    // size of byte in each thread
    protected long size;

    // length of file
    protected long length;

    // DownloadTask
    protected DownloadTask task = null;

    // url of the file to download
    protected URL url;

    // initial directory of outfile
    File outputFile = new File(System.getProperty("user.home"));

    // Array of DownloadTask
    ArrayList<DownloadTask> tasklist = new ArrayList<>();

    // Array of ThreadProgressBar
    private ProgressBar[] ProgressBarArray;

    // Downloaded byte by program
    protected long byteDownloaded = 0;

    // downloadExecutor class
    protected DownloadExecutor downloadExecutor;


    /**
     * set on the action for the button
     */
    @FXML
    public void initialize() {

        downloadButton.setOnAction(this::downloadWorker);
        cancelButton.setOnAction(this::stopWorker);
        clearButton.setOnAction(this::clearHandler);

        ProgressBarArray = new ProgressBar[5]; //5 is number of Threads and progressbar
        ProgressBarArray[0] = threadPB1;
        ProgressBarArray[1] = threadPB2;
        ProgressBarArray[2] = threadPB3;
        ProgressBarArray[3] = threadPB4;
        ProgressBarArray[4] = threadPB5;

    }

    /**
     * Call this method to clear textfield
     */
    public void clearHandler(ActionEvent event) {
        Filename.setText(" ");
        URLField.clear();
        downloadLabel.setText("Status");
        tasklist.clear();

        // reset visibility
        for(ProgressBar pb : ProgressBarArray) {
            pb.setVisible(true);
            // unbind to set value
            pb.progressProperty().unbind();
            pb.progressProperty().setValue(0);
        }
        //unbind to set value
        progressBar.progressProperty().unbind();
        progressBar.progressProperty().setValue(0);

        threadsLabel.setVisible(true);
    }


    /**
     * Call this method to start the download Task
     */
    public void downloadWorker(ActionEvent event) {
        tasklist.clear(); // clear the list before start new download

        // create class to handle with filename and url
        URLToFileHandler urlToFileHandler = new URLToFileHandler();
        urlToFileHandler.URLConnection(URLField);
        // get url
        url = urlToFileHandler.getURL();
        // get file length
        length = urlToFileHandler.getFileLength();

        // check if url can be downloaded .If it can't be downloaded , the length will be -1
        if (length > 0) {
            outputFile = urlToFileHandler.FileChooser(outputFile, Filename, URLField);
            // Download check for cancel
            boolean downloadCancel = urlToFileHandler.getDownloadStatus();


            // if download is not cancel execute download process
            if (!downloadCancel) {
                downloadExecutor = new DownloadExecutor();
                downloadExecutor.Download(url, length, outputFile, downloadLabel, threadsLabel, ProgressBarArray, progressBar);
            }

        } else {
            url = null;
            URLField.clear(); // clear textfield
        }

    }

    /**
     * call this method to cancels the task
     */
    public void stopWorker(ActionEvent event) {

        // call the cancel method of downloadExecutor class
        downloadExecutor.cancel(Filename, URLField, outputFile);
    }

}
