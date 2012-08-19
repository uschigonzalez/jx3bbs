/**   
 * @Title: BrachDetailFragment.java
 * @Package com.android.xiaow.jx3bbs
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-12 上午10:59:10
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.xiaow.core.Controller;
import com.android.xiaow.core.Initializer;
import com.android.xiaow.core.common.IResponseListener;
import com.android.xiaow.core.common.Request;
import com.android.xiaow.core.common.Response;
import com.android.xiaow.core.util.ToastUtil;
import com.android.xiaow.jx3bbs.BranchListFragment.CallBack;
import com.android.xiaow.jx3bbs.cmds.ReplayRequest;
import com.android.xiaow.jx3bbs.model.Card;
import com.android.xiaow.jx3bbs.model.Cards;
import com.android.xiaow.jx3bbs.model.MainArea;
import com.android.xiaow.jx3bbs.model.RefuseInfo;
import com.android.xiaow.jx3bbs.widget.HTMLayout;
import com.android.xiaow.jx3bbs.widget.PushListView;
import com.android.xiaow.jx3bbs.widget.PushListView.OnRefreshListener;

/**
 * @ClassName: BrachDetailFragment
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-12 上午10:59:10
 * 
 */
public class BranchDetailFragment extends ListFragment implements BranchListActivityCallBack,
        OnRefreshListener, IResponseListener {
    public static String URL_ITEM_ID = "url_item";
    public static String NAME_TITEL_ITEM = "name_title";
    String url;
    String title;
    PushListView listView;
    int page = 1;
    int page_max;
    BranchListFragment.CallBack mCallBack;
    TextView title_TextView;
    CardAdapter mAdapter;
    View refuseView;
    Button refuse;
    Button mFastRefuse;
    Cards mCards;
    EditText et;
    JX3Application application = null;
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Controller.getIntance());
    RefuseInfo mInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(URL_ITEM_ID)) {
            url = getArguments().getString(URL_ITEM_ID);
        }
        if (getArguments().containsKey(NAME_TITEL_ITEM)) {
            title = getArguments().getString(NAME_TITEL_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.branch_detail_fragment, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof CallBack)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }
        mCallBack = (CallBack) activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView = (PushListView) getListView();
        refuse = (Button) getView().findViewById(R.id.button1);
        mFastRefuse = (Button) getView().findViewById(R.id.button2);
        refuseView = getView().findViewById(R.id.refuse);
        refuseView.setVisibility(View.GONE);
        et = (EditText) getView().findViewById(R.id.editText1);
        /**
         * 暂定方案
         */
        mFastRefuse.setVisibility(View.GONE);
        refuse.setVisibility(View.VISIBLE);
        refuse.setOnClickListener(mRefuseListener);
        listView.setonRefreshListener(this);
        listView.hideFoot();
        title_TextView = (TextView) getView().findViewById(R.id.textView1);
        if (!TextUtils.isEmpty(title)) {
            title_TextView.setText(title);
        }
        onReset();
        application = (JX3Application) getActivity().getApplication();
    }

    @Override
    public void onReset() {
        Cards cards = new Cards();
        cards.cards = new ArrayList<Card>();
        mAdapter = new CardAdapter(cards);
        setListAdapter(mAdapter);
        listView.hideFoot();
        listView.onFresh();
        mInfo = null;
    }

    @Override
    public void loadBranch(MainArea branch) {
    }

    @Override
    public void onRefresh() {
        listView.hideFoot();
        Request request = new Request();
        page = 1;
        request.url = url + "&" + BranchListFragment.PAGE_FIELD + "=" + page;
        Controller.getIntance().registerCommand(Initializer.CARD_CMD_ID, request, this);
    }

    @Override
    public void addMore() {
        Request request = new Request();
        page++;
        request.url = url + "&" + BranchListFragment.PAGE_FIELD + "=" + page;
        Controller.getIntance().registerCommand(Initializer.CARD_CMD_ID, request, this);
    }

    @Override
    public RefuseInfo getInfo() {
        return mInfo;
    }

    @Override
    public void onSuccess(Response response) {
        Cards cards = (Cards) response.result;
        if (listView.isrefresh()) {
            mAdapter = new CardAdapter(cards);
            setListAdapter(mAdapter);
        } else {
            mAdapter.addData(cards);
        }
        page = cards.cur_page;
        page_max = cards.max_page;
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
            replayRequest.Content = content + "\r\n\r\n\r\n" + getString(R.string.refuse_buff, Build.MODEL);
            Controller.getIntance().registerCommand(Initializer.REPLAY_CMD_ID, replayRequest,
                    refuseListener);
            refuse.setEnabled(false);
            et.setEnabled(false);
            getView().findViewById(R.id.progressBar1).setVisibility(View.VISIBLE);
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
            getView().findViewById(R.id.progressBar1).setVisibility(View.GONE);
        }

        @Override
        public void onError(Response response) {
            ToastUtil.show(response.errorMsg);
            refuse.setEnabled(true);
            et.setEnabled(true);
            getView().findViewById(R.id.progressBar1).setVisibility(View.GONE);
        }
    };

    @Override
    public void onError(Response response) {
        onLoadComplete();
        ToastUtil.show(response.errorMsg);
        page = Math.max(1, --page);
    }

    public void onLoadComplete() {
        if (mCallBack != null) {
            mCallBack.resetEnd();
        }
        listView.onAddMoreComplete();
        listView.onRefreshComplete();
    }

    class CardAdapter extends BaseAdapter {
        List<Card> data;

        public CardAdapter(Cards cards) {
            super();
            this.data = cards.cards;
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public Card getItem(int arg0) {
            return data.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(final int postion, View arg1, ViewGroup arg2) {
            if (arg1 == null) {
                arg1 = getActivity().getLayoutInflater().inflate(R.layout.carditem, null);
                Holder holder = new Holder();
                holder.v1 = (TextView) arg1.findViewById(R.id.textView1);
                holder.v2 = (TextView) arg1.findViewById(R.id.textView2);
                holder.v3 = (TextView) arg1.findViewById(R.id.textView3);
                holder.v5 = (TextView) arg1.findViewById(R.id.textView4);
                holder.v4 = (HTMLayout) arg1.findViewById(R.id.layout1);
                holder.m1 = (ImageView) arg1.findViewById(R.id.imageView1);
                // holder.m2 = (ImageView) arg1.findViewById(R.id.imageView2);
                arg1.setTag(holder);
            }
            final Holder holder = (Holder) arg1.getTag();
            holder.v1.setText(getItem(postion).author);
            holder.v2.setText(getItem(postion).leverl);
            if (holder.v2.getText().length() > 3) {
                String str = holder.v2.getText().toString().substring(3);
                if (!TextUtils.isDigitsOnly(str)) {
                    holder.v2.setVisibility(View.VISIBLE);
                    holder.v2.setTextColor(Color.RED);
                } else {
                    holder.v2.setVisibility(View.GONE);
                }
            } else {
                holder.v2.setVisibility(View.GONE);
            }
            holder.v5.setText((postion + 1) + "#");
            holder.position = postion;
            holder.m1.setImageResource(R.drawable.noavatar);
            String url = getItem(postion).image_au1;
            if (!TextUtils.isEmpty(url)
                    && !"http://jx3.bbs.xoyo.com/images/avatars/noavatar.gif".equals(url)
                    && !"jx3.bbs.xoyo.com/images/avatars/noavatar.gif".equals(url)) {

                AsyncImageLoad.LoadImage(getItem(postion).image_au1, AsyncImageLoad.GRAVATAR,
                        holder.m1, null);
            }
            // ImageViewObserver observer = new ImageViewObserver(holder.m1,
            // false);
            // SyncLoadImage.getIntance().LoadBitmap(getItem(postion).image_au1,
            // observer, SyncLoadImage.GRAVATAR);
            holder.m1.setFocusable(false);
            // holder.m1.setOnClickListener(new OnClickListener() {
            //
            // @Override
            // public void onClick(View v) {
            // if (state == ONLY_AUTHOR) {
            // subffix="";
            // state = ALL_AUTHOR;
            // pre_state = ONLY_AUTHOR;
            // Toast.makeText(getActivity(), "正在切换至查看所有楼层",
            // Toast.LENGTH_LONG).show();
            // } else {
            // subffix="&authorid="+ getItem(postion).authorid;
            // state = ONLY_AUTHOR;
            // pre_state = ALL_AUTHOR;
            // Toast.makeText(getActivity(),
            // "正在切换至只看该作者 ：" + getItem(postion).author,
            // Toast.LENGTH_LONG).show();
            // }
            // Log.d("BUG", cur_url);
            // reflash();
            // }
            // });
            holder.v5.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (holder.v4.getVisibility() == View.VISIBLE)
                        holder.v4.setVisibility(View.GONE);
                    else
                        holder.v4.setVisibility(View.VISIBLE);
                }
            });
            String str = getItem(postion).time;
            str = str.replace("src=\"image", "src=\"http://jx3.bbs.xoyo.com/image");
            str = str.replace("只看该作者", "");
            str = str.replace("|", "");
            str = str.replaceAll("<[^>]*>", "");
            Matcher matcher = Pattern.compile("发表于[\\s]*[0-9]+-[0-9]+-[0-9]+[\\s]*[0-9]+:[0-9]+")
                    .matcher(str);
            if (matcher.find()) {
                holder.v3.setText(matcher.group());
            }
            holder.v4.setActivity(getActivity());
            holder.v4.LoadHTML(getItem(postion).detail);
            return arg1;
        }

        public void changeData(Cards cards) {
            changeData(cards.cards);
        }

        public void changeData(List<Card> cards) {
            if (data != null)
                data.clear();
            data = cards;
            this.notifyDataSetChanged();
        }

        public void addData(Cards cards) {
            this.data.addAll(cards.cards);
            this.notifyDataSetChanged();
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
