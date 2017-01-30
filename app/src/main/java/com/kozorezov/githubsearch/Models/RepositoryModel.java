package com.kozorezov.githubsearch.Models;

import android.os.Parcel;
import android.os.Parcelable;

public final class RepositoryModel implements Parcelable {
    private final String repoName;
    private final String repoDescription;
    private final String repoUrl;
    private final String ownersAvatarUrl;

    private RepositoryModel(final String repoName,
                            final String repoDescription,
                            final String repoUrl,
                            final String ownersAvatarUrl)

    {
        this.repoName = repoName;
        this.repoDescription = repoDescription;
        this.repoUrl = repoUrl;
        this.ownersAvatarUrl = ownersAvatarUrl;
    }

    public String getRepoName() {
        return repoName;
    }

    public String getRepoDescription() {
        return repoDescription;
    }

    public String getOwnersAvatarUrl() {
        return ownersAvatarUrl;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    private static final int NUMBER_OF_FIELDS = 4;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {

        final String[] fields = {
                repoName,
                repoDescription,
                repoUrl,
                ownersAvatarUrl};
        dest.writeStringArray(fields);
    }

    public static final Parcelable.Creator<RepositoryModel> CREATOR = new Parcelable.Creator<RepositoryModel>() {
        @Override
        public RepositoryModel createFromParcel(final Parcel source) {
            final String[] fields = new String[NUMBER_OF_FIELDS];
            source.readStringArray(fields);
            return new RepositoryModel.Builder()
                    .repoName(fields[0])
                    .repoDescription(fields[1])
                    .repoUrl(fields[2])
                    .ownersAvatarUrl(fields[3])
                    .build();
        }

        @Override
        public RepositoryModel[] newArray(final int size) {
            return new RepositoryModel[size];
        }
    };

    public final static class Builder {
        private static final String EMPTY_STRING = "";
        private String repoName;
        private String repoDescription;
        private String repoUrl;
        private String ownersAvatarUrl;

        public Builder repoName(final String repoName) {
            this.repoName = repoName;
            return this;
        }

        public Builder repoDescription(final String repoDescription) {
            if (repoDescription == null) {
                this.repoDescription = EMPTY_STRING;
            } else {
                this.repoDescription = repoDescription;
            }
            return this;
        }

        public Builder repoUrl(final String repoUrl) {
            this.repoUrl = repoUrl;
            return this;
        }

        public Builder ownersAvatarUrl(final String ownersAvatarUrl) {
            this.ownersAvatarUrl = ownersAvatarUrl;
            return this;
        }

        public RepositoryModel build() {
            return new RepositoryModel(repoName, repoDescription, repoUrl, ownersAvatarUrl);
        }
    }
}
