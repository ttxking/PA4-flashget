package flashget;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
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

    @FXML
    private ProgressBar threadPB1;

    @FXML
    private ProgressBar threadPB2;

    @FXML
    private ProgressBar threadPB3;

    @FXML
    private ProgressBar threadPB4;

    @FXML
    private ProgressBar threadPB5;

    // close Program menu
    @FXML
    private MenuItem closeOption;


    // string of URL
    private String urlName;

    // size of byte in each thread
    private long size;

    // connection
    private URLConnection connection;

    // length of file
    private long length;

    // DownloadTask
    private DownloadTask task = null;

    // url of the file to download
    private URL url;

    // initial directory of outfile
    File outputFile = new File(System.getProperty("user.home"));

    // Array of DownloadTask
    ArrayList<DownloadTask> tasklist = new ArrayList<>();

    // Array of ThreadProgressBar
    private ProgressBar[] ProgressBarArray;

    // Download check for cancel
    private boolean downloadCancel;

    // Downloaded byte by program
    private long byteDownloaded = 0;


    /**
     * set on the action for the button
     */
    @FXML
    public void initialize() {

        downloadButton.setOnAction(this::downloadWorker);
        cancelButton.setOnAction(this::stopWorker);
        clearButton.setOnAction(this::clearHandler);
        closeOption.setOnAction(this::Exithandler);

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
    }

    public void Exithandler(ActionEvent event) {
        System.exit(0);
    }



    /**
     * Call this method to start the download Task
     */
    public void downloadWorker(ActionEvent event) {
        tasklist.clear(); // clear the list before start new download

        try {
            urlName = URLField.getText().trim();

            url = new URL(urlName);
            // URL connection;
            connection = url.openConnection();
            // get the size of file at URL
            length = connection.getContentLengthLong();

        } catch (NullPointerException e) {
            System.err.println(e.getMessage());

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR); // throw an error alert box
            alert.setContentText("Please input a valid URL");
            alert.show();
            URLField.clear();
        }

        // check if url can be downloaded .If it can't be downloaded , the length will be -1
        if (length > 0) {
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

            } catch (NullPointerException e) {
                downloadCancel = true;
                URLField.clear();
            }

            // if download is not cancel execute download process
            if (!downloadCancel) {

                // size of each thread
                long chunkSize = 4096 * 4;
                long chunkNumber = (long) (Math.ceil(length / chunkSize));
                size = (chunkNumber / 5) * chunkSize;

                int threadUsed = 5;


                // update Label that shows byte downloaded
                ChangeListener<Long> changeListener =
                        new ChangeListener<Long>() {
                            @Override
                            public void changed(ObservableValue<? extends Long> observable, Long oldValue, Long newValue) {
                                if (oldValue == null) {
                                    oldValue = 0L; // in case the old value is null set in to zero
                                }
                                byteDownloaded += newValue - oldValue; // byte download on each thread
                                downloadLabel.setText(String.format("%d/%d", byteDownloaded, length)); // update the label
                            }
                        };

                // update Label that shows status value of downloader
                ChangeListener<String> statusListener =
                        new ChangeListener<String>() {

                            @Override
                            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                                downloadLabel.setText( newValue );
                            }
                        };



                // create a executor service
                ExecutorService executor = Executors.newFixedThreadPool(threadUsed);

                // create download task
                for (int i = 0; i < threadUsed; i++) {

                    if (i == threadUsed - 1) { // last thread
                        task = new DownloadTask(url, outputFile, size * i, length - (size * i)); // last thread handle the rest
                    } else {
                        task = new DownloadTask(url, outputFile, (size * i), size);
                    }

                    tasklist.add(task);

                    // add observer (ChangeListener) of the valueProperty
                    tasklist.get(i).valueProperty().addListener(changeListener);

                    // add it observer (statusListener) of the messageProperty
                    tasklist.get(i).messageProperty().addListener( statusListener);

                    // update the progress bar whenever the worker updates progress
                    ProgressBarArray[i].progressProperty().bind(tasklist.get(i).progressProperty());

                    // Start executing the task
                    executor.execute(tasklist.get(i));

                    System.out.println("Stating thread" + (i + 1));

                }
                // update the main progress bar according to the sub-thread
                progressBar.progressProperty().bind(tasklist.get(0).progressProperty().multiply(0.2).add(
                        tasklist.get(1).progressProperty().multiply(0.2).add(tasklist.get(2).progressProperty().multiply(0.2).add(
                                tasklist.get(3).progressProperty().multiply(0.2).add(tasklist.get(4).progressProperty().multiply(0.2))
                        ))
                ));

                executor.shutdown();
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

        // cancel all the tasks
        for (DownloadTask task : tasklist) {
            task.cancel();
        }

        Filename.setText(" ");
        URLField.clear();
        url = null;
        tasklist.clear();
        outputFile.deleteOnExit();

    }

    // TODO 1. Update Sub ProgressBar 2. Update Main ProgressBar 4. FIx add Listener
}
