/**   
 * @Title: RefuseDB.java
 * @Package com.android.xiaow.jx3bbs.db
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-9-2 上午10:01:33
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs.db;

import java.util.List;

import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.android.xiaow.core.db.BaseDatabase;
import com.android.xiaow.core.util.DBUtils;
import com.android.xiaow.jx3bbs.model.Refuse;

/**
 * @ClassName: RefuseDB
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-9-2 上午10:01:33
 * 
 */
public class RefuseDB extends BaseDatabase<Refuse> {

    public RefuseDB() {
        super();
        setDatabasePath(new DefaultDatabasePath());
    }

    public List<Refuse> getRefuses(String content) {
        if (TextUtils.isEmpty(content))
            return null;
        String sql = "select * from refuse where content like \"%" + content
                + "%\" order by level DESC,id DESC limit " + 5 + " offset 0;";
        Log.d("MSG", "SQL=" + sql);
        Cursor cursor = getDatabase().rawQuery(sql, null);
        return DBUtils.parseList(cursor, Refuse.class, true);
    }

    public List<Refuse> getAll(int num) {
        num = Math.max(num, 5);
        Cursor cursor = getDatabase().rawQuery(
                "select * from refuse order by level DESC,id DESC limit " + num + " offset 0;",
                null);
        return DBUtils.parseList(cursor, Refuse.class, true);
    }

    @Override
    public List<Refuse> getAll() {
        Cursor cursor = getDatabase().rawQuery(
                "select * from refuse order by level DESC,id DESC ;", null);
        return DBUtils.parseList(cursor, Refuse.class, true);
    }

    public Refuse getRefuse(String content) {
        Cursor cursor = getDatabase().rawQuery(
                "select * from refuse where content =\" " + content + "\";", null);
        return DBUtils.parseObject(cursor, Refuse.class);
    }

    @Override
    public void update(Refuse t) {
        super.update(t);
    }

}
