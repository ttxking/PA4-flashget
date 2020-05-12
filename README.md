
## Multi-thread File Downloader

**Author by** Anusid Wachiracharoenwong (@ttxking)
    

### Description :
an UI application that downloads file from URL. It uses multiple threads to download parts of a file in parallel. 
This helps you speed up your downloads. If the file is larger than 50MB (52,428,800 bytes in binary) it will use 5 threads. Otherwise, it will use 1 thread

### Main Features
![UI](https://s3-ap-southeast-1.amazonaws.com/img-in-th/bc316f9df957272c4bf09188098ab055.png)

* **URL TextField**  - UI for the user to enter the URL
* **Download Button**  - the user click on this button to download
* **Cancel Button**  - the user click on this button to cancel download
* **ProgressBar** - this progress bar show overall download progress of the program
* **SubProgressBar** - these 5 progress bars shows the progress of each thread

### Usage 

1. When you start the program 
    * Enter URL 
    * Click Download Button
    
    ![EnterURL](https://s3-ap-southeast-1.amazonaws.com/img-in-th/867fdca21b774483abfd52b983afbc28.png)
    
2. After click Download Button
    * enter the file name (the default file name is from URL)
    * choose directory to put the file (initial directory is *user.home*)
    * click save
    
    ![FileChooser](https://s3-ap-southeast-1.amazonaws.com/img-in-th/969a7ee53f1fb768716bbd66dc7885be.png)
    
3. After selected file destination
    * the program will start downloading 
    * the progressbar will update the process of the download
        
    If the file is more than 50MB it will use 5 threads
    ![ShowProgress>50MB](https://s3-ap-southeast-1.amazonaws.com/img-in-th/88edde18d45d68cf5bebe6e65bcbf66b.png)
    Otherwise it will use one thread
    ![ShowPorgress<50MB](https://s3-ap-southeast-1.amazonaws.com/img-in-th/ce06a6f0854c82c42571e09768940c91.png)
    
4. After the download is complete
    * Status Label will show "Download Completed"
    * You can see the time used in the terminal
    
    ![Done](https://s3-ap-southeast-1.amazonaws.com/img-in-th/d789f963ade592e9b3b223846a67b30e.png)
    #### Extra
    
    * Cancel the download - when the user click cancel all tasks will stop and also the progressBar. The Label will show
"Cancelled".
        
    ![Cancel](https://s3-ap-southeast-1.amazonaws.com/img-in-th/532efc05b0431f6c53deb070e4933296.png)
    
    * Invalid URL - when the user enters non-URL text , the program will prompt an error 
    
    ![Errors](https://s3-ap-southeast-1.amazonaws.com/img-in-th/9132dead74c3d945ddddf69c0cb40cb9.png)
    
    * Clear - when the users click on this buttons , the text field will be cleared. The Status Label will be "status"
    and the progress bar will become 5 as defaults
    
    ![Clear](https://s3-ap-southeast-1.amazonaws.com/img-in-th/b55e3bc5b18e36c92df041eb9a778ad6.png)
   


### How to run
    



### Educational Value
1. How to use threads
2. Connect to a URL in Java
3. Design a JavaFX and using Scene Builder
4. Read from a URL , get the size of file from a URL and write the content to a file
5. Using RandomAccessFile class to go to any location in a file and then write (or read) starting at that location.


## Technology 
- JavaFX11
- Scene Builder
- HTTP connection


### Design Pattern
   * Observer Pattern => in DownloadExecutor class
   
### UML Diagram



 

