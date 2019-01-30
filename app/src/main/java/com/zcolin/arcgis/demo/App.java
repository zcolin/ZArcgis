package com.zcolin.arcgis.demo;

import android.support.multidex.MultiDex;

import com.zcolin.frame.app.BaseApp;
import com.zcolin.frame.util.LogUtil;

/**
 * Created by ZColin on 2019/1/21.
 */

public class App extends BaseApp {
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.LOG_DEBUG = BuildConfig.DEBUG;
    }

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
