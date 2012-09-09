/**   
 * @Title: Controller.java 
 * @Package com.yhiker.playmate.core.controller 
 * @Description: TODO
 * @author xiaowei   
 * @date 2012-8-1 ����4:26:11 
 * @version V1.0   
 */
package com.android.xiaow.core;

import java.util.Timer;

import android.app.Application;
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
