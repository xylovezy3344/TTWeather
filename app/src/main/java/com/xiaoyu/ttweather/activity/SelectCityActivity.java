package com.xiaoyu.ttweather.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.Address;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.xiaoyu.ttweather.R;
import com.xiaoyu.ttweather.db.WeatherDB;
import com.xiaoyu.ttweather.global.GlobalUrl;
import com.xiaoyu.ttweather.model.CityModel;
import com.xiaoyu.ttweather.utils.SharedPrefUtils;
import com.xiaoyu.ttweather.utils.Utils;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class SelectCityActivity extends AppCompatActivity implements View.OnClickListener {


    private ImageButton mIbBack;
    private RelativeLayout mRlSearch;
    private RelativeLayout mRlGps;
    private GridView mGvHotCities;
    private TextView mTvMore;

    private String[] hotCities = new String[]{"北京", "上海", "广州", "深圳", "武汉", "西安",
            "南京", "成都", "杭州", "郑州", "重庆", "沈阳", "大连", "哈尔滨", "长春", "苏州"};
    private LocationClient mLocationClient;

    private boolean isFirst;
    private String selectCities;
    private ProgressDialog dialog;
    private WeatherDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);

        //如果没有选择过城市，直接定位选城市
        if (SharedPrefUtils.getInt(this, "select_city_num", 0) == 0) {
            startLocation();
        }

        initView();
        initData();
    }

    private void initView() {
        mIbBack = (ImageButton) findViewById(R.id.ib_back);
        mRlSearch = (RelativeLayout) findViewById(R.id.rl_search);
        mRlGps = (RelativeLayout) findViewById(R.id.rl_gps);
        mGvHotCities = (GridView) findViewById(R.id.gv_hot_city);
        mTvMore = (TextView) findViewById(R.id.tv_more_cities);
    }

    private void initData() {

        selectCities = SharedPrefUtils.getString(this, "select_cities", "");
        //selectCities为空说明第一次进应用
        isFirst = TextUtils.isEmpty(selectCities);

        db = WeatherDB.getInstance(this);

        mIbBack.setOnClickListener(this);
        mRlSearch.setOnClickListener(this);
        mRlGps.setOnClickListener(this);
        mTvMore.setOnClickListener(this);

        HotCitiesAdapter adapter = new HotCitiesAdapter();
        mGvHotCities.setAdapter(adapter);
        mGvHotCities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView textView = (TextView) view;

                //如果已经选择过，不能点击
                if (!selectCities.contains(hotCities[position])) {
                    SharedPrefUtils.putSelectCity(SelectCityActivity.this, hotCities[position], false);
                    getDataFromServer(hotCities[position]);
                }
            }
        });

        //判断是否添加过定位城市，如果添加过，隐藏mRlGps条目
        if (!TextUtils.isEmpty(SharedPrefUtils.getString(this, "location_city", ""))) {
            mRlGps.setVisibility(View.GONE);
        }
    }

    private void getDataFromServer(final String cityName) {
        showProgressDialog();
        final CityModel cityInfo = db.getCityInfo(cityName);
        String uri = GlobalUrl.GET_WEATHER_DATA_URL + cityInfo.id + GlobalUrl.KEY;
        RequestParams params = new RequestParams(uri);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                SharedPrefUtils.putString(SelectCityActivity.this, cityName, result);
                dialog.dismiss();
                jumpNextActivity();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("SelectCityActivity:", ex + "");
                dialog.dismiss();
                Utils.showToast(SelectCityActivity.this, "网络异常");
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

    private void showProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("加载数据...");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                finish();
                break;
            case R.id.rl_search:
                break;
            case R.id.rl_gps:
                startLocation();
                break;
            case R.id.tv_more_cities:
                startActivity(new Intent(SelectCityActivity.this, ProvinceListActivity.class));
                break;
        }

    }

    /**
     * 开始定位
     */
    private void startLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mLocationClient = new LocationClient(getApplicationContext(), option);
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                Address address = location.getAddress();
                String city = location.getAddress().city;
                String time = location.getTime();

                showLocationDialog(city);

                mLocationClient.stop();

                //Receive Location
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                sb.append(location.getTime());
                sb.append("\nerror code : ");
                sb.append(location.getLocType());
                sb.append("\nlatitude : ");
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");
                sb.append(location.getLongitude());
                sb.append("\nradius : ");
                sb.append(location.getRadius());
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 单位：公里每小时
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 单位：米
                    sb.append("\ndirection : ");
                    sb.append(location.getDirection());// 单位度
                    sb.append("\naddr : ");
                    sb.append(location.getAddrStr());
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");

                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    sb.append("\naddr : ");
                    sb.append(location.getAddrStr());
                    //运营商信息
                    sb.append("\noperationers : ");
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
                sb.append("\nlocationdescribe : ");
                sb.append(location.getLocationDescribe());// 位置语义化信息
                List<Poi> list = location.getPoiList();// POI数据
                if (list != null) {
                    sb.append("\npoilist size = : ");
                    sb.append(list.size());
                    for (Poi p : list) {
                        sb.append("\npoi= : ");
                        sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                    }
                }

                Log.e("BaiduLocationApiDem", sb.toString());
            }
        });
        mLocationClient.start();
    }

    /**
     * 显示添加定位城市对话框
     *
     * @param city 城市名
     */
    private void showLocationDialog(final String city) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("定位提示");
        builder.setIcon(R.drawable.cityselector_ic_locate_dialog_gps);
        builder.setMessage("您当前位于" + city + "，请确定是否添加？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String cityName = WeatherDB.getInstance(SelectCityActivity.this).handleCityData(city);
                SharedPrefUtils.putSelectCity(SelectCityActivity.this, cityName, true);
                getDataFromServer(cityName);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void jumpNextActivity() {

        //select_city_num值为0，说明第一次打开应用
        if (isFirst) {
            //判断是否展示过新手引导页，如果展示过直接跳进主天气页面，否则跳进新手引导页
            if (SharedPrefUtils.getBoolean(SelectCityActivity.this,
                    "has_run_guide", false)) {
                startActivity(new Intent(SelectCityActivity.this, MainActivity.class));
            } else {
                //取消GuideActivity
                //startActivity(new Intent(SelectCityActivity.this, GuideActivity.class));
                startActivity(new Intent(SelectCityActivity.this, MainActivity.class));
            }
        }
        finish();
    }

    private class HotCitiesAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return hotCities.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(SelectCityActivity.this);
            textView.setText(hotCities[position]);
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundColor(Color.WHITE);
            textView.setPadding(0, DensityUtil.dip2px(15), 0, DensityUtil.dip2px(15));
            textView.setTextSize(16);

            //如果已经选择过，不能点击，字体设灰色
            if (selectCities.contains(hotCities[position])) {
                textView.setTextColor(Color.GRAY);
            } else {
                textView.setTextColor(Color.BLACK);
            }

            return textView;
        }
    }
}
