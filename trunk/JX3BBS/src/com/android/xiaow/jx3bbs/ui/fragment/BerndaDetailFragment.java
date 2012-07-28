/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.xiaow.jx3bbs.ui.fragment;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.android.xiaow.jx3bbs.CommandID;
import com.android.xiaow.jx3bbs.R;
import com.android.xiaow.jx3bbs.model.Card;
import com.android.xiaow.jx3bbs.model.Cards;
import com.android.xiaow.jx3bbs.ui.component.HTMLayout;
import com.android.xiaow.jx3bbs.ui.component.ImageViewObserver;
import com.android.xiaow.jx3bbs.ui.component.SyncLoadImage;
import com.android.xiaow.jx3bbs.ui.fragment.MyListView.OnRefreshListener;
import com.android.xiaow.mvc.Thread.ThreadPool;
import com.android.xiaow.mvc.common.IResponseListener;
import com.android.xiaow.mvc.common.Request;
import com.android.xiaow.mvc.common.Response;

/**
 * @ClassName: BerndaDetailFragment
 * @Description: 帖子内容详细查看
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-7-7 下午3:24:20
 * 
 */
@SuppressLint("all")
public class BerndaDetailFragment extends SherlockListFragment implements
		IResponseListener {

	private View mView;
	private float mWeight;
	private int marginLeft, marginRight, marginTop, marginBottom;
	MyListView pullListView;
	String header = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html;charset=gb2312\"></head><body>";
	String footer = "</body></html>";
	Hashtable<Integer, List<Card>> lists = new Hashtable<Integer, List<Card>>();
	CardAdapter mAdapter;
	String orgin_url;
	String cur_url;
	public int state = 0;
	public int pre_state = 0;
	public static final int ONLY_AUTHOR = 1;
	public static final int ALL_AUTHOR = 0;
	public String subffix="";
	// need a public empty constructor for framework to instantiate
	public BerndaDetailFragment() {

	}

	public BerndaDetailFragment(int colour, float weight, int margin_left,
			int margin_right, int margin_top, int margin_bottom) {
		mWeight = weight;
		marginLeft = margin_left;
		marginRight = margin_right;
		marginTop = margin_top;
		marginBottom = margin_bottom;
	}

	String url = "";

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title == null ? null : title.getText().toString();
	}

	TextView title;

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.cardlist, null);
		pullListView = (MyListView) mView.findViewById(android.R.id.list);
		pullListView.setonRefreshListener(HeadRefreshListener);
		mAdapter = new CardAdapter(new Cards());
		pullListView.setAdapter(mAdapter);
		// pullListView.addFooterView(inflater.inflate(
		// R.layout.indeterminate_progress_action, null));
		mView.setBackgroundColor(Color.TRANSPARENT);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
				LayoutParams.FILL_PARENT, mWeight);
		lp.setMargins(marginLeft, marginTop, marginRight, marginBottom);
		mView.setLayoutParams(lp);
		title = (TextView) mView.findViewById(R.id.textView1);
		return mView;
	}

	boolean update = false;
	int visiblePosition;
	OnRefreshListener HeadRefreshListener = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			visiblePosition = getListView().getLastVisiblePosition();
			cur_page = 1;
			loadPage(1);
		}

		@Override
		public void addMore() {
			visiblePosition = getListView().getLastVisiblePosition();
			if (cur_page < maxPage) {
				loadPage(++cur_page);
			}
		}
	};
	int maxPage = 1;
	int cur_page = 0;

	public void loadUrl(String url) {
		state = 0;
		pre_state = 0;
		this.url = url;
		cur_url = url;
		visiblePosition = 0;
		setTitle("");
		reset();
		subffix="";
	}

	public void reflash() {
		cur_page = 1;
		reset();
	}

	public void reset() {
		cur_page = 1;
		maxPage = 1;
		lists.clear();
		if (mAdapter != null && mAdapter.data != null) {
			mAdapter.data.clear();
			mAdapter.notifyDataSetChanged();
		}
		pullListView.hideFoot();
		pullListView.onRefreshComplete();
		pullListView.onFresh();
	}

	private void loadPage(int index) {
		if (index < 1 || index > maxPage) {
			return;
		}
		if (index == 1) {
			maxPage = 1;
		}
		if (pre_state != state) {
			lists.clear();
		}
		Request request = new Request();
		request.setData(cur_url + "&page=" + index+subffix);
		Log.d("BUG", "正在加载的URL :" + request.getData().toString());
		request.setTag(index);
		ThreadPool.getInstance().enqueueCommand(CommandID.COMMAND_CARD,
				request, this);
	}

	/**
	 * @Title: setTitle
	 * @Description: 设置帖子标题
	 * @param title
	 */
	public void setTitle(String title) {
		title = TextUtils.isEmpty(title) ? "" : title;
		if (this.title != null) {
			this.title.setText(title);
			this.title.setTextColor(Color.MAGENTA);
		}
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == CommandID.COMMAND_CARD) {
				Response response = (Response) msg.obj;
				Cards cards = (Cards) response.getData();
				maxPage = cards.max_page;
				cur_page = cards.cur_page;
				if (cur_page == 1)
					lists.clear();
				if (lists.containsKey(cur_page)) {
					lists.remove(cur_page);
				}
				lists.put(cur_page, cards.cards);
				if (maxPage <= cur_page) {
					pullListView.hideFoot();
				} else if (!pullListView.isShowFoot()) {
					pullListView.showFoot();
				}
				List<Card> data = new LinkedList<Card>();
				for (int i = 0; i <= lists.size(); i++) {
					List<Card> tmp = lists.get(i);
					if (tmp != null)
						data.addAll(tmp);
				}
				mAdapter.changeData(data);
				setSelection(visiblePosition);
				pullListView.onRefreshComplete();
				pullListView.onAddMoreComplete();
			} else if (msg.what == 0x001) {
				Toast.makeText(getActivity(), "加载失败!", Toast.LENGTH_LONG)
						.show();
				pullListView.onRefreshComplete();
				pullListView.onAddMoreComplete();
			}
		}

	};

	@Override
	public void onSuccess(Response response) {
		Message msg = mHandler.obtainMessage(CommandID.COMMAND_CARD);
		msg.obj = response;
		mHandler.sendMessage(msg);
	}

	@Override
	public void onError(Response response) {
		mHandler.sendEmptyMessage(0x001);
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
				arg1 = getActivity().getLayoutInflater().inflate(
						R.layout.carditem, null);
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
			ImageViewObserver observer = new ImageViewObserver(holder.m1, false);
			SyncLoadImage.getIntance().LoadBitmap(getItem(postion).image_au1,
					observer, SyncLoadImage.GRAVATAR);
			holder.m1.setFocusable(false);
			holder.m1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (state == ONLY_AUTHOR) {
						subffix="";
						state = ALL_AUTHOR;
						pre_state = ONLY_AUTHOR;
						Toast.makeText(getActivity(), "正在切换至查看所有楼层",
								Toast.LENGTH_LONG).show();
					} else {
						subffix="&authorid="+ getItem(postion).authorid;
						state = ONLY_AUTHOR;
						pre_state = ALL_AUTHOR;
						Toast.makeText(getActivity(),
								"正在切换至只看该作者 ：" + getItem(postion).author,
								Toast.LENGTH_LONG).show();
					}
					Log.d("BUG", cur_url);
					reflash();
				}
			});
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
			str = str.replace("src=\"image",
					"src=\"http://jx3.bbs.xoyo.com/image");
			str = str.replace("只看该作者", "");
			str = str.replace("|", "");
			str = str.replaceAll("<[^>]*>", "");
			Matcher matcher = Pattern.compile(
					"发表于[\\s]*[0-9]+-[0-9]+-[0-9]+[\\s]*[0-9]+:[0-9]+")
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