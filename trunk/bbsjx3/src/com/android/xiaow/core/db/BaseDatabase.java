/**   
 * @Title: BaseDatabase.java 
 * @Package com.yhiker.playmate.core.db 
 * @Description: TODO
 * @author xiaowei   
 * @date 2012-8-6 ����1:46:48 
 * @version V1.0   
 */
package com.android.xiaow.core.db;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.android.xiaow.core.util.DBUtils;

/**
 * 
 * @author ���� xiaowei
 * @����ʱ�� 2012-8-6 ����1:46:48 ��˵��
 * 
 */
public abstract class BaseDatabase<T> {
    protected Type type;
    protected Class<? extends T> cls;
    protected IDatabasePath databasePath;
    protected String mTableName;

    @SuppressWarnings("unchecked")
    public BaseDatabase() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        type = GsonType.canonicalize(parameterized.getActualTypeArguments()[0]);
        cls = (Class<? extends T>) GsonType.getRawType(this.type);
        validTableName();
    }

    public IDatabasePath getDatabasePath() {
        return databasePath;
    }

    public void setDatabasePath(IDatabasePath databasePath) {
        this.databasePath = databasePath;
    }

    public SQLiteDatabase getDatabase() {
        return databasePath.getDatabase();
    }

    public List<T> getAll() {
        Cursor cursor = getDatabase().query(mTableName, null, null, null, null, null, null);
        return DBUtils.parseList(cursor, cls, true);
    }

    @SuppressWarnings("unchecked")
    public void update(T t) {
        if (t == null)
            return;
        update(Arrays.asList(t));
    }

    public void update(List<T> t) {
        if (t == null || t.size() < 1)
            return;
        Cursor cursor = getDatabase().query(mTableName, null, null, null, null, null, null);
        for (T t2 : t) {
            ContentValues values = DBUtils.copyValues(t2, cursor);
            HashMap<String, Field> map = DBUtils.getCommanField(cls, cursor);
            List<String> args = new ArrayList<String>();
            String whereClause = buildWhere(map, t2, args);
            boolean insert = true;
            if (args.size() > 0) {
                insert = (getDatabase().update(mTableName, values, whereClause,
                        args.isEmpty() ? null : args.toArray(new String[args.size()])) > 0) ? false
                        : true;
            }
            if (insert) {
                getDatabase().insertWithOnConflict(mTableName, null, values,
                        SQLiteDatabase.CONFLICT_REPLACE);
            }
        }
    }

    public List<T> getPartByParam(String[] columns, String selection, String[] selectionArgs) {
        Cursor cursor = getDatabase().query(mTableName, columns, selection, selectionArgs, null,
                null, null);
        return DBUtils.parseList(cursor, cls, true);
    }

    public void deleteByParam(String whereClause, String[] whereArgs) {
        getDatabase().delete(mTableName, whereClause, whereArgs);
    }

    protected void validTableName() {
        DBTableName tableName2 = cls.getAnnotation(DBTableName.class);
        if (tableName2 != null) {
            mTableName = tableName2.value();
        }
    }

    protected String buildWhere(HashMap<String, Field> map, T t, List<String> args) {
        String whereClause = "";
        Iterator<String> keys = map.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            Field field = map.get(key);
            DBPrimaryKeyName primaryKey = field.getAnnotation(DBPrimaryKeyName.class);
            if (primaryKey != null) {
                if (!TextUtils.isEmpty(whereClause)) {
                    whereClause += " and ";
                }
                whereClause += key + " =  ? ";
                try {
                    field.setAccessible(true);
                    Object object = field.get(t);
                    args.add(object == null ? "" : object.toString());
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return whereClause;
    }
}
