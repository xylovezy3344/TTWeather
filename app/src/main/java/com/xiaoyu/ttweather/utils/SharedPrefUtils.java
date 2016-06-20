package com.xiaoyu.ttweather.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.xiaoyu.ttweather.db.WeatherDB;

/**
 * SharedPrefUtils
 * Created by xiaoy on 16/6/1.
 */
public class SharedPrefUtils {
    public static boolean getBoolean(Context context, String key, boolean defValue) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }

    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).apply();
    }

    public static String getString(Context context, String key, String defValue) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getString(key, defValue);
    }

    public static void putString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    public static int getInt(Context context, String key, int defValue) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getInt(key, defValue);
    }

    public static void putInt(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        sp.edit().putInt(key, value).apply();
    }

    public static void putSelectCity(Context context, String city, boolean isLocation) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        int selectCityNum = sp.getInt("select_city_num", 0);
        //String key = "select_city_" + (selectCityNum + 1);

        String selectCities = sp.getString("select_cities", "");
        selectCities = selectCities + city + ",";
        sp.edit().putString("select_cities", selectCities).apply();

        //如果是定位的城市，记录位置
        if (isLocation) {
            sp.edit().putString("location_city", city).apply();
        }

        //sp.edit().putString(key, city).apply();
        sp.edit().putInt("select_city_num", (selectCityNum + 1)).apply();
    }
}
