package com.coolstar.makeposter.view.posterview;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;

import androidx.appcompat.widget.AppCompatCheckedTextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coolstar.makeposter.R;
import com.coolstar.makeposter.utils.ScreenUtility;
import com.coolstar.makeposter.widget.RoundedColorView;
import com.coolstar.makeposter.widget.comboSeekBar.ComboSeekBar;
import com.coolstar.makeposter.widget.textposter.DiffSizePoster;
import com.coolstar.makeposter.widget.textposter.HorizontalBeatPoster;
import com.coolstar.makeposter.widget.textposter.HorizontalCornerPoster;
import com.coolstar.makeposter.widget.textposter.HorizontalPolylinePoster;
import com.coolstar.makeposter.widget.textposter.HorizontalPoster;
import com.coolstar.makeposter.widget.textposter.HorizontalQuotesPoster;
import com.coolstar.makeposter.widget.textposter.HorizontalSplitPoster;
import com.coolstar.makeposter.widget.textposter.HorizontalUnderlinePoster;
import com.coolstar.makeposter.widget.textposter.IPoster;
import com.coolstar.makeposter.widget.textposter.VerticalDoublelinePoster;
import com.coolstar.makeposter.widget.textposter.VerticalPoster;
import com.coolstar.makeposter.widget.textposter.VerticalSinglelinePoster;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jiguangxing on 2016/3/10.
 */
public class StyleMenuHelper {
    final IPosterView posterView;
    public StyleMenuHelper(IPosterView posterView) {
        this.posterView = posterView;
    }

    private TextViewHolder textViewHolder;
    private FontViewHolder fontViewHolder;
    private ColorViewHolder colorViewHolder;
    private StyleViewHolder styleViewHolder;

    public void initView(View parentView){
        textViewHolder = new TextViewHolder();
        fontViewHolder = new FontViewHolder();
        colorViewHolder = new ColorViewHolder();
        styleViewHolder = new StyleViewHolder();
        textViewHolder.initView(parentView);
        fontViewHolder.initView(parentView);
        colorViewHolder.initView(parentView);
        styleViewHolder.initView(parentView);
    }

    public void showTextView(){
        textViewHolder.showView();
        fontViewHolder.hideView();
        colorViewHolder.hideView();
        styleViewHolder.hideView();
    }

    public void showFontView(){
        textViewHolder.hideView();
        fontViewHolder.showView();
        colorViewHolder.hideView();
        styleViewHolder.hideView();
    }

    public void showColorView(){
        textViewHolder.hideView();
        fontViewHolder.hideView();
        colorViewHolder.showView();
        styleViewHolder.hideView();
    }

    public void showStyleView(){
        textViewHolder.hideView();
        fontViewHolder.hideView();
        colorViewHolder.hideView();
        styleViewHolder.showView();
    }

