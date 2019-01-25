/*
 * *********************************************************
 *   author   colin
 *   company  telchina
 *   email    wanglin2046@126.com
 *   date     19-1-24 上午9:07
 * ********************************************************
 */

package com.telchina.tharcgiscore;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zcolin.frame.util.ScreenUtil;
import com.zcolin.gui.ZDialog;

/**
 * 选择图层显示dialog
 */
public class GisMapTypeDialog extends ZDialog implements View.OnClickListener {
    private int                                 baseMapType;
    private LinearLayout                        llSatellite;
    private TextView                            tvSatellite;
    private LinearLayout                        llVector;
    private TextView                            tvVector;
    private View                                llMapType;
    private ZDialogParamSubmitListener<Integer> onSubmitListener;

    public GisMapTypeDialog(Context context, int baseMapType, ZDialogParamSubmitListener<Integer> onSubmitListener) {
        super(context, R.layout.zarcgis_maptype, R.style.zarcgis_style_dialog_transparent);
        this.baseMapType = baseMapType;
        this.onSubmitListener = onSubmitListener;
        initView();
    }

    public GisMapTypeDialog setItemBackground(int mapTypeBg) {
        llVector.setBackgroundResource(mapTypeBg);
        llSatellite.setBackgroundResource(mapTypeBg);
        return this;
    }

    public GisMapTypeDialog setItemBackground(Drawable mapTypeBg) {
        llVector.setBackground(mapTypeBg);
        llSatellite.setBackground(mapTypeBg);
        return this;
    }

    public GisMapTypeDialog setItemTextColorStateList(int mapTypeTextSelector) {
        tvVector.setTextColor(getContext().getResources().getColorStateList(mapTypeTextSelector));
        tvSatellite.setTextColor(getContext().getResources().getColorStateList(mapTypeTextSelector));
        return this;
    }

    public GisMapTypeDialog setItemTextColorStateList(ColorStateList mapTypeTextSelector) {
        tvVector.setTextColor(mapTypeTextSelector);
        tvSatellite.setTextColor(mapTypeTextSelector);
        return this;
    }

    public GisMapTypeDialog setItemTextColor(int color) {
        tvVector.setTextColor(color);
        tvSatellite.setTextColor(color);
        return this;
    }

    private void initView() {
        setDialogBackground(android.R.color.transparent);
        setAnim(R.style.zarcgis_style_dialog_lefttoright);
        setGravity(Gravity.RIGHT);

        llMapType = findViewById(R.id.ll_maptype);
        llMapType.setOnClickListener(this);
        llSatellite = (LinearLayout) getView(R.id.ll_satellite);
        llSatellite.setOnClickListener(this);
        tvSatellite = (TextView) getView(R.id.tv_satellite);
        tvSatellite.setOnClickListener(this);
        llVector = (LinearLayout) getView(R.id.ll_vector);
        llVector.setOnClickListener(this);
        tvVector = (TextView) getView(R.id.tv_vector);
        tvVector.setOnClickListener(this);

        if (baseMapType == GisMapView.TYPE_BASETILED_IMG) {
            tvSatellite.setSelected(true);
            tvVector.setSelected(false);
            llSatellite.setSelected(true);
            llVector.setSelected(false);
        } else {
            tvSatellite.setSelected(false);
            tvVector.setSelected(true);
            llSatellite.setSelected(false);
            llVector.setSelected(true);
        }
    }

    /**
     * 设置窗口主要展示界面的高度
     * 就是去掉toobar 去掉tab的中心展示区域的高度
     */
    public void setContentHeight(int contentHeight) {
        setLayout(ScreenUtil.getScreenWidth(getContext()), contentHeight);
    }

    /**
     * 设置窗口主要展示界面的高度
     * 就是去掉toobar 去掉tab的中心展示区域的高度
     */
    public void setContentBackground(int barckgroundColor) {
        findViewById(R.id.ll_maptype).setBackgroundColor(barckgroundColor);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.ll_maptype) {
            dismiss();
        } else if (i == R.id.ll_satellite || i == R.id.tv_satellite) {
            tvSatellite.setSelected(true);
            tvVector.setSelected(false);
            llSatellite.setSelected(true);
            llVector.setSelected(false);
            onSubmitListener.submit(GisMapView.TYPE_BASETILED_IMG);
        } else if (i == R.id.ll_vector || i == R.id.tv_vector) {
            tvSatellite.setSelected(false);
            tvVector.setSelected(true);
            llSatellite.setSelected(false);
            llVector.setSelected(true);
            onSubmitListener.submit(GisMapView.TYPE_BASETILED_VEC);
        }
        this.dismiss();
    }

    public int[] getWidthHeight() {
        llMapType.measure(0, 0);
        return new int[]{llMapType.getMeasuredWidth(), llMapType.getMeasuredHeight()};
    }

}
