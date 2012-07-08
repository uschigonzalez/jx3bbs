package com.android.xiaow.jx3bbs.adapter;

import android.view.View;
import android.widget.BaseAdapter;

public abstract class CellAdapter extends BaseAdapter{
	public abstract int[] getCellLayoutParams(int postion);
	public abstract void initCellChildView(int postion ,View view);
}
