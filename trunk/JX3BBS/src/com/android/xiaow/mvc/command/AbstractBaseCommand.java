package com.android.xiaow.mvc.command;

import com.android.xiaow.mvc.common.Request;
import com.android.xiaow.mvc.common.Response;
import com.android.xiaow.mvc.common.IResponseListener;

public abstract class AbstractBaseCommand implements ICommand
{
	private Request request;
	private Response response;
	private IResponseListener responseListener;
	private boolean terminated;

	public AbstractBaseCommand()
	{
		super();
	}

	public Request getRequest()
	{
		return request;
	}

	public void setRequest(Request request)
	{
		this.request = request;
	}

	public Response getResponse()
	{
		return response;
	}

	public void setResponse(Response response)
	{
		this.response = response;
	}

	public IResponseListener getResponseListener()
	{
		return responseListener;
	}

	public void setResponseListener(IResponseListener responseListener)
	{
		this.responseListener = responseListener;
	}

	public boolean isTerminated()
	{
		return terminated;
	}

	public void setTerminated(boolean terminated)
	{
		this.terminated = terminated;
	}

}
