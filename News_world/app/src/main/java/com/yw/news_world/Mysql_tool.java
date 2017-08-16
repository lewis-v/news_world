package com.yw.news_world;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Created by LYW on 2017/5/28.
 */
public class Mysql_tool {
    Context context;
    Mysqlist mysqlist;
    static Mysql_tool mysql_tool;

    public static Mysql_tool getMysql_tool(Context context){
        if (mysql_tool == null){
            mysql_tool = new Mysql_tool(context);
        }
        return mysql_tool;
    }

    public Mysql_tool(Context context){
        this.context = context;
    }

    public synchronized void saveObject(List<Jsoup_data> mdata,String name){

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(mdata);
                objectOutputStream.flush();
                byte data[] = byteArrayOutputStream.toByteArray();
                objectOutputStream.close();
                byteArrayOutputStream.close();
                mysqlist = Mysqlist.getInstens(context);
                SQLiteDatabase database = mysqlist.getWritableDatabase();
                try {
                    database.execSQL("insert into classtable (classtabledata,kind) values(?,?)", new Object[]{data, name});
                } catch (IllegalStateException e) {
                    System.out.println("sqlist_err:" + e);
                }
                System.out.println("插入数据库");
                database.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public synchronized List<Jsoup_data> getObject(String name){

            List<Jsoup_data> mdata = null;
            Mydata.table_name = name;
            mysqlist = Mysqlist.getInstens(context);
            SQLiteDatabase database = mysqlist.getReadableDatabase();
            Cursor cursor = database.rawQuery("select * from classtable", null);
            if (cursor != null) {
                try {
                    while (cursor.moveToNext()) {
                        if (cursor.getString(cursor.getColumnIndex("kind")).equals(name)) {
                            byte data[] = cursor.getBlob(cursor.getColumnIndex("classtabledata"));
                            ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(data);
                            try {
                                ObjectInputStream inputStream = new ObjectInputStream(arrayInputStream);
                                mdata = (List<Jsoup_data>) inputStream.readObject();
                                inputStream.close();
                                arrayInputStream.close();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }catch (IllegalStateException e){
                    System.out.println("sqlist_err:"+e);
                }
            }
            return mdata;
    }

}
