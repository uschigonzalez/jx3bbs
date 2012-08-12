/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.xiaow.jx3bbs.widget;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.android.xiaow.jx3bbs.R;
//import com.android.xiaow.jx3bbs.ui.viewflow.FlowIndicator;

/**
 * The workspace is a wide area with a wallpaper and a finite number of screens.
 * Each screen contains a number of icons, folders or widgets the user can
 * interact with. A workspace is meant to be used with a fixed width only.
 */
public class Workspace extends ViewGroup {
	private static final String TAG = "Launcher.Workspace";
	private static final int INVALID_SCREEN = -999;

	/**
	 * The velocity at which a fling gesture will cause us to snap to the next
	 * screen
	 */
	private static final int SNAP_VELOCITY = 600;

	private final WallpaperManager mWallpaperManager;

	private final int mDefaultScreen;

	private boolean mFirstLayout = true;

	private int mCurrentScreen;
	private int mNextScreen = INVALID_SCREEN;
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;


	

	private float mLastMotionX;
	private float mLastMotionY;

	private final static int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING = 1;

	private int mTouchState = TOUCH_STATE_REST;


	/**
	 * Cache of vacant cells, used during drag events and invalidated as needed.
	 */
	private CellLayout.CellInfo mVacantCache = null;

	private final int[] mTempCell = new int[2];


	private int mTouchSlop;
	private int mMaximumVelocity;

	private static final int INVALID_POINTER = -1;

	private int mActivePointerId = INVALID_POINTER;


	private static final float NANOTIME_DIV = 1000000000.0f;
	private static final float SMOOTHING_SPEED = 0.75f;
	private static final float SMOOTHING_CONSTANT = (float) (0.016 / Math
			.log(SMOOTHING_SPEED));
	private float mSmoothingTime;
	private float mTouchX;

	private Runnable mAfterLayout = null;

	private WorkspaceOvershootInterpolator mScrollInterpolator;
//	private FlowIndicator mIndicator;

	private static final float BASELINE_FLING_VELOCITY = 2500.f;
	private static final float FLING_VELOCITY_INFLUENCE = 0.4f;

	private static class WorkspaceOvershootInterpolator implements Interpolator {
		private static final float DEFAULT_TENSION = 1.3f;
		private float mTension;

		public WorkspaceOvershootInterpolator() {
			mTension = DEFAULT_TENSION;
		}

		public void setDistance(int distance) {
			mTension = distance > 0 ? DEFAULT_TENSION / distance
					: DEFAULT_TENSION;
		}

		public void disableSettle() {
			mTension = 0.f;
		}

		public float getInterpolation(float t) {
			// _o(t) = t * t * ((tension + 1) * t + tension)
			// o(t) = _o(t - 1) + 1
			t -= 1.0f;
			return t * t * ((mTension + 1) * t + mTension) + 1.0f;
		}
	}

	/**
	 * Used to inflate the Workspace from XML.
	 * 
	 * @param context
	 *            The application's context.
	 * @param attrs
	 *            The attribtues set containing the Workspace's customization
	 *            values.
	 */
	public Workspace(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * Used to inflate the Workspace from XML.
	 * 
	 * @param context
	 *            The application's context.
	 * @param attrs
	 *            The attribtues set containing the Workspace's customization
	 *            values.
	 * @param defStyle
	 *            Unused.
	 */
	public Workspace(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mWallpaperManager = WallpaperManager.getInstance(context);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.Workspace, defStyle, 0);
		mDefaultScreen = a.getInt(R.styleable.Workspace_defaultScreen, 1);
		a.recycle();

		setHapticFeedbackEnabled(false);
		initWorkspace();
	}

	/**
	 * Initializes various states for this workspace.
	 */
	private void initWorkspace() {
		Context context = getContext();
		mScrollInterpolator = new WorkspaceOvershootInterpolator();
		mScroller = new Scroller(context, mScrollInterpolator);
		mCurrentScreen = mDefaultScreen;

		final ViewConfiguration configuration = ViewConfiguration
				.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
	}

