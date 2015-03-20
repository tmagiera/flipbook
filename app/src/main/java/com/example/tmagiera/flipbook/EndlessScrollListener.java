package com.example.tmagiera.flipbook;

import android.app.Activity;
import android.widget.AbsListView;

/**
 * Created by tmagiera on 2015-03-13.
 */
public class EndlessScrollListener implements AbsListView.OnScrollListener {

    private int visibleThreshold = 10;
    private int currentPage;
    private int previousTotal = 0;
    private boolean loading = true;
    private Activity activity;

    public EndlessScrollListener() {
    }
    public EndlessScrollListener(Activity activity, Integer currentPage) {
        this.activity = activity;
        this.currentPage = currentPage;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
                currentPage--;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            // I load the next page of gigs using a background task,
            // but you can call any function here.
            DownloadKwejkXmlTask task = new DownloadKwejkXmlTask(activity);
            task.execute("http://api.kwejk.pl?page=" + currentPage);

            loading = true;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }
}