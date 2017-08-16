package com.yw.news_world;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/5/27.
 */
public class Jsoup_get {
    String url = "";
    List<Jsoup_data> dataList = new ArrayList<Jsoup_data>();
    List<Jsoup_data> List = new ArrayList<Jsoup_data>();
    List<Jsoup_data> cacheList;

    List<String> getImage = new ArrayList<String>();
    Boolean Isend =false,Isget =true,Isgetover=false;
    int i = 0;//线程内用于更新计数
    int sqlite_flag =0;//sqlite读取标记
    Document d = null;
    Mysql_tool mysqlTool;
    Boolean Iscon =true;//有无网络连接引起的获取为空标记
    Handler handler;
    String name = null;

    public Jsoup_get(List<Jsoup_data> mList, Mysql_tool mysqlTool, final String url, Handler handler,String name){
        this.url = url;
        Isend = false;
        Isget = true;
        cacheList = mList;
        this.mysqlTool = mysqlTool;
        i = 0;
        this.handler = handler;
        this.name = name;
        this.List = mysqlTool.getObject(name);
        if(this.List != null){
            System.out.println("默认加载10条");
            refreshBySqlite(10);//默认加载10条
        }
    }

    public Jsoup_get(List<Jsoup_data> mList,final String url,Handler handler){
        this.url = url;
        Isend = false;
        Isget = true;
        cacheList = mList;
        this.handler = handler;
    }

    public Jsoup_get(final String url){
        this.url = url;
        Isend = false;
        Isget = true;
    }

