package com.android.xiaow.jx3bbs.ui;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ProgressBar;

import com.android.xiaow.jx3bbs.CommandID;
import com.android.xiaow.jx3bbs.Config;
import com.android.xiaow.jx3bbs.Initializer;
import com.android.xiaow.jx3bbs.R;
import com.android.xiaow.mvc.BaseActivity;
import com.android.xiaow.mvc.Thread.ThreadPool;
import com.android.xiaow.mvc.command.AbstractHttpCommand;
import com.android.xiaow.mvc.common.Request;
import com.android.xiaow.mvc.controller.Controller;

public class SplashActivity extends BaseActivity {
	public static final String DB_PATH = Environment
			.getExternalStorageDirectory().getPath()
			+ File.separator
			+ "JX3BBS/DB/jx3bbs.db";
	public boolean isFirst = false;
	ProgressBar mBar;
	int pro = 0;
	SharedPreferences sp;

	@Override
	protected int getContentViewID() {
		sp = getSharedPreferences("JX3BBS", Context.MODE_APPEND);
		AbstractHttpCommand.set_cookie = sp.getString("cookies", "");
		if(!TextUtils.isEmpty(AbstractHttpCommand.set_cookie)){
			Log.d("MSG", AbstractHttpCommand.set_cookie);
		}
		isFirst = sp.getBoolean("first", true);

		SharedPreferences sp = Controller.getInstance().getApplicationContext()
				.getSharedPreferences("Settings", Context.MODE_PRIVATE);
		Config.ONLY_GRAVATAR = sp.getBoolean("ONLY_GRAVATAR",
				Config.ONLY_GRAVATAR);
		Config.ALL_IN_WIFI = sp.getBoolean("ALL_IN_WIFI", Config.ALL_IN_WIFI);
		Config.ONLY_IMAGE = sp.getBoolean("ONLY_IMAGE", Config.ONLY_IMAGE);
		Config.ALL = sp.getBoolean("ALL", Config.ALL);
		Config.OFF_ALL = sp.getBoolean("OFF_ALL", Config.OFF_ALL);

		Initializer.ensureInitialized();
		ThreadPool.getInstance().start();

		return R.layout.splash;
	}

	Timer mTimer;
	TimerTask task;

	@Override
	protected void onAfterCreate(Bundle savedInstanceState) {
		Request request = new Request();
		request.setContext(this);
		if (isFirst) {
			go(CommandID.COMMAND_INIT, request, false, false);
		}
		mBar = (ProgressBar) findViewById(R.id.progressBar1);
		task = new TimerTask() {
			@Override
			public void run() {
				launchMainActivity();
			}
		};
		mTimer = new Timer();
		mTimer.schedule(task, 1000);
	}

	private void launchMainActivity() {
		if (isFirst) {
			Editor editor = sp.edit();
			editor.putBoolean("first", false);
			editor.commit();
		}
		Intent intent = new Intent(this, MainListActivity.class);
		intent.putExtra("first", isFirst);
		startActivity(intent);
		finish();
	}
}
