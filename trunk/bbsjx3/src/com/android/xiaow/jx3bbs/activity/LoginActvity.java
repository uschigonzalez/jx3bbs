/**   
 * @Title: LoginFragment.java
 * @Package com.android.xiaow.jx3bbs
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-12 下午10:52:43
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs.activity;

import java.net.URLDecoder;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.xiaow.core.BaseFragmentActivity;
import com.android.xiaow.core.Controller;
import com.android.xiaow.core.cmds.AbstractHttpCommand;
import com.android.xiaow.core.util.ToastUtil;
import com.android.xiaow.jx3bbs.R;

/**
 * @ClassName: LoginFragment
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-12 下午10:52:43
 * 
 */
@SuppressLint("SetJavaScriptEnabled")
public class LoginActvity extends BaseFragmentActivity {

    WebView mWebView;
    String cookie;
    public static final int RESULT_OK = 100;

    ProgressBar progressBar;
    TextView tv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        mWebView = (WebView) findViewById(R.id.webView1);
        tv = (TextView) findViewById(R.id.textView1);
        CookieSyncManager.createInstance(this);
        CookieManager.getInstance().removeAllCookie();
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                progressBar.setProgress(newProgress);
                tv.setText(newProgress + "%");
                if (newProgress >= 100) {
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
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.loadUrl("https://my.xoyo.com/login/login/aHR0cCUzQSUyRiUyRmp4My5iYnMueG95by5jb20lMkZpbmRleC5waHA=__bbs");
    }

    @Override
    public void onPause() {
        super.onPause();
        CookieSyncManager.createInstance(this).stopSync();
    }

    @Override
    public void onResume() {
        super.onResume();
        CookieSyncManager.createInstance(this).startSync();
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

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("MSG", "shouldOverrideUrlLoading------>" + url);
            Log.d("MSG", "登陆成功");
            cookie = CookieManager.getInstance().getCookie(".xoyo.com");
            buildCookie();
            return true;
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
            setResult(RESULT_OK);
            isShow = true;
            ToastUtil.show("欢迎登陆：" + nickname);
            finish();

        }
    }

}
