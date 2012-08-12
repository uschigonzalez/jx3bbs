/**   
 * @Title: Observer.java 
 * @Package com.hiker.onebyone.download 
 * @Description: TODO
 * @author xiaowei   
 * @date 2012-7-24 下午3:08:12 
 * @version V1.0   
 */
package com.android.xiaow.core.common;

/**
 * 
 * @author 作者 xiaowei
 * @创建时间 2012-7-24 下午3:08:12 类说明
 * 
 */
public interface Observer {
	void update(Observable observable, ObserverInfo info);
	// void setObserver(Observer observer);
	// void cancel();
}
