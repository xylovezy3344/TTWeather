package com.xiaoyu.ttweather.activity;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.viewpagerindicator.LinePageIndicator;
import com.xiaoyu.ttweather.R;
import com.xiaoyu.ttweather.pager.MainPager;
import com.xiaoyu.ttweather.utils.SharedPrefUtils;
import com.xiaoyu.ttweather.utils.Utils;
import com.xiaoyu.ttweather.view.MainViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public LinearLayout llApp;
    private RelativeLayout mRlTitle;
    private MainViewPager mVpMain;
    private TextView mTvTitle;
    private List<MainPager> pagerList;
    private String[] cityNames;
    private LinePageIndicator mIndicator;
    private ImageButton mIbMarket;
    private ImageButton mIbMenu;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    private void initView() {
        llApp = (LinearLayout) findViewById(R.id.ll_app);
        mRlTitle = (RelativeLayout) findViewById(R.id.rl_title);
        mVpMain = (MainViewPager) findViewById(R.id.vp_main);
        mIndicator = (LinePageIndicator)findViewById(R.id.indicator);

        //标题栏
        //CheckBox mCbVoice = (CheckBox) findViewById(R.id.cb_voice);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mIbMarket = (ImageButton) findViewById(R.id.ib_market);
        mIbMenu = (ImageButton) findViewById(R.id.ib_menu);
    }

    private void initData() {

        int statusBarHeight = Utils.getStatusBarHeight(this);

        mRlTitle.measure(0, 0);
        int measuredHeight = mRlTitle.getMeasuredHeight();


        String selectCities = SharedPrefUtils.getString(this, "select_cities", "");
        cityNames = selectCities.split(",");

        mTvTitle.setText(cityNames[0]);
        mTvTitle.setOnClickListener(this);
        mIbMarket.setOnClickListener(this);
        mIbMenu.setOnClickListener(this);

        pagerList = new ArrayList<>();
        for (String cityName : cityNames) {
            pagerList.add(new MainPager(this, cityName, measuredHeight));
        }

        MainPagerAdapter mMainPagerAdapter = new MainPagerAdapter();
        mVpMain.setAdapter(mMainPagerAdapter);

        mIndicator.setViewPager(mVpMain);
        mIndicator.setCurrentItem(0);
        pagerList.get(0).initData();
        mVpMain.setDragLayout(pagerList.get(0).getmDlContent());
        //We set this on the indicator, NOT the pager
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTvTitle.setText(cityNames[position]);
                pagerList.get(position).initData();
                mVpMain.setDragLayout(pagerList.get(position).getmDlContent());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //添加城市
            case R.id.tv_title:
                startActivityForResult(new Intent(MainActivity.this, CityManageActivity.class), 0x12);
                break;
            case R.id.ib_market:
                break;
            case R.id.ib_menu:
                showPopupWindow();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x12) {
            initData();
        }
    }

    private void showPopupWindow() {
        View view = View.inflate(this, R.layout.main_popup_window, null);
        TextView tvSetting = (TextView) view.findViewById(R.id.tv_setting);

        final PopupWindow popup = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popup.setBackgroundDrawable(getResources().getDrawable(R.drawable.forecast_drawer));
        popup.showAsDropDown(mIbMenu, -Utils.dip2px(this, 50), Utils.dip2px(this, 5));

        tvSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                popup.dismiss();
            }
        });
    }

    private class MainPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return pagerList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(pagerList.get(position).mRootView);
            return pagerList.get(position).mRootView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
