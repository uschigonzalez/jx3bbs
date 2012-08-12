/**   
 * @Title: IResponseListener.java 
 * @Package com.yhiker.playmate.core.common 
 * @Description: TODO
 * @author xiaowei   
 * @date 2012-7-23 ����4:11:48 
 * @version V1.0   
 */
package com.android.xiaow.core.common;

/**
 * 
 * @author ���� xiaowei
 * @����ʱ�� 2012-7-23 ����4:11:48 ��˵��
 * 
 */
public interface IResponseListener {
	public void onSuccess(Response response);

	public void onError(Response response);
}
