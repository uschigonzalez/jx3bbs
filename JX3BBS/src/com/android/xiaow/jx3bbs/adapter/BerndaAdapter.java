package com.android.xiaow.jx3bbs.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.xiaow.jx3bbs.R;
import com.android.xiaow.jx3bbs.database.DataStore;
import com.android.xiaow.jx3bbs.database.SqliteConn;
import com.android.xiaow.jx3bbs.ui.component.CellLayout;

public class BerndaAdapter extends BaseAdapter {
	private OnScreenChangeListener mOnScreenChangeListener;
	int mCount;
	int mAVE_PAGE_NUM;
	Context mContext;
	String parent;

	public BerndaAdapter(Context context, int ave_page_num, String name) {
		parent = name;
		mCount = SqliteConn
				.getDatabase(mContext)
				.query("Bernda", new String[] { DataStore.Bernda.ID },
						DataStore.Bernda.PARENT + " = ?",
						new String[] { parent }, null, null, null).getCount();
		mAVE_PAGE_NUM = ave_page_num;
		this.mContext=context;
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		Log.d("MSG", "º”‘ÿ“≥ ˝£∫" + position);
		CellLayout mCell;
		if (convertView == null) {
			mCell = (CellLayout)LayoutInflater.from(mContext).inflate(
					R.layout.screen, null);
		} else {
			mCell = (CellLayout) convertView;
		}
		mCell.setChangeView(true);
		BerndaItemAdapter mAdapter = (BerndaItemAdapter) mCell.getAdapter();
		if (position == 0) {
			Cursor cursor = SqliteConn.getDatabase(mContext).query(
					"Bernda",
					null,
					DataStore.Bernda.PARENT + " = ? and "
							+ DataStore.Bernda.TYPE + "= ? ",
					new String[] { this.parent, "0" }, null, null, null);
			if (mAdapter != null)
				mAdapter.changeCursor(cursor);
			else {
				mAdapter = new BerndaItemAdapter(mAVE_PAGE_NUM, mContext, cursor);
			}
		} else {
			int from = (position - 1) * mAVE_PAGE_NUM;
			Cursor cursor = SqliteConn.getDatabase(mContext).query(
					"Bernda",
					null,
					DataStore.Bernda.PARENT + " = ? and "
							+ DataStore.Bernda.TYPE + " = ? ",
					new String[] { this.parent, "1" }, null, null,
					"last_time DESC ", from + "," + mAVE_PAGE_NUM);
			if(mAdapter==null){
				mAdapter = new BerndaItemAdapter(mAVE_PAGE_NUM, mContext, cursor);
				mCell.setAdapter(mAdapter);
			}else{
				mAdapter.changeCursor(cursor);
			}
		}
		if (mCell.getAdapter() == null)
			mCell.setAdapter(mAdapter);
		if (mOnScreenChangeListener != null) {
			mOnScreenChangeListener.onScreenChanged(position);
		}
		return mCell;
	}

	public interface OnScreenChangeListener {

		void onScreenChanged(int index);
	}

	public void setOnScreenChangedListener(OnScreenChangeListener l) {
		mOnScreenChangeListener = l;
	}
}
