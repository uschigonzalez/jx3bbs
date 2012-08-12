package com.android.xiaow.jx3bbs.model;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.xiaow.core.db.DBPrimaryKeyName;
import com.android.xiaow.core.db.DBTableName;

@DBTableName("MainBrach")
public class MainArea implements Serializable, Comparable<MainArea>, Parcelable {
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

    public static final Parcelable.Creator<MainArea> CREATOR = new Parcelable.Creator<MainArea>() {
        public MainArea createFromParcel(Parcel in) {
            return new MainArea(in);
        }

        public MainArea[] newArray(int size) {
            return new MainArea[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(name);
        dest.writeString(url_last);
        dest.writeString(last_name);
        dest.writeString(parent);
        dest.writeInt(today);
        dest.writeInt(newthread);
        dest.writeInt(isSubBroad);
        dest.writeInt(refuse);
    }

    public MainArea(Parcel in) {
        url = in.readString();
        name = in.readString();
        url_last = in.readString();
        last_name = in.readString();
        parent = in.readString();
        today = in.readInt();
        newthread = in.readInt();
        isSubBroad = in.readInt();
        refuse = in.readInt();

    }

    @Override
    public int compareTo(MainArea another) {
        if (another == null)
            return 1;
        return another.today > today ? 1 : -1;
    }

}
