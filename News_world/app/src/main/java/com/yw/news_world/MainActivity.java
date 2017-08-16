package com.yw.news_world;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<Fragment> fragmentList= new ArrayList<Fragment>();;
    MyViewPager viewPager;
    TabLayout tabLayout;
    List<String> string = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this,SplashActivity.class);
        startActivity(intent);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.ACCESS_NETWORK_STATE
                ,Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.CHANGE_NETWORK_STATE,Manifest.permission.READ_PHONE_STATE},1);
        getSupportActionBar().hide();

//        Intent intent1 = new Intent(this,OneActivity.class);
//        startActivity(intent1);
        viewPager = (MyViewPager) findViewById(R.id.firstview);
        tabLayout = (TabLayout)findViewById(R.id.firsttab);


        Mysql_tool mysql_tool = Mysql_tool.getMysql_tool(this);
        Mydata mydata = new Mydata(this);
        mydata.getcollect(mysql_tool);
        mydata.setAllKind();
        mydata.getkind();
        mydata.getpeople();

        fragmentList.add(new Fragment_home());
        fragmentList.add(new Fragment_listen());
        fragmentList.add(new Fragment_message());
        fragmentList.add(new Fragment_my());

        string.add("首页");
        string.add("听新闻");
        string.add("消息");
        string.add("我的");

        viewPager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(),fragmentList,string));
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(4);//pager缓存view数量


        tabLayout.getTabAt(0).setIcon(R.mipmap.home);//为首页选项添加照片
        tabLayout.getTabAt(1).setIcon(R.mipmap.listen);//为听新闻选项添加照片
        tabLayout.getTabAt(2).setIcon(R.mipmap.message);//为消息选项添加照片
        tabLayout.getTabAt(3).setIcon(R.mipmap.my);//为我的选项添加照片


    }
    @Override
    protected void onResume() {
        /**
         * 设置为横屏
         */
        if(getRequestedOrientation()== ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }

    protected void onDestroy(){
        super.onDestroy();
    }

}
