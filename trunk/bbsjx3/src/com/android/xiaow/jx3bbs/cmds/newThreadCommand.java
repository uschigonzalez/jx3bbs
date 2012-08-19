/**   
 * @Title: ReplayCommand.java
 * @Package com.android.xiaow.jx3bbs.cmds
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-15 下午11:28:39
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs.cmds;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.text.TextUtils;
import android.util.Log;

import com.android.xiaow.core.cmds.BaseHttpCommand;
import com.android.xiaow.core.common.Response;
import com.android.xiaow.core.util.HttpUtil;

/**
 * @ClassName: ReplayCommand
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-15 下午11:28:39
 * 
 */
public class newThreadCommand extends BaseHttpCommand {
    ThreadRequest replayRequest;
    HttpPost post;

    @Override
    public void preExecute() {
        super.preExecute();
        //"http://jx3.bbs.xoyo.com/post.php?&action=newthread&fid=7053&extra=&topicsubmit=yes"
        post = new HttpPost(getRequest().url);
        setHttpRequest(post);
    }

    @Override
    public Object getSuccesData(HttpResponse response) throws Exception {
        String msg = HttpUtil.getResultFormResponse(response);
        Log.d("MSG", msg);
        if (getResponse() == null) {
            setResponse(new Response());
        }
//        if (!TextUtils.isEmpty(msg) && msg.contains("非常感谢，您的回复已经发布，现在将转入主题页")) {
//            getResponse().isError = false;
//        } else if (!TextUtils.isEmpty(msg) && msg.contains("对不起，您两次发表间隔少于 30 秒，请不要灌水")) {
//            getResponse().errorMsg = "对不起，您两次发表间隔少于 30 秒，请不要灌水";
//            getResponse().isError = true;
//        }else {
//            getResponse().errorMsg = "未知错误！";
//            getResponse().isError = true;
//        }
        return null;
    }

    @Override
    public void addHeader() {
        super.addHeader();
        replayRequest = (ThreadRequest) getRequest();
        getHttpRequest().addHeader("Accept", "text/html, application/xhtml+xml, */*");
        getHttpRequest().addHeader("Accept-Encoding", "gzip, deflate");
        getHttpRequest().addHeader("Accept-Language", "zh-CN");
        getHttpRequest().addHeader("Connection", "Keep-Alive");
        getHttpRequest().addHeader("Host", "jx3.bbs.xoyo.com");
        getHttpRequest().addHeader("User-Agent",
                "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("formhash", replayRequest.formhash));
        nvps.add(new BasicNameValuePair("wysiwyg", "0"));        
        nvps.add(new BasicNameValuePair("updateswfattach", "0"));
        nvps.add(new BasicNameValuePair("subject",replayRequest.subject));
        nvps.add(new BasicNameValuePair("typeid", replayRequest.typeid));
        nvps.add(new BasicNameValuePair("checkbox", "0"));
        nvps.add(new BasicNameValuePair("message", replayRequest.Content));
        nvps.add(new BasicNameValuePair("localid[]", "1"));
        nvps.add(new BasicNameValuePair("usesig", "1"));  
        try {            
            post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

}
