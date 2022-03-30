package com.coolstar.makeposter.widget.textposter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;

import com.coolstar.makeposter.utils.LangUtils;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by 纪广兴 on 2016/1/28.
 */
public class HorizontalPoster implements IPoster {
    protected int ROW_SPACE = 6;      // 行间距设置

    protected int margin_Left = 0;
    protected int margin_Top = 0;
    protected int margin_Right = 0;
    protected int margin_Bottom = 0;

    protected RectF textRect;     //保存文字设置字号后的大小值，此值与文字、字号二个属性相关，与其它颜色等无关
    protected Paint textPaint;    //保存文字颜色、字体、样式、字号等信息
    protected Layout.Alignment textAlign = Layout.Alignment.ALIGN_NORMAL;    //文字对齐方式
    protected String[] textList;      //设置进来的原始歌词数组
    protected ArrayList<TxtRowInfo> drawTextList;
    protected float txtHeight;        //保存当前字体、字号下，字的真正高度
    protected float maxWidth;
    protected Paint tmpRectPaint;
    protected boolean mShadowEnabled;

    public HorizontalPoster() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(36);
        textPaint.setShadowLayer(2,2,2, Color.argb(160,0,0,0));
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.LEFT);

        textRect = new RectF();

        tmpRectPaint = new Paint();
        tmpRectPaint.setColor(Color.argb(128,88,88,88));
        tmpRectPaint.setStyle(Paint.Style.FILL);
    }

    protected void setMargin(int parentWidth,int parentHeight,int left,int top,int right,int bottom){
        margin_Left = left;
        margin_Top = top;
        margin_Right = right;
        margin_Bottom = bottom;
        updateTextRect(parentWidth,parentHeight);
    }

    /**
     * 此函数给子类一个机会，
     * 让子类控制是否需要保留歌词中的空格*默认实现是，有中文歌词去掉，全英文歌词保留
     * @return true:表示要保留空格，false:表示要去掉中文中的空格
     */
    protected boolean isNeedSaveSpace(String[] texts){
        return false == LangUtils.isChinese(Arrays.toString(texts));
    }

    protected String[] splitSpaceChar(String[] texts) {
        if(isNeedSaveSpace(texts)){
            return texts;   //英文歌词不按空格分行,或需要强制保留空格的中文歌词
        }
        ArrayList<String> tmpList = null;
        if(texts!=null&&texts.length>0){
            //这里要处理一下空格问题，如果是空格，转为换行。
            tmpList = new ArrayList<String>();
            for(int i=0;i<texts.length;i++){
                String[] tmpArr = texts[i].split(" ");
                for(int j=0;j<tmpArr.length;j++){
                    if(!TextUtils.isEmpty(tmpArr[j].trim())){
                        tmpList.add(tmpArr[j].trim());
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

    @Override
    public void setTexts(String[] texts, int parentWidth, int parentHeight) {
        if(texts==null){
            textList = null;
        }else{
            textList = splitSpaceChar(texts);
        }
        updateTextRect(parentWidth,parentHeight);
    }

    @Override
    public void updateTextRect(int parentWidth, int parentHeight) {
        Log.d("Poster","------------updateTextRect-----------");
        if(textList!=null&&textList.length>0){
            if(drawTextList==null){
                drawTextList = new ArrayList<TxtRowInfo>(textList.length*2); //默认设置是歌词行数2倍，不能每个都换行吧！
            }
            drawTextList.clear();
            Paint.FontMetrics pfm = textPaint.getFontMetrics();
            txtHeight = pfm.descent-pfm.ascent;   //保存现有字号下单个字的高度
            maxWidth = 0;     //保存最大宽度
            float totalHeight=-pfm.top;    //保存总高度
            totalHeight+=margin_Top;  //加上上面的空隙
            float rawWidth;     //保存每一行的宽度
            for(int i=0;i<textList.length;i++){
                rawWidth = textPaint.measureText(textList[i]);
                if(rawWidth>(parentWidth-margin_Left-margin_Right)){
                    float[] chayWidths = new float[textList[i].length()];
                    textPaint.getTextWidths(textList[i],0,textList[i].length(),chayWidths);
                    float rowTmpWidth = 0f;
                    int startIdx = 0;
                    for(int j=0;j<chayWidths.length;j++){
                        if(rowTmpWidth+chayWidths[j]>(parentWidth-margin_Left-margin_Right)){ //如果加上当前的字符，超过父控件宽度了。则不加当前控件，换行
                            TxtRowInfo item = new TxtRowInfo();
                            item.rowText = textList[i].substring(startIdx,j);
                            item.rowWidth = rowTmpWidth;
                            item.startTop = totalHeight;
                            drawTextList.add(item);
                            maxWidth = Math.max(maxWidth,rowTmpWidth); //得到最大宽度

                            totalHeight+=(txtHeight+ROW_SPACE); //增加行高度
                            rowTmpWidth = chayWidths[j]; //重新计算新行宽度
                            startIdx = j;//保存换行的起始字符
                        }else{
                            rowTmpWidth+=chayWidths[j];
                        }
                    }
                    TxtRowInfo item = new TxtRowInfo();
                    item.rowText = textList[i].substring(startIdx);
                    item.rowWidth = rowTmpWidth;
                    item.startTop = totalHeight;
                    drawTextList.add(item);
                    maxWidth = Math.max(maxWidth,rowTmpWidth); //得到最大宽度
                    totalHeight+=(txtHeight+ROW_SPACE); //增加行高度
                }else{
                    TxtRowInfo info = new TxtRowInfo();
                    info.rowText = textList[i];
                    info.rowWidth = rawWidth;
                    info.startTop = totalHeight;
                    drawTextList.add(info);
                    totalHeight +=(txtHeight+ROW_SPACE);
                    maxWidth = Math.max(maxWidth,rawWidth); //得到最大宽度
                }

            }
            totalHeight = totalHeight+pfm.ascent-ROW_SPACE; //此处要减去开始直接设置的文字top值与ascent的差值，保持上面文字边缘空白相同
            totalHeight += margin_Bottom;  //加上下面要保留的空隙
            updateDrawTextLeft(maxWidth);
            textRect.set(0,0,margin_Left+maxWidth+margin_Right,totalHeight);  //保存文字显示区域
        }else{
            if(drawTextList!=null){
                drawTextList.clear();
                drawTextList = null;
            }
        }
    }

    protected void updateDrawTextLeft(float maxWidth) {
        if(drawTextList!=null){
            float rectWidth = margin_Left + maxWidth + margin_Right; //使用文字的最宽长度加上二边的空隙，转成区域的宽度，做为参数传到对齐函数中
            for(int i=0;i<drawTextList.size();i++){
                drawTextList.get(i).updateLeftAlign(textAlign,rectWidth,margin_Left,margin_Right); //更新文字行起点坐标
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
        updateDrawTextLeft(maxWidth);
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
}
