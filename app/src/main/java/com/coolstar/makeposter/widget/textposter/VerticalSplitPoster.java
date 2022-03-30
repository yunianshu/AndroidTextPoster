package com.coolstar.makeposter.widget.textposter;

/**
 * Created by 纪广兴 on 2016/1/29.
 */
public class VerticalSplitPoster extends VerticalPoster {
    @Override
    public void setTexts(String[] texts, int parentWidth, int parentHeight) {
        if(texts==null){
            textList = null;
        }else{
            textList = new String[texts.length];
            StringBuilder sb = new StringBuilder(128);
            for(int i=0;i<texts.length;i++){
                sb.setLength(0);
                for(int j=0;j<texts[i].length();j++){
                    if(j==texts[i].length()-1){
                        sb.append(texts[i].charAt(j));
                    }else{
                        sb.append(texts[i].charAt(j)).append("-");
                    }
                }
                textList[i] = sb.toString();
            }
        }
        updateTextRect(parentWidth,parentHeight);
    }
}
