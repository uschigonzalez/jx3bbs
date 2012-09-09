/**   
 * @Title: BranchListAdapter.java
 * @Package com.android.xiaow.jx3bbs
 * @Description: TODO(用一句话描述该文件做什么)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-12 下午7:24:39
 * @version V1.0   
 */
package com.android.xiaow.jx3bbs.activity;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.xiaow.jx3bbs.R;
import com.android.xiaow.jx3bbs.model.Bernda;

/**
 * @ClassName: BranchListAdapter
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaowei xiaowei_8852@sina.com
 * @date 2012-8-12 下午7:24:39
 * 
 */
public class BranchListAdapter extends AbstractAdapter<Bernda> {

    int[] color = new int[] { R.color.color1, R.color.color2, R.color.color4, R.color.color6,
            R.color.color7, R.color.color9 };

    public BranchListAdapter(List<Bernda> berndas, Context context) {
        super(context, berndas);

    }

    @Override
    public View CreateView(int position, View convertView, LayoutInflater inflater) {
        Holder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.berndaitem, null);
            holder = new Holder();
            holder.tv1 = (TextView) convertView.findViewById(R.id.textView1);
            holder.tv2 = (TextView) convertView.findViewById(R.id.textView2);
            holder.tv3 = (TextView) convertView.findViewById(R.id.textView3);
            holder.tv4 = (TextView) convertView.findViewById(R.id.textView4);
            holder.tv5 = (TextView) convertView.findViewById(R.id.textView5);
            holder.postion = position;
            convertView.setTag(holder);
        }
        if (position % 2 == 0) {
            convertView.setBackgroundResource(R.drawable.item_back12);
        } else {
            convertView.setBackgroundResource(R.drawable.item_back13);

        }

        holder = (Holder) convertView.getTag();
        Bernda bernda = getItem(position);
        if (TextUtils.isEmpty(bernda.item)) {
            bernda.item = "";
        }
        holder.tv1.setText(bernda.item + bernda.name);
        holder.tv5.setText(bernda.refuse + "");
        holder.tv2.setText(bernda.author);
        holder.tv3.setText(bernda.lastName);
        holder.tv4.setText(bernda.last_time);
        holder.postion = position;
        convertView.setTag(holder);
        return convertView;
    }

    public static class Holder {
        public TextView tv1;
        public TextView tv2;
        public TextView tv3;
        public TextView tv4;
        public TextView tv5;
        public LinearLayout bar;
        public int postion;
    }

}
