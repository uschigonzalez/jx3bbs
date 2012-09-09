/**   
 * @Title: MainListActivity.java
 * @Package com.android.xiaow.jx3bbs
 * @Description: 显示所有版块，循环横向划屏切换
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-11 下午10:55:12
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs.activity;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.xiaow.core.BaseFragmentActivity;
import com.android.xiaow.core.service.LogServiceUtil;
import com.android.xiaow.core.util.ColorUtil;
import com.android.xiaow.jx3bbs.R;
import com.android.xiaow.jx3bbs.db.MainBrachDB;
import com.android.xiaow.jx3bbs.model.MainArea;
import com.android.xiaow.jx3bbs.widget.CellLayout;
import com.android.xiaow.jx3bbs.widget.Workspace;

/**
 * @ClassName: MainListActivity
 * @Description: 显示所有版块，循环横向划屏切换
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-11 下午10:55:12
 * 
 */
public class MainListActivity extends BaseFragmentActivity {
    Workspace work;
    int[] textColor = null;
    int[] color = null;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.workspace);
        textColor = ColorUtil.generateTransitionalColor(0x00ff0000, 0x00000000, 12);
        color = new int[] { R.color.color0, R.color.color1, R.color.color2, R.color.color3,
                R.color.color4, R.color.color5, R.color.color6, R.color.color7, R.color.color8,
                R.color.color9 };
        work = (Workspace) findViewById(R.id.workspace);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public HashMap<String, List<MainArea>> map;

    public void init() {
        map = new MainBrachDB().getAreaByParent();
        CellLayout cellLayout = null;
        String[] titles = map.keySet().toArray(new String[map.size()]);
        for (int i = 0; i < map.size(); i++) {
            if (i < work.getChildCount()) {
                cellLayout = (CellLayout) work.getChildAt(i);
            } else {
                cellLayout = (CellLayout) getLayoutInflater().inflate(R.layout.screen, null, false);
                work.addView(cellLayout);
            }
            cellLayout.removeAllViews();
            List<MainArea> mainAreas = map.get(titles[i]);
            Collections.sort(mainAreas);
            int step = mainAreas.size();
            textColor = ColorUtil.generateTransitionalColor(0x00ff0000, 0x00000000,
                    Math.max(step, 3));
            for (int j = 0; j < mainAreas.size(); j++) {
                MainArea mainArea = mainAreas.get(j);
                View view = buildView(mainArea, j);
                int[] vacant = new int[2];
                if (cellLayout.getVacantCell(vacant, 2, 1)) {
                    work.addInScreen(view, i, vacant[0], vacant[1], 2, 1);
                }
            }
        }
    }

    public View buildView(MainArea mainArea, int position) {
        View view = getLayoutInflater().inflate(R.layout.areaitem, null);
        TextView tv1 = (TextView) view.findViewById(R.id.textView1);
        tv1.setText(mainArea.name);
        view.setBackgroundResource(color[position % color.length]);
        view.setOnClickListener(mItemClickListener);
        view.setTag(mainArea);
        return view;
    }

    View.OnClickListener mItemClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            MainArea mArea = (MainArea) v.getTag();
            Intent intent = new Intent(MainListActivity.this, BrachListAtivity.class);
            intent.putExtra(BrachListAtivity.BRANCH_NAME, mArea.name);
            intent.putExtra(BrachListAtivity.PARENT_BRANCH, mArea.parent);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_left, R.anim.zoom_out);
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LogServiceUtil.stopService(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        init();
    }

}
