package com.android.xiaow.jx3bbs.widget;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.xiaow.jx3bbs.R;

public class PushListView extends ListView implements OnScrollListener {
    public final static int RELEASE_To_REFRESH = 0;
    public final static int PULL_To_REFRESH = 1;
    // 正在刷新
    public final static int REFRESHING = 2;
    // 刷新完成
    public final static int DONE = 3;
    public final static int LOADING = 4;

    private final static int RATIO = 3;
    private LayoutInflater inflater;
    private LinearLayout headView;
    private TextView tipsTextview;
    private TextView lastUpdatedTextView;
    private ImageView arrowImageView;
    private ProgressBar progressBar;

    private RotateAnimation animation;
    private RotateAnimation reverseAnimation;
    private boolean isRecored;
    protected int headContentWidth;
    protected int headContentHeight;
    private int startY;
    private int firstItemIndex;
    protected int state;
    private boolean isBack;
    private OnRefreshListener refreshListener;
    private boolean isRefreshable;
    private View footView;
    protected int footContentWidth;
    protected int footContentHeight;
    int i = 1;
    protected int foot_state = View.GONE;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm ");

    public PushListView(Context context) {
        super(context);
        init(context);
    }

    public PushListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setCacheColorHint(context.getResources().getColor(R.color.transparent));
        inflater = LayoutInflater.from(context);
        headView = (LinearLayout) inflater.inflate(R.layout.head, null);
        footView = inflater.inflate(R.layout.foot, null);
        arrowImageView = (ImageView) headView.findViewById(R.id.head_arrowImageView);
        arrowImageView.setMinimumWidth(70);
        arrowImageView.setMinimumHeight(50);
        progressBar = (ProgressBar) headView.findViewById(R.id.head_progressBar);
        tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);
        lastUpdatedTextView = (TextView) headView.findViewById(R.id.head_lastUpdatedTextView);
        setDividerHeight(0);
        measureView(headView);
        measureView(footView);
        headContentHeight = headView.getMeasuredHeight();
        headContentWidth = headView.getMeasuredWidth();
        footContentHeight = footView.getMeasuredHeight();
        footContentWidth = footView.getMeasuredWidth();
        footView.setVisibility(View.VISIBLE);
        headView.setPadding(0, -1 * headContentHeight, 0, 0);
        headView.invalidate();
        footView.invalidate();
        footView.setPadding(0, 0, 0, 0);
        addHeaderView(headView, null, false);
        addFooterView(footView, null, false);
        setOnScrollListener(this);

        animation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(250);
        animation.setFillAfter(true);

        reverseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseAnimation.setInterpolator(new LinearInterpolator());
        reverseAnimation.setDuration(200);
        reverseAnimation.setFillAfter(true);

        state = DONE;
        isRefreshable = false;
        footView.findViewById(R.id.textView2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                onFreshMore();
            }
        });
    }

    public void divider() {
        setDividerHeight(5);
        setDivider(getResources().getDrawable(R.color.color3));

    }

    public void onFreshMore() {
        if (refreshListener != null) {
            refreshListener.addMore();
            footView.findViewById(R.id.textView2).setVisibility(View.GONE);
            footView.findViewById(R.id.lin1).setVisibility(View.VISIBLE);
        }
    }

    public void onScroll(AbsListView arg0, int firstVisiableItem, int visibleItemCount,
            int totalItemCount) {
        firstItemIndex = firstVisiableItem;

        // Log.d("BUG", "firstVisiableItem + visibleItemCount="
        // + (firstVisiableItem + visibleItemCount));
        // Log.d("BUG", "totalItemCount=" + totalItemCount);
        // Log.d("BUG", "isUpfresh=" + isUpfresh);
    }

    public void onScrollStateChanged(AbsListView arg0, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {

            // Log.d("BUG", "isUpfresh=VISIBLE");
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (isRefreshable) {
            switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (firstItemIndex == 0 && !isRecored) {
                    isRecored = true;
                    startY = (int) event.getY();
                }
                break;
            case MotionEvent.ACTION_UP:

                if (state != REFRESHING && state != LOADING) {
                    if (state == DONE) {
                    }
                    if (state == PULL_To_REFRESH) {
                        state = DONE;
                        changeHeaderViewByState();
                    }
                    if (state == RELEASE_To_REFRESH) {
                        state = REFRESHING;
                        changeHeaderViewByState();
                        onRefresh();
                    }
                }
                isRecored = false;
                isBack = false;
                break;
            case MotionEvent.ACTION_MOVE:
                int tempY = (int) event.getY();
                if (!isRecored && firstItemIndex == 0) {
                    isRecored = true;
                    startY = tempY;
                }

                // if(tempY>startY){
                // footView.setPadding(0, 0, 0, -1 * headContentHeight
                // + (tempY - startY) / RATIO);
                // }else{
                // footView.setPadding(0, 0, 0, (tempY - startY) / RATIO
                // - headContentHeight);
                //
                // }

                if (state != REFRESHING && isRecored && state != LOADING) {
                    if (state == RELEASE_To_REFRESH) {
                        setSelection(0);
                        if (((tempY - startY) / RATIO < headContentHeight) && (tempY - startY) > 0) {
                            state = PULL_To_REFRESH;
                            changeHeaderViewByState();
                        } else if (tempY - startY <= 0) {
                            state = DONE;
                            changeHeaderViewByState();
                        }
                    }
                    if (state == PULL_To_REFRESH) {
                        setSelection(0);
                        if ((tempY - startY) / RATIO >= headContentHeight) {
                            state = RELEASE_To_REFRESH;
                            isBack = true;
                            changeHeaderViewByState();
                        } else if (tempY - startY <= 0) {
                            state = DONE;
                            changeHeaderViewByState();
                        }
                    }
                    if (state == DONE) {
                        if (tempY - startY > 0) {
                            state = PULL_To_REFRESH;
                            changeHeaderViewByState();
                        }
                    }
                    if (state == PULL_To_REFRESH) {
                        headView.setPadding(0, -1 * headContentHeight + (tempY - startY) / RATIO,
                                0, 0);
                    }
                    if (state == RELEASE_To_REFRESH) {
                        headView.setPadding(0, (tempY - startY) / RATIO - headContentHeight, 0, 0);
                    }
                }
                break;
            }
        }
        return super.onTouchEvent(event);
    }

    boolean flag = true;

    public void setFootVisiable(boolean flag) {
        this.flag = flag;
    }

    public boolean isrefresh() {
        return state == REFRESHING;
    }

    public void onFresh() {
        onFresh(true);
    }

    public void onFresh(boolean flag) {
        state = REFRESHING;
        if (flag)
            setSelection(0);
        headView.setPadding(0, headContentHeight, 0, 0);
        changeHeaderViewByState();
        onRefresh();
    }

    private void changeHeaderViewByState() {
        switch (state) {
        case RELEASE_To_REFRESH:
            arrowImageView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            tipsTextview.setVisibility(View.VISIBLE);
            lastUpdatedTextView.setVisibility(View.VISIBLE);
            arrowImageView.clearAnimation();
            arrowImageView.startAnimation(animation);
            tipsTextview.setText("请释放 刷新");
            break;
        case PULL_To_REFRESH:
            progressBar.setVisibility(View.GONE);
            tipsTextview.setVisibility(View.VISIBLE);
            lastUpdatedTextView.setVisibility(View.VISIBLE);
            arrowImageView.clearAnimation();
            arrowImageView.setVisibility(View.VISIBLE);
            if (isBack) {
                isBack = false;
                arrowImageView.clearAnimation();
                arrowImageView.startAnimation(reverseAnimation);
                tipsTextview.setText("isBack  is true ！！！");
            } else {
                tipsTextview.setText("isBack  is false ！！！");
            }

            break;
        case REFRESHING:
            headView.setPadding(0, 0, 0, 0);
            progressBar.setVisibility(View.VISIBLE);
            arrowImageView.clearAnimation();
            arrowImageView.setVisibility(View.GONE);
            tipsTextview.setText("正在加载中 ...");
            lastUpdatedTextView.setVisibility(View.VISIBLE);
            break;
        case DONE:
            headView.setPadding(0, -1 * headContentHeight, 0, 0);
            progressBar.setVisibility(View.GONE);
            arrowImageView.clearAnimation();
            arrowImageView.setImageResource(R.drawable.arrow);
            tipsTextview.setText("已经加载完毕 ");
            lastUpdatedTextView.setVisibility(View.VISIBLE);
            break;
        }
    }

    public void setonRefreshListener(OnRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
        isRefreshable = true;
    }

    public interface OnRefreshListener {
        /**
         * @Title: onRefresh
         * @Description: 刷新
         */
        public void onRefresh();

        /**
         * @Title: addMore
         * @Description: 加载更多
         */
        public void addMore();
    }

    public void onRefreshComplete() {
        state = DONE;
        lastUpdatedTextView.setText("已加载完成: " + sdf.format(new Date()));
        changeHeaderViewByState();

    }

    public boolean isShowFoot() {
        return foot_state == View.VISIBLE;
    }

    public void hideFoot() {
        foot_state = View.GONE;
        footView.setVisibility(View.GONE);
    }

    public void showFoot() {
        foot_state = View.VISIBLE;
        footView.setVisibility(View.VISIBLE);
        footView.findViewById(R.id.textView2).setVisibility(View.VISIBLE);
        footView.findViewById(R.id.lin1).setVisibility(View.GONE);
    }

    public void onAddMoreComplete() {
        if (foot_state != View.GONE) {
            footView.findViewById(R.id.textView2).setVisibility(View.VISIBLE);
            footView.findViewById(R.id.lin1).setVisibility(View.GONE);
            footView.setVisibility(View.VISIBLE);
        }
    }

    private void onRefresh() {
        if (refreshListener != null) {
            refreshListener.onRefresh();
        }
    }

    @SuppressWarnings("deprecation")
    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    public void setAdapter(BaseAdapter adapter) {
        lastUpdatedTextView.setText("this is in MyListView:" + sdf.format(new Date()));
        if (adapter.getCount() == 0) {
            footView.setVisibility(View.GONE);
        }
        adapter.registerDataSetObserver(new DataSetObserver() {

            @Override
            public void onChanged() {
                super.onChanged();
                if (getAdapter() != null) {
                    if (getAdapter().getCount() > 10) {
                        footView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onInvalidated() {
                super.onInvalidated();
                if (getAdapter() != null) {
                    if (getAdapter().getCount() > 10) {
                        footView.setVisibility(View.VISIBLE);
                    }
                }
            }

        });
        super.setAdapter(adapter);
    }
}