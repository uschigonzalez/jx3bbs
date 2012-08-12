/**   
 * @Title: MainBrachDB.java
 * @Package com.android.xiaow.jx3bbs.db
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-11 下午7:08:18
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.database.Cursor;

import com.android.xiaow.core.db.BaseDatabase;
import com.android.xiaow.jx3bbs.db.DataStore.MainBrach;
import com.android.xiaow.jx3bbs.model.MainArea;

/**
 * @ClassName: MainBrachDB
 * @Description: 关于表MainBranch的所有操作
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-11 下午7:08:18
 * 
 */
public class MainBrachDB extends BaseDatabase<MainArea> {

    public MainBrachDB() {
        super();
        setDatabasePath(new DefaultDatabasePath());
    }

    public List<MainArea> getAreasByParent(String parent) {
        return getPartByParam(null, MainBrach.PARENT + " = \"" + parent + "\" and "
                + MainBrach.IS_SUB_BROAD + "= 0", null);
    }

    public HashMap<String, List<MainArea>> getAreaByParent() {
        HashMap<String, List<MainArea>> map = new HashMap<String, List<MainArea>>();
        Cursor cursor = getDatabase().query(mTableName, new String[] { MainBrach.PARENT }, null,
                null, MainBrach.PARENT, null, null);
        List<String> parents = new ArrayList<String>();
        while (cursor.moveToNext()) {
            parents.add(cursor.getString(0));
        }
        cursor.close();
        for (String parent : parents) {
            map.put(parent,
                    getPartByParam(null, MainBrach.PARENT + " = \"" + parent + "\" and "
                            + MainBrach.IS_SUB_BROAD + "= 0 ", null));
        }
        return map;
    }
}
