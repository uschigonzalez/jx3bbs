/**
 * @author:xiaowei
 * @version:2012-5-25下午11:56:28
 */
package com.android.xiaow.jx3bbs.ui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.android.xiaow.jx3bbs.R;
import com.android.xiaow.jx3bbs.common.SubBoard;
import com.android.xiaow.jx3bbs.database.SqliteConn;
import com.android.xiaow.jx3bbs.model.MainArea;
import com.android.xiaow.jx3bbs.ui.component.SlidingGroup;
import com.android.xiaow.jx3bbs.ui.component.SyncLoadImage;
import com.android.xiaow.jx3bbs.ui.fragment.BerndaDetailFragment;
import com.android.xiaow.jx3bbs.ui.fragment.SubBerndaListFragment;
import com.android.xiaow.jx3bbs.ui.fragment.SubBerndaListFragment.ItemOnClickListener;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * @author xiaowei
 * 
 */
public class BerndaActivity extends SherlockFragmentActivity implements
		ItemOnClickListener {

	private final Handler handler = new Handler();
	ActionBar actionBar;
	private List<String> title;

	private SubBerndaListFragment leftFrag;
	private BerndaDetailFragment rightFrag;

	private String parent = "";// 当前版块的父版块名称
	private String url = "";// 当前版块的url地址
	private String name = "";// 当前版块的名称
	private HashMap<String, MainArea> lists;
	FragmentTransaction ft;
	View root;
	View root1;
	MainArea cur_are;
	SlidingGroup slidingGroup;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.berndafragement);
		root = findViewById(R.id.root);
		slidingGroup = (SlidingGroup) findViewById(R.id.sliding);
		parent = getIntent().getStringExtra("parent");
		url = getIntent().getStringExtra("urk");
		name = getIntent().getStringExtra("name");
		lists = new HashMap<String, MainArea>();

		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		loadActionList();

		final int MARGIN = 16;
		leftFrag = new SubBerndaListFragment(getResources().getColor(
				R.color.android_green), 1f, MARGIN, MARGIN / 2, MARGIN, MARGIN);

		rightFrag = new BerndaDetailFragment(getResources().getColor(
				R.color.honeycombish_blue), 1f, MARGIN / 2, MARGIN, MARGIN,
				MARGIN);
		ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.root, leftFrag);
		ft.add(R.id.root, rightFrag);
		isLandScape = false;
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			isLandScape = true;
		} else {
			ft.hide(rightFrag);
		}
		ft.commit();
	}

	/**
	 * 加载Actionbar上list列表
	 */
	private void loadActionList() {
		if (TextUtils.isEmpty(parent)) {
			parent = "";
		}
		title = new LinkedList<String>();
		Cursor cursor = SqliteConn.getDatabase(this).query("MainBrach", null,
				null, null, null, null, " today DESC ");
		lists.clear();
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
			if (TextUtils.isEmpty(mArea.parent)) {
				mArea.parent = MainListActivity.MAIN_BRACH;
			}
			if (!mArea.parent.equals(parent))
				continue;
			if (SubBoard.validContact(mArea.name)) {
				continue;
			}
			lists.put(mArea.name, mArea);
			title.add(mArea.name);
		}
		actionBar.setListNavigationCallbacks(new ArrayAdapter<String>(this,
				R.layout.sherlock_spinner_dropdown_item, title),
				mOnNavigationListener);
		actionBar.setSelectedNavigationItem(title.indexOf(name));
	}

	OnNavigationListener mOnNavigationListener = new OnNavigationListener() {

		@Override
		public boolean onNavigationItemSelected(int itemPosition, long itemId) {
			cur_are = lists.get(title.get(itemPosition));
			// 通知子列表切换到版块
			rotateLeftFrag();
			if (leftFrag.isHidden()) {
				ft = getSupportFragmentManager().beginTransaction();
				ft.hide(rightFrag);
				ft.show(leftFrag);
				ft.commit();
			}
			leftFrag.refresh(cur_are);
			return false;
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main_menu, menu);

		// set up a listener for the refresh item
		final MenuItem refresh = (MenuItem) menu.findItem(R.id.menu_refresh);
		refresh.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			// on selecting show progress spinner for 1s
			public boolean onMenuItemClick(MenuItem item) {
				// item.setActionView(R.layout.progress_action);
				handler.postDelayed(new Runnable() {
					public void run() {
						refresh.setActionView(null);
					}
				}, 1000);
				return false;
			}
		});
		// if (11 > Build.VERSION.SDK_INT) {
		// menu.findItem(R.id.menu_search).setActionView(
		// SearchViewCompat.newSearchView(this));
		// } else {
	
		// .setActionView(new SearchView(this));
		// }
		return super.onCreateOptionsMenu(menu);
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			slidingGroup.snapToNext();
			return false;
		case R.id.menu_refresh:
			item.setActionView(R.layout.indeterminate_progress_action);
			if (!leftFrag.isHidden()) {
				rotateLeftFrag();
				try {
					leftFrag.show();
					leftFrag.refresh();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (!rightFrag.isHidden()) {
				rightFrag.reflash();
			}

			return true;

			// case R.id.menu_text:
			// // alpha animation of blue fragment
			// ObjectAnimator alpha =
			// ObjectAnimator.ofFloat(rightFrag.getView(),
			// "alpha", 1f, 0f);
			// alpha.setRepeatMode(ObjectAnimator.REVERSE);
			// alpha.setRepeatCount(1);
			// alpha.setDuration(800);
			// alpha.start();
			// return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void rotateLeftFrag() {
		// if (leftFrag != null) {
		// ObjectAnimator.ofFloat(leftFrag.getView(), "rotationY", 0, 360)
		// .setDuration(500).start();
		// }
	}

	boolean isLandScape;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		ft = getSupportFragmentManager().beginTransaction();
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// 横屏
			isLandScape = true;
			ft.show(rightFrag);
			ft.show(leftFrag);
			leftFrag.show();
		} else {
			// 竖屏
			ft.hide(rightFrag);
			ft.show(leftFrag);
			leftFrag.show();

		}
		ft.commit();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.android.xiaow.jx3bbs.ui.fragment.SubBerndaListFragment.
	 * ItemOnClickListener#onItemClick(java.lang.String)
	 */
	@Override
	public void onItemClick(String url, String title) {

		if (!isLandScape) {
			ft = getSupportFragmentManager().beginTransaction();
			ft.hide(leftFrag);
			leftFrag.hidden();
			ft.show(rightFrag);
			ft.commit();
		}
		SyncLoadImage.getIntance().clear();
		rightFrag.loadUrl(url);

		rightFrag.setTitle(title);
	}

	@Override
	public void onBackPressed() {
		if (!isLandScape && leftFrag.isHidden()) {
			ft = getSupportFragmentManager().beginTransaction();
			ft.hide(rightFrag);
			ft.show(leftFrag);
			leftFrag.show();
			ft.commit();
			ObjectAnimator.ofFloat(leftFrag.getView(), "rotationY", 0, 360)
					.setDuration(500).start();
			SyncLoadImage.getIntance().clear();
		} else
			super.onBackPressed();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("parent", parent);
		outState.putString("url", url);
		outState.putString("name", name);
		outState.putString("area_url", cur_are.url);
		outState.putString("area_name", cur_are.name);
		outState.putInt("area_today", cur_are.today);
		outState.putInt("area_newthread", cur_are.newthread);
		outState.putInt("area_refuse", cur_are.refuse);
		outState.putString("area_url_last", cur_are.url_last);
		outState.putString("area_last_name", cur_are.last_name);
		outState.putBoolean("area_isSubBroad", cur_are.isSubBroad);
		outState.putString("area_parent", cur_are.parent);

		if (!TextUtils.isEmpty(rightFrag.getUrl()))
			outState.putString("right_url", rightFrag.getUrl());
		if (!TextUtils.isEmpty(rightFrag.getTitle()))
			outState.putString("right_title", rightFrag.getTitle());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		parent = savedInstanceState.getString("parent");
		loadActionList();
		MainArea mainArea = new MainArea();
		mainArea.url = savedInstanceState.getString("area_url");
		mainArea.name = savedInstanceState.getString("area_name");
		mainArea.today = savedInstanceState.getInt("area_today");
		mainArea.newthread = savedInstanceState.getInt("area_newthread");
		mainArea.refuse = savedInstanceState.getInt("area_refuse");
		mainArea.url_last = savedInstanceState.getString("area_url_last");
		mainArea.last_name = savedInstanceState.getString("area_last_name");
		mainArea.isSubBroad = savedInstanceState.getBoolean("area_isSubBroad");
		mainArea.parent = savedInstanceState.getString("area_parent");
		if (leftFrag != null && leftFrag.isHidden()) {
			leftFrag.refresh(mainArea);
		}
		if (rightFrag != null && rightFrag.isHidden()) {
			if (!TextUtils.isEmpty(savedInstanceState.getString("right_title")))
				rightFrag.setTitle(savedInstanceState.getString("right_title"));
			if (!TextUtils.isEmpty(savedInstanceState.getString("right_url")))
				rightFrag.loadUrl(savedInstanceState.getString("right_url"));
		}
	}

}
