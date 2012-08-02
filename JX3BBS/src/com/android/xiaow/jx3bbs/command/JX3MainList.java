package com.android.xiaow.jx3bbs.command;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.TextUtils;
import android.util.Log;

import com.android.xiaow.jx3bbs.CommandID;
import com.android.xiaow.jx3bbs.Initializer;
import com.android.xiaow.jx3bbs.common.SubBoard;
import com.android.xiaow.jx3bbs.database.MainBrachConn;
import com.android.xiaow.jx3bbs.model.MainArea;
import com.android.xiaow.jx3bbs.utils.HttpUtil;
import com.android.xiaow.mvc.Thread.ThreadPool;
import com.android.xiaow.mvc.command.AbstractHttpCommand;
import com.android.xiaow.mvc.common.Request;

public class JX3MainList extends AbstractHttpCommand {

	public JX3MainList() {
		super();
		setURI(URI.create("http://jx3.bbs.xoyo.com/index.php"));
	}

	@Override
	protected HttpRequestBase getHttpRequest() {
		if (getRequest().getData() != null
				&& HttpUtil.validStr(getRequest().getData().toString())) {
			return new HttpGet(getRequest().getData().toString());
		}
		return new HttpGet(getURI());
	}

	@Override
	protected synchronized Object getSuccessResponse(HttpResponse response) {
		List<MainArea> datas = new ArrayList<MainArea>();

		String str = HttpUtil.getResultFormResponse(response);
		if(str.contains("风雨潇潇梦无痕")){
			Log.d("BBB", "登录成功：风雨潇潇梦无痕：");
		}
		Document doc = Jsoup.parse(str);
		/**
		 * id:wrap的标签为主题内容root标签
		 */
		Element element = doc.body().getElementById("wrap");
		/**
		 * class=“mainbox list” 每个分区的子版块列表
		 */
		Elements elements = element.getElementsByAttributeValue("class",
				"mainbox list");
		List<Element> lists = new LinkedList<Element>();
		Iterator<Element> it = elements.iterator();
		Element _ele;
		while (it.hasNext()) {
			_ele = it.next();
			/**
			 * tbody标签为个每个版本是具体标签
			 */
			Elements _els = _ele.getElementsByTag("tbody");
			int len = _els.size();
			lists.addAll(Arrays.asList(_els.toArray(new Element[len])));
		}
		it = lists.iterator();
		while (it.hasNext()) {
			_ele = it.next();
			String _str = "";
			Elements _ele_1 = _ele.getElementsByAttributeValue("class",
					"forumnums");
			_str = _ele_1.get(0).ownText();
			// 表示回帖量为0，此版块为连接到其他地方的版块 故忽略
			if (!HttpUtil.validStr(_str)) {
				continue;
			}
			MainArea mainArea = new MainArea();
			// 回帖量
			mainArea.refuse = HttpUtil.findNumByStr(_str);
			_ele_1 = _ele_1.get(0).getElementsByTag("em");
			if (_ele_1.size() > 0) {
				_str = _ele_1.get(0).ownText();
				// 发帖量
				mainArea.newthread = HttpUtil.findNumByStr(_str);
			}
			Elements _ele_2 = _ele
					.getElementsByAttributeValue("class", "forumlast").get(0)
					.getElementsByTag("a");
			if (_ele_2.size() > 0) {
				_str = _ele_2.get(0).attr("href");
				// 最后发帖地址
				mainArea.url_last = "http://jx3.bbs.xoyo.com/" + _str;
				_str = _ele_2.get(0).ownText();
				// 最后发帖名称
				mainArea.last_name = _str;
			}

			_ele_1 = _ele.getElementsByTag("h2");
			_str = _ele_1.get(0).getElementsByTag("a").get(0).attr("href");
			// 版块地址
			mainArea.url = "http://jx3.bbs.xoyo.com/" + _str;
			// 版块名称
			_str = _ele_1.get(0).getElementsByTag("a").get(0).ownText();
			mainArea.name = _str;
			if (Initializer.bernda.contains(mainArea.name.trim())) {
				continue;
			}
			if (TextUtils.isEmpty(mainArea.name.trim())) {
				continue;
			}
			_ele_2 = _ele_1.get(0).getElementsByTag("strong");
			if (_ele_2.size() > 0) {
				// 今日发帖量
				_str = _ele_2.get(0).ownText();
				mainArea.today = HttpUtil.findNumByStr(_str);
			}
			if (getRequest().getTag() != null) {
				mainArea.parent = getRequest().getTag().toString();
			}
			mainArea.isSubBroad = SubBoard.validContact(mainArea.name);
			if (mainArea.isSubBroad) {
				Request request = new Request();
				request.setContext(getRequest().getContext());
				request.setData(mainArea.url);
				request.setTag(mainArea.name);
				ThreadPool.getInstance().enqueueCommand(
						CommandID.COMMAND_MAINAREA, request,
						getResponseListener());
			}
			datas.add(mainArea);
		}

		if (getRequest().getContext() != null) {
			MainBrachConn.getInstance().save(datas);
		}
		return datas;
	}

	@Override
	protected byte[] getBody() {
		return null;
	}

	@Override
	protected String getContentType() {
		return null;
	}

}
