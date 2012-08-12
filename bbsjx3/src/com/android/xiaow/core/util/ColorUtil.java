package com.android.xiaow.core.util;

public class ColorUtil {
	/**********************************
	 * 
	 * color1��ǳɫ,color2����ɫ,ʵ�ֽ��� steps��ָ�ڶ��������н���, �����ҽ�����steps���ǿ� �����½���ʱsteps���Ǹ�
	 * 
	 **********************************/
	public static int[] generateTransitionalColor(int color1, int color2,
			int steps) {
		int[] color1RGB = retrieveRGBComponent(color1);
		int[] color2RGB = retrieveRGBComponent(color2);
		if (steps < 3 || color1RGB == null || color2RGB == null)
			return null;
		int[] colors = new int[steps];
		colors[0] = color1;
		colors[colors.length - 1] = color2;
		steps = steps - 2;
		int redDiff = color2RGB[0] - color1RGB[0];
		int greenDiff = color2RGB[1] - color1RGB[1];
		int blueDiff = color2RGB[2] - color1RGB[2];
		// from the second to the last second.
		for (int i = 1; i < colors.length - 1; i++) {
			colors[i] = generateFromRGBComponent(new int[] {
					color1RGB[0] + redDiff * i / steps,
					color1RGB[1] + greenDiff * i / steps,
					color1RGB[2] + blueDiff * i / steps });
		}
		return colors;
	}

	/**********************************
	 * 
	 * 
	 * ��ɫ,��ɫ����ɫ��ɫ���
	 * 
	 **********************************/
	public static int generateFromRGBComponent(int[] rgb) {
		if (rgb == null || rgb.length != 3 || rgb[0] < 0 || rgb[0] > 255
				|| rgb[1] < 0 || rgb[1] > 255 || rgb[2] < 0 || rgb[2] > 255)
			return 0xfffff;
		return rgb[0] << 16 | rgb[1] << 8 | rgb[2];
	}

	/**********************************
	 * 
	 * ����һ����ɫ,�������������: ��ɫ,��ɫ����ɫ
	 * 
	 **********************************/
	public static int[] retrieveRGBComponent(int color) {
		int[] rgb = new int[3];
		rgb[0] = (color & 0x00ff0000) >> 16;
		rgb[1] = (color & 0x0000ff00) >> 8;
		rgb[2] = (color & 0x000000ff);
		return rgb;
	}

	/**
	 * ��ȡ��ɫ����RGB���飬 Ϊ�˻�ȡ�����ݣ����ָ�CLDC1.0���ݣ�������Ŀ������һ��Float�� ���³�����
	 * 
	 * @param width
	 * @return
	 */
	public final static int[] getShadeColor(int color, int width) {
		int[] rgb;

		int shadeWidth = width;
		int nRgbData = shadeWidth * 4;

		rgb = new int[nRgbData];

		int alpha = -127;
		for (int i = 0; i < shadeWidth; i++) {
			alpha = -127 + i;
			// ��Ҫ�㷨�����
			int col = color | (128 - alpha << 24);
			rgb[i] = col;
			rgb[i + shadeWidth] = col;
			rgb[i + shadeWidth * 2] = col;
			rgb[i + shadeWidth * 3] = col;
		}
		return rgb;
	}
}
