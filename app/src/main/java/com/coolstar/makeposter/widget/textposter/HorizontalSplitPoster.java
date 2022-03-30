package com.coolstar.makeposter.widget.textposter;

/**
 * Created by 纪广兴 on 2016/1/29.
 */
public class HorizontalSplitPoster extends HorizontalPoster {

    @Override
    protected boolean isNeedSaveSpace(String[] texts){
        return false;
    }

    @Override
    public void setTexts(String[] texts, int parentWidth, int parentHeight) {
        if(texts==null){
            textList = null;
        }else{
            texts = splitSpaceChar(texts);  //把空格先转为空行，如果有空格，会重新创建数组保存返回，如果没有，直接返回传入参数
            if(textList==null){
                textList = new String[texts.length];
            }
            StringBuilder sb = new StringBuilder(128);
            for(int i=0;i<texts.length;i++){
                sb.setLength(0);
                for(int j=0;j<texts[i].length();j++){
                    if(j==texts[i].length()-1){
                        sb.append(texts[i].charAt(j));
                    }else{
                        sb.append(texts[i].charAt(j)).append("/");
                    }
                }
                textList[i] = sb.toString();
            }
        }
        updateTextRect(parentWidth,parentHeight);
    }
}
