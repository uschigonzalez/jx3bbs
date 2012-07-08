/**=========================================================
 * 版权：联信永益 版权所有 (c) 2002 - 2003
 * 文件： com.xiaowei.Thread.ThreadQuenue
 * 所含类: ThreadQuenue
 * 修改记录：
 * 日期                  作者           内容
 * =========================================================
 * 2011-9-3     xiaow
 * =========================================================*/

/**
 * 
 */
package com.android.xiaow.mvc.Thread;

import java.util.concurrent.LinkedBlockingQueue;

import com.android.xiaow.mvc.command.ICommand;

/**
 * @author xiaowei
 * 
 */
public class ThreadQuenue {

	private LinkedBlockingQueue<ICommand> srcQueue = new LinkedBlockingQueue<ICommand>();

	public ThreadQuenue() {
	}

	public void enqueue(ICommand cmd) {
		srcQueue.add(cmd);
	}

	public synchronized ICommand getNextCommand() {
		ICommand cmd = null;
		// try {
		// cmd = srcQueue.take();
		// } catch (InterruptedException e) {
		//
		// e.printStackTrace();
		// }
		cmd = srcQueue.poll();
		return cmd;
	}

	public synchronized void clear() {
		srcQueue.clear();
	}
}
