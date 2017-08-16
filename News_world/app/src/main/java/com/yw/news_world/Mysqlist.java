package com.yw.news_world;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by LYW on 2017/5/28.
 */
public class Mysqlist extends SQLiteOpenHelper {
    private static Mysqlist mysqlist = null;//建立静态对象,使此类对象只有一个


    public static Mysqlist getInstens(Context context) {
        if (mysqlist == null) {
            mysqlist = new Mysqlist(context);
        }
        return mysqlist;
    }

    private Mysqlist(Context context) {//classtable
        super(context, "News.db", null, 1);
    }

    public Mysqlist(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("创建数据库");
        String sql_class_table = "create table if not exists classtable(_id integer primary key autoincrement,classtabledata text,kind text)";
        db.execSQL(sql_class_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


}
