package com.xiaoyu.ttweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xiaoyu.ttweather.R;
import com.xiaoyu.ttweather.db.WeatherDB;
import com.xiaoyu.ttweather.global.GlobalUrl;
import com.xiaoyu.ttweather.model.CityModel;
import com.xiaoyu.ttweather.service.AutoUpdateService;
import com.xiaoyu.ttweather.utils.SharedPrefUtils;
import com.xiaoyu.ttweather.utils.Utils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class SplashActivity extends AppCompatActivity {

    private RelativeLayout mRlSpalsh;
    private AlphaAnimation animation;
    //动画结束标记
    private boolean isAnimationFinish;
    //网络获取数据结束标记
    private boolean isServerFinish;

    private String[] cityNames;
    private String selectCities;
    private int cityNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.Ext.init(getApplication());
        setContentView(R.layout.activity_splash);
        initView();
        initData();
    }

    private void initView() {
        mRlSpalsh = (RelativeLayout) findViewById(R.id.rl_splash);
    }

    private void initData() {
        startAlphaAnimation();

        //拷贝资产目录下的数据库文件
        copyDB("city.db");

        if (SharedPrefUtils.getBoolean(this, "isAutoUpdate", true)) {
            startService(new Intent(this, AutoUpdateService.class));
        }

        selectCities = SharedPrefUtils.getString(this, "select_cities", "");
        //selectCities为空说明第一次进应用
        if (!TextUtils.isEmpty(selectCities)) {

            WeatherDB db = WeatherDB.getInstance(this);

            long currentTime = System.currentTimeMillis();
            String lastUpdateTime = SharedPrefUtils.getString(this, "last_update_time", "0");
            long updateInterval = 600000;
            //当前时间减去最后一次更新时间
            //大于等于用户设置的更新间隔时间，需要从网络更新
            if (currentTime - Long.parseLong(lastUpdateTime) >= updateInterval) {
                //从网络获取数据
                cityNames = selectCities.split(",");
                for (String cityName : cityNames) {
                    CityModel cityInfo = db.getCityInfo(cityName);
                    getDataFromServer(cityInfo);
                }
            } else {
                Log.e("tag", "不需要更新");
                isServerFinish = true;
                jumpNextActivity();
            }
        } else {
            Log.e("tag", "没有选择城市");
            isServerFinish = true;
            jumpNextActivity();
        }

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.e("tag", "onAnimationEnd");
                isAnimationFinish = true;
                jumpNextActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    /**
     * //拷贝资产目录下的数据库文件到应用databases目录
     *
     * @param dbName 数据库文件名称
     */

    private void copyDB(final String dbName) {
        new Thread() {
            public void run() {
                try {
                    //创建databases文件夹
                    String DATABASES_DIR = "/data/data/com.xiaoyu.ttweather/databases/";
                    File fileDir = new File(DATABASES_DIR);
                    if (!fileDir.exists()) {
                        boolean mkdirs = fileDir.mkdirs();
                    }
                    //复制city.db
                    File file = new File(fileDir, "city.db");
                    boolean exists = file.exists();
                    if (file.exists() && file.length() > 0) {
                        Log.e("tag", "数据库是存在的。无需拷贝！");
                        return;
                    }
                    InputStream is = getAssets().open(dbName);
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    is.close();
                    fos.close();
                    Log.e("tag", "数据库拷贝完成！");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void startAlphaAnimation() {
        animation = new AlphaAnimation(0.5f, 1);
        animation.setDuration(500);
        animation.setFillAfter(true);
        mRlSpalsh.setAnimation(animation);
    }

    private void getDataFromServer(final CityModel cityInfo) {

        String uri = GlobalUrl.GET_WEATHER_DATA_URL + cityInfo.id + GlobalUrl.KEY;
        RequestParams params = new RequestParams(uri);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                SharedPrefUtils.putString(SplashActivity.this, cityInfo.city, result);
                cityNum++;
                if (cityNum == cityNames.length) {
                    Log.e("tag", "从网络获取完成");
                    long currentTime = System.currentTimeMillis();
                    SharedPrefUtils.putString(SplashActivity.this, "last_update_time", currentTime + "");
                    isServerFinish = true;
                    jumpNextActivity();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("SplashActivity:", ex + "");
                Utils.showToast(SplashActivity.this, "网络异常");
                isServerFinish = true;
                jumpNextActivity();
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

    private void jumpNextActivity() {

        if (isServerFinish && isAnimationFinish) {
            if (!TextUtils.isEmpty(selectCities)) {
                startActivity(new Intent(this, MainActivity.class));
            } else {
                startActivity(new Intent(this, SelectCityActivity.class));
            }
            finish();
        }
    }
}
