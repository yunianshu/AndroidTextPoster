package com.coolstar.makeposter.utils;

import android.util.Log;

import com.coolstar.makeposter.BuildConfig;

/**
 * Created by jiguangxing on 2016/3/7.
 */
public class LogMgr {

    public static void d(String TAG,String msg){
        if(BuildConfig.DEBUG){
            Log.d(TAG,msg);
        }
    }

    public static void e(String TAG,String msg){
        if(BuildConfig.DEBUG){
            Log.e(TAG,msg);
        }
    }
}
