package com.android.xiaow.mvc.command;

import com.android.xiaow.mvc.common.IResponseListener;
import com.android.xiaow.mvc.common.Request;
import com.android.xiaow.mvc.common.Response;

public class IdentityCommand extends AbstractBaseCommand implements ICommand
{
	public void execute()
	{
		Request request = getRequest();
		Response response = new Response();
		response.setTag(request.getTag());
		response.setError(false);
		response.setTargetActivityID((Integer) request.getData());

		setResponse(response);
		notifyListener(true);
	}

	protected void notifyListener(boolean success)
	{
		IResponseListener responseListener = getResponseListener();
		if(responseListener != null)
		{
			responseListener.onSuccess(getResponse());
		}
	}
}
