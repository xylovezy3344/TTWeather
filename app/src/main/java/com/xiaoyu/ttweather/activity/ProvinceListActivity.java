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

import com.xiaoyu.ttweather.R;
import com.xiaoyu.ttweather.db.WeatherDB;

import java.util.List;

public class ProvinceListActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton mIbBack;
    private RelativeLayout mRlSearch;
    private ListView mLvCity;
    private WeatherDB db;
    private List<String> provinceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_province_list);

        initView();
        initData();
    }

    private void initView() {
        mIbBack = (ImageButton) findViewById(R.id.ib_back);
        mRlSearch = (RelativeLayout) findViewById(R.id.rl_search);
        mLvCity = (ListView) findViewById(R.id.lv_province_list);
    }

    private void initData() {
        mIbBack.setOnClickListener(this);
        mRlSearch.setOnClickListener(this);
        //省份信息集合
        provinceList = WeatherDB.getInstance(this).getProvinces();
        mLvCity.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                provinceList));

        mLvCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ProvinceListActivity.this, CityListActivity.class);
                intent.putExtra("province", provinceList.get(position));
                startActivity(intent);
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
