package com.yw.news_world;

/**
 * Created by Administrator on 2017/5/28.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ListView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static java.lang.Math.abs;

/**
 * 继承自SwipeRefreshLayout,从而实现滑动到底部时上拉加载更多的功能.
 */
public class RefreshLayout extends SwipeRefreshLayout implements ListViewForScrollView.OnScrollListener {

    /**
     * 滑动到最下面时的上拉操作
     */

    private int mTouchSlop;
    /**
     * listview实例
     */
    private ListView mListView;

    /**
     * 上拉监听器, 到了最底部的上拉加载操作
     */
    private OnLoadListener mOnLoadListener;

    /**
     * ListView的加载中footer
     */
    private View mListViewFooter;

    /**
     * 按下时的y,x坐标
     */
    private int mYDown,mXDown;
    /**
     * 抬起时的y坐标, 与mYDown一起用于滑动到底部时判断是上拉还是下拉
     * 判断是否为左右滑动,解决viewpager的同时触发
     */
    private int mLastY,mLastX;
    /**
     * 是否在加载中 ( 上拉加载更多 )
     */
    private boolean isLoading = false;
    /**
     * scroll滑动监听器
     */
    SmartScrollView smartScrollView;
    /**
     * 是否在底部
     */
    boolean IsonScrollEnd=false;

    /**
     * @param context
     */
    public RefreshLayout(Context context) {
        this(context, null);
    }

    @SuppressLint("InflateParams")
    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mListViewFooter = LayoutInflater.from(context).inflate(
                R.layout.list_footer, null, false);//加载的背景
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 初始化ListView对象
        if (mListView == null) {
//            getListView();
            System.out.println("mListView is null");
        }
    }

    public void setmListView(ListView listView,SmartScrollView smartScrollView){
        this.mListView = listView;
        this.smartScrollView = smartScrollView;
        this.smartScrollView.setScanScrollChangedListener(new SmartScrollView.ISmartScrollChangedListener() {
            @Override
            public void onScrolledToBottom() {//到达底部
               IsonScrollEnd = true;
            }

            @Override
            public void onScrolledToTop() {//到达顶部

            }

            @Override
            public void onScrolledTOther() {//不在顶.底部
                IsonScrollEnd = false;
            }
        });
    }

//    /**
//     * 获取ListView对象
//     */
//    private void getListView() {
//        int childs = getChildCount();
//        if (childs > 0) {
//
//            View childView = getChildAt(1);
//
//            System.out.println(childs+"获取ListView对象"+childView);
//            if (childView instanceof ListViewForScrollView) {
//                mListView = (ListView) childView;
//                // 设置滚动监听器给ListView, 使得滚动的情况下也可以自动加载
//                mListView.setOnScrollListener(this);
//                Log.d(VIEW_LOG_TAG, "### 找到listview");
//            }else{
//                System.out.println("not to find listview");
//            }
//        }
//    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 按下
                mYDown = (int) event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                // 移动
                mLastY = (int) event.getRawY();
                break;

            case MotionEvent.ACTION_UP:
                // 抬起

                this.setEnabled(true);//启用布局,防止在viewpager中没有还原
                if (canLoad()) {
                    System.out.println("触发load");
                    loadData();
                }
                break;
            default:
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    /**
     * 是否可以加载更多, 条件是到了最底部, listview不在加载中, 且为上拉操作.
     *
     * @return
     */
    private boolean canLoad() {
        return IsonScrollEnd&&isBottom() && !isLoading && isPullUp();
    }

    /**
     * 判断是否到了最底部
     */
    private boolean isBottom() {

        if (mListView != null && mListView.getAdapter() != null) {

            if (mListView.getLastVisiblePosition() == (mListView
                    .getAdapter().getCount() - 1)){
                System.out.println(mListView.getLastVisiblePosition()+"最后一个"+(mListView.getAdapter().getCount() - 1));
                return true;
            }

        }
        return false;
    }

    /**
     * 是否是上拉操作
     *
     * @return
     */
    private boolean isPullUp() {
        System.out.println("距离:"+mTouchSlop+",(mYDown - mLastY):"+(mYDown - mLastY));
        return (mYDown - mLastY) >= mTouchSlop*5;
    }

    /**
     * 如果到了最底部,而且是上拉操作.那么执行onLoad方法
     */
    private void loadData() {
        if (mOnLoadListener != null) {
            // 设置状态
            setLoading(true);
            //
            mOnLoadListener.onLoad();
        }
    }

    /**
     * @param loading
     */
    public void setLoading(boolean loading) {
        isLoading = loading;
        if (isLoading) {
            mListView.addFooterView(mListViewFooter);
        } else {
            mListView.removeFooterView(mListViewFooter);
            mYDown = 0;
            mLastY = 0;
            mLastX = 0;
            mXDown = 0;
        }
    }

    /**
     * @param loadListener
     */
    public void setOnLoadListener(OnLoadListener loadListener) {
        mOnLoadListener = loadListener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // 滚动时到了最底部也可以加载更多
        if (canLoad()) {
            loadData();
        }
    }

    /**
     * 设置刷新
     */
    public void setRefreshing(SwipeRefreshLayout refreshLayout,
                                     boolean refreshing, boolean notify) {
        Class<? extends SwipeRefreshLayout> refreshLayoutClass = refreshLayout
                .getClass();
        if (refreshLayoutClass != null) {

            try {
                Method setRefreshing = refreshLayoutClass.getDeclaredMethod(
                        "setRefreshing", boolean.class, boolean.class);
                setRefreshing.setAccessible(true);
                setRefreshing.invoke(refreshLayout, refreshing, notify);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 加载更多的监听器
     */
    public interface OnLoadListener {
        public void onLoad();
    }


}