    /**
     * 获取网页源码
     */
    public void getHtml(){
        new Thread(){
            public void run(){
                Isgetover =false;
                try {
                    d = Jsoup.connect(url).get();//连接获得源码
//                    System.out.println("html:"+d);
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                System.out.println("get html:" + d.toString());
                Isgetover =true;
            }
        }.start();
    }

    /**
     * 截取jsoup_data类信息,并保存
     * @param count
     */
    public boolean getdata(final int count){
        new Thread(){
            public void run() {
                while (!Isgetover) {
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Isend = false;//标识获取连接没有结束
                Elements link = null;
                try {
                    link = d.select("a");//截取<a
                    Iscon = true;
                } catch (NullPointerException e) {
                    Message message = new Message();
                    message.what = 2;
                    message.obj = "加载失败,请检查您的网络并重试";
                    handler.sendMessage(message);
                    Iscon = false;
                }
                if (Iscon) {
                    String title = null, href = null;
                    Bitmap imageView = null;
                    int size = dataList.size();
                    i = 0;
                    for (Element element : link) {//兼容新浪
                        if (dataList.size() >= count + size) {
                            break;
                        }
                        Date date = new Date();
                        title = element.attr("title");
                        href = element.attr("href");

                        if (element.select("img").attr("data-src").length() > 0) {
                            try {
                                imageView = Imagedown.getImageBitmap(element.select("img").attr("data-src"));
                            } catch (Exception e) {
                                System.out.println("dataloaderr:" + e);
                            }
                        }
                        else if (element.select("img").attr("src").length() > 0){
                            try {
                                imageView = Imagedown.getImageBitmap(element.select("img").attr("src"));
                            } catch (Exception e) {
                                System.out.println("dataloaderr:" + e);
                            }
                        }else {
                            imageView = null;
                        }
                        if (title.length() > 15 && Isget&&title.indexOf("手机新浪网")==-1) {
                            if (!IsExistsByTitle(title)) {//没有相同的,添加新闻
                                i++;
                                Jsoup_data data = new Jsoup_data(title, href, imageView, date.getTime(), url);
                                dataList.add(0, data);
//                                System.out.println("elemet:" + element.attr("href"));
//                                System.out.println("elemet_text:" + element.text());
//                                System.out.println("elemet_img:" + element.select("img").attr("data-src"));
//                                System.out.println("elemet:" + element.select("img").attr("alt"));
                            }
                        }
                        title = "";
                        href = "";
                    }
                    if (i == 0){
                        for (Element element : link) {//兼容百度
                            if (dataList.size() >= count + size) {
                                break;
                            }
//                            System.out.println("element_a:"+element);
                            Date date = new Date();
                            title = element.text();
                            href = element.attr("href");

                            if (element.select("img").attr("src").length() > 0) {
                                try {
                                    imageView = Imagedown.getImageBitmap(element.select("img").attr("src"));
                                } catch (Exception e) {
                                    System.out.println("dataloaderr:" + e);
                                }
                            } else {
                                imageView = null;
                            }
                            if (title.length() > 15 && Isget&&title.indexOf("手机新浪网")==-1) {
                                if (!IsExistsByTitle(title)) {//没有相同的,添加新闻
                                    i++;
                                    Jsoup_data data = new Jsoup_data(title, href, imageView, date.getTime(), url);
                                    dataList.add(0, data);
//                                    System.out.println("elemet:" + element.attr("href"));
//                                    System.out.println("elemet_text:" + element.text());
//                                    System.out.println("elemet_img:" + element.select("img").attr("data-src"));
//                                    System.out.println("elemet:" + element.select("img").attr("alt"));
                                }
                            }
                            title = "";
                            href = "";
                        }
                    }

                }
                updatatime();
//                System.out.println("dataload_over"+i);
                Isend = true;//获取链接结束
            }
        }.start();
        return true;
    }

    /**
     * 获取list,并更新到数据库
     * @return List<Jsoup_data>
     */
    public List<Jsoup_data> getDataList(){
        System.out.println("返回");
        if (mysqlTool!=null && dataList.size()>0){
            mysqlTool.saveObject(dataList,name);
            System.out.println("name:"+name);
        }
        return new ArrayList<Jsoup_data>(dataList);
    }

    /**
     * 加载滚动栏照片
     * @param  position int
     * @return Jsoup_data
     */
    public Jsoup_data getimg(int position) {
        while (!Isgetover) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Elements link = null;
        try {
            link = d.select("a");//截取<a
            Iscon = true;
        } catch (NullPointerException e) {
            Message message = new Message();
            message.what = 2;
            message.obj = "加载失败,请检查您的网络并重试";
            handler.sendMessage(message);
            Iscon = false;
        }
        Bitmap imageView = null;
        String title = null;
        String href = null;
        Jsoup_data jsoup_data = null;
        if (Iscon) {
            int j = 0;
            for (Element element : link) {
                if (element.select("pic").toString().length() > 13 && element.attr("title").length()>0 &&element.attr("href").length()>10) {
                    try {
                        title = element.attr("title");
                        href = element.attr("href");
                        imageView = Imagedown.getImageBitmap(element.select("pic").toString().substring(7, element.select("pic").toString().length() - 6));
                    } catch (Exception e) {
                        System.out.println("imgloaderr:" + i + e + "\n" + element.toString());
                    }
//                System.out.println("pic" +i+":"+ element.select("pic").toString().substring(7, element.select("pic").toString().length() - 6));
                    if (j == position) {
                        jsoup_data = new Jsoup_data(title,href,imageView);
                        break;
                    }
                    j++;
                }
            }
        }
        return jsoup_data;
    }



    /**
     * 更新新闻跟新的时间
     */
    public void updatatime(){
        long l;
        for(Jsoup_data data :dataList){
            Date date = new Date();
            l=(date.getTime()-data.time)/1000;//计算时间差
            if(l<60){//1分钟以内
                data.timestring = "刚刚";
            }else if (l>=60&&l<60*60){//1小时以内
                data.timestring = l/60+"分钟前";
            }else if(l>=60*60&&l<60*60*24){//24小时以内
                data.timestring = l/60/60+"小时前";
            }else if (l>=60*60*24&&l<60*60*24*2){//昨天
                SimpleDateFormat sf = new SimpleDateFormat("hh:mm");
                data.timestring = "昨天 "+sf.format(date);
            }else if (l>=60*60*24*2&&l<60*60*24*3){//前天
                SimpleDateFormat sf = new SimpleDateFormat("hh:mm");
                data.timestring = "前天 "+sf.format(date);
            }else{
                SimpleDateFormat sf = new SimpleDateFormat("MM月dd日");
                data.timestring = sf.format(date);
            }
        }
    }


    /**
     * 更新sqlite到列表中
     */
    public void refreshBySqlite(int count){
        if(this.List != null) {
            int flag = count + sqlite_flag;
            System.out.println("sqlite长度:"+List.size());
            if (sqlite_flag == List.size()) {
//                System.out.println("加载sqlite到达末端");
                Message message2 = new Message();
                message2.what = 2;
                message2.obj = "没有更多啦!!!";
                handler.sendMessage(message2);
            }
            while (sqlite_flag < List.size() && sqlite_flag < flag) {
                System.out.println("加载sqlite");
                dataList.add(List.get(sqlite_flag));
                sqlite_flag++;
            }
            updatatime();
        }else{
//            System.out.println("加载sqlite到达末端");
            Message message2 = new Message();
            message2.what = 2;
            message2.obj = "没有更多啦!!!";
            handler.sendMessage(message2);
        }
    }

    /**
     * 判断标题是否存在
     * @param title String
     * @return Boolean
     */
    public Boolean IsExistsByTitle (String title){
        for (Jsoup_data mdata :dataList){
            if (mdata.title.equals(title)){
                return true;
            }
        }
        return false;
    }

    /**
     * 获取类别对应网址
     * @param name String
     */
    public void getKindUrl(String name){
        while (!Isgetover) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Isend = false;//标识获取连接没有结束
        Elements link = null;
        try {
            link = d.select("a");//截取<a
            Iscon = true;
        } catch (NullPointerException e) {
            Message message = new Message();
            message.what = 2;
            message.obj = "加载失败,请检查您的网络并重试";
            handler.sendMessage(message);
            Iscon = false;
        }
        if (Iscon) {
            String title = null, href = null;
            i = 0 ;
            for (Element element : link) {
                title = element.text();
                href = element.attr("href");
//                System.out.println("elemet:" + element.attr("href"));
//                System.out.println("elemet_text:" + element.text());
                if (title.equals(name)){
                    if (href.startsWith("/")){
                        href=this.url+href.substring(1);
                    }
                    this.url = href;
//                    System.out.println("url:"+this.url);
                    break;
                }
                title = "";
                href = "";

            }
        }
    }

    /**
     * 获取听新闻的由图片,内容较匹配的新闻
     * @param count int
     */
    public void get_data_listen(final int count){

        while (!Isgetover) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Isend = false;//标识获取连接没有结束
        Elements link = null;
        try {
            link = d.select("a");//截取<a
            Iscon = true;
        } catch (NullPointerException e) {
            Message message = new Message();
            message.what = 2;
            message.obj = "加载失败,请检查您的网络并重试";
            handler.sendMessage(message);
            Iscon = false;
        }
        if (Iscon) {
            String title = null, href = null;
            Bitmap imageView = null;
            int size = dataList.size();
            i = 0;
            for (Element element : link) {//兼容新浪
                if (dataList.size() >= count + size) {
                    break;
                }
                Date date = new Date();
                title = element.attr("title");
                href = element.attr("href");

                if (element.select("pic").toString().length() > 20) {
                    try {
                        imageView = Imagedown.getImageBitmap(element.select("pic").toString().substring(7, element.select("pic").toString().length() - 6));
                    } catch (Exception e) {
//                        System.out.println("dataloaderr:" + e);
                    }
                } else {
                    imageView = null;
                }
                if (imageView != null && title.length() > 15 && Isget) {
                    if (!IsExistsByTitle(title)) {//没有相同的,添加新闻
                        i++;
                        Jsoup_data data = new Jsoup_data(title, href, imageView, date.getTime(), url);
                        if (data.imageView!=null) {
                            System.out.println("加一次");
                            dataList.add(0, data);
                        }
                        System.out.println("elemet:" + element.attr("href"));
                        System.out.println("elemet_text:" + element.text());
                        System.out.println("elemet_img:" + element.select("pic"));
                        System.out.println("elemet:" + element.select("img").attr("alt"));
                    }
                }
                title = "";
                href = "";
            }
            if (i == 0){
                for (Element element : link) {//兼容百度
                    if (dataList.size() >= count + size) {
                        break;
                    }
//                    System.out.println("element_a:"+element);
                    Date date = new Date();
                    title = element.text();
                    href = element.attr("href");

                    if (element.select("img").attr("src").length() > 10) {
                        try {
                            imageView = Imagedown.getImageBitmap(element.select("img").attr("src"));
                        } catch (Exception e) {
                            System.out.println("dataloaderr:" + e);
                        }
                    } else {
                        imageView = null;
                    }
                    if (imageView != null &&title.length() > 50 && Isget) {
                        if (!IsExistsByTitle(title)) {//没有相同的,添加新闻
                            i++;
                            Jsoup_data data = new Jsoup_data(title, href, imageView, date.getTime(), url);
                            if (data.imageView!=null) {
                                dataList.add(0, data);
                                System.out.println("加一次");
                            }
                            System.out.println("elemet:" + element.attr("href"));
                            System.out.println("elemet_text:" + element.text());
                            System.out.println("elemet_img:" + element.select("img").attr("src"));
                            System.out.println("elemet:" + element.select("img").attr("alt"));
                        }
                    }
                    title = "";
                    href = "";
                }
            }

        }
//        System.out.println("dataload_over"+i);
        Isend = true;//获取链接结束
    }

    /**
     * 获取听新闻的内容
     */
    public String get_news_listen(){
        StringBuilder stringBuilder = new StringBuilder();
        while (!Isgetover) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Isend = false;//标识获取连接没有结束
        Elements link = null;
        try {
            link = d.select("p");//截取<a
            Iscon = true;
        } catch (NullPointerException e) {
            Iscon = false;
        }
        if (Iscon) {
            String title = null;
            for (Element element : link) {//兼容新浪
                title = element.text();
                if (title.length() > 10 && Isget && title.indexOf("010-")==-1 && title.indexOf("京ICP")==-1) {
//                    System.out.println("elemet_text:" + element.text());
                    stringBuilder.append(title);
                }
                title = "";
            }
        }
//        System.out.println("dataload_over:"+stringBuilder);
        Isend = true;//获取链接结束
        return stringBuilder.toString();
    }




}
