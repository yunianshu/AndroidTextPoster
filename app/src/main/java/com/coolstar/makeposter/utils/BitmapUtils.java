package com.coolstar.makeposter.utils;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.InputStream;

/**
 * Created by jiguangxing on 2016/3/7.
 */
public class BitmapUtils {
    private static SizeInfo sizeInfo;

    private static class SizeInfo{
        public int width;
        public int height;

        public boolean isBigSize(){
            return width>=480&&height>=800;
        }
    }

    /**
     * 判断图片是否是大于480*800的图，大于为true，小于为false
     * @param filePath
     * @return
     */
    public static boolean isBigBitmap(String filePath){
        SizeInfo tmpSize = getBitmapSize(filePath);
        if(tmpSize!=null){
           return tmpSize.isBigSize();
        }
        return false;
    }

    private static SizeInfo getBitmapSize(String filePath) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        try{
            BitmapFactory.decodeFile(filePath,opt);  //此处需要sd卡读权限 ，不加会返回null

            if(sizeInfo==null){
                sizeInfo = new SizeInfo();
            }
            sizeInfo.width = opt.outWidth;
            sizeInfo.height = opt.outHeight;
            return sizeInfo;
        }catch (Throwable e){
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 缩放bitmap
     *
     * @param f 缩放的比例
     * @param bitmap
     * @return
     */
    public static Bitmap postScale(Bitmap bitmap, float f) {
        Matrix matrix = new Matrix();
        matrix.postScale(f, f); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    /**
     * 高斯模糊一张bitmap图片
     * @param sentBitmap
     * @param radius
     * @param smallScale
     * @param bigScale
     * @return
     */
    public static Bitmap doBlur(Bitmap sentBitmap, int radius , float smallScale, float bigScale) {
        if (radius < 1) {
            return null;
        }
        Bitmap bitmap = postScale(sentBitmap, smallScale);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dvCount = 256 * divsum;
        int dv[] = new int[dvCount];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {
                if (rsum >= dvCount || gsum >= dvCount || bsum >= dvCount) {//判断防止越界
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                    return null;
                }
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        Bitmap resultBitmap = postScale(bitmap,bigScale);
        if(bitmap != null && !bitmap.isRecycled()){
            bitmap.recycle();
            bitmap = null;
        }
        return resultBitmap;
    }

    /**
     * Decode and sample down a bitmap from resources to the requested width and
     * height.
     *
     * @param res
     *            The resources object containing the image data
     * @param resId
     *            The resource id of the image data
     * @param reqWidth
     *            The requested width of the resulting bitmap
     * @param reqHeight
     *            The requested height of the resulting bitmap
     * @return A bitmap sampled down from the original with the same aspect
     *         ratio and dimensions that are equal to or greater than the
     *         requested width and height
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth,
                                                         int reqHeight) {

        // BEGIN_INCLUDE (read_bitmap_dimensions)
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inPurgeable = true;
//		options.inInputShareable = true;

        options.inJustDecodeBounds = true;
        if (!DeviceInfo.hasHoneycomb()) {
            options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        }
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = CalculateStrategyCommonBitmapSize(options, reqWidth, reqHeight);
        // END_INCLUDE (read_bitmap_dimensions)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * Decode and sample down a bitmap from a file to the requested width and
     * height.
     *
     * @param filename
     *            The full path of the file to decode
     * @param reqWidth
     *            The requested width of the resulting bitmap
     * @param reqHeight
     *            The requested height of the resulting bitmap
     * @return A bitmap sampled down from the original with the same aspect
     *         ratio and dimensions that are equal to or greater than the
     *         requested width and height
     */
    public static Bitmap decodeSampledBitmapFromFile(String filename, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inPurgeable = true;
//		options.inInputShareable = true;
        if (!DeviceInfo.hasHoneycomb()) {
            options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        }
        return decodeSampledBitmapFromFile(filename, reqWidth, reqHeight, options);
    }
    public static Bitmap decodeSampledBitmapFromFile(String filename, int reqWidth, int reqHeight,final BitmapFactory.Options opts) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = opts;
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(filename, options);
//		if (reqHeight <= 50 && opts.outHeight <= 100)
//			reqHeight = -1;
//		if (reqWidth <= 50 && opts.outWidth <= 100)
//			reqWidth = -1;

        // Calculate inSampleSize
        options.inSampleSize = CalculateStrategyCommonBitmapSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filename, options);
    }

    /**
     * Decode and sample down a bitmap from a file input stream to the requested
     * width and height.
     *
     * @param fileDescriptor
     *            The file descriptor to read from
     * @param reqWidth
     *            The requested width of the resulting bitmap
     * @param reqHeight
     *            The requested height of the resulting bitmap
     * @return A bitmap sampled down from the original with the same aspect
     *         ratio and dimensions that are equal to or greater than the
     *         requested width and height
     */
    public static Bitmap decodeSampledBitmapFromDescriptor(FileDescriptor fileDescriptor,
                                                           int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inPurgeable = true;
//		options.inInputShareable = true;
        options.inJustDecodeBounds = true;
        if (!DeviceInfo.hasHoneycomb()) {
            options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        }
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

        // Calculate inSampleSize
        options.inSampleSize = CalculateStrategyCommonBitmapSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
    }

    public static Bitmap decodeSampledBitmapFromStream(InputStream stream, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        if (!DeviceInfo.hasHoneycomb()) {
            options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        }
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream, null, options);

        // Calculate inSampleSize
        options.inSampleSize = CalculateStrategyCommonBitmapSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeStream(stream, null, options);
    }

    private static int CalculateStrategyCommonBitmapSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        float rateVal = 1.0f;
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                rateVal = (float) height / (float) reqHeight;
            } else {
                rateVal = (float) width / (float) reqWidth;
            }
            inSampleSize = (int)Math.floor(rateVal+0.7f); //此处加0.7，然后用floor向下取整，可让540的在加载720图时，也缩放加载
        }
        return inSampleSize;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static int getBitmapSize(BitmapDrawable value) {
        Bitmap bitmap = value.getBitmap();
        if (bitmap == null) {
            return 0; //ui 1.0 ab huawei npe
        }
        if (DeviceInfo.hasKitKat()) {
            return bitmap.getAllocationByteCount();
        }
        if (DeviceInfo.hasHoneycombMR1()) {
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }


}
