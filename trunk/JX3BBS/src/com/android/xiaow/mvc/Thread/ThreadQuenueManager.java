/**=========================================================
 * 版权：联信永益 版权所有 (c) 2002 - 2003
 * 文件： com.xiaowei.Thread.ThreadQuenueManager
 * 所含类: ThreadQuenueManager
 * 修改记录：
 * 日期                  作者           内容
 * =========================================================
 * 2011-9-3     xiaow
 * =========================================================*/

/**
 * 
 */
package com.android.xiaow.mvc.Thread;

import java.util.HashMap;

import com.android.xiaow.mvc.command.ICommand;
import com.android.xiaow.mvc.command.IdentityCommand;

/**
 * @author xiaowei
 * 
 */
public class ThreadQuenueManager {
	private final HashMap<Integer, Class<? extends ICommand>> commands = new HashMap<Integer, Class<? extends ICommand>>();
	private static ThreadQuenueManager instance = new ThreadQuenueManager();

	private boolean initialized = false;
	private ThreadPool pool;
	private ThreadQuenue queue;
	public static final int COMMAND_ID_IDENTITY = 1;

	private ThreadQuenueManager() {
	}

	public static ThreadQuenueManager getInstance() {
		return instance;
	}

	public void initialize() {
		if (!initialized) {
			queue = new ThreadQuenue();
			pool = ThreadPool.getInstance();
			pool.start();
			initialized = true;
			commands.put(COMMAND_ID_IDENTITY, IdentityCommand.class);
		}
	}

	public boolean isThreadPollEnd() {
		return pool.isEnd();
	}



	/**
	 * This acts as the producer for the queue... Generally used by command
	 * executor to enqueue
	 * 
	 * @param cmd
	 */
	public void enqueue(ICommand cmd) {
		queue.enqueue(cmd);
		
	}



	public void clear() {
		queue.clear();
	}

	public void shutdown() {
		if (initialized) {
			queue.clear();
			pool.shutdown();
			initialized = false;
		}
	}

	
}
