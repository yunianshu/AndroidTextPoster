package com.coolstar.makeposter.beans;

import java.util.Objects;

/**
 * Created by jiguangxing on 2016/3/4.
 */
public class AsyncErrorInfo {
    int error;
    String msg;

    public AsyncErrorInfo(int err, String errMsg) {
        this.error = err;
        this.msg = errMsg;
    }
}
