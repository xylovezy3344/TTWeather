package com.xiaoyu.ttweather.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.xiaoyu.ttweather.R;
import com.xiaoyu.ttweather.adapter.FiveDaysWeatherAdapter;
import com.xiaoyu.ttweather.utils.Utils;

/**
 *
 * Created by xiaoy on 16/6/10.
 */
public class MyGridView extends GridView {

    private Paint paint;
    private ImageView iv;
    private int[] highMargin;
    private int[] lowMargin;

    public MyGridView(Context context) {
        this(context, null);
    }

    public MyGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);

        FiveDaysWeatherAdapter myAdapter = (FiveDaysWeatherAdapter) adapter;
        highMargin = myAdapter.getHighMargin();
        lowMargin = myAdapter.getLowMargin();

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(5);
        paint.setAntiAlias(true);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (highMargin != null && lowMargin != null) {
            //每个条目宽
            float perWidth = (getWidth() - getPaddingStart() - getPaddingEnd())/ 6;
            //基准Y值
            int y = Utils.dip2px(getContext(), 145f);
            //每个点Y值都是基准Y值+margin的值
            //高温点起点
            float highX0 = perWidth / 2 + getPaddingStart();
            float highY0 = y + highMargin[0];
            //第二个点
            float highX1 = highX0 + perWidth;
            float highY1 = y + highMargin[1];
            //第三个点
            float highX2 = highX1 + perWidth;
            float highY2 = y + highMargin[2];
            //第四个点
            float highX3 = highX2 + perWidth;
            float highY3 = y + highMargin[3];
            //第五个点
            float highX4 = highX3 + perWidth;
            float highY4 = y + highMargin[4];
            //第六个点
            float highX5 = highX4 + perWidth;
            float highY5 = y + highMargin[5];

            float[] highPts={highX0, highY0, highX1, highY1,
                    highX1, highY1, highX2, highY2,
                    highX2, highY2, highX3, highY3,
                    highX3, highY3, highX4, highY4,
                    highX4, highY4, highX5, highY5};
            canvas.drawLines(highPts, paint);

            //绘制低温点折线
            //低温点X坐标与高温点一致，Y坐标=高温点Y值+margin值
            //低温点起点
            float lowY0 = highY0 + lowMargin[0];
            //第二个点
            float lowY1 = highY1 + lowMargin[1];
            //第三个点
            float lowY2 = highY2 + lowMargin[2];
            //第四个点
            float lowY3 = highY3 + lowMargin[3];
            //第五个点
            float lowY4 = highY4 + lowMargin[4];
            //第六个点
            float lowY5 = highY5 + lowMargin[5];

            float[] lowPts={highX0, lowY0, highX1, lowY1,
                    highX1, lowY1, highX2, lowY2,
                    highX2, lowY2, highX3, lowY3,
                    highX3, lowY3, highX4, lowY4,
                    highX4, lowY4, highX5, lowY5};
            canvas.drawLines(lowPts, paint);
        }
    }
}
