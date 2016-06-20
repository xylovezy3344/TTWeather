package com.xiaoyu.ttweather.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 *
 * Created by xiaoy on 16/6/10.
 */
public class MyScrollView extends ScrollView {

    private MyDragLayout mDragLayout;
    private int topMax;
    private boolean isTop;

    public MyScrollView(Context context) {
        this(context, null);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDragLayout(MyDragLayout mDragLayout) {
        this.mDragLayout = mDragLayout;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (topMax == 0 && isTop) {
            getParent().requestDisallowInterceptTouchEvent(false);
        } else {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        topMax = scrollY;
        isTop = clampedY;
    }
}
