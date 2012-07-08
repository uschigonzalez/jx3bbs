/**=========================================================
 * 版权：联信永益 版权所有 (c) 2002 - 2003
 * 文件： com.xiaowei.Thread.BaseThread
 * 所含类: BaseThread
 * 修改记录：
 * 日期                  作者           内容
 * =========================================================
 * 2011-9-3     xiaow
 * =========================================================*/

/**
 * 
 */
package com.android.xiaow.mvc.Thread;

import com.android.xiaow.mvc.command.ICommand;

/**
 * 基础线程，便于线程的管理
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
