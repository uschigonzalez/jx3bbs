package com.android.xiaow.mvc.common;

public interface IResponseListener
{
	void onSuccess(Response response);

	void onError(Response response);
}
