package com.coolstar.makeposter.widget.textposter;

import android.graphics.Canvas;

/**
 * Created by 纪广兴 on 2016/1/29.
 */
public class VerticalBoxPoster extends VerticalPoster {
    private static final float LINE_SPACE = 10;
    @Override
    public void onPostDraw(Canvas canvas) {
        super.onPostDraw(canvas);
        float oldWidth = textPaint.getStrokeWidth();
        textPaint.setStrokeWidth(3);
        canvas.drawRect(textRect,textPaint);
        textPaint.setStrokeWidth(oldWidth);
    }
}
