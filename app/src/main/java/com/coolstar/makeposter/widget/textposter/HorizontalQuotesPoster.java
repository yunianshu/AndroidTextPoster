package com.coolstar.makeposter.widget.textposter;

import android.graphics.Canvas;

/**
 * 在文字上下增加一对引号排版类
 * Created by 纪广兴 on 2016/2/17.
 */
public class HorizontalQuotesPoster extends HorizontalSymbolPoster {
    @Override
    void drawSymbols(Canvas canvas) {
        if(noTextFlag){
            return;
        }
        float oldFontSize = textPaint.getTextSize();
        float txtOffet = -textPaint.getFontMetrics().top;
        textPaint.setTextSize(oldFontSize+oldFontSize);
        canvas.drawText("“",margin_Left,txtOffet+txtOffet,textPaint);  //高度的定位，因为符号与正文的字号不同，完全是试出来的相对位置，无科学依据。
        canvas.drawText("”",textRect.width()-margin_Right-singleTxtWidth,textRect.height()-txtHeight+txtOffet+txtOffet/2,textPaint);
        textPaint.setTextSize(oldFontSize);
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
