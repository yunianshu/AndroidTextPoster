package com.coolstar.makeposter.widget.comboSeekBar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SeekBar;

import com.coolstar.makeposter.R;

import java.util.ArrayList;
import java.util.List;


/**
 * 此控件来自于github上的
 * https://github.com/karabaralex/android-comboseekbar
 * 使用示例
 * <cn.kuwo.ui.widget.comboSeekBar.ComboSeekBar xmlns:cbs="http://schemas.android.com/apk/res-auto"
 android:id="@+id/textbar"
 android:layout_width="match_parent"
 android:layout_height="wrap_content"
 cbs:unselectedLineHeight="2dp"
 cbs:selectedLineHeight="2dp"
 cbs:dotRadius="4dp"
 cbs:multiline="false"
 cbs:combothumb="@drawable/thumbpic"
 cbs:thumbHeight="15dp"
 cbs:txtSize="14sp"
 cbs:thumbWidth="15dp"
 cbs:myColor="@android:color/black" />
 */
public class ComboSeekBar extends SeekBar {

	private static final int DEFAULT_SELECTED_LINE_HEIGHT = 10;
	private static final int DEFAULT_UNSELECTED_LINE_HEIGHT = 5;
	private static final int DEFAULT_TEXT_SIZE = 15;
	private static final int DEFAULT_COLOR = Color.WHITE;
	private static final int DEFAULT_DOT_RADIUS = 10;
	private static final int DEFAULT_THUMB_WIDTH = 50;
	private static final int DEFAULT_THUMB_HEIGHT = 50;

	private CustomThumbDrawable mThumb;
	private List<Dot> mDots = new ArrayList<Dot>();
	private OnItemClickListener mItemClickListener;
	private Dot prevSelected = null;
	private int mColor;
	private int mTextSize;
	private int mDotRadius;
	private int mSelectedLineHeight;
	private int mUnselectedLineHeight;
	private int mThumbWidth;
	private int mThumbHeight;
	private Drawable mThumbResource;
	private boolean mIsMultiline;
	private boolean needResetSelectPosition;  //设置初始位置时，可能控件布局还没完成，则需要此标记进行判断

	/**
	 * @param context
	 *            context.
	 */
	public ComboSeekBar(Context context) {
		super(context);
	}

