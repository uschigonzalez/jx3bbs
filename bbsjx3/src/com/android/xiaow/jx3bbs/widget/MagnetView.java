package com.android.xiaow.jx3bbs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.android.xiaow.jx3bbs.anim.Rotation3D;

public class MagnetView extends RelativeLayout {

	public MagnetView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MagnetView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MagnetView(Context context) {
		super(context);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(ev.getAction()==MotionEvent.ACTION_DOWN){
			Rotation3D rotation3d = new Rotation3D(getWidth(),
					getHeight(), getPaddingLeft(), getPaddingRight(),
					getPaddingTop(), getPaddingBottom(), ev.getX(),
					ev.getY());
			rotation3d.setDuration(500);
			startAnimation(rotation3d);
		}
		return super.dispatchTouchEvent(ev);
	}

	
}
