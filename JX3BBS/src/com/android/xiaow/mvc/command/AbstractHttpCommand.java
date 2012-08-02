package com.android.xiaow.mvc.command;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;

import com.android.xiaow.jx3bbs.utils.NetUtil;
import com.android.xiaow.mvc.common.Response;

/**
 * <pre>
 * Steps:
 * 1. Prepare - Initialize headers. Sub-classes can override this method parse
 * request etc
 * 2. onBeforeExecute:
 * 2.1. Get the URI
 * 2.2. Get HTTP command
 * 2.3. Add request to headers
 * 2.4. onBeforeExecute({@link HttpRequestBase}) -- Can add body and other
 * headers if need be etc
 * 3. go:
 * 3.1. Instantiate {@link HttpClient} 3.2. Execute the client using
 * http-request
 * 3.3. Validate the response (200)
 * 3.4. If valid, {@link #getSuccessResponse(HttpResponse)} or else
 * {@link #getErrorResponse(Exception)} or
 * {@link #getErrorResponse(HttpResponse)} 3.5. Set the response
 * 4. onAfterExecute:
 * Nothing special to be done. And also, don't override
 * {@link #notifyListener(boolean)} - let notification happen
 * </pre>
 * 
 * @author Gaurav Vaish
 * 
 */
public abstract class AbstractHttpCommand extends AbstractCommand {

	private HashMap<String, String> headers = new HashMap<String, String>();
	private URI uri;
	private HttpRequestBase request;
	// Set-Cookie
	public static String set_cookie = "";

	protected AbstractHttpCommand() {
	}

	protected void prepare() {
		initializeHeaders();
	}

	protected final void onBeforeExecute() {
		request = getHttpRequest();

		addHeadersToRequest();
		String ctype = getContentType();
		if (ctype != null) {
			request.addHeader(HTTP.CONTENT_TYPE, ctype);
		}

		onBeforeExecute(request);
	}

	public void go() {
		if (!NetUtil.checkNet()) {
			setResponse(new Response());
			getResponse().setError(true);
			return;
		}
		HttpClient client = new DefaultHttpClient();
		Object responseData = null;
		Response response = getResponse() == null ? new Response()
				: getResponse();
		response.setTag(getRequest().getTag());
		response.setTargetActivityID(getRequest().getActivityID());
		client.getParams().setIntParameter("http.connection.timeout", 15000);
		Log.i("AbstractHttpCommand", "Created the request: " + client
				+ ", for request: " + request);
		try {
			// (Request-Line) GET / HTTP/1.1
			request.addHeader("Accept", "text/html, application/xhtml+xml, */*");
			request.addHeader("Accept-Encoding", "gzip, deflate");
			request.addHeader("Accept-Language", "zh-CN");
			request.addHeader("Connection", "Keep-Alive");
			// Cookie bbs_sid=ALWYRyAb3813435529430.79364300;
			// CNZZDATA30049344=cnzz_eid=99017672-1343552948-&ntime=1343552948&cnzz_a=0&retime=1343552945333&sin=&ltime=1343552945333&rtime=0;
			// CNZZDATA30023918=cnzz_eid=49510413-1343552948-&ntime=1343552948&cnzz_a=0&retime=1343552946336&sin=&ltime=1343552946336&rtime=0;
			// CNZZDATA2409549=cnzz_eid=86583548-1343552948-&ntime=1343552948&cnzz_a=0&retime=1343552946346&sin=&ltime=1343552946346&rtime=0;
			// CNZZDATA2437048=cnzz_eid=85662301-1343552948-&ntime=1343552948&cnzz_a=0&retime=1343552949356&sin=&ltime=1343552949356&rtime=0;
			// CNZZDATA30066334=cnzz_eid=45605747-1343552953-&ntime=1343552953&cnzz_a=0&retime=1343552954649&sin=&ltime=1343552954649&rtime=0
			request.addHeader("Host", "jx3.bbs.xoyo.com");
			request.addHeader("User-Agent",
					"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
			// set_cookie=CookieManager.getInstance().getCookie("jx3.bbs.xoyo.com");
			if (!TextUtils.isEmpty(set_cookie)) {
				request.setHeader("Set-Cookie", set_cookie);
				request.setHeader("Cookie", set_cookie);
			}
			HttpResponse rawResponse = client.execute(request);
			// new GZIPInputStream(rawResponse.getEntity().getContent())
			// set_cookie= rawResponse.getFirstHeader("Set-Cookie").getValue();
			if (rawResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// If All-Iz-Well, give the sub-class the raw-response to
				// process and generate Response-data
				responseData = getSuccessResponse(rawResponse);
				response.setError(false);
			} else {
				// If something's wrong with the response, let the sub-class
				// create appropriate Response-data
				responseData = getErrorResponse(rawResponse);
				response.setError(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// If something's wrong with the network or otherwise, let the
			// sub-class create appropriate Response-data
			responseData = getErrorResponse(e);
			response.setError(true);
		}

		response.setData(responseData);
		setResponse(response);
	}

	protected void onBeforeExecute(HttpRequestBase request) {
	}

	protected Object getErrorResponse(HttpResponse response) {
		return null;
	}

	protected Object getErrorResponse(Exception error) {
		return error;
	}

	protected Object getSuccessResponse(HttpResponse response) {
		InputStream payload = null;
		try {
			payload = response.getEntity().getContent();
		} catch (Exception e) {
			e.printStackTrace();
			return getErrorResponse(e);
		}
		return payload;
	}

	private void addHeadersToRequest() {
		for (String name : headers.keySet()) {
			request.addHeader(name, headers.get(name));
		}
	}

	protected abstract byte[] getBody();

	protected abstract String getContentType();

	protected HttpRequestBase getHttpRequest() {
		if (getBody() != null) {
			return new HttpPost(uri);
		} else {
			return new HttpGet(uri);
		}
	}

	public void setURI(URI uri) {
		this.uri = uri;
	}

	public URI getURI() {
		return uri;
	}

	protected void initializeHeaders() {
		addHeader(HTTP.CONN_CLOSE, "close");
		// can add some default headers like cookie, agent etc
		// override and call addHeader
	}

	protected final void addHeader(String name, String value) {
		headers.put(name, value);
	}
}
