package com.coolstar.makeposter.widget.textposter;

import android.graphics.Canvas;

/**
 * 上下有折线效果的版式
 * Created by 纪广兴 on 2016/2/17.
 */
public class HorizontalPolylinePoster extends HorizontalSymbolPoster {
    @Override
    void drawSymbols(Canvas canvas) {
        if(noTextFlag){
            return;
        }
        float lineWidth = textRect.width()-margin_Left-margin_Right-singleTxtWidth; //线宽度是去掉左右空隙后，二边各去掉半个字符宽。
        float lineLeft = margin_Left+singleTxtWidth/2.0f;
        float lineRight = lineLeft + lineWidth;
        float lineTop = txtHeight/2;
        float lineHeight = Math.min(txtHeight-lineTop,margin_Top);
        float strokeWidth = txtHeight/10f;
        float strokeOffet = strokeWidth/2f;
        float oldStrokeWidth = textPaint.getStrokeWidth();
        textPaint.setStrokeWidth(strokeWidth);  //角的线宽度与文字的高度关联
        float[] linePts = new float[24];
        linePts[0] = lineLeft;
        linePts[1] = lineTop+lineHeight;
        linePts[2] = lineLeft;
        linePts[3] = lineTop;

        linePts[4] = lineLeft-strokeOffet;
        linePts[5] = lineTop;
        linePts[6] = lineRight+strokeOffet;
        linePts[7] = lineTop;

        linePts[8] = lineRight;
        linePts[9] = lineTop;
        linePts[10] = lineRight;
        linePts[11] = lineTop+lineHeight;

        linePts[12] = lineLeft;
        linePts[13] = textRect.height()-lineTop;
        linePts[14] = lineLeft;
        linePts[15] = textRect.height()-lineHeight-lineTop;

        linePts[16] = lineLeft-strokeOffet;
        linePts[17] = textRect.height()-lineTop;
        linePts[18] = lineRight+strokeOffet;
        linePts[19] = textRect.height()-lineTop;

        linePts[20] = lineRight;
        linePts[21] = textRect.height()-lineHeight-lineTop;
        linePts[22] = lineRight;
        linePts[23] = textRect.height()-lineTop;
        canvas.drawLines(linePts,textPaint);     //使用这种画法取代分段画线,可以有阴影时,防止接头处有阴影
        textPaint.setStrokeWidth(oldStrokeWidth);
    }

    @Override
    void initMarginBySymbolType() {
        txtHeight = textPaint.getFontMetrics().bottom - textPaint.getFontMetrics().top;
        singleTxtWidth = textPaint.measureText("国");
        margin_Left = MARGIN_DEFAULT;
        margin_Top = (int)txtHeight;
        margin_Right = MARGIN_DEFAULT;
        margin_Bottom = (int)txtHeight;
    }
}
