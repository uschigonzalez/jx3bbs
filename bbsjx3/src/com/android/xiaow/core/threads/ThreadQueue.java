/**   
 * @Title: ThreadQueue.java 
 * @Package com.yhiker.playmate.core 
 * @Description: TODO
 * @author xiaowei   
 * @date 2012-7-23 ����4:07:07 
 * @version V1.0   
 */
package com.android.xiaow.core.threads;

import java.util.concurrent.LinkedBlockingQueue;

import com.android.xiaow.core.cmds.ICommand;

/**
 * 
 * @author ���� xiaowei
 * @����ʱ�� 2012-7-23 ����4:07:07 ��˵��
 * 
 */
public class ThreadQueue {
    LinkedBlockingQueue<ICommand> cmds;

    public ThreadQueue() {
        cmds = new LinkedBlockingQueue<ICommand>();
    }

    public ThreadQueue(int capacity) {
        cmds = new LinkedBlockingQueue<ICommand>(capacity);
    }

    protected synchronized ICommand getNextICommand() {
        ICommand cmd = null;
        try {
            cmd = cmds.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return cmd;
    }

    public void equeue(ICommand cmd) {
        cmds.add(cmd);
    }

    public void clear() {
        cmds.clear();
    }
}
