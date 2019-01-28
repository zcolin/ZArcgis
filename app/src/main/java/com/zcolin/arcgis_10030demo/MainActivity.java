package com.zcolin.arcgis_10030demo;

import android.graphics.Color;
import android.os.Bundle;

import com.telchina.tharcgiscore.GisMapOperateView;
import com.telchina.tharcgiscore.callout.GisCalloutMgr;
import com.zcolin.frame.app.BaseFrameActivity;

/**
 * Created by ZColin on 2019/1/21.
 */
public class MainActivity extends BaseFrameActivity {
    private GisMapOperateView gisMapOperateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        gisMapOperateView = findViewById(R.id.gismap_view);
        initData();
    }


    private void initData() {
        gisMapOperateView.initMapViews();
        GisCalloutMgr.instance(gisMapOperateView.getCallout(), mActivity, "我是一个callout")
                     .backgroundColor(Color.GREEN)
                     .borderColor(Color.RED)
                     .outSideTouchDismiss(gisMapOperateView.getGisMapView())
                     .show(gisMapOperateView.getMapCenterPoint());

        gisMapOperateView.drawText(gisMapOperateView.getMapCenterPoint(), "我是绘制的文字");
        gisMapOperateView.drawPictureMarker(gisMapOperateView.getMapCenterPoint(), getResources().getDrawable(R.drawable.ic_launcher));
    }
}
