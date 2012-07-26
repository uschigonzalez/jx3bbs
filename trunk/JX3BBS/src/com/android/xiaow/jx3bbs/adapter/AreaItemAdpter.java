package com.android.xiaow.jx3bbs.adapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.xiaow.jx3bbs.R;
import com.android.xiaow.jx3bbs.model.MainArea;
import com.android.xiaow.jx3bbs.utils.ColorUtil;

public class AreaItemAdpter extends CellAdapter {

	List<MainArea> data;
	Context mContext;
	View.OnClickListener mOnClickListener;

	public View.OnClickListener getOnClickListener() {
		return mOnClickListener;
	}

	public void setOnClickListener(View.OnClickListener l) {
		this.mOnClickListener = l;
	}

	int textColor[];
	int color[];
	Comparator<MainArea> mParator = new Comparator<MainArea>() {
		@Override
		public int compare(MainArea object1, MainArea object2) {
			return object2.today - object1.today;
		}
	};

	public AreaItemAdpter(List<MainArea> data, Context mContext) {
		super();
		this.data = data;
		this.mContext = mContext;
		Collections.sort(data, mParator);
		// color = ColorUtil.generateTransitionalColor(0x00B3E4E3, 0x0075CDCC,
		// data.size());

		color = new int[] { R.drawable.item_back0, R.drawable.item_back1,
				R.drawable.item_back2, R.drawable.item_back3,
				R.drawable.item_back4, R.drawable.item_back5,
				R.drawable.item_back6, R.drawable.item_back7,
				R.drawable.item_back8, R.drawable.item_back9,
				R.drawable.item_back10, R.drawable.item_back11 };
		textColor = ColorUtil.generateTransitionalColor(0x00ff0000, 0x00000000,
				data.size());
	}

	@Override
	public int getCount() {
		return data.size();
	}

	public void changeData(List<MainArea> data) {
		this.data = data;
		// color = ColorUtil.generateTransitionalColor(0x0095FFFD, 0x0003A4A2,
		// data.size());
		textColor = ColorUtil.generateTransitionalColor(0x00ff0000, 0x00000000,
				data.size());
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
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
		holder.tv1.setText(data.get(position).name);
		holder.tv2.setText(data.get(position).today + "");
		holder.tv2.setTextColor(0xff000000 + textColor[position]);
		convertView.setBackgroundResource(color[position % color.length]);
		holder.postion = position;
		holder.mainArea = data.get(position);
		convertView.setOnClickListener(mOnClickListener);
		convertView.setTag(holder);
		return convertView;
	}

	public int[] getCellLayoutParams(int postion) {

		if (postion < 3) {
			return new int[] { 3, 1 };
		} else if (postion < (6)) {
			return new int[] { 2, 1 };
		}
		return new int[] { 1, 1 };

	}

	public int getNextRandom() {
		return new Random().nextInt();
	}

	public static class Holder {
		public TextView tv1;
		public TextView tv2;
		public int postion;
		public MainArea mainArea;
	}

	@Override
	public void initCellChildView(int postion, View view) {

	}

}
