/**
 * @author:xiaowei
 * @version:2012-8-5����4:03:53
 */
package com.android.xiaow.core.cmds.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.text.TextUtils;

import com.android.xiaow.core.cmds.BaseHttpCommand;
import com.android.xiaow.core.common.Response;
import com.android.xiaow.core.image.ImageRequest;

/**
 * @author xiaowei
 * 
 */
public class ImageDownCommand extends BaseHttpCommand {

    String url;
    String path;

    @Override
    public void execute() {
        prepare();
        File file = new File(path);
        if (file.exists() && file.length() > 0) {
            if (getListener() != null) {
                if (getResponse() == null) {
                    setResponse(new Response());
                    getResponse().isError = false;
                }
                getResponse().result = path;
                getListener().onSuccess(getResponse());
            }
        } else
            super.execute();
//        Log.d("MSG", path + "," + url);

    }

    @Override
    public Object getSuccesData(HttpResponse response) throws IllegalStateException, IOException {
        InputStream is = response.getEntity().getContent();
        if (!TextUtils.isEmpty(path)) {
            try {
                File file = new File(path);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.flush();
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return path;
        }
        return null;
    }

    @Override
    public void prepare() {
        ImageRequest request = (ImageRequest) getRequest();
        if (request != null) {
            url = request.url;
            path = request.path;
            setHttpRequest(new HttpGet(url));
        }
    }

    @Override
    public void onAfterExecute() {
        // TODO Auto-generated method stub

    }

}
