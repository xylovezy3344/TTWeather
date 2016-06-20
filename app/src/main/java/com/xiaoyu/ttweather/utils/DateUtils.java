package com.xiaoyu.ttweather.utils;

import java.util.Calendar;

/**
 * 时间工具类
 * Created by xiaoy on 16/6/9.
 */
public class DateUtils {

    /**
     * 根据日期返回星期几
     *
     * @param date 日期（服务器返回数据，格式 xxxx-xx-xx）
     * @return 星期几
     */
    public static String getWeek(String date) {

        String[] dates = date.split("\\-");
        int year = Integer.valueOf(dates[0]);
        int month = Integer.valueOf(dates[1]);
        int day = Integer.valueOf(dates[2]);

        /*如何计算某一天是星期几?
        基姆拉尔森计算公式
        Week=(Day + 2*Month + 3*(Month+1）/5 + Year + Year/4 - Year/100 + Year/400) % 7
        （其中的Year是4位数的，如2009。“%”号是等式除7取余数）
        注意：
        i. 该公式中要把1月和2月分别当成上一年的13月和14月处理。
        例如：2008年1月4日要换成 2007年13月4日带入公式。
        ii.该式对应的与蔡勒公式有点区别：“0”为星期1，……，“6”为星期日。*/

        if (month == 1) {
            month = 13;
            year--;
        } else if (month == 2) {
            month = 14;
            year--;
        }

        int week=(day + 2*month + 3*(month+1)/5 + year + year/4 - year/100 + year/400) % 7;
        String weekstr = null;
        switch(week)
        {
            case 0: weekstr="周一"; break;
            case 1: weekstr="周二"; break;
            case 2: weekstr="周三"; break;
            case 3: weekstr="周四"; break;
            case 4: weekstr="周五"; break;
            case 5: weekstr="周六"; break;
            case 6: weekstr="周日"; break;
        }
        return weekstr;
    }

    public static boolean isDay() {
        Calendar ca = Calendar.getInstance();
//        int year = ca.get(Calendar.YEAR);//获取年份
//        int month=ca.get(Calendar.MONTH);//获取月份
//        int day=ca.get(Calendar.DATE);//获取日
//        int WeekOfYear = ca.get(Calendar.DAY_OF_WEEK);
        int hour=ca.get(Calendar.HOUR_OF_DAY);//小时
//        int minute=ca.get(Calendar.MINUTE);//分
//        int second=ca.get(Calendar.SECOND);//秒

        return 6 <= hour && hour < 18;
    }
}
