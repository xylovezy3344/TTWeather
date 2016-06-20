package com.xiaoyu.ttweather.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MainViewPager extends ViewPager {
    private MyDragLayout mDragLayout;
    private int startY;
    private int startX;

    public MainViewPager(Context context) {
        this(context, null);
    }

    public MainViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDragLayout(MyDragLayout mDragLayout) {
        this.mDragLayout = mDragLayout;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragLayout.getStatus() == MyDragLayout.Status.Close && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mDragLayout.getStatus() == MyDragLayout.Status.Close && super.onTouchEvent(ev);
    }
}
