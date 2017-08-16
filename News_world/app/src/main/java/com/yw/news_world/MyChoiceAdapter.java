package com.yw.news_world;

import android.content.Context;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.widget.AbsListView.*;

/**
 * Created by Administrator on 2017/6/4.
 */
public class MyChoiceAdapter extends Jsoup_data.DataAdapter {
    public boolean isInActionMode = false;
    public HashMap<Integer,Boolean> list = new HashMap<Integer,Boolean>();

    public MyChoiceAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.choice_list, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.choice_list_title);
            holder.time = (TextView) convertView.findViewById(R.id.choice_list_time);
            holder.from = (TextView) convertView.findViewById(R.id.choice_list_from);
            holder.image = (ImageView) convertView.findViewById(R.id.choice_list_image);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(jsoup_datas.get(position).title);
        holder.time.setText(jsoup_datas.get(position).timestring);
        holder.from.setText("来自: " + jsoup_datas.get(position).from);
        if (jsoup_datas.get(position).imageView != null) {
            holder.image.setVisibility(View.VISIBLE);
            holder.image.setImageBitmap(jsoup_datas.get(position).getBitmap());

            System.out.print("img_not_null");
        } else {
            holder.image.setVisibility(View.GONE);
            System.out.print("img_null");
        }
        if (isInActionMode) {
            holder.checkBox.setVisibility(View.VISIBLE);
        }else{
            holder.checkBox.setVisibility(View.INVISIBLE);
        }
        if(list.containsKey(position)&&isInActionMode){
            if(list.get(position)){
                holder.checkBox.setChecked(true);
            }else{
                holder.checkBox.setChecked(false);
            }
        }

        return convertView;
    }





    public static class ViewHolder{
        public TextView title;
        public ImageView image;
        public TextView time;
        public TextView from;
        public CheckBox checkBox;
    }
}

