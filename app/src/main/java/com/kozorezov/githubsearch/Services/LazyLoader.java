package com.kozorezov.githubsearch.Services;


import android.database.DataSetObserver;
import android.widget.AbsListView;

abstract public class LazyLoader extends DataSetObserver implements AbsListView.OnScrollListener {
    private final static int LOAD_THRESHOLD = 4;
    private boolean isLoaded = true;

    @Override
    public void onScrollStateChanged(final AbsListView view, final int scrollState) {
    }

    @Override
    public void onChanged() {
        isLoaded = true;
    }

    @Override
    public void onScroll(final AbsListView view,
                         final int firstVisibleItem,
                         final int visibleItemCount,
                         final int totalItemCount) {

        if (totalItemCount == 1) {
            return;
        }
        if (isLoaded && (firstVisibleItem + visibleItemCount) >= (totalItemCount - LOAD_THRESHOLD)) {
            isLoaded = false;
            loadMore(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    abstract public void loadMore(final AbsListView view,
                                  final int firstVisibleItem,
                                  final int visibleItemCount,
                                  final int totalItemCount);
}
