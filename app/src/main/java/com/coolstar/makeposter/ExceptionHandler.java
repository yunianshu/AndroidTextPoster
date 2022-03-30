package com.coolstar.makeposter;

import android.text.TextUtils;

import com.coolstar.makeposter.utils.DirMgr;

import org.joda.time.LocalDateTime;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by jiguangxing on 2016/3/8.
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        StringBuilder stringBuilder = new StringBuilder(1024);
        stringBuilder.append("Thread.id=").append(thread.getId()).append("\n");
        stringBuilder.append("exception=").append(throwable2String(ex));
        saveCrachToFile(stringBuilder.toString());
    }

    private void saveCrachToFile(String log) {
        if(TextUtils.isEmpty(log)){
            return;
        }
        log = log.replaceAll("\n", "\r\n");
        LocalDateTime dayTime = new LocalDateTime();
        String crachFilePath = DirMgr.getDir(DirMgr.CRACH)+dayTime.toString("yyMMddHHmmss")+".log";
        FileOutputStream fos =null;
        try {
            fos = new FileOutputStream(crachFilePath);
            fos.write(log.getBytes());
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(fos!=null){
               try {
                   fos.close();
                   fos = null;
               }catch (Exception ee){
                   ee.printStackTrace();
               }
            }
            System.exit(0);
        }
    }

    public static String throwable2String(final Throwable tr) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            tr.printStackTrace(pw);
            return sw.toString();
        } catch (Throwable e) {
        }
        return "No Memory, throwable2String failed";
    }
}
