package com.kozorezov.githubsearch;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.kozorezov.githubsearch.Models.RepositoryModel;
import com.kozorezov.githubsearch.Services.LazyLoader;
import com.kozorezov.githubsearch.Services.OnNewRequestListener;
import com.kozorezov.githubsearch.Services.SearchQueryProcessor;
import com.kozorezov.githubsearch.Utils.StateSaver;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnNewRequestListener {
    private SearchQueryProcessor searchQueryProcessor;
    private ListView listView;
    private ListAdapter listAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchQueryProcessor = new SearchQueryProcessor(this);
        final Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        listView = (ListView) findViewById(R.id.list_view);
        configureListView(listView);
        if (savedInstanceState != null) {
            retrieveState(savedInstanceState);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        configureSearchView(searchView);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (listView != null && listAdapter != null && searchQueryProcessor != null) {
            saveState(outState);
        }
    }

    @Override
    public void onReceive() {
        if(listView!=null){
            listView.setSelectionFromTop(0,0);
        }
    }

    private void configureSearchView(final SearchView searchView) {
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        searchView.setQueryRefinementEnabled(true);
        searchView.setOnQueryTextListener(searchQueryProcessor);
    }

    private void configureListView(final ListView listView) {
        final List<RepositoryModel> entries = new ArrayList<>();
        listAdapter = new ListAdapter(this, R.id.list_view, entries);
        final RelativeLayout listFooter =
                (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.list_footer, null);
        final RelativeLayout listHeader =
                (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.list_header, null);
        final ProgressBar progressBar = (ProgressBar) listFooter.findViewById(R.id.list_load_progress);
        listView.addFooterView(listFooter);
        listView.addHeaderView(listHeader);
        searchQueryProcessor.setSearchQueryCallback(listAdapter);
        searchQueryProcessor.setOnNewRequestListener(this);
        listView.setAdapter(listAdapter);
        final LazyLoader lazyLoader = new LazyLoader() {
            @Override
            public void loadMore(final AbsListView view,
                                 final int firstVisibleItem,
                                 final int visibleItemCount,
                                 final int totalItemCount) {
                int total = searchQueryProcessor.getTotalEntries();
                if (totalItemCount < total) {
                    progressBar.setVisibility(View.VISIBLE);
                    searchQueryProcessor.requestNextPage();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        };
        listView.setOnScrollListener(lazyLoader);
        listAdapter.registerDataSetObserver(lazyLoader);
    }

    private void saveState(final Bundle outState) {
        searchQueryProcessor.saveCurrentState(outState);
        int listVisiblePosition = listView.getFirstVisiblePosition();
        final View view = listView.getChildAt(0);
        int listPaddingTop = (view == null) ? 0 : (view.getTop() - listView.getPaddingTop());

        StateSaver.saveListVisiblePosition(outState, listVisiblePosition);
        StateSaver.saveListPaddingTop(outState, listPaddingTop);

        final ArrayList<RepositoryModel> entriesList = new ArrayList<>();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            entriesList.add(listAdapter.getItem(i));
        }
        StateSaver.saveEntriesList(outState, entriesList);
    }

    private void retrieveState(final Bundle savedInstanceState) {
        if (searchQueryProcessor != null)
            searchQueryProcessor.retrieveCurrentState(savedInstanceState);
        if (listAdapter != null && listView != null) {
            final List<RepositoryModel> entriesList = StateSaver.loadEntriesList(savedInstanceState);
            if (entriesList != null) {
                listAdapter.addAll(entriesList);
                listView.setSelectionFromTop(
                        StateSaver.getSavedListVisiblePosition(savedInstanceState),
                        StateSaver.getSavedListPaddingTop(savedInstanceState));
                listAdapter.notifyDataSetChanged();
            }
        }
    }
}

