/**   
 * @Title: GPSService.java 
 * @Package com.hiker.onebyone.travel.service 
 * @Description: TODO
 * @author xiaowei   
 * @date 2012-8-2 ����2:36:11 
 * @version V1.0   
 */
package com.android.xiaow.core.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

/**
 * 
 * @author ���� xiaowei
 * @����ʱ�� 2012-8-2 ����2:36:11 ��˵��
 * 
 */
public class GPSService extends Service implements LocationListener {

	public static GPSService service;
	public long minTime = 10 * 1000;
	public float distance = 0;
	public Location location;
	/** ��providerΪnull��ʱ����Ϊfalse����ʾ��ǰ�޷���ȡgps��Ϣ */
	public boolean isStart;

	@Override
	public void onCreate() {
		super.onCreate();
		service = this;
		initLocation();
	}

	public boolean initLocation() {
		LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		isStart = true;
		// ���ҵ�������Ϣ
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE); // �߾���
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW); // �͹���
		String provider = manager.getBestProvider(criteria, true); // ��ȡGPS��Ϣ
		if (TextUtils.isEmpty(provider)) {
			isStart = false;
		}
		Log.d("GPS", "GPS------>: isStart:" + isStart + "," + provider);
		if (!isStart)
			return isStart;
		location = manager.getLastKnownLocation(provider);
		Log.d("GPS", "GPS------>: location:" + (location != null));
		manager.requestLocationUpdates(provider, minTime, distance, this);
		return isStart;
	}

	public Location getLastLocation() {
		Log.d("GPS", "GPS------->getLastLocation:"
				+ (location != null)
				+ ","
				+ (location != null ? "Longitude:" + location.getLongitude()
						+ ",Latitude" + location.getLatitude() : ""));
		return location;
	}

	public boolean isStartLocation() {
		return isStart;
	}

	public boolean restartLocation() {
		return initLocation();
	}

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d("GPS",
				"GPS------->" + (location == null) + "," + location != null ? "Longitude:"
						+ location.getLongitude()
						+ ",Latitude"
						+ location.getLatitude()
						: "");
		this.location = location;
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		Log.d("GPS", "GPS----------->onStatusChanged:" + provider);
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		Log.d("GPS", "GPS----------->onProviderEnabled:" + provider);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		Log.d("GPS", "GPS----------->onProviderDisabled:" + provider);

	}

}
