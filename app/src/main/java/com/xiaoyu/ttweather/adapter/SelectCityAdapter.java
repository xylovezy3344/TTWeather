package com.xiaoyu.ttweather.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoyu.ttweather.R;
import com.xiaoyu.ttweather.model.SelectCity;
import com.xiaoyu.ttweather.utils.SharedPrefUtils;
import com.xiaoyu.ttweather.utils.Utils;

import java.util.List;

/**
 * 城市管理页面
 * Created by xiaoy on 16/6/13.
 */
public class SelectCityAdapter extends BaseAdapter {

    private Context mContext;
    private List<SelectCity> mSelectCityList;
    private boolean mIsSetting;
    private String warnCity;
    private String localCity;

    public SelectCityAdapter(Context context, List<SelectCity> selectCityList) {
        mContext = context;
        mSelectCityList = selectCityList;
        mIsSetting = true;
    }

    @Override
    public int getCount() {
        if (mSelectCityList.size() < 9) {
            return mSelectCityList.size() + 1;
        } else {
            return 9;
        }
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_select_city, null);
            holder = new ViewHolder();

            holder.llMain = (LinearLayout) convertView.findViewById(R.id.ll_select_city_main);
            holder.ivAdd = (ImageView) convertView.findViewById(R.id.iv_select_city_add);

            holder.ivDelete = (ImageView) convertView.findViewById(R.id.iv_delete);
            holder.llSetWarn = (LinearLayout) convertView.findViewById(R.id.ll_set_warn);
            holder.tvWarnCity = (TextView) convertView.findViewById(R.id.tv_warn_city);

            holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.ivLocate = (ImageView) convertView.findViewById(R.id.iv_locate);
            holder.tvCityName = (TextView) convertView.findViewById(R.id.tv_city_name);
            holder.tvHighTemp = (TextView) convertView.findViewById(R.id.tv_high_temp);
            holder.tvLowTemp = (TextView) convertView.findViewById(R.id.tv_low_temp);
            holder.tvCond = (TextView) convertView.findViewById(R.id.tv_cond);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position != 9 && position == mSelectCityList.size()) {
            if (mIsSetting) {
                convertView.setVisibility(View.VISIBLE);
                holder.llMain.setVisibility(View.INVISIBLE);
                holder.ivAdd.setVisibility(View.VISIBLE);
                holder.ivDelete.setVisibility(View.INVISIBLE);
            } else {
                convertView.setVisibility(View.INVISIBLE);
            }
        } else {
            holder.llMain.setVisibility(View.VISIBLE);
            holder.ivAdd.setVisibility(View.INVISIBLE);

            localCity = SharedPrefUtils.getString(mContext, "location_city", "");
            if (mSelectCityList.get(position).title.equals(localCity)) {
                holder.ivLocate.setVisibility(View.VISIBLE);
            } else {
                holder.ivLocate.setVisibility(View.GONE);
            }

            holder.ivIcon.setImageResource(mSelectCityList.get(position).pic);
            holder.tvCityName.setText(mSelectCityList.get(position).title);
            holder.tvHighTemp.setText(mSelectCityList.get(position).highTemp);
            holder.tvLowTemp.setText(mSelectCityList.get(position).lowTemp);
            holder.tvCond.setText(mSelectCityList.get(position).cond);

            warnCity = SharedPrefUtils.getString(mContext, "warn_city", "");
            if (warnCity.equals(mSelectCityList.get(position).title)) {
                holder.tvWarnCity.setText("提醒城市");
                holder.tvWarnCity.setTextColor(Color.argb(255, 46, 144, 253));
            } else if (TextUtils.isEmpty(warnCity) && position == 0) {
                holder.tvWarnCity.setText("提醒城市");
                holder.tvWarnCity.setTextColor(Color.argb(255, 46, 144, 253));
                warnCity = mSelectCityList.get(position).title;
                SharedPrefUtils.putString(mContext, "warn_city",
                        mSelectCityList.get(position).title);
            } else {
                holder.tvWarnCity.setText("设置为提醒城市");
                holder.tvWarnCity.setTextColor(Color.GRAY);
            }

            if (mIsSetting) {
                holder.ivDelete.setVisibility(View.INVISIBLE);
                if (holder.tvWarnCity.getText().equals("设置为提醒城市")) {
                    holder.llSetWarn.setVisibility(View.INVISIBLE);
                } else {
                    holder.llSetWarn.setVisibility(View.VISIBLE);
                }
            } else {
                holder.llSetWarn.setVisibility(View.VISIBLE);
                holder.ivDelete.setVisibility(View.VISIBLE);

                holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String deleteCityName = mSelectCityList.get(position).title;

                        if (deleteCityName.equals(warnCity)) {
                            Utils.showToast(mContext, deleteCityName + "为提醒城市，提醒城市" +
                                    "不能删除，请先设置其它城市为提醒城市再尝试删除！");
                        } else {

                            if (deleteCityName.equals(localCity)) {
                                SharedPrefUtils.putString(mContext, "location_city", "");
                            }

                            //从SharedPreferences中删除城市
                            String selectCities = SharedPrefUtils.getString(mContext, "select_cities", "");
                            String newSelectCities = selectCities.replace(deleteCityName + ",", "");
                            SharedPrefUtils.putString(mContext, "select_cities", newSelectCities);
                            //删除条目缓存数据
                            SharedPrefUtils.putString(mContext, deleteCityName, "");
                            //城市数目减一
                            int selectCityNum = SharedPrefUtils.getInt(mContext, "select_city_num", 0);
                            SharedPrefUtils.putInt(mContext, "select_city_num", --selectCityNum);
                            //从mSelectCityList中删除
                            mSelectCityList.remove(position);
                            //如果删除的是选中的city，默认将第一项设为选中city
                            if (deleteCityName.equals(SharedPrefUtils.getString(mContext,
                                    "checked_city", ""))) {
                                SharedPrefUtils.putString(mContext, "checked_city", mSelectCityList.get(0).title);
                            }

                            SelectCityAdapter.this.notifyDataSetChanged();
                        }
                    }
                });

                holder.llSetWarn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!warnCity.equals(mSelectCityList.get(position).title)) {
                            holder.tvWarnCity.setText("提醒城市");
                            holder.tvWarnCity.setTextColor(Color.argb(255, 46, 144, 253));
                            SharedPrefUtils.putString(mContext, "warn_city",
                                    mSelectCityList.get(position).title);
                            SelectCityAdapter.this.notifyDataSetChanged();
                        }
                    }
                });
            }
        }

        return convertView;
    }

    public void notifyDataSetChanged(boolean isSetting) {
        mIsSetting = isSetting;
        super.notifyDataSetChanged();
    }

    class ViewHolder {
        public ImageView ivDelete;
        public ImageView ivLocate;
        public TextView tvCityName;
        public TextView tvHighTemp;
        public TextView tvLowTemp;
        public ImageView ivIcon;
        public TextView tvCond;
        public TextView tvWarnCity;
        public LinearLayout llSetWarn;
        public LinearLayout llMain;
        public ImageView ivAdd;
    }
}
