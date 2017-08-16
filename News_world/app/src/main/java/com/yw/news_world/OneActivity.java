package com.yw.news_world;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;

public class OneActivity extends Activity {
    Intent intent;
    MyService myService;
    MyService.MyBind myBind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);
        intent = new Intent(this, MyService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!bindService(intent, serviceConnection, BIND_AUTO_CREATE)) {
            System.out.println("绑定失败");
        } else {
            System.out.println("绑定成功");
        }

    }
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("绑定服务");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
