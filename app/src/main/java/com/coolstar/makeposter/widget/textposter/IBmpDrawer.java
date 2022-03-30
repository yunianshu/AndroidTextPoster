package com.coolstar.makeposter.widget.textposter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.widget.ImageView;

/**
 * 可以增加颜色调节的画图接口
 * 可实现调节颜色、转黑白、亮度等效果
 * Created by 纪广兴 on 2016/2/3.
 */
public interface IBmpDrawer {
    public void setDrawBmp(Bitmap bmp);
    public int getBmpWidth();
    public int getBmpHeight();
    public void changeBrightness(float value);  //改变亮度
    public void changeColorMatrix(float[] martixarrs); //只改变颜色矩阵的数组值，不重新创建对象
    public void setBmpScaleType(ImageView.ScaleType type);
    public void setParentSize(int width, int height); //用于进行缩放绘制时使用
    public void onDrawBmp(Canvas canvas);
    public ImageView.ScaleType getBmpScaleType();
    public void outputDraw(Canvas canvas, int outWidth, int outHeight); //专用于画图到输出文件画布用方法，方便进行缩放绘制
    public Rect getBmpScaleRect();
    public int getParentWidth();
    public int getParentHeight();
}
