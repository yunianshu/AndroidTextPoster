package com.coolstar.makeposter.view.mainview;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.coolstar.makeposter.R;
import com.coolstar.makeposter.beans.PictureInfo;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by jiguangxing on 2016/3/4.
 */
public class MainPicHolder extends RecyclerView.ViewHolder {
    private final View.OnClickListener picClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            if(relationListener!=null){
                relationListener.onItemClick(relationPosition,relationItem,v);
            }
        }
    };
    private final View.OnLongClickListener picLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if(relationListener!=null){
                relationListener.onItemLongClick(relationPosition,relationItem,v);
                return true;
            }
            return false;
        }
    };
    ImageView imgPicture;
    TextView tvName;
    private MainPicAdapter.OnItemClickListener relationListener;
    private PictureInfo relationItem;
    private int relationPosition;

    public MainPicHolder(View itemView) {
        super(itemView);
        imgPicture = (ImageView) itemView.findViewById(R.id.rvitem_picture);
        imgPicture.setOnClickListener(picClickListener);
        imgPicture.setLongClickable(true);
        imgPicture.setOnLongClickListener(picLongClickListener);
        tvName = (TextView)itemView.findViewById(R.id.rvitem_name);
    }

    public void updateData(int position,PictureInfo item) {
        relationPosition = position;
        if(TextUtils.isEmpty(item.picName)){
            tvName.setText("");
        }else{
            tvName.setText(item.picName);
        }
        Picasso.get().load(new File(item.fileName)).placeholder(R.mipmap.rvitem_place).into(imgPicture);
    }

    public void setEventListener(PictureInfo item, MainPicAdapter.OnItemClickListener listener) {
        relationItem = item;
        relationListener = listener;
    }
}
