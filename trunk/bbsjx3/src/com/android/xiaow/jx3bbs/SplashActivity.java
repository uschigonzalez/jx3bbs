package com.android.xiaow.jx3bbs;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.xiaow.core.BaseFragmentActivity;
import com.android.xiaow.core.Initializer;
import com.android.xiaow.core.service.LogServiceUtil;
import com.android.xiaow.core.threads.Manager;

public class SplashActivity extends BaseFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Initializer.ensureInitialized();
        SharedPreferences sp = getSharedPreferences("Splash", MODE_PRIVATE);
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
        LogServiceUtil.StartService(this);
    }

    public void goNext() {
        startActivity(new Intent(this, MainListActivity.class));
        overridePendingTransition(R.anim.slide_left, R.anim.zoom_out);
        finish();
    }
}
