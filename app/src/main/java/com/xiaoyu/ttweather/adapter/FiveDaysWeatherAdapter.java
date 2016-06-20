package com.xiaoyu.ttweather.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoyu.ttweather.R;
import com.xiaoyu.ttweather.model.DailyWeather;
import com.xiaoyu.ttweather.utils.DateUtils;
import com.xiaoyu.ttweather.utils.Utils;
import com.xiaoyu.ttweather.utils.WeatherDataUtils;

import java.util.List;

/**
 * 5天天气GridView的适配器
 * Created by xiaoy on 16/6/8.
 */
public class FiveDaysWeatherAdapter extends BaseAdapter {

    private List<DailyWeather> list;
    private Context context;
    private int tempHighMax;
    private int tempHighMin;
    private int tempLowMax;
    private final int[] highMargin;
    private final int[] lowMargin;
    private float percent;

    public FiveDaysWeatherAdapter(Context context, List<DailyWeather> list) {
        this.list = list;
        this.context = context;

        //拿到6天最高气温的最高值最低值和最低气温的最高值
        tempHighMax = tempHighMin = list.get(0).tmp.max;
        tempLowMax = list.get(0).tmp.min;
        for (DailyWeather weather : list) {
            if (weather.tmp.max > tempHighMax)
                tempHighMax = weather.tmp.max;
            if (weather.tmp.max < tempHighMin)
                tempHighMin = weather.tmp.max;
            if (weather.tmp.min > tempLowMax)
                tempLowMax = weather.tmp.min;
        }

        highMargin = new int[6];
        lowMargin = new int[6];
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_5days_temperature, null);
            holder = new ViewHolder();
            holder.tvText = (TextView) convertView.findViewById(R.id.tv_5days_text);
            holder.tvDate = (TextView) convertView.findViewById(R.id.tv_5days_date);
            holder.ivIconHighTemp = (ImageView) convertView.findViewById(R.id.iv_5days_high_temp);
            holder.tvTextHighTemp = (TextView) convertView.findViewById(R.id.tv_5days_high_temp);
            holder.ivHighDot = (ImageView) convertView.findViewById(R.id.iv_5days_high_dot);
            holder.ivLowDot = (ImageView) convertView.findViewById(R.id.iv_5days_low_dot);
            holder.tvTextLowTemp = (TextView) convertView.findViewById(R.id.tv_5days_low_temp);
            holder.ivIconLowTemp = (ImageView) convertView.findViewById(R.id.iv_5days_low_temp);
            holder.tvWindDirection = (TextView) convertView.findViewById(R.id.tv_5days_wind_direction);
            holder.tvWindPower = (TextView) convertView.findViewById(R.id.tv_5days_wind_power);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DailyWeather dailyWeather = list.get(position);

        //显示星期几
        if (position == 0) {

        } else {
            holder.tvText.setText(DateUtils.getWeek(dailyWeather.date));
        }
        //显示日期（例：6月9号：6/9）
        String[] dates = dailyWeather.date.split("\\-");
        String date = Integer.valueOf(dates[1]) + "/" + Integer.valueOf(dates[2]);
        holder.tvDate.setText(date);
        //显示最高最低温度
        holder.tvTextHighTemp.setText(dailyWeather.tmp.max + "°");
        holder.tvTextLowTemp.setText(dailyWeather.tmp.min + "°");
        //显示风向，风力
        String dir = dailyWeather.wind.dir;
        if (dir.equals("无持续风向")) {
            holder.tvWindDirection.setText("微风");
            holder.tvWindPower.setText("<2级");
        } else {
            holder.tvWindDirection.setText(dir);
            holder.tvWindPower.setText(dailyWeather.wind.sc + "级");
        }
        //显示图片
        holder.ivIconHighTemp.setImageResource(WeatherDataUtils.getWeatherPic(
                context, dailyWeather.cond.code_d, true));
        holder.ivIconLowTemp.setImageResource(WeatherDataUtils.getWeatherPic(
                context, dailyWeather.cond.code_n, false));

        int dyHigh = (tempHighMax - dailyWeather.tmp.max) * 10;
        LinearLayout.MarginLayoutParams highParams = (LinearLayout.MarginLayoutParams)
                holder.ivIconHighTemp.getLayoutParams();
        highParams.setMargins(0, dyHigh, 0, 0);
        holder.ivIconHighTemp.requestLayout();

        highMargin[position] = dyHigh;

        int dyCenter = (dailyWeather.tmp.max - tempHighMin) * 10;
        int dyLow = (tempLowMax - dailyWeather.tmp.min) * 10;
        LinearLayout.MarginLayoutParams lowParams = (LinearLayout.MarginLayoutParams)
                holder.ivLowDot.getLayoutParams();
        lowParams.setMargins(0, dyCenter + dyLow + 20, 0, 0);
        holder.ivLowDot.requestLayout();

        //两个圆点圆心之间固定有10dp距离
        lowMargin[position] = dyCenter + dyLow + 20 + Utils.dip2px(context, 10);

        return convertView;
    }


    class ViewHolder {
        public TextView tvText;
        public TextView tvDate;
        public ImageView ivIconHighTemp;
        public TextView tvTextHighTemp;
        public ImageView ivHighDot;
        public ImageView ivLowDot;
        public TextView tvTextLowTemp;
        public ImageView ivIconLowTemp;
        public TextView tvWindDirection;
        public TextView tvWindPower;
    }

    public int[] getHighMargin() {
        return highMargin;
    }

    public int[] getLowMargin() {
        return lowMargin;
    }

}
