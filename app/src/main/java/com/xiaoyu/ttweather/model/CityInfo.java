package com.xiaoyu.ttweather.model;

/**
 * CityInfo
 * Created by xiaoy on 16/6/8.
 */
public class CityInfo {
    public String city;     //城市名称
    public String cnty;     //国家名称
    public String id;       //城市ID
    public String lat;      //纬度
    public String lon;      //经度
    public Update update;   //数据更新时间,24小时制

    public class Update {
        public String loc;  //数据更新的当地时间
        public String utc;  //数据更新的UTC时间
    }
}
