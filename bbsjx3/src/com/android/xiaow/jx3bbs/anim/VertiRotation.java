package com.android.xiaow.jx3bbs.anim;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class VertiRotation extends Animation {

	// 开始角度
	private final float mFromDegrees;
	private final float mToDegrees;
	private final float mCenterX;
	private final float mCenterY;
	private final float mDepthZ;
	private final boolean mReverse;
	private Camera mCamera;

	

	/**
	 * 创建一个绕Y轴旋转的3D效果，旋转点为2D空间的中心点(centerX,centerY) 该效果由起始角度和终止角度决定
	 * 在旋转开始时，Z轴被定义一个旋转到屏幕里面的深度值，该值可固定，也可以随时间变化
	 * 
	 * @param fromDegrees
	 *            开始角度
	 * @param toDegrees
	 *            终止角度
	 * @param centerX
	 *            2D空间X坐标
	 * @param centerY
	 *            2D空间Y坐标
	 * @param depthZ
	 *            旋转到里面的深度
	 * @param reverse
	 *            如果可旋转则为true
	 */
	public VertiRotation(float mFromDegrees, float mToDegrees, float mCenterX,
			float mCenterY, float mDepthZ, boolean mReverse) {
		super();
		this.mFromDegrees = mFromDegrees;
		this.mToDegrees = mToDegrees;
		this.mCenterX = mCenterX;
		this.mCenterY = mCenterY;
		this.mDepthZ = mDepthZ;
		this.mReverse = mReverse;
	}
	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		// Camera类是用来实现绕Y轴旋转后透视投影的
		mCamera = new Camera();
	}

	// 生成Transformation
	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		final float fromDegrees = mFromDegrees;
		// 生成中间角度
		float degrees = fromDegrees
				+ ((mToDegrees - fromDegrees) * interpolatedTime);
		final float centerX = mCenterX;
		final float centerY = mCenterY;
		final Camera camera = mCamera;
		final Matrix matrix = t.getMatrix();
		camera.save();
		if (mReverse) {
			// camera.translate对矩阵进行平移变换操作
			camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime);
		} else {
			camera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime));
		}
		camera.rotateX(degrees);
		// 取得变换后的矩阵
		camera.getMatrix(matrix);
		// camera.restore进行旋转
		camera.restore();
		matrix.preTranslate(-centerX, -centerY);
		matrix.postTranslate(centerX, centerY);
	}
}
