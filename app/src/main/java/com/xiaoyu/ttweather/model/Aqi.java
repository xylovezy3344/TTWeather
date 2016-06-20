package com.xiaoyu.ttweather.model;

/**
 * Aqi
 * Created by xiaoy on 16/6/8.
 */
public class Aqi {
    public AqiData city;    //城市数据

    public class AqiData {
        public int aqi;  //空气质量指数
        public String co;   //一氧化碳1小时平均值(ug/m³)
        public String no2;  //二氧化氮1小时平均值(ug/m³)
        public String o3;   //臭氧1小时平均值(ug/m³)
        public String pm10; //PM10 1小时平均值(ug/m³)
        public String pm25; //PM2.5 1小时平均值(ug/m³)
        public String qlty; //空气质量类别
        public String so2;  //二氧化硫1小时平均值(ug/m³)
    }
}
