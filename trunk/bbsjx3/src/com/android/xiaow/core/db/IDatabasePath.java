/**   
 * @Title: IDdatabsePath.java 
 * @Package com.yhiker.playmate.core.db 
 * @Description: TODO
 * @author xiaowei   
 * @date 2012-8-7 ����10:15:57 
 * @version V1.0   
 */
package com.android.xiaow.core.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * @author ���� xiaowei
 * @����ʱ�� 2012-8-7 ����10:15:57 ��˵��
 * 
 */
public interface IDatabasePath {

    SQLiteDatabase getDatabase();
}
