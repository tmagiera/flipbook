package com.example.tmagiera.flipbook;

import android.app.Activity;
import android.os.AsyncTask;
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
    private Integer pageNumber = 0;
    private static EntryAdapter adapter;

    public DownloadKwejkXmlTask(Activity activity) {
        this.activity = activity;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(String... url) {
        Log.d(this.getClass().getSimpleName(), "Downloading: " + url[0]);
        String data = download(url[0]);

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

        if (adapter == null) {
            adapter = new EntryAdapter(activity, entries);
            ListView listView = (ListView) activity.findViewById(R.id.listview);
            listView.setAdapter(adapter);
            listView.setOnScrollListener(new EndlessScrollListener(activity, pageNumber - 1));
            Log.d(this.getClass().getSimpleName(), "Create new adapter");
        } else {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    adapter.notifyDataSetChanged();
                    Log.d(this.getClass().getSimpleName(), "Notify adapter of a data change");
                }
            });
        }
    }

    private String download(String location) {
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
