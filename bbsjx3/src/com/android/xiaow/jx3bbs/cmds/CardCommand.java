/**   
 * @Title: CardCommand.java
 * @Package com.android.xiaow.jx3bbs.cmds
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-12 下午9:12:00
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs.cmds;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.TextUtils;
import android.util.Log;

import com.android.xiaow.core.cmds.BaseHttpCommand;
import com.android.xiaow.core.util.HttpUtil;
import com.android.xiaow.jx3bbs.model.Card;
import com.android.xiaow.jx3bbs.model.Cards;
import com.android.xiaow.jx3bbs.widget.HTMLayout;

/**
 * @ClassName: CardCommand
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-12 下午9:12:00
 * 
 */
public class CardCommand extends BaseHttpCommand {

    @Override
    public void preExecute() {
        super.preExecute();
        setHttpRequest(new HttpGet(getRequest().url));
    }

    @Override
    public void addHeader() {
        super.addHeader();
        getHttpRequest().addHeader("Accept", "text/html, application/xhtml+xml, */*");
        getHttpRequest().addHeader("Accept-Encoding", "gzip, deflate");
        getHttpRequest().addHeader("Accept-Language", "zh-CN");
        getHttpRequest().addHeader("Connection", "Keep-Alive");
        getHttpRequest().addHeader("Host", "jx3.bbs.xoyo.com");
        getHttpRequest().addHeader("User-Agent",
                "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
    }

    @Override
    public Object getSuccesData(HttpResponse response) throws Exception {
        Cards cards = new Cards();
        List<Card> list = new ArrayList<Card>();
        String html = HttpUtil.getResultFormResponse(response);
        if (html.contains("风雨潇潇梦无痕")) {
            Log.d("BBB", "登录成功：风雨潇潇梦无痕：");
        }
        Element root = Jsoup.parse(html).body();
        Element element = root.getElementById("postlist");
        Elements ele1 = root.getElementsByAttributeValue("class", "pages");
        if (ele1.size() > 0 && ele1.get(0).outerHtml().contains("下一页")) {
            cards.hasNextPage = true;
            String _pages = ele1.get(0).outerHtml();
            Matcher _ma = Pattern.compile("<strong>[0-9]{1,}</strong>").matcher(_pages);
            if (_ma.find()) {
                String cur_pages = _ma.group();
                _ma = Pattern.compile("[0-9]{1,}").matcher(cur_pages);
                if (_ma.find())
                    cards.cur_page = Integer.parseInt(_ma.group());
            }

            Matcher ma = Pattern.compile("page=[0-9]{1,}").matcher(_pages);
            int max = -1;
            // <strong>1</strong> 此标志位当前页

            while (ma.find()) {
                String _page = ma.group();
                int tmp = Integer.parseInt(_page.substring(_page.indexOf("=") + 1));
                max = Math.max(max, tmp);
            }
            cards.max_page = max;
        } else
            cards.hasNextPage = false;
        for (Element ele : element.children()) {
            Card card = new Card();
            String author = "";
            Elements eles0 = ele.getElementsByAttributeValue("class", "postauthor");
            for (Element element2 : eles0.get(0).children()) {
                if (element2.hasAttr("class") && element2.attr("class").equals("postinfo")) {
                    author = element2.child(0).ownText();// 名称
                } else if (element2.id().startsWith("userinfo") && element2.id().endsWith("_a")) {
                    if (element2.children().size() < 1 || element2.child(0).children().size() < 1
                            || element2.child(0).child(0).children().size() < 3) {
                        continue;
                    }
                    card.image_au1 = element2.child(0).child(0).child(0).attr("src");// 头像
                    card.image_au2 = element2.child(0).child(0).child(2).attr("src");// 头像下的横栏
                    card.leverl = element2.child(1).child(0).ownText();// LV.12
                }
            }
            Matcher ma1 = Pattern.compile("space[^\"]+php[^\"]+[0-9]+").matcher(eles0.outerHtml());
            if (ma1.find()) {
                String _id = ma1.group();
                card.authorid = _id.split("=")[1];
                if (TextUtils.isEmpty(cards.authorId)) {
                    cards.authorId = card.authorid;
                }
            }
            /**
             * detail // string regexstr = @"<img[^>]*>"; //去除图片的正则 // string
             * regexstr = @"<(?!br).*?>"; //去除所有标签，只剩br // string regexstr = @"
             * <table[^>
             * ]*?>.*?
             * </table>
             * "; //去除table里面的所有内容 string regexstr =
             * 
             * @"<(?!img|br|p|/p).*?>"; //去除所有标签，只剩img,br,p
             */
            String detail = "";
            Elements eles1 = ele.getElementsByAttributeValue("class", "postcontent");
            card.time = eles1.get(0).getElementsByAttributeValue("class", "authorinfo").outerHtml();
            Elements _id_eles = eles1.get(0).getElementsByTag("img");
            String id = "";
            for (Element element2 : _id_eles) {
                if (element2.hasAttr("class") && element2.attr("class").equals("authicon")
                        && element2.hasAttr("id") && element2.id().startsWith("authicon")) {
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
            // 内容
            Element ele_detail = ele.getElementById("postmessage_" + id);
            if (ele_detail == null) {
                detail = "<div class=\"locked\">提示: <em>作者被禁止或删除 内容自动屏蔽</em></div>";
            } else {
                detail = ele_detail.outerHtml();
            }
            eles1 = ele.getElementsByAttributeValue("class", "t_attachlist attachimg");
            if (eles1.size() > 0) {
                for (Element element2 : eles1) {
                    Matcher ma = Pattern.compile(HTMLayout.regex_img_0).matcher(
                            element2.outerHtml());
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
        // Log.d("MSG", "解析版块时间：" + (t3 - t2) + "ms");
        return cards;
    }

}
