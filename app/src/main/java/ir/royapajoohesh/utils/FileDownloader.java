package ir.royapajoohesh.utils;
import android.os.AsyncTask;

import java.net.URL;

/* Created by DrTJ @ April 2015 */

public class FileDownloader extends AsyncTask<URL, Integer, Void> {
    String fileURL;
    Byte[] data;

    public FileDownloader(String url) {
        this.fileURL = url;
    }

    public interface downloadCompleteListener{
        void OnDownloadCompleted(String url, Byte[] data);
        void OnDownloadFailed(String url, String error);
    }

    @Override
    protected Void doInBackground(URL... params) {
        return null;
    }


    public static boolean Download(String url){
        Boolean isDone = false;

        FileDownloader downloader = new FileDownloader(url);
        downloader.execute();


        return isDone;
    }
}
