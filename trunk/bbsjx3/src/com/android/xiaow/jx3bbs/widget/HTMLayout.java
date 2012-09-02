/**   
 * @Title: HTMLayout.java
 * @Package com.android.xiaow.jx3bbs.widget
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-12 下午9:13:47
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs.widget;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.xiaow.jx3bbs.AsyncImageLoad;
import com.android.xiaow.jx3bbs.R;
import com.android.xiaow.jx3bbs.UrlDrawable;
import com.android.xiaow.jx3bbs.UrldrawableObserver;

/**
 * @ClassName: HTMLayout
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-12 下午9:13:47
 * 
 */
public class HTMLayout extends LinearLayout {
    public static final String regex_img_0 = "<\\s*img[^>]*>";
    public static final String REGEX_EMPTY_STRING = "<[^/>]*></[^>]*>";
    public static final String SPL_STRING = "http://pic.xoyo.com/bbs/images/default/attachimg.gif";
    public static final String SPL_DOWNLOAD = "<strong>下载</strong><[^d]*[\\s]*div[^>]*>[^.]*[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}[^.]*[0-9]{1,2}:[0-9]{1,2}[^.]*</div>";
    public static final String SPL_URL = "(http|https|ftp|file){1}(:\\/\\/)?([\\da-z-\\.]+)\\.([a-z]{2,6})([\\/\\w \\.-?&%-=]*)*\\/?";
    String content;
    ArrayList<String> data = new ArrayList<String>();
    Activity mActivity;

    /**
     * @param context
     */
    public HTMLayout(Activity context) {
        super(context);
        mActivity = context;
    }

    public HTMLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Activity getActivity() {
        return mActivity;
    }

    public void setActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    boolean isfresh = false;

    public String getCopyContent() {
        if (TextUtils.isEmpty(copyString))
            return "";
        return copyString.replaceAll("<[^>]*>", "");
    }

    String copyString;
    public void LoadHTML(String str) {
        content = str;
        copyString=str;
        isfresh = false;
        reset();
        if (content.contains("<blockquote>")) {
            String[] strs = content.split("<[^>]*blockquote>");
            boolean flag = false;
            for (String str1 : strs) {
                if (flag) {
                    data.add("<blockquote>" + str1 + "</blockquote>");
                    flag = false;
                } else
                    parserContent(str1);
                if (str1.contains("<div class=\"quote\">")) {
                    flag = true;
                }
            }
        } else
            parserContent(content);
        loadView();
    }

    public void LoadHTML(InputStream is) {
        LoadHTML(loadSrc(is));
    }

    public void reset() {
        data.clear();
        this.removeAllViews();
    }

