package com.android.xiaow.jx3bbs.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.xiaow.jx3bbs.CommandID;
import com.android.xiaow.jx3bbs.R;
import com.android.xiaow.jx3bbs.database.MainBrachConn;
import com.android.xiaow.jx3bbs.database.SqliteConn;
import com.android.xiaow.jx3bbs.model.MainArea;
import com.android.xiaow.jx3bbs.ui.component.CellLayout;
import com.android.xiaow.jx3bbs.ui.component.SyncLoadImage;
import com.android.xiaow.jx3bbs.ui.component.Workspace;
import com.android.xiaow.jx3bbs.ui.viewflow.TitleFlowIndicator;
import com.android.xiaow.jx3bbs.ui.viewflow.ViewFlow;
import com.android.xiaow.jx3bbs.utils.ColorUtil;
import com.android.xiaow.mvc.BaseActivity;
import com.android.xiaow.mvc.common.Request;
import com.android.xiaow.mvc.common.Response;

public class MainListActivity extends BaseActivity {

	public static final String MAIN_BRACH = "主板块";

	ListView mListView;
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
	int[] textColor = null;
	int[] color = null;

	@Override
	protected int getContentViewID() {
		textColor = ColorUtil.generateTransitionalColor(0x00ff0000, 0x00000000,
				12);
		color = new int[] { R.drawable.item_back0, R.drawable.item_back1,
				R.drawable.item_back2, R.drawable.item_back3,
				R.drawable.item_back4, R.drawable.item_back5,
				R.drawable.item_back6, R.drawable.item_back7,
				R.drawable.item_back8, R.drawable.item_back9,
				R.drawable.item_back10, R.drawable.item_back11 };
		return R.layout.workspace;
	}

	@Override
	protected void onAfterCreate(Bundle savedInstanceState) {
		super.onAfterCreate(savedInstanceState);
		work = (Workspace) findViewById(R.id.workspace);
		data = new HashMap<String, List<MainArea>>();
		title = new ArrayList<String>();
		// 更新板块的最新数据，但不返回给界面
		Request request = new Request();
		request.setContext(this);
		go(CommandID.COMMAND_MAINAREA, request, false, false);
		new LoadMainAread().execute();
	}

	class LoadMainAread extends
			AsyncTask<Void, Void, Map<String, List<MainArea>>> {
		ProgressDialog pro;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pro = new ProgressDialog(MainListActivity.this);
		}

		@Override
		protected Map<String, List<MainArea>> doInBackground(Void... params) {
			/**
			 * 查询数据，查询所有的版块
			 */
			Cursor cursor = SqliteConn.getDatabase(MainListActivity.this)
					.query("MainBrach", null, null, null, null, null,
							" today DESC");
			while (cursor.moveToNext()) {
				MainArea mArea = new MainArea();
				mArea.name = cursor.getString(cursor.getColumnIndex("name"));
				mArea.url = cursor.getString(cursor.getColumnIndex("url"));
				mArea.url_last = cursor.getString(cursor
						.getColumnIndex("url_last"));
				mArea.last_name = cursor.getString(cursor
						.getColumnIndex("last_name"));
				mArea.newthread = cursor.getInt(cursor
						.getColumnIndex("newthread"));
				mArea.refuse = cursor.getInt(cursor.getColumnIndex("refuse"));
				mArea.today = cursor.getInt(cursor.getColumnIndex("today"));
				int _tmp = cursor.getInt(cursor.getColumnIndex("isSubBroad"));
				mArea.isSubBroad = _tmp == 0 ? false : true;
				mArea.parent = cursor
						.getString(cursor.getColumnIndex("parent"));
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
			return data;
		}

		@Override
		protected void onPostExecute(Map<String, List<MainArea>> result) {
			super.onPostExecute(result);
			CellLayout cellLayout = null;
			for (int i = 0; i < title.size(); i++) {

				if (i < work.getChildCount()) {
					cellLayout = (CellLayout) work.getChildAt(i);
				} else {
					cellLayout = (CellLayout) getLayoutInflater().inflate(
							R.layout.screen, null, false);
					work.addView(cellLayout);
				}
				List<MainArea> mainAreas = data.get(title.get(i));
				int step=mainAreas.size();
				
				textColor = ColorUtil.generateTransitionalColor(0x00ff0000,
						0x00000000, Math.max(step, 3));
				for (int j = 0; j < mainAreas.size(); j++) {
					MainArea mainArea = mainAreas.get(j);
					View view = buildView(mainArea, j);
					int[] vacant = new int[2];
					if (cellLayout.getVacantCell(vacant, 2, 1)) {
						work.addInScreen(view, i, vacant[0], vacant[1], 2, 1);
					}
				}
			}
			pro.dismiss();
		}
	}

	public View buildView(MainArea mainArea, int position) {
		View view = getLayoutInflater().inflate(R.layout.areaitem, null);
		TextView tv1 = (TextView) view.findViewById(R.id.textView1);
		tv1.setText(mainArea.name);
		TextView tv2 = (TextView) view.findViewById(R.id.textView2);
		tv2.setText(mainArea.today + "");
		int len = textColor.length;
		int index = position % len;
		if (index > -1 && index < len)
			tv2.setTextColor(0xff000000 + textColor[index]);
		view.setBackgroundResource(color[position % color.length]);
		view.setOnClickListener(mOnClickListener);
		view.setTag(mainArea);
		return view;
	}

	public void onError(Response response) {
		super.onError(response);
		hideProgress();
		Toast.makeText(this, "获取失败", 100).show();
	}

	public void onSuccess(Response response) {
		super.onSuccess(response);
		hideProgress();
		Toast.makeText(this, "获取成功", 100).show();
	}

	protected void onResume() {
		super.onResume();
		SyncLoadImage.getIntance().clear();
	}

	View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			MainArea area = (MainArea) v.getTag();
			Request request = new Request();
			request.setData(area.url);
			request.setTag(area.name);
			if (area.isSubBroad) {
				request.setContext(MainListActivity.this);
				if (MainBrachConn.getInstance().getCount(area.name) < 1) {
					go(CommandID.COMMAND_MAINAREA, request, true, true);
				} else {
					request.setData(area.name);
					go(CommandID.COMMAND_MAINAREA_DB, request, false, true);
				}
				isReturn = true;
			} else {
				Intent intent = new Intent(MainListActivity.this,
						BerndaActivity.class);
				intent.putExtra("first", isfirst);
				intent.putExtra("url", area.url);
				intent.putExtra("name", area.name);
				intent.putExtra("parent", area.parent);
				overridePendingTransition(android.R.anim.fade_out,
						android.R.anim.fade_out);
				startActivity(intent);
			}
		}
	};

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

}
