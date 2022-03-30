package com.coolstar.makeposter.presenter.poster;

import android.graphics.Bitmap;
import android.util.Log;

import com.coolstar.makeposter.beans.PictureInfo;
import com.coolstar.makeposter.utils.BitmapUtils;
import com.coolstar.makeposter.utils.DeviceInfo;
import com.coolstar.makeposter.utils.ThreadBuilder;
import com.coolstar.makeposter.view.posterview.IPosterView;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by jiguangxing on 2016/3/4.
 */
public class PosterPresenter {
    IPosterView posterView;

    public PosterPresenter(IPosterView posterView) {
        this.posterView = posterView;
        EventBus.getDefault().register(this);
    }

    public void releasePresenter(){
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEventBuildPoster(Bitmap bmp){
        Log.d("EventBusTest","Poster.presenter.event");
        posterView.onBuildSuccess(bmp);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEventDecodeBitmap(PictureInfo item){
        posterView.setArtistBackground(item.decodeBmp);
    }

    public void startLoadPicture(final PictureInfo curPicInfo) {
        ThreadBuilder.getInstance().runThread(new Runnable() {
            @Override
            public void run() {
                Bitmap mBackBmp = BitmapUtils.decodeSampledBitmapFromFile(curPicInfo.fileName, DeviceInfo.WIDTH,DeviceInfo.HEIGHT);
                if(mBackBmp!=null){
                    curPicInfo.decodeBmp = mBackBmp;
                   EventBus.getDefault().post(curPicInfo);
                }
            }
        });
    }
}
