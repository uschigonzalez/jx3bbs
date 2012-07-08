package com.android.xiaow.jx3bbs.command;

import java.util.List;

import android.text.TextUtils;

import com.android.xiaow.jx3bbs.database.BerndaConn;
import com.android.xiaow.jx3bbs.model.Bernda;
import com.android.xiaow.mvc.command.AbstractCommand;
import com.android.xiaow.mvc.common.Response;

public class BerndaDB extends AbstractCommand {

	@Override
	protected void go() {
		List<Bernda> data = null;
		setResponse(new Response());
		String str = getRequest().getData().toString();
		if (getRequest().getContext() == null)
			return;
		if (TextUtils.isDigitsOnly(getRequest().getTag().toString())) {
			int from = Integer.parseInt(getRequest().getTag().toString());
			data = BerndaConn.getInstance().getData(str, from);
		} else {
			data = BerndaConn.getInstance().getData(str);
		}
		getResponse().setData(data);
	}

}
