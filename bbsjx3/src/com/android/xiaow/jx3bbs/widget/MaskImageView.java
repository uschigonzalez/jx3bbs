package com.android.xiaow.jx3bbs.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MaskImageView extends ImageView {
	Rect rect;
	Path clip;
	Paint paint;

	public MaskImageView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public MaskImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	public MaskImageView(Context context) {
		super(context);

	}

	

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {

		canvas.save();
		canvas.drawColor(Color.TRANSPARENT);
		rect = new Rect();
		getFocusedRect(rect);
		clip = new Path();
		clip.addCircle(rect.centerX(), rect.centerY(),
				Math.min(rect.width() / 2, rect.height() / 2), Direction.CW);
		canvas.clipPath(clip);
			super.onDraw(canvas);
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(0xfff3fafb);
		paint.setStrokeWidth(2);
		paint.setStyle(Style.STROKE);
		canvas.drawPath(clip, paint);

	}

}
