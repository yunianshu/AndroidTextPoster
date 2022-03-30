package com.coolstar.makeposter.widget.comboSeekBar;

/**
 * Created by 纪广兴 on 2016/2/16.
 */
public class Dot {
    public int id;
    public int mX;
    public String text;
    public boolean isSelected = false;

    @Override
    public boolean equals(Object o) {
        return ((Dot) o).id == id;
    }
}
