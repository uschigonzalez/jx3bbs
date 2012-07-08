package com.android.xiaow.jx3bbs.command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import android.text.TextUtils;

import com.android.xiaow.jx3bbs.utils.HttpUtil;
import com.android.xiaow.mvc.command.AbstractHttpCommand;

public class ImageDownLoad extends AbstractHttpCommand {

	@Override
	protected HttpRequestBase getHttpRequest() {
		if (getRequest().getData() != null
				&& HttpUtil.validStr(getRequest().getData().toString())) {
			return new HttpGet(getRequest().getData().toString());
		}
		return new HttpGet(getURI());
	}

	@Override
	protected Object getSuccessResponse(HttpResponse response) {
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			InputStream is = null;
			try {
				if (response.getEntity().getContentEncoding() != null
						&& response.getEntity().getContentEncoding().getValue()
								.contains("gzip")) {
					is = new GZIPInputStream(response.getEntity().getContent());
				}
				if (is == null)
					is = response.getEntity().getContent();
				String filePath = getRequest().getTag().toString();
				if (TextUtils.isEmpty(filePath)) {
					return null;
				}
				File file = new File(filePath);
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				byte[] buffer = new byte[1024];
				FileOutputStream fos = new FileOutputStream(file);
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				is.close();
				fos.close();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
//		Bitmap bitmap = BitmapFactory.decodeFile(getRequest().getTag()
//				.toString());
//		if (bitmap.getWidth() > 400 || bitmap.getHeight() > 400) {
//			Bitmap bitmap2 = ThumbnailUtils.extractThumbnail(bitmap, 400,
//					bitmap.getHeight() * 400 / bitmap.getWidth());
//			File file=new File(getRequest().getTag().toString());
//			file.deleteOnExit();
//			try {
//				FileOutputStream out=new FileOutputStream(file);
//				bitmap2.compress(Bitmap.CompressFormat.PNG, 70, out);
//				out.close();
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		bitmap.recycle();
		return getRequest().getData();
	}

	@Override
	protected byte[] getBody() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

}
