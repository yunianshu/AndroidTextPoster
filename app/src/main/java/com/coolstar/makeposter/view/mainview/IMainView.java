package com.coolstar.makeposter.view.mainview;

import com.coolstar.makeposter.beans.PictureInfo;

import java.util.List;

/**
 * 主界面的View接口
 * Created by jiguangxing on 2016/3/4.
 */
public interface IMainView {
    public void startLoading();
    public void stopLoading();
    public void setPictureList(List<PictureInfo> list);
    public void showPictureList();
    public void showErrorInfo();
}
