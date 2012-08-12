/**   
 * @Title: AbstractDatabase.java
 * @Package com.yhiker.playmate.core.db
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-11 下午3:18:07
 * @version V1.0   
 */
package com.android.xiaow.core.db;

/**
 * @ClassName: AbstractDatabase
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-11 下午3:18:07
 * 
 */
public class AbstractDatabase<T> extends BaseDatabase<T> {

    public AbstractDatabase(IDatabasePath path) {
        super();
        this.databasePath = path;
    }
}