    private String loadSrc(InputStream is) {
        String str = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuffer buffer = new StringBuffer();
            String str1 = "";
            while ((str1 = reader.readLine()) != null) {
                buffer.append(str1);
            }
            str = buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    public void parserContent(String src) {
        src = src.replace("src=\"images", "src=\"http://jx3.bbs.xoyo.com/images");
        Matcher m = Pattern.compile(regex_img_0).matcher(src);
        int src_len = src.length();
        int src_start = 0;
        while (m.find()) {
            String str = m.group();
            if (str.contains("http://jx3.bbs.xoyo.com/")
                    && !(str.contains("file=") && !str.contains("file=\"http://jx3.bbs"))) {
                continue;
            }
            int start = m.start();
            int end = m.end();
            String str1 = "";
            if (start > src_start) {
                str1 = src.substring(0, start - src_start);
            }
            src = src.substring(end - src_start);
            int length = 0;
            while (src.startsWith("</")) {
                if (src.startsWith("</img>")) {
                    src = src.replace("</img>", "");
                }
                Matcher ma = Pattern.compile("</[^>]+>").matcher(src);
                while (ma.find()) {
                    if (ma.start() == 0 || src.substring(0, ma.start()).matches("</[^>]+>")) {
                        String tmp = ma.group();
                        str1 += tmp;
                        length += tmp.length();
                    } else {
                        break;
                    }
                }
                src = src.substring(length);
                length = 0;
            }
            if (!TextUtils.isEmpty(str1)) {
                data.add(str1.trim());
            }
            data.add(str.trim());
            src_start = src_len - src.length();
        }
        if (!TextUtils.isEmpty(src))
            data.add(src.trim());
    }

    public void loadView() {
        int len = data.size();
        String str = "";
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.topMargin = 10;
        for (int i = 0; i < len; i++) {
            str = data.get(i);
            if (str.startsWith("<blockquote>")) {
                str = str.replaceAll("<[^>]*blockquote>", "");
                str = str.replaceAll(
                        "<img[^>]*>", "");
                TextView textView = (TextView)LayoutInflater.from(getContext()).inflate(R.layout.simple_list_item_2, null,false);
                int padding = getResources().getDimensionPixelOffset(R.dimen.padding_small);
                textView.setPadding(padding, padding, padding, padding);
                textView.setBackgroundResource(R.drawable.refuse_info);
                Spanned spanned = Html.fromHtml(str);
                textView.setText(spanned);
                addView(textView);
                
                continue;
            }
            str = str.replaceAll(REGEX_EMPTY_STRING, "");
            if (str.matches(regex_img_0)) {
                ImageView imv = new ImageView(getContext());
                imv.setScaleType(ScaleType.CENTER_INSIDE);
                imv.setLayoutParams(params);
                imv.setImageResource(R.drawable.back);
                String url = "";
                if (str.contains("file=\"")) {
                    url = str.substring(str.indexOf("file=\""));
                    url = url.substring(6, url.indexOf("\"", 10));
                } else if (str.contains("src=\"")) {
                    url = str.substring(str.indexOf("src=\""));
                    url = url.substring(5, url.indexOf("\"", 10));
                }
                if (url.contains(SPL_STRING)) {
                    str = "";
                    continue;
                }
                AsyncImageLoad.LoadImage(url, AsyncImageLoad.IMAGE, imv, null);
                // AsyncImageLoad.getIntance().loadImage(url, path, imv, null);
                // ImageViewObserver observer = new ImageViewObserver(imv,
                // true);
                // SyncLoadImage.getIntance().LoadBitmap(url, observer,
                // SyncLoadImage.IMAGE);
                // imv.setTag(SyncLoadImage
                // .getPathByType(url, SyncLoadImage.IMAGE));
                imv.setFocusable(false);
                imv.setOnClickListener(mImageClickListener);
                addView(imv);
            } else {
                str = str.replaceAll(SPL_DOWNLOAD, "").trim();
                if (TextUtils.isEmpty(str)) {
                    continue;
                }
                str = str.replaceAll("<div[^>]*>", "");
                str = str.replaceAll("</div>", "");
                if (TextUtils.isEmpty(str.replaceAll("<[^>]*>", "").trim())) {
                    continue;
                }
                if (str.contains("<blockquote>")) {
                    str = str.replace("<blockquote>", "<blockquote bgcolor=\"#88BABCBC\">");
                }
                List<String> s1 = new ArrayList<String>();
                if (str.contains("<img")) {
                    String[] s0 = str.split("<img[^>]*>");
                    Matcher ma = Pattern.compile("<img[^>]*>").matcher(str);
                    int t_i = 0;
                    if (ma.find()) {
                        if (t_i < s0.length) {
                            s1.add(s0[t_i]);
                        }
                        s1.add(ma.group());
                        t_i++;
                    }
                } else {
                    s1.add(str);
                }
                TextView tv =  (TextView)LayoutInflater.from(getContext()).inflate(R.layout.simple_list_item_2, null,false);
                tv.setFocusable(true);
                tv.setFocusableInTouchMode(true);
                // SpannableStringBuilder spanned = new
                // SpannableStringBuilder();
                // for (int j = 0; j < s1.size(); j++) {
                // String s0 = s1.get(j).trim();
                // if (s0.startsWith("<img")) {
                // UrlDrawable urlDrawable = new UrlDrawable();
                // urlDrawable.drawable = getResources().getDrawable(
                // R.drawable.j01);
                // int start = s0.indexOf("\"");
                // int end = s0.indexOf("\"", start + 2);
                // String url = s0.substring(start + 1, end);
                // ImageGetter imageGetter = new ImageGetter();
                // imageGetter.view = tv;
                // SpannableString spannableString = new SpannableString(
                // "face");
                // ImageSpan span = new ImageSpan(urlDrawable,
                // ImageSpan.ALIGN_BOTTOM);
                // spannableString.setSpan(span, 0, 4,
                // Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                // UrldrawableObserver observer = new UrldrawableObserver(
                // tv, urlDrawable);
                // SyncLoadImage.getIntance().LoadBitmap(url, observer,
                // SyncLoadImage.SMILE);
                // spanned.append(spannableString);
                // } else {
                // ImageGetter mImageGetter = new ImageGetter();
                // mImageGetter.view = tv;
                // Spanned sp = Html.fromHtml(s0, mImageGetter, null);
                // spanned.append(sp);
                // }
                // }
                ImageGetter mImageGetter = new ImageGetter();
                mImageGetter.view = tv;
                Spanned spanned = Html.fromHtml(str, mImageGetter, null);
                spanned.getSpanFlags(Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(spanned);
                addView(tv);

            }
        }
    }

    View.OnClickListener textClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            String content = ((TextView) v).getText().toString();
            List<String> str = new ArrayList<String>();
            Matcher ma = Pattern.compile(SPL_URL).matcher(content);
            while (ma.find()) {
                str.add(ma.group());
            }
            if (str.size() < 1)
                return;
            if (str.size() == 1) {
                Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(str.get(0)));
                it.addCategory(Intent.CATEGORY_BROWSABLE).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(it);
            } else {
                // Dialog dialog = new Dialog(getContext(),
                // R.style.NoTitleDialogTheme);
                // LinearLayout layout = new LinearLayout(getContext());
                // for (String string : str) {
                // TextView textView = new TextView(getContext());
                // textView.setAutoLinkMask(Linkify.ALL);
                // }
            }
        }
    };

    public void loadFinish() {
        if (isfresh)
            return;
        isfresh = true;
        reset();
        parserContent(content);
        loadView();
    }

    View.OnClickListener mImageClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            String url = v.getTag().toString();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
            mActivity.startActivityForResult(intent, 0);
            // Intent intent = new Intent(mActivity, ZoomActivity.class);
            // intent.putExtra("url", url);
            // mActivity.startActivity(intent);
        }
    };

    class ImageGetter implements Html.ImageGetter {
        View view;
        UrlDrawable urlDrawable;

        @Override
        public Drawable getDrawable(String source) {
            if (!TextUtils.isEmpty(source)) {
                if (urlDrawable == null)
                    urlDrawable = new UrlDrawable();
                urlDrawable.drawable = getResources().getDrawable(R.drawable.j01);
                UrldrawableObserver observer = new UrldrawableObserver(view, urlDrawable);
                AsyncImageLoad.LoadImage(source, AsyncImageLoad.SMILE, null, observer);
                return urlDrawable;
            }
            return null;
        }
    };
    /**
     * Html.ImageGetter mImageGetter = new Html.ImageGetter() {
     * 
     * @Override public Drawable getDrawable(String source) { Drawable drawable
     *           = null; if ((drawable = SyncLoadImage.loadDrawble(source)) !=
     *           null) { return drawable; } return new
     *           BitmapDrawable(getResources()); } };
     */
}
