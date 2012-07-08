package com.android.xiaow.mvc.Thread;

import com.android.xiaow.mvc.command.ICommand;

public class CommandThread implements Runnable {
    private int threadId;
    private Thread thread = null;
    private boolean running = false;
    private boolean stop = false;

    public CommandThread(int threadId) {
        this.threadId = threadId;
        thread = new Thread(this);
    }

    public void run() {
        while (!stop) {
        	ICommand cmd = ThreadPool.getInstance().getNextCommand();
            if (cmd == null) {
                running = false;
                break;
            }
            cmd.execute();
        }
    }

    public void start() {
        thread.start();
        running = true;
    }

    public void stop() {
        stop = true;
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public int getThreadId() {
        return threadId;
    }
}
