package com.yw.news_world;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class Set_listen extends MyswipActivity {
    ListView listView;
    MysetAdapter setAdapter;
    Mydata mydata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_listen);
        listView = (ListView)findViewById(R.id.set_listen_list);

        mydata = new Mydata(this);

        setAdapter = new MysetAdapter(this,R.layout.list_set,Mydata.people);
        listView.setAdapter(setAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Mydata.people_position = position;
                listView.setAdapter(setAdapter);
                Ifly ifly = new Ifly(Set_listen.this);
                ifly.Speaking("本程序语音技术由讯飞提供");
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mydata.savepeople();
        Toast.makeText(Set_listen.this,"听新闻语音人设置成功,重启后生效",Toast.LENGTH_SHORT).show();
    }

    class MysetAdapter extends ArrayAdapter{
        TextView textView;
        public LayoutInflater layoutInflater = null;
        public MysetAdapter(Context context, int resource, Object[] objects) {
            super(context, resource, objects);
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

                if(convertView == null){
                    convertView = layoutInflater.inflate(R.layout.list_set, null);
                    textView = (TextView)convertView.findViewById(R.id.list_my);
                    convertView.setTag(textView);
                }else{
                    textView = (TextView)convertView.getTag();
                }
            textView.setText(Mydata.people[position]);
            textView.setHeight(200);
            if (position == Mydata.people_position){
                textView.setBackgroundColor(Color.YELLOW);
                System.out.println("position:"+position);
            }else{
                textView.setBackgroundColor(Color.WHITE);
            }
            return convertView;
        }
    }


}
