/**=========================================================
 * ��Ȩ���������� ��Ȩ���� (c) 2002 - 2003
 * �ļ��� com.xiaowei.Thread.BaseThread
 * ������: BaseThread
 * �޸ļ�¼��
 * ����                  ����           ����
 * =========================================================
 * 2011-9-3     xiaow
 * =========================================================*/

/**
 * 
 */
package com.android.xiaow.mvc.Thread;

import com.android.xiaow.mvc.command.ICommand;

/**
 * �����̣߳������̵߳Ĺ���
 * 
 * @author xiaowei
 * 
 */
public abstract class BaseThread {

	public abstract Object threadStart();

	public abstract void threadRun();

	public abstract Object threadEnd();

	public boolean stop;

	public boolean start;

	private ICommand cmd = null;

	public ICommand getCmd() {
		return cmd;
	}

	public void setCmd(ICommand cmd) {
		this.cmd = cmd;
	}

	public void execute() {
		start = true;
		threadStart();
		threadRun();
		threadEnd();
		stop = true;
		start = false;
	}

}
