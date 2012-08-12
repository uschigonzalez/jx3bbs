/**   
 * @Title: DefaultDatabasePath.java
 * @Package com.android.xiaow.jx3bbs.db
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-11 下午7:07:27
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs.db;

import android.database.sqlite.SQLiteDatabase;

import com.android.xiaow.core.db.IDatabasePath;

/**
 * @ClassName: DefaultDatabasePath
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-11 下午7:07:27
 * 
 */
public class DefaultDatabasePath implements IDatabasePath {

    /**
     * (非 Javadoc)
     * <p>
     * Title: getDatabase
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @return
     * @see com.android.xiaow.core.db.IDatabasePath#getDatabase()
     */
    @Override
    public SQLiteDatabase getDatabase() {
        // TODO Auto-generated method stub
        return SqliteConn.getDatabase();
    }

}
