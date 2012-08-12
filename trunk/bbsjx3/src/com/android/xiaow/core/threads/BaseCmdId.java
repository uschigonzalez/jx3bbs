/**   
 * @Title: BaseCmdId.java 
 * @Package com.yhiker.playmate.core.threads 
 * @Description: TODO
 * @author xiaowei   
 * @date 2012-8-1 ����3:56:58 
 * @version V1.0   
 */
package com.android.xiaow.core.threads;

import android.util.SparseArray;

import com.android.xiaow.core.cmds.NoCommand;

/**
 * 
 * @author ���� xiaowei
 * @����ʱ�� 2012-8-1 ����3:56:58 ��˵��
 * 
 */
public  class BaseCmdId {

	private SparseArray<String> sparseArray = new SparseArray<String>();

	public BaseCmdId() {
		super();
		sparseArray.put(0, NoCommand.class.getName());
	}

	public boolean registerCommandId(int commandId, String className) {
		if (commandId == 0) return false;
		sparseArray.put(commandId, className);
		return true;
	}

	public String getName(int commandId) {
		return sparseArray.get(commandId);
	}

}
