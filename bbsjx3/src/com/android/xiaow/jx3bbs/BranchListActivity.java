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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.android.xiaow.core.BaseFragmentActivity;
import com.android.xiaow.core.Controller;
import com.android.xiaow.core.cmds.AbstractHttpCommand;
import com.android.xiaow.core.util.NetUtil;
import com.android.xiaow.core.util.ToastUtil;
import com.android.xiaow.jx3bbs.db.MainBrachDB;
import com.android.xiaow.jx3bbs.model.MainArea;
import com.android.xiaow.jx3bbs.model.RefuseInfo;
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
    NewThreadFragment t_fragment;
    Button set_btn;
    Button login_btn;
    Button login_out_btn;

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
        loadNaviList();
        loadSetting();
        getSupportFragmentManager().addOnBackStackChangedListener(onBackStackChangedListener);
    }

    FragmentManager.OnBackStackChangedListener onBackStackChangedListener = new FragmentManager.OnBackStackChangedListener() {

        @Override
        public void onBackStackChanged() {
            if (fragment.isVisible()) {
                cur_Fragment = fragment;
                mCallBack = fragment;
            } else if (detailFragment != null && detailFragment.isVisible()) {
                mCallBack = detailFragment;
                cur_Fragment = detailFragment;
            }
        }
    };

    /** 加载设置页面的设置信息 */
    public void loadSetting() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Controller
                .getIntance());
        String nickname = sp.getString("nickname", null);
        String cookies = sp.getString("cookies", null);
        AbstractHttpCommand.set_cookie = null;
        if (LoginFragment.isLogin()) {
            AbstractHttpCommand.set_cookie = cookies;
            login_btn.setVisibility(View.GONE);
            login_out_btn.setText("注销   ：" + nickname);
            login_out_btn.setVisibility(View.VISIBLE);
        } else {
            login_btn.setVisibility(View.VISIBLE);
            login_out_btn.setVisibility(View.GONE);
        }
    }

    /** 跳转到设置页面 */
    View.OnClickListener setListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            slidingGroup.snapToDefault();
            startActivity(new Intent(BranchListActivity.this, PreferenceActivity.class));
            overridePendingTransition(R.anim.slide_left, R.anim.zoom_out);
        }
    };
    /** 设置页面登录按钮的监听 */
    View.OnClickListener loginListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!NetUtil.checkNet()) {
                ToastUtil.show("没有检查到网络连接");
                return;
            }
            slidingGroup.snapToDefault();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_left, R.anim.zoom_out, R.anim.slide_left,
                    R.anim.zoom_out);
            ft.hide(cur_Fragment);
            LoginFragment loginFragment = new LoginFragment();
            ft.add(R.id.item_list, loginFragment);
            ft.addToBackStack("login2");
            ft.commit();
            mCallBack = loginFragment;
        }
    };
    /** 设置页面注销按钮的监听 */
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
            login_out_btn.setVisibility(View.GONE);
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

    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Controller.getIntance());

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
        case R.id.new_thread:
            if (mCallBack == null || mCallBack.getInfo() == null)
                return true;
            if (System.currentTimeMillis() - ((JX3Application) getApplication()).lastRefuse < 30 * 1000) {
                ToastUtil.show("对不起，您两次发表间隔少于 30 秒，请不要灌水");
                return true;
            }
            if (TextUtils.isEmpty(sp.getString("nickname", ""))) {
                ToastUtil.show("对不起，请先登录");
                return true;
            }
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_left, R.anim.zoom_out, R.anim.slide_left,
                    R.anim.zoom_out);
            t_fragment = new NewThreadFragment();
            Bundle args = new Bundle();
            RefuseInfo info = mCallBack.getInfo();
            info.parent = cur_Branch.name;
            args.putSerializable("Info", info);
            t_fragment.setArguments(args);
            if (fragment.isVisible())
                ft.hide(fragment);
            else if (cur_Fragment != null && cur_Fragment.isVisible()) {
                ft.hide(cur_Fragment);
            }
            ft.add(R.id.item_list, t_fragment);
            ft.addToBackStack("new_thread");
            ft.commit();
            cur_Fragment = t_fragment;
            mCallBack = t_fragment;
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
        if (fragment != null && fragment.isVisible() && mCallBack instanceof BranchListFragment) {
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
            ((LinearLayout) findViewById(R.id.item_list)).removeAllViews();
            ft.replace(R.id.item_list, fragment);
            ft.commit();
        }
        cur_Fragment = fragment;
    }

    BranchDetailFragment detailFragment;

    /** 响应Fragment中的事件，以便于加载版块 */
    @Override
    public void itemSelected(String name, String url) {
        Bundle bundle = new Bundle();
        bundle.putString(BranchDetailFragment.NAME_TITEL_ITEM, name);
        bundle.putString(BranchDetailFragment.URL_ITEM_ID, url);
        detailFragment = new BranchDetailFragment();
        mCallBack = detailFragment;
        detailFragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_left, R.anim.zoom_out, R.anim.slide_left,
                R.anim.zoom_out);
        ft.add(R.id.item_list, detailFragment);
        ft.hide(fragment);
        ft.addToBackStack(null);
        ft.commit();
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
            getSupportFragmentManager().popBackStack();
            mCallBack = (BranchListActivityCallBack) cur_Fragment;
        }
        if (mCallBack instanceof NewThreadFragment) {
            navigationSelected(cur_Branch);
        }
        loadSetting();
        if (mCallBack != null) {
            mCallBack.onReset();
        }
    }

    @Override
    public void onBackPressed() {
        if (mCallBack instanceof LoginFragment) {
            if (mCallBack instanceof LoginFragment) {
                getSupportFragmentManager().popBackStack();
                mCallBack = (BranchListActivityCallBack) cur_Fragment;
            }
            if (mCallBack instanceof NewThreadFragment) {
                navigationSelected(cur_Branch);
            }
            return;
        }
        if (cur_Fragment instanceof BranchListFragment) {
            finish();
        } else
            super.onBackPressed();
    }

}
