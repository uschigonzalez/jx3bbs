/**   
 * @Title: BranchDetailActivity.java
 * @Package com.android.xiaow.jx3bbs.activity
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-9-6 下午9:38:00
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.xiaow.core.Controller;
import com.android.xiaow.core.Initializer;
import com.android.xiaow.core.common.IResponseListener;
import com.android.xiaow.core.common.Request;
import com.android.xiaow.core.common.Response;
import com.android.xiaow.core.util.ToastUtil;
import com.android.xiaow.jx3bbs.AsyncImageLoad;
import com.android.xiaow.jx3bbs.JX3Application;
import com.android.xiaow.jx3bbs.R;
import com.android.xiaow.jx3bbs.cmds.ReplayRequest;
import com.android.xiaow.jx3bbs.db.RefuseDB;
import com.android.xiaow.jx3bbs.model.Card;
import com.android.xiaow.jx3bbs.model.Cards;
import com.android.xiaow.jx3bbs.model.MainArea;
import com.android.xiaow.jx3bbs.model.Refuse;
import com.android.xiaow.jx3bbs.model.RefuseInfo;
import com.android.xiaow.jx3bbs.widget.HTMLayout;
import com.android.xiaow.jx3bbs.widget.PushListView;
import com.android.xiaow.jx3bbs.widget.PushListView.OnRefreshListener;

/**
 * @ClassName: BranchDetailActivity
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-9-6 下午9:38:00
 * 
 */
