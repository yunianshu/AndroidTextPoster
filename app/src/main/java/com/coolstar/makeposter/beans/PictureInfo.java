package com.coolstar.makeposter.beans;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;


/**
 * 手机图片项信息定义
 * Created by jiguangxing on 2016/3/4.
 */
public class PictureInfo implements Parcelable {

    public String picName;      //图片名称
    public String fileName;     //文字路径及名称
    public String dirName;      //图片所在目录名称

    public Bitmap decodeBmp;    //只用于传递解码出来的bitmap

    public PictureInfo() {
    }

    protected PictureInfo(Parcel in) {
        picName = in.readString();
        fileName = in.readString();
        dirName = in.readString();
    }

    public static final Creator<PictureInfo> CREATOR = new Creator<PictureInfo>() {
        @Override
        public PictureInfo createFromParcel(Parcel in) {
            return new PictureInfo(in);
        }

        @Override
        public PictureInfo[] newArray(int size) {
            return new PictureInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(picName);
        dest.writeString(fileName);
        dest.writeString(dirName);
    }
}
