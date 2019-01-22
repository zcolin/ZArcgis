/*
 * *********************************************************
 *   author   colin
 *   company  telchina
 *   email    wanglin2046@126.com
 *   date     19-1-8 下午4:28
 * ********************************************************
 */

package com.telchina.tharcgiscore.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

/**
 * 皮肤辅助类
 */
public class ThemeUtil {
    public static int getColor(Context context, int colorRes) {
        TypedArray array = context.getTheme().obtainStyledAttributes(new int[]{colorRes});
        int color = array.getColor(0, Color.WHITE);
        array.recycle();
        return color;
    }

    public static int[] getColors(Context context, int[] colorRes) {
        TypedArray array = context.getTheme().obtainStyledAttributes(colorRes);
        int[] colors = new int[colorRes.length];
        for (int i = 0; i < colorRes.length; i++) {
            colors[i] = array.getColor(i, Color.WHITE);
        }
        array.recycle();
        return colors;
    }

    public static ColorStateList getColorStateList(Context context, int stateListRes) {
        TypedArray array = context.getTheme().obtainStyledAttributes(new int[]{stateListRes});
        ColorStateList colorStateList = array.getColorStateList(0);
        array.recycle();
        return colorStateList;
    }

    public static ColorStateList[] getColorStateLists(Context context, int[] stateListRes) {
        TypedArray array = context.getTheme().obtainStyledAttributes(stateListRes);
        ColorStateList[] colorStateLists = new ColorStateList[stateListRes.length];
        for (int i = 0; i < colorStateLists.length; i++) {
            colorStateLists[i] = array.getColorStateList(i);
        }
        array.recycle();
        return colorStateLists;
    }

    public static Drawable getDrawable(Context context, int drawableRes) {
        TypedArray array = context.getTheme().obtainStyledAttributes(new int[]{drawableRes});
        Drawable drawable = array.getDrawable(0);
        array.recycle();
        return drawable;
    }

    public static Drawable[] getDrawables(Context context, int[] drawableRes) {
        TypedArray array = context.getTheme().obtainStyledAttributes(drawableRes);
        Drawable[] drawables = new Drawable[drawableRes.length];
        for (int i = 0; i < drawableRes.length; i++) {
            drawables[i] = array.getDrawable(i);
        }
        array.recycle();
        return drawables;
    }

    public static int getResource(Context context, int styleRes) {
        TypedArray array = context.getTheme().obtainStyledAttributes(new int[]{styleRes});
        int styles = array.getResourceId(0, -1);
        array.recycle();
        return styles;
    }


}
