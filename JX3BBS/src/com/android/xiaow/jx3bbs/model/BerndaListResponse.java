/**   
 * @Title: BerndaListResponse.java
 * @Package com.android.xiaow.jx3bbs.model
 * @Description: TODO(��һ�仰�������ļ���ʲô)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-7-7 ����4:24:20
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs.model;

import java.util.List;

import com.android.xiaow.mvc.common.Response;

/**
 * @ClassName: BerndaListResponse
 * @Description: �����б�ķ��ض���
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-7-7 ����4:24:20
 * 
 */
public class BerndaListResponse extends Response {
	/**
	 * @Fields cur_page : ��ǰҳ
	 */
	public int cur_page;
	/**
	 * @Fields max_page : ���ҳ��
	 */
	public int max_page;
	public List<Bernda> berndas;
}
