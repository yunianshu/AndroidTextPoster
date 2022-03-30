package com.coolstar.makeposter.widget.textposter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.util.Log;
import android.widget.ImageView;

import com.coolstar.makeposter.utils.LogMgr;


/**
 * 实现颜色画图片
 * Created by 纪广兴 on 2016/2/3.
 */
public class BmpDrawer implements IBmpDrawer {
    private ColorMatrix brightnessMatrix;  //保存亮度
    private ColorMatrix hueMatrix;          //保存色调
    private ColorMatrix bgColorMatrix;  //保存传入的颜色矩阵
    private PaintFlagsDrawFilter pfd; //抗锯齿画图片开启
    private Bitmap bgBmp;
    private ImageView.ScaleType bgScaleType = ImageView.ScaleType.FIT_XY;
    private int parentWidth;
    private int parentHeight;
    private Paint bgPaint;
    private Rect scaleRect; //保存根据缩放类型计算到的要画的地方的rect值
    private Rect bmpRect;  //保存图片的整体大小

    public BmpDrawer() {
        bgPaint = new Paint();
        bgPaint.setStyle(Paint.Style.FILL);
        bgColorMatrix = new ColorMatrix();  //保存传入的颜色矩阵
        brightnessMatrix = new ColorMatrix();  //保存传入的颜色矩阵
        hueMatrix = new ColorMatrix();  //保存传入的颜色矩阵
        bgColorMatrix = new ColorMatrix();  //保存传入的颜色矩阵
        pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG); //抗锯齿画图片开启
        bmpRect = new Rect();
        scaleRect = new Rect();
    }

    private void updateScaleRect() {
        if(bgBmp==null){
            return;
        }
        float scaleXVal = 0f;
        float scaleYVal = 0f;
        float minScaleVal = 0f;
        switch (bgScaleType){
            case FIT_XY:
                scaleRect.set(0,0,parentWidth,parentHeight);
                break;
            case FIT_START:
                scaleRect.set(0,0,bgBmp.getWidth(),bgBmp.getHeight());
                break;
            case FIT_END:
                scaleRect.set(parentWidth-bgBmp.getWidth(),parentHeight-bgBmp.getHeight(),parentWidth,parentHeight);
                break;
            case FIT_CENTER://把图片按比例扩大/缩小到View的宽度，居中显示
                scaleXVal = parentWidth*1.0f/bgBmp.getWidth();  //获取屏幕与图的倍数，大于1.0，表示屏幕比图大，小于1.0，表示图比控件大
                scaleYVal = parentHeight*1.0f/bgBmp.getHeight();
                minScaleVal = Math.min(scaleXVal,scaleYVal);
                if(scaleXVal>scaleYVal){
                    int scaledValue = (int) (bgBmp.getWidth()*minScaleVal);
                    int width_space = (parentWidth-scaledValue)/2;
                    scaleRect.set(width_space,0,width_space+scaledValue,parentHeight);
                }else{
                    int scaledValue = (int) (bgBmp.getHeight()*minScaleVal);
                    int height_space = (parentHeight-scaledValue)/2;
                    scaleRect.set(0,height_space,parentWidth,height_space+scaledValue);
                }
                break;
            case CENTER_INSIDE: //将图片的内容完整居中显示，通过按比例缩小或原来的size使得图片长/宽等于或小于View的长/宽
                scaleXVal = parentWidth*1.0f/bgBmp.getWidth();  //获取屏幕与图的倍数，大于1.0，表示屏幕比图大，小于1.0，表示图比控件大
                scaleYVal = parentHeight*1.0f/bgBmp.getHeight();
                minScaleVal = Math.min(scaleXVal,scaleYVal);
                if(minScaleVal>=1.0f){
                    int width_space = (parentWidth-bgBmp.getWidth())/2;
                    int height_space = (parentHeight-bgBmp.getHeight())/2;
                    scaleRect.set(width_space,height_space,width_space+bgBmp.getWidth(),height_space+bgBmp.getHeight());
                }else{
                    if(scaleXVal>scaleYVal){
                        int scaledValue = (int) (bgBmp.getWidth()*minScaleVal);
                        int width_space = (parentWidth-scaledValue)/2;
                        scaleRect.set(width_space,0,width_space+scaledValue,parentHeight);
                    }else{
                        int scaledValue = (int) (bgBmp.getHeight()*minScaleVal);
                        int height_space = (parentHeight-scaledValue)/2;
                        scaleRect.set(0,height_space,parentWidth,height_space+scaledValue);
                    }
                }
                break;
            case CENTER_CROP: //按比例扩大图片的size居中显示，使得图片长(宽)等于或大于View的长(宽)
                scaleXVal = parentWidth*1.0f/bgBmp.getWidth();  //获取屏幕与图的倍数，大于1.0，表示屏幕比图大，小于1.0，表示图比控件大
                scaleYVal = parentHeight*1.0f/bgBmp.getHeight();
                float maxScaleVal = Math.max(scaleXVal,scaleYVal);
                if(scaleXVal>scaleYVal){
                    int scaledHeight = (int) (bgBmp.getHeight()*maxScaleVal);
                    int height_space = (parentHeight-scaledHeight)/2;
                    scaleRect.set(0,height_space,parentWidth,height_space+scaledHeight);
                }else{
                    int scaledWidth = (int) (bgBmp.getWidth()*maxScaleVal);
                    int width_space = (parentWidth-scaledWidth)/2;
                    scaleRect.set(width_space,0,width_space+scaledWidth,parentHeight);
                }
                break;
            case CENTER:
                int width_space = (parentWidth-bgBmp.getWidth())/2;
                int height_space = (parentHeight-bgBmp.getHeight())/2;
                scaleRect.set(width_space,height_space,width_space+bgBmp.getWidth(),height_space+bgBmp.getHeight());
                break;
            default:
                scaleRect.set(0,0,parentWidth,parentHeight);
                break;
        }
    }

    //=======================================================================
    @Override
    public void setDrawBmp(Bitmap bmp) {
        bgBmp = bmp;
        if(bgBmp!=null){
            bmpRect.set(0,0,bgBmp.getWidth(),bgBmp.getHeight());
            updateScaleRect();
        }
    }

    @Override
    public int getBmpWidth() {
        if(bgBmp!=null){
            int bmpWidth = bgBmp.getWidth();
            LogMgr.d("BmpSize","width:"+bmpWidth);
            return bmpWidth;
        }
        return parentWidth;
    }

    @Override
    public int getBmpHeight() {
        if(bgBmp!=null){
            int bmpHeight = bgBmp.getHeight();
            LogMgr.d("BmpSize","Height:"+bmpHeight);
            return bmpHeight;
        }
        return parentHeight;
    }

    @Override
    public void changeBrightness(float value) {
        brightnessMatrix.reset();
        brightnessMatrix.setScale(value,value,value,1f);
        bgColorMatrix.reset();
        bgColorMatrix.postConcat(hueMatrix);
        bgColorMatrix.postConcat(brightnessMatrix);
        bgPaint.setColorFilter(new ColorMatrixColorFilter(bgColorMatrix));
    }

    @Override
    public void changeColorMatrix(float[] martixarrs) {
        hueMatrix.set(martixarrs);
        bgColorMatrix.reset();
        bgColorMatrix.postConcat(hueMatrix);
        bgColorMatrix.postConcat(brightnessMatrix);
        bgPaint.setColorFilter(new ColorMatrixColorFilter(bgColorMatrix));
    }

    @Override
    public void setBmpScaleType(ImageView.ScaleType type) {
        bgScaleType = type;
        updateScaleRect();
    }

    @Override
    public void setParentSize(int width, int height) {
        parentWidth = width;
        parentHeight = height;
        updateScaleRect();
    }

    @Override
    public void onDrawBmp(Canvas canvas) {
        if(bgBmp!=null&&scaleRect!=null){
            canvas.save();
            canvas.setDrawFilter(pfd);
            canvas.drawBitmap(bgBmp,bmpRect,scaleRect,bgPaint);
            canvas.restore();
        }else{
            canvas.drawColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void outputDraw(Canvas canvas, int outWidth, int outHeight) {
        if(bgBmp!=null){
            Rect outRect = new Rect(0,0,outWidth,outHeight);
            canvas.save();
            canvas.setDrawFilter(pfd);
            canvas.drawBitmap(bgBmp,bmpRect,outRect,bgPaint);
            canvas.restore();
        }else{
            canvas.drawColor(Color.GRAY);
        }
    }

    @Override
    public Rect getBmpScaleRect() {
        return scaleRect;
    }

    @Override
    public int getParentWidth() {
        return parentWidth;
    }

    @Override
    public int getParentHeight() {
        return parentHeight;
    }

    @Override
    public ImageView.ScaleType getBmpScaleType() {
        return bgScaleType;
    }

}
