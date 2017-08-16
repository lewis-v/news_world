package com.yw.news_world;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Scroller;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class MyWeb extends MyswipActivity {

    WebView webView;
    String url = null;
    FrameLayout frameLayout;
    WebChromeClient.CustomViewCallback call;
    View myview;
    ProgressBar web_progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.url = getIntent().getStringExtra("url");
        setContentView(R.layout.activity_my_web);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);//硬件加速
        webView = (WebView) findViewById(R.id.webview);
        frameLayout = (FrameLayout) findViewById(R.id.myweb);
        web_progressbar = (ProgressBar) findViewById(R.id.web_progressbar);

        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);//硬件加速
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webSettings.setBuiltInZoomControls(true);//设置支持缩放
        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);//webview推荐模式
        webSettings.setSaveFormData(true);// 保存表单数据
        webSettings.setGeolocationEnabled(true);// 启用地理定位
        webSettings.setDisplayZoomControls(false);//隐藏webview缩放按钮　
        webSettings.setGeolocationDatabasePath("/data/data/com.jereh.html5webview/databases/");// 设置定位的数据库路径
        webSettings.setDomStorageEnabled(true);



        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null) return false;

                try {
                    if (url.startsWith("weixin://") || url.startsWith("alipays://") ||
                            url.startsWith("mailto://") || url.startsWith("tel://") || url.startsWith("intent://")
                            || url.startsWith("ifensi.fansfocus://")
                        //其他自定义的scheme
                            ) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    }
                } catch (Exception e) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                    return false;
                }

                //处理http和https开头的url

                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view,
                                           SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.loadUrl("javascript:try{autoplay();}catch(e){}");
            }


        });
        //进度条
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
                if (myview != null) {
                    callback.onCustomViewHidden();
                }
                myview = view;
                frameLayout.addView(myview);
                view.setVisibility(View.VISIBLE);
                call = callback;
                frameLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onHideCustomView() {
                super.onHideCustomView();
                if (myview == null) {
                    return;
                }
                myview.setVisibility(View.GONE);
                frameLayout.removeView(myview);
                myview = null;
                frameLayout.setVisibility(View.VISIBLE);
                call.onCustomViewHidden();
                webView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                super.onGeolocationPermissionsShowPrompt(origin, callback);
                callback.invoke(origin, true, false);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    Log.i("打印日志", "加载完成");
                    web_progressbar.setVisibility(View.INVISIBLE);
                }
                web_progressbar.setProgress(newProgress);
                System.out.println("newProgress:" + newProgress);
            }
        });

    }






    @Override
    public void onStop() {
        super.onStop();
        webView.stopLoading();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        view.removeAllViews();
        webView.destroy();
    }

}






