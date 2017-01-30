package com.kozorezov.githubsearch.Utils;

import com.kozorezov.githubsearch.Models.RepositoryModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

final public class DataParser {
    private static final String ITEMS = "items";
    private static final String NAME_TAG = "full_name";
    private static final String DESCRIPTION_TAG = "description";
    private static final String URL_TAG = "html_url";
    private static final String OWNERS_INFO_TAG = "owner";
    private static final String AVATAR_TAG = "avatar_url";
    private static final String ITEMS_COUNT_TAG = "total_count";

    public static List<RepositoryModel> parseToEntries(final JSONObject jsonObject) throws JSONException{

        final List<RepositoryModel> entriesList = new ArrayList<>();
        final JSONArray jsonArray = jsonObject.getJSONArray(ITEMS);
        for (int i=0; i<jsonArray.length(); i++) {
            final JSONObject jsonRepo = jsonArray.getJSONObject(i);
            entriesList.add(parseGitHubObject(jsonRepo));
        }
        return entriesList;
    }

    public static int getTotalItemsAmount(final JSONObject jsonObject){
        try {
            return jsonObject.getInt(ITEMS_COUNT_TAG);
        }catch (JSONException e){
            e.printStackTrace();
            return 0;
        }
    }

    private static RepositoryModel parseGitHubObject(final JSONObject jsonObject) throws JSONException {
        RepositoryModel.Builder builder = new RepositoryModel.Builder();
        builder.repoName(jsonObject.getString(NAME_TAG));
        builder.repoDescription(jsonObject.getString(DESCRIPTION_TAG));
        builder.repoUrl(jsonObject.getString(URL_TAG));
        builder.ownersAvatarUrl(jsonObject.getJSONObject(OWNERS_INFO_TAG).getString(AVATAR_TAG));
        return builder.build();
    }
}
