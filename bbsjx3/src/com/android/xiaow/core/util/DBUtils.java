package com.android.xiaow.core.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.android.xiaow.core.db.DBSerializedName;

public class DBUtils {

    /**
     * ��������͵ķ�װ���� exp��int.class Integer.class
     */
    public static HashMap<Class<? extends Object>, Class<? extends Object>> map = new HashMap<Class<? extends Object>, Class<? extends Object>>();

    static {
        map.put(double.class, Double.class);
        map.put(int.class, Integer.class);
        map.put(float.class, Float.class);
        map.put(long.class, Long.class);
        map.put(byte.class, Byte.class);
        map.put(char.class, Character.class);
        map.put(boolean.class, Boolean.class);
        map.put(short.class, Short.class);
    }

    /**
     * ��cursor�еĽ��ת���� cls
     * 
     * @param cursor
     *            ��ѯ�Ľ��
     * @param cls
     *            ���� Ϊ T��class ��Ҫ����ѯ�������� ע�⣺��������������ֶ�����ƥ���
     *            �����������Сд������������
     * @param flag
     *            true ʹ�����֮�󽫻�ر�cursor
     * @return ������������ֵ����������ض�Ϊ��
     */
    public static <T> T parseObject(Cursor cursor, Class<? extends T> cls, boolean flag) {
        if (cursor == null || cursor.isClosed() || cls == null)
            return null;
        T bean = null;
        try {
            bean = cls.newInstance();
            cursor.moveToFirst();
            bean = parseObject(cursor, cls);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        if (flag) {
            cursor.close();
        }
        return bean;
    }

    /**
     * ��cursor�еĽ��ת���� cls
     * 
     * @param cursor
     *            ��ѯ�Ľ��
     * @param cls
     *            ��Ҫ����ѯ�������� ע�⣺��������������ֶ�����ƥ���
     *            �����������Сд������������
     * @param flag
     *            true ʹ�����֮�󽫻�ر�cursor
     * @return T�ļ��� ������������ֵ����������ض�Ϊ��
     */
    public static <T> List<T> parseList(Cursor cursor, Class<? extends T> cls, boolean flag) {
        if (cursor == null || cls == null)
            return null;
        List<T> list = new ArrayList<T>();
        while (!cursor.isAfterLast()) {
            T t = parseObject(cursor, cls);
            if (t != null)
                list.add(t);
            cursor.moveToNext();
        }
        if (flag) {
            cursor.close();
        }
        return list;
    }

    /**
     * ��ȡclass���е����ԣ�Value�������cursor���ֶΣ�Key����Ӧ key-value
     * 
     * @param cls
     * @param cursor
     * @return
     */
    public static HashMap<String, Field> getCommanField(Class<? extends Object> cls, Cursor cursor) {
        if (cursor == null || cursor.isClosed() || cls == null)
            return null;
        List<Field> t_filed = getDeclaredFields(cls);
        int t_len = t_filed.size();
        int len = cursor.getColumnCount();
        HashMap<String, Field> fields = new HashMap<String, Field>();
        ArrayList<String> curs = new ArrayList<String>();
        HashMap<String, String> cursors = new HashMap<String, String>();
        for (int i = 0; i < len; i++) {
            curs.add(cursor.getColumnName(i).toLowerCase());
            cursors.put(cursor.getColumnName(i).toLowerCase(), cursor.getColumnName(i));
        }
        // ��ȡcursor��t�ж�Ӧ���������ֶ�
        for (int i = 0; i < t_len; i++) {
            Field fis = t_filed.get(i);
            DBSerializedName name = t_filed.get(i).getAnnotation(DBSerializedName.class);
            String field_name = fis.getName().toLowerCase();
            if (name != null) {
                field_name = name.value().toLowerCase();
            }
            if (curs.contains(field_name)) {
                fields.put(cursors.get(field_name), fis);
            } else {
                field_name = field_name.replace("_", "");
                if (curs.contains(field_name)) {
                    fields.put(cursors.get(field_name), fis);
                }
            }
        }
        return fields;
    }

    /**
     * 
     * @param cursor
     *            ����cursor��isClose(),�����������cursor�ĵ�ǰ��ָλ��
     * @param t
     *            ����Ϊ��
     * @return ������������ֵ����������ض�Ϊ��
     */
    public static <T> T parseObject(Cursor cursor, Class<? extends T> cls) {
        if (cursor == null || cursor.isClosed() || cls == null)
            return null;
        T t = null;
        if (cursor.getPosition() < 0)
            cursor.moveToFirst();
        if (cursor.isAfterLast())
            return null;
        HashMap<String, Field> fields = getCommanField(cls, cursor);
        Iterator<String> it = fields.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            Field field = fields.get(key);
            String value = cursor.getString(cursor.getColumnIndex(key));
            // �趨fieldΪ�ɷ��ʣ����fieldΪ˽�������ǣ���������Ϊtrue�����������쳣
            field.setAccessible(true);
            if (isBasicType(getBasicClass(field.getType()))) {
                // field�����͵ķ�װ����
                Class<? extends Object> typeClass = getBasicClass(field.getType());
                try {
                    if (t == null)
                        t = cls.newInstance();
                    // �˴�������ж�����string�Ĺ���ķ��������д���ɶ�Ӧ����
                    if (TextUtils.isEmpty(value)) {
                        continue;
                    }
                    field.set(t, typeClass.getConstructor(String.class).newInstance(value));
                } catch (Exception e) {
                    Log.d("MSG", "Exception--->:" + e.getCause() + "," + field.getName() + ","
                            + value);
                    e.printStackTrace();
                }
            }
        }
        return t;
    }

