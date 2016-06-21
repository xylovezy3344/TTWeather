package com.xiaoyu.ttweather.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoyu.ttweather.R;
import com.xiaoyu.ttweather.activity.MainActivity;
import com.xiaoyu.ttweather.activity.WeatherDetailActivity;
import com.xiaoyu.ttweather.blur.BlurBehind;
import com.xiaoyu.ttweather.blur.OnBlurCompleteListener;
import com.xiaoyu.ttweather.model.Aqi;
import com.xiaoyu.ttweather.model.CityInfo;
import com.xiaoyu.ttweather.model.DailyWeather;
import com.xiaoyu.ttweather.model.NowWeather;
import com.xiaoyu.ttweather.model.WeatherDataFromJson;
import com.xiaoyu.ttweather.utils.DateUtils;
import com.xiaoyu.ttweather.utils.Utils;
import com.xiaoyu.ttweather.utils.WeatherDataUtils;

import java.util.List;


/**
 * 主界面ListView适配器
 * Created by xiaoy on 16/6/15.
 */
public class MainListAdapter extends BaseAdapter implements View.OnClickListener {

    private Activity mContext;
    private WeatherDataFromJson.WeatherData mWeatherData;
    private String mCityName;
    private int mMainTitleHeight;

    //信息显示区
    private LinearLayout mLlTemp;
    private ImageView mIvTempMinus;
    private ImageView mIvTempLeft;
    private ImageView mIvTempRight;
    private TextView mTvCond;
    //空气质量显示区
    private LinearLayout mLlAqi;
    private ImageView mIvAqiIcon;
    private TextView mTvAqiNum;
    private TextView mTvAqiText;
    //左下今天天气
    private RelativeLayout mRlToday;
    private TextView mTvCondToday;
    private TextView mTvTempToday;
    private TextView mTvAqiToday;
    private View mAqiColorToday;
    private ImageView mIvCondToday;
    //右下明天天气
    private RelativeLayout mRlTomorrow;
    private TextView mTvDateTomorrow;
    private TextView mTvCondTomorrow;
    private TextView mTvTempTomorrow;
    private TextView mTvAqiTomorrow;
    private View mAqiColorTomorrow;
    private ImageView mIvCondTomorrow;
    private Aqi aqi;
    private int aqiTemp;
    private List<DailyWeather> daily_forecast;
    private NowWeather now;
    private int[] aqiIcons;
    private String[] aqiTexts;
    private int[] aqiBgs;
    private int aqiPosition;
    private final Point mWindowSize;


    public MainListAdapter(Activity context, String cityName,
                           WeatherDataFromJson.WeatherData weatherData, int mMainTitleHeight) {
        mContext = context;
        mCityName = cityName;
        mWeatherData = weatherData;
        this.mMainTitleHeight = mMainTitleHeight;
        MainActivity mainUi = (MainActivity) mContext;
        mWindowSize = new Point();
        mainUi.getWindowManager().getDefaultDisplay().getSize(mWindowSize);
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LinearLayout view = (LinearLayout) View.inflate(mContext, R.layout.item_main_list, null);

        View fillView = new View(mContext);
        //填充view的高度 = 屏幕高 - view高300dp - 标题栏高 - 状态栏高
        int fillViewHeight = mWindowSize.y - Utils.dip2px(mContext, 300) - mMainTitleHeight
                - Utils.getStatusBarHeight(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, fillViewHeight);
        fillView.setLayoutParams(params);

        view.addView(fillView, 0);

        initView(view);
        initData();

        return view;
    }

    private void initData() {

        aqi = mWeatherData.aqi;
        if (aqi == null) {
            aqiTemp = 0;
        }
        //城市基本信息
        //CityInfo basic = mWeatherData.basic;
        //天气预报
        daily_forecast = mWeatherData.daily_forecast;
        //实况天气
        now = mWeatherData.now;
        //生活指数
        //DescInfo suggestion = mWeatherData.suggestion;

        initWeatherData();
    }

