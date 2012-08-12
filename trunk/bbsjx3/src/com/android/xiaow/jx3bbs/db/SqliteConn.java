package com.android.xiaow.jx3bbs.db;

import com.android.xiaow.core.Controller;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteConn extends SQLiteOpenHelper {

    public SqliteConn(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public SqliteConn(Context context) {
        this(context, "JX3BBS.db", null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Bernda (id integer PRIMARY KEY AUTOINCREMENT,"
                + "url text UNIQUE, name text, author text, "
                + "scane integer, refuse integer,type integer, parent text,"
                + "item text, item_url text,lastName text, "
                + "last_time text, max_page integer DEFAULT 0)");
        db.execSQL("CREATE TABLE MainBrach (id integer PRIMARY KEY AUTOINCREMENT, "
                + "name text, url text UNIQUE," + " isSubBroad integer DEFAULT 0, "
                + "today integer, newthread text, "
                + "refuse text, url_last text, last_name text, parent text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    static SQLiteDatabase db;

    public synchronized static SQLiteDatabase getDatabase() {
        if (db == null) {
            db = new SqliteConn(Controller.getIntance()).getWritableDatabase();
        }
        return db;
    }
}
