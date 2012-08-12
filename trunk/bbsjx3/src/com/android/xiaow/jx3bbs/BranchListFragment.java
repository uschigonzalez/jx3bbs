/**   
 * @Title: BrachListFragment.java
 * @Package com.android.xiaow.jx3bbs
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-12 上午10:57:40
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.xiaow.core.Controller;
import com.android.xiaow.core.Initializer;
import com.android.xiaow.core.common.IResponseListener;
import com.android.xiaow.core.common.Response;
import com.android.xiaow.jx3bbs.cmds.BranchRequest;
import com.android.xiaow.jx3bbs.cmds.BranchResponse;
import com.android.xiaow.jx3bbs.model.Bernda;
import com.android.xiaow.jx3bbs.model.MainArea;
import com.android.xiaow.jx3bbs.widget.PushListView;
import com.android.xiaow.jx3bbs.widget.PushListView.OnRefreshListener;

/**
 * @ClassName: BrachListFragment
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-12 上午10:57:40
 * 
 */
public class BranchListFragment extends ListFragment implements OnRefreshListener,
        BranchListActivityCallBack, IResponseListener {
    public static final String PAGE_FIELD = "page";
    public static final String BRANCH = "BRANCH";
    CallBack mCallBack;
    private int mActivatedPosition;
    PushListView listView;
    MainArea mBranch;
    int page = 1;
    int page_max;
    BranchListAdapter mAdapter;
    List<Bernda> noties = new ArrayList<Bernda>();

    public static interface CallBack {
        public void itemSelected(String name, String url);

        public void resetEnd();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.branch_list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                Arrays.asList("item1", "item2", "item3", "item4", "item5")));
        listView = (PushListView) getListView();
        listView.setonRefreshListener(this);
        listView.hideFoot();
        if (savedInstanceState == null && getArguments().containsKey(BRANCH)) {
            mBranch = (MainArea) getArguments().get(BRANCH);
            loadBranch(mBranch);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof CallBack)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }
        mCallBack = (CallBack) activity;
    }

    public void setActivateOnItemClick(boolean activateOnItemClick) {
        getListView().setChoiceMode(
                activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
    }

    public void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }
        mActivatedPosition = position;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (mCallBack != null && v.getTag() instanceof BranchListAdapter.Holder) {
            mCallBack.itemSelected(mAdapter.getItem(position - 1).name,
                    mAdapter.getItem(position - 1).url);
        }
    }

    BranchRequest request;
    BranchResponse response;

    @Override
    public void onRefresh() {
        // TODO:刷新的监听
        page = 1;
        listView.hideFoot();
        request = new BranchRequest();
        request.url = mBranch.url + "&" + PAGE_FIELD + "=" + page;
        request.parent = mBranch.name;
        Controller.getIntance().registerCommand(Initializer.BRANCH_LIST_CMD_ID, request, this);
    }

    @Override
    public void addMore() {
        // TODO:加载更多
        page++;
        request = new BranchRequest();
        request.url = mBranch.url + "&" + PAGE_FIELD + "=" + page;
        request.parent = mBranch.name;
        Controller.getIntance().registerCommand(Initializer.BRANCH_LIST_CMD_ID, request, this);
    }

    @Override
    public void onReset() {
        // TODO:reset
        listView.onFresh();
        listView.hideFoot();
        mAdapter = new BranchListAdapter(new ArrayList<Bernda>(), getActivity());
        setListAdapter(mAdapter);
    }

    @Override
    public void loadBranch(MainArea branch) {
        // TODO:加载版块
        mBranch = branch;
        onReset();
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
        if (listView.isrefresh()) {
            mAdapter = new BranchListAdapter(response.berndas, getActivity());
            setListAdapter(mAdapter);
        } else
            mAdapter.addData(response.berndas);
        page = response.cur_page;
        page_max = response.max_page;
        if (page < page_max) {
            listView.showFoot();
        } else {
            listView.hideFoot();
        }

        listView.onAddMoreComplete();
        listView.onRefreshComplete();
        if (mCallBack != null)
            mCallBack.resetEnd();
    }

    @Override
    public void onError(Response response) {
        listView.onAddMoreComplete();
        listView.onRefreshComplete();
        if (mCallBack != null)
            mCallBack.resetEnd();
        page = Math.max(1, --page);
    }

   

}
