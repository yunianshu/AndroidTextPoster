package com.coolstar.makeposter.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * Created by jiguangxing on 2016/3/7.
 */
public class FileUtils {
    /**
     * 判断文件是否存在
     *
     * @param path
     *            文件路径
     * @return 是否存在
     */
    public static boolean isExist(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        return file.exists();
    }

    // 拷贝文件
    public static boolean fileCopy(String from_file, String to_file) {
        return fileCopy(new File(from_file), new File(to_file));
    }

    public static boolean fileCopy(File from_file, File to_file) {
        if (!from_file.exists()) {
            return false;
        }

        if (to_file.exists()) {
            to_file.delete();
        }
        boolean success = true;
        FileInputStream from = null;
        FileOutputStream to = null;
        byte[] buffer;
        try {
            buffer = new byte[1024];
        } catch (OutOfMemoryError oom) {
            LogMgr.e(FileUtils.class.getSimpleName(), oom.getMessage());
            return false;
        }
        try {
            from = new FileInputStream(from_file);
            to = new FileOutputStream(to_file); // Create output stream
            int bytes_read;

            while ((bytes_read = from.read(buffer)) != -1) {
                // Read until EOF
                to.write(buffer, 0, bytes_read);
            }
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            buffer = null;
            if (from != null) {
                try {
                    from.close();
                } catch (IOException e) {
                }
                from = null;
            }
            if (to != null) {
                try {
                    to.close();
                } catch (IOException e) {
                }
                to = null;
            }
        }

        if (!success) {
            to_file.delete();
        }

        return success;
    }

    /**
     * 删除文件或者目录
     *
     * @param path
     *            指定路径的文件或目录
     * @return 返回操作结果
     */
    public static boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return true;
        }
        File file = new File(path);
        if (!file.exists()) return true;

        if (file.isDirectory()) {
            String[] subPaths = file.list();
            if (subPaths != null){ //4.4系统无权限可能为空
                for (String p : subPaths) {
                    if (!deleteFile(path + File.separator + p)) {
                        return false;
                    }
                }
            }else{
                LogMgr.e(FileUtils.class.getSimpleName(), "deleteFile-->"+path);
            }
        }

        return file.delete();
    }

    public static boolean deleteFilesExcept(String path, String fileName) {
        File[] files = getFiles(path);
        if(files != null) {
            for(File f : files) {
                if(!f.getAbsolutePath().endsWith(fileName)) {
                    f.delete();
                }
            }
        }
        return true;
    }

    /**
     * 创建目录，包括必要的父目录的创建，如果未创建
     *
     * @param path
     *            待创建的目录路径
     * @return 返回操作结果
     */
    public static boolean mkdir(String path) {
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            return true;
        }

        return file.mkdirs();
    }

    public static File[] getFiles(String path) {
        return getFiles(path, null);
    }

    /**
     * 获取当前目录的文件列表
     */
    public static File[] getFiles(String path, final String[] filters) {
        File file = new File(path);
        if (!file.isDirectory()) {
            return null;
        }

        FilenameFilter filter = null;
        if (filters != null && filters.length > 0) {
            filter = new FilenameFilter() {
                @Override
                public boolean accept(File directory, String filename) {
                    if (!TextUtils.isEmpty(filename)) {
                        String lowerCase = filename.toLowerCase();
                        for (String type : filters) {
                            if (lowerCase.endsWith(type)) {
                                return true;
                            }
                        }
                    }
                    return false;
                }
            };
        }

        File[] fileList = file.listFiles(filter);
        return fileList;
    }
}
