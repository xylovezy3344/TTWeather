package com.xiaoyu.ttweather.activity;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xiaoyu.ttweather.R;
import com.xiaoyu.ttweather.blur.BlurBehind;
import com.xiaoyu.ttweather.model.DailyWeather;
import com.xiaoyu.ttweather.model.WeatherDataFromJson;
import com.xiaoyu.ttweather.utils.DateUtils;
import com.xiaoyu.ttweather.utils.SharedPrefUtils;
import com.xiaoyu.ttweather.utils.WeatherDataUtils;

import java.util.List;


public class WeatherDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private int mInitialPosition;

    private ImageButton mIbClose;
    private TextView mTvTitle;
    private ImageButton mIbShare;
    private ViewPager mVpWeather;
    private List<DailyWeather> daily_forecast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);

        //背景模糊透明效果
        BlurBehind.getInstance().setBackground(this);

        initView();
        initData();
    }

    private void initView() {
        mIbClose = (ImageButton) findViewById(R.id.ib_close);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mIbShare = (ImageButton) findViewById(R.id.ib_share);
        mVpWeather = (ViewPager) findViewById(R.id.vp_weather_detail);
    }

    private void initData() {
        //哪一天
        mInitialPosition = getIntent().getIntExtra("position", 0);
        //城市名称
        String mCityName = getIntent().getStringExtra("city_name");

        mTvTitle.setText(mCityName);
        mIbClose.setOnClickListener(this);
        mIbShare.setOnClickListener(this);

        //拿到城市天气数据
        String result = SharedPrefUtils.getString(this, mCityName, "");
        if (!TextUtils.isEmpty(result)) {
            parseData(result);
        }
    }

    private void parseData(String result) {
        Gson gson = new Gson();
        WeatherDataFromJson dataFromJson = gson.fromJson(result, WeatherDataFromJson.class);
        //天气预报
        daily_forecast = dataFromJson.HeWeatherDataList.get(0).daily_forecast;

        mVpWeather.setAdapter(new WeatherDetailAdapter());
        mVpWeather.setCurrentItem(mInitialPosition);
    }

    private class WeatherDetailAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return daily_forecast.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = View.inflate(WeatherDetailActivity.this, R.layout.pager_weather_detail, null);

            TextView tvTitle = (TextView) view.findViewById(R.id.tv_title_date);
            TextView tvSubTitle = (TextView) view.findViewById(R.id.tv_subtitle_date);

            ImageView ivIconDay = (ImageView) view.findViewById(R.id.iv_icon_day);
            TextView tvCondDay = (TextView) view.findViewById(R.id.tv_cond_day);
            TextView tvHighTemp = (TextView) view.findViewById(R.id.tv_high_temp);

            ImageView ivIconNight = (ImageView) view.findViewById(R.id.iv_icon_night);
            TextView tvCondNight = (TextView) view.findViewById(R.id.tv_cond_night);
            TextView tvLowTemp = (TextView) view.findViewById(R.id.tv_low_temp);

            TextView tvWind = (TextView) view.findViewById(R.id.tv_wind);

            //显示星期几
            if (position == 0) {
                tvTitle.setText("今天");
            } else {
                tvTitle.setText(DateUtils.getWeek(daily_forecast.get(position).date));
            }
            //显示日期（例：6月9号：6/9）
            String[] dates = daily_forecast.get(position).date.split("\\-");
            String date = Integer.valueOf(dates[1]) + "/" + Integer.valueOf(dates[2]);
            tvSubTitle.setText(date);
            //显示图片
            ivIconDay.setImageResource(WeatherDataUtils.getWeatherPic(
                        WeatherDetailActivity.this, daily_forecast.get(position).cond.code_d, true));

            ivIconNight.setImageResource(WeatherDataUtils.getWeatherPic(
                        WeatherDetailActivity.this, daily_forecast.get(position).cond.code_n, false));
            //显示天气情况
            tvCondDay.setText(daily_forecast.get(position).cond.txt_d);
            tvCondNight.setText(daily_forecast.get(position).cond.txt_n);
            //显示最高最低温度
            tvHighTemp.setText(daily_forecast.get(position).tmp.max + "°");
            tvLowTemp.setText(daily_forecast.get(position).tmp.min + "°");
            //显示风向，风力
            String dir = daily_forecast.get(position).wind.dir;
            if (dir.equals("无持续风向")) {
                tvWind.setText("微风 <2级");
            } else {
                if (daily_forecast.get(position).wind.sc.equals("微风")) {
                    tvWind.setText(dir + " <2级");
                } else {
                    tvWind.setText(dir + " " + daily_forecast.get(position).wind.sc + "级");
                }
            }

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_close:
                finish();
                break;
            case R.id.ib_share:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, 0);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, 0);
    }
}
