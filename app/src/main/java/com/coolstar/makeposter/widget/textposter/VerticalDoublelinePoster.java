package com.coolstar.makeposter.widget.textposter;

import android.graphics.Canvas;

/**
 * Created by 纪广兴 on 2016/1/29.
 */
public class VerticalDoublelinePoster extends VerticalPoster {
    private static final float LINE_SPACE = 10;
    @Override
    public void onPostDraw(Canvas canvas) {
        super.onPostDraw(canvas);
        float lineLeft = textRect.left - LINE_SPACE;
        float lineRight = textRect.right+LINE_SPACE;
        float oldWidth = textPaint.getStrokeWidth();
        textPaint.setStrokeWidth(3);
        canvas.drawLine(lineLeft,textRect.top,lineLeft,textRect.bottom,textPaint);
        canvas.drawLine(lineRight,textRect.top,lineRight,textRect.bottom,textPaint);
        textPaint.setStrokeWidth(oldWidth);
    }
}
