package com.kozorezov.githubsearch.Services;

import com.android.volley.VolleyError;
import org.json.JSONObject;

public interface SearchQueryCallback {
    void onSearchResponse(JSONObject jsonObject, Flag flag);
    void onErrorResponse(VolleyError error);

    enum Flag{
        ERASE_LOADED,
        APPEND_LOADED
    }
}
