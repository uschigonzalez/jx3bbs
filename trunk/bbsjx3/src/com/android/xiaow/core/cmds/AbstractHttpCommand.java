package com.android.xiaow.core.cmds;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import com.android.xiaow.core.common.Response;


public abstract class AbstractHttpCommand extends AbstractCommand {

	public static String set_cookie;

    public abstract void preExecute();

	public abstract void addHeader();

	public abstract void AfterExecute();

	public abstract void go();

	@Override
	public void execute() {
		preExecute();
		addHeader();
		go();
		AfterExecute();
	
		if (getListener() != null) {
			if (getResponse() == null) {
				setResponse(new Response());
				getResponse().isError = true;
				getResponse().errorMsg="����쳣";
			}
			if (!getResponse().isError) {
				getListener().onSuccess(getResponse());
			} else {
				getListener().onError(getResponse());
			}
		}
	}

	protected HttpUriRequest request;

	protected HttpResponse response;

	public HttpUriRequest getHttpRequest() {
		return request;
	}

	public void setHttpRequest(HttpUriRequest request) {
		this.request = request;
	}

	public HttpResponse getHttpResponse() {
		return response;
	}

	public void setHttpResponse(HttpResponse response) {
		this.response = response;
	}

}
