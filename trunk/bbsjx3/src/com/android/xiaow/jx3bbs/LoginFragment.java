/**   
 * @Title: LoginFragment.java
 * @Package com.android.xiaow.jx3bbs
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-12 下午10:52:43
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs;

import java.net.URLDecoder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.xiaow.core.Controller;
import com.android.xiaow.core.cmds.AbstractHttpCommand;
import com.android.xiaow.core.util.ToastUtil;
import com.android.xiaow.jx3bbs.BranchListFragment.CallBack;
import com.android.xiaow.jx3bbs.model.MainArea;
import com.android.xiaow.jx3bbs.model.RefuseInfo;

/**
 * @ClassName: LoginFragment
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-12 下午10:52:43
 * 
 */
@SuppressLint("SetJavaScriptEnabled")
public class LoginFragment extends Fragment implements BranchListActivityCallBack {

    CallBack mCallBack;
    WebView mWebView;
    String cookie;
    LoginFinishCallBack loginFinishCallBack;

    private synchronized void finish() {
        if (loginFinishCallBack != null) {
            loginFinishCallBack.loginFinish();
            loginFinishCallBack = null;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        isShow = false;
        if (!(activity instanceof CallBack)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }
        if (activity instanceof LoginFinishCallBack) {
            loginFinishCallBack = (LoginFinishCallBack) activity;
        }
        mCallBack = (CallBack) activity;
    }

    ProgressBar progressBar;
    TextView tv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login, null, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        mWebView = (WebView) view.findViewById(R.id.webView1);
        tv = (TextView) view.findViewById(R.id.textView1);
        Log.d("MSG", "onCreateView-------->" + (view == null));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CookieSyncManager.createInstance(getActivity());
        CookieManager.getInstance().removeAllCookie();
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                super.onProgressChanged(view, newProgress);
                // Activity和Webview根据加载程度决定进度条的进度大小
                // 当加载到100%的时候 进度条自动消失
//                Log.d("MSG", "onCreateView-------->" + newProgress);
                progressBar.setProgress(newProgress );
                tv.setText(newProgress  + "%");
                if (newProgress >= 100) {
//                    Log.d("MSG", "onCreateView-------->View.GONE");

                    tv.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            }

        });
        mWebView.setWebViewClient(webViewClient);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setSaveFormData(false);
        mWebView.getSettings().setSavePassword(false);
        mWebView.loadUrl("https://my.xoyo.com/login/login/aHR0cCUzQSUyRiUyRmp4My5iYnMueG95by5jb20lMkZpbmRleC5waHA=__bbs");
    }

    @Override
    public void onPause() {
        super.onPause();
        CookieSyncManager.createInstance(getActivity()).stopSync();
    }

    @Override
    public void onResume() {
        super.onResume();
        CookieSyncManager.createInstance(getActivity()).startSync();
    }

    /**
     * @Title: isLogin
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @return
     */
    public static boolean isLogin() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Controller
                .getIntance());
        return !(TextUtils.isEmpty(sp.getString("nickname", null)) || TextUtils.isEmpty(sp
                .getString("cookies", null)));
    }

    @Override
    public void onReset() {
        if (mCallBack != null)
            mCallBack.resetEnd();
    }

    WebViewClient webViewClient = new WebViewClient() {

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            cookie = CookieManager.getInstance().getCookie(".xoyo.com");
            buildCookie();
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            Log.d("BBB", "onLoadResource," + url);
            cookie = CookieManager.getInstance().getCookie(".xoyo.com");
            buildCookie();

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d("MSG", "登陆成功");
            cookie = CookieManager.getInstance().getCookie(".xoyo.com");
            buildCookie();

        }

    };

    boolean isShow = false;

    @SuppressWarnings("deprecation")
    public void buildCookie() {
        cookie += ";" + CookieManager.getInstance().getCookie("jx3.bbs.xoyo.com");

        AbstractHttpCommand.set_cookie = cookie;
        String nickname = "";
        if (cookie.contains("nickname")) {
            int start = cookie.indexOf("nickname");
            int end = cookie.substring(start).indexOf(";");
            nickname = cookie.substring(start, end + start);
            nickname = nickname.substring(nickname.indexOf("=") + 1);
            nickname = URLDecoder.decode(nickname);
        }

        Editor editor = PreferenceManager.getDefaultSharedPreferences(Controller.getIntance())
                .edit();
        editor.putString("nickname", nickname);
        editor.putString("cookies", cookie);
        editor.commit();
        if (!TextUtils.isEmpty(nickname) && !isShow) {
            finish();
            isShow = true;
            ToastUtil.show("欢迎登陆：" + nickname);
        }
    }

    @Override
    public void loadBranch(MainArea branch) {

    }

    @Override
    public RefuseInfo getInfo() {
        return null;
    }

}
