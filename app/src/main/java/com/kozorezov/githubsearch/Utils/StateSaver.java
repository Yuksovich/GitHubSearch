package com.kozorezov.githubsearch.Utils;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.kozorezov.githubsearch.Models.RepositoryModel;

import java.util.ArrayList;

final public class StateSaver {
    private static final String SAVED_LIST_POSITION = "com.kozorezov.githubsearch.saved_list_position";
    private static final String SAVED_LIST_PADDING = "com.kozorezov.githubsearch.saved_list_padding";
    private static final String CURRENT_SEARCH_POSITION = "com.kozorezov.githubsearch.current_search_position";
    private static final String SEARCH_TOTAL_COUNT = "com.kozorezov.githubsearch.search_total_count";
    private static final String SEARCH_CURRENT_QUERY = "com.kozorezov.githubsearch.search_current_query";
    private static final String LOADED_ENTRIES = "com.kozorezov.githubsearch.loaded_entries";

    private StateSaver() {
        throw new UnsupportedOperationException();
    }

    public static void saveListVisiblePosition(@NonNull final Bundle outState, final int listVisiblePosition) {
        outState.putInt(SAVED_LIST_POSITION, listVisiblePosition);
    }

    public static int getSavedListVisiblePosition(@NonNull final Bundle savedInstanceState) {
        return savedInstanceState.getInt(SAVED_LIST_POSITION, 0);
    }

    public static void saveListPaddingTop(@NonNull final Bundle outState, final int listPaddingTop) {
        outState.putInt(SAVED_LIST_PADDING, listPaddingTop);
    }

    public static int getSavedListPaddingTop(@NonNull final Bundle savedInstanceState) {
        return savedInstanceState.getInt(SAVED_LIST_PADDING, 0);
    }

    public static void saveSearchCurrentPage(@NonNull final Bundle outState, final int currentPage) {
        outState.putInt(CURRENT_SEARCH_POSITION, currentPage);
    }

    public static int getSearchCurrentPage(@NonNull final Bundle savedInstanceState) {
        return savedInstanceState.getInt(CURRENT_SEARCH_POSITION, 1);
    }

    public static void saveSearchTotalCount(@NonNull final Bundle outState, final int totalCount) {
        outState.putInt(SEARCH_TOTAL_COUNT, totalCount);
    }

    public static int getSearchTotalCount(@NonNull final Bundle savedInstanceState) {
        return savedInstanceState.getInt(SEARCH_TOTAL_COUNT, 0);
    }

    public static void saveSearchCurrentQuery(@NonNull final Bundle outState, final String currentQuery) {
        outState.putString(SEARCH_CURRENT_QUERY, currentQuery);
    }

    public static String getSearchCurrentQuery(@NonNull final Bundle savedInstanceState) {
        return savedInstanceState.getString(SEARCH_CURRENT_QUERY, null);
    }

    public static void saveEntriesList(@NonNull final Bundle outState, final ArrayList<RepositoryModel> entries) {
        outState.putParcelableArrayList(LOADED_ENTRIES, entries);
    }

    public static ArrayList<RepositoryModel> loadEntriesList(@NonNull final Bundle savedInstanceState) {
        return savedInstanceState.getParcelableArrayList(LOADED_ENTRIES);
    }
}
