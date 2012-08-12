/**   
 * @Title: BranchResponse.java
 * @Package com.android.xiaow.jx3bbs.cmds
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-12 下午6:56:26
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs.cmds;

import java.util.List;

import com.android.xiaow.core.common.Response;
import com.android.xiaow.jx3bbs.model.Bernda;

/**
 * @ClassName: BranchResponse
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-12 下午6:56:26
 * 
 */
public class BranchResponse extends Response {
    /**
     * @Fields cur_page : 当前页
     */
    public int cur_page;
    /**
     * @Fields max_page : 最大页数
     */
    public int max_page;
    public List<Bernda> berndas;
}
