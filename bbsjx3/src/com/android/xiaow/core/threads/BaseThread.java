/**   
 * @Title: BaseThread.java 
 * @Package com.yhiker.playmate.core 
 * @Description: TODO
 * @author xiaowei   
 * @date 2012-7-23 ����4:06:27 
 * @version V1.0   
 */
package com.android.xiaow.core.threads;

import com.android.xiaow.core.cmds.ICommand;
import com.android.xiaow.core.cmds.NoCommand;

/**
 * 
 * @author ���� xiaowei
 * @����ʱ�� 2012-7-23 ����4:06:27 ��˵��
 * 
 */
public class BaseThread extends Thread {
	ThreadQueue queue;

	public BaseThread(ThreadQueue queue) {
		super();
		this.queue = queue;
	}

	@Override
	public void run() {
		super.run();
		while (true) {
			ICommand cmd = queue.getNextICommand();
			if (cmd != null) {
				if (cmd instanceof NoCommand) { return; }
				cmd.execute();
			}
		}
	}

}
