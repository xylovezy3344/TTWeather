package com.xiaoyu.ttweather.pager;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ScrollView;

import com.google.gson.Gson;
import com.xiaoyu.ttweather.R;
import com.xiaoyu.ttweather.activity.MainActivity;
import com.xiaoyu.ttweather.activity.WeatherDetailActivity;
import com.xiaoyu.ttweather.adapter.FiveDaysWeatherAdapter;
import com.xiaoyu.ttweather.adapter.MainListAdapter;
import com.xiaoyu.ttweather.adapter.SuggestionAdapter;
import com.xiaoyu.ttweather.blur.BlurBehind;
import com.xiaoyu.ttweather.blur.OnBlurCompleteListener;
import com.xiaoyu.ttweather.db.WeatherDB;
import com.xiaoyu.ttweather.global.GlobalUrl;
import com.xiaoyu.ttweather.model.Aqi;
import com.xiaoyu.ttweather.model.DailyWeather;
import com.xiaoyu.ttweather.model.DescInfo;
import com.xiaoyu.ttweather.model.WeatherDataFromJson;
import com.xiaoyu.ttweather.utils.SharedPrefUtils;
import com.xiaoyu.ttweather.utils.Utils;
import com.xiaoyu.ttweather.view.MainListView;
import com.xiaoyu.ttweather.view.MyDragLayout;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by xiaoy on 16/6/16.
 */
public class MainPager {

    private Activity mActivity;
    public View mRootView;
    private String mCityName;
    private MyDragLayout mDlContent;
    private MainListView mLvMain;
    private ScrollView mSvBottom;
    private GridView mGv5daysWeather;
    private GridView mGvSuggestion;
    private String mCityId;
    private SuggestionAdapter suggestionAdapter;
    private int[] mainBgPic;
    private List<String> mDescList;
    private List<DailyWeather> daily_forecast;
    private DescInfo suggestion;
    private List<String> mDescDetailList;

    public MainPager(Activity activity, String cityName) {
        mActivity = activity;
        mCityName = cityName;
        initView();
    }

    private void initView() {
        mRootView = View.inflate(mActivity, R.layout.pager_main, null);
        mDlContent = (MyDragLayout) mRootView.findViewById(R.id.dl_content);
        mLvMain = (MainListView) mRootView.findViewById(R.id.lv_main);
        mSvBottom = (ScrollView) mRootView.findViewById(R.id.sv_bottom);

        View mBottomView =  View.inflate(mActivity, R.layout.bottom_content, mSvBottom);
        mGv5daysWeather = (GridView) mBottomView.findViewById(R.id.gv_5days_temperature);
        mGvSuggestion = (GridView) mBottomView.findViewById(R.id.gv_suggestion);
    }

    public void initData() {

        mLvMain.setDragLayout(mDlContent);
        mLvMain.setmCityName(mCityName);

        mCityId = WeatherDB.getInstance(mActivity).getCityInfo(mCityName).id;

        //背景图片数组
        TypedArray taNumPic = mActivity.getResources().obtainTypedArray(R.array.main_bg);
        mainBgPic = new int[taNumPic.length()];
        for (int i = 0; i < taNumPic.length(); i++) {
            mainBgPic[i] = taNumPic.getResourceId(i, 0);
        }
        taNumPic.recycle();

        mDlContent.setDragStatusListener(new MyDragLayout.OnDragStatusChangeListener() {
            @Override
            public void onClose() {
                mSvBottom.smoothScrollTo(0, 0);
            }

            @Override
            public void onOpen() {

            }

            @Override
            public void onDraging(float percent) {

            }
        });
        mLvMain.setOnRefreshListener(new MainListView.onRefreshListener() {
            @Override
            public void onRefresh() {
                getDataFromServer();
                int random = (int) (Math.random() * (mainBgPic.length - 1));
                MainActivity activity = (MainActivity) mActivity;
                activity.llApp.setBackgroundResource(mainBgPic[random]);
            }
        });

        String result = SharedPrefUtils.getString(mActivity, mCityName, "");
        if (TextUtils.isEmpty(result)) {
            getDataFromServer();
        } else {
            parseData(result);
        }
    }

