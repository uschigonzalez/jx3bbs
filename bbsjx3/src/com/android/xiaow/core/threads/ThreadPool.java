/**   
 * @Title: ThreadPool.java 
 * @Package com.yhiker.playmate.core 
 * @Description: TODO
 * @author xiaowei   
 * @date 2012-7-23 ����4:07:26 
 * @version V1.0   
 */
package com.android.xiaow.core.threads;

/**
 * 
 * @author ���� xiaowei
 * @����ʱ�� 2012-7-23 ����4:07:26 ��˵��
 * 
 */
public class ThreadPool {
	BaseThread[] threads;
	ThreadQueue queue;
	
	/**
	 * 
	 * <p>Title: </p> 
	 * <p>Description: </p> 
	 * @param capacity �����������������߳���
	 * @param queue ��ִ�ж���
	 * @author xiaowei
	 * @date 2012-8-1 ����3:41:45
	 */
	public ThreadPool(int capacity, ThreadQueue queue) {
		super();
		threads = new BaseThread[capacity];
		this.queue = queue;
		initPool();
	}

	private void initPool() {
		for (int i = 0; i < threads.length; i++) {
			BaseThread thread = threads[i];
			if (thread == null) {
				thread = new BaseThread(queue);
			}
			thread.start();
		}
	}

}
