/**   
 * @Title: IFilter.java 
 * @Package com.yhiker.playmate.core.threads 
 * @Description: TODO
 * @author xiaowei   
 * @date 2012-8-1 ����3:27:08 
 * @version V1.0   
 */
package com.android.xiaow.core.filter;

import com.android.xiaow.core.common.IResponseListener;
import com.android.xiaow.core.common.Observer;
import com.android.xiaow.core.common.Request;

/**
 * 
 * @author ���� xiaowei
 * @����ʱ�� 2012-8-1 ����3:27:08 ��˵��
 * 
 */
public interface IFilter {

	public boolean doFilter(int commandId, Request request, IResponseListener listener,
			Observer observer);

//	public void cancel();

}
