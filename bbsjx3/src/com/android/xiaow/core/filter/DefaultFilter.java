/**   
 * @Title: DefualtFilter.java 
 * @Package com.yhiker.playmate.core.filter 
 * @Description: TODO
 * @author xiaowei   
 * @date 2012-8-1 ����4:31:52 
 * @version V1.0   
 */
package com.android.xiaow.core.filter;

import com.android.xiaow.core.cmds.BaseCommand;
import com.android.xiaow.core.cmds.ICommand;
import com.android.xiaow.core.common.IResponseListener;
import com.android.xiaow.core.common.Observer;
import com.android.xiaow.core.common.Request;
import com.android.xiaow.core.threads.BaseCmdId;
import com.android.xiaow.core.threads.ThreadPool;
import com.android.xiaow.core.threads.ThreadQueue;

/**
 * 
 * @author ���� xiaowei
 * @����ʱ�� 2012-8-1 ����4:31:52 ��˵��
 * 
 */
public class DefaultFilter implements IFilter {
	BaseCmdId cmdIds = new BaseCmdId();
	ThreadPool pool;
	ThreadQueue queue;

	/**
	 * @param capacity
	 *            ����С��1�����С��1����Ĭ���޸�Ϊ1 ����߳���
	 * @author xiaowei
	 * @date 2012-8-1 ����4:47:00
	 */
	public DefaultFilter(int capacity) {
		super();
		capacity = capacity < 1 ? 1 : capacity;
		queue = new ThreadQueue();
		pool = new ThreadPool(capacity, queue);
	}

	public boolean registerCommandId(int commandId, String className) {
		return cmdIds.registerCommandId(commandId, className);
	}

	@Override
	public boolean doFilter(int commandId, Request request, IResponseListener listener,
			Observer observer) {
		String className = cmdIds.getName(commandId);
		if (className == null) return false;
		try {
			Class<?> cls = Class.forName(className);
			Object obj = cls.newInstance();
			if (obj instanceof ICommand) {
				if (obj instanceof BaseCommand) {
					BaseCommand cmd = (BaseCommand) obj;
					cmd.setListener(listener);
					cmd.setRequest(request);
					cmd.registerObserver(observer);
				}
				ICommand cmd = (ICommand) obj;
				queue.equeue(cmd);
			} else {
				return false;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		return true;
	}
}
