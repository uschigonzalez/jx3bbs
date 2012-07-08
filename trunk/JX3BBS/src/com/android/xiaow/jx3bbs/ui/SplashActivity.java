package com.android.xiaow.jx3bbs.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;

import com.android.xiaow.jx3bbs.CommandID;
import com.android.xiaow.jx3bbs.Config;
import com.android.xiaow.jx3bbs.Initializer;
import com.android.xiaow.jx3bbs.R;
import com.android.xiaow.jx3bbs.database.MainBrachConn;
import com.android.xiaow.mvc.BaseActivity;
import com.android.xiaow.mvc.Thread.ThreadPool;
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
	private Handler maLauncher = new Handler() {
		public void handleMessage(Message msg) {
			if (pro > 90) {

				launchMainActivity();
			} else {
				pro += 10;
				mBar.setProgress(pro);
			}
			// System.out.println("mBar:" + mBar.getProgress() + "," + pro);
		}
	};
	SharedPreferences sp;

	@Override
	protected int getContentViewID() {
		// if (10 < Build.VERSION.SDK_INT) {
		// StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		// .detectDiskReads().detectDiskWrites().detectNetwork()
		// .penaltyLog().build());
		// StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		// .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
		// .build());
		// }
		sp = getSharedPreferences("JX3BBS", Context.MODE_APPEND);
		isFirst = sp.getBoolean("first", true);

		SharedPreferences sp = Controller.getInstance().getApplicationContext()
				.getSharedPreferences("Settings", Context.MODE_WORLD_WRITEABLE);
		Config.ONLY_GRAVATAR = sp.getBoolean("ONLY_GRAVATAR",
				Config.ONLY_GRAVATAR);
		Config.ALL_IN_WIFI = sp.getBoolean("ALL_IN_WIFI", Config.ALL_IN_WIFI);
		Config.ONLY_IMAGE = sp.getBoolean("ONLY_IMAGE", Config.ONLY_IMAGE);
		Config.ALL = sp.getBoolean("ALL", Config.ALL);
		Config.OFF_ALL = sp.getBoolean("OFF_ALL", Config.OFF_ALL);

		Initializer.ensureInitialized();
		ThreadPool.getInstance().start();
		Request request = new Request();
		request.setContext(this);
		go(CommandID.COMMAND_MAINAREA, request, false, false);
		return R.layout.splash;
	}

	Timer mTimer;
	TimerTask task;

	@Override
	protected void onAfterCreate(Bundle savedInstanceState) {
		mBar = (ProgressBar) findViewById(R.id.progressBar1);
		task = new TimerTask() {
			@Override
			public void run() {
				maLauncher.sendEmptyMessage(0);
				// copyToFile();
			}
		};

		mTimer = new Timer();
		mTimer.schedule(task, 0, 100);
	}

	private void launchMainActivity() {
		if (isFirst) {
			Editor editor = sp.edit();
			editor.putBoolean("first", false);
			editor.commit();
		}
		int len = MainBrachConn.getInstance().getCount();
		if (len > 26) {
			Intent intent = new Intent(this, MainListActivity.class);
			intent.putExtra("first", isFirst);
			startActivity(intent);
			finish();
			mTimer.cancel();
		} else {
			pro = 0;
			mBar.setProgress(0);
		}
	}

	public void copyToFile() {
		File file = new File(DB_PATH);
		if (file.exists())
			return;
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		try {
			InputStream is = getAssets().open("JX3BBS.db");
			OutputStream os = new FileOutputStream(file);
			byte[] buff = new byte[1024];
			int len = 0;
			while ((len = is.read(buff)) != -1) {
				os.write(buff, 0, len);
			}
			is.close();
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
