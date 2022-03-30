package com.coolstar.makeposter.view.posterview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.coolstar.makeposter.R;

/**
 * Created by jiguangxing on 2016/3/15.
 */
public class StyleListAdapter extends RecyclerView.Adapter<StyleListItemHolder> {
    private final LayoutInflater layoutBuilder;

    public void setListener(ListItemEventListener listener) {
        this.listener = listener;
    }

    ListItemEventListener listener;
    Context context;
    private int[] styleArrs={R.mipmap.text_style_chs_1,
            R.mipmap.spic_c1,
            R.mipmap.spic_c2,
            R.mipmap.spic_c3,
            R.mipmap.spic_c4,
            R.mipmap.text_style_chs_4,
            R.mipmap.text_style_chs_6,
            R.mipmap.text_style_chs_8,
            R.mipmap.text_style_chs_9,
            R.mipmap.text_style_chs_11,
            R.mipmap.text_style_chs_14
    };

    public StyleListAdapter(Context context, ListItemEventListener listItemEventListener) {
        this.context = context;
        this.listener = listItemEventListener;
        this.layoutBuilder = LayoutInflater.from(context);
    }

    @Override
    public StyleListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StyleListItemHolder(layoutBuilder.inflate(R.layout.rvitem_style,null));
    }

    @Override
    public void onBindViewHolder(StyleListItemHolder holder, int position) {
        holder.updateData(position,styleArrs[position],listener);
    }

    @Override
    public int getItemCount() {
        return styleArrs.length;
    }


    public static interface ListItemEventListener{
        void onItemClick(int postion, View view);
        void onItemLongClick(int postion, View view);
    }
}
