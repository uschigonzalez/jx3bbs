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
		if(str.contains("�����������޺�")){
			Log.d("BBB", "��¼�ɹ��������������޺ۣ�");
		}
		Document doc = Jsoup.parse(str);
		/**
		 * id:wrap�ı�ǩΪ��������root��ǩ
		 */
		Element element = doc.body().getElementById("wrap");
		/**
		 * class=��mainbox list�� ÿ���������Ӱ���б�
		 */
		Elements elements = element.getElementsByAttributeValue("class",
				"mainbox list");
		List<Element> lists = new LinkedList<Element>();
		Iterator<Element> it = elements.iterator();
		Element _ele;
		while (it.hasNext()) {
			_ele = it.next();
			/**
			 * tbody��ǩΪ��ÿ���汾�Ǿ����ǩ
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
			// ��ʾ������Ϊ0���˰��Ϊ���ӵ������ط��İ�� �ʺ���
			if (!HttpUtil.validStr(_str)) {
				continue;
			}
			MainArea mainArea = new MainArea();
			// ������
			mainArea.refuse = HttpUtil.findNumByStr(_str);
			_ele_1 = _ele_1.get(0).getElementsByTag("em");
			if (_ele_1.size() > 0) {
				_str = _ele_1.get(0).ownText();
				// ������
				mainArea.newthread = HttpUtil.findNumByStr(_str);
			}
			Elements _ele_2 = _ele
					.getElementsByAttributeValue("class", "forumlast").get(0)
					.getElementsByTag("a");
			if (_ele_2.size() > 0) {
				_str = _ele_2.get(0).attr("href");
				// �������ַ
				mainArea.url_last = "http://jx3.bbs.xoyo.com/" + _str;
				_str = _ele_2.get(0).ownText();
				// ���������
				mainArea.last_name = _str;
			}

			_ele_1 = _ele.getElementsByTag("h2");
			_str = _ele_1.get(0).getElementsByTag("a").get(0).attr("href");
			// ����ַ
			mainArea.url = "http://jx3.bbs.xoyo.com/" + _str;
			// �������
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
				// ���շ�����
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
