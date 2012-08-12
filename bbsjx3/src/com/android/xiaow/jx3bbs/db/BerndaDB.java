/**   
 * @Title: BerndaDB.java
 * @Package com.android.xiaow.jx3bbs.db
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-11 下午7:14:13
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs.db;

import com.android.xiaow.core.db.BaseDatabase;
import com.android.xiaow.jx3bbs.model.Bernda;

/**
 * @ClassName: BerndaDB
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-11 下午7:14:13
 * 
 */
public class BerndaDB extends BaseDatabase<Bernda> {

    public BerndaDB() {
        super();
        setDatabasePath(new DefaultDatabasePath());
    }

}