    /**
     * ��ʵ��������ԣ�value����cursor��key�����ֶ���һһ��Ӧ�ģ�������contentvalues��
     * 
     * @param t
     * @param c
     * @return
     */
    public static <T> ContentValues copyValues(T t, Cursor c) {
        ContentValues values = new ContentValues();
        HashMap<String, Field> fields = null;
        if (c != null) {
            fields = getCommanField(t.getClass(), c);
        }
        if (fields != null) {
            Iterator<String> it = fields.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                Field field = fields.get(key);
                if (!isBasicType(getBasicClass(field.getType()))) {
                    continue;
                }
                try {
                    field.setAccessible(true);
                    Object obj = field.get(t);
                    if (obj == null || TextUtils.isEmpty(obj.toString())) {
                        continue;
                    }
                    // TODO:��������Ϊ0 ����
                    values.put(key, obj.toString());
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return values;
    }

    /**
     * ��ȡtypeClass�ķ�װ����
     * 
     * @param typeClass
     * @return
     */
    public static Class<? extends Object> getBasicClass(Class<? extends Object> typeClass) {
        if (map.containsKey(typeClass)) {
            return map.get(typeClass);
        }
        return typeClass;
    }

    /**
     * �ж�typeClass�ǲ��ǻ�����
     * 
     * @param typeClass
     * @return
     */
    public static boolean isBasicType(Class<? extends Object> typeClass) {
        if (typeClass.equals(Integer.class) || typeClass.equals(Long.class)
                || typeClass.equals(Float.class) || typeClass.equals(Double.class)
                || typeClass.equals(Boolean.class) || typeClass.equals(Byte.class)
                || typeClass.equals(Short.class) || typeClass.equals(String.class)) {

            return true;

        } else {
            return false;
        }
    }

    /**
     * 
     * @Title: copyBean
     * @Description: ��������ͬ���Ե�ֵ����
     * @param src
     *            Դ
     * @param dest
     *            Ŀ��
     * @return void ��������
     */
    public static void copyBean(Object src, Object dest) {
        if (src == null || dest == null)
            return;
        List<Field> ds = getDeclaredFields(src.getClass());
        List<Field> ss = getDeclaredFields(dest.getClass());
        for (int i = 0; i < ss.size(); i++) {
            Field sf = ss.get(i);
            for (int j = 0; j < ds.size(); j++) {
                Field df = ds.get(j);
                if (df.getName().equals(sf.getName()) && df.getType().equals(sf.getType())) {
                    sf.setAccessible(true);
                    try {
                        sf.set(dest, df.get(src));
                        break;
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    public static List<Field> getDeclaredFields(Class<?> cls) {
        List<Field> fields = new ArrayList<Field>();
        String name = Object.class.getSimpleName();
        while (!cls.getSimpleName().equals(name)) {
            fields.addAll(Arrays.asList(cls.getDeclaredFields()));
            cls = cls.getSuperclass();
        }
        return fields;
    }

}
