package com.example.bgwcompass.dataClasses;

import android.graphics.Bitmap;

public class savedAvatar {
    private final Bitmap bimage;
    private final String url;

    public savedAvatar(Bitmap bimage, String url) {
        this.bimage = bimage;
        this.url = url;
    }

    public Bitmap getBimage() {
        return bimage;
    }

    public String getUrl() {
        return url;
    }
}
