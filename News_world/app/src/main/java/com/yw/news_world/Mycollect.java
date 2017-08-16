package com.yw.news_world;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Iterator;
import java.util.Map;
import java.util.zip.Inflater;

public class Mycollect extends MyswipActivity {
    ListView collect_list;
    MyChoiceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycollect);
        collect_list = (ListView)findViewById(R.id.collect_list);

        if (Mydata.collect.size()==0){
            Toast.makeText(this,"没有收藏的新闻哟",Toast.LENGTH_LONG).show();
        }

        collect_list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        adapter = new MyChoiceAdapter(this, R.layout.choice_list, Mydata.collect);
        collect_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!adapter.isInActionMode) {
                    Intent intent = new Intent(Mycollect.this,MyWeb.class);
                    intent.putExtra("url",Mydata.collect.get(position).href);
                    startActivity(intent);
                }

            }
        });

        collect_list.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
//         当item的选中状态被选中的时候调用

                System.out.println("position:" + position + checked);
                if (adapter.list.containsKey(position)) {
                    adapter.list.remove(position);
                    adapter.list.put(position, checked);
                } else {
                    adapter.list.put(position, checked);
                }

                adapter.notifyDataSetChanged();


                System.out.println("onItemCheckedStateChanged");
            }


            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                //         在进入ActionMode的时候调用
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_delete, menu);
                mode.setTitle("选择要删除的项");
                adapter.isInActionMode = true;
                adapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // 当listview中的item被点击的时候调用
                if (item.getItemId() == R.id.memu_delete) {
                    System.out.println("onActionItemClicked:true");
                    Iterator iterator = adapter.list.entrySet().iterator();
                    while(iterator.hasNext()){
                        Map.Entry entry = (Map.Entry)iterator.next();
                        if ((Boolean) entry.getValue()){
                            Mydata.collect.remove((int)entry.getKey());
                            Mydata.savecollect();
                        }
                    }
                    mode.finish();
                    return true;
                }
                System.out.println("onActionItemClicked:false"+item.getItemId());
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
// 在退出ActionMode的时候调用，如果处于删除状态，就删除选中的数据，
                // 否则，重置所有选中的状态
                adapter.isInActionMode = false;
                adapter.list.clear();
                adapter.notifyDataSetChanged();
            }
        });
        collect_list.setAdapter(adapter);//更新viewpager




    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        return super.onOptionsItemSelected(item);
    }
}
