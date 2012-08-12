package com.android.xiaow.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpUtil {

	public static String loadContent(String url) {
		StringBuffer sBuffer = new StringBuffer();
		HttpGet request = new HttpGet(url.trim());
		HttpClient client = new DefaultHttpClient();
		try {
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

				sBuffer.append(EntityUtils.toString(response.getEntity()));
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sBuffer.toString();
	}

	public static String getUrl(String str) {
		Pattern pattern = Pattern.compile("http://[^\"]+");
		Matcher matcher = pattern.matcher(str);
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}

	public static void downFileFormUrl(String url, String path) {
		downFileFormUrl(url, path, "");
	}

	public static void downFileFormUrl(String url, String path, String cookies) {
		File file = new File(path);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		} else {
			file.deleteOnExit();
		}

		try {
			HttpGet request = new HttpGet(url.trim());
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(request);
			request.setHeader("Connection", HTTP.CONN_KEEP_ALIVE);
			request.setHeader("Set-Cookie", cookies);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

				InputStream is = response.getEntity().getContent();
				byte[] bs = new byte[1024];
				OutputStream out = new FileOutputStream(file);
				while ((is.read(bs)) != -1) {
					out.write(bs);
				}
				out.close();
				is.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean validStr(String str) {
		if (str == null || "".equals(str.trim())) {
			return false;
		}
		return true;
	}

	public static int findNumByStr(String str) {
		if (!validStr(str)) {
			return 0;
		}
		str = str.trim();
		Pattern pattern = Pattern.compile("[0-9]+");
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			String num = matcher.group();
			return Integer.parseInt(num);
		}
		return 0;
	}

	public static <T> List<T> copyListByIndex(List<T> list, int from, int to) {
		List<T> dest = new ArrayList<T>();
		int index = from;
		while (index < to) {
			if (index >= list.size())
				break;
			dest.add(list.get(index));
			index++;
		}
		return dest;
	}

	public static Date pareString(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-DD hh:mm");
		try {
			return sdf.parse(str);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}

	public static String getResultFormResponse(HttpResponse response) {
		StringBuffer buffer = new StringBuffer();
		try {
			if (response.getEntity().getContentEncoding() != null
					&& response.getEntity().getContentEncoding().getValue()
							.contains("gzip")) {
				String str = "";
				InputStream is = new GZIPInputStream(response.getEntity()
						.getContent());
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));
				while ((str = reader.readLine()) != null) {
					buffer.append(str);
				}
				is.close();
				reader.close();
				Log.d("BUG", "_________gzip______________");
			} else {
				buffer.append(EntityUtils.toString(response.getEntity()));
				Log.d("BUG", "_______no Gzip____________");
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}
}
