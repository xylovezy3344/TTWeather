package com.xiaoyu.ttweather.model;

/**
 * NowWeather
 * Created by xiaoy on 16/6/8.
 */
public class NowWeather {
    public Cond cond;   //天气状况
    public String fl;   //体感温度
    public String hum;  //湿度(%)
    public String pcpn; //降雨量(mm)
    public String pres; //气压
    public int tmp;  //当前温度(摄氏度)
    public String vis;  //能见度(km)
    public Wind wind;   //风力状况

    public class Cond {
        public int code; //天气代码
        public String txt;  //天气描述
    }
    public class Wind {
        public String deg;  //风向(角度)
        public String dir;  //风向(方向)
        public String sc;   //风力等级
        public String spd;  //风速(Kmph)
    }
}
