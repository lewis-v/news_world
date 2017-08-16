package com.yw.news_world;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2017/5/30.
 */
public class Fragment_home_default extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        RefreshLayout.OnLoadListener {
    private String kind = "";
    private View view = null;
    private ListViewForScrollView listView =null;
    private Jsoup_get jsoup_get =null;
    private List<String> list_title = new ArrayList<String>();
    private List<Jsoup_data> dataList =new ArrayList<Jsoup_data>();
    final private int load_count = 10;//一次加载新闻的数量
    private Mysql_tool mysql_tool ;//自定义jsou_data的sqlite工具类
    private SmartScrollView smartScrollView = null;
    private Thread load_t =null,list_thread =null;
    private String []url = {"https://news.baidu.com/","http://wap.hao123.com/news/"};//存放要加载的网址
    private int url_position =0;//当前加载第几个网址
    private int refresh_count =0;//刷新网页时,无新内容切换网页搜索的次数
    private LinearLayout linearLayout;
    Handler default_handler;
    ImageButton goup;
    int cacheposition = 0;

    private RefreshLayout swipeLayout;

    public Fragment_home_default() {
        // Required empty public constructor


    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        this.kind = bundle.getString("kind");
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        System.out.println("new fragment:" + kind);
        refresh_count = url.length;
        mysql_tool = Mysql_tool.getMysql_tool(getActivity());

        default_handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        if (dataList != null) {
                            try {
                                Jsoup_data.DataAdapter adapter = new Jsoup_data.DataAdapter(getActivity(), R.layout.list, dataList);
                                listView.setAdapter(adapter);//更新viewpager
                                System.out.println("刷新ui" + kind);
                            }catch (NullPointerException e){}
                        }
                        break;
                    case 2:
                        Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
                super.handleMessage(msg);
            }

        };
        view = inflater.inflate(R.layout.fragment_fragment_home_default, container, false);
        listView = (ListViewForScrollView) view.findViewById(R.id.default_listview);
        smartScrollView = (SmartScrollView) view.findViewById(R.id.default_scroll);
        swipeLayout = (RefreshLayout) view.findViewById(R.id.default_swip);
        goup = (ImageButton)view.findViewById(R.id.default_goup);

        goup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smartScrollView.fullScroll(SmartScrollView.FOCUS_UP);

            }
        });

        list_thread = new Mydafaultlist_thread();
        list_thread.start();//开启listview初始化线程

        //swipeLayout.setColorSchemeResources(R.color.colorAccent,R.color.colorPrimary,R.color.colorPrimaryDark,R.color.blank);//设置更新圈圈颜色
        setListener(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),MyWeb.class);
                intent.putExtra("url",dataList.get(position).href);
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                cacheposition = position;
                new AlertDialog.Builder(getActivity()).setMessage("是否收藏本条新闻")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Mydata.collect.add(dataList.get(cacheposition));
                                Mydata.savecollect();
                                System.out.println("收藏"+cacheposition);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
                return true;
            }



        });


        System.out.println("kind:" + kind);
        return view;

    }

    public void onDestroy(){
        super.onDestroy();
        System.out.println("destroy:"+kind);

    }




    /**
     * dafault_listview初始化线程
     */
    class Mydafaultlist_thread extends Thread {
        public void run() {
            jsoup_get = new Jsoup_get(dataList, mysql_tool, url[url_position],default_handler,(kind.toCharArray() + "").substring(4));
            dataList = jsoup_get.getDataList();
            Message message = new Message();
            message.what = 1;
            default_handler.sendMessage(message);
            jsoup_get.getHtml();
            jsoup_get.getKindUrl(kind);
            jsoup_get.getHtml();
            jsoup_get.getdata(load_count);//抓取新闻信息

            refresh_list();
        }
    }

        /**
         * 刷新listview内容
         */
        public boolean refresh_list() {
            jsoup_get.Isend = false;
            while (!jsoup_get.Isend) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (jsoup_get.i==0){
                if (refresh_count==0){
                    refresh_count = url.length;
                    return  false;//切换搜索结束
                }
                url_position++;
                if (url_position == url.length){//最后一个网址,循环回第一个
                    url_position=0;
                }
                    refresh_count--;
                    jsoup_get.url = url[url_position];
                    jsoup_get.getHtml();
                    jsoup_get.getKindUrl(kind);
                    jsoup_get.getHtml();
                    jsoup_get.getdata(load_count);//抓取新闻信息
                    refresh_list();

            }else {
                refresh_count = url.length;
                dataList = jsoup_get.getDataList();
                Message message2 = new Message();
                message2.what = 1;
                default_handler.sendMessage(message2);
            }
            return true;
        }














    /**
     * 设置监听
     */
    private void setListener(ListView listViewForScrollView) {
        swipeLayout.setmListView(listViewForScrollView,smartScrollView);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setOnLoadListener(this);
    }

    /**
     * 上拉刷新
     */
    @Override
    public void onRefresh() {
        swipeLayout.postDelayed(new Runnable() {

            @Override
            public void run() {
                // 更新数据  更新完后调用该方法结束刷新
//                adapter.notifyDataSetChanged();
                synchronized (this){
                    swipeLayout.setRefreshing(false);
                    new Thread(){
                        public void run(){
                            jsoup_get.getHtml();
                            jsoup_get.getdata(load_count);
                            refresh_list();
                        }
                    }.start();
                }
            }
        }, 5000);
    }

    /**
     * 上拉加载更多
     */
    @Override
    public void onLoad() {

        swipeLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    swipeLayout.setLoading(false);
                    load_t = new Thread() {
                        public void run() {
                            System.out.println("加载更多");
                            jsoup_get.refreshBySqlite(load_count);
                            dataList = jsoup_get.getDataList();
                            Message message = new Message();
                            message.what = 1;
                            default_handler.sendMessage(message);
                        }
                    };
                    load_t.start();
                }
            }
        }, 2000);


    }
}

