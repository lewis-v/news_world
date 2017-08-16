package com.yw.news_world;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/5/27.
 */
public class Jsoup_data implements Serializable{
    public String href = null;
    public byte[] imageView = null;
    public long time = 0;
    public String from = null;
    public String title = null;
    public String timestring = "刚刚";

    public Jsoup_data(String title,String href,Bitmap imageView,long time,String from){
        this.title = title;
        this.href = href;
        if (imageView != null) {
            this.imageView = getBytes(imageView);
        }
        this.time = time;
        this.from = from;
    }
    public Jsoup_data(String title,String href,Bitmap imageView) {
        this.title = title;
        this.href = href;
        try {
            if (imageView != null) {
                this.imageView = getBytes(imageView);
            }
        } catch (NullPointerException e) {
        }
    }

    public byte[] getBytes(Bitmap bitmap){//bitmap转换成byte,用于序列化
        //实例化字节数组输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);//压缩位图
        return baos.toByteArray();//创建分配字节数组
    }
    public  Bitmap getBitmap(){//byte转换成bitmap
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeByteArray(imageView, 0, imageView.length);//从字节数组解码位图
        }catch (NullPointerException e){
            return null;
        }

        return bitmap;
    }



    static class DataAdapter extends ArrayAdapter {//listview的适配器,将jsoup的数据显示到listview中
        public List<Jsoup_data> jsoup_datas = null;
        public LayoutInflater layoutInflater = null;
        public DataAdapter(Context context, int resource, List objects) {
            super(context, resource, objects);
            this.jsoup_datas = objects;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return jsoup_datas.size();
        }

        @Override
        public Object getItem(int position) {
            return jsoup_datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                convertView = layoutInflater.inflate(R.layout.list, null);
                holder = new ViewHolder();
                holder.title = (TextView)convertView.findViewById(R.id.list_title);
                holder.time = (TextView)convertView.findViewById(R.id.list_time);
                holder.from = (TextView)convertView.findViewById(R.id.list_from);
                holder.image = (ImageView)convertView.findViewById(R.id.list_image);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }
            holder.title.setText(jsoup_datas.get(position).title);
            holder.time.setText(jsoup_datas.get(position).timestring);
            holder.from.setText("来自: "+jsoup_datas.get(position).from);
            if (jsoup_datas.get(position).imageView != null){
                holder.image.setVisibility(View.VISIBLE);
               holder.image.setImageBitmap(jsoup_datas.get(position).getBitmap());

                System.out.print("img_not_null");
            }else{
                holder.image.setVisibility(View.GONE);
                System.out.print("img_null");
            }

            return convertView;
        }
    }
    private static class ViewHolder{
        public TextView title;
        public ImageView image;
        public TextView time;
        public TextView from;
    }




}
