package com.android.xiaow.jx3bbs;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.android.xiaow.core.BaseFragmentActivity;
import com.android.xiaow.core.Controller;
import com.android.xiaow.core.Initializer;
import com.android.xiaow.core.cmds.AbstractHttpCommand;
import com.android.xiaow.core.threads.Manager;
import com.android.xiaow.jx3bbs.activity.LoginActvity;
import com.android.xiaow.jx3bbs.activity.MainListActivity;

public class SplashActivity extends BaseFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Initializer.ensureInitialized();
        SharedPreferences sp = getSharedPreferences("Splash", MODE_PRIVATE);
        SharedPreferences sp1 = PreferenceManager.getDefaultSharedPreferences(Controller
                .getIntance());
        String cookies = sp1.getString("cookies", null);
        if (LoginActvity.isLogin()) {
            AbstractHttpCommand.set_cookie = cookies;
        }
        if (sp.getBoolean("first", true)) {
            Manager.getIntance().registerCommand(Initializer.INIT_CMD_ID);
        }
        sp.edit().putBoolean("first", false).commit();
        setContentView(R.layout.activity_splash);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                goNext();
            }
        }, 3000);
        // LogServiceUtil.StartService(this);
    }

    public void goNext() {
        startActivity(new Intent(this, MainListActivity.class));
        overridePendingTransition(R.anim.slide_left, R.anim.zoom_out);
        finish();
    }
}
