package com.android.xiaow.core.util;


import android.content.Context;
import android.widget.Toast;

import com.android.xiaow.core.Controller;

/**
 * use for create message box,keep it only one Toast instance
 * 
 * @author Carl
 * 
 */
public class ToastUtil {
	private static Toast toast;
	private static Context context = Controller.getIntance();

	private ToastUtil() {
	}

	private static void checkToast() {
		if (toast == null) {
			/**
			 * toast = new Toast(context); toast.setView(Toast.makeText(context,
			 * null, 0).getView());
			 */
			toast = Toast.makeText(context, null, Toast.LENGTH_LONG);
		}
	}

	public static void show(String msg, int time) {
		checkToast();
		toast.setText(msg);
		toast.setDuration(time);
		toast.show();
	}

	public static void show(int msg, int time) {
		checkToast();
		toast.setText(msg);
		toast.setDuration(time);
		toast.show();
	}

	public static void show(String msg) {
		checkToast();
		toast.setText(msg);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();
	}

	public static void show(int msg) {
		checkToast();
		toast.setText(msg);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.show();
	}
}
