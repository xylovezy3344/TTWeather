package com.xiaoyu.ttweather;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.test.ApplicationTestCase;
import android.util.Log;
import android.util.TimeUtils;

import com.xiaoyu.ttweather.utils.DateUtils;
import com.xiaoyu.ttweather.utils.SharedPrefUtils;
import com.xiaoyu.ttweather.utils.Utils;

import org.xutils.common.util.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);

        long time=System.currentTimeMillis();
        Log.e("tag", time + "");
    }
}