	@Override
	public void addView(View child, int index, LayoutParams params) {
		if (!(child instanceof CellLayout)) {
			throw new IllegalArgumentException(
					"A Workspace can only have CellLayout children.");
		}
		super.addView(child, index, params);
	}

	@Override
	public void addView(View child) {
		if (!(child instanceof CellLayout)) {
			throw new IllegalArgumentException(
					"A Workspace can only have CellLayout children.");
		}
		super.addView(child);
	}

	@Override
	public void addView(View child, int index) {
		if (!(child instanceof CellLayout)) {
			throw new IllegalArgumentException(
					"A Workspace can only have CellLayout children.");
		}
		super.addView(child, index);
	}

	@Override
	public void addView(View child, int width, int height) {
		if (!(child instanceof CellLayout)) {
			throw new IllegalArgumentException(
					"A Workspace can only have CellLayout children.");
		}
		super.addView(child, width, height);
	}

	@Override
	public void addView(View child, LayoutParams params) {
		if (!(child instanceof CellLayout)) {
			throw new IllegalArgumentException(
					"A Workspace can only have CellLayout children.");
		}
		super.addView(child, params);
	}

	boolean isDefaultScreenShowing() {
		return mCurrentScreen == mDefaultScreen;
	}

	/**
	 * Returns the index of the currently displayed screen.
	 * 
	 * @return The index of the currently displayed screen.
	 */
	int getCurrentScreen() {
		return mCurrentScreen;
	}

	/**
	 * Sets the current screen.
	 * 
	 * @param currentScreen
	 */
	void setCurrentScreen(int currentScreen) {
		if (!mScroller.isFinished())
			mScroller.abortAnimation();
		clearVacantCache();
		mCurrentScreen = Math.max(0,
				Math.min(currentScreen, getChildCount() - 1));
//		updateIndicators(mCurrentScreen);
		scrollTo(mCurrentScreen * getWidth(), 0);
		updateWallpaperOffset();
		invalidate();
	}

	/**
	 * Adds the specified child in the current screen. The position and
	 * dimension of the child are defined by x, y, spanX and spanY.
	 * 
	 * @param child
	 *            The child to add in one of the workspace's screens.
	 * @param x
	 *            The X position of the child in the screen's grid.
	 * @param y
	 *            The Y position of the child in the screen's grid.
	 * @param spanX
	 *            The number of cells spanned horizontally by the child.
	 * @param spanY
	 *            The number of cells spanned vertically by the child.
	 */
public	void addInCurrentScreen(View child, int x, int y, int spanX, int spanY) {
		addInScreen(child, mCurrentScreen, x, y, spanX, spanY, false);
	}

	/**
	 * Adds the specified child in the current screen. The position and
	 * dimension of the child are defined by x, y, spanX and spanY.
	 * 
	 * @param child
	 *            The child to add in one of the workspace's screens.
	 * @param x
	 *            The X position of the child in the screen's grid.
	 * @param y
	 *            The Y position of the child in the screen's grid.
	 * @param spanX
	 *            The number of cells spanned horizontally by the child.
	 * @param spanY
	 *            The number of cells spanned vertically by the child.
	 * @param insert
	 *            When true, the child is inserted at the beginning of the
	 *            children list.
	 */
public	void addInCurrentScreen(View child, int x, int y, int spanX, int spanY,
			boolean insert) {
		addInScreen(child, mCurrentScreen, x, y, spanX, spanY, insert);
	}

	/**
	 * Adds the specified child in the specified screen. The position and
	 * dimension of the child are defined by x, y, spanX and spanY.
	 * 
	 * @param child
	 *            The child to add in one of the workspace's screens.
	 * @param screen
	 *            The screen in which to add the child.
	 * @param x
	 *            The X position of the child in the screen's grid.
	 * @param y
	 *            The Y position of the child in the screen's grid.
	 * @param spanX
	 *            The number of cells spanned horizontally by the child.
	 * @param spanY
	 *            The number of cells spanned vertically by the child.
	 */
public	void addInScreen(View child, int screen, int x, int y, int spanX, int spanY) {
		addInScreen(child, screen, x, y, spanX, spanY, false);
	}

