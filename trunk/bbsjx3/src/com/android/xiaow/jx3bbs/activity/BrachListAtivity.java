/**   
 * @Title: BrachListAtivity.java
 * @Package com.android.xiaow.jx3bbs.activity
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-9-6 下午8:51:27
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.xiaow.core.Controller;
import com.android.xiaow.core.Initializer;
import com.android.xiaow.core.common.IResponseListener;
import com.android.xiaow.core.common.Response;
import com.android.xiaow.core.util.ToastUtil;
import com.android.xiaow.jx3bbs.R;
import com.android.xiaow.jx3bbs.cmds.BranchRequest;
import com.android.xiaow.jx3bbs.cmds.BranchResponse;
import com.android.xiaow.jx3bbs.model.Bernda;
import com.android.xiaow.jx3bbs.model.MainArea;
import com.android.xiaow.jx3bbs.model.RefuseInfo;
import com.android.xiaow.jx3bbs.widget.PushListView;

/**
 * @ClassName: BrachListAtivity
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-9-6 下午8:51:27
 * 
 */
public class BrachListAtivity extends BaseBranchActivity implements PushListView.OnRefreshListener,
        IResponseListener, OnItemClickListener {
    PushListView mListView;
    int page = 1;
    int page_max;
    BranchListAdapter mAdapter;
    List<Bernda> noties = new ArrayList<Bernda>();

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.branch_list_fragment);
        mListView = (PushListView) findViewById(android.R.id.list);
        mListView.setonRefreshListener(this);
        mListView.hideFoot();
        menuRefresh();
        mAdapter = new BranchListAdapter(new ArrayList<Bernda>(), this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void navigationSelected(MainArea mainArea) {
        mAdapter.clear();
        cur_Branch = mainArea;
        menuRefresh();
    }

    @Override
    public void menuRefresh() {
        mListView.onFresh();
    }

    @Override
    public RefuseInfo getInfo() {
        return mInfo;
    }

    BranchRequest request;
    BranchResponse response;

    @Override
    public void onRefresh() {
        page = 1;
        mListView.hideFoot();
        request = new BranchRequest();
        request.url = cur_Branch.url + "&" + PAGE_FIELD + "=" + page;
        request.parent = cur_Branch.name;
        Controller.getIntance().registerCommand(Initializer.BRANCH_LIST_CMD_ID, request, this);
    }

    @Override
    public void addMore() {
        page++;
        request = new BranchRequest();
        request.url = cur_Branch.url + "&" + PAGE_FIELD + "=" + page;
        request.parent = cur_Branch.name;
        Controller.getIntance().registerCommand(Initializer.BRANCH_LIST_CMD_ID, request, this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        if (view.getTag() instanceof BranchListAdapter.Holder && (position > 0)
                && position <= mAdapter.getCount()) {
            Intent intent = new Intent(this, BranchDetailActivity.class);
            intent.putExtra(BranchDetailActivity.NAME_TITEL_ITEM,
                    mAdapter.getItem(position - 1).name);
            intent.putExtra(BRANCH_NAME, cur_Branch.name);
            intent.putExtra(PARENT_BRANCH, cur_Branch.parent);
            intent.putExtra(BranchDetailActivity.URL_ITEM_ID, mAdapter.getItem(position - 1).url);
            startActivityForResult(intent, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == BranchDetailActivity.RESULT_OK & data != null
                && data.hasExtra("MAIN_AREA")) {
            cur_Branch = (MainArea) data.getSerializableExtra("MAIN_AREA");
            menuRefresh();
        }
    }

    @Override
    public void onSuccess(Response resp) {
        response = (BranchResponse) resp;
        for (Bernda bernda : response.berndas) {
            if (bernda.type == 0) {
                noties.add(bernda);
            }
        }
        response.berndas.removeAll(noties);
        if (mListView.isrefresh()) {
            mAdapter.changeData(response.berndas);
        } else
            mAdapter.addMore(response.berndas);
        page = response.cur_page;
        page_max = response.max_page;
        if (page < page_max) {
            mListView.showFoot();
        } else {
            mListView.hideFoot();
        }

        mListView.onAddMoreComplete();
        mListView.onRefreshComplete();
        onMenuItemFreshFinish();
        mInfo = new RefuseInfo();
        mInfo.formhash = response.formhash;
        mInfo.fid = response.fid;
        mInfo.gid = response.gid;
        mInfo.tid = response.tid;
    }

    @Override
    public void onError(Response response) {
        mListView.onAddMoreComplete();
        mListView.onRefreshComplete();
        onMenuItemFreshFinish();
        page = Math.max(1, --page);
        ToastUtil.show(response.errorMsg);
    }

    @Override
    public void newThreadFinish(Intent data) {
        menuRefresh();
    }

}
