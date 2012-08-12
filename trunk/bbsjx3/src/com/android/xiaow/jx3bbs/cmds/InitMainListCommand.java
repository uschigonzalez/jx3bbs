/**   
 * @Title: InitMainListCommand.java
 * @Package com.android.xiaow.jx3bbs.cmds
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-11 下午11:20:27
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs.cmds;

import com.android.xiaow.core.cmds.AbstractCommand;
import com.android.xiaow.core.common.Response;
import com.android.xiaow.jx3bbs.db.MainBrachDB;

/**
 * @ClassName: InitMainListCommand
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-11 下午11:20:27
 * 
 */
public class InitMainListCommand extends AbstractCommand {

    /**
     * (非 Javadoc)
     * <p>
     * Title: go
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @see com.android.xiaow.core.cmds.AbstractCommand#go()
     */
    @Override
    public void go() {
        setResponse(new Response());
        getResponse().result = new MainBrachDB().getAreaByParent();
        getResponse().isError = false;
    }

}
