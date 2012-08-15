/**   
 * @Title: AsyncImageLoad.java
 * @Package com.android.xiaow.jx3bbs
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-12 下午9:24:54
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs;

import java.io.File;
import java.security.MessageDigest;

import com.android.xiaow.core.Controller;
import com.android.xiaow.core.image.ImageCallBack;
import com.android.xiaow.core.util.NetUtil;

import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

/**
 * @ClassName: AsyncImageLoad
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-12 下午9:24:54
 * 
 */
public class AsyncImageLoad {

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

    /**
     * <string-array name="setting_list_item_name"> <item>仅在wifi下，下载图片和头像</item>
     * <item>仅下载图片</item> <item>仅下载头像</item> <item>下载图片和头像</item>
     * </string-array>
     */
    public static final void LoadImage(String url, int type, ImageView imageView,
            ImageCallBack callBack) {
        if (TextUtils.isEmpty(url))
            return;
        if (!url.startsWith("http://")) {
            url = "http://jx3.bbs.xyo.com/" + url;
        }
        url = url.trim();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Controller
                .getIntance());
        int mode = Integer.parseInt(sp.getString("image_down", "0"));
        boolean down = false;
        if (mode == 0 && NetUtil.checkWifi()) {
            down = true;
        } else if (mode == 1 &&( type == IMAGE || type == SMILE)) {
            down = true;
        } else if (mode == 2 && (type == GRAVATAR || type == SMILE)) {
            down = true;
        } else if (mode == 3) {
            down = true;
        }
        Log.d("MSG", "mode:" + mode + "," + NetUtil.checkWifi() + "," + type+","+down);

        com.android.xiaow.core.image.AsyncImageLoad.getIntance().loadImage(url,
                getPathByType(url, type), imageView, callBack, down);
    }

    public static final String getPathByType(String url, int type) {
        String diff = url.substring(url.lastIndexOf("."));
        if (diff.toLowerCase().contains(".jpg") || diff.toUpperCase().contains(".JPEG")) {
            diff = ".jpg";
        } else if (diff.toLowerCase().contains(".bmp")) {
            diff = ".bmp";
        } else if (diff.toLowerCase().contains(".gif")) {
            diff = ".gif";
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append(new File(Environment.getExternalStorageDirectory(), "/JX3BBS").getPath());
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
}
