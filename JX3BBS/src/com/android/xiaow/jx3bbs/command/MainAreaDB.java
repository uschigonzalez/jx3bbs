package com.android.xiaow.jx3bbs.command;

import java.util.List;

import com.android.xiaow.jx3bbs.database.MainBrachConn;
import com.android.xiaow.jx3bbs.model.MainArea;
import com.android.xiaow.mvc.command.AbstractCommand;
import com.android.xiaow.mvc.common.Response;

public class MainAreaDB extends AbstractCommand {

	@Override
	protected void go() {
		setResponse(new Response());
		String str = getRequest().getData().toString();
		if (getRequest().getContext() == null)
			return;
		List<MainArea> data=MainBrachConn.getInstance().getBrach(str);
		getResponse().setData(data);
	}

}
