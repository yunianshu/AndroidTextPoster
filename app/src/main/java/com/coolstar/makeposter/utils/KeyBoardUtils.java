package com.coolstar.makeposter.utils;

import android.app.Service;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by jiguangxing on 2016/3/15.
 */
public class KeyBoardUtils {

    public static void hideKeyboard(View windowView) {
        if (windowView == null) {
            return ;
        }

        Context context = windowView.getContext();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(windowView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void hideKeyboard(Context context) {
        if (context == null) {
            return ;
        }

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    // 李建衡：显示键盘
    public static boolean showKeyboard(final View windowView) {
        if (windowView == null) {
            return false;
        }

        if (!windowView.isFocused()) {
            windowView.requestFocus();
        }

        Context context = windowView.getContext();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.showSoftInput(windowView, InputMethodManager.SHOW_IMPLICIT);
    }


}
