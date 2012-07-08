package com.android.xiaow.jx3bbs.ui.component;

import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ImageViewObserver extends DataSetObserver {

	public ImageView mImageView;
	public Drawable mDrawable;
	public boolean isBackgroud;
	public String filePath;
	BitmapFactory.Options options;
	Callback callback;

	public ImageViewObserver() {
		super();
	}

	public ImageViewObserver(ImageView miImageView, boolean isBackgroud) {
		super();
		this.mImageView = miImageView;
		this.isBackgroud = isBackgroud;
		options = new BitmapFactory.Options();

	}

	public void init(String filePath) {
		if (TextUtils.isEmpty(filePath))
			return;
		this.filePath = filePath;
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onChanged() {
		super.onChanged();
		if (mImageView == null)
			return;
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
		if (mDrawable != null)
			mDrawable.setCallback(null);
		if (mDrawable != null) {
			if (isBackgroud)
				mImageView.setBackgroundDrawable(mDrawable);
			else
				mImageView.setImageDrawable(mDrawable);
		}
		if (callback != null)
			callback.loadFinish();

	}

	public LinearLayout getParent(View view) {
		if (view.getParent() != null
				&& view.getParent() instanceof LinearLayout) {
			return getParent((View) view.getParent());
		}
		return (LinearLayout) view;
	}

	int computeSampleSize(BitmapFactory.Options options, int target) {
		int w = options.outWidth;
		int h = options.outHeight;
		int candidateW = w / target;
		int candidateH = h / target;
		int candidate = Math.max(candidateW, candidateH);
		if (candidate == 0)
			return 1;
		if (candidate > 1) {
			if ((w > target) && (w / candidate) < target)
				candidate -= 1;
		}
		if (candidate > 1) {
			if ((h > target) && (h / candidate) < target)
				candidate -= 1;
		}
		return candidate;
	}

	@Override
	public void onInvalidated() {
		super.onInvalidated();
	}

	public static interface Callback {
		abstract void loadFinish();
	}
}
