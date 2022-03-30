package com.coolstar.makeposter;

import android.app.Application;

import com.coolstar.makeposter.utils.DeviceInfo;

/**
 * Created by jiguangxing on 2016/3/7.
 */
public class CustomApplication extends Application {

    public static CustomApplication appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
//        DeviceInfo.init(this);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
