package flashget;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * download file from input url and write to the
 * output file using Buffered
 *
 * @author Anusid Wachiracharoenwong
 */
public class DownloadTask extends Task<Long> {

    private URL url; // url of the input
    private File outfile; // output file
    private long start; //start position
    private long size; // size of thread


    public DownloadTask(URL url, File outfile, long start, long size) {
        this.url = url;
        this.outfile = outfile;
        this.start = start;
        this.size = size;
    }

    @Override
    protected Long call()  {
        final int BUFFERSIZE = 16*1024; // buffer size
        long bytesRead = 0; // byte read form the input
        String range ; // range of each thread

        byte[] buffer = new byte[BUFFERSIZE];

        long startTime = System.nanoTime();
        // create input stream and output stream
        try {

            // URL connection;
            URLConnection connection = url.openConnection(); // this one to get length

            // set the range of thread
            if( size > 0) {
                range = String.format("bytes=%d-%d", start, start+size-1);
            }
            else {
                // size not given read from start byte to the end of the file
                range = String.format("bytes=%d", start);
            }

            // set request property
            connection.setRequestProperty("Range", range);

            System.out.println(range);
            // read input from url and write to output file)
            int n = 0;

            // create input stream
            InputStream in = connection.getInputStream() ;

            // random access the file
            RandomAccessFile randomAccessFile = new RandomAccessFile(outfile,"rwd");
            // seek to location to start writing
            randomAccessFile.seek(start);

            do {
                if(n < 0) break;;

                randomAccessFile.write(buffer, 0, n);
                bytesRead += n;

                // update progress and value
                updateProgress(bytesRead, size);
                updateValue( bytesRead );


                // when the cancel button is pressed
                if (isCancelled()) {
                    updateMessage("Cancelled"); // call update message to label
                    updateProgress(0,size);
                    break;
                }

                if (bytesRead == size) {
                    updateMessage("Download Completed");
                    break;
                }

            } while (  (n = in.read(buffer)) < size);

            // close the i/0
            randomAccessFile.close();
            in.close();

            // give time
            double elapsed = 1.0E-9*( System.nanoTime() - startTime );
            System.out.printf("Elapsed %.6f sec\n", elapsed);

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR); // throw an error alert box
            alert.setContentText("File can't be written or File is not found");
            alert.show();
        }
        return bytesRead;
    }
}
