package com.coolstar.makeposter.widget.textposter;

import android.graphics.ColorMatrix;

/**
 * 定义一组预定义好的颜色矩阵效果
 * Created by 纪广兴 on 2016/2/3.
 */
public class ColorMatrixFactory {
    private static final float[] COLORMATRIX_DEFAULT     = {1, 0, 0, 0, 0,
                                                            0, 1, 0, 0, 0,
                                                            0, 0, 1, 0, 0,
                                                            0, 0, 0, 1, 0};
//    private static final float[] COLORMATRIX_OLDPHOTO    = {-0.36F, 1.691F, -0.32F, 0, 0, //标准旧照片效果，偏黄色了点
//                                                            0.325F, 0.398F, 0.275F, 0, 0,
//                                                            0.79F, 0.796F, -0.76F, 0, 0,
//                                                            0, 0, 0, 1, 0};
    private static final float[] COLORMATRIX_OLDPHOTO    = {0.393F, 0.769F, 0.189F, 0, -10, //在标准的基础上，把颜色偏移值设置了一下，不那么黄
                                                            0.349F, 0.686F, 0.168F, 0, -10,
                                                            0.272F, 0.534F, 0.131F, 0, -20,
                                                            0, 0, 0, 1, 0};
    private static final float[] COLORMATRIX_BLACKWHITE  = {0.33F, 0.59F, 0.11F, 0, 0,
                                                            0.33F, 0.59F, 0.11F, 0, 0,
                                                            0.33F, 0.59F, 0.11F, 0, 0,
                                                            0, 0, 0, 1, 0};
    private static final float[] COLORMATRIX_DRAK        = {0.5F, 0, 0, 0, 0,
                                                            0, 0.5F, 0, 0, 0,
                                                            0, 0, 0.5F, 0, 0,
                                                            0, 0, 0, 1, 0};
    private static final float[] COLORMATRIX_CONTRAST    ={1.3F, 0, 0, 0, 0,
                                                            0, 1.3F, 0, 0, 0,
                                                            0, 0, 1.3F, 0, 0,
                                                            0, 0, 0, 1, 0};
//    private static final float[] COLORMATRIX_CONTRAST    = {1.438F, -0.122F, -0.016F, 0, -0.03F, //此效果脸部太亮
//                                                            -0.062F, 1.378F, -0.016F, 0, 0.05F,
//                                                            -0.062F, -0.122F, 1.483F, 0, -0.02F,
//                                                            0, 0, 0, 1, 0};

    public static final int TYPE_DEFAULT        = 0;
    public static final int TYPE_DRAK           = 1;
    public static final int TYPE_OLDPHOTO       = 2;
    public static final int TYPE_BLACKWHITE     = 3;
    public static final int TYPE_CONTRAST       = 4;


    /**
     * 通过传入类型参数，返回指定效果的矩阵
     * @param type：预先定义好的矩阵类型
     * @return
     */
    public static ColorMatrix buildColorMatrix(int type){
        switch (type){
            case TYPE_DEFAULT:
                return new ColorMatrix(COLORMATRIX_DEFAULT);
            case TYPE_OLDPHOTO:
                return new ColorMatrix(COLORMATRIX_OLDPHOTO);
            case TYPE_BLACKWHITE:
                return new ColorMatrix(COLORMATRIX_BLACKWHITE);
            case TYPE_DRAK:
                return new ColorMatrix(COLORMATRIX_DRAK);
            case TYPE_CONTRAST:
                return new ColorMatrix(COLORMATRIX_CONTRAST);
        }
        return null;
    }


    public static final int DIR_NEXT = 1;
    public static final int DIR_PREV = -1;
    private static int[] MATRIX_ARR = {TYPE_DEFAULT,TYPE_DRAK,TYPE_OLDPHOTO,TYPE_BLACKWHITE,TYPE_CONTRAST};
    private static String[] COLORMATRIX_TITLE = {"默认","变暗","老照片","黑白","变亮"};
    private static int MATRIX_INDEX = 0;        //保存用户当前选择的颜色数组索引
    public static void resetIndex(){
        MATRIX_INDEX = 0;
    }

    public static String getSelectedMatrixTitle(){
        int index = MATRIX_INDEX%COLORMATRIX_TITLE.length;
        return COLORMATRIX_TITLE[index];
    }

    /**
     * 从定义好的矩阵数组中，选择一个颜色效果
     * @param direction：方向选择，在数组中是向前（1），还是向后（-1）
     * @return
     */
    public static ColorMatrix selectColorMatrix(int direction){
        int arrIndex = MATRIX_INDEX+direction;
        if(arrIndex<0){
            arrIndex = MATRIX_ARR.length-1;
        }else if(arrIndex>=MATRIX_ARR.length){
            arrIndex = 0;
        }
        MATRIX_INDEX = arrIndex;
        return buildColorMatrix(MATRIX_INDEX);  //这里要保证顺序，把索引当类型使用
    }

    public static float[] buildColorMatrixArr(int type){
        switch (type){
            case TYPE_DEFAULT:
                return COLORMATRIX_DEFAULT;
            case TYPE_OLDPHOTO:
                return COLORMATRIX_OLDPHOTO;
            case TYPE_BLACKWHITE:
                return COLORMATRIX_BLACKWHITE;
            case TYPE_DRAK:
                return COLORMATRIX_DRAK;
            case TYPE_CONTRAST:
                return COLORMATRIX_CONTRAST;
        }
        return null;
    }

    public static float[] selectColorMatrixArr(int direction){
        int arrIndex = MATRIX_INDEX+direction;
        if(arrIndex<0){
            arrIndex = MATRIX_ARR.length-1;
        }else if(arrIndex>=MATRIX_ARR.length){
            arrIndex = 0;
        }
        MATRIX_INDEX = arrIndex;
        return buildColorMatrixArr(MATRIX_INDEX);  //这里要保证顺序，把索引当类型使用
    }
}
