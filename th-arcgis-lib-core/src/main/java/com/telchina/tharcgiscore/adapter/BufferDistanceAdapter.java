/*
 * *********************************************************
 *   author   zxt
 *   company  telchina
 *   email    zhuxuetong123@163.com
 *   date     18-12-21 上午9:13
 * ********************************************************
 */

package com.telchina.tharcgiscore.adapter;

import android.widget.TextView;

import com.telchina.tharcgiscore.R;
import com.telchina.tharcgiscore.util.ThemeUtil;
import com.zcolin.gui.zrecyclerview.BaseRecyclerAdapter;

/**
 * 缓冲区半径adapter
 */

public class BufferDistanceAdapter extends BaseRecyclerAdapter<String> {


    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.th_arcgis_buffer_distance_item;
    }

    @Override
    public void setUpData(CommonHolder holder, int position, int viewType, String data) {
        TextView tvBuffer=getView(holder,R.id.tv_buffer);
        tvBuffer.setTextAppearance(tvBuffer.getContext(), ThemeUtil.getResource(tvBuffer.getContext(), R.attr.calloutTextSubPrimaryStyle));
        tvBuffer.setText(data);
    }
}
