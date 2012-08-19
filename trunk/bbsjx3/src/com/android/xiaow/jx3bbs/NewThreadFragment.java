/**   
 * @Title: NewThreadFragment.java
 * @Package com.android.xiaow.jx3bbs
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-19 上午9:05:21
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.xiaow.core.Controller;
import com.android.xiaow.core.Initializer;
import com.android.xiaow.core.common.IResponseListener;
import com.android.xiaow.core.common.Response;
import com.android.xiaow.core.util.ToastUtil;
import com.android.xiaow.jx3bbs.cmds.ThreadRequest;
import com.android.xiaow.jx3bbs.db.SqliteConn;
import com.android.xiaow.jx3bbs.model.MainArea;
import com.android.xiaow.jx3bbs.model.RefuseInfo;

/**
 * @ClassName: NewThreadFragment
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-19 上午9:05:21
 * 
 */
public class NewThreadFragment extends Fragment implements BranchListActivityCallBack {

    LoginFinishCallBack mCallBack;
    RefuseInfo mInfo;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof LoginFinishCallBack)) {
            throw new IllegalStateException(
                    "Activity must implement fragment's LoginFinishCallBack.");
        }
        mCallBack = (LoginFinishCallBack) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Object obj = getArguments().getSerializable("Info");
        if (obj instanceof RefuseInfo) {
            mInfo = (RefuseInfo) obj;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.newthread_fragment, null, false);
    }

    Spinner spinner;
    EditText title;
    EditText content;
    Button btn;
    String type = "0";
    List<String[]> datas;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        spinner = (Spinner) getView().findViewById(R.id.spinner1);
        title = (EditText) getView().findViewById(R.id.editText1);
        content = (EditText) getView().findViewById(R.id.editText2);
        btn = (Button) getView().findViewById(R.id.button1);
        btn.setOnClickListener(replayListener);
        SQLiteDatabase db = SqliteConn.getDatabase();
        Cursor cursor = db.query("type", null, "parent like \"%" + mInfo.parent + "%\"", null,
                null, null, null);
        datas = new ArrayList<String[]>();
        while (!cursor.isAfterLast()) {
            if (cursor.getPosition() < 0)
                cursor.moveToFirst();
            if (cursor.isAfterLast())
                break;
            String[] str = new String[2];
            str[0] = cursor.getString(cursor.getColumnIndex("name"));
            str[1] = cursor.getString(cursor.getColumnIndex("value"));
            datas.add(str);
            cursor.moveToNext();
        }
        cursor.close();
        spinner.setAdapter(new SpinnerAdapter(datas));
        if (datas.size() > 0)
            type = datas.get(spinner.getSelectedItemPosition())[1];

    }

    @Override
    public void onReset() {
    }

    @Override
    public void loadBranch(MainArea branch) {

    }

    @Override
    public RefuseInfo getInfo() {
        return null;
    }

    View.OnClickListener replayListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (TextUtils.isEmpty(title.getText().toString())) {
                ToastUtil.show("标题不能为空");
                return;
            }
            String con = content.getText().toString();
            if (con.getBytes().length < 10 || con.getBytes().length > 1000) {
                ToastUtil.show("内容不能低于10个字节，大于1000个字节");
                return;
            }
            if (datas.size() > 0)
                type = datas.get(spinner.getSelectedItemPosition())[1];
            ThreadRequest request = new ThreadRequest();
            request.url = "http://jx3.bbs.xoyo.com/post.php?&action=newthread&fid=" + mInfo.fid
                    + "&extra=&topicsubmit=yes";
            request.Content = con + "\r\n\r\n\r\n" + getString(R.string.refuse_buff, Build.MODEL);
            ;
            request.subject = title.getText().toString();
            request.typeid = type;
            request.formhash = mInfo.formhash;
            getView().setEnabled(false);
            Controller.getIntance().registerCommand(Initializer.THREAD_CMD_ID, request, listener);
            showDialog();
        }
    };

    public class SpinnerAdapter extends BaseAdapter {
        List<String[]> datas;

        public SpinnerAdapter(List<String[]> datas) {
            super();
            this.datas = datas;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public String[] getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(
                        android.R.layout.simple_list_item_1, null, false);
            }
            TextView tv = (TextView) convertView;
            tv.setText(getItem(position)[0]);
            return convertView;
        }

    }

    IResponseListener listener = new IResponseListener() {

        @Override
        public void onSuccess(Response response) {
            ToastUtil.show("发帖成功");
            hideDialog();
            if (mCallBack != null)
                mCallBack.loginFinish();
        }

        @Override
        public void onError(Response response) {
            getView().setEnabled(true);
            ToastUtil.show("发帖失败");
            hideDialog();
        }
    };

    ProgressDialog dialog;

    public void showDialog() {
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("正在发帖...");
        dialog.show();
    }

    public void hideDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.show();
        }
    }
}
