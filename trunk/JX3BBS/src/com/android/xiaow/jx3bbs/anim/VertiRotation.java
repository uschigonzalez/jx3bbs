package com.android.xiaow.jx3bbs.anim;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class VertiRotation extends Animation {

	// ��ʼ�Ƕ�
	private final float mFromDegrees;
	private final float mToDegrees;
	private final float mCenterX;
	private final float mCenterY;
	private final float mDepthZ;
	private final boolean mReverse;
	private Camera mCamera;

	

	/**
	 * ����һ����Y����ת��3DЧ������ת��Ϊ2D�ռ�����ĵ�(centerX,centerY) ��Ч������ʼ�ǶȺ���ֹ�ǶȾ���
	 * ����ת��ʼʱ��Z�ᱻ����һ����ת����Ļ��������ֵ����ֵ�ɹ̶���Ҳ������ʱ��仯
	 * 
	 * @param fromDegrees
	 *            ��ʼ�Ƕ�
	 * @param toDegrees
	 *            ��ֹ�Ƕ�
	 * @param centerX
	 *            2D�ռ�X����
	 * @param centerY
	 *            2D�ռ�Y����
	 * @param depthZ
	 *            ��ת����������
	 * @param reverse
	 *            �������ת��Ϊtrue
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
		// Camera��������ʵ����Y����ת��͸��ͶӰ��
		mCamera = new Camera();
	}

	// ����Transformation
	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		final float fromDegrees = mFromDegrees;
		// �����м�Ƕ�
		float degrees = fromDegrees
				+ ((mToDegrees - fromDegrees) * interpolatedTime);
		final float centerX = mCenterX;
		final float centerY = mCenterY;
		final Camera camera = mCamera;
		final Matrix matrix = t.getMatrix();
		camera.save();
		if (mReverse) {
			// camera.translate�Ծ������ƽ�Ʊ任����
			camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime);
		} else {
			camera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime));
		}
		camera.rotateX(degrees);
		// ȡ�ñ任��ľ���
		camera.getMatrix(matrix);
		// camera.restore������ת
		camera.restore();
		matrix.preTranslate(-centerX, -centerY);
		matrix.postTranslate(centerX, centerY);
	}
}
