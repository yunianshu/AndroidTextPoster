package com.coolstar.makeposter.view.posterview;

import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.coolstar.makeposter.R;

/**
 * Created by jiguangxing on 2016/3/15.
 */
public class StyleListItemHolder extends RecyclerView.ViewHolder {
    private final View.OnClickListener imgClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(outListener!=null){
                outListener.onItemClick(position,v);
            }
        }
    };
    StyleListAdapter.ListItemEventListener outListener;  //输出到外面的点击接口
    int position;//保存当前显示的内容索引
    private ImageView imgStyle;
    public StyleListItemHolder(View itemView) {
        super(itemView);
        imgStyle = (ImageView)itemView.findViewById(R.id.poster_item_style_img);
        imgStyle.setOnClickListener(imgClickListener);
    }


    public void updateData(int position,int resId, StyleListAdapter.ListItemEventListener listener) {
        this.position = position;
        this.outListener = listener;
        imgStyle.setImageResource(resId);
    }
}
