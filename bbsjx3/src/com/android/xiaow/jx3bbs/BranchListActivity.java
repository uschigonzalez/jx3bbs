/**   
 * @Title: BrachListActivity.java
 * @Package com.android.xiaow.jx3bbs
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-12 上午10:59:50
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs;

import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.android.xiaow.core.BaseFragmentActivity;
import com.android.xiaow.core.Controller;
import com.android.xiaow.core.cmds.AbstractHttpCommand;
import com.android.xiaow.jx3bbs.db.MainBrachDB;
import com.android.xiaow.jx3bbs.model.MainArea;
import com.android.xiaow.jx3bbs.widget.SlidingGroup;

/**
 * @ClassName: BrachListActivity
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-12 上午10:59:50
 * 
 */
public class BranchListActivity extends BaseFragmentActivity implements
        BranchListFragment.CallBack, LoginFinishCallBack {
    public static final String PARENT_BRANCH = "parent_branch";
    public static final String BRANCH_NAME = "branch_name";
    ActionBar actionBar;
    String branch_name;
    String parent_branch;
    MainBrachDB db;
    List<MainArea> mBranches;
    MainArea cur_Branch;
    BranchListActivityCallBack mCallBack;
    Fragment cur_Fragment;
    SlidingGroup slidingGroup;
    MenuItem item;
    BranchListFragment fragment;

    Button set_btn;
    Button login_btn;
    Button login_out_btn;
    View login_out;
    TextView nick_name;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.branch_list_activity);
        slidingGroup = (SlidingGroup) findViewById(R.id.SlidingGroup);
        branch_name = getIntent().getStringExtra(BRANCH_NAME);
        parent_branch = getIntent().getStringExtra(PARENT_BRANCH);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        set_btn = (Button) findViewById(R.id.button1);
        set_btn.setOnClickListener(setListener);
        login_btn = (Button) findViewById(R.id.button2);
        login_btn.setOnClickListener(loginListener);
        login_out_btn = (Button) findViewById(R.id.button3);
        login_out_btn.setOnClickListener(loginOutListener);
        nick_name = (TextView) findViewById(R.id.textView1);
        login_out = findViewById(R.id.loginOut);
        loadNaviList();
        loadSetting();
    }

    public void loadSetting() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Controller
                .getIntance());
        String nickname = sp.getString("nickname", null);
        String cookies = sp.getString("cookies", null);
        AbstractHttpCommand.set_cookie = null;
        if (LoginFragment.isLogin()) {
            nick_name.setText(nickname);
            AbstractHttpCommand.set_cookie = cookies;
            login_btn.setVisibility(View.GONE);
            login_out.setVisibility(View.VISIBLE);
        } else {
            login_btn.setVisibility(View.VISIBLE);
            login_out.setVisibility(View.GONE);
        }
    }

    View.OnClickListener setListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            slidingGroup.snapToDefault();
            startActivity(new Intent(BranchListActivity.this, PreferenceActivity.class));
            overridePendingTransition(R.anim.slide_left, R.anim.zoom_out);
        }
    };
    View.OnClickListener loginListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            slidingGroup.snapToDefault();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_left, R.anim.zoom_out, R.anim.slide_left,
                    R.anim.zoom_out);
            ft.hide(cur_Fragment);
            LoginFragment loginFragment = new LoginFragment();
            ft.add(R.id.item_list, loginFragment);
            ft.commit();
            mCallBack = loginFragment;
        }
    };
    View.OnClickListener loginOutListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Editor editor = PreferenceManager.getDefaultSharedPreferences(Controller.getIntance())
                    .edit();
            AbstractHttpCommand.set_cookie = "";
            editor.putString("nickname", "");
            editor.putString("cookies", "");
            editor.commit();
            slidingGroup.snapToDefault();
            login_btn.setVisibility(View.VISIBLE);
            login_out.setVisibility(View.GONE);
            if (mCallBack != null) {
                mCallBack.onReset();
            }
        }
    };

    /** 创建ActionBar上的Menu菜单 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /** 监听ActionBar上的Menu菜单按钮 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_refresh:
            this.item = item;
            item.setActionView(R.layout.indeterminate_progress_action);
            if (mCallBack != null) {
                mCallBack.onReset();
            }
            break;
        case android.R.id.home:
            slidingGroup.snapToNext();
            break;
        }
        return true;
    }

    /** 添加ActionBar的Branch列表 */
    public void loadNaviList() {
        db = new MainBrachDB();
        mBranches = db.getAreasByParent(parent_branch);
        actionBar.setListNavigationCallbacks(new NavigactionAdapter(), mNavigationListener);
        for (MainArea area : mBranches) {
            if (area.name.equals(branch_name)) {
                cur_Branch = area;
                actionBar.setSelectedNavigationItem(mBranches.indexOf(area));
                break;
            }
        }
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

    /** 响应ActionBar上的下拉列表选择，切换版块 */
    public void navigationSelected(MainArea mArea) {
        if (fragment != null) {
            mCallBack = fragment;
            fragment.loadBranch(mArea);
        } else {
            fragment = new BranchListFragment();
            mCallBack = fragment;
            Bundle bundle = new Bundle();
            bundle.putParcelable(BranchListFragment.BRANCH, mArea);
            fragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_left, R.anim.zoom_out, R.anim.slide_left,
                    R.anim.zoom_out);
            ft.replace(R.id.item_list, fragment);
            ft.commit();
        }
        cur_Fragment = fragment;
    }

    /** 响应Fragment中的事件，以便于加载版块 */
    @Override
    public void itemSelected(String name, String url) {
        Bundle bundle = new Bundle();
        bundle.putString(BranchDetailFragment.NAME_TITEL_ITEM, name);
        bundle.putString(BranchDetailFragment.URL_ITEM_ID, url);
        BranchDetailFragment detailFragment = new BranchDetailFragment();
        mCallBack = detailFragment;
        detailFragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_left, R.anim.zoom_out, R.anim.slide_left,
                R.anim.zoom_out);
        ft.add(R.id.item_list, detailFragment);
        ft.hide(fragment);
        ft.addToBackStack(null);
        ft.commit();
        cur_Fragment = detailFragment;
    }

    /** ActionBar上 下拉菜单的Adapter */
    class NavigactionAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mBranches.size();
        }

        @Override
        public MainArea getItem(int position) {
            return mBranches.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater()
                        .inflate(android.R.layout.simple_list_item_1, null);
            }
            TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
            tv.setText(getItem(position).name);
            return tv;
        }
    }

    @Override
    public void resetEnd() {
        if (item != null) {
            item.setActionView(null);
        }
    }

    @Override
    public void loginFinish() {
        if (mCallBack instanceof LoginFragment) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_left, R.anim.zoom_out, R.anim.slide_left,
                    R.anim.zoom_out);
            ft.remove((Fragment) mCallBack);
            ft.show(cur_Fragment);
            ft.commit();
            mCallBack = (BranchListActivityCallBack) cur_Fragment;
        }
        loadSetting();
        if (mCallBack != null) {
            mCallBack.onReset();
        }
    }

}
