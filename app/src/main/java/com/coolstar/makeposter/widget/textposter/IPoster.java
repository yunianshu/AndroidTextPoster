package com.coolstar.makeposter.widget.textposter;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Layout;

/**
 * 排版通用接口
 * 用于控件更换不同排版对象方便
 * Created by 纪广兴 on 2016/1/22.
 */
public interface IPoster {
    Layout.Alignment getTextAlign();
    int getTextColor();
    float getTextSize();
    Typeface getTextFont();
    String[] getTexts();
    boolean isHasShadow();
    void setTexts(String[] texts, int parentWidth, int parentHeight); //此处需要注意，不能通过poster的对象直接设置text，要把poster安装到textdrawer后，通过textdrawer的setTexts设置内容
    void setTextColor(int color);
    void setTextSize(float size, int parentWidth, int parentHeight);
    void setTextAlign(Layout.Alignment align);
    void setTextFont(Typeface font, int parentWidth, int parentHeight);
    void setShadow(boolean enabled);
    void onPostDraw(Canvas canvas);
    RectF getTextRect();  //获取文字显示区域，用于初始定位文字是居中时，mScrollX,mScrollY如何设置相关值
    void updateTextRect(int parentWidth, int parentHeight);
}
