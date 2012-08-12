package com.android.xiaow.core.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.xiaow.core.Controller;

public class NetUtil {
	
	public static boolean checkWifi() {
		boolean isWifiConnect = true;

		ConnectivityManager cm = (ConnectivityManager) Controller.getIntance()
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

	public static boolean checkNet() {
		/* ���ϵͳ�����ȡ�ֻ����ӹ������ */
		ConnectivityManager connectivity = (ConnectivityManager) Controller
				.getIntance().getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null && info.isConnected()) {
				// �жϵ�ǰ�����Ƿ�����
				if (info.getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

}
