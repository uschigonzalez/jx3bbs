package com.android.xiaow.jx3bbs;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import android.os.Environment;

import com.android.xiaow.jx3bbs.command.BerndaDB;
import com.android.xiaow.jx3bbs.command.BerndaList;
import com.android.xiaow.jx3bbs.command.CardsCommand;
import com.android.xiaow.jx3bbs.command.ImageDownLoad;
import com.android.xiaow.jx3bbs.command.InitCommand;
import com.android.xiaow.jx3bbs.command.JX3MainList;
import com.android.xiaow.jx3bbs.command.MainAreaDB;
import com.android.xiaow.jx3bbs.common.SubBoard;
import com.android.xiaow.jx3bbs.ui.MainListActivity;
import com.android.xiaow.mvc.Thread.ThreadPool;
import com.android.xiaow.mvc.controller.Controller;

public class Initializer {
	public static void ensureInitialized() {
		Controller ctrl = Controller.getInstance();
		if (ctrl != null) {
			ctrl.registerActivity(ActivityID.ACTIVITY_ID_MAIN,
					MainListActivity.class);
		}

	
			ThreadPool.getInstance().registerCommand(CommandID.COMMAND_MAINAREA, JX3MainList.class);
			ThreadPool.getInstance().registerCommand(CommandID.COMMAND_BERNDA, BerndaList.class);
			ThreadPool.getInstance().registerCommand(CommandID.COMMAND_MAINAREA_DB, MainAreaDB.class);
			ThreadPool.getInstance().registerCommand(CommandID.COMMAND_BERNDA_DB, BerndaDB.class);
			ThreadPool.getInstance().registerCommand(CommandID.COMMAND_CARD,CardsCommand.class);
			ThreadPool.getInstance().registerCommand(CommandID.COMMAND_IMAGE,ImageDownLoad.class);
			ThreadPool.getInstance().registerCommand(CommandID.COMMAND_INIT	,InitCommand.class);
		
		SubBoard.addSubBoard("门派讨论区");
		SubBoard.addSubBoard("研发交流区");
	}
	public static final List<String> bernda=Arrays.asList(new String[]{"公告区","客服区"});

	public static final String PATH = new File(Environment
			.getExternalStorageDirectory().getPath(), "/jx3bbs/").getPath();
}
