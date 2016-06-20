package com.xiaoyu.ttweather.model;

/**
 * DescInfo
 * Created by xiaoy on 16/6/8.
 */
public class DescInfo {
    public Desc comf;   //舒适指数
    public Desc cw;     //洗车指数
    public Desc drsg;   //穿衣指数
    public Desc flu;    //感冒指数
    public Desc sport;  //运动指数
    public Desc trav;   //旅游指数
    public Desc uv;     //紫外线指数

    public class Desc {
        public String brf;  //简介
        public String txt;  //详情
    }
}
