/**   
 * @Title: NewThreadFragment.java
 * @Package com.android.xiaow.jx3bbs
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-19 上午9:05:21
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.xiaow.core.BaseFragmentActivity;
import com.android.xiaow.core.Controller;
import com.android.xiaow.core.Initializer;
import com.android.xiaow.core.common.IResponseListener;
import com.android.xiaow.core.common.Response;
import com.android.xiaow.core.util.ToastUtil;
import com.android.xiaow.jx3bbs.R;
import com.android.xiaow.jx3bbs.cmds.ThreadRequest;
import com.android.xiaow.jx3bbs.db.SqliteConn;
import com.android.xiaow.jx3bbs.model.RefuseInfo;

/**
 * @ClassName: NewThreadFragment
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-19 上午9:05:21
 * 
 */
public class NewThreadActivity extends BaseFragmentActivity {

    RefuseInfo mInfo;
    public static final int RESULT_OK = 101;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newthread_fragment);
        Object obj = getIntent().getSerializableExtra("Info");
        if (obj instanceof RefuseInfo) {
            mInfo = (RefuseInfo) obj;
        }
        init();
    }

    Spinner spinner;
    EditText title;
    EditText content;
    Button btn;
    String type = "0";
    List<String[]> datas;

    private void init() {
        spinner = (Spinner) findViewById(R.id.spinner1);
        title = (EditText) findViewById(R.id.editText1);
        content = (EditText) findViewById(R.id.editText2);
        btn = (Button) findViewById(R.id.button1);
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
        spinner.setAdapter(new SpinnerAdapter(this, datas));
        if (datas.size() > 0)
            type = datas.get(spinner.getSelectedItemPosition())[1];

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
            getWindow().getDecorView().setEnabled(false);
            Controller.getIntance().registerCommand(Initializer.THREAD_CMD_ID, request, listener);
            showDialog();
        }
    };

    public class SpinnerAdapter extends AbstractAdapter<String[]> {

        public SpinnerAdapter(Context context, List<String[]> data) {
            super(context, data);
        }

        @Override
        public View CreateView(int position, View convertView, LayoutInflater inflater) {
            if (convertView == null) {
                convertView = inflater.inflate(android.R.layout.simple_list_item_1, null, false);
            }
            TextView tv = (TextView) convertView;
            tv.setText(getItem(position)[0]);
            return convertView;
        }

    }

    IResponseListener listener = new IResponseListener() {

        @Override
        public void onSuccess(Response response) {
            setResult(RESULT_OK);
            ToastUtil.show("发帖成功");
            hideDialog();
            finish();
        }

        @Override
        public void onError(Response response) {
            getWindow().getDecorView().setEnabled(true);
            ToastUtil.show("发帖失败");
            hideDialog();
        }
    };

    ProgressDialog dialog;

    public void showDialog() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("正在发帖...");
        dialog.show();
    }

    public void hideDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.show();
        }
    }
}
