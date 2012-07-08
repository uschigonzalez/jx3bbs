/**   
 * @Title: ImageThread.java
 * @Package com.android.xiaow.jx3bbs.command
 * @Description: 图片下载线程
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-7-7 下午7:36:56
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs.command;

import java.util.concurrent.LinkedBlockingQueue;

import com.android.xiaow.mvc.command.ICommand;
import com.android.xiaow.mvc.common.IResponseListener;
import com.android.xiaow.mvc.common.Request;

/**
 * @ClassName: ImageThread
 * @Description: 图片下载线程管理
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-7-7 下午7:36:56
 * 
 */
public class ImageThread {
	private LinkedBlockingQueue<ICommand> srcQueue = new LinkedBlockingQueue<ICommand>();
	static ImageThread thread = new ImageThread();
	public static final int MAX_IMAGE_THREAD = 2;
	RootThread[] threads = new RootThread[MAX_IMAGE_THREAD];

	private ImageThread() {
	}

	public static ImageThread getInstance() {
		return thread;
	}

	public static void enqueueCommand(Request request,
			IResponseListener listener) {
		ICommand command = new ImageDownLoad();
		command.setRequest(request);
		command.setResponseListener(listener);
		ImageThread.getInstance().srcQueue.add(command);
		ImageThread.getInstance().start();
	}

	public void start() {
		for (int i = 0; i < MAX_IMAGE_THREAD; i++) {
			if (threads[i] == null||!threads[i].isRun) {
				threads[i] = new RootThread();
				threads[i].start();
			}
		}
	}

	class RootThread extends Thread {
		public boolean isRun = false;

		@Override
		public void run() {
			isRun = true;
			super.run();
			ICommand cmd = null;
			while ((cmd = srcQueue.poll()) != null) {
				cmd.execute();
			}
			isRun = false;
		}

	}

}