	/**
	 * @param context
	 *            context.
	 * @param attrs
	 *            attrs.
	 */
	public ComboSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ComboSeekBar);

		mColor = a.getColor(R.styleable.ComboSeekBar_myColor, DEFAULT_COLOR);
		mTextSize = a.getDimensionPixelSize(R.styleable.ComboSeekBar_txtSize,
				DEFAULT_TEXT_SIZE);
		mDotRadius = a.getDimensionPixelSize(
				R.styleable.ComboSeekBar_dotRadius, DEFAULT_DOT_RADIUS);
		mSelectedLineHeight = a.getDimensionPixelSize(
				R.styleable.ComboSeekBar_selectedLineHeight,
				DEFAULT_SELECTED_LINE_HEIGHT);
		mUnselectedLineHeight = a.getDimensionPixelSize(
				R.styleable.ComboSeekBar_unselectedLineHeight,
				DEFAULT_UNSELECTED_LINE_HEIGHT);
		mThumbHeight = a.getDimensionPixelSize(
				R.styleable.ComboSeekBar_thumbHeight, DEFAULT_THUMB_HEIGHT);
		mThumbWidth = a.getDimensionPixelSize(
				R.styleable.ComboSeekBar_thumbWidth, DEFAULT_THUMB_WIDTH);
		mIsMultiline = a.getBoolean(R.styleable.ComboSeekBar_multiline, false);
		mThumbResource = a.getDrawable(R.styleable.ComboSeekBar_combothumb);
		// do something with str

		a.recycle();
		if (mThumbResource == null)
			mThumb = new CustomThumbDrawable(mColor);
		else
			mThumb = new CustomThumbDrawable(context, mColor, mThumbResource,
					mThumbHeight, mThumbWidth);
		setThumb(mThumb);
		setProgressDrawable(new CustomDrawable(this.getProgressDrawable(),
				this, mThumb.getRadius(), mDots, mColor, mTextSize,
				mSelectedLineHeight, mUnselectedLineHeight, mDotRadius,
				mIsMultiline));

		setPadding(0, 0, 0, 0);
		// init the first position when show
		postDelayed(new Runnable() {
			@Override
			public void run() {
				lockPosition();
			}
		}, 0);
	}

	private void lockPosition() {
		if ((mThumb != null) && (mDots.size() > 1)) {
			Rect bounds = mThumb.copyBounds();
			int intervalWidth = mDots.get(1).mX - mDots.get(0).mX;
			if ((mDots.get(mDots.size() - 1).mX + mThumbWidth / 2 - bounds.centerX()) < 0) {
				bounds.right = mDots.get(mDots.size() - 1).mX;
				bounds.left = mDots.get(mDots.size() - 1).mX;
				mThumb.setBounds(bounds);

				for (Dot dot : mDots) {
					dot.isSelected = false;
				}
				mDots.get(mDots.size() - 1).isSelected = true;
				handleClick(mDots.get(mDots.size() - 1));
			}
			for (int i = 0; i < mDots.size(); i++) {
				if (Math.abs(mDots.get(i).mX + mThumbWidth / 2 - bounds.centerX()) <= (intervalWidth / 2)) {
					bounds.right = mDots.get(i).mX + mThumbWidth / 2;
					bounds.left = mDots.get(i).mX + mThumbHeight / 2;
					mThumb.setBounds(bounds);
					mDots.get(i).isSelected = true;
					handleClick(mDots.get(i));
				} else {
					mDots.get(i).isSelected = false;
				}
			}
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean result = super.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			lockPosition();
			break;

		default:
			break;
		}
		return result;
	}

	/**
	 * @param color
	 *            color.
	 */
	public void setColor(int color) {
		mColor = color;
		mThumb.setColor(color);
		setProgressDrawable(new CustomDrawable(this.getProgressDrawable(),
				this, mThumb.getRadius(), mDots, color, mTextSize,
				mSelectedLineHeight, mUnselectedLineHeight, mDotRadius,
				mIsMultiline));
	}

	public synchronized void setSelection(int position) {
		if ((position < 0) || (position >= mDots.size())) {
			throw new IllegalArgumentException("Position is out of bounds:"
					+ position);
		}
		for (Dot dot : mDots) {
			if (dot.id == position) {
				dot.isSelected = true;
				if(dot.id>0&&dot.mX<1){
					needResetSelectPosition = true;
				}else{
					changeThumbPosition(dot.mX);
				}
			} else {
				dot.isSelected = false;
			}
		}

		invalidate();
	}

	private void changeThumbPosition(int dotX){
		if(mThumb!=null){
			Rect bounds = mThumb.copyBounds();
			bounds.right = dotX + mThumbWidth / 2;
			bounds.left = dotX + mThumbHeight / 2;
			mThumb.setBounds(bounds);
		}
	}

	public void setAdapter(List<String> dots) {
		mDots.clear();
		int index = 0;
		for (String dotName : dots) {
			Dot dot = new Dot();
			dot.text = dotName;
			dot.id = index++;
			mDots.add(dot);
		}
		initDotsCoordinates();
	}

	@Override
	public void setThumb(Drawable thumb) {
		if (thumb instanceof CustomThumbDrawable) {
			mThumb = (CustomThumbDrawable) thumb;
		}
		super.setThumb(thumb);
	}

	private void handleClick(Dot selected) {
		if ((prevSelected == null) || (prevSelected.equals(selected) == false)) {
			if (mItemClickListener != null) {
				mItemClickListener.onItemClick(null, this, selected.id,
						selected.id);
			}
			prevSelected = selected;
		}
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		CustomDrawable d = (CustomDrawable) getProgressDrawable();

		int thumbHeight = mThumb == null ? 0 : mThumb.getIntrinsicHeight();
		int dw = 0;
		int dh = 0;
		if (d != null) {
			dw = d.getIntrinsicWidth();
			dh = Math.max(thumbHeight, d.getIntrinsicHeight());
		}

		dw += getPaddingLeft() + getPaddingRight();
		dh += getPaddingTop() + getPaddingBottom();

		setMeasuredDimension(resolveSize(dw, widthMeasureSpec),
				resolveSize(dh, heightMeasureSpec));
	}
	
	private int getSelectedDot() {
		for(Dot dot: mDots) {
			if(dot.isSelected)
				return mDots.indexOf(dot);
		}
		return -1;
	}

	/**
	 * dot coordinates.
	 */
	private void initDotsCoordinates() {
		float intervalWidth = (getWidth() - (mThumb.getRadius() * 2))
				/ (mDots.size() - 1);
		int selectDotPos = 0;
		for (Dot dot : mDots) {
			dot.mX = (int) (mThumbWidth / 2 + intervalWidth * (dot.id));
			if(dot.isSelected){
				selectDotPos = dot.mX;
			}
		}
		if(intervalWidth>0&&needResetSelectPosition){ //如果初始化时控件大小已知，则把初始位置也设置一下
			changeThumbPosition(selectDotPos);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		initDotsCoordinates();
	}

	/**
	 * Sets a listener to receive events when a list item is clicked.
	 * 
	 * @param clickListener
	 *            Listener to register
	 * 
	 * @see ListView#setOnItemClickListener(OnItemClickListener)
	 */
	public void setOnItemClickListener(
			OnItemClickListener clickListener) {
		mItemClickListener = clickListener;
	}

}
