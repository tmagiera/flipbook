package com.example.tmagiera.flipbook;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class DownloadIntentService extends IntentService {
    public static final String FIELD_ID = "id";
    public static final String FIELD_URL = "url";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_EXTRA = "extra_information";
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_BINARY = "binary";

    public DownloadIntentService() {
        super("DownloadIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            Log.d("HTTP", "Not connected");
            if (networkInfo != null) {
                Log.d("HTTP", networkInfo.toString());
            }
            return;
        }

        String location = intent.getStringExtra(FIELD_URL);
        Log.d("HTTP", location);
        try {
            URL url = new URL(location);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            int response = conn.getResponseCode();
            Log.d("HTTP", "The response code is: " + response);

            String textData = null;
            byte[] binaryData = new byte[0];

            switch (intent.getStringExtra(FIELD_TYPE)) {
                case TYPE_TEXT:
                    textData = downloadText(conn.getInputStream());
                    break;
                case TYPE_BINARY:
                    binaryData = downloadBinary(conn.getInputStream());
                    break;
            }

            Intent dataIntent = new Intent("Download");
            dataIntent.putExtra(FIELD_ID, intent.getIntExtra(FIELD_ID, 0));
            dataIntent.putExtra(FIELD_TYPE, intent.getStringExtra(FIELD_TYPE));
            dataIntent.putExtra("location", location);
            dataIntent.putExtra("textData", textData);
            dataIntent.putExtra("binaryData", binaryData);

            LocalBroadcastManager.getInstance(this).sendBroadcast(dataIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] downloadBinary(InputStream is) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] buf = new byte[16384];
        byte[] data = null;

        try {
            while ((nRead = is.read(buf, 0, buf.length)) != -1) {
                buffer.write(buf, 0, nRead);
            }
            data = buffer.toByteArray();

            Log.d("HTTP", "Type: byte Bytes: " + data.length);
        } catch (Exception e) {
            Log.d("HTTP", "Error");
        }

        return data;
    }

    private String downloadText(InputStream is) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String read;

        try {
            while ((read = br.readLine()) != null) {
                sb.append(read);
            }

            Log.d("HTTP", "Type: text Bytes: " + sb.toString().length() + " Data: " + sb.toString());
        } catch (Exception e) {
            Log.d("HTTP", "Error");
        }

        return sb.toString();
    }
}
