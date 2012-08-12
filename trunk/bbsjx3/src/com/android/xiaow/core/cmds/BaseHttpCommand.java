/**
 * @author:xiaowei
 * @version:2012-8-5下午4:06:16
 */
package com.android.xiaow.core.cmds;

import static com.android.xiaow.core.Config.TIME_OUT;

import java.net.SocketTimeoutException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import android.text.TextUtils;
import android.util.Log;

import com.android.xiaow.core.common.Response;
import com.android.xiaow.core.util.NetUtil;

/**
 * @author xiaowei
 * 
 */

public abstract class BaseHttpCommand extends AbstractHttpCommand {

    @Override
    public void preExecute() {

    }

    @Override
    public void addHeader() {
       
    }

    @Override
    public void AfterExecute() {

    }

    @Override
    public void go() {
        DefaultHttpClient client = new DefaultHttpClient();
        try {
            if (!TextUtils.isEmpty(set_cookie)) {
                request.setHeader("Set-Cookie", set_cookie);
                request.setHeader("Cookie", set_cookie);
            }
            client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TIME_OUT);
            client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, TIME_OUT);
            if (!NetUtil.checkNet()) {
                Response data = new Response();
                data.isError = true;
                data.errorMsg = "网络连接异常";
                setResponse(data);
                return;
            }
            response = client.execute(getHttpRequest());
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                Object obj = getSuccesData(response);
                if (getResponse() == null) {
                    setResponse(new Response());
                }
                getResponse().result = obj;
                getResponse().isError = false;
            } else {
                Response data = new Response();
                data.isError = true;
                data.errorMsg = "服务器异常";
                Log.i(getClass().getSimpleName(), "HTTP ERROR CODE:"
                        + response.getStatusLine().getStatusCode());
                setResponse(data);
            }
        } catch (ConnectTimeoutException e) {
            e.printStackTrace();
            if (getResponse() == null) {
                setResponse(new Response());
            }
            Response responseData = getResponse();
            responseData.isError = true;
            responseData.errorMsg = "连接服务器超时！";
            setResponse(responseData);
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            if (getResponse() == null) {
                setResponse(new Response());
            }
            Response responseData = getResponse();
            responseData.isError = true;
            responseData.errorMsg = "连接服务器超时！";
            setResponse(responseData);
        } catch (java.net.ConnectException e) {
            e.printStackTrace();
            if (getResponse() == null) {
                setResponse(new Response());
            }
            Response responseData = getResponse();
            responseData.isError = true;
            responseData.errorMsg = "无法连接到服务器！";
            setResponse(responseData);
        } catch (Exception e) {
            e.printStackTrace();
            if (getResponse() == null) {
                setResponse(new Response());
            }
            Response responseData = getResponse();
            responseData.isError = true;
            responseData.errorMsg = "连接服务器失败，请检查网络连接！";
            setResponse(responseData);
        }

    }

    public abstract Object getSuccesData(HttpResponse response) throws Exception;

}