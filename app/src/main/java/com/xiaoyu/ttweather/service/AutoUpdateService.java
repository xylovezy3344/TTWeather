package com.xiaoyu.ttweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.xiaoyu.ttweather.activity.SplashActivity;
import com.xiaoyu.ttweather.db.WeatherDB;
import com.xiaoyu.ttweather.global.GlobalUrl;
import com.xiaoyu.ttweather.model.CityModel;
import com.xiaoyu.ttweather.receiver.AlarmReceiver;
import com.xiaoyu.ttweather.utils.SharedPrefUtils;
import com.xiaoyu.ttweather.utils.Utils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class AutoUpdateService extends Service {

    private AlarmManager manager;
    private PendingIntent pi;
    private String[] cityNames;
    private int getDataTime;

    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                UpdateGlobalData();
            }
        }).start();

        manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int updateInterval = SharedPrefUtils.getInt(this, "update_interval", 1);
        long triggerAtTime = System.currentTimeMillis() + updateInterval * 1000 * 3600;
        Intent i = new Intent(this, AlarmReceiver.class);
        pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pi);

        return super.onStartCommand(intent, flags, startId);
    }

    private void UpdateGlobalData() {
        getDataTime = 0;
        WeatherDB db = WeatherDB.getInstance(this);
        String selectCities = SharedPrefUtils.getString(this, "select_cities", "");
        cityNames = selectCities.split(",");
        for (String cityName : cityNames) {
            CityModel cityInfo = db.getCityInfo(cityName);
            getDataFromServer(cityInfo);
        }
    }

    private void getDataFromServer(final CityModel cityInfo) {

        String uri = GlobalUrl.GET_WEATHER_DATA_URL + cityInfo.id + GlobalUrl.KEY;
        RequestParams params = new RequestParams(uri);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                SharedPrefUtils.putString(AutoUpdateService.this, cityInfo.city, result);
                getDataTime++;
                if (getDataTime == cityNames.length) {
                    Log.e("tag", "从网络获取完成");
                    long currentTime = System.currentTimeMillis();
                    SharedPrefUtils.putString(AutoUpdateService.this, "last_update_time", currentTime + "");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("SplashActivity:", ex + "");
                Log.e("tag", "网络异常");
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e("tag", "onCancelled：" + cex);
            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        manager.cancel(pi);
    }
}
