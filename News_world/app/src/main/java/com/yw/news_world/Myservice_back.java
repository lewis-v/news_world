package com.yw.news_world;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import java.util.ArrayList;

public class Myservice_back extends Service {
    MyService.MyBind myBind = null;
    Bitmap myimg = null;
    String mytitle = "News_world";
    Intent intent_back;


    public Myservice_back() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        intent_back = new Intent(this, MyService.class);
        startService(intent_back);
        if (myBind ==null) {
            intent = new Intent(this, MyService.class);//notifition绑定听新闻服务
            if (!bindService(intent, serviceConnection, BIND_AUTO_CREATE)) {
                System.out.println("后台绑定失败");
            } else {
                System.out.println("后台绑定成功");
            }
        }

        shwoNotify(true,mytitle);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        myBind.listen_clearList();
        unbindService(serviceConnection);
        stopService(intent_back);
        System.out.println("后台服务关闭");
    }



    public void shwoNotify(boolean Isplay,String title){
        int notifyId = 101;
        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.mynotification);
//        mRemoteViews.setImageViewResource(R.id.notification_img, R.mipmap.ic_launcher);
        if (myimg!=null) {
            mRemoteViews.setImageViewBitmap(R.id.notification_img,myimg);
        }else{
            mRemoteViews.setImageViewResource(R.id.notification_img, R.mipmap.news_back);
        }
        if(Isplay){
            mRemoteViews.setImageViewResource(R.id.bt_play,R.mipmap.play_back);
        }else{
            mRemoteViews.setImageViewResource(R.id.bt_play,R.mipmap.pause_back);
        }
        mRemoteViews.setTextViewText(R.id.notification_title,title);
        Intent buttonIntent = new Intent();
        /* 上一首按钮 */
        buttonIntent.setAction(MyService.ACTION_BUTTON);
        buttonIntent.putExtra(MyService.INTENT_BUTTONID_TAG, MyService.BUTTON_PREV_ID);
        //这里加了广播，所及INTENT的必须用getBroadcast方法
        PendingIntent intent_prev = PendingIntent.getBroadcast(this, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.bt_last, intent_prev);
        /* 播放/暂停  按钮 */
        buttonIntent.setAction(MyService.ACTION_BUTTON);
        buttonIntent.putExtra(MyService.INTENT_BUTTONID_TAG, MyService.BUTTON_PALY_ID);
        PendingIntent intent_play = PendingIntent.getBroadcast(this, 2, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.bt_play, intent_play);
        /* 下一首 按钮  */
        buttonIntent.setAction(MyService.ACTION_BUTTON);
        buttonIntent.putExtra(MyService.INTENT_BUTTONID_TAG, MyService.BUTTON_NEXT_ID);
        PendingIntent intent_next = PendingIntent.getBroadcast(this, 3, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.bt_next, intent_next);
        /* 关闭 按钮  */
        buttonIntent.setAction(MyService.ACTION_BUTTON);
        buttonIntent.putExtra(MyService.INTENT_BUTTONID_TAG, MyService.BUTTON_CLOSE_ID);
        PendingIntent intent_close = PendingIntent.getBroadcast(this, 4, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notification_close, intent_close);



        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContent(mRemoteViews)
                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))//设置点击意图,不设置会不显示
                .setTicker("News_world")//通知栏标题
                .setPriority(Notification.PRIORITY_DEFAULT)//优先级
                .setOngoing(true)//设置是否为一个后台任务
                .setSmallIcon(R.mipmap.news_back_small);//通知栏缩略图标

        Notification notify = mBuilder.build();

        notify.contentView = mRemoteViews;

        startForeground(notifyId, notify);
    }
    public PendingIntent getDefalutIntent(int flags){
        Intent myintent = new Intent(Myservice_back.this,MainActivity.class);
        myintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        PendingIntent pendingIntent= PendingIntent.getActivity(this, 0, myintent, flags);
        return pendingIntent;
    }


    /**
     * 判断服务是否开启
     */
    private boolean isWorked(String className) {
        ActivityManager myManager = (ActivityManager) this
                .getApplicationContext().getSystemService(
                        Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString()
                    .equals(className)) {
                return true;
            }
        }
        return false;
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBind = (MyService.MyBind)service;
            myBind.getService().setListen_tool_notifi(new Listen_Tool() {
                @Override
                public void setStop() {
                    System.out.println("stop");
                    shwoNotify(true,mytitle);
                }

                @Override
                public void setStart(String title,int position,Bitmap img) {
                    myimg = img;
                    mytitle = title;
                    shwoNotify(false,mytitle);
                    System.out.println("start");
                }

                @Override
                public void setPlan(int position) {

                }

                @Override
                public void closeService() {
                    myBind.listen_clearList();
                    Myservice_back.this.stopSelf();
                }
            });
            myBind.listen_init(Myservice_back.this);
            System.out.println("后台绑定服务");

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
