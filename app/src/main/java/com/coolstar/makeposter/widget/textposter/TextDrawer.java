package com.coolstar.makeposter.widget.textposter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.core.view.MotionEventCompat;

import com.coolstar.makeposter.R;
import com.coolstar.makeposter.utils.AlbumUtils;
import com.coolstar.makeposter.utils.FileUtils;
import com.coolstar.makeposter.utils.LogMgr;
import com.coolstar.makeposter.utils.ThreadBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.greenrobot.event.EventBus;


/**
 * 文字画布控件
 * Created by 纪广兴 on 2016/1/19.
 */
public class TextDrawer extends View {
    public static interface OnPosterEventListener{
        void onPosterFinished(Bitmap bmp);
    }

    public TextDrawer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    public TextDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public TextDrawer(Context context) {
        super(context);
        init(context);
    }

    public void setGestureListener(OnGestureChangedListener gestureListener) {
        this.gestureListener = gestureListener;
    }

    //拖拽操作=================================================================================
    public static interface OnGestureChangedListener{
        void onHorizontalGesture(float distanceX, float totalWidth);
        void onVerticalGesture(float distanceY, float totalHeight);
    }
    private OnGestureChangedListener gestureListener;
    private boolean isGestureMode = false;
    private boolean isDragMode = false;
    private  float downX,downY;
    private boolean canScrollY(float moveY, RectF txtRect) {
        if((txtRect.top+moveY)<0){
            return false;
        }else if((txtRect.bottom+moveY)>(getHeight()-logoHeight)){
            return false;
        }else{
            return true;
        }
    }
    private boolean canScrollX(float moveX, RectF txtRect) {
        if((txtRect.left+moveX)<0){
            return false;
        }else if((txtRect.right+moveX)>getWidth()){
            return false;
        }else{
            return true;
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(poster!=null){
            RectF txtRect = new RectF(poster.getTextRect());
            txtRect.offset(mScrollX,mScrollY);
            switch (MotionEventCompat.getActionMasked(event)) {
                case MotionEvent.ACTION_DOWN:
                    downX = event.getX();
                    downY = event.getY();
                    getParent().requestDisallowInterceptTouchEvent(true);
                    if(txtRect.contains(event.getX(),event.getY()))
                    {
                        isDragMode = true;
                    }else{
                        isGestureMode = true;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(isDragMode){
                        float moveX = event.getX();
                        if(canScrollX(moveX - downX,txtRect)){
                            mScrollX += moveX - downX;
                            invalidate();
                            downX = moveX;
                        }
                        float moveY = event.getY();
                        if(canScrollY(moveY - downY,txtRect)){
                            mScrollY += moveY - downY;
                            invalidate();
                            downY = moveY;
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if(isGestureMode){
                        float moveX = event.getX();
                        float moveY = event.getY();
                        float dx = Math.abs(moveX-downX);
                        float dy = Math.abs(moveY-downY);
                        if(dx>dy){
                            if(dx>(getWidth()/4.0f)){
                                if(gestureListener!=null){
                                    gestureListener.onHorizontalGesture(moveX-downX,getWidth());
                                }
                            }
                        }else{
                            if(dy>(getHeight()/5.0f)){
                                if(gestureListener!=null){
                                    gestureListener.onVerticalGesture(moveY-downY,getHeight());
                                }
                            }
                        }
                    }
                    isGestureMode = false;
                    isDragMode = false;
                    getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return isDragMode||isGestureMode;
        }else{
            return super.onTouchEvent(event);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        onDrawBackBmp(canvas);
        //画文字，文字位置使用scroll变量偏移
        canvas.save();
        canvas.translate(mScrollX,mScrollY);
        if(poster!=null){
            poster.onPostDraw(canvas);
        }
        canvas.restore();
        //画标题文字与酷我logo
        drawLogoAndTitle(canvas);  //画标题和logo
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if(oldw>0&&oldw!=w){
            posterNeedReSize = true;
        }
        super.onSizeChanged(w, h, oldw, oldh);
        if(bmpDrawer!=null){
            bmpDrawer.setParentSize(w,h);
        }
        if(w>1&&h>1&posterNeedReSize&poster!=null){
            poster.updateTextRect(getTextDragWidth(),getTextDragHeight());
            RectF txtRect = poster.getTextRect();
            mScrollX = (w-txtRect.width())/2;
            mScrollY = (h-txtRect.height()-logoHeight)/2;
        }
    }
    //背景图绘制=================================================================================
    IBmpDrawer bmpDrawer;
    public void setArtistBackground(Bitmap bmp){
       if(bmpDrawer!=null){
           bmpDrawer.setDrawBmp(bmp);
           invalidate();
       }
    }
    private void onDrawBackBmp(Canvas canvas){
        if(bmpDrawer!=null){
            bmpDrawer.onDrawBmp(canvas);
        }
    }
    public void changeArtistBmpColorArr(float[] martixarr){
        if(bmpDrawer!=null){
            bmpDrawer.changeColorMatrix(martixarr);
            invalidate();
        }
    }
    public void changeArtistBmpBrightness(float value){
        if(bmpDrawer!=null){
            bmpDrawer.changeBrightness(value);
            invalidate();
        }
    }
    public void setArtistBmpScaleType(ImageView.ScaleType type){
        if(bmpDrawer!=null){
            bmpDrawer.setBmpScaleType(type);
            invalidate();
        }
    }
    //初始化变量区=================================================================================
    private IPoster poster;
    private boolean posterNeedReSize;  //保存变量，在设置时如果宽高还没设置，则修改此标记，在宽高有效——再updateRect一次
    private float mScrollX;
    private float  mScrollY;
    private float mTitleMarginValue;  //保存从xml中设置的标题二边的空白宽度
    private void init(Context context){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB){
            this.setLayerType(LAYER_TYPE_SOFTWARE, null);  //描边、阴影等效果不支持硬件加速
        }
        mTitleMarginValue = context.getResources().getDimension(R.dimen.txtDrawer_title_margin);
        mScrollX = 0;
        mScrollY = 0;
        poster = new HorizontalPoster();
        titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setColor(Color.WHITE);
        titlePaint.setTextSize(getResources().getDimension(R.dimen.text_size_18));
//        titlePaint.setShadowLayer(2,2,2,Color.argb(160,0,0,0));
        titlePaint.setStyle(Paint.Style.FILL);
        gradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gradientPaint.setStyle(Paint.Style.FILL);
        posterNeedReSize = false;

        bmpDrawer = new BmpDrawer();

    }
    //文字设置=================================================================================
    public boolean hasText(){
        return oriTexts!=null;
    }
    private String[] oriTexts = null;
    public void setTextAlign(Layout.Alignment align){
        if(poster!=null){
            poster.setTextAlign(align);
            invalidate();
        }
    }

    public void setTextColor(int color){
        if(poster!=null){
            poster.setTextColor(color);
            invalidate();
        }
    }

    public void setTextSize(float size){
        if(poster!=null){
            float curWidth = getWidth();
            float curHeight = getTextDragHeight();
            if(curWidth<1||curHeight<1){
                poster.setTextSize(size,getTextDragWidth(),getTextDragHeight());
                posterNeedReSize = true;
            }else{
                RectF txtRect = poster.getTextRect();
                float oldLeft = txtRect.width()/2;
                float oldTop = txtRect.height()/2;
                poster.setTextSize(size,getTextDragWidth(),getTextDragHeight());
                txtRect = poster.getTextRect();
                mScrollX -= (txtRect.width()/2-oldLeft);
                mScrollY -= (txtRect.height()/2-oldTop);
                if(mScrollX<0){
                    mScrollX = 0;
                }else if((mScrollX+txtRect.width())>curWidth){
                    mScrollX = curWidth-txtRect.width();
                }
                if(mScrollY<0){
                    mScrollY = 0;
                }else if((mScrollY+txtRect.height())>curHeight){
                    mScrollY = curHeight-txtRect.height()-logoHeight;
                }
                invalidate();
            }
        }
    }
    public void setTexts(String[] textArrs){
        if(textArrs==null){
            oriTexts = null;
            if(poster!=null){
                poster.setTexts(null,getTextDragWidth(),getTextDragHeight());
                invalidate();
            }
            return;
        }
        if(poster!=null){
            oriTexts = new String[textArrs.length];
            System.arraycopy(textArrs,0,oriTexts,0,textArrs.length); //深度拷贝字符串数组，因为传到poster有可能会被修改，转换poster时，要重新使用oriTexts给新的poster赋值
            float curWidth = getWidth();
            float curHeight = getTextDragHeight();
            if(curWidth<1||curHeight<1){
                poster.setTexts(textArrs,getTextDragWidth(),getTextDragHeight());
                posterNeedReSize = true;
            }else{
                poster.setTexts(textArrs,getTextDragWidth(),getTextDragHeight());
                RectF txtRect = poster.getTextRect();
                mScrollX = (curWidth-txtRect.width())/2;
                mScrollY = (curHeight-txtRect.height()-logoHeight)/2;
                invalidate();
            }
        }
    }
    public void resetPosterTexts(){
        if(poster!=null&&oriTexts!=null){
            float curWidth = getWidth();
            float curHeight = getTextDragHeight();

            String[] tmpTexts = new String[oriTexts.length];
            System.arraycopy(oriTexts,0,tmpTexts,0,oriTexts.length);
            poster.setTexts(tmpTexts,getTextDragWidth(),getTextDragHeight());
            RectF txtRect = poster.getTextRect();
            mScrollX = (curWidth-txtRect.width())/2;
            mScrollY = (curHeight-txtRect.height()-logoHeight)/2;
            invalidate();
        }
    }
    public void setShadow(boolean enabled){
        if(poster!=null){
            poster.setShadow(enabled);
            invalidate();
        }
    }
    public void setFont(Typeface font){
        if(poster!=null){
            poster.setTextFont(font,getTextDragWidth(),getTextDragHeight());
            invalidate();
        }
    }
    public Layout.Alignment getTextAlign(){
        if(poster!=null){
            return poster.getTextAlign();
        }
        return Layout.Alignment.ALIGN_NORMAL;
    }

    public int getTextColor(){
        if(poster!=null){
            return poster.getTextColor();
        }
        return Color.WHITE;
    }
    public float getTextSize(){
        if(poster!=null){
            return poster.getTextSize();
        }
        return 0;
    }
    public Typeface getTextFont(){
        if(poster!=null){
            return poster.getTextFont();
        }
        return null;
    }
    public boolean isHasShadow() {
        if(poster!=null){
            return poster.isHasShadow();
        }
        return false;
    }
    //文字和图标========================================================================
    private static final int V_SAPCE = 10;  //标题与logo上下各有的间距
    private Bitmap kuwoLogo;
    private String kuwoMusicInfo;  //歌曲名称与歌手名
    private float logoHeight;  //保存标题与logo在图上的高度，可用于限制歌词移动范围
    private Paint titlePaint;
    private RectF gradientRect;
    private Paint gradientPaint;
    private float titleLogoTotalHeight;
    public void setMusicTitleAndLogo(String title, Bitmap logoBmp){
        kuwoLogo = logoBmp;
        kuwoMusicInfo = title;
        float bmpHeight = 0f;
        if(logoBmp!=null){
            bmpHeight = logoBmp.getHeight();
        }
        float txtHeight = titlePaint.getFontMetrics().bottom-titlePaint.getFontMetrics().top;
        titleLogoTotalHeight = V_SAPCE+bmpHeight+V_SAPCE+V_SAPCE+txtHeight+V_SAPCE;
        gradientRect = new RectF(0,getHeight()-titleLogoTotalHeight,getWidth(),getHeight());
    }
    private void drawLogoAndTitle(Canvas canvas){
        canvas.save();
        drawGradientBackground(canvas);
        float logoTop = drawLogo(canvas);
        logoHeight = drawTitle(logoTop,canvas);
        canvas.restore();
    }

    private void drawGradientBackground(Canvas canvas) {
        if(gradientRect==null){
            return;
        }
        if(gradientRect.width()<1||gradientRect.height()<1|| Math.abs(gradientRect.height()-titleLogoTotalHeight)>1){  //初始化
            gradientRect.set(0,getHeight()-titleLogoTotalHeight,getWidth(),getHeight());
        }
        if(gradientPaint.getShader()==null){
            LinearGradient shader = new LinearGradient(
                    0, gradientRect.top,
                    0, gradientRect.bottom,
                    Color.TRANSPARENT,
                    Color.parseColor("#7F000000"),
                    Shader.TileMode.CLAMP);
            gradientPaint.setShader(shader);
        }
        canvas.drawRect(gradientRect,gradientPaint);
    }

    private float drawTitle(float logoTop, Canvas canvas) {
        if(!TextUtils.isEmpty(kuwoMusicInfo)){
            float titleTargetWidth = bmpDrawer.getBmpScaleRect().width()-mTitleMarginValue-mTitleMarginValue; //有时有缩放显示的情况,文字也要同步变窄,默认是和控件一样宽
            float txtHeight = titlePaint.getFontMetrics().bottom-titlePaint.getFontMetrics().top;

            float txtTop = logoTop-txtHeight-V_SAPCE*2; //文字高度是在图标上方，文字高度上下各留20间距的区域中，居中显示
            RectF txtRect = new RectF(mTitleMarginValue,txtTop,getWidth()-mTitleMarginValue,logoTop);
            Paint.FontMetricsInt fontMetrics = titlePaint.getFontMetricsInt();
            //判断字符串长度是不是超长，超长转为...
            String drawTxt = kuwoMusicInfo;
            float txtWidth = titlePaint.measureText(kuwoMusicInfo);
            if(txtWidth>titleTargetWidth){
                float tailWidth = titlePaint.measureText("...");
                float[] wordWidths = new float[kuwoMusicInfo.length()];
                titlePaint.getTextWidths(kuwoMusicInfo,wordWidths);
                float tmpWidth = tailWidth;
                int wordIdx = kuwoMusicInfo.length()-1;  //默认是显示全部字符
                for(int i=0;i<wordWidths.length;i++){
                    if((tmpWidth+wordWidths[i])>titleTargetWidth){
                        wordIdx = i;
                        break;
                    }else{
                        tmpWidth += wordWidths[i];
                    }
                }
                drawTxt = kuwoMusicInfo.substring(0,wordIdx)+"...";  //截取一部分标题内容
            }
            float baseline = (txtRect.bottom + txtRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
            // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
            titlePaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(drawTxt, txtRect.centerX(), baseline, titlePaint);
//            canvas.drawRect(txtRect,titlePaint);
            return getHeight() - txtRect.top; //高度减去文字的上边坐标，得到文字与logo的总高度
        }
        return 0f;
    }

    private float drawLogo(Canvas canvas) {
        if(kuwoLogo!=null){
            int bmpWidth = kuwoLogo.getWidth();
            int bmpHeight = kuwoLogo.getHeight();
            float logoTop = getHeight()-bmpHeight-V_SAPCE-V_SAPCE;
            float logoLeft = (getWidth()-bmpWidth)/2;
            canvas.drawBitmap(kuwoLogo,logoLeft,logoTop,null);
            return logoTop-V_SAPCE;  //留出上下各20的间距
        }
        return 0;
    }
    //创建海报文件=======================================================================


    public void asyncBuildPostPicture(final String filePath, final boolean saveSdFile){
        ThreadBuilder.getInstance().runThread( new Runnable() {
            @Override
            public void run() {
                final Bitmap bmp = saveSdFile?buildPostPicture(filePath):buildPostPicture(); //根据标识，决定调用哪个函数生成图片
                if(saveSdFile==false){
                    AlbumUtils.addBitmapToAlbum(getContext(),bmp);
                }
                EventBus.getDefault().post(bmp);
            }
        });
    }

    /**
     * 此方法只生成图片，不生成本地文件
     * @return
     */
    public Bitmap buildPostPicture(){
        if(bmpDrawer==null){
            return null;
        }
        return innerBuildPost(null,getWidth(),getHeight(),false);
    }
    /**
     * 创建海报图片文件并保存到文件中，返回是否保存成功的标识
     * @param filePath
     * @return
     */
    public Bitmap buildPostPicture(String filePath) {
        if(bmpDrawer==null){
            return null;
        }
        return innerBuildPost(filePath,getWidth(),getHeight(),true);
    }

    private Bitmap innerBuildPost(String filePath, int outWidth, int outHeight, boolean saveFile){
        if(saveFile&& FileUtils.isExist(filePath)){
            if(false == FileUtils.deleteFile(filePath)){
                return null;
            }
        }
        try {
            Bitmap outBmp = Bitmap.createBitmap(outWidth,outHeight, Bitmap.Config.ARGB_8888);
            LogMgr.d("PostBmp","width:"+outWidth+",Height:"+outWidth);
            Canvas canvas = new Canvas(outBmp);
            ImageView.ScaleType oldScaleType =  bmpDrawer.getBmpScaleType();
            bmpDrawer.outputDraw(canvas,outWidth,outHeight);
            float scaleX = outWidth*1.0f/getWidth();  //获取屏幕与真实图片的比例
            float scaleY = outHeight*1.0f/getHeight();  //获取屏幕与真实图片的比例
            LogMgr.d("PostBmp","scaleX:"+scaleX+",scaleY:"+scaleY);
            Matrix matrix = canvas.getMatrix();
            canvas.save();
            if(Math.abs(scaleX-1.0f)>0.01|| Math.abs(scaleY-1.0f)>0.01){
                matrix.setScale(scaleX,scaleY,0,0); //缩放处理显示与原图的位置关系
                canvas.setMatrix(matrix);
            }
            if(poster!=null){
                canvas.save();
                canvas.translate(mScrollX,mScrollY);
                poster.onPostDraw(canvas);
                canvas.restore();
            }
            drawLogoAndTitle(canvas);
            canvas.restore();
            if(saveFile){  //如果设置要保存文件，则保存到本地sd卡中，如果不要，则直接返回bitmap对象
                File bmpFile = new File(filePath);
                FileOutputStream fout = new FileOutputStream(bmpFile);
                outBmp.compress(Bitmap.CompressFormat.JPEG,100,fout);
                fout.flush();
                fout.close();
            }
            return outBmp;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable e){
            System.gc();
            e.printStackTrace();
        }
        return null;
    }

    public IPoster getPoster() {
        return poster;
    }
    public void setPoster(IPoster pter) {
        this.poster = pter;
        if(poster!=null){
            RectF txtRect = poster.getTextRect();
            mScrollX = (getWidth()-txtRect.width())/2;
            mScrollY = (getHeight()-txtRect.height()-logoHeight)/2;
            invalidate();
        }
    }

    public int getTextDragWidth(){
        if(bmpDrawer!=null){
            return bmpDrawer.getBmpScaleRect().width();
        }else{
            return getWidth();
        }
    }
    public int getTextDragHeight(){
        return (int) (getHeight()-logoHeight);
    }
}
