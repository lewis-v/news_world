package com.yw.news_world;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2017/5/27.
 */
public class Imagedown {
    /**
     * 获取图片
     * @param path 图片路径
     * @return
     */
    public static Bitmap getImageBitmap(String path) throws Exception{
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if(conn.getResponseCode() == 200){
            InputStream inStream = conn.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inStream);
            return bitmap;
        }
        return null;
    }


}
