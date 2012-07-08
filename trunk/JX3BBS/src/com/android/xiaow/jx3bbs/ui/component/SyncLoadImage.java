package com.android.xiaow.jx3bbs.ui.component;

import java.io.File;
import java.lang.ref.SoftReference;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;

import android.database.DataSetObservable;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.android.xiaow.jx3bbs.Config;
import com.android.xiaow.jx3bbs.command.ImageThread;
import com.android.xiaow.jx3bbs.utils.NetUtil;
import com.android.xiaow.mvc.command.ICommand;
import com.android.xiaow.mvc.common.IResponseListener;
import com.android.xiaow.mvc.common.Request;
import com.android.xiaow.mvc.common.Response;

public class SyncLoadImage implements IResponseListener {
	/**
	 * 头像
	 */
	public static final int GRAVATAR = 0;
	/**
	 * 表情
	 */
	public static final int SMILE = 1;
	/**
	 * 普通图片
	 */
	public static final int IMAGE = 2;

	private HashMap<String, SoftReference<Bitmap>> imagecache;

	private HashMap<String, DataSetObservable> mDataSetObservable;
	private Handler mHandler;
	private static SyncLoadImage instance;

	public static final SyncLoadImage getIntance() {
		return instance == null ? new SyncLoadImage() : instance;
	}

	private SyncLoadImage() {
		mDataSetObservable = new HashMap<String, DataSetObservable>();
		imagecache = new HashMap<String, SoftReference<Bitmap>>();
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				DataSetObservable observable = mDataSetObservable.get(msg.obj
						.toString());
				if (observable != null)
					observable.notifyChanged();
			}

		};
	}

	/**
	 * 
	 * @param url
	 *            加载图片的地址
	 * @param observer
	 *            image观察者，用来更新图片
	 * @param type
	 *            图片类型
	 */
	@SuppressWarnings("deprecation")
	public synchronized void LoadBitmap(String url, ImageViewObserver observer,
			int type) {
		if (TextUtils.isEmpty(url) || observer == null)
			return;
		if (!url.startsWith("http://")) {
			url = "http://jx3.bbs.xyo.com/" + url;
		}
		url = url.trim();
		// 添加图片下载控制
		if (!checkType(type))
			return;
		String filePath = getPathByType(url, type);
		if (imagecache.containsKey(filePath)) {
			SoftReference<Bitmap> softReference = imagecache.get(url);
			Bitmap bitmap = softReference.get();
			observer.mDrawable = new BitmapDrawable(bitmap);
			observer.filePath = null;
			observer.onChanged();
			return;
		} else {
			observer.init(filePath);
			if (new File(filePath).exists()) {
				observer.onChanged();
				return;
			}
			DataSetObservable observable = mDataSetObservable.get(url);
			if (observable == null) {
				observable = new DataSetObservable();
			} else {
				observable.registerObserver(observer);
				mDataSetObservable.put(url, observable);
				return;
			}
			observable.registerObserver(observer);
			mDataSetObservable.put(url, observable);
		}

		/**
		 * 异步加载图片
		 */
		Request request = new Request();
		request.setData(url);
		request.setTag(filePath);
		System.out.println("图片下载。。。。。。。。");
		ImageThread.enqueueCommand(request, this);
	}

	class imageRunnable implements Runnable {
		ICommand command;

		public imageRunnable(ICommand command) {
			super();
			this.command = command;
		}

		@Override
		public void run() {
			if (command != null)
				command.execute();
		}
	};

	public boolean checkType(int type) {
		if (Config.ALL)
			return true;
		if (Config.ALL_IN_WIFI)
			return NetUtil.checkWifi();
		switch (type) {
		case GRAVATAR:
			return Config.ONLY_GRAVATAR;
		case SMILE:
			return true;
		default:
			return Config.ONLY_IMAGE;
		}
	}

	@Override
	public void onSuccess(Response response) {
		if (response.getData() == null) {
			onError(response);
			return;
		}
		Message msg = new Message();
		msg.what = 0x100;
		msg.obj = response.getData();
		mHandler.sendMessage(msg);
	}

	@Override
	public void onError(Response response) {

	}

	public static final String getPathByType(String url, int type) {
		String diff = url.substring(url.lastIndexOf("."));
		if (diff.toLowerCase().contains(".jpg")
				|| diff.toUpperCase().contains(".JPEG")) {
			diff = ".jpg";
		} else if (diff.toLowerCase().contains(".bmp")) {
			diff = ".bmp";
		} else if (diff.toLowerCase().contains(".gif")) {
			diff = ".gif";
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append(new File(Environment.getExternalStorageDirectory(),
				"/JX3BBS").getPath());
		switch (type) {
		case GRAVATAR:
			buffer.append(File.separator + "头像/");
			break;
		case SMILE:
			buffer.append(File.separator + "表情/");
			break;
		default:
			buffer.append(File.separator + "图片/");
			break;
		}
		buffer.append(MD5(url) + diff);
		return buffer.toString();
	}

	// MD5加密，32位
	public static String MD5(String str) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

		char[] charArray = str.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);

		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}

	// 可逆的加密算法
	public static String encryptmd5(String str) {
		char[] a = str.toCharArray();
		for (int i = 0; i < a.length; i++) {
			a[i] = (char) (a[i] ^ 'l');
		}
		String s = new String(a);
		return s;
	}

	public void equen(String filePath, Bitmap drawable) {
		imagecache.put(filePath, new SoftReference<Bitmap>(drawable));
	}

	public void clear() {
		Iterator<SoftReference<Bitmap>> it = imagecache.values().iterator();
		while (it.hasNext()) {
			SoftReference<Bitmap> soft = it.next();
			soft.get().recycle();
		}
		imagecache.clear();
	}

}
