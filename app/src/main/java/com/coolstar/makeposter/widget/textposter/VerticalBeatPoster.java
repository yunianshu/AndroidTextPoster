package com.coolstar.makeposter.widget.textposter;

import java.util.ArrayList;

/**
 * Created by 纪广兴 on 2016/1/29.
 */
public class VerticalBeatPoster extends VerticalPoster {
    @Override
    public void updateTextRect(int parentWidth, int parentHeight) {
        if(drawTextList==null){
            drawTextList = new ArrayList<TxtRowInfo>(128); //默认设置是歌词行数2倍，不能每个都换行吧！
        }
        float maxRowWidth = textPaint.measureText("国");
        COL_SPACE = (int)maxRowWidth;
        super.updateTextRect(parentWidth, parentHeight);
        if(drawTextList!=null){  //当用户设置的就是空字符串时,这里不要处理了
            for(int i=0;i<drawTextList.size();i++){
                if(i%2==0){
                    drawTextList.get(i).leftOffet = maxRowWidth/6;
                }else{
                    drawTextList.get(i).leftOffet = -maxRowWidth/6;
                }
            }
            textRect.left -=txtHeight/3;
            textRect.right+=txtHeight/3;  //扩大文字区域
        }
    }
}
