package com.xiaoyu.ttweather.view;

import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;


import com.nineoldandroids.view.ViewHelper;
import com.xiaoyu.ttweather.R;
import com.xiaoyu.ttweather.utils.Utils;

public class MyDragLayout extends LinearLayout {

    private ViewDragHelper mDragHelper;
    private int mHeight;
    private int mWidth;
    private int mRange;
    private ViewGroup mMainContent;
    private ViewGroup mBottomContent;

    private OnDragStatusChangeListener mListener;
    private Status mStatus = Status.Close;

    private int mBottomContentHeight;
    private int mMainContentHideHeight;

    /**
     * 状态枚举
     */
    public enum Status {
        Close, Open, Draging;
    }

    public interface OnDragStatusChangeListener {
        void onClose();

        void onOpen();

        void onDraging(float percent);
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status mStatus) {
        this.mStatus = mStatus;
    }

    public void setDragStatusListener(OnDragStatusChangeListener mListener) {
        this.mListener = mListener;
    }

    public MyDragLayout(Context context) {
        this(context, null);
    }

    public MyDragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyDragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        /**
         * a. 初始化 (通过静态方法)
         */
        mDragHelper = ViewDragHelper.create(this, mCallBack);
    }

    /**
     * b.传递触摸事件
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        // 返回true, 持续接受事件
        return true;
    }

    /**
     * c.重写事件
     */
    ViewDragHelper.Callback mCallBack = new ViewDragHelper.Callback() {

        /**
         * 1. 根据返回结果决定当前child是否可以拖拽
         *
         * @param child 当前被拖拽的View
         * @param pointerId 区分多点触摸的id
         * @return
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        // 当capturedChild被捕获时,调用.
        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }

        // 返回拖拽的范围, 不对拖拽进行真正的限制. 仅仅决定了动画执行速度
        @Override
        public int getViewVerticalDragRange(View child) {
            return -mRange;
        }

        /**
         * 2. 根据建议值 修正将要移动到的(横向)位置   (重要)
         *    此时没有发生真正的移动
         *
         * @param child 当前拖拽的View
         * @param top   新的位置的建议值    top = oldTop + dy
         * @param dy    位置变化量
         * @return
         */
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            if (child == mMainContent) {
                top = fixTop(top, mMainContent);
            } else {
                top = fixTop(top, mBottomContent);
            }
            return top;
        }

        /**
         * 3. 当View位置改变的时候, 处理要做的事情 (更新状态, 伴随动画, 重绘界面)
         *    此时,View已经发生了位置的改变
         *
         * @param changedView   改变位置的View
         * @param left  新的左边值
         * @param top   新的上边值
         * @param dx    水平方向变化量
         * @param dy    垂直方向变化量
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);

            int anotherTop = top;

            if (changedView == mMainContent) {

                top = fixTop(top, mMainContent);

                float percent = top * 1.0f / mRange;
                int dy2 = (int) (mMainContentHideHeight * percent);
                int bottom = mHeight + top - dy2;
                mMainContent.layout(0, top, mWidth, bottom);

                anotherTop = bottom;
                anotherTop = fixTop(anotherTop, mBottomContent);
                mBottomContent.layout(0, anotherTop, mWidth, mBottomContentHeight
                        + anotherTop);

                // 更新状态,执行动画
                dispatchDragEvent(top);

            } else {

                anotherTop = mMainContent.getTop() + dy;
                anotherTop = fixTop(anotherTop, mMainContent);
                float percent = 1.0f * anotherTop / mRange;
                int dy2 = (int) (mMainContentHideHeight * percent);

                int bottom = mHeight + anotherTop - dy2;

                mMainContent.layout(0, anotherTop, mWidth, bottom);
                mBottomContent.layout(0, bottom, mWidth, mBottomContentHeight + bottom);
                // 更新状态,执行动画
                dispatchDragEvent(anotherTop);
            }

            // 为了兼容低版本, 每次修改值之后, 进行重绘
            invalidate();
        }

        /**
         * 4. 当View被释放的时候, 处理的事情(执行动画)
         *
         * @param releasedChild 被释放的子View
         * @param xvel  水平方向的速度, 向右为+
         * @param yvel  竖直方向的速度, 向下为+
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

            // 判断执行 关闭/开启
            // 先考虑所有开启的情况,剩下的就都是关闭的情况

            if (releasedChild == mBottomContent) {
                if (yvel >= 0 && mMainContent.getTop() > mRange) {
                    close();
                }
            } else {
                if (yvel == 0 && mMainContent.getTop() < mRange / 2.0f) {
                    open();
                } else if (yvel < 0 && mMainContent.getTop() < 0) {
                    open();
                } else {
                    close();
                }
            }
        }
    };

    @Override
    public void computeScroll() {
        super.computeScroll();
        // 2. 持续平滑动画 (高频率调用)
        if (mDragHelper.continueSettling(true)) {
            //  如果返回true, 动画还需要继续执行
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void close() {
        int finalTop = 0;
        // 1. 触发一个平滑动画
        if (mDragHelper.smoothSlideViewTo(mMainContent, 0, finalTop)) {
            // 返回true代表还没有移动到指定位置, 需要刷新界面.
            // 参数传this(child所在的ViewGroup)
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void open() {
        int finalTop = mRange;
        if (mDragHelper.smoothSlideViewTo(mMainContent, 0, finalTop)) {
            // 返回true代表还没有移动到指定位置, 需要刷新界面.
            // 参数传this(child所在的ViewGroup)
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * 根据范围修正上边值
     */
    private int fixTop(int top, ViewGroup viewGroup) {

        if (viewGroup == mMainContent) {
            if (top > 0) {
                return 0;
            } else if (top < mRange) {
                return mRange;
            }
        } else if (viewGroup == mBottomContent) {
            if (top >= mHeight) {
                return mHeight;
            } else if (top < mHeight + mRange - mMainContentHideHeight) {
                return mHeight + mRange - mMainContentHideHeight;
            }
        }

        return top;
    }

    /**
     * 更新状态,执行动画
     */
    protected void dispatchDragEvent(int newTop) {
        float percent = newTop * 1.0f / mRange;

        if (mListener != null) {
            mListener.onDraging(percent);
        }

        // 更新状态, 执行回调
        Status preStatus = mStatus;
        mStatus = updateStatus(percent);
        if (mStatus != preStatus) {
            // 状态发生变化
            if (mStatus == Status.Close) {
                // 当前变为关闭状态
                if (mListener != null) {
                    mListener.onClose();
                }
            } else if (mStatus == Status.Open) {
                if (mListener != null) {
                    mListener.onOpen();
                }
            }
        }

        //伴随动画:
        animViews(newTop);

    }

    private void animViews(int newTop) {

        float percent = newTop * 1.0f / mRange;
        int dy2 = (int) (mMainContentHideHeight * percent);
        int bottom = mHeight + newTop - dy2;

        float newPercent = (mHeight - bottom * 1.0f) / (mMainContentHideHeight - mRange);

        //估值器
        FloatEvaluator evaluator = new FloatEvaluator();
        ArgbEvaluator argbEvaluator = new ArgbEvaluator();

        // 1. 下面板: 缩放动画, 平移动画, 透明度动画
        // 缩放动画 2.0 -> 1.0 ---- 2.0-percent
//        ViewHelper.setScaleX(mBottomContent, evaluator.evaluate(newPercent, 2.0f, 1.0f));
//        ViewHelper.setScaleY(mBottomContent, evaluator.evaluate(newPercent, 2.0f, 1.0f));
        // 平移动画: mHeight -> mHeight+mRange
//        ViewHelper.setTranslationY(mBottomContent, evaluator.evaluate(newPercent, mHeight,
//                mHeight + mRange - mMainContentHideHeight));
        // 透明度: 0.5 -> 1.0f
//        ViewHelper.setAlpha(mBottomContent, percent);

        // 2. 背景动画: 亮度变化 (颜色变化)
        ViewParent parent = getParent();
        LinearLayout parentParent = (LinearLayout) parent.getParent();
        parentParent.getBackground().setColorFilter((Integer) argbEvaluator.evaluate(percent,
                Color.TRANSPARENT, Color.argb(200, 0, 0, 0)), PorterDuff.Mode.SRC_OVER);
    }

    private Status updateStatus(float percent) {
        if (percent == 0f) {
            return Status.Close;
        } else if (percent == 1.0f) {
            return Status.Open;
        }
        return Status.Draging;
    }

    /**
     * 当尺寸有变化的时候调用
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();

        // 移动的范围
        mRange = Utils.dip2px(getContext(), 300.0f) - mHeight;

        mMainContentHideHeight = Utils.dip2px(getContext(), 120);
        mBottomContentHeight = mMainContentHideHeight - mRange;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mMainContent = (ViewGroup) getChildAt(0);
        mBottomContent = (ViewGroup) getChildAt(1);
    }

}
