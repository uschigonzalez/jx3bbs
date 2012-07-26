package com.android.xiaow.jx3bbs.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.xiaow.jx3bbs.model.MainArea;
import com.android.xiaow.mvc.controller.Controller;

public class MainBrachConn {
	SqliteConn mConn;
	SQLiteDatabase db;

	
private static MainBrachConn instance;
	
	public static final MainBrachConn getInstance(){
		instance=instance==null?new MainBrachConn(Controller.getInstance().getApplicationContext()):instance;
		return instance;
	}
	
	
	private MainBrachConn(Context context) {
		 db = SqliteConn.getDatabase(context);
	}

	public void save(MainArea mArea) {
		ArrayList<MainArea> data = new ArrayList<MainArea>();
		data.add(mArea);
		save(data);
	}

	public void save(List<MainArea> data) {
		/**
		 * db.execSQL(
		 * "CREATE TABLE MainBrach (id integer PRIMARY KEY AUTOINCREMENT, " +
		 * "name text, url text UNIQUE," + " isSubBroad boolean DEFAULT false, "
		 * + "today integer, newthread text, " +
		 * "refuse text, url_last text, last_name text)");
		 */
		db.beginTransaction();
		for (MainArea mainArea : data) {
			ContentValues values = new ContentValues();
			values.put("url", mainArea.url);
			values.put("parent", mainArea.parent);
			values.put("name", mainArea.name);
			values.put("isSubBroad", mainArea.isSubBroad);
			values.put("today", mainArea.today);
			values.put("refuse", mainArea.refuse);
			values.put("newthread", mainArea.newthread);
			values.put("url_last", mainArea.url_last);
			values.put("last_name", mainArea.last_name);
			db.insertWithOnConflict("MainBrach", "", values,
					SQLiteDatabase.CONFLICT_REPLACE);
			;
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	public int getCount() {
		return db.query("MainBrach", new String[] { "id" }, null, null, null,
				null, null).getCount();
	}

	public int getCount(String str) {

		return db.query("MainBrach", new String[] { "id" }, "parent = ?",
				new String[] { str }, null, null, null).getCount();
	}

	public List<MainArea> getBrach(String parentName) {
		Cursor cursor = db.query("MainBrach", null, "parent = ?",
				new String[] { parentName }, null, null, " today DESC");
		List<MainArea> mAreas = new ArrayList<MainArea>();
		while (cursor.moveToNext()) {
			MainArea mArea = new MainArea();
			mArea.name = cursor.getString(cursor.getColumnIndex("name"));
			mArea.url = cursor.getString(cursor.getColumnIndex("url"));
			mArea.url_last = cursor
					.getString(cursor.getColumnIndex("url_last"));
			mArea.last_name = cursor.getString(cursor
					.getColumnIndex("last_name"));
			mArea.newthread = cursor.getInt(cursor.getColumnIndex("newthread"));
			mArea.refuse = cursor.getInt(cursor.getColumnIndex("refuse"));
			mArea.today = cursor.getInt(cursor.getColumnIndex("today"));
			int _tmp = cursor.getInt(cursor.getColumnIndex("isSubBroad"));
			mArea.parent = cursor.getString(cursor.getColumnIndex("parent"));
			mArea.isSubBroad = _tmp == 0 ? false : true;
			mAreas.add(mArea);
		}
		return mAreas;
	}
	public void executeSql(String sql){
		db.execSQL(sql);
		Log.d("BUG", "executeSql : "+sql.length());
	}
}
