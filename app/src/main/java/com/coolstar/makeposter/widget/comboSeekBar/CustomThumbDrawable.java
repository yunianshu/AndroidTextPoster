package com.coolstar.makeposter.widget.comboSeekBar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;

import com.coolstar.makeposter.utils.ScreenUtility;


/**
 * seekbar background with text on it.
 * 
 * @author sazonov-adm
 * 
 */
public class CustomThumbDrawable extends Drawable {
	/**
	 * paints.
	 */
	private Paint circlePaint;
	private float mRadius;
	private Drawable mThumbDrawable;
	private int mThumbHeight;
	private int mThumbWidth;

	public CustomThumbDrawable(Context context, int color,
							   Drawable thumbDrawable, int thumbHeight, int thumbWidth) {
		this.mThumbDrawable = thumbDrawable;
		this.mThumbHeight = thumbHeight;
		this.mThumbWidth = thumbWidth;
		mRadius = ScreenUtility.dip2px(8f);
	}

	public CustomThumbDrawable(int color) {
		mRadius = ScreenUtility.dip2px(8f);
		setColor(color);
	}

	public void setColor(int color) {
		circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circlePaint.setColor((0xA0 << 24) + (color & 0x00FFFFFF));
		invalidateSelf();
	}

	public float getRadius() {
		if (mThumbDrawable == null)
			return mRadius;
		return mThumbHeight * 1.0f / 2;
	}

	@Override
	protected final boolean onStateChange(int[] state) {
		invalidateSelf();
		return false;
	}

	@Override
	public final boolean isStateful() {
		return true;
	}

	private Bitmap drawable2Bitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		} else if (drawable instanceof NinePatchDrawable) {
			Bitmap bitmap = Bitmap
					.createBitmap(
							drawable.getIntrinsicWidth(),
							drawable.getIntrinsicHeight(),
							drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
									: Bitmap.Config.RGB_565);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			drawable.draw(canvas);
			return bitmap;
		} else {
			return null;
		}
	}

	private boolean isOffetSet = false;
	@Override
	public final void draw(Canvas canvas) {
		if("m2note".equals(Build.BOARD)&&"Meizu".equals(Build.MANUFACTURER)&&isOffetSet==false){
			int offetVal = ScreenUtility.dip2px(20); //在魅族的note上,发现有偏移情况,其它手机没有,特别处理
			Rect tmpRect = this.getBounds();
			this.setBounds(tmpRect.left,tmpRect.top-offetVal,tmpRect.right,tmpRect.bottom-offetVal);
			isOffetSet = true;
		}
		int height = this.getBounds().centerY();
		int width = this.getBounds().centerX();
//		canvas.drawRect(this.getBounds(),circlePaint);
		if (mThumbDrawable == null) {
			canvas.drawCircle(width, height, mRadius, circlePaint);
		} else {
			Bitmap bm = drawable2Bitmap(mThumbDrawable);
			Bitmap bitmap = Bitmap.createScaledBitmap(bm, mThumbWidth,
					mThumbHeight, true);
			canvas.drawBitmap(bitmap, width - getRadius(), height - getRadius(), circlePaint);
		}
	}

	@Override
	public int getIntrinsicHeight() {
		if (mThumbDrawable == null)
			return (int) (mRadius * 2);
		return mThumbHeight;
	}

	@Override
	public int getIntrinsicWidth() {
		if (mThumbDrawable == null)
			return (int) (mRadius * 2);
		return mThumbWidth;
	}

	@Override
	public final int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	@Override
	public void setAlpha(int alpha) {
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
	}
}