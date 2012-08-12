/**   
 * @Title: BrachDetailActivity.java
 * @Package com.android.xiaow.jx3bbs
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-12 上午10:59:29
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs;

import android.os.Bundle;

import com.android.xiaow.core.BaseFragmentActivity;

/**
 * @ClassName: BrachDetailActivity
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-12 上午10:59:29
 * 
 */
public class BranchDetailActivity extends BaseFragmentActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.branch_detail_activity);
        if (arg0 == null) {
            BranchDetailFragment fragment = new BranchDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putString(BranchDetailFragment.NAME_TITEL_ITEM,
                    getIntent().getStringExtra(BranchDetailFragment.NAME_TITEL_ITEM));
            bundle.putString(BranchDetailFragment.URL_ITEM_ID,
                    getIntent().getStringExtra(BranchDetailFragment.URL_ITEM_ID));
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .setCustomAnimations(R.anim.slide_left, R.anim.zoom_out).commit();
        }
    }

}
