package com.xiaoyu.ttweather.utils;

import android.content.Context;
import android.content.res.TypedArray;

import com.xiaoyu.ttweather.R;

/**
 *
 * Created by xiaoy on 16/6/10.
 */
public class WeatherDataUtils {
    /**
     * 获取空气质量级别
     *
     * @param aqiNow 空气质量
     * @return 级别
     */
    public static int getAqiPosition(int aqiNow) {
        if (0 <= aqiNow && aqiNow < 51) {
            return 0;
        } else if (51 <= aqiNow && aqiNow < 101) {
            return 1;
        } else if (101 <= aqiNow && aqiNow < 151) {
            return 2;
        } else if (151 <= aqiNow && aqiNow < 201) {
            return 3;
        } else if (201 <= aqiNow && aqiNow < 251) {
            return 4;
        } else if (251 <= aqiNow && aqiNow < 301) {
            return 5;
        } else if (301 <= aqiNow) {
            return 6;
        }
        return 0;
    }

    /**
     * 获取天气信息图片
     *
     * @param context
     * @param condCode
     * @return
     */
    public static int getWeatherPic(Context context, int condCode, boolean isDay) {
        //天气图片数组
        TypedArray taWeather = context.getResources().obtainTypedArray(R.array.weather_pic);
        int[] weatherPics = new int[taWeather.length()];
        for (int i = 0; i < taWeather.length(); i++) {
            weatherPics[i] = taWeather.getResourceId(i, 0);
        }
        taWeather.recycle();

        switch (condCode) {
            case 100:
                return isDay ? weatherPics[0] : weatherPics[1];
            case 101:
            case 102:
            case 103:
                return isDay ? weatherPics[2] : weatherPics[3];
            case 104:
                return weatherPics[4];
            case 300:
            case 301:
                return weatherPics[5];
            case 302:
            case 303:
                return weatherPics[6];
            case 304:
                return weatherPics[7];
            case 305:
            case 306:
            case 309:
                return weatherPics[8];
            case 307:
            case 308:
            case 310:
            case 311:
            case 312:
                return weatherPics[9];
            case 400:
            case 401:
            case 407:
                return weatherPics[10];
            case 402:
            case 403:
                return weatherPics[11];
            case 404:
            case 405:
            case 406:
                return weatherPics[12];
            case 500:
            case 501:
                return weatherPics[13];
            case 502:
                return weatherPics[14];
            case 503:
            case 504:
            case 505:
            case 506:
            case 507:
            case 508:
                return weatherPics[15];
            default:
                return weatherPics[0];
        }
    }
}
