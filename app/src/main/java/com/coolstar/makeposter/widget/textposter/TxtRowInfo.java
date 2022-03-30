package com.coolstar.makeposter.widget.textposter;

import android.text.Layout;

/**
 * 文字自绘时记录行信息
 * Created by 纪广兴 on 2016/1/28.
 */
public class TxtRowInfo {
    public String rowText;      //一行歌词字符串，保存不超过屏幕长度的
    public int   rowIndex;      //保存行中每一个字的所在行索引
    public float rowWidth;      //本行字符有效宽度,高度
    public float startLeft;     //本行文字左侧绘制起点
    public float startTop;      //本行文字左侧绘制高度
    public float leftOffet;     //左侧的对齐用偏移量
    public float topOffet;      //竖向对齐用高度偏移量
    public int colIndex;        //保存所在列索引，用于对齐更新列起点位置
    public float colHeight;     //保存所在列的总列长

    public void updateLeftAlign(Layout.Alignment align, float maxWidth, int marginLeft, int marginRight){
        if(align == Layout.Alignment.ALIGN_OPPOSITE){
            leftOffet = (maxWidth -marginRight - rowWidth);
        }else if(align== Layout.Alignment.ALIGN_CENTER){
            leftOffet = (maxWidth-marginLeft-marginRight-rowWidth)/2 + marginLeft;
        }else{
            leftOffet = marginLeft;
        }
        if(leftOffet<0){  //修正一下，有时会得到负值？
            leftOffet = 0;
        }
    }
    public void updateTopAlign(Layout.Alignment align, float maxHeight){
        if(align == Layout.Alignment.ALIGN_OPPOSITE){
            topOffet = (maxHeight - colHeight);

        }else if(align== Layout.Alignment.ALIGN_CENTER){
            topOffet = (maxHeight-colHeight)/2;
        }
        if(topOffet<0){  //修正一下，有时会得到负值？
            topOffet = 0;
        }
    }

    public void updateColHeight(int colIdx, float height) {
        if(colIndex==colIdx){
            colHeight = height;
        }
    }

    public void updateLeftForVertical(float offetX) {
        startLeft -=offetX;
    }

    public float getLeftPosition(){
        return startLeft+leftOffet;
    }
    public float getTopPosition(){
        return startTop+topOffet;
    }

    public void updateRowWidth(int rowCount, float width) {
        if(rowIndex==rowCount){
            rowWidth = width;
        }
    }
}
