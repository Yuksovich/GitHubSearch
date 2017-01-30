package com.kozorezov.githubsearch.Utils;

import android.graphics.Bitmap;
import android.util.LruCache;
import com.android.volley.toolbox.ImageLoader;

public enum ImageCache {

    INSTANCE;

    private final static int CACHE_SIZE = 20;
    private final static ImageLoader.ImageCache imageCache = new ImageLoader.ImageCache() {
        private final LruCache<String, Bitmap> cache = new LruCache<>(CACHE_SIZE);

        @Override
        public Bitmap getBitmap(final String url) {
            return cache.get(url);
        }

        @Override
        public void putBitmap(final String url, final Bitmap bitmap) {
            cache.put(url, bitmap);
        }
    };

    public ImageLoader.ImageCache getImageCache() {
        return imageCache;
    }


}