    private void getDataFromServer() {

        String uri = GlobalUrl.GET_WEATHER_DATA_URL + mCityId + GlobalUrl.KEY;
        RequestParams params = new RequestParams(uri);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                parseData(result);
                mLvMain.onRefreshComplete(true);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("tag", "onError：" + ex);
                Utils.showToast(mActivity, "网络异常");
                mLvMain.onRefreshComplete(false);
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

    /*private void showProgressDialog() {
        dialog = new ProgressDialog(mActivity);
        dialog.setMessage("正在加载，请稍候...");
        dialog.setCancelable(false);
        dialog.show();
    }*/

    private void parseData(String result) {

        Gson gson = new Gson();
        WeatherDataFromJson dataFromJson = gson.fromJson(result, WeatherDataFromJson.class);
        //空气质量指数
        Aqi aqi = dataFromJson.HeWeatherDataList.get(0).aqi;
        if (aqi == null) {
            int aqiTemp = 0;
        }
        //城市基本信息
        //CityInfo basic = dataFromJson.HeWeatherDataList.get(0).basic;
        //天气预报
        daily_forecast = dataFromJson.HeWeatherDataList.get(0).daily_forecast;
        //实况天气
        //NowWeather now = dataFromJson.HeWeatherDataList.get(0).now;
        //生活指数
        suggestion = dataFromJson.HeWeatherDataList.get(0).suggestion;

        MainListAdapter mMainAdapter = new MainListAdapter(mActivity, mCityName, dataFromJson.HeWeatherDataList.get(0));
        mLvMain.setAdapter(mMainAdapter);

        FiveDaysWeatherAdapter dailyWeatherAdapter = new FiveDaysWeatherAdapter(mActivity, daily_forecast);
        mGv5daysWeather.setAdapter(dailyWeatherAdapter);
        mGv5daysWeather.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivityForBlur(position);
            }
        });

        mDescList = new ArrayList<>();
        mDescDetailList = new ArrayList<>();
        initListData();

        suggestionAdapter = new SuggestionAdapter(mActivity, mDescList);
        mGvSuggestion.setAdapter(suggestionAdapter);
        setGridViewHeightBasedOnChildren(mGvSuggestion);
        mGvSuggestion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.SuggestionDialog);
                builder.setMessage(mDescDetailList.get(position))
                        //.setPositiveButton("确定", null)
                        .show();
            }
        });
    }

    private void initListData() {
        mDescList.add(daily_forecast.get(0).astro.sr);
        mDescList.add(daily_forecast.get(0).astro.ss);
        mDescList.add(suggestion.comf.brf);
        mDescList.add(suggestion.cw.brf);
        mDescList.add(suggestion.drsg.brf);
        mDescList.add(suggestion.flu.brf);
        mDescList.add(suggestion.sport.brf);
        mDescList.add(suggestion.trav.brf);
        mDescList.add(suggestion.uv.brf);

        mDescDetailList.add("日出时间：" + daily_forecast.get(0).astro.sr);
        mDescDetailList.add("日落时间：" + daily_forecast.get(0).astro.ss);
        mDescDetailList.add(suggestion.comf.txt);
        mDescDetailList.add(suggestion.cw.txt);
        mDescDetailList.add(suggestion.drsg.txt);
        mDescDetailList.add(suggestion.flu.txt);
        mDescDetailList.add(suggestion.sport.txt);
        mDescDetailList.add(suggestion.trav.txt);
        mDescDetailList.add(suggestion.uv.txt);
    }

    public void setGridViewHeightBasedOnChildren(GridView gridView) {

        if (suggestionAdapter == null) {
            return;
        }

        int NumColumns = 3;

        int totalHeight = 0;
        int count ;
        if (suggestionAdapter.getCount() % NumColumns > 0) {
            count = suggestionAdapter.getCount() / NumColumns + 1;
        } else {
            count = suggestionAdapter.getCount() / NumColumns;
        }

        for (int i = 0; i < count; i++) {
            View listItem = suggestionAdapter.getView(i, null, gridView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight + Utils.dip2px(mActivity, 40);
        gridView.setLayoutParams(params);
    }

    public MyDragLayout getmDlContent() {
        return mDlContent;
    }

    /**
     * 进入天气详情页面设置背景模糊透明
     *
     * @param position 第几个页面
     */
    private void startActivityForBlur(final int position) {
        BlurBehind.getInstance().execute(mActivity, new OnBlurCompleteListener() {
            @Override
            public void onBlurComplete() {
                Intent intent = new Intent(mActivity, WeatherDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("position", position);
                intent.putExtra("city_name", mCityName);
                mActivity.startActivity(intent);
            }
        });
    }
}
