/**   
 * @Title: AbstractAdapter.java
 * @Package com.android.xiaow.jx3bbs.activity
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-9-6 下午8:17:02
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs.activity;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @ClassName: AbstractAdapter
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-9-6 下午8:17:02
 * 
 */
public abstract class AbstractAdapter<T> extends BaseAdapter {

    Context context;
    List<T> data;
    LayoutInflater mInflater;

    public AbstractAdapter(Context context, List<T> data) {
        super();
        this.context = context;
        this.data = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public void addMore(List<T> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void changeData(List<T> data) {
        this.data.clear();
        addMore(data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return CreateView(position, convertView, mInflater);
    }

    public abstract View CreateView(int position, View convertView, LayoutInflater inflater);
}
