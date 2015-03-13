package com.example.tmagiera.flipbook;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class KwejkXmlLoader {
    private static Map<String, String> xmlMap;
    //private WeakReference<ImageView> imageViewReference;

    public KwejkXmlLoader() {
        xmlMap = new HashMap<String, String>();
    }

    public String fetchXml(String urlString) {
        if (xmlMap.containsKey(urlString)) {
            Log.d(this.getClass().getSimpleName(), "returned from cache :" + urlString);
            return xmlMap.get(urlString);
        }

        try {
            String xml = fetch(urlString);
            xmlMap.put(urlString, xml);
            Log.d(this.getClass().getSimpleName(), "got a xml: " + xml);
            return xml;
        } catch (MalformedURLException e) {
            Log.e(this.getClass().getSimpleName(), "fetchXml failed", e);
            return null;
        } catch (IOException e) {
            Log.e(this.getClass().getSimpleName(), "fetchXml failed", e);
            return null;
        }
    }

    public void fetchXmlOnThread(final String urlString, final String xml) {

        if (xmlMap.containsKey(urlString)) {
            xml.concat(xmlMap.get(urlString));
        }

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                xml.concat((String) message.obj);
            }
        };

        Thread thread = new Thread() {
            @Override
            public void run() {
                String xml = fetchXml(urlString);
                Message message = handler.obtainMessage(1, xml);
                handler.sendMessage(message);
            }
        };
        thread.start();
    }

    private String fetch(String urlString) throws MalformedURLException, IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet request = new HttpGet(urlString);
        HttpResponse response = httpClient.execute(request);
        return response.getEntity().getContent().toString();
    }

}