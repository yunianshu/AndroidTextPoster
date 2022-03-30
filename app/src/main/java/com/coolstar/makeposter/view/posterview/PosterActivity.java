package com.coolstar.makeposter.view.posterview;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.coolstar.makeposter.R;
import com.coolstar.makeposter.beans.PictureInfo;
import com.coolstar.makeposter.presenter.poster.PosterPresenter;
import com.coolstar.makeposter.utils.ColoredSnackbar;
import com.coolstar.makeposter.utils.DialogUtils;
import com.coolstar.makeposter.utils.KeyBoardUtils;
import com.coolstar.makeposter.utils.ScreenUtility;
import com.coolstar.makeposter.widget.textposter.ColorMatrixFactory;
import com.coolstar.makeposter.widget.textposter.IPoster;
import com.coolstar.makeposter.widget.textposter.TextDrawer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

/**
 * Created by jiguangxing on 2016/3/7.
 */
public class PosterActivity extends AppCompatActivity implements IPosterView {
    private PictureInfo curPicInfo;
    private Bitmap mBackBmp;
    private View.OnClickListener addBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DialogUtils.showConfirmDialog(PosterActivity.this, "确定要生成文字海报图片吗？", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mDrawer.asyncBuildPostPicture(null,false);  //异步生成图片，通过eventbus通知图片生成是否成功
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }
    };

    private View selectTabView;
    private View.OnClickListener tabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            KeyBoardUtils.hideKeyboard(v);
            if(isStylePanelShowing()==false){
                showStylePanel();
            }else if(v==selectTabView){
                hideStylePanel();
            }
            if(selectTabView!=null){
                selectTabView.setSelected(false);
            }
            selectTabView = v;
            selectTabView.setSelected(true);
            switch (v.getId()){
                case R.id.poster_tab_text:
                    menuHelper.showTextView();
                    break;
                case R.id.poster_tab_font:
                    menuHelper.showFontView();
                    break;
                case R.id.poster_tab_color:
                    menuHelper.showColorView();
                    break;
                case R.id.poster_tab_style:
                    menuHelper.showStyleView();
                    break;
            }
        }
    };
    private float panelHideHeight;
    private TextDrawer.OnGestureChangedListener drawerGestureListener = new TextDrawer.OnGestureChangedListener() {
        @Override
        public void onHorizontalGesture(float distanceX, float totalWidth) {
            mDrawer.changeArtistBmpColorArr(ColorMatrixFactory.selectColorMatrixArr(distanceX>0f?ColorMatrixFactory.DIR_NEXT:ColorMatrixFactory.DIR_PREV));
            showMessage(""+ColorMatrixFactory.getSelectedMatrixTitle());
        }

        @Override
        public void onVerticalGesture(float distanceY, float totalHeight) {
            if(Math.abs(distanceY)>100){
                if(distanceY>0){
                    if(isStylePanelShowing()){
                        hideStylePanel();
                    }
                }else{
                    if(isStylePanelShowing()==false){
                        showStylePanel();
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster);
        mPresenter = new PosterPresenter(this);
        initView();
        panelHideHeight = ScreenUtility.dip2px(159); //此值要与xml中的layout_marginBottom相同，没从控件获取，是为了省事
        Intent intent = getIntent();
        curPicInfo = intent.getParcelableExtra("PictureInfo");
    }

    private PosterPresenter mPresenter;
    private TextDrawer mDrawer;
    private FloatingActionButton mAddBtn;
    private View mBottomPanel;
    private View tabText,tabFont,tabColor,tabStyle;
    private FrameLayout bottomContentParent;
    private StyleMenuHelper menuHelper;
    private void initView() {
        mDrawer = (TextDrawer)findViewById(R.id.poster_drawer);
        mDrawer.setGestureListener(drawerGestureListener);
        mAddBtn = (FloatingActionButton)findViewById(R.id.poster_add);
        mAddBtn.setOnClickListener(addBtnClickListener);
        mBottomPanel = findViewById(R.id.poster_bottom_panel);
        tabText = findViewById(R.id.poster_tab_text);
        tabFont = findViewById(R.id.poster_tab_font);
        tabColor = findViewById(R.id.poster_tab_color);
        tabStyle = findViewById(R.id.poster_tab_style);
        bottomContentParent = (FrameLayout)findViewById(R.id.poster_bottom_content);
        menuHelper = new StyleMenuHelper(this);
        menuHelper.initView(bottomContentParent);
        tabText.setOnClickListener(tabClickListener);
        tabFont.setOnClickListener(tabClickListener);
        tabColor.setOnClickListener(tabClickListener);
        tabStyle.setOnClickListener(tabClickListener);
    }


    @Override
    protected void onDestroy() {
        mPresenter.releasePresenter();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.startLoadPicture(curPicInfo);
    }

    //============================================================================================
    private boolean isPanelShowing = false;

    @Override
    public void showStylePanel() {
        if(mBottomPanel!=null&&isPanelShowing==false){
            isPanelShowing = true;
            ObjectAnimator transAnim =  ObjectAnimator.ofFloat(mBottomPanel,"translationY",0,-panelHideHeight);
            transAnim.setDuration(500);
            transAnim.start();
        }
    }

    @Override
    public void hideStylePanel() {
        if(mBottomPanel!=null&&isPanelShowing){
            ObjectAnimator transAnim =  ObjectAnimator.ofFloat(mBottomPanel,"translationY",-panelHideHeight,0);
            transAnim.setDuration(500);
            transAnim.start();
            isPanelShowing = false;
        }
    }

    @Override
    public boolean isStylePanelShowing() {
        return isPanelShowing;
    }

    @Override
    public void setTextColor(int color) {
        if(mDrawer!=null){
            mDrawer.setTextColor(color);
        }
    }

    @Override
    public void setTextShadow(boolean checked) {
        mDrawer.setShadow(checked);
    }

    @Override
    public void setTextAlign(Layout.Alignment alignment) {
        mDrawer.setTextAlign(alignment);
    }

    @Override
    public void showMessage(String msg) {
        Snackbar snackbar = Snackbar.make(mDrawer,msg,Snackbar.LENGTH_SHORT);
        ColoredSnackbar.info(snackbar).show();
    }

    private ArrayList<String> inputList = new ArrayList<String>(10);
    @Override
    public void addPosterText(String posterText) {
        inputList.add(posterText);
        String[] tmpArr = (String[]) inputList.toArray(new String[0]);
        mDrawer.setTexts(tmpArr);
    }

    @Override
    public void setTextSize(float size) {
        mDrawer.setTextSize(size);
    }

    @Override
    public void onBuildSuccess(Bitmap bmp) {
        if(bmp!=null){
            showMessage("海报保存到相册成功");
        }
    }

    @Override
    public IPoster getPoster() {
        return mDrawer.getPoster();
    }

    @Override
    public void setPoster(IPoster poster) {
        mDrawer.setPoster(poster);
    }

    @Override
    public void resetPosterTexts() {
        mDrawer.resetPosterTexts();
    }

    @Override
    public int getDrawerWidth() {
        return mDrawer.getWidth();
    }

    @Override
    public int getDrawerHeight() {
        return mDrawer.getHeight();
    }

    @Override
    public void showTitleAndLogo(String title, Bitmap bmp) {
        mDrawer.setMusicTitleAndLogo(title,bmp);
    }

    @Override
    public void setArtistBackground(Bitmap bmp) {
        mDrawer.setArtistBackground(bmp);
    }
    //============================================================================================

    @Override
    public void onBackPressed() {
        if(isStylePanelShowing()){
            hideStylePanel();
            return;
        }
        super.onBackPressed();
    }
}