    /**
     * 填充页面数据
     */
    private void initWeatherData() {
        Resources res = mContext.getResources();

        //空气质量描述数组
        aqiTexts = res.getStringArray(R.array.aqi_text);
        //空气质量图标数组
        TypedArray taIcon = res.obtainTypedArray(R.array.aqi_icon);
        aqiIcons = new int[taIcon.length()];
        for (int i = 0; i < taIcon.length(); i++) {
            aqiIcons[i] = taIcon.getResourceId(i, 0);
        }
        taIcon.recycle();
        //空气质量颜色数组
        TypedArray taBg = res.obtainTypedArray(R.array.aqi_bg);
        aqiBgs = new int[taIcon.length()];
        for (int i = 0; i < taBg.length(); i++) {
            aqiBgs[i] = taBg.getResourceId(i, 0);
        }
        taBg.recycle();

        aqiPosition = WeatherDataUtils.getAqiPosition(aqi == null ? aqiTemp : aqi.city.aqi);

        //填充信息显示区数据
        initLlTemp();
        //空气质量显示区
        initAqi();
        //左下今天天气
        initToday();
        //右下明天天气
        initTomorrow();
    }

    /**
     * 填充信息显示区数据
     */
    private void initLlTemp() {

        TypedArray taNumPic = mContext.getResources().obtainTypedArray(R.array.temp_num_pic);
        int[] numPics = new int[taNumPic.length()];
        for (int i = 0; i < taNumPic.length(); i++) {
            numPics[i] = taNumPic.getResourceId(i, 0);
        }
        taNumPic.recycle();

        int tempNow = now.tmp;
        if (tempNow < 0) {
            mIvTempMinus.setVisibility(View.VISIBLE);
        } else {
            mIvTempMinus.setVisibility(View.GONE);
        }
        mIvTempLeft.setImageResource(numPics[tempNow / 10]);
        mIvTempRight.setImageResource(numPics[tempNow % 10]);
        mTvCond.setText(now.cond.txt);
        mLlTemp.setOnClickListener(this);
    }

    /**
     * 填充空气质量显示区数据
     */
    private void initAqi() {
        mIvAqiIcon.setImageResource(aqiIcons[aqiPosition]);
        mIvAqiIcon.setBackgroundResource(aqiBgs[aqiPosition]);
        mTvAqiNum.setText((aqi == null ? aqiTemp : aqi.city.aqi) + "");
        mTvAqiText.setText(aqiTexts[aqiPosition]);
        mLlAqi.setOnClickListener(this);
    }

    /**
     * 填充左下今天天气数据
     */
    private void initToday() {

        DailyWeather dailyWeather = daily_forecast.get(0);

        if (dailyWeather.cond.code_d == dailyWeather.cond.code_n) {
            mTvCondToday.setText(dailyWeather.cond.txt_d);
        } else {
            mTvCondToday.setText(dailyWeather.cond.txt_d + "转" + dailyWeather.cond.txt_n);
        }
        mTvTempToday.setText(dailyWeather.tmp.max + "°/" + dailyWeather.tmp.min + "°");
        mTvAqiToday.setText(aqiTexts[aqiPosition]);
        mAqiColorToday.setBackgroundResource(aqiBgs[aqiPosition]);
        if (DateUtils.isDay()) {
            mIvCondToday.setImageResource(WeatherDataUtils.getWeatherPic(mContext,
                    dailyWeather.cond.code_d, DateUtils.isDay()));
        } else {
            mIvCondToday.setImageResource(WeatherDataUtils.getWeatherPic(mContext,
                    dailyWeather.cond.code_n, DateUtils.isDay()));
        }
        mRlToday.setOnClickListener(this);
    }

