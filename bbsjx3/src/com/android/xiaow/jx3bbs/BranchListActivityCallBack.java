/**   
 * @Title: BranchListActivityCallBack.java
 * @Package com.android.xiaow.jx3bbs
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-12 下午6:11:35
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs;

import com.android.xiaow.jx3bbs.model.MainArea;

/**
 * @ClassName: BranchListActivityCallBack
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-12 下午6:11:35
 * 
 */
public interface BranchListActivityCallBack {
    public void onReset();

    public void loadBranch(MainArea branch);
}
