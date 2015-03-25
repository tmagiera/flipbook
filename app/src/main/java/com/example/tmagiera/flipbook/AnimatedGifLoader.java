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

    private static Map<String, byte[]> gifMap;

    public AnimatedGifLoader() {
        gifMap = new HashMap<String, byte[]>();
    }

    public byte[] fetchDrawable(String urlString) {
        if (gifMap.containsKey(urlString)) {
            Log.d(this.getClass().getSimpleName(), "returned from cache :" + urlString);
            return gifMap.get(urlString);
        }

        try {
            Log.d(this.getClass().getSimpleName(), "requesting a gif :" + urlString);
            InputStream is = fetch(urlString);
            byte[] animatedGif = ByteStreams.toByteArray(is);
            gifMap.put(urlString, animatedGif);

            Log.d(this.getClass().getSimpleName(), "got a gif :" + urlString);
            return animatedGif;
        } catch (MalformedURLException e) {
            Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
            return null;
        } catch (IOException e) {
            Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
            return null;
        }
    }

    public void fetchAnimatedGifOnThread(final String urlString, final AnimatedGifImageView animatedGif) {

        if (gifMap.containsKey(urlString)) {
            //imageViewReference.get().setImageDrawable(drawableMap.get(urlString));
            animatedGif.setAnimatedGif(gifMap.get(urlString), AnimatedGifImageView.TYPE.FIT_CENTER);
        }

        //final WeakReference<AnimatedGifImageView> animatedGifViewReference = new WeakReference(animatedGif);

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                //animatedGifViewReference.get().setAnimatedGif((byte[]) message.obj, AnimatedGifImageView.TYPE.STREACH_TO_FIT);
//                imageView.animatge();
                //imageView.setEnabled(true);
                //imageView.setBackground(R.drawable.ic_loading);
                animatedGif.setAnimatedGif((byte[]) message.obj, AnimatedGifImageView.TYPE.STREACH_TO_FIT);
                //animatedGif.invalidate();
                //imageView.setEnabled(true);
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