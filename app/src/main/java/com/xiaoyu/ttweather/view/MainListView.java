package com.xiaoyu.ttweather.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xiaoyu.ttweather.R;
import com.xiaoyu.ttweather.activity.CityManageActivity;
import com.xiaoyu.ttweather.utils.SharedPrefUtils;


/**
 * Created by xiaoy on 16/6/15.
 */
public class MainListView extends ListView {

    private MyDragLayout mDragLayout;
    private int startY;
    private View mHeaderView;
    private int mHeaderViewHeight;
    private String mCityName;

    //下拉刷新状态值
    private static final int STATUS_PULL_DOWN_REFRESH = 0;
    private static final int STATUS_RELEASE_REFRESH = 1;
    private static final int STATUS_REFRESHING = 2;

    private int currentRefreshStatus = STATUS_PULL_DOWN_REFRESH;
    private ImageView mIvIcon;
    private TextView mTvText;
    private TextView mTvTime;
    private onRefreshListener mRefreshListener;

    public MainListView(Context context) {
        this(context, null);
    }

    public MainListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHeaderView();
    }

    public void setmCityName(String mCityName) {
        this.mCityName = mCityName;
    }

    private void initHeaderView() {
        //下拉刷新
        mHeaderView = View.inflate(getContext(), R.layout.pull_refresh, null);
        this.addHeaderView(mHeaderView);
        mHeaderView.measure(0, 0);
        mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);

        mIvIcon = (ImageView) mHeaderView.findViewById(R.id.iv_icon);
        mTvText = (TextView) mHeaderView.findViewById(R.id.tv_text);
        mTvTime = (TextView) mHeaderView.findViewById(R.id.tv_refresh_time);
    }

    public void setDragLayout(MyDragLayout dragLayout) {
        mDragLayout = dragLayout;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mDragLayout.getStatus() == MyDragLayout.Status.Close) {
            boolean pull = super.onTouchEvent(ev);
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startY = (int) ev.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:

                    if (startY == -1) {
                        startY = (int) ev.getRawY();
                    }

                    //正在刷新时不下拉
                    if (currentRefreshStatus == STATUS_REFRESHING) {
                        break;
                    }

                    int endY = (int) ev.getRawY();
                    int dy = endY - startY;
                    if (dy > 0) {
                        pull = true;
                        int padding = -mHeaderViewHeight + dy;
                        mHeaderView.setPadding(0, padding, 0, 0);
                        if (padding >= 0 ) {    //松开刷新
                            currentRefreshStatus = STATUS_RELEASE_REFRESH;
                        } else {    //下拉刷新
                            currentRefreshStatus = STATUS_PULL_DOWN_REFRESH;
                        }
                        changeFreshStatus();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    startY = -1;
                    if (currentRefreshStatus == STATUS_RELEASE_REFRESH) {   //松开刷新
                        currentRefreshStatus = STATUS_REFRESHING;
                        changeFreshStatus();
                        mHeaderView.setPadding(0, 0, 0, 0);
                    } else if (currentRefreshStatus == STATUS_PULL_DOWN_REFRESH) {  //下拉刷新
                        mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);
                    }
                    break;
            }
            return pull;
        } else {
            return true;
        }
    }

    private void changeFreshStatus() {
        switch (currentRefreshStatus) {
            case STATUS_PULL_DOWN_REFRESH:
                mTvText.setText("下拉刷新");
                mTvTime.setVisibility(VISIBLE);
                mTvTime.setText(getLastUpdateTime());
                break;
            case STATUS_RELEASE_REFRESH:
                mTvText.setText("松开刷新");
                mTvTime.setVisibility(VISIBLE);
                mTvTime.setText(getLastUpdateTime());
                break;
            case STATUS_REFRESHING:
                mTvText.setText("正在刷新");
                mTvTime.setVisibility(GONE);
                if (mRefreshListener != null) {
                    mRefreshListener.onRefresh();
                }
                break;
        }
    }

    private String getLastUpdateTime() {
        //获取当前时间
        long currentTime = System.currentTimeMillis();
        //获取全局刷新时间
        String globalLastUpdate = SharedPrefUtils.getString(getContext(), "last_update_time", "");
        long globalLastUpdateTime;
        if (TextUtils.isEmpty(globalLastUpdate)) {
            globalLastUpdateTime = 0;
        } else {
            globalLastUpdateTime = Long.parseLong(globalLastUpdate);
        }
        //获取单个城市刷新时间
        String key = mCityName + "_last_update";
        String cityLastUpdate = SharedPrefUtils.getString(getContext(), key, "");
        long cityLastUpdateTime;
        if (TextUtils.isEmpty(cityLastUpdate)) {
            cityLastUpdateTime = 0;
        } else {
            cityLastUpdateTime = Long.parseLong(cityLastUpdate);
        }
        //最近刷新时间
        long lastUpdateTime = globalLastUpdateTime > cityLastUpdateTime ?
                globalLastUpdateTime : cityLastUpdateTime;

        // 毫秒变秒/1000
        int dTime = (int) ((currentTime - lastUpdateTime)/1000);

        if (dTime / 3600 > 0) {
            return dTime / 3600 + "小时前更新";
        } else if (dTime / 60 > 0) {
            return dTime / 60 + "分钟前更新";
        } else {
            return "刚刚更新";
        }
    }



    public interface onRefreshListener {
        public void onRefresh();
    }
    public void setOnRefreshListener(onRefreshListener listener) {
        mRefreshListener = listener;
    }

    public void onRefreshComplete(boolean success) {

        currentRefreshStatus = STATUS_PULL_DOWN_REFRESH;
        changeFreshStatus();
        mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);

        if (success) {
            long currentTime = System.currentTimeMillis();
            String key = mCityName + "_last_update";
            SharedPrefUtils.putString(getContext(), key, currentTime + "");
        }
    }
}
