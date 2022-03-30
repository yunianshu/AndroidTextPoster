package com.coolstar.makeposter.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * 可设置圆角的颜色显示控件，带描边效果
 * Created by 纪广兴 on 2016/2/17.
 */
public class RoundedColorView extends View {
    private static final int COLOR_BORDER_DEFAULT   = Color.parseColor("#666666");
    private static final int COLOR_BORDER_SELECTED  = Color.parseColor("#ffcd2d");

    public RoundedColorView(Context context) {
        super(context);
        init(context);
    }

    public RoundedColorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RoundedColorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    //=========================================================================================
    private Paint borderPaint;  //描边用画笔
    private Paint fillPaint;  //填充颜色用画笔
    private int radius = 10;
    private RectF fillRect;
    private int borderWidth = 2;  //描边宽度

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int width) {
        if(borderWidth!=width){
            this.borderWidth = width;
            invalidate();
        }
    }

    public int getBorderColor(){
        return borderPaint.getColor();
    }

    public void setBorderColor(int borderColor){
        borderPaint.setColor(borderColor);
        invalidate();
    }

    public int getFillColor() {
        return fillPaint.getColor();
    }

    public void setFillColor(int fillColor) {
        fillPaint.setColor(fillColor);
        invalidate();
    }

    private void init(Context context){
        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.WHITE);
        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderWidth);
        borderPaint.setColor(Color.GRAY);
        fillRect = new RectF();

    }

    //=========================================================================================

    @Override
    public void setSelected(boolean selected) {
        borderPaint.setColor(selected?COLOR_BORDER_SELECTED:COLOR_BORDER_DEFAULT);  //选择状态通过边框显示
        super.setSelected(selected);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRoundRect(fillRect,radius,radius,fillPaint);
        canvas.drawRoundRect(fillRect,radius,radius,borderPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        fillRect.set(getPaddingLeft(),getPaddingTop(),w-getPaddingRight(),h-getPaddingBottom());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = getPaddingLeft()+100+getPaddingRight();
        int desiredHeight = getPaddingTop()+100+getPaddingBottom();

        int widthSpec = resolveMeasured(widthMeasureSpec,desiredWidth);
        int heightSpec = resolveMeasured(heightMeasureSpec,desiredHeight);

        setMeasuredDimension(widthSpec,heightSpec);
    }

    /**
     * 计算不同模式下，最终尺寸值
     * @param measureSpec
     * @param desired
     * @return
     */
    private int resolveMeasured(int measureSpec, int desired)
    {
        int result = 0;
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.UNSPECIFIED:
                result = desired;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(specSize, desired);
                break;
            case MeasureSpec.EXACTLY:
            default:
                result = specSize;
        }
        return result;
    }
}
