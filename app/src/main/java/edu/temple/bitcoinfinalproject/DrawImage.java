package edu.temple.bitcoinfinalproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class DrawImage extends AsyncTask<String, Void, Bitmap> {

    private Listener randomListener;


    public interface Listener{

        void onImageLoaded(Bitmap bitmap);
        void onError();
    }

    public DrawImage(Listener listener) {

        randomListener = listener;
    }


    @Override
    protected void onPostExecute(Bitmap bitmap) {

        if (bitmap != null) {

            randomListener.onImageLoaded(bitmap);

        } else {

            randomListener.onError();
        }
    }

    @Override
    protected Bitmap doInBackground(String... args) {

        try {

            return BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}