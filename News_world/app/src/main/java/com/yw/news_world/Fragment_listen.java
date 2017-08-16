package com.yw.news_world;


import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_listen extends Fragment  implements SwipeRefreshLayout.OnRefreshListener{
    RecyclerView recycler;
    ImageButton bt_last,bt_next,bt_start,bt_recycle;
    ImageView imageView;
    View view;
    List<Jsoup_data> dataList = new ArrayList<Jsoup_data>();
    Get_thread get_thread;
    Get_thread2 get_thread2;
    Jsoup_get jsoup_get;
    SwipeRefreshLayout refreshLayout;
    Handler listen_handler;
    Intent intent,intent_back;
    MyService myService;
    boolean Isstart = false;
    MyService.MyBind myBind;
    TextView listen_tv;
    Thread Service_t;

    final private int load_count = 10;//一次加载新闻的数量
    MyHomeAdapter myHomeAdapter;

    public Fragment_listen() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =inflater.inflate(R.layout.fragment_fragment_listen, container, false);

        initView();
        initService();
        listen_handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        refreshLayout.setRefreshing(false);
//                        myHomeAdapter.notifyDataSetChanged();
                        recycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                        myHomeAdapter = new MyHomeAdapter(getActivity(),dataList);
                        myHomeAdapter.setOnItemClikListener(new MyHomeAdapter.OnItemClickLitener(){
                            @Override
                            public void onItemClick(View view, int position) {
                                synchronized (this){
                                    if (!isWorked("com.yw.news_world.Myservice_back")) {
                                        intent_back = new Intent(getActivity(), Myservice_back.class);
                                        getActivity().startService(intent_back);
                                    }
                                    myBind.listen_setList(dataList);
                                    myBind.listen_start(position);
                                }
                            }

                            @Override
                            public void onItemLongClick(View view, int position) {

                            }
                        });
                        recycler.setAdapter(myHomeAdapter);
                        System.out.println("更新成功");
                        break;
                    case 2:
                        try{
                            Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        }catch (NullPointerException e){

                        }

                        break;
                }
                super.handleMessage(msg);
            }
        };


        get_thread = new Get_thread();
        get_thread.start();

        refreshLayout.setOnRefreshListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unbindService(serviceConnection);
        System.out.println("解绑服务");
    }

    /**
     * 初始化控件
     */
    public void initView(){
        recycler = (RecyclerView)view.findViewById(R.id.listen_recycler);
        bt_last = (ImageButton)view.findViewById(R.id.listen_last);
        bt_start = (ImageButton)view.findViewById(R.id.listen_start);
        bt_next = (ImageButton)view.findViewById(R.id.listen_next);
        bt_recycle = (ImageButton)view.findViewById(R.id.listen_recycle);
        imageView = (ImageView)view.findViewById(R.id.listen_img);
        refreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.listen_swip);
        listen_tv = (TextView)view.findViewById(R.id.listen_title);

        recycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        myHomeAdapter = new MyHomeAdapter(getActivity(),dataList);
        recycler.setAdapter(new MyHomeAdapter(getActivity(),dataList));
        recycler.addItemDecoration(new DividerGridItemDecoration(getActivity()));


        bt_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isWorked("com.yw.news_world.Myservice_back")) {
                    intent_back = new Intent(getActivity(), Myservice_back.class);
                    getActivity().startService(intent_back);
                }
                myBind.listen_last();
            }
        });
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isWorked("com.yw.news_world.Myservice_back")) {
                    intent_back = new Intent(getActivity(), Myservice_back.class);
                    getActivity().startService(intent_back);
                }
                if (Isstart){
                    myBind.listen_pause();
                }else{
                    myBind.listen_start();

                }
            }
        });
        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isWorked("com.yw.news_world.Myservice_back")) {
                    intent_back = new Intent(getActivity(), Myservice_back.class);
                    getActivity().startService(intent_back);
                }
                myBind.listen_next();
            }
        });
        bt_recycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isWorked("com.yw.news_world.Myservice_back")) {
                    intent_back = new Intent(getActivity(), Myservice_back.class);
                    getActivity().startService(intent_back);
                }
                myBind.Play_all();
            }
        });

    }

    /**
     * 初始化服务
     */
    public void initService() {
        new Thread(){
            public void run() {
                intent_back = new Intent(getActivity(), Myservice_back.class);
                getActivity().startService(intent_back);
                Service_bind();
                while(myBind == null){
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myBind.listen_set();
                    }
                });

            }
        }.start();

    }

    /**
     * 听新闻服务
     */
    public void Service_bind(){
        Intent myintent = new Intent(getActivity(), MyService.class);
        myintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!getActivity().bindService(myintent, serviceConnection, getActivity().BIND_AUTO_CREATE)) {
            System.out.println("绑定失败");
        } else {
            System.out.println("绑定成功");
        }
    }

    /**
     * 首次截取信息线程
     */
    class Get_thread extends Thread{
        public void run(){
            while(myBind == null){
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            List<Jsoup_data> data =  myBind.listen_getList();
            if (data != null){
                dataList = new ArrayList<>(data);
                jsoup_get = new Jsoup_get(dataList,"https://m.sina.cn/",listen_handler);
                jsoup_get.getHtml();
                jsoup_get.get_data_listen(data.size());
            }else {
                jsoup_get = new Jsoup_get(dataList, "https://m.sina.cn/", listen_handler);
                jsoup_get.getHtml();
                jsoup_get.get_data_listen(load_count);
            }
                while (!jsoup_get.Isend) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dataList = jsoup_get.getDataList();
                jsoup_get.Isend = false;
                Message message = new Message();
                message.what = 1;
                listen_handler.sendMessage(message);
                myBind.listen_setList(dataList);

        }
    }
    /**
     * 截取信息线程
     */
    class Get_thread2 extends Thread{
        public void run(){
                jsoup_get.getHtml();
                jsoup_get.get_data_listen(load_count);

                while (!jsoup_get.Isend) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dataList = jsoup_get.getDataList();
                jsoup_get.Isend = false;
                Message message = new Message();
                message.what = 1;
                listen_handler.sendMessage(message);
                myBind.listen_setList(dataList);
        }
    }

    /**
     * 服务绑定的回调
     */
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBind = (MyService.MyBind)service;
            myService = myBind.getService();
            myService.setListen_tool(new Listen_Tool() {
                @Override
                public void setStop() {
                    Isstart = false;
                    //设置为播放
                    bt_start.setBackgroundResource(R.mipmap.play);
                }

                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void setStart(String title,int position,Bitmap img) {
                    Isstart = true;
                    //设置为暂停
                    bt_start.setBackgroundResource(R.mipmap.pause);
                    listen_tv.setText(title);
                    try {
                        if (img != null) {
                            imageView.setBackground(new BitmapDrawable(getActivity().getResources(), img));
                        } else {
                            imageView.setBackgroundResource(R.mipmap.news_back);
                        }
                    }catch (NullPointerException e){}
                }


                @Override
                public void setPlan(int position) {

                }

                @Override
                public void closeService() {

                }
            });

            System.out.println("绑定服务");


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };



    /**
     * 判断服务是否开启
     */
    private boolean isWorked(String className) {
        ActivityManager myManager = (ActivityManager) getActivity()
                .getApplicationContext().getSystemService(
                        Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString()
                    .equals(className)) {
                return true;
            }
        }
        return false;
    }






    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        refreshLayout.postDelayed(new Runnable() {

            @Override
            public void run() {
                // 更新数据  更新完后调用该方法结束刷新
                synchronized (this){
                    jsoup_get.getHtml();
                    jsoup_get.getdata(load_count);
                    get_thread2 = new Get_thread2();
                    get_thread2.start();
                }
            }
        }, 2000);
    }






}
