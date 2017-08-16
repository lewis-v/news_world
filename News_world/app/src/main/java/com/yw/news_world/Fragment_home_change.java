package com.yw.news_world;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_home_change extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        RefreshLayout.OnLoadListener {
    private String kind = "";
    private View view = null;
    private TextView recommend_tv=null;
    private ViewPager advPager = null;
    private ImageView imageView = null;
    private ImageView[] imageViews = null;
    private ListViewForScrollView listView =null;
    public AtomicInteger what = new AtomicInteger(0);
    AdvAdapter advAdapter = null;
    private boolean isContinue = true;
    Jsoup_get jsoup_get =null;
    int count = 0;
    private List<String> list_title = new ArrayList<String>();
    private List<Jsoup_data> dataList =new ArrayList<Jsoup_data>();
    private float mxdown = 0.0f,mxlast = 0.0f,mydown = 0.0f,mylast = 0.0f;
    final private int load_count = 10;//一次加载新闻的数量
    final private int load_img_count =4;//viewpager加载图片数量
    Mysql_tool mysql_tool ;//自定义jsou_data的sqlite工具类
    SmartScrollView smartScrollView = null;
    Thread load_t =null,list_thread =null;
    String url = null;//存放要加载的网址
    LinearLayout linearLayout;
    int cacheposition = 0;//缓存位置
    Handler recommend_handler;
    Map<Integer,String> map_img = new ArrayMap<Integer,String>();

    private RefreshLayout swipeLayout;
    ImageButton goup;

    public Fragment_home_change() {
        // Required empty public constructor
        this.kind = Mydata.kind;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mysql_tool = Mysql_tool.getMysql_tool(getActivity());

        recommend_handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        advPager.setCurrentItem(what.get());//滚动一次viewpager
                        break;
                    case 1:
                        Jsoup_data.DataAdapter adapter = new Jsoup_data.DataAdapter(getActivity(), R.layout.list, dataList);
                        listView.setAdapter(adapter);//更新viewpager
                        System.out.println("刷新ui");
                        break;
                    case 2:
                        try {
                            Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        }catch (NullPointerException e){

                        }

                        break;
                    case 3:
                        Jsoup_data.DataAdapter adapter2 = new Jsoup_data.DataAdapter(getActivity(), R.layout.list, dataList);
                        listView.setAdapter(adapter2);//更新viewpager
                        System.out.println("刷新ui");
                        smartScrollView.fullScroll(SmartScrollView.FOCUS_UP);
                        break;
                }
                super.handleMessage(msg);
            }

        };
        view = inflater.inflate(R.layout.fragment_home_recommend, container, false);
        listView = (ListViewForScrollView) view.findViewById(R.id.recommend_listview);
        smartScrollView = (SmartScrollView) view.findViewById(R.id.smartscrollview);
        swipeLayout = (RefreshLayout) view.findViewById(R.id.swipe_container);
        goup = (ImageButton)view.findViewById(R.id.recommend_goup);

        goup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smartScrollView.fullScroll(SmartScrollView.FOCUS_UP);

            }
        });

        initViewPager();
        advPager.setCurrentItem(500);//设置显示第几张图片
        list_thread = new Mylist_thread();
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
                                Toast.makeText(getActivity(),"收藏新闻成功",Toast.LENGTH_SHORT).show();
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
    }








    /**
     * listview初始化线程设置
     */

    class Mylist_thread extends Thread {
        public void run() {
            jsoup_get = new Jsoup_get(dataList, mysql_tool, "https://m.sina.cn/",recommend_handler,(kind.toCharArray() + "").substring(4));
            try {
                sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dataList = jsoup_get.getDataList();
            Message message = new Message();
            message.what =1;
            recommend_handler.sendMessage(message);
            jsoup_get.getHtml();
            refresh_img();
            jsoup_get.getdata(load_count);//抓取新闻信息
            refresh_list();
        }
    }

    /**
     * 刷新滚动栏照片
     */
    public void refresh_img(){
        count = 0;
        while(count<load_img_count) {//从网络下载照片并更新
            Thread_img img_t = new Thread_img(count);
            img_t.start();
            count++;
        }
    }

    /**
     * 刷新listview内容
     */
    public void refresh_list(){
        jsoup_get.Isend = false;
        while (!jsoup_get.Isend) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        dataList = jsoup_get.getDataList();
        Message message2 = new Message();
        message2.what = 3;
        recommend_handler.sendMessage(message2);
    }

    /**
     * viewpager的设置
     */
    private void whatOption() {//每秒自增一次
        what.incrementAndGet();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {

        }
    }

    /**
     * 初始化viewpager
     */
    private void initViewPager() {
        advPager = (ViewPager) view.findViewById(R.id.recommend_view);
        recommend_tv = (TextView) view.findViewById(R.id.recommend_tv);
        ViewGroup group = (ViewGroup) view.findViewById(R.id.recommend_linearlayout);

        for (int i =0; i<load_img_count ;i++){
            list_title.add("");
        }
        //      这里存放的是四张广告背景
        final List<View> advPics = new ArrayList<View>();

        ImageView img1 = new ImageView(getActivity());
        img1.setBackgroundResource(R.mipmap.loading);
        advPics.add(img1);

        ImageView img2 = new ImageView(getActivity());
        img2.setBackgroundResource(R.mipmap.loading);
        advPics.add(img2);

        ImageView img3 = new ImageView(getActivity());
        img3.setBackgroundResource(R.mipmap.loading);
        advPics.add(img3);

        ImageView img4 = new ImageView(getActivity());
        img4.setBackgroundResource(R.mipmap.loading);
        advPics.add(img4);

        //      对imageviews进行填充
        imageViews = new ImageView[advPics.size()];
        //小图标
        for (int i = 0; i < advPics.size(); i++) {
            imageView = new ImageView(getActivity());
            imageView.setLayoutParams(new LayoutParams(20, 20));//设置控件的大小
            imageView.setPadding(5, 5, 5, 5);//设置控件间距
            imageViews[i] = imageView;
            if (i == 0) {
                recommend_tv.setText(list_title.get(0));
                imageViews[i]
                        .setBackgroundResource(R.mipmap.circle);
            } else {
                imageViews[i]
                        .setBackgroundResource(R.mipmap.circle_not);
            }
            group.addView(imageViews[i]);
        }

        advAdapter = new AdvAdapter(advPics);
        advPager.setAdapter(advAdapter);
        advPager.setOnPageChangeListener(new GuidePageChangeListener());
        advPager.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isContinue = false;
                        mxdown = event.getRawX();
                        mydown = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        isContinue = false;
                        mxlast = event.getRawX();
                        mylast = event.getRawY();
                        if (Math.abs(mxlast - mxdown)>Math.abs(mylast - mydown)){//如果x轴滑动大于轴,判定为对viewpager的操作
                           swipeLayout.setEnabled(false);//使swipe失效
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        isContinue = true;
                        swipeLayout.setEnabled(true);//恢复swipe效果,此处有可能不置信,事件不触发,在swipe中有处理
                        break;
                    default:
                        isContinue = true;
                        break;
                }
                return false;
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (isContinue) {
                        Message message = new Message();
                        message.what = 0;
                        recommend_handler.sendMessage(message);
                        whatOption();
                    }
                }
            }

        }).start();
    }
    private final class GuidePageChangeListener implements ViewPager.OnPageChangeListener {//滚动的触发器

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
            what.getAndSet(arg0);
            for (int i = 0; i < imageViews.length; i++) {
                recommend_tv.setText(list_title.get(arg0%4));
                imageViews[arg0%4]
                        .setBackgroundResource(R.mipmap.circle);
                if (arg0%4 != i%4) {
                    imageViews[i%4]
                            .setBackgroundResource(R.mipmap.circle_not);
                }
            }

        }

    }

    private class AdvAdapter extends PagerAdapter {//viewpager适配器
        private List<View> views = null;

        public AdvAdapter(List<View> views) {
            this.views = views;
        }

        public void changelist(int position,View view){//更改显示图片,实现实时更新
            views.set(position,view);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(views.get(arg1%4));
        }



        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public Object instantiateItem(ViewGroup arg0, int arg1) {
            View view =views.get(arg1 % 4);
            arg0.removeView(view);
            arg0.addView(view);
//            try {
//                ((ViewPager) arg0).addView(views.get(arg1 % 4), 0);
//            }catch (Exception e){
//                for (View view : views) {
//                    ViewGroup p = (ViewGroup) view.getParent();
//                    if (p != null) {
//                        p.removeAllViewsInLayout();
//                    }
//                    System.out.println("轮播:"+e);
//                }
//            }
            return views.get(arg1%4);
        }



        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }



    }
    class Thread_img extends Thread{//更新滚动栏的线程
        int position;
        Thread_img(int position){
            this.position = position;
        }
        public void run(){
            System.out.println(position + "start");
            MyImageView img = new MyImageView(getActivity());
            img.setFlags(position);
            Jsoup_data jsoup_data= jsoup_get.getimg(position);//加载照片,标题
            if (jsoup_data != null) {
                Bitmap bitmap = jsoup_data.getBitmap();
                String title = jsoup_data.title;
                map_img.put(position,jsoup_data.href);
                if (bitmap != null && title != null) {
                    list_title.set(position, title);
                    img.setImageBitmap(bitmap);
                    img.setScaleType(ImageView.ScaleType.FIT_XY);
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MyImageView myImageView = (MyImageView)v;
                            Intent intent = new Intent(getActivity(), MyWeb.class);
                            intent.putExtra("url", map_img.get(myImageView.flags));
                            startActivity(intent);
                        }
                    });
                    advAdapter.changelist(position, img);//更新position位置的图片
                }
            }
        }
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
                                recommend_handler.sendMessage(message);
                            }
                        };
                        load_t.start();
                    }
                }
            }, 2000);


    }
}
