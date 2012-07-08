package com.android.xiaow.mvc;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.android.xiaow.mvc.Thread.ThreadPool;
import com.android.xiaow.mvc.common.IResponseListener;
import com.android.xiaow.mvc.common.Request;
import com.android.xiaow.mvc.common.Response;
import com.android.xiaow.mvc.controller.Controller;

public abstract class BaseActivity extends Activity implements
		IResponseListener {
	private View mainView;
	private MenuItem selectedItem = null;

	private static final int DIALOG_ID_PROGRESS_DEFAULT = 0x174980;

	protected WindowManager wm;

	private List<View> flowView = new LinkedList<View>();

	public void addflowView(View view, WindowManager.LayoutParams wmParams) {
		if (flowView.contains(view)) {
			return;
		}
		flowView.add(view);
		if (wmParams == null) {
			wmParams = new WindowManager.LayoutParams();
			wmParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
		}
		wm.addView(view, wmParams);
	}

	public void removeFlowView(View view) {
		if (flowView.contains(view)) {
			wm.removeView(view);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		for (int i = 0; i < flowView.size(); i++) {
			wm.removeView(flowView.get(i));
		}
	}

	public void updateSize(View view, WindowManager.LayoutParams wmParams) {
		if (wmParams == null) {
			wmParams = new WindowManager.LayoutParams();
			wmParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
		}
		if (flowView.contains(view)) {
			wm.updateViewLayout(view, wmParams);
		}
	}

	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 通知Controller Activity正在创建
		notifiyControllerActivityCreating();
		// 创建之前
		onBeforeCreate(savedInstanceState);
		super.onCreate(savedInstanceState);
		onCreateContent(savedInstanceState);
		// 创建时
		// 通知Controller Activity已经创建
		notifiyControllerActivityCreated();
		// Activity 创建之后
		onAfterCreate(savedInstanceState);
	}

	public Controller getController() {
		return (Controller) getApplication();
	}

	private void notifiyControllerActivityCreating() {
		getController().onActivityCreating(this);
	}

	private void notifiyControllerActivityCreated() {
		getController().onActivityCreated(this);
	}

	protected void onBeforeCreate(Bundle savedInstanceState) {
	}

	protected void onAfterCreate(Bundle savedInstanceState) {
	}

	protected void onCreateContent(Bundle savedInstanceState) {
		LayoutInflater inflator = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

		int rid = getContentViewID();
		if (rid != -1) {
			mainView = inflator.inflate(getContentViewID(), null, false);
		} else {
			mainView = createContentView();
		}
		setContentView(mainView);
	}

	protected View createContentView() {
		return null;
	}

	protected int getContentViewID() {
		return -1;
	}

	protected final View getMainView() {
		return mainView;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (hasCustomOptionsMenu()) {
			return createCustomOptionsMenu(menu);
		}
		return createDefaultOptionsMenu(menu);
	}

	@Override
	public final boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();

		System.out.println("Context Item Selected... " + item.getTitle());

		boolean result = onOptionsItemSelected(itemId);
		if (result) {
			if (selectedItem != null) {
				selectedItem
						.setIcon(optionMenuIds[selectedItem.getItemId()][1]);
			}
			item.setIcon(optionMenuIds[itemId][2]);
			selectedItem = item;
		}

		return true;
	}

	protected final void deselectAllOptionMenuItems() {
		if (selectedItem != null) {
			selectedItem.setIcon(optionMenuIds[selectedItem.getItemId()][1]);
			selectedItem = null;
		}
	}

	protected boolean onOptionsItemSelected(int itemId) {
		return true;
	}

	private int[][] optionMenuIds = {
	/*
	 * { R.string.title_of_item_01, R.drawable.icon_deselected_item_01,
	 * R.drawable.icon_selected_item_01 }
	 */
	};

	private boolean createDefaultOptionsMenu(Menu menu) {
		MenuItem item;

		for (int i = 0; i < optionMenuIds.length; i++) {
			item = menu.add(0, i, i, optionMenuIds[i][0]);
			item.setIcon(optionMenuIds[i][1]);
		}

		return true;
	}

	protected boolean hasCustomOptionsMenu() {
		return false;
	}

	protected boolean createCustomOptionsMenu(Menu menu) {
		return false;
	}

	public void onError(Response response) {
	}

	public void onSuccess(Response response) {
	}

	// 加载数据之前
	public void preProcessData(Response response) {

	}

	public void processData(Response response) {

	}

	protected void showProgress() {
		showDialog(DIALOG_ID_PROGRESS_DEFAULT);
	}

	protected void hideProgress() {
		try {
			removeDialog(DIALOG_ID_PROGRESS_DEFAULT);
		} catch (IllegalArgumentException iae) {
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_ID_PROGRESS_DEFAULT:
			ProgressDialog dlg = new ProgressDialog(this);
			// dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dlg.setMessage("Working...");
			dlg.setCancelable(true);
			return dlg;
		default:
			return super.onCreateDialog(id);

		}
	}

	protected final void go(int commandID, Request request) {

		go(commandID, request, true);
	}

	protected final void go(int commandID, Request request, boolean showProgress) {

		go(commandID, request, true, true);
	}

	protected final void go(int commandID, Request request,
			boolean showProgress, boolean callback) {

		if (showProgress) {
			showProgress();
		}
		ThreadPool.getInstance().enqueueCommand(commandID, request,
				callback ? mResponseListener : null);
	}

	IResponseListener mResponseListener = new IResponseListener() {

		@Override
		public void onSuccess(Response response) {
			Message msg = handler.obtainMessage();
			msg.obj = response;
			msg.what = 100;
			handler.sendMessage(msg);
		}

		@Override
		public void onError(Response response) {
			onSuccess(response);
		}
	};
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			processResponse(msg);
		}
	};

	protected final void back() {

		getController().back();
	}

	protected void processResponse(Message msg) {
		if (msg.what == 100) {
			Response response = (Response) msg.obj;
			if (response.isError()) {
				onError(response);
			} else
				onSuccess(response);
		}
	}

}
