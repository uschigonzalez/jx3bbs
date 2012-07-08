package com.android.xiaow.jx3bbs.ui.component;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.View;

public class UrldrawableObserver extends ImageViewObserver {
	View view;
	UrlDrawable urlDrawable;

	public UrldrawableObserver(View view, UrlDrawable urlDrawable) {
		super();
		this.view = view;
		this.urlDrawable = urlDrawable;
		options = new BitmapFactory.Options();
	}

	@Override
	public void setCallback(Callback callback) {
		super.setCallback(callback);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onChanged() {
		if (!TextUtils.isEmpty(filePath)) {
			final int IMAGE_MAX_SIZE = 500;
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, options);
			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			if (options.outHeight > IMAGE_MAX_SIZE
					|| options.outWidth > IMAGE_MAX_SIZE) {
				scale = (int) Math.pow(
						2,
						(int) Math.round(Math.log(IMAGE_MAX_SIZE
								/ (double) Math.max(options.outHeight,
										options.outWidth))
								/ Math.log(0.5)));
			}
			options = new BitmapFactory.Options();
			options.inSampleSize = scale;
			Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
			mDrawable = new BitmapDrawable(bitmap);
			SyncLoadImage.getIntance().equen(filePath, bitmap);
		}
		if (urlDrawable != null && mDrawable != null) {
			urlDrawable.drawable = mDrawable;
			urlDrawable.drawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
			view.postInvalidate();
		}
	}

	@Override
	public void onInvalidated() {
		super.onInvalidated();
	}

}
