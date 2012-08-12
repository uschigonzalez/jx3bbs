/**   
 * @Title: MainListActivity.java
 * @Package com.android.xiaow.jx3bbs
 * @Description: 显示所有版块，循环横向划屏切换
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-11 下午10:55:12
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.xiaow.core.BaseFragmentActivity;
import com.android.xiaow.core.Controller;
import com.android.xiaow.core.Initializer;
import com.android.xiaow.core.common.IResponseListener;
import com.android.xiaow.core.common.Response;
import com.android.xiaow.core.util.ColorUtil;
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
public class MainListActivity extends BaseFragmentActivity implements IResponseListener {
    Workspace work;
    int[] textColor = null;
    int[] color = null;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.workspace);
        textColor = ColorUtil.generateTransitionalColor(0x00ff0000, 0x00000000, 12);
        color = new int[] { R.drawable.item_back0, R.drawable.item_back1, R.drawable.item_back2,
                R.drawable.item_back3, R.drawable.item_back4, R.drawable.item_back5,
                R.drawable.item_back6, R.drawable.item_back7, R.drawable.item_back8,
                R.drawable.item_back9, R.drawable.item_back10, R.drawable.item_back11 };
        work = (Workspace) findViewById(R.id.workspace);
        Controller.getIntance().registerCommand(Initializer.INIT_MAIN_BRACH_CMD_ID, null, this);
    }

    @SuppressWarnings("all")
    @Override
    public void onSuccess(Response response) {
        HashMap<String, List<MainArea>> map = (HashMap<String, List<MainArea>>) response.result;
        CellLayout cellLayout = null;
        String[] titles = map.keySet().toArray(new String[map.size()]);
        for (int i = 0; i < map.size(); i++) {

            if (i < work.getChildCount()) {
                cellLayout = (CellLayout) work.getChildAt(i);
            } else {
                cellLayout = (CellLayout) getLayoutInflater().inflate(R.layout.screen, null, false);
                work.addView(cellLayout);
            }

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

    @Override
    public void onError(Response response) {

    }

    public View buildView(MainArea mainArea, int position) {
        View view = getLayoutInflater().inflate(R.layout.areaitem, null);
        TextView tv1 = (TextView) view.findViewById(R.id.textView1);
        tv1.setText(mainArea.name);
        // TextView tv2 = (TextView) view.findViewById(R.id.textView2);
        // tv2.setText(mainArea.today + "");
        // int len = textColor.length;
        // int index = position % len;
        // if (index > -1 && index < len)
        // tv2.setTextColor(0xff000000 + textColor[index]);
        view.setBackgroundResource(color[position % color.length]);
        view.setOnClickListener(mItemClickListener);
        view.setTag(mainArea);
        return view;
    }

    View.OnClickListener mItemClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            MainArea mArea = (MainArea) v.getTag();
            Intent intent = new Intent(MainListActivity.this, BranchListActivity.class);
            intent.putExtra(BranchListActivity.BRANCH_NAME, mArea.name);
            intent.putExtra(BranchListActivity.PARENT_BRANCH, mArea.parent);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_left, R.anim.zoom_out);
        }
    };
}
