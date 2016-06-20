package com.xiaoyu.ttweather.model;

/**
 * DailyWeather
 * Created by xiaoy on 16/6/8.
 */
public class DailyWeather {
    public Astro astro; //天文数值
    public Cond cond;   //天气状况
    public String date; //当地日期
    public String hum;  //湿度(%)
    public String pcpn; //降雨量(mm)
    public String pop;  //降水概率
    public String pres; //气压
    public Tmp tmp;     //温度
    public String vis;  //能见度(km)
    public Wind wind;   //风力状况

    public class Astro {
        public String sr;   //日出时间
        public String ss;   //日落时间
    }
    public class Cond {
        public int code_d;   //白天天气代码
        public int code_n;   //夜间天气代码
        public String txt_d;    //白天天气描述
        public String txt_n;    //夜间天气描述
    }
    public class Tmp {
        public int max;  //最高温度(摄氏度)
        public int min;  //最低温度(摄氏度)
    }

    public class Wind {
        public String deg;  //风向(角度)
        public String dir;  //风向(方向)
        public String sc;   //风力等级
        public String spd;  //风速(Kmph)
    }
}
