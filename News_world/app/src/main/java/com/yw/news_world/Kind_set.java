package com.yw.news_world;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Toast;

public class Kind_set extends MyswipActivity {
    RecyclerView have,not_have;
    MyHomeAdapter_kind kindAdapter,kindAdapter_not;
    Mydata mydata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kind_set);
        have = (RecyclerView)findViewById(R.id.kind_have);
        not_have = (RecyclerView)findViewById(R.id.kind_not);
        mydata = new Mydata(this);

        have.setLayoutManager(new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL));
        kindAdapter = new MyHomeAdapter_kind(this,Mydata.have_kind);
        have.setAdapter(kindAdapter);
        have.addItemDecoration(new DividerGridItemDecoration(this));
        have.setItemAnimator(new DefaultItemAnimator());

        not_have.setLayoutManager(new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL));
        kindAdapter_not = new MyHomeAdapter_kind(this,mydata.getNotHave());
        not_have.setAdapter(kindAdapter_not);
        not_have.addItemDecoration(new DividerGridItemDecoration(this));
        not_have.setItemAnimator(new DefaultItemAnimator());

        kindAdapter.setOnItemClikListener(new MyHomeAdapter_kind.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (Mydata.have_kind.get(position).equals("推荐")){
                    Toast.makeText(Kind_set.this, "默认推荐不可设置", Toast.LENGTH_SHORT).show();
                }
                else {
                    Mydata.have_kind.remove(position);
                    String str = kindAdapter.removeData(position);
                    mydata.savekind(Mydata.have_kind);
                    kindAdapter_not.addData(mydata.getNotHave().size() - 1, str);
                    Toast.makeText(Kind_set.this, "设置成功,重启程序后生效", Toast.LENGTH_SHORT).show();
                }
            }
        });
        kindAdapter_not.setOnItemClikListener(new MyHomeAdapter_kind.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {
                String str = kindAdapter_not.removeData(position);
                kindAdapter.addData(Mydata.have_kind.size(),str);
                Mydata.have_kind.add(str);
                mydata.savekind(Mydata.have_kind);
                Toast.makeText(Kind_set.this,"设置成功,重启程序后生效",Toast.LENGTH_SHORT).show();
            }
        });




    }
}
