package com.coolstar.makeposter.widget.textposter;

import android.graphics.Canvas;

/**
 * 左右各有一个字符宽度空白，前后各画一个角标类型
 * Created by 纪广兴 on 2016/2/17.
 */
public class HorizontalCornerPoster extends HorizontalSymbolPoster {
    @Override
    void drawSymbols(Canvas canvas) {
        if(noTextFlag){
            return;
        }
        float oldStrokeWidth = textPaint.getStrokeWidth();
        float strokeOffet = txtHeight/10.0f/2;
        float lineWidth = Math.min(margin_Left,txtHeight)-strokeOffet-strokeOffet;
        textPaint.setStrokeWidth(txtHeight/10.0f);  //角的线宽度与文字的高度关联
        float[] linePts = new float[24];
        linePts[0] = strokeOffet;
        linePts[1] = margin_Top+lineWidth;
        linePts[2] = strokeOffet;
        linePts[3] = margin_Top;

        linePts[4] = 0;
        linePts[5] = margin_Top+strokeOffet;
        linePts[6] = lineWidth;
        linePts[7] = margin_Top+strokeOffet;

        lineWidth = Math.min(margin_Right,txtHeight)-strokeOffet-strokeOffet;

        linePts[8] = textRect.width()-lineWidth;
        linePts[9] = textRect.height()-margin_Bottom-strokeOffet;
        linePts[10] = textRect.width();
        linePts[11] = textRect.height()-margin_Bottom-strokeOffet;

        linePts[12] = textRect.width()-strokeOffet;
        linePts[13] = textRect.height()-margin_Bottom;
        linePts[14] = textRect.width()-strokeOffet;
        linePts[15] = textRect.height()-lineWidth-margin_Bottom;

        canvas.drawLines(linePts,textPaint);     //使用这种画法取代分段画线,可以有阴影时,防止接头处有阴影

        textPaint.setStrokeWidth(oldStrokeWidth);
    }

    @Override
    void initMarginBySymbolType() {
        singleTxtWidth = textPaint.measureText("国");
        margin_Left = (int)(singleTxtWidth+0.5f);
        margin_Top = MARGIN_DEFAULT;
        margin_Right = (int)(singleTxtWidth+0.5f);
        margin_Bottom = MARGIN_DEFAULT;
    }
}
