package com.android.xiaow.jx3bbs;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

import com.android.xiaow.core.image.ImageCallBack;

public class UrldrawableObserver implements ImageCallBack {
    View view;
    UrlDrawable urlDrawable;

    public UrldrawableObserver(View view, UrlDrawable urlDrawable) {
        super();
        this.view = view;
        this.urlDrawable = urlDrawable;
    }


    @SuppressWarnings("deprecation")
    @Override
    public void callback(Bitmap bitmap) {
        urlDrawable.drawable = new BitmapDrawable(bitmap);
        view.postInvalidate();
    }

}
