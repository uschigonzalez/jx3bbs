/**   
 * @Title: AsyncImageLoad.java 
 * @Package com.hiker.onebyone.data.image 
 * @Description: TODO
 * @author xiaowei   
 * @date 2012-7-16 下午5:13:12 
 * @version V1.0   
 */
package com.android.xiaow.core.image;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.ImageView;

import com.android.xiaow.core.Controller;
import com.android.xiaow.core.Initializer;
import com.android.xiaow.core.common.IResponseListener;
import com.android.xiaow.core.common.Response;
import com.android.xiaow.core.threads.Manager;

/**
 * 
 * @author 作者 xiaowei
 * @创建时间 2012-7-16 下午5:13:12 类说明
 * 
 */
public class AsyncImageLoad {
    public BitmapCache cache = BitmapCache.getInstance();

    static AsyncImageLoad load;

    public static AsyncImageLoad getIntance() {
        return load == null ? load = new AsyncImageLoad() : load;
    }

    /**
     * 
     * @Title: loadImage
     * @Description: 加载图片
     * @param url
     *            图片的地址
     * @param imageView
     * @param callBack
     *            void 返回类型
     * @author xiaowei
     * @date 2012-7-16 下午5:40:49
     */
    public void loadImage(String url, String path, ImageView mView, ImageCallBack callBack) {
        loadImage(url, path, mView, callBack, true);
    }

    public void loadImage(String url, String path, ImageView mView, ImageCallBack callBack,
            boolean down) {
        if (TextUtils.isEmpty(url))
            return;
        if (mView != null)
            mView.setTag(path);
        if (cached(url, mView, callBack)) {
            return;
        } else if (hasDown(url, path, mView, callBack)) {
            return;
        } else if (down)
            down(url, path, mView, callBack);
    }

    /** 图片是否已经下载过 */
    private boolean hasDown(String url, String path, ImageView mView, ImageCallBack callBack) {
        File file = new File(path);
        if (!file.exists())
            return false;
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        cache.putBitmap(url, bitmap);
        cached(url, mView, callBack);
        return true;
    }

    /** 图片是否已经缓存 */
    private boolean cached(String url, ImageView mView, ImageCallBack callBack) {
        Bitmap bitmap = cache.getBitmap(url);
        if (bitmap == null)
            return false;
        ImageRunable runable = new ImageRunable(mView, bitmap, callBack);
        Controller.getIntance().handler.post(runable);
        return true;
    }

    /** 图片下载 */
    private void down(String url, String path, ImageView mView, ImageCallBack callBack) {
        ImageRequest request = new ImageRequest();
        request.url = url;
        request.path = path;
        ImageDownListener listener = new ImageDownListener(url, path, mView, callBack);
        Manager.getIntance().registerCommand(Initializer.IMAGE_CMD_ID, request, listener);
    }

    /** 图片下载监听 */
    class ImageDownListener implements IResponseListener {
        String url;
        String path;
        ImageView mView;
        ImageCallBack callBack;

        public ImageDownListener(String url, String path, ImageView mView, ImageCallBack callBack) {
            super();
            this.url = url;
            this.path = path;
            this.mView = mView;
            this.callBack = callBack;
        }

        @Override
        public void onSuccess(Response response) {
            if (response.result != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                cache.putBitmap(url, bitmap);
                cached(url, mView, callBack);
            }
        }

        @Override
        public void onError(Response response) {

        }
    }

    /** 用户线程执行 */
    class ImageRunable implements Runnable {

        ImageView mView;
        Bitmap bitmap;
        ImageCallBack callBack;

        public ImageRunable(ImageView mView, Bitmap bitmap, ImageCallBack callBack) {
            super();
            this.mView = mView;
            this.bitmap = bitmap;
            this.callBack = callBack;
        }

        @Override
        public void run() {
            if (mView != null) {
                mView.setImageBitmap(bitmap);
                mView.postInvalidate();
            }
            if (callBack != null)
                callBack.callback(bitmap);
        }

    }

}
