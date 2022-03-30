package com.coolstar.makeposter.widget.textposter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Layout;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by 纪广兴 on 2016/1/28.
 */
public class VerticalPoster implements IPoster {
    protected static  int COL_SPACE = 3;      // 列间距设置
    protected static  int ROW_SPACE = 1;      // 行间距设置

    protected RectF textRect;     //保存文字设置字号后的大小值，此值与文字、字号二个属性相关，与其它颜色等无关
    protected Paint textPaint;    //保存文字颜色、字体、样式、字号等信息
    protected Layout.Alignment textAlign = Layout.Alignment.ALIGN_NORMAL;    //文字对齐方式
    protected String[] textList;      //设置进来的原始歌词数组
    protected ArrayList<TxtRowInfo> drawTextList;
    protected float txtHeight;        //保存当前字体、字号下，字的真正高度
    protected float maxHeight;
    protected Paint tmpRectPaint;
    protected boolean mShadowEnabled;

    public VerticalPoster() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setShadowLayer(2,2,2, Color.argb(160,0,0,0));
        textPaint.setTextSize(36);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.LEFT);

        textRect = new RectF();

        tmpRectPaint = new Paint();
        tmpRectPaint.setColor(Color.argb(128,88,88,88));
        tmpRectPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void setTexts(String[] texts, int parentWidth, int parentHeight) {
        if(texts==null){
            textList = null;
        }else{
            textList = texts;
        }
        updateTextRect(parentWidth,parentHeight);
    }

    @Override
    public void updateTextRect(int parentWidth, int parentHeight) {
        Log.d("Poster","------------updateTextRect-----------");
        if(textList!=null&&textList.length>0){
            if(drawTextList==null){
                drawTextList = new ArrayList<TxtRowInfo>(128); //默认设置是歌词行数2倍，不能每个都换行吧！
            }
            drawTextList.clear();
            Paint.FontMetrics pfm = textPaint.getFontMetrics();
            txtHeight = pfm.bottom-pfm.top;   //保存现有字号下单个字的高度
            maxHeight = 0;     //保存最大高度
            float maxRowWidth = textPaint.measureText("国"); //默认最宽的字符就是中文，数字与字母都比中文窄
            float startLeft = 0;
            float startTop =ROW_SPACE+(-pfm.top);
            int colCount = 1;  //默认肯定有第一列
            float totalWidth=0;    //保存总高度
            for(int i=0;i<textList.length;i++){
                float[] rawWidths = new float[textList[i].length()];     //保存每一行的每个字符的宽度数组
                textPaint.getTextWidths(textList[i],rawWidths);
                int rawNo = 0;  //记录换行的个数
                for(int j=0;j<textList[i].length();j++){
                    startLeft = parentWidth - colCount*(maxRowWidth+COL_SPACE);
                    startTop  = ROW_SPACE+(-pfm.top) + (j-rawNo)*txtHeight;//因为top为负数，则要先取负再加上
                    if((startTop+txtHeight)>parentHeight){ //如果测试发现下一个字符超过边界，则换行
                        rawNo = j;  //保存换行的这个字符index
                        updateColHeight(colCount,(startTop+pfm.bottom+ROW_SPACE));
                        colCount++;
                        startTop = ROW_SPACE+(-pfm.top);
                        startLeft = parentWidth - colCount*(maxRowWidth+COL_SPACE); //换列要重新计算一下
                    }
                    maxHeight = Math.max(maxHeight,(startTop+pfm.bottom+ROW_SPACE)); //保存最高的列，用于更新字符Rect

                    TxtRowInfo item = new TxtRowInfo();
                    item.rowText = String.valueOf(textList[i].charAt(j));
                    item.startLeft = startLeft+(maxRowWidth-rawWidths[j])/2; //把窄的字符要居中，所以这里要处理起点
                    item.startTop = startTop;
                    item.colIndex = colCount;   //保存此字所在列位置，用于列对齐时更新起点做条件
                    item.rowWidth = rawWidths[j];
                    drawTextList.add(item);
                }
                updateColHeight(colCount,(startTop+pfm.bottom+ROW_SPACE));
                colCount++;
            }
            totalWidth = (colCount-1)*(maxRowWidth+COL_SPACE)+COL_SPACE; //此处要加上最左一行左边的空白区域
            updateDrawTextTop(maxHeight);
            updateVerTextLeft(parentWidth,totalWidth);
            textRect.set(0,0,totalWidth,maxHeight);  //保存文字显示区域
        }else{
            if(drawTextList!=null){
                drawTextList.clear();
                drawTextList = null;
            }
        }
    }

    private void updateVerTextLeft(int parentWidth, float totalWidth) {
        if(drawTextList!=null){
            for(int i=0;i<drawTextList.size();i++){
                drawTextList.get(i).updateLeftForVertical(parentWidth-totalWidth);
            }
        }
    }

    /**
     * 把一列排版后，这里把此列中的所有字符的列高度值填写
     * @param colCount
     * @param colHeight
     */
    private void updateColHeight(int colCount, float colHeight) {
        if(drawTextList!=null){
            for(int i=0;i<drawTextList.size();i++){
                drawTextList.get(i).updateColHeight(colCount,colHeight);
            }
        }
    }

    private void updateDrawTextTop(float maxHeight) {
        if(drawTextList!=null){
            for(int i=0;i<drawTextList.size();i++){
                drawTextList.get(i).updateTopAlign(textAlign,maxHeight); //更新文字行起点坐标
            }
        }
    }

    @Override
    public void setTextColor(int color) {
        textPaint.setColor(color);
        if(mShadowEnabled){
            if(color== Color.BLACK){
                textPaint.setShadowLayer(2,2,2, Color.argb(160,255,255,255));
            }else{
                textPaint.setShadowLayer(2,2,2, Color.argb(160,0,0,0));
            }
        }
    }

    @Override
    public void setTextSize(float size, int parentWidth, int parentHeight) {
        textPaint.setTextSize(size);
        updateTextRect(parentWidth,parentHeight);
    }

    @Override
    public void setTextAlign(Layout.Alignment align) {
        textAlign = align;
        updateDrawTextTop(maxHeight);
    }

    @Override
    public void setTextFont(Typeface font, int parentWidth, int parentHeight) {
        textPaint.setTypeface(font);
        updateTextRect(parentWidth,parentHeight);
    }

    @Override
    public void setShadow(boolean enabled) {
        mShadowEnabled = enabled;
        if(enabled){
            if(textPaint.getColor()== Color.BLACK){
                textPaint.setShadowLayer(2,2,2, Color.argb(160,255,255,255));
            }else{
                textPaint.setShadowLayer(2,2,2, Color.argb(160,0,0,0));
            }
        }else{
            textPaint.clearShadowLayer();
        }
    }

    @Override
    public void onPostDraw(Canvas canvas) {
        if(drawTextList!=null){
            for(int i=0;i<drawTextList.size();i++){
                TxtRowInfo row = drawTextList.get(i);
                canvas.drawText(row.rowText,row.getLeftPosition(),row.getTopPosition(),textPaint);
            }
//            canvas.drawRect(textRect,tmpRectPaint);
        }
    }

    @Override
    public RectF getTextRect() {
        return textRect;
    }


    @Override
    public Layout.Alignment getTextAlign() {
        return textAlign;
    }

    @Override
    public int getTextColor() {
        return textPaint.getColor();
    }

    @Override
    public float getTextSize() {
        return textPaint.getTextSize();
    }

    @Override
    public Typeface getTextFont() {
        return textPaint.getTypeface();
    }

    @Override
    public String[] getTexts() {
        return textList;
    }

    @Override
    public boolean isHasShadow() {
        return mShadowEnabled;
    }

    //=========================================================================================
//    protected static class TxtColInfo{
//
//    }
}
