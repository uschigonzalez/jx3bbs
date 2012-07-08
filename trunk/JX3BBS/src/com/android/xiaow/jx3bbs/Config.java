package com.android.xiaow.jx3bbs;

import com.android.xiaow.mvc.controller.Controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Config {
	public static final int TYPE_ONLY_IMAGE = 0;
	public static final int TYPE_ALL = 1;
	public static final int TYPE_ALL_IN_WIFI = 2;
	public static final int TYPE_OFF_ALL = 3;
	public static final int TYPE_ONLY_GRAVATAR = 4;

	public static boolean ONLY_IMAGE = false;
	public static boolean ALL = false;
	public static boolean ALL_IN_WIFI = true;
	public static boolean OFF_ALL = false;
	public static boolean ONLY_GRAVATAR = false;

	public static void setType(boolean flag, int type) {
		if (type == TYPE_ONLY_IMAGE) {
			ONLY_IMAGE = true;
			ALL = false;
			ALL_IN_WIFI = false;
			ONLY_GRAVATAR = false;
			OFF_ALL = false;
		} else if (type == TYPE_ALL) {
			ONLY_IMAGE = false;
			ALL = true;
			ALL_IN_WIFI = false;
			ONLY_GRAVATAR = false;
			OFF_ALL = false;
		} else if (type == TYPE_ALL_IN_WIFI) {
			ONLY_IMAGE = false;
			ALL = false;
			ALL_IN_WIFI = true;
			ONLY_GRAVATAR = false;
			OFF_ALL = false;
		} else if (type == TYPE_ONLY_GRAVATAR) {
			ONLY_IMAGE = false;
			ALL = false;
			ALL_IN_WIFI = false;
			ONLY_GRAVATAR = true;
			OFF_ALL = false;
		} else if (type == TYPE_OFF_ALL) {
			ONLY_IMAGE = false;
			ALL = false;
			ALL_IN_WIFI = false;
			ONLY_GRAVATAR = false;
			OFF_ALL = true;
		}

		SharedPreferences sp = Controller.getInstance().getApplicationContext()
				.getSharedPreferences("Settings", Context.MODE_WORLD_WRITEABLE);
		Editor editor=sp.edit();
		editor.putBoolean("ONLY_GRAVATAR", ONLY_GRAVATAR);
		editor.putBoolean("ALL_IN_WIFI", ALL_IN_WIFI);
		editor.putBoolean("ONLY_IMAGE", ONLY_IMAGE);
		editor.putBoolean("ALL", ALL);
		editor.putBoolean("OFF_ALL", OFF_ALL);
		editor.commit();
	}
}
