package com.android.xiaow.mvc.controller;

import java.util.HashMap;
import java.util.Stack;

import android.R;
import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.xiaow.mvc.BaseActivity;
import com.android.xiaow.mvc.Thread.ThreadPool;
import com.android.xiaow.mvc.common.IResponseListener;
import com.android.xiaow.mvc.common.Request;
import com.android.xiaow.mvc.common.Response;

public class Controller extends Application implements IResponseListener {
	private static final String TAG = "Controller";

	private static Controller theInstance;

	public static final int ACTIVITY_ID_UPDATE_SAME = 0;
	// will be handled using "back" method.
	// Assumption: Previous navigation is never triggered by a command execution
	// (similar to that on the web => Clicking a button/link/form-submit only
	// takes you forward)
	// public static final int ACTIVITY_ID_SHOW_PREVIOUS = 1;

	public static final int ACTIVITY_ID_BASE = 1000;
	public static final int COMMAND_ID_BASE = 5000;
	private BaseActivity currentActivity;

	private final HashMap<Integer, Class<? extends BaseActivity>> registeredActivities = new HashMap<Integer, Class<? extends BaseActivity>>();
	private Stack<ActivityStackInfo> activityStack = new Stack<ActivityStackInfo>();
	private NavigationDirection currentNavigationDirection;
	private int currentActivityId;

	@Override
	public void onCreate() {
		super.onCreate();
		theInstance = this;

		registeredActivities.clear();
		// 1 to 10 are reserved. or better, use enums?
		// registeredActivities.put(11, LoginActivity.class);
		// registeredActivities.put(87945, HomeActivity.class);

	}

	public void onActivityCreating(BaseActivity activity) {
		if (activityStack.size() > 0) {
			ActivityStackInfo info = activityStack.peek();// 获得栈顶元素，但不移除它
			if (info != null) {
				Response response = info.getResponse();
				activity.preProcessData(response);
			}
		}
	}

	public void onActivityCreated(BaseActivity activity) {
		if (currentActivity != null) {
			currentActivity.finish();
		}
		currentActivity = activity;

		int size = activityStack.size();

		if (size > 0) {
			ActivityStackInfo info = activityStack.peek();
			if (info != null) {
				Response response = info.getResponse();
				activity.processData(response);

				if (size >= 2 && !info.isRecord()) {
					activityStack.pop();
				}
			}
		}
	}

	public void go(int commandID, Request request, IResponseListener listener,
			boolean record, boolean resetStack) {
		Log.i(TAG, "go with cmdid=" + commandID + ", record: " + record
				+ ",rs: " + resetStack + ", request: " + request);
		if (resetStack) {
			activityStack.clear();
		}
		if (request.getActivityID() == ACTIVITY_ID_UPDATE_SAME) {
			request.setActivityID(currentActivityId);
		}

		currentNavigationDirection = NavigationDirection.Forward;
		ActivityStackInfo info = new ActivityStackInfo(commandID, request,
				record, resetStack);
		activityStack.add(info);

		Log.d("MSG", "activityStack:" + activityStack.size());
		Object[] newTag = { request.getTag(), listener };
		request.setTag(newTag);

		Log.i(TAG, "Enqueue-ing command");
		ThreadPool.getInstance().enqueueCommand(commandID, request, this);
		Log.i(TAG, "Enqueued command");
	}

	public void back() {
		Log.i(TAG, "ActivityStack Size: " + activityStack.size());
		if (activityStack != null && activityStack.size() != 0) {
			if (activityStack.size() >= 2) {
				// Throw-away the last command, but only if there are at least
				// two commands
				ActivityStackInfo info = activityStack.pop();
				while (!info.isRecord()) {
					while (activityStack.size() < 2)
						break;
					info = activityStack.pop();
				}
			}

			currentNavigationDirection = NavigationDirection.Backward;
			ActivityStackInfo info = activityStack.peek();
			Request request = info.getRequest();
			ThreadPool.getInstance().enqueueCommand(info.getCommandID(),
					request, this);
		}
	}

	private void processResponse(Message msg) {

		Response response = (Response) msg.obj;

		ActivityStackInfo top = activityStack.peek();

		Class<? extends BaseActivity> _cls = registeredActivities
				.get(currentActivityId);
		if (_cls != null
				&& !currentActivity.getClass().getName().equals(_cls.getName())) {
			currentActivityId = -1;
		}

		top.setResponse(response);
		if (response != null) {
			int targetActivityID = response.getTargetActivityID();
			Object[] newTag = (Object[]) response.getTag();
			Object tag = newTag[0];
			IResponseListener originalListener = (IResponseListener) newTag[1];
			response.setTag(tag);

			// self-update
			if (targetActivityID == currentActivityId) {
				if (originalListener != null) {
					if (!response.isError()) {
						originalListener.onSuccess(response);
					} else {
						originalListener.onError(response);
					}
				}
			} else {
				Class<? extends BaseActivity> cls = registeredActivities
						.get(targetActivityID);
				currentActivityId = targetActivityID;
				while (!top.isRecord()) {
					if (activityStack.size() < 2)
						break;
					activityStack.pop();
					top = activityStack.peek();
				}
				int asize = activityStack.size();
				switch (currentNavigationDirection) {
				case Forward:
					if (asize >= 2) {
						if (!top.isRecord()) {
							activityStack.pop();
						}
					}
					break;
				case Backward:
					// Popping of the last command from the stack would have
					// happened in (back)
					// Just reset the navigation direction
					currentNavigationDirection = NavigationDirection.Forward;
					break;
				}
				if (cls != null) {
					Intent launcherIntent = new Intent(currentActivity, cls);
					currentActivity.overridePendingTransition(R.anim.fade_out,
							R.anim.fade_out);
					currentActivity.startActivity(launcherIntent);
					currentActivity.finish();
					top.setActivityClass(cls);
				}
			}
		}
	}

	public void registerActivity(int id, Class<? extends BaseActivity> clz) {
		registeredActivities.put(id, clz);
	}

	public void unregisterActivity(int id) {
		registeredActivities.remove(id);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			processResponse(msg);
		}
	};

	public static Controller getInstance() {
		return theInstance;
	}

	public int getActivityStackSize() {
		return activityStack.size();
	}

	public void onError(Response response) {

		handleResponse(response);
	}

	public void onSuccess(Response response) {
		handleResponse(response);
	}

	public void clearActivityStackSize() {
		activityStack.clear();
	}

	private void handleResponse(Response response) {
		if (activityStack.isEmpty())
			return;
		Message msg = new Message();
		msg.what = 0;
		msg.obj = response;
		handler.sendMessage(msg);
	}
}
