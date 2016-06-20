package com.xiaoyu.ttweather.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.xiaoyu.ttweather.model.CityModel;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 数据库操作类
 * Created by xiaoy on 16/6/1.
 */
public class WeatherDB {
    private static WeatherDB weatherDB;
    private final SQLiteDatabase db;
    private static final String DB_NAME = "city.db";


    /**
     * 构造方法私有化
     */
    private WeatherDB(Context context) {
        SQLiteOpenHelper helper = new SQLiteOpenHelper(context, DB_NAME, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        db = helper.getWritableDatabase();
    }

    /**
     * 获取实例
     */
    public static WeatherDB getInstance(Context context) {
        if (weatherDB == null) {
            weatherDB = new WeatherDB(context);
        }
        return weatherDB;
    }

    /**
     * 保存城市信息
     */
    public void saveCities(List<CityModel> cityList) {
        for (CityModel city : cityList) {
            db.execSQL("insert into city values(null,?,?,?)",
                    new String[]{city.city, city.id, city.prov});
        }
    }

    /**
     * 获取所有省份信息
     */
    public List<String> getProvinces() {
        List<String> provinceList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select province from city", null);
        while (cursor.moveToNext()) {
            String province = cursor.getString(cursor.getColumnIndex("province"));
            provinceList.add(province);
        }
        cursor.close();
        return removeDuplicate(provinceList);
    }

    /**
     * 根据省份名称查找所有该省份下城市信息
     *
     * @param province 省份名称
     * @return 该省份下城市信息集合
     */
    public List<CityModel> getCities(String province) {
        List<CityModel> cityList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from city where province like ?",
                new String[]{province});
        while (cursor.moveToNext()) {
            CityModel city = new CityModel();
            city.city = cursor.getString(cursor.getColumnIndex("name"));
            city.id = cursor.getString(cursor.getColumnIndex("city_id"));
            city.prov = cursor.getString(cursor.getColumnIndex("province"));
            cityList.add(city);
        }
        cursor.close();
        return cityList;
    }

    /**
     * List集合去除重复数据
     *
     * @param list
     * @return
     */
    private List<String> removeDuplicate(List<String> list) {
        Set set = new LinkedHashSet<String>();
        set.addAll(list);
        list.clear();
        list.addAll(set);
        return list;
    }

    /**
     * 处理城市数据，定位方法返回的数据是XX市，而数据库中数据只是XX，没有市，所有要处理
     *
     * @param cityBefore 传入的定位方法返回的数据城市名（XX市）
     * @return 处理后去掉"市"的城市名
     */
    public String handleCityData(String cityBefore) {


        Cursor cursor = db.rawQuery("select name from city", null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            if (cityBefore.contains(name)) {
                return name;
            }
        }
        cursor.close();
        return "没有";
    }

    public CityModel getCityInfo(String cityName) {
        CityModel cityModel = new CityModel();
        Cursor cursor = db.rawQuery("select * from city where name like ?",
                new String[]{cityName});
        if (cursor.moveToNext()) {
            String cityId = cursor.getString(cursor.getColumnIndex("city_id"));
            String province = cursor.getString(cursor.getColumnIndex("province"));

            cityModel.city = cityName;
            cityModel.id = cityId;
            cityModel.prov = province;
        }
        cursor.close();
        return cityModel;
    }
}
