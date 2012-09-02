/**   
 * @Title: Refuse.java
 * @Package com.android.xiaow.jx3bbs.model
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-9-2 上午10:00:53
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs.model;

import com.android.xiaow.core.db.DBTableName;

import android.text.TextUtils;

/**
 * @ClassName: Refuse
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-9-2 上午10:00:53
 * 
 */
@DBTableName("refuse")
public class Refuse {
    public String content;
    public int level;

    @Override
    public String toString() {
        if (TextUtils.isEmpty(content))
            return "";
        return content;
    }
}
