package com.coolstar.makeposter.widget.textposter;

import android.graphics.Canvas;
import android.text.TextUtils;

import java.util.Arrays;

/**
 * 在文字外围，留空间画符号排版抽象类
 * Created by 纪广兴 on 2016/2/14.
 */
public abstract class HorizontalSymbolPoster extends HorizontalPoster {
    public static final int MARGIN_DEFAULT = 8;  //默认的边距空隙值

    protected float singleTxtWidth = 0f;
    protected boolean noTextFlag = true;  //判断是否有真正输出的歌词文字,没有时,装饰符号也不要画出来

    @Override
    public void setTexts(String[] texts, int parentWidth, int parentHeight) {
        if(texts==null){
            noTextFlag = true; //当为null时,使用deepToString也会返回一个"null"字符串,不是我想要的结果
        }else{
            String txtContent = Arrays.deepToString(texts);
            noTextFlag = TextUtils.isEmpty(txtContent.trim()); //如果内容为空,则标识出来.画符号时判断
        }
        super.setTexts(texts, parentWidth, parentHeight);
    }

    public HorizontalSymbolPoster() {
        super();
        initMarginBySymbolType();
    }

    @Override
    public void onPostDraw(Canvas canvas) {
        super.onPostDraw(canvas);
        drawSymbols(canvas);
    }

    @Override
    public void setTextSize(float size, int parentWidth, int parentHeight) {
        textPaint.setTextSize(size);
        initMarginBySymbolType();
        super.setTextSize(size, parentWidth, parentHeight);
    }
    //＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
    abstract void drawSymbols(Canvas canvas); //画出装饰符号
    abstract void initMarginBySymbolType(); //根据符号类型，增加文字四周不同的宽度空白
}
