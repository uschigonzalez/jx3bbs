/**   
 * @Title: PreferenceActivity.java
 * @Package com.android.xiaow.jx3bbs
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-11 下午5:38:53
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs;

import android.os.Bundle;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.android.xiaow.jx3bbs.db.MainBrachDB;

/**
 * @ClassName: PreferenceActivity
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-11 下午5:38:53
 * 
 */
public class PreferenceActivity extends SherlockPreferenceActivity {

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.setting, false);
        addPreferencesFromResource(R.xml.setting);
    }

}
