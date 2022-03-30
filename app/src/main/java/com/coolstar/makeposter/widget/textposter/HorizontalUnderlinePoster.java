package com.coolstar.makeposter.widget.textposter;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 带下划线的水平排版
 * Created by 纪广兴 on 2016/2/14.
 */
public class HorizontalUnderlinePoster extends HorizontalPoster {

    @Override
    public void setTexts(String[] texts, int parentWidth, int parentHeight) {
        ROW_SPACE = (int) (txtHeight/2f);
        super.setTexts(texts, parentWidth, parentHeight);
    }

    @Override
    public void setTextSize(float size, int parentWidth, int parentHeight) {
        textPaint.setTextSize(size);
        Paint.FontMetrics pfm = textPaint.getFontMetrics();
        txtHeight = pfm.descent-pfm.ascent;   //保存现有字号下单个字的高度
        ROW_SPACE = (int) (txtHeight/2f);
        super.setTextSize(size, parentWidth, parentHeight);
    }

    @Override
    public void updateTextRect(int parentWidth, int parentHeight) {
        ROW_SPACE = (int) (txtHeight/2f);
        super.updateTextRect(parentWidth, parentHeight);
    }

    @Override
    public void onPostDraw(Canvas canvas) {
        if(drawTextList!=null){
            float oldStrokeWidth = textPaint.getStrokeWidth();
            float lineStrokeWidth = ROW_SPACE/6;
            float lineTopOffet = textPaint.getFontMetrics().bottom + ROW_SPACE/2.0f; //计算文字绘制基准y值与行间距中心的偏移量
            textPaint.setStrokeWidth(lineStrokeWidth);
            for(int i=0;i<drawTextList.size();i++){
                TxtRowInfo row = drawTextList.get(i);
                canvas.drawText(row.rowText,row.getLeftPosition(),row.getTopPosition(),textPaint);
                canvas.drawLine(row.getLeftPosition(),row.getTopPosition()+lineTopOffet,row.getLeftPosition()+row.rowWidth,row.getTopPosition()+lineTopOffet,textPaint);
            }
            textPaint.setStrokeWidth(oldStrokeWidth);
//            canvas.drawRect(textRect,tmpRectPaint);
        }
    }
}
