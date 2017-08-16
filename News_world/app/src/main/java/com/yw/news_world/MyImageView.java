package com.yw.news_world;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by Administrator on 2017/6/1.
 */
public class MyImageView extends ImageView {
    public int flags;
    public MyImageView(Context context) {
        super(context);
    }
    public void setFlags(int flags){
        this.flags = flags;
    }

}
