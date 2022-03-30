package com.coolstar.makeposter.mod.main;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.coolstar.makeposter.beans.AsyncErrorInfo;
import com.coolstar.makeposter.beans.PictureInfo;
import com.coolstar.makeposter.utils.BitmapUtils;
import com.coolstar.makeposter.utils.ThreadBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by jiguangxing on 2016/3/4.
 */
public class MainMod implements IMain {
    @Override
    public void asyncLoadPictureList(final Context context) {
        ThreadBuilder.getInstance().runThread(new Runnable() {
            @Override
            public void run() {
                String[] projection = new String[] { MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.BUCKET_ID, // 直接包含该图片文件的文件夹ID，防止在不同下的文件夹重名
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME, // 直接包含该图片文件的文件夹名
                        MediaStore.Images.Media.DISPLAY_NAME, // 图片文件名
                        MediaStore.Images.Media.DATA // 图片绝对路径
//                        ,"count("+MediaStore.Images.Media._ID+")"//统计当前文件夹下共有多少张图片
                };
                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                try {
                    
                    Cursor cursor = context.getContentResolver().query(uri,projection,null,null,null);
                    if(cursor!=null){
                        List<PictureInfo> list = new ArrayList<PictureInfo>();
                        while (cursor.moveToNext()){
                            PictureInfo item = new PictureInfo();
                            item.fileName = cursor.getString(4);
                            item.picName = cursor.getString(3);
                            item.dirName = cursor.getString(2);
                            if(BitmapUtils.isBigBitmap(item.fileName)){
                                list.add(item);
                            }
                        }
                        EventBus.getDefault().post(list);
                    }else{
                        EventBus.getDefault().post(new AsyncErrorInfo(2,"parse error"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    EventBus.getDefault().post(new AsyncErrorInfo(1,"db query error"));
                }
            }
        });
    }
}
