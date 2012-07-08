package com.android.xiaow.jx3bbs.utils;

import com.android.xiaow.mvc.controller.Controller;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtil {
	public static  boolean checkWifi() {
		boolean isWifiConnect = true;
		ConnectivityManager cm = (ConnectivityManager) Controller.getInstance()
				.getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// check the networkInfos numbers
		NetworkInfo[] networkInfos = cm.getAllNetworkInfo();
		for (int i = 0; i < networkInfos.length; i++) {
			if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED) {
				if (networkInfos[i].getType() == ConnectivityManager.TYPE_MOBILE) {
					isWifiConnect = false;
				}
				if (networkInfos[i].getType() == ConnectivityManager.TYPE_WIFI) {
					isWifiConnect = true;
				}
			}
		}
		return isWifiConnect;
	}
}
