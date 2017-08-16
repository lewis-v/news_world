package com.yw.news_world;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;

import java.util.ArrayList;
import java.util.List;

public class MyService extends Service {
    static MyBind myBind = null;
    Ifly ifly;
    Listen_Tool listen_tool;
    Listen_Tool listen_tool_notifi;
    List<Jsoup_data> stringList = new ArrayList<Jsoup_data>();
    int position = 0;//播放到第几个新闻
    static boolean Isplay =false;
    boolean Isnext = true,Isgo = true,Ispause = true;
    Thread Play_t;
    int mpercent = 0;//播放进度
    Bitmap myimg = null;
    String mytitle = "News_world";

    public final static String ACTION_BUTTON = "com.yw.servise_test.Myservice_back";
    public final static String INTENT_BUTTONID_TAG = "ButtonId";
    public final static int BUTTON_PREV_ID = 1;
    public final static int BUTTON_PALY_ID = 2;
    public final static int BUTTON_NEXT_ID = 3;
    public final static int BUTTON_CLOSE_ID= 4;
    public ButtonBroadcastReceiver bReceiver;

    public MyService() {
        if (myBind == null) {
            myBind = new MyBind();
        }
        System.out.println("服务类创建");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("服务开启");
    }

    public void setListen_tool(Listen_Tool listen_tool){
        this.listen_tool = listen_tool;
    }
    public void setListen_tool_notifi( Listen_Tool listen_tool){
        this.listen_tool_notifi = listen_tool;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public IBinder onBind(Intent intent) {
        initButtonReceiver();
        return myBind;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        super.onUnbind(intent);
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bReceiver);
        System.out.println("服务关闭");
    }




    class MyBind extends Binder {
        public MyService getService(){
            return MyService.this;
        }
        public void listen_init(Context context){
            ifly = new Ifly(context);
        }

        /**
         * 开始播放
         */
        public void listen_start() {
//            Stop_play_all();
            if (stringList.size() > 0) {
                if (Isplay) {
                    ifly.mTts.resumeSpeaking();

                } else {
                    Play();
                }
                if (Play_t != null) {
                    if (Play_t.isAlive()) {
                        Isgo = false;
                    }
                }
                Ispause = true;
                System.out.println("listen_start");
                listen_tool_notifi.setStart(stringList.get(position).title, position, stringList.get(position).getBitmap());
                listen_tool.setStart(stringList.get(position).title, position, stringList.get(position).getBitmap());
            }
        }
        /**
         * 播放指定位置
         */
        public void listen_start(int pos) {
            Stop_play_all();
            position = pos;
            Play();
            listen_tool_notifi.setStart(stringList.get(position).title,position,stringList.get(position).getBitmap());
            listen_tool.setStart(stringList.get(position).title,position,stringList.get(position).getBitmap());
        }

        /**
         * 设置播放器状态
         */
        public void listen_set(){
            if (stringList!=null) {
                if (stringList.size() > 0) {
                    if (Ispause) {
                        listen_tool_notifi.setStart(stringList.get(position).title, position, stringList.get(position).getBitmap());
                        listen_tool.setStart(stringList.get(position).title, position, stringList.get(position).getBitmap());

                    } else {
                        listen_tool.setStop();
                        listen_tool_notifi.setStop();
                    }
                }
            }
        }

        /**
         * 暂停播放
         */
        public void listen_pause(){
//            Stop_play_all();
            if (Play_t!=null){
                if (Play_t.isAlive()){
                    Isgo = true;
                }
            }System.out.println("listen_pause");
            ifly.mTts.pauseSpeaking();
            Ispause = false;
            listen_tool.setStop();
            listen_tool_notifi.setStop();

        }
        /**
         * 下一则新闻
         */
        public void listen_next(){
            if (stringList != null) {
                if (stringList.size() > 0) {
                    Stop_play_all();
                    if (ifly.mTts.isSpeaking()) {
                        ifly.mTts.stopSpeaking();
                        position++;
                        if (position >= stringList.size()) {
                            position = 0;
                        }
                    }
                    Play();
                }
            }
        }

        /**
         * 上一则新闻
         */
        public void listen_last(){
            if (stringList != null) {
                Stop_play_all();
                if (stringList.size() > 0) {
                    if (ifly.mTts.isSpeaking()) {
                        ifly.mTts.stopSpeaking();
                        position--;
                        if (position == -1) {
                            position = stringList.size() - 1;
                        }
                    } else {
                        if (position == 0) {
                            position = stringList.size() - 2;
                        } else if (position == 1) {
                            position = stringList.size() - 1;
                        } else {
                            position -= 2;
                        }
                    }
                    Play();
                }
            }
        }

