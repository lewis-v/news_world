package com.yw.news_world;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/5/31.
 */
interface Listen_Tool {
    void setStop();
    void setStart(String title,int position,Bitmap img);
    void setPlan(int position);
    void closeService();

}
