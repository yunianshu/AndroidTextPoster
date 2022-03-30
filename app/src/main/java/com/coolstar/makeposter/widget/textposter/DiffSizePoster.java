package com.coolstar.makeposter.widget.textposter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.TextUtils;

import com.coolstar.makeposter.utils.LangUtils;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * 固定宽度行字符大小不同效果排版
 * 默认为屏幕1/3宽
 * Created by 纪广兴 on 2016/2/1.
 */
public class DiffSizePoster implements IPoster {
    protected static final float FIX_WIDTH_COL = 2.5f;  //默认固定宽度为屏幕的1/3
    protected  static final int HEIGHT_SPACE = 10;
    private static final float BOX_WIDTH = 3;

    protected RectF textRect;     //保存文字设置字号后的大小值，此值与文字、字号二个属性相关，与其它颜色等无关
    protected Paint textPaint;    //保存文字颜色、字体、样式、字号等信息
    protected Layout.Alignment textAlign = Layout.Alignment.ALIGN_NORMAL;    //文字对齐方式
    protected String[] textList;      //设置进来的原始歌词数组
    protected float txtHeight;        //保存当前字体、字号下，字的真正高度
    protected float maxWidth;
    protected Paint boxPaint;   //文字外框用画笔
    protected boolean mShadowEnabled;
    protected ArrayList<DiffRowInfo> drawTextList;
    //==================================================================================

    public DiffSizePoster() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(20);
        textPaint.setShadowLayer(2,2,2, Color.argb(160,0,0,0));
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.LEFT);

        textRect = new RectF();

        boxPaint = new Paint();
        boxPaint.setColor(Color.WHITE);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(BOX_WIDTH);
    }

    protected String[] splitSpaceChar(String[] texts) {
        if(false == LangUtils.isChinese(Arrays.toString(texts))){
            return texts;   //英文歌词不按空格分行
        }
        ArrayList<String> tmpList = null;
        if(texts!=null&&texts.length>0){
            //这里要处理一下空格问题，如果是空格，转为换行。
            tmpList = new ArrayList<String>();
            for(int i=0;i<texts.length;i++){
                String[] tmpArr = texts[i].split(" ");
                for(int j=0;j<tmpArr.length;j++){
                    String rowStr = tmpArr[j].trim();
                    if(!TextUtils.isEmpty(rowStr)){
                        if(rowStr.length()>9){  //如果一行文字超过或等于10个，则强制换行一下
                            tmpList.add(rowStr.substring(0,6));
                            tmpList.add(rowStr.substring(6));
                        }else{
                            tmpList.add(tmpArr[j]);
                        }
                    }
                }
            }
        }
        if(tmpList !=null){
            String[] retArr = new String[tmpList.size()];
            tmpList.toArray(retArr);
            return retArr;
        }else{
            return texts;
        }
    }
    //==================================================================================
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

    @Override
    public void setTexts(String[] texts, int parentWidth, int parentHeight) {
        if(texts==null){
            textList = null;
        }else{
            texts = splitSpaceChar(texts);
            textList = texts;
        }
        updateTextRect((int)(parentWidth/FIX_WIDTH_COL),parentHeight);
    }

    @Override
    public void setTextColor(int color) {
        textPaint.setColor(color);
        boxPaint.setColor(color);
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
//        textPaint.setTextSize(size);
//        updateTextRect((int)(parentWidth/FIX_WIDTH_COL),parentHeight);
    }

    @Override
    public void setTextAlign(Layout.Alignment align) {
        textAlign = align;
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
                DiffRowInfo row = drawTextList.get(i);
                textPaint.setTextSize(row.fontSize);
                canvas.drawText(row.rowText,row.startLeft,row.startTop,textPaint);
            }
            canvas.drawRect(textRect,boxPaint);
        }
    }

    @Override
    public RectF getTextRect() {
        return textRect;
    }

    @Override
    public void updateTextRect(int parentWidth, int parentHeight) {
        if(textList!=null&&textList.length>0){
            if(drawTextList==null){
                drawTextList = new ArrayList<DiffRowInfo>();
            }
            drawTextList.clear();
            float totalHeight = 0;
            for(int i=0;i<textList.length;i++){
                float curTextSize = textPaint.getTextSize();
                float rowTotalWidth = textPaint.measureText(textList[i]);
                if(rowTotalWidth>parentWidth){
                    //缩小字号，直到小于父控件
                    while (rowTotalWidth>parentWidth){
                        textPaint.setTextSize(--curTextSize);
                        rowTotalWidth = textPaint.measureText(textList[i]);
                    }

                }else{
                    //扩大字号，直到大于父控件，然后获取前一字号值
                    while (rowTotalWidth<parentWidth){
                        textPaint.setTextSize(++curTextSize);
                        rowTotalWidth = textPaint.measureText(textList[i]);
                    }
                    curTextSize--; //大于时才循环停止，所以这里要再减去最后大于的字号值，还原到小于宽度范围内
                    textPaint.setTextSize(curTextSize);
                    rowTotalWidth = textPaint.measureText(textList[i]);
                }
//                float maxRowWidth = textPaint.measureText("国"); //保存每个汉字标准的宽度
                float maxRowHeight = textPaint.getFontMetrics().bottom - textPaint.getFontMetrics().top;
                float totoalWidth = 0;
                if(totalHeight<0.1f){
                    totalHeight += (maxRowHeight-(textPaint.getFontMetrics().bottom)); //保存上面所有行高，用于本行的top定位,因为文字的baseline在文字中下部，所以这里现减去bottom值，把y定位到baseline上
                }else{
                    totalHeight += (maxRowHeight-(textPaint.getFontMetrics().bottom)+HEIGHT_SPACE); //保存上面所有行高，用于本行的top定位,因为文字的baseline在文字中下部，所以这里现减去bottom值，把y定位到baseline上
                }
                if(totalHeight>parentHeight){
                    break;
                }
                float[] rowWidths = new float[textList[i].length()]; //保存每个字符的宽度，
                textPaint.getTextWidths(textList[i],rowWidths);
                float leftOffet = (parentWidth-rowTotalWidth)/2; //保存宽度差的一半，做为行首偏移量，实现居中效果
                for(int j=0;j<textList[i].length();j++){
                    DiffRowInfo item = new DiffRowInfo();
                    item.rowText = ""+textList[i].charAt(j);
                    item.startTop =totalHeight; //
                    item.startLeft = leftOffet + totoalWidth;
                    item.fontSize = curTextSize;
                    drawTextList.add(item);

                    totoalWidth += rowWidths[j];
                }
            }
            totalHeight = Math.min(totalHeight+textPaint.getFontMetrics().bottom+HEIGHT_SPACE,parentHeight);  //最高不能高过父控件高度
            textRect.set(0,0,parentWidth,totalHeight);
        }else{
            if(drawTextList!=null){
                drawTextList.clear();
                drawTextList = null;
            }
        }
    }
}
