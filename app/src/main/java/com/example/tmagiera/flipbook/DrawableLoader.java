package com.example.tmagiera.flipbook;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class DrawableLoader {
    private final static int cacheSize = 20;
    private static Map<String, Drawable> drawableMap;
    //private WeakReference<ImageView> imageViewReference;

    public DrawableLoader() {
        drawableMap = new HashMap<String, Drawable>();
    }

    public Drawable fetchDrawable(String urlString) {
        if (drawableMap.containsKey(urlString)) {
            Log.d(this.getClass().getSimpleName(), "returned from cache :" + urlString);
            return drawableMap.get(urlString);
        }

        try {
            InputStream is = fetch(urlString);
            Drawable drawable = Drawable.createFromStream(is, "src");
            Log.d(this.getClass().getSimpleName(),"count of object in cache: " + drawableMap.size());
            if (drawableMap.size() > cacheSize) {
                for (Map.Entry<String, Drawable> entry : drawableMap.entrySet()) {
                    Log.d(this.getClass().getSimpleName(),"remove from cache: " + entry.getKey());
                    drawableMap.remove(entry.getKey());
                    break;
                };
            }
            drawableMap.put(urlString, drawable);
            Log.d(this.getClass().getSimpleName(), "got a thumbnail drawable: " + drawable.getBounds() + ", "
                    + drawable.getIntrinsicHeight() + "," + drawable.getIntrinsicWidth() + ", "
                    + drawable.getMinimumHeight() + "," + drawable.getMinimumWidth());
            return drawable;
        } catch (MalformedURLException e) {
            Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
            return null;
        } catch (IOException e) {
            Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
            return null;
        }
    }

    public void fetchDrawableOnThread(final String urlString, final ImageView imageView) {

        //imageViewReference = new WeakReference<ImageView>(imageView);

        if (drawableMap.containsKey(urlString)) {
            //imageViewReference.get().setImageDrawable(drawableMap.get(urlString));
            imageView.setImageDrawable(drawableMap.get(urlString));
        }

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                //imageViewReference.get().setImageDrawable((Drawable) message.obj);
                imageView.setImageDrawable((Drawable) message.obj);
            }
        };

        Thread thread = new Thread() {
            @Override
            public void run() {
                Drawable drawable = fetchDrawable(urlString);
                Message message = handler.obtainMessage(1, drawable);
                handler.sendMessage(message);
            }
        };
        thread.start();
    }

    private InputStream fetch(String urlString) throws MalformedURLException, IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet request = new HttpGet(urlString);
        HttpResponse response = httpClient.execute(request);
        return response.getEntity().getContent();
    }

}