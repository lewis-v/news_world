package com.yw.news_world;

import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017/5/27.
 */
public class Mydata {
    public static List<String> have_kind = new ArrayList<String>();
    public static String kind = "";
    public static String table_name=null;
    public static String[] mykind = {"推荐","娱乐","科技","财经","体育","国际","国内","游戏","社会","军事"};
    public static List<String> all_kind = new ArrayList<String>();
    public static List<Jsoup_data> collect = new ArrayList<Jsoup_data>();
    Context context;
    static Mysql_tool mysqlTool;
    static String sqlist = "collect";
    public static String[] people = {"小燕(普通话女声)","小宇(普通话男声)","小妍(普通话女声)","小峰(普通话男声)",
            "小梅(粤语女声)","小莉(台湾普通话女声)","小蓉(四川话女声)  仅支持中文","小芸(东北话女声)   仅支持中文",
            "小坤(河南话男声)   仅支持中文","小强(湖南话男声)   仅支持中文","小莹(陕西话女声)   仅支持中文",
            "小新(普通话男童声)   仅支持中文","楠楠(普通话女童声)   仅支持中文",
            "老孙(普通话老年男声)    仅支持中文"};
    public static String[] people_name = {"xiaoyan","xiaoyu","vixy","vixf","vixm","vixl","vixr","vixyun",
            "vixk","vixqa","vixying","vixx","vinn","vils"};
    public static int people_position = 0;

    public Mydata(Context context){
        this.context = context;
    }

    /**
     * 设置所有类型
     */
    public void setAllKind(){
        all_kind.clear();
        Collections.addAll(all_kind,mykind);
    }

    /**
     * 获取我的收藏
     */
    public void getcollect(Mysql_tool tool){
        mysqlTool = tool;
        new Thread(){
            public void run(){
                List<Jsoup_data> jsoup_datas = mysqlTool.getObject(sqlist);
                if (jsoup_datas != null) {
                    collect = new ArrayList<Jsoup_data>(jsoup_datas);
                }
            }
        }.start();
    }
    /**
     * 获取我的语音人选择
     */
    public void getpeople(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.yw.news_world_preferences",context.MODE_PRIVATE);
        if (sharedPreferences.getInt("PEOPLE",0) == 0){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("PEOPLE",0);
            editor.commit();
        }
        people_position = sharedPreferences.getInt("PEOPLE",0);
    }

    /**
     * 保存我的语音人设置
     */
    public void savepeople(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.yw.news_world_preferences",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("PEOPLE",people_position);
        editor.commit();
    }

    /**
     * 保存我的收藏
     */
    static public void savecollect(){
        new Thread(){
            public void run(){
                mysqlTool.saveObject(collect,sqlist);
            }
        }.start();
    }

    /**
     * 获取关注的类型
     */
    public void getkind(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.yw.news_world_preferences",context.MODE_PRIVATE);
        if (sharedPreferences.getString("KIND","").length() == 0){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("KIND","推荐,娱乐,科技,财经");
            editor.commit();
        }
        String get = sharedPreferences.getString("KIND","");
        have_kind.clear();
        Collections.addAll(have_kind,get.split(","));
    }

    /**
     * 保存关注的类型
     * @param kind List<String>
     */
    public void savekind(List<String> kind) {
        StringBuilder cache = new StringBuilder();
        for (int i = 0; i < kind.size(); i++) {
            String str = kind.get(i);
            cache.append(str);
            if (i < kind.size() - 1) {
                cache.append(",");
            }
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.yw.news_world_preferences", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("KIND", cache.toString());
        editor.commit();
    }

    /**
     * 获取未关注的类型
     */
    public List<String> getNotHave(){
        List<String> cache= new ArrayList<String>();
        for (String str : all_kind){
            if (!IsExists(str)){
                cache.add(str);
            }
        }
        return new ArrayList<String>(cache);
    }

    /**
     * 判断是否存在类型
     */
    public boolean IsExists(String s){
        for (String string : have_kind){
            if (s.equals(string)){
                return true;
            }
        }
        return false;
    }
}
