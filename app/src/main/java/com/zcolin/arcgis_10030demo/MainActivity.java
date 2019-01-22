package com.zcolin.arcgis_10030demo;

import android.os.Bundle;

import com.telchina.tharcgiscore.GisMapOperateView;
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
    
    
    private void initData(){
        gisMapOperateView.initMapViews();
    }
}
