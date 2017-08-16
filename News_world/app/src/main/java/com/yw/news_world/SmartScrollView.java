package com.yw.news_world;

/**
 * Created by Administrator on 2017/5/29.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * 监听ScrollView滚动到顶部或者底部做相关事件拦截
 */
public class SmartScrollView extends ScrollView {

    private boolean isScrolledToTop = true;// 初始化的时候设置一下值
    private boolean isScrolledToBottom = false;
    public SmartScrollView(Context context){
        super(context);
    }

    public SmartScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private ISmartScrollChangedListener mSmartScrollChangedListener;

    /** 定义监听接口 */
    public interface ISmartScrollChangedListener {
        void onScrolledToBottom();//底部
        void onScrolledToTop();//顶部
        void onScrolledTOther();//不在顶部也不再底部
    }

    public void setScanScrollChangedListener(ISmartScrollChangedListener smartScrollChangedListener) {
        mSmartScrollChangedListener = smartScrollChangedListener;
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (scrollY == 0) {
            isScrolledToTop = clampedY;
            isScrolledToBottom = false;
        } else {
            isScrolledToTop = false;
            isScrolledToBottom = clampedY;
        }
        notifyScrollChangedListeners();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (android.os.Build.VERSION.SDK_INT < 9) {  // API 9及之后走onOverScrolled方法监听
            if (getScrollY() == 0) {
                isScrolledToTop = true;
                isScrolledToBottom = false;
            } else if (getScrollY() + getHeight() - getPaddingTop()-getPaddingBottom() == getChildAt(0).getHeight()) {

                isScrolledToBottom = true;
                isScrolledToTop = false;
            } else {
                isScrolledToTop = false;
                isScrolledToBottom = false;
            }
            notifyScrollChangedListeners();
        }

    }

    private void notifyScrollChangedListeners() {
        if (isScrolledToTop) {
            if (mSmartScrollChangedListener != null) {
                mSmartScrollChangedListener.onScrolledToTop();
            }
        } else if (isScrolledToBottom) {
            if (mSmartScrollChangedListener != null) {
                mSmartScrollChangedListener.onScrolledToBottom();
            }
        }else{
            if (mSmartScrollChangedListener != null) {
                mSmartScrollChangedListener.onScrolledTOther();
            }

        }
    }

    public boolean isScrolledToTop() {
        return isScrolledToTop;
    }

    public boolean isScrolledToBottom() {
        return isScrolledToBottom;
    }
}