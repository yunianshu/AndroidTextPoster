package com.coolstar.makeposter.view.mainview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.coolstar.makeposter.R;
import com.coolstar.makeposter.beans.PictureInfo;

import java.util.List;

/**
 * Created by jiguangxing on 2016/3/4.
 */
public class MainPicAdapter extends RecyclerView.Adapter<MainPicHolder> {
    private List<PictureInfo> mItems;
    private LayoutInflater layoutBuilder;

    public MainPicAdapter(Context context, OnItemClickListener listener) {
        this.listener = listener;
        layoutBuilder = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setPicList(List<PictureInfo> list) {
        mItems = list;
    }

    public static interface OnItemClickListener{
        void onItemClick(int postion, PictureInfo item,View view);
        void onItemLongClick(int postion, PictureInfo item,View view);
    }

    private OnItemClickListener listener;


    @Override
    public MainPicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainPicHolder(layoutBuilder.inflate(R.layout.rvitem_main,null));
    }

    @Override
    public void onBindViewHolder(MainPicHolder holder, int position) {
        if(mItems!=null){
            PictureInfo item = mItems.get(position);
            holder.updateData(position,item);
            holder.setEventListener(item,listener);
        }
    }

    @Override
    public int getItemCount() {
        if(mItems!=null){
            return mItems.size();
        }
        return 0;
    }
}