	/**
	 * Adds the specified child in the specified screen. The position and
	 * dimension of the child are defined by x, y, spanX and spanY.
	 * 
	 * @param child
	 *            The child to add in one of the workspace's screens.
	 * @param screen
	 *            The screen in which to add the child.
	 * @param x
	 *            The X position of the child in the screen's grid.
	 * @param y
	 *            The Y position of the child in the screen's grid.
	 * @param spanX
	 *            The number of cells spanned horizontally by the child.
	 * @param spanY
	 *            The number of cells spanned vertically by the child.
	 * @param insert
	 *            When true, the child is inserted at the beginning of the
	 *            children list.
	 */
public	void addInScreen(View child, int screen, int x, int y, int spanX,
			int spanY, boolean insert) {
		if (screen < 0 || screen >= getChildCount()) {
			Log.e(TAG, "The screen must be >= 0 and < " + getChildCount()
					+ " (was " + screen + "); skipping child");
			return;
		}

		clearVacantCache();

		final CellLayout group = (CellLayout) getChildAt(screen);
		CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child
				.getLayoutParams();
		if (lp == null) {
			lp = new CellLayout.LayoutParams(x, y, spanX, spanY);
		} else {
			lp.cellX = x;
			lp.cellY = y;
			lp.cellHSpan = spanX;
			lp.cellVSpan = spanY;
		}
		group.addView(child, insert ? 0 : -1, lp);

	}

public	CellLayout.CellInfo findAllVacantCells(boolean[] occupied) {
		CellLayout group = (CellLayout) getChildAt(mCurrentScreen);
		if (group != null) {
			return group.findAllVacantCells(occupied, null);
		}
		return null;
	}

	private void clearVacantCache() {
		if (mVacantCache != null) {
			mVacantCache.clearVacantCells();
			mVacantCache = null;
		}
	}

	

	private void updateWallpaperOffset() {
		/**ĳЩ��������Ϊ�˴�����*/
//		updateWallpaperOffset(getChildAt(getChildCount() - 1).getRight()
//				- (getRight() - getLeft()));
	}

