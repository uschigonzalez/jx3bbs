/**   
 * @Title: BaseFragmentActivity.java
 * @Package com.android.xiaow.core
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-11 下午5:16:42
 * @version V1.0   
 */
package com.android.xiaow.core;

import android.content.Intent;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.android.xiaow.jx3bbs.R;
import com.baidu.mobstat.StatService;

/**
 * @ClassName: BaseFragmentActivity
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-11 下午5:16:42
 * 
 */
public class BaseFragmentActivity extends SherlockFragmentActivity {

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
        overridePendingTransition(R.anim.slide_left, R.anim.zoom_out);
    }

}
