/**   
 * @Title: ICommand.java 
 * @Package com.yhiker.playmate.cmds 
 * @Description: TODO
 * @author xiaowei   
 * @date 2012-7-23 ����4:06:46 
 * @version V1.0   
 */
package com.android.xiaow.core.cmds;


/**
 * 
 * @author ���� xiaowei
 * @����ʱ�� 2012-7-23 ����4:06:46 ��˵��
 * 
 */
public interface ICommand {
	public abstract void execute();
	public boolean isTerminal();
	public void onTerminal();
}