    private class TextViewHolder{
        private View mRootView;
        private EditText etInput;
        private View btnAdd;
        private AppCompatCheckedTextView chkLogo;
        private View.OnClickListener btnAddClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputStr = etInput.getText().toString();
                posterView.addPosterText(inputStr);
                etInput.setText("");
            }
        };
        private View.OnClickListener logoClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkLogo.setChecked(!chkLogo.isChecked());
                if(chkLogo.isChecked()){
                    Bitmap logo = BitmapFactory.decodeResource(v.getResources(),R.mipmap.poster_title_logo);
                    posterView.showTitleAndLogo("",logo);
                }else{
                    posterView.showTitleAndLogo("",null);
                }
            }
        };

        public void initView(View parentView) {
            mRootView = parentView.findViewById(R.id.poster_menu_text);
            etInput = (EditText)mRootView.findViewById(R.id.menu_text_edtext);
            btnAdd = mRootView.findViewById(R.id.menu_text_addbtn);
            btnAdd.setOnClickListener(btnAddClickListener);
            chkLogo = (AppCompatCheckedTextView)mRootView.findViewById(R.id.menu_text_logockbox);
            chkLogo.setOnClickListener(logoClickListener);
        }

        public void showView() {
            mRootView.setVisibility(View.VISIBLE);
        }
        public void hideView() {
            mRootView.setVisibility(View.INVISIBLE);
        }
    }

    private class FontViewHolder{
        private View mRootView;
        private  float[] sizeList;
        private AppCompatCheckedTextView chkShadow;
        private ComboSeekBar sizeBar;
        private View btnAlignLeft,btnAlignCenter,btnAlignRight;
        private View.OnClickListener shadowClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkShadow.setChecked(!chkShadow.isChecked());
                posterView.setTextShadow(chkShadow.isChecked());
            }
        };
        private View.OnClickListener alignClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.menu_font_left:
                        posterView.setTextAlign(Layout.Alignment.ALIGN_NORMAL);
                        break;
                    case R.id.menu_font_center:
                        posterView.setTextAlign(Layout.Alignment.ALIGN_CENTER);
                        break;
                    case R.id.menu_font_right:
                        posterView.setTextAlign(Layout.Alignment.ALIGN_OPPOSITE);
                        break;
                }
            }
        };
        private AdapterView.OnItemClickListener sizeItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                posterView.setTextSize(sizeList[position]);
            }
        };

        public void initView(View parentView) {
            mRootView = parentView.findViewById(R.id.poster_menu_font);
            chkShadow = (AppCompatCheckedTextView)mRootView.findViewById(R.id.menu_font_shadowcheckbox);
            chkShadow.setOnClickListener(shadowClickListener);
            btnAlignLeft = mRootView.findViewById(R.id.menu_font_left);
            btnAlignCenter = mRootView.findViewById(R.id.menu_font_center);
            btnAlignRight = mRootView.findViewById(R.id.menu_font_right);
            btnAlignLeft.setOnClickListener(alignClickListener);
            btnAlignCenter.setOnClickListener(alignClickListener);
            btnAlignRight.setOnClickListener(alignClickListener);
            sizeList = new float[5];
            Resources resLoader = parentView.getResources();
            sizeList[0] = resLoader.getDimension(R.dimen.poster_font1);
            sizeList[1] = resLoader.getDimension(R.dimen.poster_font2);
            sizeList[2] = resLoader.getDimension(R.dimen.poster_font3);
            sizeList[3] = resLoader.getDimension(R.dimen.poster_font4);
            sizeList[4] = resLoader.getDimension(R.dimen.poster_font5);
            sizeBar = (ComboSeekBar)mRootView.findViewById(R.id.menu_font_sbar);
            List<String> sizeList = Arrays.asList("","","","","");
            sizeBar.setAdapter(sizeList);
            sizeBar.setSelection(2);  //默认选择中间的字号
            sizeBar.setOnItemClickListener(sizeItemClickListener);
        }

        public void showView() {
            mRootView.setVisibility(View.VISIBLE);
        }
        public void hideView() {
            mRootView.setVisibility(View.INVISIBLE);
        }
    }

    private class ColorViewHolder{
        private View mRootView;
        private GridView colorGrid;
        private ColorGridAdapter adapter;
        private AdapterView.OnItemClickListener colorItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int selColor = adapter.setSelectItem(position);
                posterView.setTextColor(selColor);
            }
        };

        public void initView(View parentView) {
            mRootView = parentView.findViewById(R.id.poster_menu_color);
            colorGrid = (GridView) parentView.findViewById(R.id.menu_color_gridView);
            adapter = new ColorGridAdapter(LayoutInflater.from(parentView.getContext()));
            colorGrid.setAdapter(adapter);
            colorGrid.setOnItemClickListener(colorItemClickListener);
        }

        public void showView() {
            mRootView.setVisibility(View.VISIBLE);
        }
        public void hideView() {
            mRootView.setVisibility(View.INVISIBLE);
        }
    }

    private class StyleViewHolder{
        private View mRootView;
        private View itemSelected;
        private RecyclerView listView;
        private StyleListAdapter adapter;
        private StyleListAdapter.ListItemEventListener listItemEventListener = new StyleListAdapter.ListItemEventListener() {
            @Override
            public void onItemClick(int postion, View view) {
                if(itemSelected!=null){
                    itemSelected.setBackgroundColor(Color.GRAY);
                }
                itemSelected = view;
                itemSelected.setBackgroundColor(Color.parseColor("#FF4081"));
                setStyleIndex(postion);
            }

            @Override
            public void onItemLongClick(int postion, View view) {

            }
        };
        public void setStyleIndex(int idx){
            IPoster curPoster = posterView.getPoster();
            IPoster poster = null;
            switch (idx){
                case 0:
                    if(curPoster.getClass() == HorizontalPoster.class){    //画标准样式
                        return;
                    }
                    poster = new HorizontalPoster();
                    copyFontInfo(poster,curPoster);
                    posterView.setPoster(poster);
                    break;
                case 1:
                    if(curPoster.getClass() == HorizontalCornerPoster.class){    //画角标样式
                        return;
                    }
                    poster = new HorizontalCornerPoster();
                    copyFontInfo(poster,curPoster);
                    posterView.setPoster(poster);
                    break;
                case 2:
                    if(curPoster.getClass() == HorizontalPolylinePoster.class){  //画上现三折线样式
                        return;
                    }
                    poster = new HorizontalPolylinePoster();
                    poster.setTextAlign(Layout.Alignment.ALIGN_CENTER);
                    copyFontInfo(poster,curPoster);
                    posterView.setPoster(poster);
                    break;
                case 3:
                    if(curPoster.getClass() == HorizontalQuotesPoster.class){    //画引号样式
                        return;
                    }
                    poster = new HorizontalQuotesPoster();
                    copyFontInfo(poster,curPoster);
                    posterView.setPoster(poster);
                    break;
                case 4:
                    if(curPoster.getClass() == HorizontalUnderlinePoster.class){ //画行分割线样式
                        return;
                    }
                    poster = new HorizontalUnderlinePoster();
                    copyFontInfo(poster,curPoster);
                    poster.setTextAlign(Layout.Alignment.ALIGN_CENTER);
                    poster.setShadow(true);
                    posterView.setPoster(poster);
                    break;
                case 5:
                    if(curPoster.getClass() == HorizontalSplitPoster.class){        //画带/分割符样式
                        return;
                    }
                    poster = new HorizontalSplitPoster();
                    copyFontInfo(poster,curPoster);
                    posterView.setPoster(poster);
                    break;
                case 6:
                    if(curPoster.getClass() == VerticalPoster.class){  //画标准竖向文字
                        return;
                    }
                    poster = new VerticalPoster();
                    copyFontInfo(poster,curPoster);
                    posterView.setPoster(poster);
                    break;
                case 7:
                    if(curPoster.getClass() == VerticalSinglelinePoster.class){  //画竖向带一竖线样式
                        return;
                    }
                    poster = new VerticalSinglelinePoster();
                    copyFontInfo(poster,curPoster);
                    posterView.setPoster(poster);
                    break;
                case 8:
                    if(curPoster.getClass() == VerticalDoublelinePoster.class){ //画竖向带二竖线样式
                        return;
                    }
                    poster = new VerticalDoublelinePoster();
                    copyFontInfo(poster,curPoster);
                    posterView.setPoster(poster);
                    break;
                case 9:
                    if(curPoster.getClass() == DiffSizePoster.class){   //画带不同行字体大小样式
                        return;
                    }
                    poster = new DiffSizePoster();
                    copyFontInfo(poster,curPoster);
                    posterView.setPoster(poster);
                    break;
                case 10:
                    if(curPoster.getClass() == HorizontalBeatPoster.class){ //画跳跃字符样式
                        return;
                    }
                    poster = new HorizontalBeatPoster();
                    copyFontInfo(poster,curPoster);
                    posterView.setPoster(poster);
                    break;
            }
            posterView.resetPosterTexts();
        }

        private void copyFontInfo(IPoster toPoster, IPoster fromPoster) {
            toPoster.setTextColor(fromPoster.getTextColor());
            toPoster.setTextFont(fromPoster.getTextFont(),posterView.getDrawerWidth(),posterView.getDrawerHeight());
            if(fromPoster.getClass()==DiffSizePoster.class){
                toPoster.setTextSize(ScreenUtility.sp2px(18),posterView.getDrawerWidth(),posterView.getDrawerHeight());
            }else{
                toPoster.setTextSize(fromPoster.getTextSize(),posterView.getDrawerWidth(),posterView.getDrawerHeight());
            }
        }

        public void initView(View parentView) {
            mRootView = parentView.findViewById(R.id.poster_menu_style);
            listView = (RecyclerView) mRootView.findViewById(R.id.menu_style_recview);
            listView.setLayoutManager(new LinearLayoutManager(parentView.getContext(),LinearLayoutManager.HORIZONTAL,false));
            adapter = new StyleListAdapter(parentView.getContext(),listItemEventListener);
            listView.setAdapter(adapter);
            listView.setItemAnimator(new DefaultItemAnimator());
        }

        public void showView() {
            mRootView.setVisibility(View.VISIBLE);
        }
        public void hideView() {
            mRootView.setVisibility(View.INVISIBLE);
        }
    }

    //===========================================================================================
    //颜色选择适配器
    private class ColorGridAdapter extends BaseAdapter {
        private LayoutInflater layoutBuilder;
        private int selPosition =0;  //当前选择的颜色位置

        public ColorGridAdapter(LayoutInflater layoutBuilder) {
            this.layoutBuilder = layoutBuilder;
        }

        private final int[] ColorArr={Color.WHITE,Color.parseColor("#f57797"),
                Color.parseColor("#f0bc0f"),Color.parseColor("#69f3ac"),
                Color.parseColor("#79fa66"),Color.parseColor("#79cbff"),
                Color.parseColor("#505fbc"),Color.parseColor("#3699ac"),
                Color.parseColor("#faff79"),Color.parseColor("#ff33cb"),
                Color.parseColor("#a2baff"),Color.BLACK
        };

        public int setSelectItem(int position) {
            if(position!=selPosition){
                selPosition = position;
                notifyDataSetInvalidated();
                return ColorArr[position];
            }
            return ColorArr[position];
        }

        @Override
        public int getCount() {
            return ColorArr.length;
        }

        @Override
        public Object getItem(int position) {
            if(position<ColorArr.length)
            {
                return ColorArr[position];
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ColorViewHolder viewHolder = null;
            if(convertView==null){
                convertView =layoutBuilder.inflate(R.layout.gditem_poster_color,null);
                viewHolder = new ColorViewHolder();
                viewHolder.initUI(convertView);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ColorViewHolder) convertView.getTag();
            }
            viewHolder.updateData(position);
            return convertView;
        }


        private class ColorViewHolder{
            private RoundedColorView colorView;
            public void initUI(View view){
                colorView = (RoundedColorView) view.findViewById(R.id.gditem_poster_color_tvcolor);
            }

            public void updateData(int position){
                colorView.setSelected(selPosition==position);  //设置选择状态
                colorView.setFillColor(ColorArr[position]);
            }
        }

    }
}
