/**=========================================================
 * 版权：联信永益 版权所有 (c) 2002 - 2003
 * 文件： com.xiaowei.Thread.ThreadPool
 * 所含类: ThreadPool
 * 修改记录：
 * 日期                  作者           内容
 * =========================================================
 * 2011-9-3     xiaow
 * =========================================================*/

/**
 * 
 */
package com.android.xiaow.mvc.Thread;

import java.lang.reflect.Modifier;
import java.util.HashMap;

import com.android.xiaow.mvc.command.ICommand;
import com.android.xiaow.mvc.command.IdentityCommand;
import com.android.xiaow.mvc.common.IResponseListener;
import com.android.xiaow.mvc.common.Request;

/**
 * @author xiaowei
 * 
 */
public class ThreadPool {
	private final HashMap<Integer, Class<? extends ICommand>> commands = new HashMap<Integer, Class<? extends ICommand>>();
	public static final int COMMAND_ID_IDENTITY = 1;

	static private ThreadPool instance = new ThreadPool();

	private boolean started = false;

	private boolean end = false;
	private CommandThread[] threads;

	ThreadQuenue mQuenue;

	public static ThreadPool getInstance() {

		return instance;
	}

	private ThreadPool() {
		mQuenue = new ThreadQuenue();
		commands.put(COMMAND_ID_IDENTITY, IdentityCommand.class);

	}

	public void start() {
		if (!started) {
			System.out.println("ThreadPool ::: started");
			int threadCount = ThreadConfig.MAX_THREADS_COUNT;
			threads = new CommandThread[threadCount];
			for (int threadId = 0; threadId < threadCount; threadId++) {
				threads[threadId] = new CommandThread(threadId);
				threads[threadId].start();
			}
			started = true;
			boolean flag = true;
			while (flag) {
				flag = false;
				for (int threadId = 0; threadId < threadCount; threadId++) {
					if (threads[threadId].isRunning()) {
						flag = true;
					}
				}
			}
			end = true;
		}
	}

	public void restart(){
		if(end){
			started=false;
			start();
			return;
		}
		shutdown();
		start();
			
	}
	public void shutdown() {
		if (started) {
			for (CommandThread thread : threads) {
				thread.stop();
			}
			threads = null;
			started = false;
		}
	}

	public boolean isEnd() {
		return end;
	}

	private ICommand getCommand(int commandId) {
		ICommand rv = null;

		if (commands.containsKey(commandId)) {
			Class<? extends ICommand> cmd = commands.get(commandId);
			if (cmd != null) {
				int modifiers = cmd.getModifiers();
				if ((modifiers & Modifier.ABSTRACT) == 0
						&& (modifiers & Modifier.INTERFACE) == 0) {
					try {
						rv = cmd.newInstance();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		return rv;
	}

	/**
	 * This acts as the consumer for the queue...
	 * 
	 * @return
	 */
	public ICommand getNextCommand() {
		ICommand cmd = mQuenue.getNextCommand();
		return cmd;
	}

	public void registerCommand(int commandId, Class<? extends ICommand> command) {
		if (command != null) {
			commands.put(commandId, command);
		}
	}

	public void unregisterCommand(int commandId) {
		commands.remove(commandId);
	}

	/**
	 * 
	 * enqueue:入列 Command:命令
	 */
	public void enqueueCommand(int commandId, Request request,
			IResponseListener listener) {
		final ICommand cmd = getCommand(commandId);
		if (cmd != null) {
			cmd.setRequest(request);
			cmd.setResponseListener(listener);
			mQuenue.enqueue(cmd);
			rejectThread();
		}
	}

	/**
	 * 检测是否有活动的线程，如果活动的线程少于预定线程，则启动活动线程
	 */

	protected void rejectThread() {
		int len = threads.length;
		int active = 0;
		int deadth_index = -1;
		for (int i = 0; i < len; i++) {
			if (threads[i].isRunning())
				active++;
			else
				deadth_index = i;
		}
		if (active < ThreadConfig.MAX_THREADS_COUNT) {
			threads[deadth_index] = new CommandThread(deadth_index);
			threads[deadth_index].start();
		}
	}
}
