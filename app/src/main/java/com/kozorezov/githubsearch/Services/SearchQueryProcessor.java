package com.kozorezov.githubsearch.Services;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.ProgressBar;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kozorezov.githubsearch.R;
import com.kozorezov.githubsearch.Utils.DataParser;
import com.kozorezov.githubsearch.Utils.StateSaver;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

final public class SearchQueryProcessor implements SearchView.OnQueryTextListener {

    private static final String REQUEST_TAG = "com.kozorezov.githubsearch.tag";
    private static final int MIN_QUERY_LENGTH = 3;
    private static final String URL_TEMPLATE = "https://api.github.com/search/repositories?q=%s&page=%d&per_page=%d";
    private static final long SEARCH_DELAY = 500; //delay in millis between input and starting search
    private static final Handler handler = new Handler();
    private static final String CHARSET = "UTF-8";


    private final Activity context;
    private RequestQueue requestQueue;
    private SearchQueryCallback callback;
    private OnNewRequestListener onNewRequestListener;
    private int currentPage = 1;
    private String currentQuery;
    private int totalEntries;
    private ProgressBar searchProgress;

    public SearchQueryProcessor(final Activity context) {
        this.context = context;
    }

    @Override
    public boolean onQueryTextSubmit(final String query) {
        currentPage = 1;
        doRequest(query, currentPage, SearchQueryCallback.Flag.ERASE_LOADED);
        return true;
    }

    @Override
    public boolean onQueryTextChange(final String newText) {
        if (newText.length() < MIN_QUERY_LENGTH) {
            return false;
        }
        currentPage = 1;
        doRequest(newText, currentPage, SearchQueryCallback.Flag.ERASE_LOADED);
        searchProgress = (ProgressBar) context.findViewById(R.id.search_progress);
        searchProgress.setVisibility(View.VISIBLE);
        if(onNewRequestListener!=null) {
            onNewRequestListener.onReceive();
        }
        return true;
    }

    public void requestNextPage() {
        doRequest(currentQuery, currentPage, SearchQueryCallback.Flag.APPEND_LOADED);
    }

    public int getTotalEntries() {
        return totalEntries;
    }

    public void saveCurrentState(final Bundle outState) {
        StateSaver.saveSearchCurrentPage(outState, currentPage);
        StateSaver.saveSearchTotalCount(outState, totalEntries);
        StateSaver.saveSearchCurrentQuery(outState, currentQuery);
    }

    public void retrieveCurrentState(final Bundle savedInstanceState) {
        currentPage = StateSaver.getSearchCurrentPage(savedInstanceState);
        totalEntries = StateSaver.getSearchTotalCount(savedInstanceState);
        currentQuery = StateSaver.getSearchCurrentQuery(savedInstanceState);
    }

    public void setSearchQueryCallback(final SearchQueryCallback callback) {
        this.callback = callback;
    }

    public void setOnNewRequestListener(final OnNewRequestListener onNewRequestListener){
        this.onNewRequestListener = onNewRequestListener;
    }

    private void doRequest(final String query, final int page, final SearchQueryCallback.Flag flag) {
        if (query == null) {
            return;
        }
        currentQuery = query;
        currentPage++;
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        String request;
        try {
            request = String.format(Locale.ENGLISH, URL_TEMPLATE, URLEncoder.encode(query, CHARSET), page, context.getResources().getInteger(R.id.items_per_load));
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
            request = "";
        }
        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, request, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        if (searchProgress != null) {
                            searchProgress.setVisibility(View.INVISIBLE);
                        }
                        if (callback != null) {
                            if (SearchQueryCallback.Flag.ERASE_LOADED.equals(flag)) {
                                totalEntries = DataParser.getTotalItemsAmount(response);
                            }
                            callback.onSearchResponse(response, flag);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                if (searchProgress != null) {
                    searchProgress.setVisibility(View.INVISIBLE);
                }
                if (callback != null) {
                    callback.onErrorResponse(error);
                }
            }
        });
        jsonRequest.setTag(REQUEST_TAG);

        switch (flag) {
            case APPEND_LOADED:
                requestQueue.add(jsonRequest);
                break;
            case ERASE_LOADED:
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestQueue.add(jsonRequest);
                    }
                }, SEARCH_DELAY);
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }
}

