package com.android.xiaow.jx3bbs;

import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

@SuppressWarnings("deprecation")
public class UrlDrawable extends BitmapDrawable {
	public Drawable drawable;
	{
		drawable = new BitmapDrawable();
		drawable.setBounds(0, 0, 75, 75);
	}

	@Override
	public void draw(Canvas canvas) {
		if (drawable != null)
			drawable.draw(canvas);
	}

}