	private void updateWallpaperOffset(int scrollRange) {
		IBinder token = getWindowToken();
		if (token != null) {
			mWallpaperManager.setWallpaperOffsetSteps(
					1.0f / (getChildCount() - 1), 0);
			mWallpaperManager.setWallpaperOffsets(
					getWindowToken(),
					Math.max(0.f,
							Math.min(getScrollX() / (float) scrollRange, 1.f)),
					0);
		}
	}

	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
		mTouchX = x;
		mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
			mTouchX = mScroller.getCurrX();
			super.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			updateWallpaperOffset();
			postInvalidate();
		} else if (mNextScreen != INVALID_SCREEN) {
			if (mNextScreen == -1
					&& (Preferences.getInstance()).getEndlessScrolling()) {
				mCurrentScreen = getChildCount() - 1;
				scrollTo(mCurrentScreen * getWidth(), getScrollY());
				updateWallpaperOffset();
			} else if (mNextScreen == getChildCount()
					&& Preferences.getInstance().getEndlessScrolling()) {
				mCurrentScreen = 0;
				scrollTo(0, getScrollY());
				updateWallpaperOffset();
			} else
				mCurrentScreen = Math.max(0,
						Math.min(mNextScreen, getChildCount() - 1));
//			updateIndicators(mCurrentScreen);
			// Launcher.setScreen(mCurrentScreen);
			mNextScreen = INVALID_SCREEN;
			clearChildrenCache();
		} else if (mTouchState == TOUCH_STATE_SCROLLING) {
			final float now = System.nanoTime() / NANOTIME_DIV;
			final float e = (float) Math.exp((now - mSmoothingTime)
					/ SMOOTHING_CONSTANT);
			final float dx = mTouchX - getScrollX();

			final int scrolltoX = getScrollX() + (int) (dx * e);
			super.scrollTo(scrolltoX, getScrollY());
			mSmoothingTime = now;

			// Keep generating points as long as we're more than 1px away from
			// the target
			if (dx > 1.f || dx < -1.f) {
				updateWallpaperOffset();
				postInvalidate();
			}
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		// ViewGroup.dispatchDraw() supports many features we don't need:
		// clip to padding, layout animation, animation listener, disappearing
		// children, etc. The following implementation attempts to fast-track
		// the drawing dispatch by drawing only what we know needs to be drawn.

		boolean fastDraw = mTouchState != TOUCH_STATE_SCROLLING
				&& mNextScreen == INVALID_SCREEN;
		// If we are not scrolling or flinging, draw only the current screen
		if (fastDraw) {
			drawChild(canvas, getChildAt(mCurrentScreen), getDrawingTime());
		} else {
			long drawingTime = getDrawingTime();
			int width = getWidth();
			float scrollPos = (float) getScrollX() / width;
			boolean endlessScrolling = Preferences.getInstance()
					.getEndlessScrolling();

			int leftScreen;
			int rightScreen;
			boolean isScrollToRight = false;
			int childCount = getChildCount();
			if (scrollPos < 0 && endlessScrolling) {
				leftScreen = childCount - 1;
				rightScreen = 0;
			} else {
				leftScreen = Math.min((int) scrollPos, childCount - 1);
				rightScreen = leftScreen + 1;
				if (endlessScrolling) {
					rightScreen = rightScreen % childCount;
					isScrollToRight = true;
				}
			}

			if (isScreenNoValid(leftScreen)) {
				if (rightScreen == 0 && !isScrollToRight) {
					int offset = childCount * width;
					canvas.translate(-offset, 0);
					drawChild(canvas, getChildAt(leftScreen), drawingTime);
					canvas.translate(+offset, 0);
				} else {
					drawChild(canvas, getChildAt(leftScreen), drawingTime);
				}
			}
			if (scrollPos != leftScreen && isScreenNoValid(rightScreen)) {
				if (endlessScrolling && rightScreen == 0 && isScrollToRight) {
					int offset = childCount * width;
					canvas.translate(+offset, 0);
					drawChild(canvas, getChildAt(rightScreen), drawingTime);
					canvas.translate(-offset, 0);
				} else {
					drawChild(canvas, getChildAt(rightScreen), drawingTime);
				}
			}
		}
	}

	private boolean isScreenNoValid(int screen) {
		return screen >= 0 && screen < getChildCount();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		computeScroll();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"Workspace can only be used in EXACTLY mode.");
		}

		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"Workspace can only be used in EXACTLY mode.");
		}

		// The children are given the same width and height as the workspace
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}

		if (mFirstLayout) {
			setHorizontalScrollBarEnabled(false);
			scrollTo(mCurrentScreen * width, 0);
			setHorizontalScrollBarEnabled(true);
			updateWallpaperOffset(width * (getChildCount() - 1));
			mFirstLayout = false;
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		int childLeft = 0;

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				final int childWidth = child.getMeasuredWidth();
				child.layout(childLeft, 0, childLeft + childWidth,
						child.getMeasuredHeight());
				childLeft += childWidth;
			}
		}
		if (mAfterLayout != null) {
			this.post(mAfterLayout);
		}
	}

	public void setAfterLayoutListener(Runnable afterLayout) {
		mAfterLayout = afterLayout;
	}

	@Override
	public boolean requestChildRectangleOnScreen(View child, Rect rectangle,
			boolean immediate) {
		int screen = indexOfChild(child);
		if (screen != mCurrentScreen || !mScroller.isFinished()) {
			snapToScreen(screen);
			return true;
		}
		return false;
	}

	@Override
	public boolean dispatchUnhandledMove(View focused, int direction) {
		if (direction == View.FOCUS_LEFT) {
			if (getCurrentScreen() > 0) {
				snapToScreen(getCurrentScreen() - 1);
				return true;
			}
		} else if (direction == View.FOCUS_RIGHT) {
			if (getCurrentScreen() < getChildCount() - 1) {
				snapToScreen(getCurrentScreen() + 1);
				return true;
			}
		}
		return super.dispatchUnhandledMove(focused, direction);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		/*
		 * final boolean allAppsVisible = mLauncher.isAllAppsVisible(); if
		 * (allAppsVisible) { return false; // We don't want the events. Let
		 * them fall through to the all apps view. }
		 */
		/*
		 * This method JUST determines whether we want to intercept the motion.
		 * If we return true, onTouchEvent will be called and we do the actual
		 * scrolling there.
		 */

		/*
		 * Shortcut the most recurring case: the user is in the dragging state
		 * and he is moving his finger. We want to intercept this motion.
		 */
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE)
				&& (mTouchState != TOUCH_STATE_REST)) {
			if(Math.abs(mLastMotionX-ev.getX())<3||Math.abs(mLastMotionY-ev.getY())<3){
				return false;
			}
			return true;
		}

		acquireVelocityTrackerAndAddMovement(ev);

		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_MOVE: {
			/*
			 * mIsBeingDragged == false, otherwise the shortcut would have
			 * caught it. Check whether the user has moved far enough from his
			 * original down touch.
			 */

			/*
			 * Locally do absolute value. mLastMotionX is set to the y value of
			 * the down event.
			 */
			final int pointerIndex = ev.findPointerIndex(mActivePointerId);
			final float x = ev.getX(pointerIndex);
			final float y = ev.getY(pointerIndex);
			final int xDiff = (int) Math.abs(x - mLastMotionX);
			final int yDiff = (int) Math.abs(y - mLastMotionY);

			final int touchSlop = mTouchSlop;
			boolean xMoved = xDiff > touchSlop;
			boolean yMoved = yDiff > touchSlop;

			if (xMoved || yMoved) {

				if (xMoved) {
					// Scroll if the user moved far enough along the X axis
					mTouchState = TOUCH_STATE_SCROLLING;
					mLastMotionX = x;
					mTouchX = getScrollX();
					mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
					enableChildrenCache(mCurrentScreen - 1, mCurrentScreen + 1);
				}
				// Either way, cancel any pending longpress
				
			}
			break;
		}

		case MotionEvent.ACTION_DOWN: {
			final float x = ev.getX();
			final float y = ev.getY();
			// Remember location of down touch
			mLastMotionX = x;
			mLastMotionY = y;
			mActivePointerId = ev.getPointerId(0);

			/*
			 * If being flinged and user touches the screen, initiate drag;
			 * otherwise don't. mScroller.isFinished should be false when being
			 * flinged.
			 */
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_SCROLLING
					: TOUCH_STATE_REST;
			break;
		}

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:

			if (mTouchState != TOUCH_STATE_SCROLLING) {
				final CellLayout currentScreen = (CellLayout) getChildAt(mCurrentScreen);
				if (!currentScreen.lastDownOnOccupiedCell()) {
					getLocationOnScreen(mTempCell);
					// Send a tap to the wallpaper if the last down was on empty
					// space
					final int pointerIndex = ev
							.findPointerIndex(mActivePointerId);
					if (pointerIndex >= 0) {
						mWallpaperManager.sendWallpaperCommand(
								getWindowToken(), "android.wallpaper.tap",
								mTempCell[0] + (int) ev.getX(pointerIndex),
								mTempCell[1] + (int) ev.getY(pointerIndex), 0,
								null);
					}
				}
			}

			// Release the drag
			clearChildrenCache();
			mTouchState = TOUCH_STATE_REST;
			mActivePointerId = INVALID_POINTER;
			releaseVelocityTracker();
			break;

		case MotionEvent.ACTION_POINTER_UP:
			onSecondaryPointerUp(ev);
			break;
		}

		/*
		 * The only time we want to intercept motion events is if we are in the
		 * drag mode.
		 */
		return super.onInterceptTouchEvent(ev);
