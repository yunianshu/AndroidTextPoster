package com.coolstar.makeposter.view.posterview;

import android.graphics.Bitmap;
import android.text.Layout;

import com.coolstar.makeposter.beans.PictureInfo;
import com.coolstar.makeposter.widget.textposter.IPoster;

/**
 * Created by jiguangxing on 2016/3/7.
 */
public interface IPosterView {
    void showStylePanel();
    void hideStylePanel();
    boolean isStylePanelShowing();

    void setTextColor(int color);

    void setTextShadow(boolean checked);

    void setTextAlign(Layout.Alignment alignment);

    void showMessage(String msg);

    void addPosterText(String posterText);

    void setTextSize(float size);

    void onBuildSuccess(Bitmap bmp);

    IPoster getPoster();

    void setPoster(IPoster poster);

    void resetPosterTexts();

    int getDrawerWidth();

    int getDrawerHeight();

    void showTitleAndLogo(String title, Bitmap bmp);

    void setArtistBackground(Bitmap bmp);
}
