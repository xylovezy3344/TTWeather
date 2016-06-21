package com.xiaoyu.ttweather.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xiaoyu.ttweather.R;
import com.xiaoyu.ttweather.adapter.SelectCityAdapter;
import com.xiaoyu.ttweather.db.WeatherDB;
import com.xiaoyu.ttweather.global.GlobalUrl;
import com.xiaoyu.ttweather.model.Aqi;
import com.xiaoyu.ttweather.model.CityInfo;
import com.xiaoyu.ttweather.model.CityModel;
import com.xiaoyu.ttweather.model.DailyWeather;
import com.xiaoyu.ttweather.model.DescInfo;
import com.xiaoyu.ttweather.model.NowWeather;
import com.xiaoyu.ttweather.model.SelectCity;
import com.xiaoyu.ttweather.model.WeatherDataFromJson;
import com.xiaoyu.ttweather.utils.DateUtils;
import com.xiaoyu.ttweather.utils.SharedPrefUtils;
import com.xiaoyu.ttweather.utils.Utils;
import com.xiaoyu.ttweather.utils.WeatherDataUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class CityManageActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton mIbBack;
    private ImageButton mIbRight;
    private ImageButton mIbRefresh;
    private GridView mGvSelectCity;

    private ProgressDialog dialog;
    private List<SelectCity> selectCityList;
    private SelectCityAdapter adapter;

    private boolean isSetting;

    private int SELECT_CITY_REQUEST_CODE = 0x00;
    private String[] cityNames;
    private WeatherDB db;

    private int parseTime;
    private int serverTime;
    private RelativeLayout mRlTitle;
    private int mGridViewHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_manage);

        initView();
        initData();
    }

    private void initView() {
        mRlTitle = (RelativeLayout) findViewById(R.id.rl_title);
        mIbBack = (ImageButton) findViewById(R.id.ib_back);
        mIbRight = (ImageButton) findViewById(R.id.ib_right);
        mIbRefresh = (ImageButton) findViewById(R.id.ib_refresh);
        mGvSelectCity = (GridView) findViewById(R.id.gv_select_city);
    }

    private void initData() {

        isSetting = true;
        db = WeatherDB.getInstance(this);

        mIbBack.setOnClickListener(this);
        mIbRefresh.setOnClickListener(this);
        mIbRight.setOnClickListener(this);

        //屏幕尺寸
        Point windowSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(windowSize);
        //标题栏高度
        mRlTitle.measure(0, 0);
        int titleHeight = mRlTitle.getMeasuredHeight();
        //状态栏高度
        int statusBarHeight = Utils.getStatusBarHeight(this);
        //mGvSelectCity的总高度 pading值还有10
        mGridViewHeight = windowSize.y - titleHeight - statusBarHeight - Utils.dip2px(this, 10);

        //城市数据集合
        selectCityList = new ArrayList<>();

        String selectCities = SharedPrefUtils.getString(this, "select_cities", "");
        cityNames = selectCities.split(",");

        parseTime = 0;
        //从文件缓存中获取数据
        for (String cityName : cityNames) {
            String result = SharedPrefUtils.getString(this, cityName, "");
            if (TextUtils.isEmpty(result)) {
                getDataFromServer(cityName);
            } else {
                parseData(cityName, result);
            }
        }

        mGvSelectCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isSetting) {
                    if (position != 9 && position == selectCityList.size()) {
                        startActivityForResult(new Intent(CityManageActivity.this,
                                SelectCityActivity.class), SELECT_CITY_REQUEST_CODE);
                    } else {
                        if (position != 0) {
                            String selectCities = SharedPrefUtils.getString(CityManageActivity.this,
                                    "select_cities", "");
                            String replace = selectCities.replace(selectCityList.get(position).title + ",", "");
                            selectCities = selectCityList.get(position).title + "," + replace;
                            SharedPrefUtils.putString(CityManageActivity.this, "select_cities",
                                    selectCities);
                        }
                        startActivity(new Intent(CityManageActivity.this, MainActivity.class));
                    }
                }
            }
        });

        mGvSelectCity.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (isSetting) {
                    isSetting = false;
                    mIbRight.setImageResource(R.drawable.city_manage_right_selector);
                    adapter.notifyDataSetChanged(false);
                } else {
                    isSetting = true;
                    mIbRight.setImageResource(R.drawable.city_manage_right_setting_selector);
                    adapter.notifyDataSetChanged(true);
                }
                return true;
            }
        });
    }

    private void parseData(String cityName, String result) {

        parseTime++;

        SelectCity selectCity = new SelectCity();
        selectCity.title = cityName;

        if (!TextUtils.isEmpty(result)) {
            Gson gson = new Gson();
            WeatherDataFromJson dataFromJson = gson.fromJson(result, WeatherDataFromJson.class);

            //天气预报
            List<DailyWeather> daily_forecast = dataFromJson.HeWeatherDataList.get(0).daily_forecast;

            if (DateUtils.isDay()) {
                selectCity.pic = WeatherDataUtils.getWeatherPic(this, daily_forecast
                        .get(0).cond.code_d, true);
            } else {
                selectCity.pic = WeatherDataUtils.getWeatherPic(this, daily_forecast
                        .get(0).cond.code_n, false);
            }
            selectCity.highTemp = daily_forecast.get(0).tmp.max + "°";
            selectCity.lowTemp = daily_forecast.get(0).tmp.min + "°";
            if (daily_forecast.get(0).cond.code_d == daily_forecast.get(0).cond.code_n) {
                selectCity.cond = daily_forecast.get(0).cond.txt_d;
            } else {
                selectCity.cond = daily_forecast.get(0).cond.txt_d + "转" + daily_forecast
                        .get(0).cond.txt_n;
            }
        }

        selectCityList.add(selectCity);

        if (selectCityList.size() == cityNames.length) {
            //排序
            List<SelectCity> temp = new ArrayList<>();
            for (String name : cityNames) {
                for (SelectCity city : selectCityList) {
                    if (city.title.equals(name)) {
                        temp.add(city);
                    }
                }
            }

            selectCityList.clear();
            selectCityList = temp;
        }

        if (parseTime == cityNames.length) {
            adapter = new SelectCityAdapter(this, selectCityList, mGridViewHeight);
            mGvSelectCity.setAdapter(adapter);
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private void showProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("正在加载，请稍候...");
        dialog.setCancelable(false);
        dialog.show();
    }

    private void getDataFromServer(final String cityName) {
        CityModel cityInfo = db.getCityInfo(cityName);
        String uri = GlobalUrl.GET_WEATHER_DATA_URL + cityInfo.id + GlobalUrl.KEY;
        RequestParams params = new RequestParams(uri);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                serverTime++;
                SharedPrefUtils.putString(CityManageActivity.this, cityName, result);
                parseData(cityName, result);

                //刷新完成,更新刷新时间
                if (serverTime == cityNames.length) {
                    long currentTime = System.currentTimeMillis();
                    SharedPrefUtils.putString(CityManageActivity.this, "last_update_time", currentTime + "");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("CityManageActivity:", ex + "");
                Utils.showToast(CityManageActivity.this, "网络异常");
                String result = SharedPrefUtils.getString(CityManageActivity.this, cityName, "");
                parseData(cityName, result);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                finish();
                break;
            case R.id.ib_right:
                if (isSetting) {
                    isSetting = false;
                    mIbRight.setImageResource(R.drawable.city_manage_right_selector);
                    adapter.notifyDataSetChanged(false);
                } else {
                    isSetting = true;
                    mIbRight.setImageResource(R.drawable.city_manage_right_setting_selector);
                    adapter.notifyDataSetChanged(true);
                }
                break;
            case R.id.ib_refresh:
                showProgressDialog();
                serverTime = 0;
                parseTime = 0;
                String selectCities = SharedPrefUtils.getString(this, "select_cities", "");
                cityNames = selectCities.split(",");
                selectCityList.clear();
                for (String cityName : cityNames) {
                    getDataFromServer(cityName);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (isSetting) {
            super.onBackPressed();
        } else {
            isSetting = true;
            mIbRight.setImageResource(R.drawable.city_manage_right_setting_selector);
            adapter.notifyDataSetChanged(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_CITY_REQUEST_CODE) {
            initData();
        }
    }
}
