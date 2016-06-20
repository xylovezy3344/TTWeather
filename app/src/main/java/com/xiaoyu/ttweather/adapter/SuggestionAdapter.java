package com.xiaoyu.ttweather.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.media.Image;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoyu.ttweather.R;

import java.util.List;

/**
 * SuggestionAdapter
 * Created by xiaoy on 16/6/12.
 */
public class SuggestionAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mDescList;
    private final int[] suggestionPics;
    private final String[] title;

    public SuggestionAdapter(Context context, List<String> descList) {
        this.mContext = context;
        this.mDescList = descList;

        TypedArray taSuggestion = context.getResources().obtainTypedArray(R.array.suggestion_pic);
        suggestionPics = new int[taSuggestion.length()];
        for (int i = 0; i < taSuggestion.length(); i++) {
            suggestionPics[i] = taSuggestion.getResourceId(i, 0);
        }
        taSuggestion.recycle();

        title = new String[]{"日出", "日落", "舒适度", "洗车", "穿衣", "感冒", "运动", "旅行", "紫外线"};
    }

    @Override
    public int getCount() {
        return mDescList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDescList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_gridview_suggestion, null);
            holder = new ViewHolder();
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tvDesc = (TextView) convertView.findViewById(R.id.tv_desc);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ivIcon.setImageResource(suggestionPics[position]);
        holder.tvTitle.setText(title[position]);
        holder.tvDesc.setText(mDescList.get(position));

        return convertView;
    }

    class ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;
        TextView tvDesc;
    }
}
