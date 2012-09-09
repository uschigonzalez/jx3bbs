/**   
 * @Title: BaseBranchActivity.java
 * @Package com.android.xiaow.jx3bbs.activity
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-9-6 下午8:27:59
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs.activity;

import java.util.List;

import android.app.ActionBar.OnNavigationListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.android.xiaow.core.BaseFragmentActivity;
import com.android.xiaow.core.util.ToastUtil;
import com.android.xiaow.jx3bbs.JX3Application;
import com.android.xiaow.jx3bbs.R;
import com.android.xiaow.jx3bbs.db.MainBrachDB;
import com.android.xiaow.jx3bbs.model.MainArea;
import com.android.xiaow.jx3bbs.model.RefuseInfo;

/**
 * @ClassName: BaseBranchActivity
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-9-6 下午8:27:59
 * 
 */
public abstract class BaseBranchActivity extends BaseFragmentActivity {
    public static final String PARENT_BRANCH = "parent_branch";
    public static final String BRANCH_NAME = "branch_name";
    public static final String PAGE_FIELD = "page";

    protected String branch_name;
    protected String parent_branch;
    protected MainBrachDB db;
    protected List<MainArea> mBranches;
    protected MainArea cur_Branch;
    protected RefuseInfo mInfo;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        branch_name = getIntent().getStringExtra(BRANCH_NAME);
        parent_branch = getIntent().getStringExtra(PARENT_BRANCH);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        loadNaviList();
    }

    MenuItem freshMenuItem;

    /** 创建ActionBar上的Menu菜单 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main_menu, menu);
        freshMenuItem = menu.findItem(R.id.menu_refresh);
        return true;
    }

    /** 添加ActionBar的Branch列表 */
    private void loadNaviList() {
        db = new MainBrachDB();
        mBranches = db.getAreasByParent(parent_branch);
        getActionBar().setListNavigationCallbacks(new NavigactionAdapter(this, mBranches),
                mNavigationListener);
        for (MainArea area : mBranches) {
            if (area.name.equals(branch_name)) {
                cur_Branch = area;
                getActionBar().setSelectedNavigationItem(mBranches.indexOf(area));
                break;
            }
        }
    }

    /** Actionbar上版块选择 */
    public abstract void navigationSelected(MainArea mainArea);

    /** actionBar上刷新按钮点击 */
    public abstract void menuRefresh();

    public abstract RefuseInfo getInfo();

    /** 发帖结束 */
    public abstract void newThreadFinish(Intent data);

    public void new_thread() {
        // TODO:跳转至发帖
        if (getInfo() == null) {
            ToastUtil.show("请刷新，或登陆后再试!");
        }
        if (System.currentTimeMillis() - ((JX3Application) getApplication()).lastRefuse < 30 * 1000) {
            ToastUtil.show("对不起，您两次发表间隔少于 30 秒，请不要灌水");
            return;
        }
        if (TextUtils.isEmpty(sp.getString("nickname", ""))) {
            ToastUtil.show("对不起，请先登录");
            startActivityForResult(new Intent(this, LoginActvity.class), 0);
            return;
        }
        Intent intent = new Intent(this, NewThreadActivity.class);
        intent.putExtra("Info", getInfo());
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == LoginActvity.RESULT_OK) {
            menuRefresh();
        } else if (resultCode == NewThreadActivity.RESULT_OK) {
            newThreadFinish(data);
        }
    }

    /** 监听ActionBar上的Menu菜单按钮 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_refresh:
            item.setActionView(R.layout.indeterminate_progress_action);
            menuRefresh();
            break;
        case R.id.new_thread:
            new_thread();
            break;
        case android.R.id.home:
            onBackPressed();
            break;
        }
        return true;
    }

    public void onMenuItemFreshFinish() {
        if (freshMenuItem != null)
            freshMenuItem.setActionView(null);
    }

    /** 切换版块监听 */
    OnNavigationListener mNavigationListener = new OnNavigationListener() {

        @Override
        public boolean onNavigationItemSelected(int itemPosition, long itemId) {
            cur_Branch = mBranches.get(itemPosition);
            navigationSelected(cur_Branch);
            return false;
        }
    };

    /** 版块的下拉列表 */
    public static class NavigactionAdapter extends AbstractAdapter<MainArea> {

        public NavigactionAdapter(Context context, List<MainArea> data) {
            super(context, data);
        }

        @Override
        public View CreateView(int position, View convertView, LayoutInflater inflater) {
            if (convertView == null) {
                convertView = inflater.inflate(android.R.layout.simple_list_item_1, null);
            }
            TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
            tv.setText(getItem(position).name);
            return tv;
        }

    }
}
