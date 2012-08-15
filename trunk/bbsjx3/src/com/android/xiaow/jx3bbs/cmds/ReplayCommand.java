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
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.android.xiaow.core.cmds.BaseHttpCommand;
import com.android.xiaow.core.util.HttpUtil;

/**
 * @ClassName: ReplayCommand
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-15 下午11:28:39
 * 
 */
public class ReplayCommand extends BaseHttpCommand {
    ReplayRequest replayRequest;
    HttpPost post;

    @Override
    public void preExecute() {
        super.preExecute();
        post = new HttpPost(getRequest().url);
        setHttpRequest(post);
    }

    @Override
    public Object getSuccesData(HttpResponse response) throws Exception {
        Log.d("MSG", EntityUtils.getContentCharSet(response.getEntity()));
        Log.d("MSG", HttpUtil.getResultFormResponse(response));
        return null;
    }

    @Override
    public void addHeader() {
        super.addHeader();
        replayRequest = (ReplayRequest) getRequest();
        getHttpRequest().addHeader("Accept", "text/html, application/xhtml+xml, */*");
        getHttpRequest().addHeader("Accept-Encoding", "gzip, deflate");
        getHttpRequest().addHeader("Accept-Language", "zh-CN");
        getHttpRequest().addHeader("Connection", "Keep-Alive");
        getHttpRequest().addHeader("Host", "jx3.bbs.xoyo.com");
        getHttpRequest().addHeader("User-Agent",
                "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("formhash", replayRequest.formhash));
        nvps.add(new BasicNameValuePair("subject", replayRequest.subject));
        nvps.add(new BasicNameValuePair("usesig", replayRequest.usesig));
        try {
            nvps.add(new BasicNameValuePair("message", "Android回帖功能测试，路过+111"));
            post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            setHttpRequest(post);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

}
