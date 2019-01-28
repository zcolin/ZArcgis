/*
 * *********************************************************
 *   author   colin
 *   company  telchina
 *   email    wanglin2046@126.com
 *   date     19-1-28 下午2:36
 * ********************************************************
 */

package com.telchina.tharcgiscore.layermgr;

import android.graphics.Color;

import com.esri.arcgisruntime.data.ShapefileFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.telchina.tharcgiscore.GisMapView;

/**
 * shape图层管理类
 */
public class GisShapeFileLayer extends GisFeatureLayer {

    public GisShapeFileLayer(GisMapView mapView) {
        super(mapView);
    }

    public void addFeatureLayerFromShapefile(ShapefileFeatureTable shapefileFeatureTable) {
        shapefileFeatureTable.addDoneLoadingListener(() -> {
            FeatureLayer featureLayer = new FeatureLayer(shapefileFeatureTable);
            addLayer(shapefileFeatureTable.getTableName(), featureLayer);

            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 1.0f);
            SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.YELLOW, lineSymbol);
            SimpleRenderer renderer = new SimpleRenderer(fillSymbol);
            featureLayer.setRenderer(renderer);
            featureLayer.setSelectionWidth(5);//设置选中颜色
            featureLayer.setSelectionColor(Color.GREEN);
        });
        shapefileFeatureTable.loadAsync();
    }
}