    /**
     * 填充右下明天天气数据
     */
    private void initTomorrow() {

        DailyWeather dailyWeather = daily_forecast.get(1);

        mTvDateTomorrow.setText(DateUtils.getWeek(dailyWeather.date));

        if (dailyWeather.cond.code_d == dailyWeather.cond.code_n) {
            mTvCondTomorrow.setText(dailyWeather.cond.txt_d);
        } else {
            mTvCondTomorrow.setText(dailyWeather.cond.txt_d + "转" + dailyWeather.cond.txt_n);
        }
        mTvTempTomorrow.setText(dailyWeather.tmp.max + "°/" + dailyWeather.tmp.min + "°");
        mTvAqiTomorrow.setText(aqiTexts[aqiPosition]);
        mAqiColorTomorrow.setBackgroundResource(aqiBgs[aqiPosition]);
        if (DateUtils.isDay()) {
            mIvCondTomorrow.setImageResource(WeatherDataUtils.getWeatherPic(mContext,
                    dailyWeather.cond.code_d, DateUtils.isDay()));
        } else {
            mIvCondTomorrow.setImageResource(WeatherDataUtils.getWeatherPic(mContext,
                    dailyWeather.cond.code_n, DateUtils.isDay()));
        }
        mRlTomorrow.setOnClickListener(this);
    }

    private void initView(View view) {

        //信息显示区
        mLlTemp = (LinearLayout) view.findViewById(R.id.ll_temperature);
        mIvTempMinus = (ImageView) view.findViewById(R.id.iv_temperature_minus);
        mIvTempLeft = (ImageView) view.findViewById(R.id.iv_temperature_left);
        mIvTempRight = (ImageView) view.findViewById(R.id.iv_temperature_right);
        mTvCond = (TextView) view.findViewById(R.id.tv_cond);

        //空气质量显示区
        mLlAqi = (LinearLayout) view.findViewById(R.id.ll_aqi);
        mIvAqiIcon = (ImageView) view.findViewById(R.id.iv_aqi_icon);
        mTvAqiNum = (TextView) view.findViewById(R.id.tv_aqi_num);
        mTvAqiText = (TextView) view.findViewById(R.id.tv_aqi_text);

        //左下今天天气
        mRlToday = (RelativeLayout) view.findViewById(R.id.rl_today);
        mTvCondToday = (TextView) view.findViewById(R.id.tv_cond_today);
        mTvTempToday = (TextView) view.findViewById(R.id.tv_temperature_today);
        mTvAqiToday = (TextView) view.findViewById(R.id.tv_aqi_text_today);
        mAqiColorToday = view.findViewById(R.id.v_aqi_color_today);
        mIvCondToday = (ImageView) view.findViewById(R.id.iv_cond_today);

        //右下明天天气
        mRlTomorrow = (RelativeLayout) view.findViewById(R.id.rl_tomorrow);
        mTvDateTomorrow = (TextView) view.findViewById(R.id.tv_date_tomorrow);
        mTvCondTomorrow = (TextView) view.findViewById(R.id.tv_cond_tomorrow);
        mTvTempTomorrow = (TextView) view.findViewById(R.id.tv_temperature_tomorrow);
        mTvAqiTomorrow = (TextView) view.findViewById(R.id.tv_aqi_text_tomorrow);
        mAqiColorTomorrow = view.findViewById(R.id.v_aqi_color_tomorrow);
        mIvCondTomorrow = (ImageView) view.findViewById(R.id.iv_cond_tomorrow);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //左下今天天气
            case R.id.rl_today:
                startActivityForBlur(0);
                break;
            //右下明天天气
            case R.id.rl_tomorrow:
                startActivityForBlur(1);
                break;
        }
    }

    /**
     * 进入天气详情页面设置背景模糊透明
     *
     * @param position 第几个页面
     */
    private void startActivityForBlur(final int position) {
        BlurBehind.getInstance().execute(mContext, new OnBlurCompleteListener() {
            @Override
            public void onBlurComplete() {
                Intent intent = new Intent(mContext, WeatherDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("position", position);
                intent.putExtra("city_name", mCityName);
                mContext.startActivity(intent);
            }
        });
    }
}
