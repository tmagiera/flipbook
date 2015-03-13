package com.example.tmagiera.flipbook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.example.tmagiera.flipbook.KwejkXmlParser.Entry;

import java.util.List;

public class MainActivity extends ActionBarActivity {
    private static FrameLayout frame;
    private static final int CONTENT_VIEW_ID = 10101010;
//    private static Map<String, String> entries;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//
//        FrameLayout frame = new FrameLayout(this);
//        frame.setId(CONTENT_VIEW_ID);
//
//        FragmentManager fm = getFragmentManager();
//        Fragment fragment = fm.findFragmentByTag("imageFragment");
//        if (fragment == null) {
//            FragmentTransaction ft = fm.beginTransaction();
//            ft.add(CONTENT_VIEW_ID, new ImageFragment(), "imageFragment");
////            ft.add(CONTENT_VIEW_ID, new ImageFragment(), "imageFragment");
////            ft.add(CONTENT_VIEW_ID, new ImageFragment(), "imageFragment");
//            ft.commit();
//        }
//
//        setContentView(frame);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("Download"));

        invokeDownload();
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String location = intent.getStringExtra("location");
            switch (intent.getStringExtra(DownloadIntentService.FIELD_TYPE)) {
                case DownloadIntentService.TYPE_TEXT:
                    String textData = intent.getStringExtra("textData");
                    Log.d("Broadcast", action + " " + location);
                    List<Entry> parsedXml = null;
                    try {
                        parsedXml = new KwejkXmlParser().parse(textData);
                    } catch (Exception e) {
                        Log.d("XMLParser", "Cannot parse XML " + textData);
                    }

                    EntryAdapter adapter = new EntryAdapter(context, parsedXml);
                    ListView listView = (ListView) findViewById(R.id.listview);
                    listView.setAdapter(adapter);

                    break;
            }
        }
    };

    private void invokeDownload() {
        Intent intentApi = new Intent(this, DownloadIntentService.class);
        intentApi.putExtra(DownloadIntentService.FIELD_ID, 0);
        intentApi.putExtra(DownloadIntentService.FIELD_URL, "http://api.kwejk.pl");
        intentApi.putExtra(DownloadIntentService.FIELD_TYPE, DownloadIntentService.TYPE_TEXT);
        startService(intentApi);
    }
}
