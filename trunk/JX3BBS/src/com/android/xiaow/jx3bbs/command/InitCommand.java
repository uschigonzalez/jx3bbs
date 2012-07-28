/**   
 * @Title: InitCommand.java
 * @Package com.android.xiaow.jx3bbs.command
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-7-26 下午8:17:09
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
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-7-26 下午8:17:09
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
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '纯阳宫', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=7095', 0, 0, 0, 0, '', '', '门派讨论区');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ('七秀坊', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=7091', 0, 0, 0, 0, '', '', '门派讨论区');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '少林寺', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=7092', 0, 0, 0, 0, '', '', '门派讨论区');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '天策府', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=7094', 0, 0, 0, 0, '', '', '门派讨论区');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '万花谷', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=7093', 0, 0, 0, 0, '', '', '门派讨论区');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '藏剑山庄', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8034', 0, 0, 0, 0, '', '', '门派讨论区');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '五毒圣地', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8553', 0, 0, 0, 0, '', '', '门派讨论区');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '唐家堡', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8639', 0, 0, 0, 0, '', '', '门派讨论区');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( 'YY频道', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8507', 0, 0, 0, 0, '', '', null);",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '新手区', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=7085', 0, 0, 0, 0, '', '', null);",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '问题反馈区', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=7099', 0, 1, 0, 0, '', '', null);",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '研发交流区', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8744', 1, 0, 0, 0, '', '', null);",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '综合讨论区', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=7101', 0, 3, 0, 0, '', '', null);",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '经验技巧区', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8037', 0, 2, 0, 0, '', '', null);",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '门派讨论区', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=7087', 1, 0, 0, 0, '', '', null);",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '图片、文学区', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=7089', 0, 0, 0, 0, '', '', null);",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '游戏影音区', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8542', 0, 0, 0, 0, '', '', null);",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '公会区', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=7090', 0, 0, 0, 0, '', '', null);",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '大唐驿报', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8035', 0, 0, 0, 0, '', '', null);",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '测试讨论区', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=7053', 0, 0, 0, 0, '', '', null);",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '剑网3高级玩家测试员专区', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8004', 0, 0, 0, 0, '', '', null);",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '事务区', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8505', 0, 0, 0, 0, '', '', null);",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '程序引擎', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8736', 0, 0, 0, 0, '', '', '研发交流区');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '武学招式', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8737', 0, 0, 0, 0, '', '', '研发交流区');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '关卡设计', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8738', 0, 0, 0, 0, '', '', '研发交流区');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '系统功能', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8739', 0, 0, 0, 0, '', '', '研发交流区');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '美术表现', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8740', 0, 0, 0, 0, '', '', '研发交流区');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ( '活动玩法', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8741', 0, 0, 0, 0, '', '', '研发交流区');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ('背景剧情', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8742', 0, 0, 0, 0, '', '', '研发交流区');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ('历程场景', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8743', 0, 0, 0, 0, '', '', '研发交流区');",
			"INSERT INTO MainBrach ( name, url, isSubBroad, today, newthread, refuse, url_last, last_name, parent) VALUES ('数值设计', 'http://jx3.bbs.xoyo.com/forumdisplay.php?fid=8745', 0, 0, 0, 0, '', '', '研发交流区');" };
}
