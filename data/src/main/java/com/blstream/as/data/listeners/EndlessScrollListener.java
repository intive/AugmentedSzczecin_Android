package com.blstream.as.data.listeners;

import android.widget.AbsListView;

import com.blstream.as.data.fragments.PoiListFragment;

/**
 * Created by Rafal Soudani on 2015-03-26.
 */
public class EndlessScrollListener implements AbsListView.OnScrollListener {

    private final PoiListFragment poiListFragment;
    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;


    public EndlessScrollListener(PoiListFragment poiListFragment) {
        this.poiListFragment = poiListFragment;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        int visibleThreshold = 5;
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
                currentPage++;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            //TODO: Odkomentować gdy serwer będzie obsługiwć PAGINACJE
            //poiListFragment.getPage(currentPage + 1);
            loading = true;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }
}