package com.example.tmagiera.flipbook;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.common.io.ByteStreams;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class AnimatedGifLoader {
    private final static int cacheSize = 20;
    private static Map<String, byte[]> drawableMap;
    //private WeakReference<ImageView> imageViewReference;

    public AnimatedGifLoader() {
        drawableMap = new HashMap<String, byte[]>();
    }

    public byte[] fetchDrawable(String urlString) {
        if (drawableMap.containsKey(urlString)) {
            Log.d(this.getClass().getSimpleName(), "returned from cache :" + urlString);
            return drawableMap.get(urlString);
        }

        try {
            InputStream is = fetch(urlString);
            byte[] animatedgif = ByteStreams.toByteArray(is);

            if (drawableMap.size() > cacheSize) {
                for (Map.Entry<String, byte[]> entry : drawableMap.entrySet()) {
                    drawableMap.remove(entry.getKey());
                    Log.d(this.getClass().getSimpleName(),"removed from cache: " + entry.getKey());
                    break;
                };
            }
            drawableMap.put(urlString, animatedgif);
            Log.d(this.getClass().getSimpleName(), "got a gif");
            return animatedgif;
        } catch (MalformedURLException e) {
            Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
            return null;
        } catch (IOException e) {
            Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
            return null;
        }
    }

    public void fetchAnimatedGifOnThread(final String urlString, final AnimatedGifImageView imageView) {

        //imageViewReference = new WeakReference<ImageView>(imageView);

        if (drawableMap.containsKey(urlString)) {
            //imageViewReference.get().setImageDrawable(drawableMap.get(urlString));
            imageView.setAnimatedGif(drawableMap.get(urlString), AnimatedGifImageView.TYPE.FIT_CENTER);
        }

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                //imageViewReference.get().setImageDrawable((Drawable) message.obj);
                imageView.setAnimatedGif((byte[]) message.obj, AnimatedGifImageView.TYPE.FIT_CENTER);
            }
        };

        Thread thread = new Thread() {
            @Override
            public void run() {
                byte[] drawable = fetchDrawable(urlString);
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