package com.coolstar.makeposter.presenter.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.coolstar.makeposter.beans.AsyncErrorInfo;
import com.coolstar.makeposter.beans.PictureInfo;
import com.coolstar.makeposter.mod.main.IMain;
import com.coolstar.makeposter.mod.main.MainMod;
import com.coolstar.makeposter.view.mainview.IMainView;

import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;


/**
 * Created by jiguangxing on 2016/3/4.
 */
public class MainPresenter {
    IMain  mainMod;
    IMainView mainView;
    private boolean isLoading;

    public MainPresenter(IMainView mainView) {
        this.mainView = mainView;
        this.mainMod = new MainMod();
        EventBus.getDefault().register(this);
    }

    public void releasePresenter(){
        EventBus.getDefault().unregister(this);
    }

    public void loadAllPictures(Context context) {
        if(isLoading){
            return;
        }
        isLoading = true;
        mainView.startLoading();
        mainMod.asyncLoadPictureList(context);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEventReceiveList(List<PictureInfo> list){
        mainView.setPictureList(list);
        mainView.stopLoading();
        mainView.showPictureList();
    }
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEventReceiveError(AsyncErrorInfo errInfo){
        mainView.stopLoading();
        mainView.showErrorInfo();
    }
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEventMainTest(Bitmap bmp){
        Log.d("EventBusTest","main.presenter.event");
    }
}