        /**
         * 播放所有新闻
         */
        public void Play_all(){
            Stop_play_all();
            Ispause = true;
            if (ifly.mTts.isSpeaking()){
                ifly.mTts.stopSpeaking();
            }
            Isnext = true;
            Isgo = false;
            Play_t = new Thread(){
                int flag = position;
                public void run(){
                    while(flag<stringList.size()&&Isnext) {
                        ifly.mTts.startSpeaking("下一则新闻", mSynListener);
                        Isgo = false;
                        while (ifly.mTts.isSpeaking()||Isgo) {
                            try {
                                sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                break;
                            }
                        }
                        Play();
                        flag++;
                        Isgo = false;
                        while (ifly.mTts.isSpeaking()||Isgo) {
                            try {
                                sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                break;
                            }
                        }


                    }
                    ifly.mTts.startSpeaking("新闻播放结束", mSynListener);
                    Isnext = false;
                }
            };
            Play_t.start();
        }

        /**
         * 结束播放所有
         */
        public void Stop_play_all(){
            Isnext = false;
            if (Play_t!=null) {
                if (Play_t.isAlive()) {
                    Play_t.interrupt();
                    try {
                        Play_t.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }


        /**
         * 设置播放列表
         * @param list (List<String>
         */
        public void listen_setList(List<Jsoup_data> list){
            stringList = new ArrayList<Jsoup_data>(list);
            System.out.println("设置列表成功");
        }
        /**
         * 清空播放列表
         */
        public void listen_clearList(){
            stringList = null;
            System.out.println("清空列表成功");
        }
        /**
         * 获取播放列表
         */
        public List<Jsoup_data> listen_getList(){
            if (stringList == null){
                return null;
            }
            if (stringList.size()>0){
                return new ArrayList<Jsoup_data>(stringList);
            }
            return null;
        }

        public int listen_getplan(){
            return mpercent;
        }
    }




   public void Play() {
       new Thread(){
           public void run(){
               if (position<stringList.size()) {
                   Jsoup_get jsoupGet = new Jsoup_get(stringList.get(position).href);
                   jsoupGet.getHtml();
                   String str = jsoupGet.get_news_listen();
                   System.out.println("listen_触发点击:" + str + "网址:" + stringList.get(position).href);
                   ifly.Speaking(str, mSynListener);
                   Ispause = true;
               }
           }
       }.start();



   }




    //合成监听器
    private SynthesizerListener mSynListener = new SynthesizerListener() {
        //会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError error) {
            Isplay = false;
            listen_tool.setStop();
            listen_tool_notifi.setStop();
            position++;
            if (position>=stringList.size()){
                position=0;
            }
            System.out.println("结束播放");
        }


        //缓冲进度回调
        //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
//            System.out.println("info:"+percent+","+beginPos+","+endPos+","+info);
        }


        //开始播放
        public void onSpeakBegin() {
            Isplay = true;
            listen_tool.setStart(stringList.get(position).title,position,stringList.get(position).getBitmap());
            listen_tool_notifi.setStart(stringList.get(position).title,position,stringList.get(position).getBitmap());
            System.out.println("开始播放");
        }

        //暂停播放
        public void onSpeakPaused() {
            System.out.println("暂停播放");

        }

        //播放进度回调
        //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            listen_tool.setPlan(percent);
            listen_tool_notifi.setPlan(percent);
        }

        //恢复播放回调接口
        public void onSpeakResumed() {
            System.out.println("恢复播放");
        }

        //会话事件回调接口
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
            System.out.println("会话事件回调接口");
        }
    };

    public void initButtonReceiver(){
        bReceiver = new ButtonBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_BUTTON);
        registerReceiver(bReceiver, intentFilter);
    }
    public class ButtonBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();

            if(action.equals(ACTION_BUTTON)){
                int buttonId = intent.getIntExtra(INTENT_BUTTONID_TAG, 0);
                switch (buttonId) {
                    case BUTTON_PREV_ID:
                        System.out.println("last");
                        myBind.listen_last();
                        break;
                    case BUTTON_PALY_ID:
                        if(Ispause){
                            myBind.listen_pause();
                        }else{
                            myBind.listen_start();
                        }
                        break;
                    case BUTTON_NEXT_ID:
                        System.out.println("next");
                        myBind.listen_next();
                        break;
                    case BUTTON_CLOSE_ID:
                        System.out.println("close");
                        listen_tool_notifi.closeService();
                        ifly.Destroy();
                        MyService.this.stopSelf();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}

