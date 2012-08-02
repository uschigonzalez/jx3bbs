/**
 * @author:xiaowei
 * @version:2012-5-26����11:34:59
 */
package com.android.xiaow.jx3bbs.command;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.TextUtils;
import android.util.Log;

import com.android.xiaow.jx3bbs.model.Card;
import com.android.xiaow.jx3bbs.model.Cards;
import com.android.xiaow.jx3bbs.ui.component.HTMLayout;
import com.android.xiaow.jx3bbs.utils.HttpUtil;
import com.android.xiaow.mvc.command.AbstractHttpCommand;

/**
 * @author xiaowei
 * 
 */
public class CardsCommand extends AbstractHttpCommand {

	@Override
	protected HttpRequestBase getHttpRequest() {
		if (getRequest().getData() != null
				&& HttpUtil.validStr(getRequest().getData().toString())) {
			return new HttpGet(getRequest().getData().toString());
		}
		return new HttpGet(getURI());
	}

	@Override
	protected Object getSuccessResponse(HttpResponse response) {
		Cards cards = new Cards();
		List<Card> list = new ArrayList<Card>();
			String html =HttpUtil.getResultFormResponse(response);
			if(html.contains("�����������޺�")){
				Log.d("BBB", "��¼�ɹ��������������޺ۣ�");
			}
			Element root = Jsoup.parse(html).body();
			Element element = root.getElementById("postlist");
			Elements ele1 = root.getElementsByAttributeValue("class", "pages");
			if (ele1.size() > 0 && ele1.get(0).outerHtml().contains("��һҳ")) {
				cards.hasNextPage = true;
				String _pages = ele1.get(0).outerHtml();
				Matcher _ma = Pattern.compile("<strong>[0-9]{1,}</strong>")
						.matcher(_pages);
				if(_ma.find()){
					String cur_pages = _ma.group();
					_ma=Pattern.compile("[0-9]{1,}")
							.matcher(cur_pages);
					if(_ma.find()) 
						 cards.cur_page = Integer.parseInt(_ma.group());
				}
				
				Matcher ma = Pattern.compile("page=[0-9]{1,}").matcher(_pages);
				int max = -1;
				// <strong>1</strong> �˱�־λ��ǰҳ

				while (ma.find()) {
					String _page = ma.group();
					int tmp = Integer.parseInt(_page.substring(_page
							.indexOf("=") + 1));
					max = Math.max(max, tmp);
				}
				cards.max_page=max;
			} else
				cards.hasNextPage = false;
			for (Element ele : element.children()) {
				Card card = new Card();
				String author = "";
				Elements eles0 = ele.getElementsByAttributeValue("class",
						"postauthor");
				for (Element element2 : eles0.get(0).children()) {
					if (element2.hasAttr("class")
							&& element2.attr("class").equals("postinfo")) {
						author = element2.child(0).ownText();// ����
					} else if (element2.id().startsWith("userinfo")
							&& element2.id().endsWith("_a")) {
						if (element2.children().size() < 1
								|| element2.child(0).children().size() < 1
								|| element2.child(0).child(0).children().size() < 3) {
							continue;
						}
						card.image_au1 = element2.child(0).child(0).child(0)
								.attr("src");// ͷ��
						card.image_au2 = element2.child(0).child(0).child(2)
								.attr("src");// ͷ���µĺ���
						card.leverl = element2.child(1).child(0).ownText();// LV.12
					}
				}
				Matcher ma1 = Pattern.compile("space[^\"]+php[^\"]+[0-9]+")
						.matcher(eles0.outerHtml());
				if (ma1.find()) {
					String _id = ma1.group();
					card.authorid = _id.split("=")[1];
					if (TextUtils.isEmpty(cards.authorId)) {
						cards.authorId = card.authorid;
					}
				}
				/**
				 * detail // string regexstr = @"<img[^>]*>"; //ȥ��ͼƬ������ //
				 * string regexstr = @"<(?!br).*?>"; //ȥ�����б�ǩ��ֻʣbr // string
				 * regexstr = @"
				 * <table[^>
				 * ]*?>.*?
				 * </table>
				 * "; //ȥ��table������������� string regexstr =
				 * 
				 * @"<(?!img|br|p|/p).*?>"; //ȥ�����б�ǩ��ֻʣimg,br,p
				 */
				String detail = "";
				Elements eles1 = ele.getElementsByAttributeValue("class",
						"postcontent");
				card.time = eles1.get(0)
						.getElementsByAttributeValue("class", "authorinfo")
						.outerHtml();
				Elements _id_eles = eles1.get(0).getElementsByTag("img");
				String id = "";
				for (Element element2 : _id_eles) {
					if (element2.hasAttr("class")
							&& element2.attr("class").equals("authicon")
							&& element2.hasAttr("id")
							&& element2.id().startsWith("authicon")) {
						id = element2.id().replace("authicon", "");
						if (!TextUtils.isEmpty(id)) {
							break;
						}
					}
				}
				// title
				if (ele.getElementById("threadtitle") != null) {
					cards.title = ele.getElementById("threadtitle").outerHtml();
				}
				// ����
				Element ele_detail= ele.getElementById("postmessage_" + id);
				if(ele_detail==null){
					detail="<div class=\"locked\">��ʾ: <em>���߱���ֹ��ɾ�� �����Զ�����</em></div>";
				}else{
					detail = ele_detail.outerHtml();			
				}
				eles1 = ele.getElementsByAttributeValue("class",
						"t_attachlist attachimg");
				if (eles1.size() > 0) {
					for (Element element2 : eles1) {
						Matcher ma = Pattern.compile(HTMLayout.regex_img_0)
								.matcher(element2.outerHtml());
						if (ma.find()) {
							detail = detail + ma.group();
						}
					}
				}
				card.author = author;
				card.detail = detail;
				list.add(card);
			}
			cards.cards = list;
			// long t3 = System.currentTimeMillis();
			// Log.d("MSG", "�������ʱ�䣺" + (t3 - t2) + "ms");
		

		return cards;
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
