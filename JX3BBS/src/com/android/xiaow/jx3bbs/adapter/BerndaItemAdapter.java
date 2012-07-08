package com.android.xiaow.jx3bbs.adapter;

import java.util.List;
import java.util.Random;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.xiaow.jx3bbs.R;
import com.android.xiaow.jx3bbs.anim.Rotation3D;
import com.android.xiaow.jx3bbs.database.DataStore;
import com.android.xiaow.jx3bbs.model.Bernda;
import com.android.xiaow.jx3bbs.utils.ColorUtil;

public class BerndaItemAdapter extends CellAdapter {

	List<Bernda> data;
	Context mContext;
	ViewTouchAnimaEnd l;
	View.OnClickListener mOnClickListener;
	public int count;

	int color[];
	int textColor[];
	Cursor cursor;
	int nameIndex = 0;
	int refuseIndex = 0;
	int scaneIndex = 0;

	public BerndaItemAdapter(int count, Context mContext, Cursor cursor) {
		super();
		this.count = count;
		color = ColorUtil.generateTransitionalColor(0x0095FFFD, 0x0003A4A2,
				count);
		textColor = ColorUtil.generateTransitionalColor(0x00ff0000, 0x00000000,
				count);
		this.mContext = mContext;
		this.cursor = cursor;
		if (cursor != null) {
			nameIndex = cursor.getColumnIndexOrThrow(DataStore.Bernda.NAME);
			refuseIndex = cursor.getColumnIndex(DataStore.Bernda.REFUSE);
			scaneIndex = cursor.getColumnIndex(DataStore.Bernda.SCANE);
		}
	}

	public void changeCursor(Cursor cursor) {
		if (cursor != null ) {
			this.cursor = cursor;
			nameIndex = cursor.getColumnIndexOrThrow(DataStore.Bernda.NAME);
			refuseIndex = cursor.getColumnIndex(DataStore.Bernda.REFUSE);
			scaneIndex = cursor.getColumnIndex(DataStore.Bernda.SCANE);
			notifyDataSetChanged();
		}
	}

	public Cursor getCursor() {
		return cursor;
	}

	@Override
	public int getCount() {
		if (cursor != null)
			return cursor.getCount();
		return count;
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
		Holder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.areaitem, null);
			holder = new Holder();
			holder.tv1 = (TextView) convertView.findViewById(R.id.textView1);
			holder.tv2 = (TextView) convertView.findViewById(R.id.textView2);

			holder.postion = position;
			convertView.setTag(holder);
		}
		holder = (Holder) convertView.getTag();
		if (cursor != null && position < cursor.getCount()) {
			if (position == 0) {
				cursor.moveToFirst();
			} else
				cursor.moveToPosition(position);
			holder.tv1.setText(cursor.getString(nameIndex) + "");
			holder.tv2.setText(cursor.getString(refuseIndex) + "/"
					+ cursor.getString(scaneIndex));
			holder.tv2.setTextColor(0xff000000 + textColor[position]);
		}
		holder.postion = position;
		convertView.setOnTouchListener(mOnTouchListener);
		convertView.setOnClickListener(mOnClickListener);
		convertView.setTag(holder);
		return convertView;
	}

	@Override
	public void initCellChildView(int postion, View view) {
		getView(postion, view, null);
	}

	public int[] getCellLayoutParams(int postion) {
		return new int[] { 2, 1 };
	}

	public int getNextRandom() {
		return new Random().nextInt();
	}

	int mCurr;
	View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				Rotation3D rotation3d = new Rotation3D(v.getWidth(),
						v.getHeight(), v.getPaddingLeft(), v.getPaddingRight(),
						v.getPaddingTop(), v.getPaddingBottom(), event.getX(),
						event.getY());
				rotation3d.setDuration(1000);
				v.startAnimation(rotation3d);
				mCurr = ((Holder) v.getTag()).postion;
			}

			return false;
		}
	};

	public void changeData(List<Bernda> data) {
		this.data = data;
		color = ColorUtil.generateTransitionalColor(0x0095FFFD, 0x0003A4A2,
				data.size());
		textColor = ColorUtil.generateTransitionalColor(0x00ff0000, 0x00000000,
				data.size());

		notifyDataSetChanged();
	}

	Animation.AnimationListener mAnimationListener = new Animation.AnimationListener() {
		@Override
		public void onAnimationEnd(Animation animation) {
			l.onClick(mCurr);
		}

		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

	};

	public static class Holder {
		public TextView tv1;
		public TextView tv2;
		public LinearLayout bar;
		public int postion;
	}

	public static interface ViewTouchAnimaEnd {
		void onClick(int postion);
	}

	public View.OnClickListener getOnClickListener() {
		return mOnClickListener;
	}

	public void setOnClickListener(View.OnClickListener l) {
		this.mOnClickListener = l;
	}

	public ViewTouchAnimaEnd getOnViewTouchAnimaEnd() {
		return l;
	}

	public void setOnViewTouchAnimaEnd(ViewTouchAnimaEnd l) {
		this.l = l;
	}

}
