package com.coolstar.makeposter.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;

import com.coolstar.makeposter.CustomApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiguangxing on 2016/3/8.
 */
public class DirMgr {
    public static final int SD_ROOT 	= 0;
    public static final int EXT_ROOT 	= 1; // 外置SD卡
    public static final int HOME    = 2;
    public static final int CRACH   = 3;
    public static final int POSTER  = 4;
    public static final int LOG     = 5;

    private static final String SD_ROOTPATH = Environment
            .getExternalStorageDirectory().getAbsolutePath() + File.separator;
    private static final String HOME_PATH = SD_ROOTPATH + "MakePoster" + File.separator;

    public static String getDir(int dirIndex){
        String dirPath =null;
        switch (dirIndex){
            case SD_ROOT:
                dirPath = SD_ROOTPATH;
                break;
            case EXT_ROOT:
                List<String> paths = getStoragePaths(CustomApplication.appContext);
                if (paths!=null&&paths.size() >= 2) {
                    for (String path : paths) {
                        if (path != null && !path.equals(getFirstExterPath())) {
                            File pathFile = new File(path);
                            try {
                                String realPath = pathFile.getCanonicalPath();
                                if(realPath.equals(path)){
                                    dirPath = path;
                                    break;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                break;
            case HOME:
                dirPath = HOME_PATH;
                break;
            case CRACH:
                dirPath = HOME_PATH+File.separator+"crach";
                break;
            case POSTER:
                dirPath = HOME_PATH+File.separator+"poster";
                break;
            case LOG:
                dirPath = HOME_PATH+File.separator+"log";
                break;
        }
        if (!TextUtils.isEmpty(dirPath) && !dirPath.endsWith(File.separator)) {
            dirPath += File.separator;
        }
        File dir = new File(dirPath);
        if (!dir.exists()) {
            try {
                dir.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dirPath;
    }

    public static List<String> getAllExterSdcardPath() {
        List<String> SdList = new ArrayList<String>();

        String firstPath = getFirstExterPath();
        LogMgr.d("SDCARD","First externalSdcard:"+firstPath);
        // 得到路径
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                LogMgr.d("SDCARD",line);
                // 将常见的linux分区过滤掉
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;
                if (line.startsWith("/data/media"))
                    continue;
                if (line.contains("/mnt/media_rw/")&&line.contains("/dev/block/"))
                    continue;
                if (line.contains("system") || line.contains("cache")
                        || line.contains("sys") || line.contains("data")
                        || line.contains("tmpfs") || line.contains("shell")
                        || line.contains("root") || line.contains("acct")
                        || line.contains("proc") || line.contains("misc")
                        || line.contains("obb")) {
                    continue;
                }
                //下面是三星一款手机的外卡信息
                ///dev/block/vold/179:9 /mnt/media_rw/extSdCard vfat rw,dirsync,seclabel,nosuid,nodev,noexec,noatime,nodiratime,uid=1023,gid=1023,fmask=0007,dmask=0007,allow_utime=0020,codepage=cp437,iocharset=iso8859-1,shortname=mixed,utf8,errors=remount-ro 0 0
                ///mnt/media_rw/extSdCard /storage/extSdCard    sdcardfs rw,seclabel,nosuid,nodev,relatime,uid=1023,gid=1023,derive=unified 0 0
                //下面是标准内存sdcard信息
                ///data/media /storage/emulated/0 sdcardfs rw,seclabel,nosuid,nodev,relatime,uid=1023,gid=1023,derive=legacy,reserved=20MB 0 0
                ///data/media /storage/emulated/legacy sdcardfs rw,seclabel,nosuid,nodev,relatime,uid=1023,gid=1023,derive=legacy,reserved=20MB 0 0

                if (line.contains("fat") || line.contains("fuse") || (line
                        .contains("ntfs"))|| line.contains("/extSdCard")) {

                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        String path = columns[1];
                        if (path!=null&&!SdList.contains(path)&&path.toLowerCase().contains("sd"))
                            SdList.add(columns[1]);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!SdList.contains(firstPath)) {
            SdList.add(firstPath);
        }
        return SdList;
    }

    /**
     * 2015.09.15增加的获取外置sd卡的方法，此方法按android版本使用不同的方式，获取外置sd卡路径
     * @param cxt
     * @return
     */
    public static List<String> getStoragePaths(Context cxt) {
        List<String> pathsList = null;
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.GINGERBREAD) {
            pathsList = getAllExterSdcardPath();
        } else {
            if(cxt==null){
                return getAllExterSdcardPath();
            }
            pathsList = new ArrayList<String>();
            StorageManager storageManager = (StorageManager) cxt.getSystemService(Context.STORAGE_SERVICE);
            try {
                if(storageManager!=null){
                    Method method = StorageManager.class.getDeclaredMethod("getVolumePaths");
                    if(method!=null){
                        method.setAccessible(true);
                        Object result = method.invoke(storageManager);
                        if (result != null && result instanceof String[]) {
                            String[] pathes = (String[]) result;
                            StatFs statFs;
                            for (String path : pathes) {
                                if (!TextUtils.isEmpty(path) && new File(path).exists()) {
                                    statFs = new StatFs(path);
                                    if (statFs.getBlockCount() * statFs.getBlockSize() != 0) {
                                        pathsList.add(path);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
            }
            if(pathsList.size()==0){
                pathsList = getAllExterSdcardPath();
            }
        }
        return pathsList;
    }
    public static String getFirstExterPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }
}
