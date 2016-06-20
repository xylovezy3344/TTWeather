package com.xiaoyu.ttweather.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * 天气数据模型类
 * Created by xiaoy on 16/6/8.
 */
public class WeatherDataFromJsonNoAqi {

    @SerializedName("HeWeather data service 3.0")
    public ArrayList<WeatherDataNoAqi> HeWeatherDataListNoAqi;

    public class WeatherDataNoAqi {
        public CityInfo basic;                      //城市基本信息
        public List<DailyWeather> daily_forecast;   //天气预报
        public NowWeather now;                      //实况天气
        public DescInfo suggestion;                 //生活指数
    }
}



