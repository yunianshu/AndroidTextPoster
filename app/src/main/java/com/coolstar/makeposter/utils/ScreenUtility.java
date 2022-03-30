package com.coolstar.makeposter.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.MediaColumns;
import android.util.DisplayMetrics;


// by hongze
public class ScreenUtility {
	/**
	 * 将需要的字体大px转化为sp
	 * 
	 * @param size
	 * @return
	 */
	public static float px2sp(float size) {
        float scale = DeviceInfo.DENSITY;
		if (size <= 0) {
			size = 15;
		}
		float realSize = (float) (size * (scale - 0.1));
		return realSize;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(float dpValue) {
		float scale = DeviceInfo.DENSITY;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(float pxValue) {
		float scale = DeviceInfo.DENSITY;
		return (int) (pxValue / scale + 0.5f);
	}

	/** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
	
	/**
	 * 将需要的字体大sp转化为
	 * 
	 * @param size
	 * @return
	 */
	public static int sp2px(float size) {
		float scale = DeviceInfo.SCALED_DENSITY;
		if (size <= 0) {
			size = 15;
		}
		
        return (int) (size * scale + 0.5f);  
	}
	
	/**
	 * 将bitmap转换为圆角返回
	 * @param bitmap
	 * @param roundPixels 圆角度数
	 * @return
	 */
	public static Bitmap getRoundCornerImage(Bitmap bitmap, int roundPixels) {
		Bitmap roundConcerImage = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(roundConcerImage);
		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawRoundRect(rectF, roundPixels, roundPixels, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, null, rect, paint);
		return roundConcerImage;
	}
	
	/**
	 * 构建 1123KB/4567KB 样式的已下载大小样式.
	 * @param
	 * @return
	 */
	public static String setCurrentSizeStyleKB(long currentSize, long totalSize) {
		StringBuffer sb = new StringBuffer();
		sb.append(currentSize / 1024);
		sb.append("KB/");
		sb.append(totalSize / 1024);
		sb.append("KB");
		return sb.toString();
	}
	
	/**
	 * 构建 1.1M/4.5M 样式的已下载大小样式.
	 * @param
	 * @return
	 */
	public static String setCurrentSizeStyleMB(long currentSize, long totalSize) {
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("%1$.2f", (float)(currentSize)/1024/1024))
		.append("M/")
		.append(String.format("%1$.2f", (float)(totalSize)/1024/1024))
		.append("M");
		
		return sb.toString();
	}

	public static int convertDpToPixelInt(Context context, float dp) {

		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		int px = (int) (dp * (metrics.densityDpi / 160f));
		return px;
	}

	public static float convertDpToPixel(Context context, float dp) {

		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		float px = (float) (dp * (metrics.densityDpi / 160f));
		return px;
	}

}
