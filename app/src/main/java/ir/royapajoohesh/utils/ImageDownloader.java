package ir.royapajoohesh.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/* Created by DrTJ @ April 2015 */

public class ImageDownloader extends AsyncTask<Void, Integer, Void> {

    private ProgressBar pb;
    private String url;
    private Button save;
    private Context c;
    private int progress;
    private ImageView img;
    private Bitmap bmp;
    private TextView percent;
    private ImageLoaderListener listener;

    public ImageDownloader(String url, ProgressBar pb, Button save,
                           ImageView img, TextView percent, Context c, Bitmap bmp, ImageLoaderListener listener) {
/*--- we need to pass some objects we are going to work with ---*/
        this.url = url;
        this.pb = pb;
        this.save = save;
        this.c = c;
        this.img = img;
        this.percent = percent;
        this.bmp = bmp;
        this.listener = listener;
    }

    public interface ImageLoaderListener {
        void onImageDownloaded(Bitmap bmp);
    }

    @Override
    protected void onPreExecute() {
        progress = 0;
        pb.setVisibility(View.VISIBLE);
        percent.setVisibility(View.VISIBLE);
        Toast.makeText(c, "starting download", Toast.LENGTH_SHORT).show();

        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        bmp = getBitmapFromURL(url);

        while (progress < 100) {
            progress += 1;
            publishProgress(progress);
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        pb.setProgress(values[0]);
        percent.setText(values[0] + "%");

        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void result) {
        if (listener != null)
            listener.onImageDownloaded(bmp);

        img.setImageBitmap(bmp);
        save.setEnabled(true);
        Toast.makeText(c, "download complete", Toast.LENGTH_SHORT).show();

        super.onPostExecute(result);
    }



    public static byte[] getBitmapBytesFromURL(String link){
    	try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();

            ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
            byte[] buffer = new byte[2048];
            
            while (true) {
                int r = input.read(buffer);
                if (r == -1) break;
                outBytes.write(buffer, 0, r);
            }
            
            return outBytes.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("getBmpFromUrl error: ", e.getMessage());
            return null;
        }
    }
    
    public static Bitmap getBitmapFromURL(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            
            InputStream input = connection.getInputStream();
            
            return BitmapFactory.decodeStream(input);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("getBmpFromUrl error: ", e.getMessage());
            return null;
        }
    }

}