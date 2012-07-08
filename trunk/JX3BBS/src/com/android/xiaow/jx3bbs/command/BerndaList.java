package com.android.xiaow.jx3bbs.command;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.android.xiaow.jx3bbs.database.BerndaConn;
import com.android.xiaow.jx3bbs.model.Bernda;
import com.android.xiaow.jx3bbs.model.BerndaListResponse;
import com.android.xiaow.jx3bbs.utils.HttpUtil;
import com.android.xiaow.mvc.command.AbstractHttpCommand;

/**
 * @ClassName: BerndaList
 * @Description:���������б�
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-7-7 ����3:56:52
 * 
 */
public class BerndaList extends AbstractHttpCommand {

	
	@Override
	protected void prepare() {
		super.prepare();
		setResponse(new BerndaListResponse());
	}

	@Override
	protected byte[] getBody() {
		return null;
	}

	@Override
	protected String getContentType() {
		return null;
	}

	@Override
	protected Object getSuccessResponse(HttpResponse response) {
		BerndaListResponse bResponse = (BerndaListResponse) getResponse();
		List<Bernda> berndas = new ArrayList<Bernda>();
		String str;
		try {
			str = HttpUtil.getResultFormResponse(response);
			Document doc = Jsoup.parse(str);
			// ��ȡ<div class="pages"...></div>��ǩ���ñ�ǩ�����������б��ҳ��
			Elements pages = doc.getElementsByAttributeValue("class", "pages");
			int cur_page =-1;
			if (pages.size() > 0) {
				String _pages = pages.get(0).outerHtml();
				Matcher _ma = Pattern.compile("<strong>[0-9]{1,}</strong>")
						.matcher(_pages);
				if(_ma.find()){
					String cur_pages = _ma.group();
					_ma=Pattern.compile("[0-9]{1,}")
							.matcher(cur_pages);
					if(_ma.find()) 
						cur_page = Integer.parseInt(_ma.group());

				}
				
				bResponse.cur_page = cur_page;

				
				Matcher ma = Pattern.compile("page=[0-9]{1,}").matcher(_pages);
				int max = -1;
				// <strong>1</strong> �˱�־λ��ǰҳ

				while (ma.find()) {
					String _page = ma.group();
					int tmp = Integer.parseInt(_page.substring(_page
							.indexOf("=") + 1));
					max = Math.max(max, tmp);
				}
				bResponse.max_page = max;
			}

			Element element = doc.getElementById("moderate");
			Elements elements = element.getElementsByTag("tbody");
			for (Element element2 : elements) {
				String id = element2.attr("id");
				// element.attr("style", "\"display:none;\"");
				Bernda bernda = new Bernda();
				if (!HttpUtil.validStr(id)) {
					continue;
				} else if (id.contains("stickthread")) {
					// id="stickthread_31543959" ������
					bernda.type = false;
				} else if (id.contains("normalthread")) {
					// normalthread_31677729 ��������
					bernda.type = true;
				}
				id = "thread_" + HttpUtil.findNumByStr(id);
				Element element3 = element2.getElementById(id);
				Elements element4 = element3.getElementsByTag("a");
				if (element4.size() > 0) {
					// ��������
					bernda.name = element4.get(0).ownText();
					// ���ӵ�ַ
					bernda.url = "http://jx3.bbs.xoyo.com/"
							+ element4.get(0).attr("href");
					bernda.url = bernda.url.replaceAll("&extra=[^&]*", "");
					element4 = element4.get(0).getElementsByAttributeValue(
							"class", "threadpages");
					if (element4.size() > 0) {
						element4 = element4.get(0).getElementsByTag("a");
						// ���ҳ�����������⣬��ʾ���ҳ��Ϊ1
						String _str = element4.get(element4.size() - 1)
								.ownText();
						bernda.max_page = HttpUtil.findNumByStr(_str);
					}
				} else {
					continue;
				}
				element4 = element2.getElementsByAttributeValueStarting(
						"class", "subject");
				while (element4.size() > 0) {
					if (element4.get(0).getElementsByTag("em").size() < 1)
						break;
					element4 = element4.get(0).getElementsByTag("em").get(0)
							.getElementsByTag("a");
					// ��ǩ����
					bernda.item = element4.get(0).ownText();
					// ��ǩ��ַ
					bernda.item_url = "http://jx3.bbs.xoyo.com/"
							+ element4.get(0).attr("href");
				}
				element4 = element2.getElementsByAttributeValue("class",
						"author");
				// ����
				bernda.author = element4.get(0).getElementsByTag("a").get(0)
						.ownText();
				// Log.d("MSG", "���ߣ�"+bernda.author+","+bernda.type);
				element4 = element2
						.getElementsByAttributeValue("class", "nums");
				// �ظ�
				String _str = element4.get(0).getElementsByTag("strong").get(0)
						.ownText();
				bernda.refuse = HttpUtil.findNumByStr(_str);
				// ���
				_str = element4.get(0).getElementsByTag("em").get(0).ownText();
				element4 = element2
						.getElementsByAttributeValue("class", "lastpost")
						.get(0).getElementsByTag("em");
				bernda.last_time = element4.get(0).getElementsByTag("a").get(0)
						.ownText();
				element4 = element2
						.getElementsByAttributeValue("class", "lastpost")
						.get(0).getElementsByTag("cite");
				bernda.lastName = element4.get(0).getElementsByTag("a").get(0)
						.ownText();
				if (getRequest().getTag() != null) {
					bernda.parent = getRequest().getTag().toString();
				}
				bernda.scane = HttpUtil.findNumByStr(_str);
				berndas.add(bernda);
			}
			// long t3 = System.currentTimeMillis();
			// Log.d("MSG", "�������ʱ�䣺" + (t3 - t2) + "ms");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (getRequest().getContext() != null) {
			BerndaConn.getInstance().save(berndas);
		}
		bResponse.berndas = berndas;
		return berndas;
	}

	@Override
	protected HttpRequestBase getHttpRequest() {
		if (getRequest().getData() != null
				&& HttpUtil.validStr(getRequest().getData().toString())) {
			return new HttpGet(getRequest().getData().toString());
		}
		return new HttpGet(getURI());
	}
}
