/**
 * @author:xiaowei
 * @version:2012-5-26下午5:23:57
 */
package com.android.xiaow.jx3bbs.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.android.xiaow.jx3bbs.CommandID;
import com.android.xiaow.jx3bbs.R;
import com.android.xiaow.jx3bbs.model.Bernda;
import com.android.xiaow.jx3bbs.model.BerndaListResponse;
import com.android.xiaow.jx3bbs.model.MainArea;
import com.android.xiaow.jx3bbs.ui.fragment.MyListView.OnRefreshListener;
import com.android.xiaow.mvc.Thread.ThreadPool;
import com.android.xiaow.mvc.common.IResponseListener;
import com.android.xiaow.mvc.common.Request;
import com.android.xiaow.mvc.common.Response;

/**
 * @ClassName: SubBerndaListFragment
 * @Description: 帖子列表
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-7-7 下午3:25:52
 * 
 */
public class SubBerndaListFragment extends SherlockListFragment implements
		IResponseListener {
	/**
	 * @Fields mView : 当前显示的View，即此页面的跟视图
	 */
	private View mView;
	private int mColour;
	private float mWeight;
	private int marginLeft, marginRight, marginTop, marginBottom;
	private View root;
	/**
	 * @Fields berndas : 记录帖子列表 key:页数 List<Bernda> 帖子列表
	 */
	private HashMap<Integer, List<Bernda>> berndas = new HashMap<Integer, List<Bernda>>();
	/**
	 * @Fields max_page : 最大页数
	 */
	private int max_page = -1;

	/**
	 * @Fields cur_page : 当页数
	 */
	private int cur_page = -1;
	/**
	 * @Fields bar :进度条
	 */
	private ProgressBar bar;
	private MainArea area;
	/**
	 * @Fields noticeData :公告帖子
	 */
	private List<Bernda> noticeData;
	/**
	 * @Fields noticeList : 公告帖子列表
	 */
	private ListView noticeList;
	/**
	 * @Fields pullListView : 帖子显示列表（不包括公告贴）
	 */
	MyListView pullListView;

	BerndaAdapter adapter;

	// need a public empty constructor for framework to instantiate
	public SubBerndaListFragment() {

	}

	public SubBerndaListFragment(int colour, float weight, int margin_left,
			int margin_right, int margin_top, int margin_bottom) {
		mColour = colour;
		mWeight = weight;
		marginLeft = margin_left;
		marginRight = margin_right;
		marginTop = margin_top;
		marginBottom = margin_bottom;
	}

	/**
	 * (非 Javadoc)
	 * <p>
	 * Title: onCreateView
	 * </p>
	 * <p>
	 * Description: 初始化该页面
	 * </p>
	 * 
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 * @return
	 * @see android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater,
	 *      android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		noticeData = new ArrayList<Bernda>();
		mView = inflater.inflate(R.layout.berndalist, null);
		bar = (ProgressBar) mView.findViewById(R.id.progressBar1);
		root = mView.findViewById(R.id.root);
		noticeList = (ListView) mView.findViewById(R.id.listView1);
		pullListView = (MyListView) mView.findViewById(android.R.id.list);
		pullListView.setonRefreshListener(HeadRefreshListener);
		mView.setBackgroundColor(Color.TRANSPARENT);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
				LayoutParams.FILL_PARENT, mWeight);
		lp.setMargins(marginLeft, marginTop, marginRight, marginBottom);
		adapter = new BerndaAdapter(new ArrayList<Bernda>());
		pullListView.setAdapter(adapter);
		mView.setLayoutParams(lp);
		return mView;
	}

	/**
	 * @Title: hidden
	 * @Description: 隐藏当前页面
	 */
	public void hidden() {
		mView.setVisibility(View.GONE);
	}

	/**
	 * @Title: show
	 * @Description:显示当前页面
	 */
	public void show() {
		mView.setVisibility(View.VISIBLE);
	}

	int lastVisibleIndex = -1;

	/**
	 * @Title: refresh
	 * @Description: 加载指定版块
	 * @param mainArea
	 */
	public void refresh(MainArea mainArea) {
		if (mainArea == null)
			return;
		this.area=mainArea;
		reset();
//		loadPage(1, true);
	}

	/**
	 * @Title: refresh
	 * @Description: 刷新当前块，若在没有调用加载版裤子之后调用，则会抛出异常
	 * @throws Exception
	 */
	public void refresh() throws Exception {
		if (area == null)
			throw new Exception("ManArea 不能为空！");
		reset();
		loadPage(1, true);
	}

	private void reset() {
		cur_page = 0;
		max_page = 0;
		berndas.clear();
		pullListView.onAddMoreComplete();
//		pullListView.onFresh();
		root.setVisibility(View.GONE);
		bar.setVisibility(View.VISIBLE);
	}

	/**
	 * @Title: loadFinish
	 * @Description: 加载内容结束时调用
	 */
	public void loadFinish() {
		root.setVisibility(View.VISIBLE);
		bar.setVisibility(View.GONE);
		pullListView.onRefreshComplete();
		pullListView.onAddMoreComplete();
		if (!update && lastVisibleIndex > -1) {
			getListView().setSelection(
					getListAdapter().getCount() < 32 ? 1 : getListAdapter()
							.getCount() - 32);
		}
	}

	/**
	 * @Title: loadNotice
	 * @Description: 加载公告帖子
	 */
	public void loadNotice() {
		noticeList.setAdapter(new BerndaAdapter(noticeData));
	}

	/**
	 * @Title: showNotice
	 * @Description: 显示公告帖子列表
	 */
	public void showNotice() {
		noticeList.setVisibility(View.VISIBLE);
	}

	/**
	 * @Title: HideNotice
	 * @Description: 隐藏公告帖子列表
	 */
	public void HideNotice() {
		noticeList.setVisibility(View.GONE);
	}

	/**
	 * @Title: updateData
	 * @Description: 从本地数据库加载指定页
	 * @param size
	 */
	public void updateData(int size) {
		Request request = new Request();
		request.setData(area.name);
		request.setContext(getActivity());
		request.setTag(area.name);
		request.setId(CommandID.COMMAND_BERNDA_DB);
		request.setTag(size);
		ThreadPool.getInstance().enqueueCommand(CommandID.COMMAND_BERNDA_DB,
				request, this);
	}

	/**
	 * @Title: loadPage
	 * @Description: 加载指定页面
	 * @param page
	 *            页数
	 * @param callback
	 *            是否回调
	 */
	public void loadPage(int page, boolean callback) {
		// System.out.println("start get BerndaList page:" + page);
		if (page == 1)
			berndas.clear();
		Request request = new Request();
		request.setTag(area.name);
		request.setContext(getActivity());
		request.setData(area.url + "&page=" + page);
		request.setId(CommandID.COMMAND_BERNDA);
		ThreadPool.getInstance().enqueueCommand(CommandID.COMMAND_BERNDA,
				request, callback ? this : null);
	}

	boolean update = false;
	OnRefreshListener HeadRefreshListener = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			if (area != null) {
				try {
					loadPage(1, true);
					update = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void addMore() {
			loadPage(++cur_page, true);
		}
	};

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == CommandID.COMMAND_BERNDA
					|| msg.what == CommandID.COMMAND_BERNDA_DB) {
				Toast.makeText(pullListView.getContext(), "加载完成", 100).show();
				Iterator<Integer> it = berndas.keySet().iterator();
				List<Bernda> data = new ArrayList<Bernda>();
				noticeData.clear();
				while (it.hasNext()) {
					List<Bernda> data0 = berndas.get(it.next());
					for (Bernda bernda : data0) {
						if (!bernda.type) {
							noticeData.add(bernda);
						} else if (!data.contains(bernda)) {
							data.add(bernda);
						}
					}
				}
				adapter.changeData(data);

				if (cur_page >= max_page) {
					pullListView.hideFoot();
				} else if (!pullListView.isShowFoot()) {
					pullListView.showFoot();
				}
				loadFinish();

				// Response response = (Response) msg.obj;
				// if (response.getId() == CommandID.COMMAND_BERNDA) {
				// updateData(getListAdapter() == null ? 30 : getListAdapter()
				// .getCount() + 30);
				// }
				// if (response.getData() == null) {
				// return;
				// } else {
				// List<Bernda> berndas = (List<Bernda>) response.getData();
				// if (berndas.isEmpty()) {
				// loadPage(1, true);
				// }
				// List<Bernda> data = new ArrayList<Bernda>();
				// noticeData.clear();
				// for (Bernda bernda : berndas) {
				// if (!bernda.type) {
				// noticeData.add(bernda);
				// } else {
				// if (!data.contains(bernda))
				// data.add(bernda);
				// }
				// }
				// if (getListAdapter() == null) {
				// setListAdapter(new BerndaAdapter(data));
				// } else {
				// ((BerndaAdapter) getListAdapter()).changeData(data);
				// }

				// }
			}

		}

	};

	@Override
	public void onSuccess(final Response response) {
		Message msg = mHandler.obtainMessage(CommandID.COMMAND_BERNDA);
		msg.obj = response;
		if (response instanceof BerndaListResponse) {
			BerndaListResponse bResponse = (BerndaListResponse) response;
			if (berndas.containsKey(bResponse.cur_page))
				berndas.remove(bResponse.cur_page);
			berndas.put(bResponse.cur_page, bResponse.berndas);
			cur_page = bResponse.cur_page;
			max_page = Math.max(bResponse.max_page, max_page);
		}
		mHandler.sendMessage(msg);
	}

	@Override
	public void onError(Response response) {
		mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT).show();
				loadFinish();				
			}
		});
		
		// TODO Auto-generated method stub
		// Log.d("BUG", response.getData().toString());
	}

	class BerndaAdapter extends BaseAdapter {
		List<Bernda> datas;

		int[] color = new int[] { R.drawable.item_back0, R.drawable.item_back1,
				R.drawable.item_back2, R.drawable.item_back3,
				R.drawable.item_back4, R.drawable.item_back5,
				R.drawable.item_back6, R.drawable.item_back7,
				R.drawable.item_back8, R.drawable.item_back9,
				R.drawable.item_back10, R.drawable.item_back11 };
		public BerndaAdapter(List<Bernda> datas) {
			super();
			this.datas = datas;
		}

		@Override
		public int getCount() {
			return datas.size();
		}

		@Override
		public Bernda getItem(int position) {
			return datas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public void changeData(List<Bernda> data) {
			datas = data;
			this.notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder;
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.berndaitem, null);
				holder = new Holder();
				holder.tv1 = (TextView) convertView
						.findViewById(R.id.textView1);
				holder.tv2 = (TextView) convertView
						.findViewById(R.id.textView2);
				holder.tv3 = (TextView) convertView
						.findViewById(R.id.textView3);
				holder.tv4 = (TextView) convertView
						.findViewById(R.id.textView4);
				holder.tv5 = (TextView) convertView
						.findViewById(R.id.textView5);
				holder.postion = position;
				convertView.setTag(holder);
			}
			if (position % 2 == 0) {
				convertView.setBackgroundResource(R.drawable.item_back12);// 浅色
			} else {
				convertView.setBackgroundResource(R.drawable.item_back13);// 深色
			}
//			convertView.setBackgroundResource(color[position%color.length]);
			holder = (Holder) convertView.getTag();
			Bernda bernda = getItem(position);
			if (TextUtils.isEmpty(bernda.item)) {
				bernda.item = "";
			}
			holder.tv1.setText(bernda.item + bernda.name);
			holder.tv5.setText(bernda.refuse + "");
			holder.tv2.setText(bernda.author);
			holder.tv3.setText(bernda.lastName);
			holder.tv4.setText(bernda.last_time);
			holder.postion = position;
			convertView.setTag(holder);
			return convertView;
		}

	}

	ItemOnClickListener mItemOnClickListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof ItemOnClickListener) {
			mItemOnClickListener = (ItemOnClickListener) activity;
		}
	}

	public static class Holder {
		public TextView tv1;
		public TextView tv2;
		public TextView tv3;
		public TextView tv4;
		public TextView tv5;
		public LinearLayout bar;
		public int postion;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if (mItemOnClickListener != null)
			mItemOnClickListener.onItemClick(adapter.getItem(position-1).url,
					adapter.getItem(position-1).name);
	}

	public static interface ItemOnClickListener {
		void onItemClick(String url, String title);
	}
}
