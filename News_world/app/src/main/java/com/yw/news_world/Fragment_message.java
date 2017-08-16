package com.yw.news_world;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_message extends Fragment {
    ListView listView;
    MyAdapter myAdapter;
    List<MyMessage> stringList = new ArrayList<MyMessage>();

    public Fragment_message() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_fragment_message, container, false);
        listView = (ListView)view.findViewById(R.id.message_listview);

        MyMessage myMessage = new MyMessage("客服","欢迎使用news_world");
        stringList.add(myMessage);

        myAdapter = new MyAdapter(getActivity(),R.layout.message_list,stringList);
        listView.setAdapter(myAdapter);

        return view;
    }

    class MyAdapter extends ArrayAdapter {
        private List<MyMessage> datas = null;
        private LayoutInflater layoutInflater = null;
        private int resource;
        public MyAdapter(Context context, int resource, List objects) {
            super(context, resource, objects);
            this.datas = objects;
            this.resource = resource;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                convertView = layoutInflater.inflate(resource, null);
                holder = new ViewHolder();
                holder.title = (TextView)convertView.findViewById(R.id.message_title);
                holder.time = (TextView)convertView.findViewById(R.id.message_time);
                holder.context = (TextView)convertView.findViewById(R.id.message_context);
                holder.image = (ImageView)convertView.findViewById(R.id.message_img);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }
            holder.title.setText(datas.get(position).title);
            holder.context.setText(datas.get(position).con);
            holder.time.setText(datas.get(position).time);
            holder.image.setBackgroundResource(R.mipmap.news_back);

            return convertView;
        }
        private class ViewHolder{
            private TextView title;
            private ImageView image;
            private TextView time;
            private TextView context;
        }

    }
    class MyMessage{
        String title = null;
        String con = null;
        String time = null;

        public MyMessage(String title,String context){
            this.title = title;
            this.con = context;
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月mm日hh时mm分");
            time = simpleDateFormat.format(date);
        }
    }

}
