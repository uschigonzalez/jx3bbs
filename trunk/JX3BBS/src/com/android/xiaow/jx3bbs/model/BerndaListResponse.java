/**   
 * @Title: BerndaListResponse.java
 * @Package com.android.xiaow.jx3bbs.model
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-7-7 下午4:24:20
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs.model;

import java.util.List;

import com.android.xiaow.mvc.common.Response;

/**
 * @ClassName: BerndaListResponse
 * @Description: 帖子列表的返回对象
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-7-7 下午4:24:20
 * 
 */
public class BerndaListResponse extends Response {
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
