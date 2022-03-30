package com.coolstar.makeposter.utils;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

/**
 * Created by jiguangxing on 2016/3/15.
 */
public class DialogUtils {

    public static void showConfirmDialog(Context context, String dlgContent, DialogInterface.OnClickListener okClickListener,DialogInterface.OnClickListener cancelClickListener){
        new AlertDialog.Builder(context)
                .setTitle("确认")
                .setMessage(dlgContent)
                .setNegativeButton("取消",cancelClickListener)
                .setPositiveButton("确定",okClickListener)
                .create()
                .show();
    }

    public static void showHintDialog(Context context,String dlgContent,DialogInterface.OnClickListener okClickListener){
        new AlertDialog.Builder(context)
                .setTitle("警告")
                .setMessage(dlgContent)
                .setPositiveButton("确定",okClickListener)
                .create()
                .show();
    }

}
