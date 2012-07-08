package com.android.xiaow.jx3bbs.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.xiaow.jx3bbs.CommandID;
import com.android.xiaow.jx3bbs.R;
import com.android.xiaow.jx3bbs.adapter.AreaItemAdpter;
import com.android.xiaow.jx3bbs.adapter.CellAdapter;
import com.android.xiaow.jx3bbs.adapter.AreaItemAdpter.Holder;
import com.android.xiaow.jx3bbs.database.MainBrachConn;
import com.android.xiaow.jx3bbs.database.SqliteConn;
import com.android.xiaow.jx3bbs.model.MainArea;
import com.android.xiaow.jx3bbs.ui.component.CellLayout;
import com.android.xiaow.jx3bbs.ui.component.SyncLoadImage;
import com.android.xiaow.jx3bbs.ui.component.Workspace;
import com.android.xiaow.jx3bbs.ui.viewflow.TitleFlowIndicator;
import com.android.xiaow.jx3bbs.ui.viewflow.TitleProvider;
import com.android.xiaow.jx3bbs.ui.viewflow.ViewFlow;
import com.android.xiaow.mvc.BaseActivity;
import com.android.xiaow.mvc.common.Request;
import com.android.xiaow.mvc.common.Response;

public class MainListActivity extends BaseActivity {

	public static final String MAIN_BRACH = "主板块";

	ListView mListView;
	AreaItemAdpter mAdapter;
	CellLayout mCellLayout;
	// 判断是否在此页面加载过子版块
	boolean isReturn;
	WindowManager mManager;
	WindowManager.LayoutParams mParams;
	LinearLayout mCache;
	boolean isfirst = false;
	List<String> title;
	Map<String, List<MainArea>> data;
	TitleFlowIndicator indicator;
	ViewFlow mFlow;
	Workspace work;

	@Override
	protected int getContentViewID() {
		return R.layout.workspace;
	}

	@Override
	protected void onAfterCreate(Bundle savedInstanceState) {
		super.onAfterCreate(savedInstanceState);
		work = (Workspace) findViewById(R.id.workspace);


		// indicator = (TitleFlowIndicator) findViewById(R.id.title);
		// indicator.setTitleProvider(new TitleProvider() {
		//
		// @Override
		// public String getTitle(int position) {
		// return title.get(position);
		// }
		// });
		// mFlow = (ViewFlow) findViewById(R.id.viewflow);
		data = new HashMap<String, List<MainArea>>();
		title = new ArrayList<String>();
		// 更新板块的最新数据，但不返回给界面
		Request request = new Request();
		request.setContext(this);
		go(CommandID.COMMAND_MAINAREA, request, false, false);
		/**
		 * 查询数据，查询所有的版块
		 */
		Cursor cursor = SqliteConn.getDatabase(this).query("MainBrach", null,
				null, null, null, null, " today DESC");
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
			mArea.isSubBroad = _tmp == 0 ? false : true;
			mArea.parent = cursor.getString(cursor.getColumnIndex("parent"));
			if (mArea.isSubBroad)
				continue;
			if (TextUtils.isEmpty(mArea.parent)) {
				if (title.indexOf(MAIN_BRACH) < 0)
					title.add(MAIN_BRACH);
				mArea.parent = MAIN_BRACH;
			} else if (title.indexOf(mArea.parent) < 0) {
				if (title.indexOf(MAIN_BRACH) == 0) {
					title.add(0, mArea.parent);
				} else {
					title.add(mArea.parent);
				}
			}
			List<MainArea> areas = data.get(mArea.parent);
			if (areas == null) {
				areas = new ArrayList<MainArea>();
			}
			areas.add(mArea);
			data.put(mArea.parent, areas);
		}
		AreaItemAdpter cellAdapter = null;
		CellLayout cellLayout = null;
		for (int i = 0; i < title.size(); i++) {
			cellAdapter = new AreaItemAdpter(data.get(title.get(i)), this);
			cellAdapter.setOnClickListener(mOnClickListener);
			if (i < work.getChildCount()) {
				cellLayout = (CellLayout) work.getChildAt(i);
			} else {
				cellLayout = (CellLayout) getLayoutInflater().inflate(
						R.layout.screen, null, false);
				work.addView(cellLayout);
			}
			cellLayout.setAdapter(cellAdapter);
		}
		// mFlow.setAdapter(new MainAdapter(), title.size() > 1 ? 1 : 0);
		// mFlow.setFlowIndicator(indicator);
	}

	@Override
	public void onError(Response response) {
		super.onError(response);
		hideProgress();
		Toast.makeText(this, "获取失败", 100).show();
	}

	@Override
	public void onSuccess(Response response) {
		super.onSuccess(response);
		hideProgress();
		Toast.makeText(this, "获取成功", 100).show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		SyncLoadImage.getIntance().clear();
	}

	View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			AreaItemAdpter.Holder holder = (Holder) v.getTag();
			Request request = new Request();
			request.setData(holder.mainArea.url);
			request.setTag(holder.mainArea.name);
			if (holder.mainArea.isSubBroad) {
				request.setContext(MainListActivity.this);
				if (MainBrachConn.getInstance().getCount(holder.mainArea.name) < 1) {
					go(CommandID.COMMAND_MAINAREA, request, true, true);
				} else {
					request.setData(holder.mainArea.name);
					go(CommandID.COMMAND_MAINAREA_DB, request, false, true);
				}
				isReturn = true;
			} else {
				Intent intent = new Intent(MainListActivity.this,
						BerndaActivity.class);
				intent.putExtra("first", isfirst);
				intent.putExtra("url", holder.mainArea.url);
				intent.putExtra("name", holder.mainArea.name);
				intent.putExtra("parent", holder.mainArea.parent);
				overridePendingTransition(android.R.anim.fade_out,
						android.R.anim.fade_out);
				startActivity(intent);
			}
		}
	};

	class MainAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return title.size();
		}

		@Override
		public Object getItem(int position) {
			return title.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CellLayout mCell;
			if (convertView == null) {
				mCell = (CellLayout) getLayoutInflater().inflate(
						R.layout.screen, null);
			} else {
				mCell = (CellLayout) convertView;
			}
			AreaItemAdpter _adapter = new AreaItemAdpter(data.get(title
					.get(position)), MainListActivity.this);
			_adapter.setOnClickListener(mOnClickListener);
			mCell.setAdapter(_adapter);
			return mCell;
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

}
