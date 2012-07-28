/**   
 * @Title: InitCommand.java
 * @Package com.android.xiaow.jx3bbs.command
 * @Description: TODO(��һ�仰�������ļ���ʲô)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-7-26 ����8:17:09
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs.command;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.xiaow.jx3bbs.database.MainBrachConn;
import com.android.xiaow.jx3bbs.database.SqliteConn;
import com.android.xiaow.mvc.command.AbstractCommand;
import com.android.xiaow.mvc.common.Response;
import com.android.xiaow.mvc.controller.Controller;

/**
 * @ClassName: InitCommand
 * @Description: TODO(������һ�仰��������������)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-7-26 ����8:17:09
 * 
 */
public class InitCommand extends AbstractCommand {

	@Override
	protected void go() {
		Log.d("MSG", "InitCommand :go ");
		// MainBrachConn.getInstance().executeSql(initDBSql);
		SQLiteDatabase db = SqliteConn.getDatabase(Controller.getInstance());
		Log.d("MSG", "InitCommand :" + MainBrachConn.getInstance().getCount());
		for (String sql : sqls) {
			db.execSQL(sql);
		}
		Log.d("MSG", "InitCommand :" + MainBrachConn.getInstance().getCount());
		setResponse(new Response());
		getResponse().setError(false);
	}

	String[] sqls = new String[] {
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '������', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=7095', 0, 0, 0, 0, '', '', '����������');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ('���㷻', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=7091', 0, 0, 0, 0, '', '', '����������');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '������', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=7092', 0, 0, 0, 0, '', '', '����������');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '��߸�', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=7094', 0, 0, 0, 0, '', '', '����������');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '�򻨹�', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=7093', 0, 0, 0, 0, '', '', '����������');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '�ؽ�ɽׯ', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8034', 0, 0, 0, 0, '', '', '����������');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '�嶾ʥ��', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8553', 0, 0, 0, 0, '', '', '����������');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '�Ƽұ�', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8639', 0, 0, 0, 0, '', '', '����������');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( 'YYƵ��', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8507', 0, 0, 0, 0, '', '', null);",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '������', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=7085', 0, 0, 0, 0, '', '', null);",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '���ⷴ����', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=7099', 0, 1, 0, 0, '', '', null);",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '�з�������', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8744', 1, 0, 0, 0, '', '', null);",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '�ۺ�������', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=7101', 0, 3, 0, 0, '', '', null);",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '���鼼����', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8037', 0, 2, 0, 0, '', '', null);",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '����������', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=7087', 1, 0, 0, 0, '', '', null);",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( 'ͼƬ����ѧ��', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=7089', 0, 0, 0, 0, '', '', null);",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '��ϷӰ����', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8542', 0, 0, 0, 0, '', '', null);",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '������', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=7090', 0, 0, 0, 0, '', '', null);",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '�����䱨', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8035', 0, 0, 0, 0, '', '', null);",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '����������', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=7053', 0, 0, 0, 0, '', '', null);",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '����3�߼���Ҳ���Աר��', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8004', 0, 0, 0, 0, '', '', null);",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '������', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8505', 0, 0, 0, 0, '', '', null);",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '��������', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8736', 0, 0, 0, 0, '', '', '�з�������');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '��ѧ��ʽ', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8737', 0, 0, 0, 0, '', '', '�з�������');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '�ؿ����', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8738', 0, 0, 0, 0, '', '', '�з�������');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( 'ϵͳ����', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8739', 0, 0, 0, 0, '', '', '�з�������');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '��������', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8740', 0, 0, 0, 0, '', '', '�з�������');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '��淨', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8741', 0, 0, 0, 0, '', '', '�з�������');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ('��������', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8742', 0, 0, 0, 0, '', '', '�з�������');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ('���̳���', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8743', 0, 0, 0, 0, '', '', '�з�������');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ('��ֵ���', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8745', 0, 0, 0, 0, '', '', '�з�������');" };
}
