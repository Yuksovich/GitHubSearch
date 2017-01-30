package com.kozorezov.githubsearch;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.kozorezov.githubsearch.Models.RepositoryModel;
import com.kozorezov.githubsearch.Services.SearchQueryCallback;
import com.kozorezov.githubsearch.Utils.DataParser;
import com.kozorezov.githubsearch.Utils.ImageCache;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

final class ListAdapter extends ArrayAdapter<RepositoryModel> implements SearchQueryCallback {
    private final static String AUTH_ERROR = "com.android.volley.AuthFailureError";
    private final ImageLoader imageLoader;
    private final Activity context;

    ListAdapter(@NonNull final Activity context,
                final int resource, @NonNull
                final List<RepositoryModel> entries) {
        super(context, resource, entries);
        this.context = context;
        imageLoader = new ImageLoader(Volley.newRequestQueue(context), ImageCache.INSTANCE.getImageCache());
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final RepositoryModel entry = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            final LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.entry_in_list, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.repoName = (TextView) convertView.findViewById(R.id.list_entry_repo_name);
            viewHolder.repoDescription = (TextView) convertView.findViewById(R.id.list_entry_repo_description);
            viewHolder.repoUrl = (TextView) convertView.findViewById(R.id.list_entry_url);
            viewHolder.ownersAvatar = (NetworkImageView) convertView.findViewById(R.id.list_entry_avatar);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.repoName.setText(entry.getRepoName());
        viewHolder.repoDescription.setText(entry.getRepoDescription());
        viewHolder.repoUrl.setText(entry.getRepoUrl());
        viewHolder.ownersAvatar.setImageUrl(entry.getOwnersAvatarUrl(), imageLoader);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(entry.getRepoUrl()));
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public void onSearchResponse(final JSONObject jsonObject, final Flag flag) {
        if (Flag.ERASE_LOADED.equals(flag)) {
            setListHeaderText(DataParser.getTotalItemsAmount(jsonObject));
            super.clear();
            notifyDataSetChanged();
        }
        if (jsonObject == null) {
            return;
        }
        try {
            addEntries(DataParser.parseToEntries(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(final VolleyError error) {
        final TextView textView = (TextView) context.findViewById(R.id.list_footer_text);
        context.findViewById(R.id.list_load_progress).setVisibility(View.INVISIBLE);
        if (AUTH_ERROR.equals(error.toString())) {
            textView.setText(context.getString(R.string.limit_request_reached));
        } else {
            textView.setText(error.toString());
        }
    }

    private void addEntries(final List<RepositoryModel> additionalEntries) {
        super.addAll(additionalEntries);
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView repoName;
        TextView repoDescription;
        TextView repoUrl;
        NetworkImageView ownersAvatar;
    }

    private void setListHeaderText(int totalFound) {
        ((TextView) context.findViewById(R.id.list_header_text))
                .setText(context.getResources().getQuantityString(
                        R.plurals.entries_found_count,
                        totalFound,
                        totalFound));
    }
}
