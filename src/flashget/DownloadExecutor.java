package flashget;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/** DownloadExecutor that execute and cancel the downloads
 *
 * @author Anusid Wachiracharoenwong
 */
public class DownloadExecutor extends Controller {

    // Array of DownloadTask
    ArrayList<DownloadTask> tasklist = new ArrayList<>();

    /** call this method to start download
     *
     * @param url url of the file
     * @param length file length
     * @param outputFile output file to write
     * @param downloadLabel label to show status and byte downloaded
     * @param ProgressBarArray progressbar array of sub threads
     * @param progressBar main progressbar to update progress
     */
    public void Download(URL url, long length, File outputFile,  Label downloadLabel, Label threadsLabel, ProgressBar[] ProgressBarArray , ProgressBar progressBar) {
        int threadUsed;

        // check if file is larger than 50MB (52,428,800 in binary)
        if (length <= 52_428_800) { // less than 50 MB
            threadUsed = 1; // 1 thread
        } else { // more than 50 mb
            threadUsed = 5; // 5 thread
        }

        // size of each thread
        long chunkSize = 4096 * 4;
        long chunkNumber = (long) (Math.ceil(length / chunkSize));
        size = (chunkNumber / threadUsed) * chunkSize;

        // reset visibility every time download new file
        for(ProgressBar pb : ProgressBarArray) {
            pb.setVisible(true);
        }
        threadsLabel.setVisible(true);


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
                        downloadLabel.setText(newValue);
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
            tasklist.get(i).messageProperty().addListener(statusListener);

            // update the progress bar whenever the worker updates progress
            ProgressBarArray[i].progressProperty().bind(tasklist.get(i).progressProperty());

            // Start executing the task
            executor.execute(tasklist.get(i));

            System.out.println("Stating thread" + (i + 1));

        }
        if(threadUsed == 5) {
            // update the main progress bar according to the sub-thread
            progressBar.progressProperty().bind(tasklist.get(0).progressProperty().multiply(0.2).add(
                    tasklist.get(1).progressProperty().multiply(0.2).add(tasklist.get(2).progressProperty().multiply(0.2).add(
                            tasklist.get(3).progressProperty().multiply(0.2).add(tasklist.get(4).progressProperty().multiply(0.2))
                    ))
            ));
        } else {
            progressBar.progressProperty().bind((tasklist.get(0).progressProperty()));
            for(ProgressBar pb : ProgressBarArray) {
                pb.setVisible(false);
            }
            threadsLabel.setVisible(false);
        }


        executor.shutdown();
    }

    public void cancel(Label Filename, TextField URLField, File outputFile) {
        // cancel all the tasks
        for (DownloadTask task : tasklist) {
            task.cancel();
        }

        // set file name to empty
        Filename.setText(" ");
        // clear url filed
        URLField.clear();
        // set url to null
        url = null;
        // clear tasks list
        tasklist.clear();
        // delete the cancel file on exit
        outputFile.deleteOnExit();
    }
}
