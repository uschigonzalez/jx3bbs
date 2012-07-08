package com.android.xiaow.mvc.command;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.android.xiaow.mvc.common.IResponseListener;
import com.android.xiaow.mvc.common.Request;

public final class CommandExecutor {
	private final HashMap<Integer, Class<? extends ICommand>> commands = new HashMap<Integer, Class<? extends ICommand>>();
	private static ThreadPoolExecutor poolExecutor = null;

	private static final int MAX_THREAD_COUNT = 10;

	private static LinkedBlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>();

	private static final CommandExecutor instance = new CommandExecutor();
	private boolean initialized = false;


	private CommandExecutor() {
	}

	public static CommandExecutor getInstance() {
		return instance;
	}

	public void ensureInitialized() {
		if (!initialized) {
			initialized = true;
			poolExecutor = new ThreadPoolExecutor(MAX_THREAD_COUNT,
					MAX_THREAD_COUNT, 3, TimeUnit.SECONDS, taskQueue);
		}
	}

	public void terminateAll() {
		// TODO: Terminate or mark all commands as terminated
	}

	/**
	 * 
	 * enqueue:»Î¡– Command:√¸¡Ó
	 */
	public void enqueueCommand(int commandId, Request request,
			IResponseListener listener) {
		final ICommand cmd = getCommand(commandId);
		if (cmd != null) {
			cmd.setRequest(request);
			cmd.setResponseListener(listener);
			poolExecutor.execute(new CommandThread(cmd));
		}
	}

	public void clear() {
		taskQueue.clear();
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


	public void registerCommand(int commandId, Class<? extends ICommand> command) {
		if (command != null) {
			commands.put(commandId, command);
		}
	}

	public void unregisterCommand(int commandId) {
		commands.remove(commandId);
	}
}
