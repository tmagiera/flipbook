package com.example.tmagiera.flipbook;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ListView;

import com.example.tmagiera.flipbook.KwejkXmlParser.Entry;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DownloadKwejkXmlTask extends AsyncTask<String, String, String> {
    private Activity activity;
    private static List<Entry> entries;
    private Integer pageNumber;
    private boolean emitPageNumber = false;

    public DownloadKwejkXmlTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(String... url) {
        Log.d(this.getClass().getSimpleName(), "Downloading: " + url[0]);
        String data = download(url[0]);
        if (url.length > 1) {
            emitPageNumber = true;
        }

        return data;
    }

    @Override
    protected void onPostExecute(String data) {
        if (data == null) {
            return;
        }

        try {
            KwejkXmlParser parser = new KwejkXmlParser();
            parser.parse(data);

            if (entries == null) {
                entries = parser.getEntryList();
            } else {
                entries.addAll(parser.getEntryList());
            }
            pageNumber = parser.getPageNumber();

        } catch (Exception e) {
            Log.d(this.getClass().getSimpleName(), "Cannot getEntryList XML " + data);
            e.printStackTrace();
            return;
        }

        Log.d(this.getClass().getSimpleName(), "Number of entries: " + entries.size());
        EntryAdapter adapter = new EntryAdapter(activity, entries);
        ListView listView = (ListView) activity.findViewById(R.id.listview);
        listView.setAdapter(adapter);

        if (emitPageNumber) {
            Intent dataIntent = new Intent("pageNumber");
            dataIntent.putExtra("pageNumber", pageNumber);

            LocalBroadcastManager.getInstance(activity).sendBroadcast(dataIntent);
        }
    }

    private String download(String location) {

        Log.d(this.getClass().getSimpleName(), location);
        try {
            URL url = new URL(location);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            int response = conn.getResponseCode();
            Log.d(this.getClass().getSimpleName(), "The response code is: " + response);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String read;

            try {
                while ((read = br.readLine()) != null) {
                    sb.append(read);
                }

                Log.d(this.getClass().getSimpleName(), "Type: text Bytes: " + sb.toString().length() + " Data: " + sb.toString());
            } catch (Exception e) {
                Log.d(this.getClass().getSimpleName(), "Error");
            }

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
