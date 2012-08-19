
package com.android.xiaow.core.service;

import android.app.Activity;
import android.content.Intent;

public class LogServiceUtil {
    private static   Intent intent = new Intent("com.android.xiaow.core.service.logservice");
    
    public static  void StartService(Activity activity){
        activity.startService(intent);
    }
    public static void stopService(Activity activity){
        activity.stopService(intent);
    }
}
