/**   
 * @Title: BranchListCommand.java
 * @Package com.android.xiaow.jx3bbs.cmds
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-12 下午6:50:25
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs.cmds;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

import com.android.xiaow.core.cmds.BaseHttpCommand;
import com.android.xiaow.core.util.HttpUtil;
import com.android.xiaow.jx3bbs.model.Bernda;

/**
 * @ClassName: BranchListCommand
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-12 下午6:50:25
 * 
 */
public class BranchListCommand extends BaseHttpCommand {
    BranchRequest request;

    @Override
    public void preExecute() {
        super.preExecute();
        if (getRequest() != null && getRequest() instanceof BranchRequest) {
            request = (BranchRequest) getRequest();
        }
        setHttpRequest(new HttpGet(request.url));
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
        BranchResponse bResponse = new BranchResponse();
        setResponse(bResponse);
        List<Bernda> berndas = new ArrayList<Bernda>();
        String str;
        long t1, t2 = 0, t3 = 0;
        t1 = System.currentTimeMillis();
        Log.d("MSS", "开始读取数据：" + t1);
        try {
            str = HttpUtil.getResultFormResponse(response);
            t2 = System.currentTimeMillis();
            Log.d("MSS", "读取数据结束：" + t2);
            if (str.contains("风雨潇潇梦无痕")) {
                Log.d("BBB", "登录成功：风雨潇潇梦无痕：");
            }

            Document doc = Jsoup.parse(str);
            Element _modedby = doc.getElementById("modedby");
            if (_modedby != null) {
                Log.d("MSG", "modedby------->" + _modedby.outerHtml().replaceAll("<[^>]*>", ""));
            }
            Element root = doc.body();
            Elements _formhash = root.getElementsByAttributeValue("name", "formhash");
            if (_formhash != null && _formhash.size() > 0) {
                bResponse.formhash = _formhash.get(0).attributes().get("value");
            }
            Elements listextra = root.getElementsByAttributeValue("name", "listextra");
            if (listextra != null && listextra.size() > 0) {
                bResponse.listextra = _formhash.get(0).attributes().get("value");
            }
            Matcher matcher = Pattern.compile("gid[^<]*").matcher(str);
            if (matcher.find()) {
                String sids = matcher.group();
                Log.d("BBB", sids);
                String[] sid = sids.split(",");
                for (String _sid : sid) {
                    if (_sid.trim().indexOf("gid") > -1) {
                        matcher = Pattern.compile("[0-9]{1,}").matcher(_sid);
                        if (matcher.find()) {
                            bResponse.gid = matcher.group();
                        }
                    } else if (_sid.trim().indexOf("fid") > -1) {
                        matcher = Pattern.compile("[0-9]{1,}").matcher(_sid);
                        if (matcher.find()) {
                            bResponse.fid = matcher.group();
                        }
                    } else if (_sid.trim().indexOf("tid") > -1) {
                        matcher = Pattern.compile("[0-9]{1,}").matcher(_sid);
                        if (matcher.find()) {
                            bResponse.tid = matcher.group();
                        }
                    } 
                }
                Log.d("BBB", "gid = "+bResponse.gid+",fid="+bResponse.fid+",tid="+bResponse.tid);
            }
            
            
            // 获取<div class="pages"...></div>标签，该标签包含了帖子列表的页数
            Elements pages = doc.getElementsByAttributeValue("class", "pages");
            int cur_page = -1;
            if (pages.size() > 0) {
                String _pages = pages.get(0).outerHtml();
                Matcher _ma = Pattern.compile("<strong>[0-9]{1,}</strong>").matcher(_pages);
                if (_ma.find()) {
                    String cur_pages = _ma.group();
                    _ma = Pattern.compile("[0-9]{1,}").matcher(cur_pages);
                    if (_ma.find())
                        cur_page = Integer.parseInt(_ma.group());

                }

                bResponse.cur_page = cur_page;

                Matcher ma = Pattern.compile("page=[0-9]{1,}").matcher(_pages);
                int max = -1;
                // <strong>1</strong> 此标志位当前页

                while (ma.find()) {
                    String _page = ma.group();
                    int tmp = Integer.parseInt(_page.substring(_page.indexOf("=") + 1));
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
                    // id="stickthread_31543959" 公告贴
                    bernda.type = 0;
                } else if (id.contains("normalthread")) {
                    // normalthread_31677729 正常帖子
                    bernda.type = 1;
                }
                id = "thread_" + HttpUtil.findNumByStr(id);
                Element element3 = element2.getElementById(id);
                Elements element4 = element3.getElementsByTag("a");
                if (element4.size() > 0) {
                    // 帖子名称
                    bernda.name = element4.get(0).ownText();
                    // 帖子地址
                    bernda.url = "http://jx3.bbs.xoyo.com/" + element4.get(0).attr("href");
                    bernda.url = bernda.url.replaceAll("&extra=[^&]*", "");
                    element4 = element4.get(0).getElementsByAttributeValue("class", "threadpages");
                    if (element4.size() > 0) {
                        element4 = element4.get(0).getElementsByTag("a");
                        // 最大页数，不进入这，表示最大页数为1
                        String _str = element4.get(element4.size() - 1).ownText();
                        bernda.max_page = HttpUtil.findNumByStr(_str);
                    }
                } else {
                    continue;
                }
                element4 = element2.getElementsByAttributeValueStarting("class", "subject");
                while (element4.size() > 0) {
                    if (element4.get(0).getElementsByTag("em").size() < 1)
                        break;
                    element4 = element4.get(0).getElementsByTag("em").get(0).getElementsByTag("a");
                    // 标签类型
                    bernda.item = element4.get(0).ownText();
                    // 标签地址
                    bernda.item_url = "http://jx3.bbs.xoyo.com/" + element4.get(0).attr("href");
                }
                element4 = element2.getElementsByAttributeValue("class", "author");
                // 作者
                bernda.author = element4.get(0).getElementsByTag("a").get(0).ownText();
                // Log.d("MSG", "作者："+bernda.author+","+bernda.type);
                element4 = element2.getElementsByAttributeValue("class", "nums");
                // 回复
                String _str = element4.get(0).getElementsByTag("strong").get(0).ownText();
                bernda.refuse = HttpUtil.findNumByStr(_str);
                // 点击
                _str = element4.get(0).getElementsByTag("em").get(0).ownText();
                element4 = element2.getElementsByAttributeValue("class", "lastpost").get(0)
                        .getElementsByTag("em");
                bernda.last_time = element4.get(0).getElementsByTag("a").get(0).ownText();
                element4 = element2.getElementsByAttributeValue("class", "lastpost").get(0)
                        .getElementsByTag("cite");
                bernda.lastName = element4.get(0).getElementsByTag("a").get(0).ownText();
                bernda.parent = request.parent;
                bernda.scane = HttpUtil.findNumByStr(_str);
                berndas.add(bernda);
            }
            // long t3 = System.currentTimeMillis();
            // Log.d("MSG", "解析版块时间：" + (t3 - t2) + "ms");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // TODO：保存至数据库
        // new BerndaDB().update(berndas);
        bResponse.berndas = berndas;
        t3 = System.currentTimeMillis();
        Log.d("MSS", "解析数据结束：" + t3);
        Log.d("MSS", "读取数据耗时：" + (t2 - t1) + ",真正解析耗时：" + (t3 - t2));
        return berndas;
    }

}
