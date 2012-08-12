/**   
 * @Title: Controller.java 
 * @Package com.yhiker.playmate.core.controller 
 * @Description: TODO
 * @author xiaowei   
 * @date 2012-8-1 ����4:26:11 
 * @version V1.0   
 */
package com.android.xiaow.core;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.android.xiaow.core.common.DefaultResponseListener;
import com.android.xiaow.core.common.IResponseListener;
import com.android.xiaow.core.common.Observer;
import com.android.xiaow.core.common.Request;
import com.android.xiaow.core.threads.Manager;

/**
 * 
 * @author ���� xiaowei
 * @����ʱ�� 2012-8-1 ����4:26:11 ��˵��
 * 
 */
public abstract class Controller extends Application {

    public Handler handler;
    public Timer timer;
    private static Controller instance;

    public static final Controller getIntance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        handler = new Handler();
        startTimer();
    }

    /**
     * @Title: startTimer
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    private void startTimer() {
        timer = new Timer();
        appStart();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (!isActived()) {
                    appStop();
                    timer.cancel();
                    timer = null;
                }
            }
        }, 0, 1000);
    }

    public abstract void appStart();

    public abstract void appStop();

    public boolean isActived() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        boolean isActived = false;
        List<RunningTaskInfo> mRunServices = am.getRunningTasks(30);
        for (RunningTaskInfo info : mRunServices) {
            if (info.topActivity.getPackageName().equals(getPackageName())) {
                isActived = true;
                break;
            }
        }
        return isActived;
    }

    public void registerCommand(int commandId) {
        registerCommand(commandId, null);
    }

    public void registerCommand(int commandId, Request request) {
        registerCommand(commandId, request, null);
    }

    public void registerCommand(int commandId, Request request, IResponseListener listener) {
        registerCommand(commandId, request, listener, null);
    }

    public void registerCommand(int commandId, Request request, IResponseListener listener,
            Observer observer) {
        Manager.getIntance().registerCommand(commandId, request,
                listener == null ? null : new DefaultResponseListener(handler, listener), observer);
    }
}
