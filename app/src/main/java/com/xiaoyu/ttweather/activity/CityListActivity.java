package com.xiaoyu.ttweather.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoyu.ttweather.R;
import com.xiaoyu.ttweather.db.WeatherDB;
import com.xiaoyu.ttweather.model.CityModel;
import com.xiaoyu.ttweather.utils.SharedPrefUtils;

import java.util.ArrayList;
import java.util.List;

public class CityListActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTvTitle;
    private ImageButton mIbBack;
    private RelativeLayout mRlSearch;
    private ListView mLvCity;
    private List<CityModel> citylList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);

        initView();
        initData();
    }

    private void initView() {
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mIbBack = (ImageButton) findViewById(R.id.ib_back);
        mRlSearch = (RelativeLayout) findViewById(R.id.rl_search);
        mLvCity = (ListView) findViewById(R.id.lv_city_list);
    }

    private void initData() {
        String province = getIntent().getStringExtra("province");
        mTvTitle.setText(province);
        mIbBack.setOnClickListener(this);
        mRlSearch.setOnClickListener(this);

        citylList = WeatherDB.getInstance(this).getCities(province);
        final List<String> cityNameList = new ArrayList<>();
        for (CityModel city : citylList) {
            if (city.id.endsWith("01") || city.id.endsWith("00")) {
                cityNameList.add(city.city);
            }
        }

        mLvCity.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                cityNameList));

        mLvCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectCities = SharedPrefUtils.getString(CityListActivity.this, "select_cities", "");
                if (selectCities.contains(cityNameList.get(position))) {
                    Toast.makeText(CityListActivity.this, "该城市已经添加！", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPrefUtils.putSelectCity(CityListActivity.this, cityNameList.get(position), false);
                    //SharedPrefUtils.putString(CityListActivity.this, "checked_city", cityNameList.get(position));
                    Intent intent = new Intent();
                    intent.setClass(CityListActivity.this, CityManageActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                finish();
                break;
            case R.id.rl_search:
                break;
        }
    }
}
