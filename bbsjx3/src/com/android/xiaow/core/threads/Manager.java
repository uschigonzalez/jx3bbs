/**   
 * @Title: ThreadQueueManager.java 
 * @Package com.yhiker.playmate.core 
 * @Description: TODO
 * @author xiaowei   
 * @date 2012-7-23 ����4:07:17 
 * @version V1.0   
 */
package com.android.xiaow.core.threads;

import java.util.ArrayList;
import java.util.List;

import com.android.xiaow.core.common.IResponseListener;
import com.android.xiaow.core.common.Observer;
import com.android.xiaow.core.common.Request;
import com.android.xiaow.core.filter.IFilter;

/**
 * 
 * @author ���� xiaowei
 * @����ʱ�� 2012-7-23 ����4:07:17 ��˵��
 * 
 */

public class Manager {

    private static Manager instance = null;

    public List<IFilter> filters = new ArrayList<IFilter>();

    public synchronized static final Manager getIntance() {
        if (instance == null)
            instance = new Manager();
        return instance;
    }

    private Manager() {

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
        int len = filters.size();
        for (int i = 0; i < len; i++) {
            IFilter filter = filters.get(i);
            if (filter.doFilter(commandId, request, listener, observer)) {
                break;
            }
        }
    }

    public void addFilter(IFilter filter) {
        if (filters == null)
            filters = new ArrayList<IFilter>();
        filters.add(filter);
    }

    public void stop() {
        int len = filters.size();
        for (int i = 0; i < len; i++) {
            IFilter filter = filters.get(i);
            filter.doFilter(-1, null, null, null);
        }
    }

}