//				mTouchState != TOUCH_STATE_REST;
	}

	@SuppressWarnings("deprecation")
	private void onSecondaryPointerUp(MotionEvent ev) {
		final int pointerIndex = ev.getAction() >> MotionEvent.ACTION_POINTER_ID_SHIFT;
		final int pointerId = ev.getPointerId(pointerIndex);
		if (pointerId == mActivePointerId) {
			// This was our active pointer going up. Choose a new
			// active pointer and adjust accordingly.
			// TODO: Make this decision more intelligent.
			final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
			mLastMotionX = ev.getX(newPointerIndex);
			mLastMotionY = ev.getY(newPointerIndex);
			mActivePointerId = ev.getPointerId(newPointerIndex);
			if (mVelocityTracker != null) {
				mVelocityTracker.clear();
			}
		}
	}

	/**
	 * If one of our descendant views decides that it could be focused now, only
	 * pass that along if it's on the current screen.
	 * 
	 * This happens when live folders requery, and if they're off screen, they
	 * end up calling requestFocus, which pulls it on screen.
	 */
	@Override
	public void focusableViewAvailable(View focused) {
		View current = getChildAt(mCurrentScreen);
		View v = focused;
		while (true) {
			if (v == current) {
				super.focusableViewAvailable(focused);
				return;
			}
			if (v == this) {
				return;
			}
			ViewParent parent = v.getParent();
			if (parent instanceof View) {
				v = (View) v.getParent();
			} else {
				return;
			}
		}
	}

	void enableChildrenCache(int fromScreen, int toScreen) {
		if (fromScreen > toScreen) {
			final int temp = fromScreen;
			fromScreen = toScreen;
			toScreen = temp;
		}

		final int count = getChildCount();

		fromScreen = Math.max(fromScreen, 0);
		toScreen = Math.min(toScreen, count - 1);

		for (int i = fromScreen; i <= toScreen; i++) {
			final CellLayout layout = (CellLayout) getChildAt(i);
			layout.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
			layout.setChildrenDrawnWithCacheEnabled(true);
			layout.setChildrenDrawingCacheEnabled(true);
		}
	}

	void clearChildrenCache() {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final CellLayout layout = (CellLayout) getChildAt(i);
			layout.setChildrenDrawnWithCacheEnabled(false);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		/*
		 * if (mLauncher.isAllAppsVisible()) { // Cancel any scrolling that is
		 * in progress. if (!mScroller.isFinished()) {
		 * mScroller.abortAnimation(); } snapToScreen(mCurrentScreen); return
		 * false; // We don't want the events. Let them fall through to the all
		 * apps view. }
		 */

		acquireVelocityTrackerAndAddMovement(ev);

		final int action = ev.getAction();

		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			/*
			 * If being flinged and user touches, stop the fling. isFinished
			 * will be false if being flinged.
			 */
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}

			// Remember where the motion event started
			mLastMotionX = ev.getX();
			mActivePointerId = ev.getPointerId(0);
			if (mTouchState == TOUCH_STATE_SCROLLING) {
				enableChildrenCache(mCurrentScreen - 1, mCurrentScreen + 1);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (mTouchState == TOUCH_STATE_SCROLLING) {
				// Scroll to follow the motion event
				final int pointerIndex = ev.findPointerIndex(mActivePointerId);
				final float x = ev.getX(pointerIndex);
				final float deltaX = mLastMotionX - x;
				mLastMotionX = x;

				if (deltaX < 0) {
					if (mTouchX > 0) {
						mTouchX += Math.max(-mTouchX, deltaX);
						mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
						invalidate();
					} else if (Preferences.getInstance().getEndlessScrolling()
							&& mTouchX > -getWidth()) {
						mTouchX += deltaX;
						mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
						invalidate();
					}
				} else if (deltaX > 0) {
					final float availableToScroll = getChildAt(
							getChildCount() - 1).getRight()
							- mTouchX
							- (Preferences.getInstance().getEndlessScrolling() ? 0
									: getWidth());
					if (availableToScroll > 0) {
						mTouchX += Math.min(availableToScroll, deltaX);
						mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
						invalidate();
					}
				} else {
					awakenScrollBars();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mTouchState == TOUCH_STATE_SCROLLING) {
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
				final int velocityX = (int) velocityTracker.getXVelocity();

				final int screenWidth = getWidth();
				final int whichScreen = (int) Math
						.floor((getScrollX() + (screenWidth / 2.0))
								/ screenWidth);
				final float scrolledPos = (float) getScrollX() / screenWidth;
				if (velocityX > SNAP_VELOCITY
						&& (mCurrentScreen > (Preferences.getInstance()
								.getEndlessScrolling() ? -1 : 0))) {
					// Fling hard enough to move left.
					// Don't fling across more than one screen at a time.
					final int bound = scrolledPos < whichScreen ? mCurrentScreen - 1
							: mCurrentScreen;
					snapToScreen(Math.min(whichScreen, bound), velocityX, true);
				} else if (velocityX < -SNAP_VELOCITY
						&& mCurrentScreen < getChildCount()
								- (Preferences.getInstance()
										.getEndlessScrolling() ? 0 : 1)) {
					// Fling hard enough to move right
					// Don't fling across more than one screen at a time.
					final int bound = scrolledPos > whichScreen ? mCurrentScreen + 1
							: mCurrentScreen;
					snapToScreen(Math.max(whichScreen, bound), velocityX, true);
				} else {
					snapToScreen(whichScreen, 0, true);
				}
			}
			mTouchState = TOUCH_STATE_REST;
			mActivePointerId = INVALID_POINTER;
			releaseVelocityTracker();
			break;
		case MotionEvent.ACTION_CANCEL:
			if (mTouchState == TOUCH_STATE_SCROLLING) {
				final int screenWidth = getWidth();
				final int whichScreen = (getScrollX() + (screenWidth / 2))
						/ screenWidth;
				snapToScreen(whichScreen, 0, true);
			}
			mTouchState = TOUCH_STATE_REST;
			mActivePointerId = INVALID_POINTER;
			releaseVelocityTracker();
			break;
		case MotionEvent.ACTION_POINTER_UP:
			onSecondaryPointerUp(ev);
			break;
		}
		return true;
	}

	private void acquireVelocityTrackerAndAddMovement(MotionEvent ev) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);
	}

	private void releaseVelocityTracker() {
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}

	void snapToScreen(int whichScreen) {
		snapToScreen(whichScreen, 0, false);
	}

	private void snapToScreen(int whichScreen, int velocity, boolean settle) {
		// if (!mScroller.isFinished()) return;

		whichScreen = Math.max(
				(Preferences.getInstance().getEndlessScrolling() ? -1 : 0),
				Math.min(whichScreen, getChildCount()
						- (Preferences.getInstance().getEndlessScrolling() ? 0
								: 1)));
		clearVacantCache();
		enableChildrenCache(mCurrentScreen, whichScreen);

		mNextScreen = whichScreen;

//		updateIndicators(mNextScreen);

		View focusedChild = getFocusedChild();
		if (focusedChild != null && whichScreen != mCurrentScreen
				&& focusedChild == getChildAt(mCurrentScreen)) {
			focusedChild.clearFocus();
		}

		final int screenDelta = Math.max(1,
				Math.abs(whichScreen - mCurrentScreen));
		final int newX = whichScreen * getWidth();
		final int delta = newX - getScrollX();
		int duration = (screenDelta + 1) * 100;

		if (!mScroller.isFinished()) {
			mScroller.abortAnimation();
		}

		if (settle) {
			mScrollInterpolator.setDistance(screenDelta);
		} else {
			mScrollInterpolator.disableSettle();
		}

		velocity = Math.abs(velocity);
		if (velocity > 0) {
			duration += (duration / (velocity / BASELINE_FLING_VELOCITY))
					* FLING_VELOCITY_INFLUENCE;
		} else {
			duration += 100;
		}

		awakenScrollBars(duration);
		mScroller.startScroll(getScrollX(), 0, delta, 0, duration);
		invalidate();
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		final SavedState state = new SavedState(super.onSaveInstanceState());
		state.currentScreen = mCurrentScreen;
		return state;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		SavedState savedState = (SavedState) state;
		super.onRestoreInstanceState(savedState.getSuperState());
		if (savedState.currentScreen != -1) {
			mCurrentScreen = savedState.currentScreen;
			// Launcher.setScreen(mCurrentScreen);
		}
	}





	public void scrollLeft() {
		clearVacantCache();
		final int dest = Preferences.getInstance().getEndlessScrolling() ? -1
				: 0;
		if (mScroller.isFinished()) {
			if (mCurrentScreen > dest)
				snapToScreen(mCurrentScreen - 1);
		} else {
			if (mNextScreen > dest)
				snapToScreen(mNextScreen - 1);
		}
	}

	public void scrollRight() {
		clearVacantCache();
		final int dest = Preferences.getInstance().getEndlessScrolling() ? 0
				: 1;
		if (mScroller.isFinished()) {
			if (mCurrentScreen < getChildCount() - dest)
				snapToScreen(mCurrentScreen + 1);
		} else {
			if (mNextScreen < getChildCount() - dest)
				snapToScreen(mNextScreen + 1);
		}
	}

	public int getScreenForView(View v) {
		int result = -1;
		if (v != null) {
			ViewParent vp = v.getParent();
			int count = getChildCount();
			for (int i = 0; i < count; i++) {
				if (vp == getChildAt(i)) {
					return i;
				}
			}
		}
		return result;
	}

	public View getViewForTag(Object tag) {
		int screenCount = getChildCount();
		for (int screen = 0; screen < screenCount; screen++) {
			CellLayout currentScreen = ((CellLayout) getChildAt(screen));
			int count = currentScreen.getChildCount();
			for (int i = 0; i < count; i++) {
				View child = currentScreen.getChildAt(i);
				if (child.getTag() == tag) {
					return child;
				}
			}
		}
		return null;
	}

	
	void moveToDefaultScreen(boolean animate) {
		if (animate) {
			snapToScreen(mDefaultScreen);
		} else {
			setCurrentScreen(mDefaultScreen);
		}
		getChildAt(mDefaultScreen).requestFocus();
	}

	// void setIndicators(Drawable previous, Drawable next) {
	// mPreviousIndicator = previous;
	// mNextIndicator = next;
	// updateIndicators(mCurrentScreen);
	// }

