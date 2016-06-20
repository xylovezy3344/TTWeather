package com.xiaoyu.ttweather.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.xiaoyu.ttweather.R;
import com.xiaoyu.ttweather.service.AutoUpdateService;
import com.xiaoyu.ttweather.utils.SharedPrefUtils;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton mIbBack;
    private RelativeLayout mRlCityManage;
    private TextView mTvSelectCity;
    private TextView mTvAutoUpdate;
    private Switch mSAutoUpdate;
    private RelativeLayout mRlUpdateInterval;
    private TextView mTvUpdateInterval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();
        initData();
    }

    private void initView() {
        mIbBack = (ImageButton) findViewById(R.id.ib_back);
        mRlCityManage = (RelativeLayout) findViewById(R.id.rl_city_manage);
        mTvSelectCity = (TextView) findViewById(R.id.tv_select_city);

        mTvAutoUpdate = (TextView) findViewById(R.id.tv_auto_update_desc);
        mSAutoUpdate = (Switch) findViewById(R.id.s_auto_update);

        mRlUpdateInterval = (RelativeLayout) findViewById(R.id.rl_update_interval);
        mTvUpdateInterval = (TextView) findViewById(R.id.tv_update_interval);
    }

    private void initData() {
        mIbBack.setOnClickListener(this);
        mRlCityManage.setOnClickListener(this);
        mSAutoUpdate.setOnClickListener(this);
        mRlUpdateInterval.setOnClickListener(this);

        //城市管理设置描述
        String selectCities = SharedPrefUtils.getString(this, "select_cities", "");
        String[] selectCity = selectCities.split(",");
        String cityNames = "";
        for (int i = 0; i < selectCity.length - 1; i++) {
            cityNames = cityNames + selectCity[i] + "、";
        }
        cityNames = cityNames + selectCity[selectCity.length - 1];
        mTvSelectCity.setText(cityNames);
        //初始化自动更新
        boolean isAutoUpdate = SharedPrefUtils.getBoolean(this, "isAutoUpdate", true);
        mTvAutoUpdate.setText(isAutoUpdate ? "已开启" : "已关闭");
        mSAutoUpdate.setChecked(isAutoUpdate);
        //初始化更新间隔
        mTvUpdateInterval.setText(SharedPrefUtils.getInt(this, "update_interval", 1) + "小时");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                onBackPressed();
                break;
            case R.id.rl_city_manage:
                startActivity(new Intent(SettingActivity.this, CityManageActivity.class));
                break;
            case R.id.s_auto_update:
                boolean isAutoUpdate = mSAutoUpdate.isChecked();
                mTvAutoUpdate.setText(isAutoUpdate ? "已开启" : "已关闭");
                SharedPrefUtils.putBoolean(SettingActivity.this, "isAutoUpdate", isAutoUpdate);

                Intent intent = new Intent(SettingActivity.this, AutoUpdateService.class);
                if (isAutoUpdate) {
                    Log.e("tag", "已开启");
                    startService(intent);
                } else {
                    Log.e("tag", "已关闭");
                    stopService(intent);
                }
                break;
            case R.id.rl_update_interval:
                showUpdateIntervalDialog();
                break;
        }
    }

    private void showUpdateIntervalDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = View.inflate(this, R.layout.dialog_update_interval, null);
        ListView lvUpdateInterval = (ListView) view.findViewById(R.id.lv_update_interval);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

        String[] intervals = new String[]{"1小时", "2小时", "3小时", "4小时", "5小时"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_single_choice, intervals);
        lvUpdateInterval.setAdapter(adapter);
        int checkedPosition = SharedPrefUtils.getInt(this, "update_interval", 1) - 1;
        lvUpdateInterval.setItemChecked(checkedPosition, true);

        builder.setView(view);
        final AlertDialog dialog = builder.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        lvUpdateInterval.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPrefUtils.putInt(SettingActivity.this, "update_interval", (position + 1));
                mTvUpdateInterval.setText((position + 1) + "小时");
                dialog.dismiss();
            }
        });


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
    }
}
