package com.android.xiaow.jx3bbs.anim;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class Rotation3D extends Animation {
	private int vWidth;
	private int vHeight;
	private float RolateX;
	private float RolateY;
	private boolean isScale;
	private boolean XbigY;
	int count = 0;
	private Camera mCamera;

	public Rotation3D(int width, int height, int paddingLeft, int paddingRight,
			int paddingTop, int paddingBottom, float x, float y) {
		super();
		vWidth = width - paddingLeft - paddingRight;
		vHeight = height - paddingBottom - paddingTop;
		RolateX = vWidth / 2 - x;
		RolateY = vHeight / 2 - y;
		XbigY = Math.abs(RolateX) > Math.abs(RolateY) ? true : false;
		isScale = x > vWidth / 3 && x < vWidth * 2 / 3 && y > vHeight / 3
				&& y < vHeight * 2 / 3;

	}

	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		mCamera = new Camera();
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		super.applyTransformation(interpolatedTime, t);
		float s;
		if (interpolatedTime < 0.5) {
			count++;
			s = (float) Math.sqrt(Math.sqrt((0.5-interpolatedTime)*2*0.35f+0.65f));

		} else {
			count--;
			s = (float) Math.sqrt(Math.sqrt((interpolatedTime-0.5)*2*0.35f+0.65f));
		}
		if (isScale)
			BeginScale(t.getMatrix(), s);
		else
			BeginRolate(t.getMatrix(), (XbigY ? count : 0), (XbigY ? 0 : count));

	}

	private synchronized void BeginRolate(Matrix matrix, float rolateX,
			float rolateY) {
		// Bitmap bm = getImageBitmap();
		int scaleX = (int) (vWidth * 0.5f);
		int scaleY = (int) (vHeight * 0.5f);
		mCamera.save();
		mCamera.rotateX(RolateY > 0 ? rolateY : -rolateY);
		mCamera.rotateY(RolateX < 0 ? rolateX : -rolateX);
		mCamera.getMatrix(matrix);
		mCamera.restore();
		// 控制中心点
		if (RolateX > 0 && rolateX != 0) {
			matrix.preTranslate(-vWidth, -scaleY);
			matrix.postTranslate(vWidth, scaleY);
		} else if (RolateY > 0 && rolateY != 0) {
			matrix.preTranslate(-scaleX, -vHeight);
			matrix.postTranslate(scaleX, vHeight);
		} else if (RolateX < 0 && rolateX != 0) {
			matrix.preTranslate(-0, -scaleY);
			matrix.postTranslate(0, scaleY);
		} else if (RolateY < 0 && rolateY != 0) {
			matrix.preTranslate(-scaleX, -0);
			matrix.postTranslate(scaleX, 0);
		}
	}

	private synchronized void BeginScale(Matrix matrix, float scale) {
		int scaleX = (int) (vWidth * 0.5f);
		int scaleY = (int) (vHeight * 0.5f);
		matrix.postScale(scale, scale, scaleX, scaleY);

	}

}
