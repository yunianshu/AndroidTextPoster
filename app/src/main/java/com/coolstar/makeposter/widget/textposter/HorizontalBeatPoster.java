package com.coolstar.makeposter.widget.textposter;

import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by 纪广兴 on 2016/1/29.
 */
public class HorizontalBeatPoster extends HorizontalPoster {



    private static final int COL_SPACE = 10;
    @Override
    public void updateTextRect(int parentWidth, int parentHeight) {
        Log.d("Poster","------------updateTextRect-----------");
        if(textList!=null&&textList.length>0) {
            if (drawTextList == null) {
                drawTextList = new ArrayList<TxtRowInfo>(128); //默认设置是128个字歌词总字数，提高列表增长效率！
            }
            drawTextList.clear();
            Paint.FontMetrics pfm = textPaint.getFontMetrics();
            txtHeight = pfm.bottom - pfm.top;   //保存现有字号下单个字的高度
            float rowExSpace = txtHeight/2;
            float rowOffet = txtHeight/6;  //字符跳动的偏移量
            maxWidth = 0;     //保存最大高度
            float maxRowWidth = textPaint.measureText("国"); //默认最宽的字符就是中文，数字与字母都比中文窄
            float startLeft = 0;
            float startTop = 0;
            int rowCount = 0;  //默认第一行
            float totalHeight = 0;    //保存总高度
            for (int i = 0; i < textList.length; i++) {
                float[] rawWidths = new float[textList[i].length()];     //保存每一行的每个字符的宽度数组
                textPaint.getTextWidths(textList[i], rawWidths);
                int rawNo = 0;  //记录换行的个数
                for (int j = 0; j < textList[i].length(); j++) {
                    startLeft = (j-rawNo)*(maxRowWidth+COL_SPACE); //每个字符占同样宽度的空间，但根据字符本身宽度做居中处理
                    startTop = (txtHeight + (-pfm.top)) * rowCount+ (-pfm.top);//因为top为负数，则要先取负再加上
                    if ((startLeft + maxRowWidth) > parentWidth) { //如果测试发现下一个字符超过边界，则换行
                        rawNo = j;
                        udpateRowTotalWidth(rowCount,startLeft+maxRowWidth);
                        rowCount++;
                        startTop = (txtHeight + (-pfm.top)) * rowCount+ (-pfm.top);//因为top为负数，则要先取负再加上
                        startLeft = (j-rawNo)*(maxRowWidth+COL_SPACE); //换列要重新计算一下
                    }
                    if(j%2==0){
                        startTop += rowOffet;
                    }else{
                        startTop -= rowOffet;
                    }
                    maxWidth = Math.max(maxWidth, (startLeft +maxRowWidth)); //保存最宽的行长度，用于更新字符Rect

                    TxtRowInfo item = new TxtRowInfo();
                    item.rowText = String.valueOf(textList[i].charAt(j));
                    item.startLeft = startLeft + (maxRowWidth - rawWidths[j]) / 2; //把窄的字符要居中，所以这里要处理起点
                    item.rowIndex = rowCount;   //保存此字所在列位置，用于列对齐时更新起点做条件
                    item.startTop = startTop;
                    drawTextList.add(item);
                }
                udpateRowTotalWidth(rowCount,startLeft+maxRowWidth);
                rowCount++;
            }
            totalHeight = (txtHeight + (-pfm.top)) * rowCount; //此处要加上最下面文字的bottom值
            updateDrawTextLeft(maxWidth);
            textRect.set(0, 0, maxWidth, totalHeight);  //保存文字显示区域
        }else{
            if(drawTextList!=null){
                drawTextList.clear();
                drawTextList = null;
            }
        }
    }

    private void udpateRowTotalWidth(int rowCount, float rowWidth) {
        if(drawTextList!=null){
            for(int i=0;i<drawTextList.size();i++){
                drawTextList.get(i).updateRowWidth(rowCount,rowWidth);
            }
        }
    }
}
