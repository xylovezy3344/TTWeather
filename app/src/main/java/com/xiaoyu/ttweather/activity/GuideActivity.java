package com.xiaoyu.ttweather.activity;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.xiaoyu.ttweather.R;
import com.xiaoyu.ttweather.utils.SharedPrefUtils;
import com.xiaoyu.ttweather.view.VerticalViewPager;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends AppCompatActivity {

    private VerticalViewPager mVpGuide;
    private int[] guidePics = new int[]{R.drawable.guide_pic_1, R.drawable.guide_pic_2,
            R.drawable.guide_pic_3, R.drawable.guide_pic_4, R.drawable.guide_pic_5};
    private List<View> viewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        initView();
        initData();
    }

    private void initData() {

        viewList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(guidePics[i]);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            viewList.add(imageView);
        }

        mVpGuide.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return viewList.size() + 1;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                if (position == getCount() - 1) {
                    View view = View.inflate(GuideActivity.this, R.layout.guide_pager, null);
                    Button btnStart = (Button) view.findViewById(R.id.btn_start);
                    btnStart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            jumpNextActivity();
                        }
                    });
                    container.addView(view);
                    return view;

                } else {
                    container.addView(viewList.get(position));
                    return viewList.get(position);
                }
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
    }

    private void jumpNextActivity() {
        Intent intent = new Intent(GuideActivity.this, MainActivity.class);
        startActivity(intent);
        //记录已经展示过新手引导页
        SharedPrefUtils.putBoolean(GuideActivity.this, "has_run_guide", true);
        finish();
    }

    private void initView() {
        mVpGuide = (VerticalViewPager) findViewById(R.id.vp_guide);
    }

    @Override
    public void onBackPressed() {
        jumpNextActivity();
    }
}
