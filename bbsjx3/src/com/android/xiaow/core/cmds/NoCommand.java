/**   
 * @Title: NoCommand.java 
 * @Package com.yhiker.playmate.core.cmds 
 * @Description: TODO
 * @author xiaowei   
 * @date 2012-8-1 ����3:08:52 
 * @version V1.0   
 */
package com.android.xiaow.core.cmds;

/**
 * 
 * @author ���� xiaowei
 * @����ʱ�� 2012-8-1 ����3:08:52 ��˵��
 * 
 */
public final class NoCommand implements ICommand {

	@Override
	public void execute() {

	}

	@Override
	public boolean isTerminal() {
		return false;
	}

	@Override
	public void onTerminal() {
	}

}