public class BranchDetailActivity extends BaseBranchActivity implements IResponseListener,
        OnRefreshListener {
    public static final int RESULT_OK = 102;
    public static String URL_ITEM_ID = "url_item";
    public static String NAME_TITEL_ITEM = "name_title";
    String url;
    String title;
    PushListView listView;
    int page = 0;
    int page_max;
    TextView title_TextView;
    CardAdapter mAdapter;
    View refuseView;
    Button refuse;
    ImageView mFastRefuse;
    Cards mCards;
    AutoCompleteTextView et;
    JX3Application application = null;
    RefuseDB db = new RefuseDB();

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.branch_detail_fragment);
        url = getIntent().getStringExtra(URL_ITEM_ID);
        title = getIntent().getStringExtra(NAME_TITEL_ITEM);
        listView = (PushListView) findViewById(android.R.id.list);
        refuse = (Button) findViewById(R.id.button1);
        mFastRefuse = (ImageView) findViewById(R.id.button2);
        refuseView = findViewById(R.id.refuse);
        refuseView.setVisibility(View.GONE);
        et = (AutoCompleteTextView) findViewById(R.id.editText1);
        et.setOnFocusChangeListener(onFocusChangeListener);
        et.setAdapter(new ArrayAdapter<Refuse>(et.getContext(), R.layout.simple_list_item_1, db
                .getAll()));
        et.setThreshold(1);
        et.setCompletionHint("最近的5条记录");
        /**
         * 暂定方案
         */
        // mFastRefuse.setVisibility(View.GONE);
        refuse.setVisibility(View.VISIBLE);
        refuse.setOnClickListener(mRefuseListener);
        mAdapter = new CardAdapter(this, new ArrayList<Card>());
        listView.setonRefreshListener(this);
        listView.setAdapter(mAdapter);
        listView.hideFoot();
        title_TextView = (TextView) findViewById(R.id.textView1);
        if (!TextUtils.isEmpty(title)) {
            title_TextView.setText(title);
        }
        refuseView.findViewById(R.id.layout1).setVisibility(View.GONE);
        menuRefresh();
        application = (JX3Application) getApplication();
    }

    @Override
    public void onRefresh() {
        listView.hideFoot();
        Request request = new Request();
        if (page == 0)
            page = 1;
        else {
            page++;
            if (page > page_max)
                page = page_max;
        }
        request.url = url + "&" + PAGE_FIELD + "=" + page;
        Controller.getIntance().registerCommand(Initializer.CARD_CMD_ID, request, this);
    }

    @Override
    public void addMore() {
        Request request = new Request();
        page++;
        if (page > page_max)
            page = page_max;
        request.url = url + "&" + PAGE_FIELD + "=" + page;
        Controller.getIntance().registerCommand(Initializer.CARD_CMD_ID, request, this);
    }

    int pre_page = 0;

    @Override
    public void onSuccess(Response response) {
        Cards cards = (Cards) response.result;
        if (page > 1 && page == page_max) {
            int pre_count = mAdapter.getCount();
            List<Card> datas = new ArrayList<Card>();
            int count = pre_page * (page - 1);
            for (int i = 0; i < count; i++) {
                datas.add(mAdapter.getItem(i));
            }
            datas.addAll(cards.cards);
            mAdapter.changeData(datas);
            listView.setSelection(pre_count - 1);
        }/*
          * else if (listView.isrefresh()) { pre_page = cards.cards.size();
          * mAdapter.changeData(cards.cards); }
          */else if (cards.cur_page == 0 && cards.max_page == 0 && page_max == 0) {
            pre_page = cards.cards.size();
            int pre = mAdapter.getCount();
            mAdapter.changeData(cards.cards);
            listView.setSelection(pre - 1);
        } else {
            pre_page = cards.cards.size();
            mAdapter.addMore(cards.cards);
        }
        if (cards.hasNextPage) {
            page = cards.cur_page;
            page_max = cards.max_page;
        } else {
            page = page_max;
        }
        if (page < page_max) {
            listView.showFoot();
        } else {
            listView.hideFoot();
        }
        onLoadComplete();
        Log.d("MSG", "formhash" + cards.formhash + ",subject" + cards.subject + ",usesig"
                + cards.usesig);
        mCards = cards;
        if (!TextUtils.isEmpty(sp.getString("nickname", ""))) {
            refuseView.setVisibility(View.VISIBLE);
        }
        mInfo = new RefuseInfo();
        mInfo.formhash = mCards.formhash;
        mInfo.subject = mCards.subject;
        mInfo.usesig = mCards.usesig;
        mInfo.fid = mCards.fid;
        mInfo.tid = mCards.tid;
        mInfo.gid = mCards.gid;
    }

    @Override
    public void onError(Response response) {
        onLoadComplete();
        ToastUtil.show(response.errorMsg);
        page = Math.max(1, --page);
    }

    public void onLoadComplete() {
        onMenuItemFreshFinish();
        listView.onAddMoreComplete();
        listView.onRefreshComplete();
    }

    PopupWindow window;
    Animation out;
    Animation in;
    View.OnClickListener mRefuseListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            String content = et.getText().toString();
            if (TextUtils.isEmpty(content) || content.getBytes().length < 10) {
                ToastUtil.show("回复内容不能少于10个字节！");
                return;
            }
            if (System.currentTimeMillis() - application.lastRefuse < 1000 * 30) {
                ToastUtil.show("对不起，您两次发表间隔少于 30 秒，请不要灌水");
                return;
            }
            ReplayRequest replayRequest = new ReplayRequest();
            replayRequest.url = "http://jx3.bbs.xoyo.com/post.php?action=reply&fid="
                    + mCards.fid
                    + "&tid="
                    + mCards.tid
                    + "&extra=page%3D1&replysubmit=yes&infloat=yes&handlekey=fastpost&inajax=1&local=undefined&inajax=1&local=undefined&inajax=1&local=undefined&inajax=1&local=undefined";
            Log.d("MSG", "url=" + replayRequest.url);
            replayRequest.formhash = mCards.formhash;
            replayRequest.subject = mCards.subject;
            replayRequest.usesig = mCards.usesig;
            replayRequest.Content = content + "\r\n\r\n\r\n"
                    + getString(R.string.refuse_buff, Build.MODEL);
            boolean isSign = sp.getBoolean("isSign", false);
            if (isSign) {
                String sign_url = sp.getString("sign_url", "");
                replayRequest.Content += "\r\n   \t  \r\n    \t   \r\n"
                        + "[img]http://pic.xoyo.com/bbs/images/default/sigline.gif[/img]"
                        + "\r\n   \t  \r\n" + "[img]" + sign_url + "[/img]";
            }
            Controller.getIntance().registerCommand(Initializer.REPLAY_CMD_ID, replayRequest,
                    refuseListener);
            refuse.setEnabled(false);
            et.setEnabled(false);
            out = AnimationUtils.loadAnimation(BranchDetailActivity.this, R.anim.slide_down_out);
            in = AnimationUtils.loadAnimation(BranchDetailActivity.this, R.anim.slide_up_in);
            out.setFillAfter(true);
            in.setFillAfter(true);
            refuseView.findViewById(R.id.layout1).setVisibility(View.VISIBLE);
            refuseView.findViewById(R.id.layout1).startAnimation(in);
            refuseView.findViewById(R.id.layout2).startAnimation(out);
            Refuse refuse = db.getRefuse(content);
            if (refuse == null) {
                refuse = new Refuse();
                refuse.content = content;
            }
            ++refuse.level;
            db.update(refuse);
            et.clearFocus();
            if (window != null && window.isShowing()) {
                window.dismiss();
            }
        }
    };

    IResponseListener refuseListener = new IResponseListener() {

        @Override
        public void onSuccess(Response response) {
            ToastUtil.show("回复成功");
            application.lastRefuse = System.currentTimeMillis();
            refuse.setEnabled(true);
            et.setEnabled(true);
            et.setText("");
            refuseView.findViewById(R.id.layout1).setVisibility(View.GONE);
            refuseView.findViewById(R.id.layout1).startAnimation(out);
            refuseView.findViewById(R.id.layout2).startAnimation(in);
            listView.showFoot();
            listView.onFreshMore();
        }

        @Override
        public void onError(Response response) {
            ToastUtil.show(response.errorMsg);
            refuse.setEnabled(true);
            et.setEnabled(true);
            refuseView.findViewById(R.id.layout1).setVisibility(View.GONE);
            refuseView.findViewById(R.id.layout1).startAnimation(out);
            refuseView.findViewById(R.id.layout2).startAnimation(in);
        }
    };

    @Override
    public void navigationSelected(MainArea mainArea) {
        Intent intent = new Intent();
        intent.putExtra("MAIN_AREA", mainArea);
        if (mainArea.name.equals(branch_name))
            return;
        setResult(RESULT_OK, intent);
        finish();
    }

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                et.showDropDown();
                if (window != null && window.isShowing()) {
                    window.dismiss();
                }
            }
        }
    };

    @Override
    public void menuRefresh() {
        listView.onFresh();
    }

    @Override
    public RefuseInfo getInfo() {
        return mInfo;
    }

    @Override
    public void newThreadFinish(Intent data) {
        finish();
    }

    class CardAdapter extends AbstractAdapter<Card> {

        public CardAdapter(Context context, List<Card> data) {
            super(context, data);
        }

        @Override
        public View CreateView(int position, View convertView, LayoutInflater inflater) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.carditem, null);
                Holder holder = new Holder();
                holder.v1 = (TextView) convertView.findViewById(R.id.textView1);
                holder.v2 = (TextView) convertView.findViewById(R.id.textView2);
                holder.v3 = (TextView) convertView.findViewById(R.id.textView3);
                holder.v5 = (TextView) convertView.findViewById(R.id.textView4);
                holder.v4 = (HTMLayout) convertView.findViewById(R.id.layout1);
                holder.m1 = (ImageView) convertView.findViewById(R.id.imageView1);
                // holder.m2 = (ImageView) arg1.findViewById(R.id.imageView2);
                convertView.setTag(holder);
            }
            final Holder holder = (Holder) convertView.getTag();
            holder.v1.setText(getItem(position).author);
            holder.v2.setText(getItem(position).leverl);
            if (holder.v2.getText().length() > 3) {
                String str = holder.v2.getText().toString().substring(3);
                if (!TextUtils.isEmpty(str)) {
                    holder.v2.setVisibility(View.VISIBLE);
                    holder.v2.setTextColor(Color.RED);
                } else {
                    holder.v2.setVisibility(View.GONE);
                }
            } else {
                holder.v2.setVisibility(View.GONE);
            }
            holder.v5.setText((position + 1) + "#");
            holder.position = position;
            holder.m1.setImageResource(R.drawable.noavatar);
            String url = getItem(position).image_au1;
            if (!TextUtils.isEmpty(url)
                    && !"http://jx3.bbs.xoyo.com/images/avatars/noavatar.gif".equals(url)
                    && !"jx3.bbs.xoyo.com/images/avatars/noavatar.gif".equals(url)) {
                AsyncImageLoad.LoadImage(getItem(position).image_au1, AsyncImageLoad.GRAVATAR,
                        holder.m1, null);
            }
            holder.m1.setFocusable(false);
            String str = getItem(position).time;
            str = str.replace("src=\"image", "src=\"http://jx3.bbs.xoyo.com/image");
            str = str.replace("只看该作者", "");
            str = str.replace("|", "");
            str = str.replaceAll("<[^>]*>", "");
            Matcher matcher = Pattern.compile("发表于[\\s]*[0-9]+-[0-9]+-[0-9]+[\\s]*[0-9]+:[0-9]+")
                    .matcher(str);
            if (matcher.find()) {
                holder.v3.setText(matcher.group());
            }
            holder.v4.setActivity(BranchDetailActivity.this);
            holder.v4.LoadHTML(getItem(position).detail);
            return convertView;
        }

        class Holder {
            TextView v1;
            TextView v2;
            TextView v5;
            ImageView m1;
            ImageView m2;
            TextView v3;
            HTMLayout v4;
            int position = -1;
        }
    }

}