/*	private void updateIndicators(int screen) {
		if (mIndicator != null)
			mIndicator.onSwitched(null, screen);
		// mPreviousIndicator.setLevel(screen);
		// mNextIndicator.setLevel(screen);
	}

	@Override
	protected void onScrollChanged(int h, int v, int oldh, int oldv) {
		super.onScrollChanged(h, v, oldh, oldv);
		if (mIndicator != null) {
			
			 * The actual horizontal scroll origin does typically not match the
			 * perceived one. Therefore, we need to calculate the perceived
			 * horizontal scroll origin here, since we use a view buffer.
			 
			int hPerceived = h + (mCurrentScreen)
					* getWidth();
			mIndicator.onScrolled(hPerceived, v, oldh, oldv);
		}
	}

	public FlowIndicator getIndicator() {
		return mIndicator;
	}

	public void setIndicator(FlowIndicator mIndicator) {
		this.mIndicator = mIndicator;
	}
*/
	public static class SavedState extends BaseSavedState {
		int currentScreen = -1;

		SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			currentScreen = in.readInt();
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeInt(currentScreen);
		}

		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}

}

class Preferences {
	static Preferences mInstance;

	public static Preferences getInstance() {
		if (mInstance == null)
			mInstance = new Preferences();
		return mInstance;
	}

	public boolean getEndlessScrolling() {
		return true;
	}
}
