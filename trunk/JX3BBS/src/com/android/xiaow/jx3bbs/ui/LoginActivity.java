package com.android.xiaow.jx3bbs.ui;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.HttpAuthHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.xiaow.jx3bbs.R;
import com.android.xiaow.mvc.command.AbstractHttpCommand;

public class LoginActivity extends Activity {

	WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login2);
		webView = (WebView) findViewById(R.id.webView1);
		webView.setWebViewClient(webViewClient);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setAppCacheEnabled(true);
		webView.getSettings().setBuiltInZoomControls(true);
		if (isLogin()) {
			Toast.makeText(this, "ÒÑ¾­µÇÂ¼", 1).show();
			finish();
		} else
			webView.loadUrl("https://my.xoyo.com/login/login/aHR0cCUzQSUyRiUyRmp4My5iYnMueG95by5jb20lMkZpbmRleC5waHA=__bbs");
	}

	private String cookie;

	WebViewClient webViewClient = new WebViewClient() {

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);
			Log.d("BBB", "onPageFinished," + url);
			CookieManager cm = CookieManager.getInstance();
			cookie = CookieManager.getInstance().getCookie(".xoyo.com");
			Log.d("BBB", cookie);
			Log.d("WebView", "onPageFinished ");
			if (url.contains("jx3.bbs.xoyo.com")) {
				Log.d("MSG", "µÇÂ½³É¹¦");
				Log.d("MSG", cookie);
				finish();
			}
		}

		@Override
		public void onLoadResource(WebView view, String url) {
			super.onLoadResource(view, url);
			Log.d("BBB", "onLoadResource," + url);
			if (url.contains("jx3.bbs.xoyo.com")) {
				cookie = CookieManager.getInstance().getCookie(".xoyo.com");
				Log.d("MSG", "onLoadResource£º" + cookie);
				System.out.println(cookie);
				buildCookie();
			}
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			Log.d("BBB", "onPageStarted," + url);
			if (url.contains("jx3.bbs.xoyo.com")) {
				Log.d("MSG", "µÇÂ½³É¹¦");
				cookie = CookieManager.getInstance().getCookie(".xoyo.com");
				Log.d("MSG", cookie);
				System.out.println(cookie);
				buildCookie();
				finish();

			}
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			Log.d("BBB", "onReceivedError," + description + "," + failingUrl);
		}

		@Override
		public void onReceivedHttpAuthRequest(WebView view,
				HttpAuthHandler handler, String host, String realm) {
			super.onReceivedHttpAuthRequest(view, handler, host, realm);
			Log.d("BBB", "onReceivedHttpAuthRequest," + host + "," + realm);
		}

	};

	public boolean isLogin() {
		cookie = CookieManager.getInstance().getCookie(".xoyo.com");
		if (TextUtils.isEmpty(cookie)) {
			return false;
		}
		buildCookie();
		return true;
	}

	public void buildCookie() {
		cookie += ";"
				+ CookieManager.getInstance().getCookie("jx3.bbs.xoyo.com");

		AbstractHttpCommand.set_cookie = cookie;
		String nickname = "";
		if (cookie.contains("nickname")) {
			int start = cookie.indexOf("nickname");
			int end = cookie.substring(start).indexOf(";");
			nickname = cookie.substring(start, end + start);
			nickname = nickname.substring(nickname.indexOf("=") + 1);
			nickname= URLDecoder.decode(nickname);
		}
		Editor editor = getSharedPreferences("JX3BBS", Context.MODE_APPEND)
				.edit();
		editor.putString("nickname", nickname);
		editor.putString("cookies", AbstractHttpCommand.set_cookie);
		editor.commit();
		Toast.makeText(this, "»¶Ó­µÇÂ½£º" + nickname, 0).show();
		Log.d("MSG", cookie + "");
	}

}
