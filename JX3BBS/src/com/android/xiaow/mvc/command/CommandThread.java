package com.android.xiaow.mvc.command;

public class CommandThread implements Runnable {

    private ICommand cmd = null;

    private boolean isRunning = false;

    public CommandThread(ICommand cmd) {
        this.cmd = cmd;
    }

    public void run() {
        isRunning = true;   
        cmd.execute();
        isRunning = false;
    }

    public ICommand getCmd() {
        return cmd;
    }

    public boolean setCmd(ICommand cmd) {
        if (isRunning)
            return false;
        this.cmd = cmd;
        return true;
    }

}
