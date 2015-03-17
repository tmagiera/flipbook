package com.example.tmagiera.flipbook;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.FrameLayout;

import com.example.tmagiera.flipbook.KwejkXmlParser.Entry;

import java.util.List;

public class MainActivity extends ActionBarActivity {
    private static Activity mActivity;
    private static FrameLayout frame;
    private static final int CONTENT_VIEW_ID = 10101010;
    private static List<Entry> parsedXml;
    private static Integer currentPageNumber;

//    private static Map<String, String> entries;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(R.layout.activity_main);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("pageNumber"));
        new DownloadKwejkXmlTask(this).execute("http://api.kwejk.pl", "emitPageNumber");
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Integer pageNumber = intent.getIntExtra("pageNumber", 0);
            Log.d("Broadcasted", pageNumber.toString());
            for (int i = pageNumber - 1; i > pageNumber - 3; i--) {
                new DownloadKwejkXmlTask(mActivity).execute("http://api.kwejk.pl?page=" + i);
            }
        }
    };
}
