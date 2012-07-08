package com.android.xiaow.jx3bbs.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.xiaow.jx3bbs.model.Bernda;
import com.android.xiaow.mvc.controller.Controller;

public class BerndaConn {

	SqliteConn mConn;
	SQLiteDatabase db;

	private static BerndaConn instance;

	public static final BerndaConn getInstance() {
		instance = instance == null ? new BerndaConn(Controller.getInstance()
				.getApplicationContext()) : instance;
		return instance;
	}

	private BerndaConn(Context context) {
		db = SqliteConn.getDatabase(context);
		// db = SQLiteDatabase.openDatabase(SplashActivity.DB_PATH, null,
		// SQLiteDatabase.OPEN_READWRITE);
	}

	public void save(Bernda mBernda) {
		ArrayList<Bernda> data = new ArrayList<Bernda>();
		data.add(mBernda);
		save(data);
	}

	public void save(List<Bernda> data) {
		db.beginTransaction();

		for (Bernda bernda : data) {
			ContentValues values = new ContentValues();
			values.put("url", bernda.url);
			values.put("name", bernda.name);
			values.put("author", bernda.author);
			values.put("parent", bernda.parent);
			values.put("scane", bernda.scane);
			values.put("type", (bernda.type ? 1 : 0));
			values.put("refuse", bernda.refuse);
			values.put("item", bernda.item);
			values.put("item_url", bernda.item_url);
			values.put("last_time", bernda.last_time);
			values.put("max_page", bernda.max_page);
			values.put("lastName", bernda.lastName);
			db.insertWithOnConflict("Bernda", "", values,
					SQLiteDatabase.CONFLICT_REPLACE);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	public int getCount() {
		return db.query("Bernda", new String[] { "id" }, null, null, null,
				null, null).getCount();
	}

	public int getCount(String str) {

		return db.query("Bernda", new String[] { "id" }, "parent = ?",
				new String[] { str }, null, null, null).getCount();
	}

	public List<Bernda> getData(String str) {
		List<Bernda> mBerndas = new ArrayList<Bernda>();
		Cursor cursor = db.query("Bernda", null, "parent = ?",
				new String[] { str }, null, null, "last_time DESC");
		while (cursor.moveToNext()) {
			Bernda bernda = new Bernda();
			bernda.url = cursor.getString(cursor.getColumnIndex("url"));
			bernda.name = cursor.getString(cursor.getColumnIndex("name"));
			bernda.item = cursor.getString(cursor.getColumnIndex("item"));
			bernda.item_url = cursor.getString(cursor
					.getColumnIndex("item_url"));
			bernda.parent = cursor.getString(cursor.getColumnIndex("parent"));
			bernda.author = cursor.getString(cursor.getColumnIndex("author"));
			bernda.max_page = cursor.getInt(cursor.getColumnIndex("max_page"));
			bernda.refuse = cursor.getInt(cursor.getColumnIndex("refuse"));
			bernda.scane = cursor.getInt(cursor.getColumnIndex("scane"));
			bernda.lastName = cursor.getString(cursor
					.getColumnIndex("lastName"));
			bernda.type = cursor.getInt(cursor.getColumnIndex("type")) == 0 ? false
					: true;
			bernda.last_time = cursor.getLong(cursor
					.getColumnIndex("last_time")) + "";
			mBerndas.add(bernda);
		}
		return mBerndas;
	}

	public List<Bernda> getData(String str, int from) {
		List<Bernda> mBerndas = new ArrayList<Bernda>();
		Cursor cursor = db.query("Bernda", null, "parent = ?",
				new String[] { str }, null, null, "last_time DESC", 0 + ","
						+ from);
		while (cursor.moveToNext()) {
			Bernda bernda = new Bernda();
			bernda.url = cursor.getString(cursor.getColumnIndex("url"));
			bernda.name = cursor.getString(cursor.getColumnIndex("name"));
			bernda.item = cursor.getString(cursor.getColumnIndex("item"));
			bernda.item_url = cursor.getString(cursor
					.getColumnIndex("item_url"));
			bernda.parent = cursor.getString(cursor.getColumnIndex("parent"));
			bernda.author = cursor.getString(cursor.getColumnIndex("author"));
			bernda.max_page = cursor.getInt(cursor.getColumnIndex("max_page"));
			bernda.refuse = cursor.getInt(cursor.getColumnIndex("refuse"));
			bernda.scane = cursor.getInt(cursor.getColumnIndex("scane"));
			bernda.lastName = cursor.getString(cursor
					.getColumnIndex("lastName"));
			bernda.type = cursor.getInt(cursor.getColumnIndex("type")) == 0 ? false
					: true;
			bernda.last_time = cursor.getString(cursor
					.getColumnIndex("last_time"));
			// System.out.println("bernda.last_time:"+bernda.last_time);
			mBerndas.add(bernda);
		}
		return mBerndas;
	}
}
