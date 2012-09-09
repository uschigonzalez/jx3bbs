package com.android.xiaow.jx3bbs.model;

import java.io.Serializable;

import com.android.xiaow.core.db.DBPrimaryKeyName;
import com.android.xiaow.core.db.DBTableName;

@DBTableName("MainBrach")
public class MainArea implements Serializable, Comparable<MainArea> {
    private static final long serialVersionUID = 6991636141260909422L;
    @DBPrimaryKeyName("url")
    public String url;
    public String name;
    public int today;
    public int newthread;
    public int refuse;
    public String url_last;
    public String last_name;
    public int isSubBroad;
    public String parent;

    public MainArea() {
        super();
    }

    @Override
    public int compareTo(MainArea another) {
        if (another == null)
            return 1;
        return another.today > today ? 1 : -1;
    }

}
