/**   
 * @Title: ResponseListenerImpl.java 
 * @Package com.yhiker.playmate.core.common 
 * @Description: TODO
 * @author xiaowei   
 * @date 2012-7-23 ����4:17:48 
 * @version V1.0   
 */
package com.android.xiaow.core.common;

import android.os.Handler;

/**
 * 
 * @author ���� xiaowei
 * @����ʱ�� 2012-7-23 ����4:17:48<br/>
 *       Function�� �����ת����{@link #mHandler}���ڵ��߳�ִ��
 * 
 */
public class DefaultResponseListener implements IResponseListener {

	Handler mHandler;
	IResponseListener listener;
	Response data;

	public DefaultResponseListener(Handler mHandler, IResponseListener listener) {
		super();
		this.mHandler = mHandler;
		this.listener = listener;
	}

	public Response getData() {
		return data;
	}

	public void setData(Response data) {
		this.data = data;
	}

	@Override
	public void onSuccess(Response response) {
		setData(response);
		if (listener == null) return;
		if (mHandler == null) {
			listener.onSuccess(response);
			return;
		}
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				listener.onSuccess(getData());

			}
		});
	}

	@Override
	public void onError(Response response) {
		setData(response);
		if (listener == null) return;
		if (mHandler == null) {
			listener.onError(response);
			return;
		}
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				listener.onError(getData());
			}
		});
	}

}
