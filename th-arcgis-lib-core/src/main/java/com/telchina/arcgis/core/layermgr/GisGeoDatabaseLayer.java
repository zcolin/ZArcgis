/*
 * *********************************************************
 *   author   colin
 *   company  telchina
 *   email    wanglin2046@126.com
 *   date     19-1-28 下午3:51
 * ********************************************************
 */

package com.telchina.arcgis.core.layermgr;

import android.content.Context;

import com.esri.arcgisruntime.data.Geodatabase;
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.telchina.arcgis.core.GisMapView;
import com.zcolin.gui.ZDialogAsyncProgress;

import java.util.List;

/**
 * 特征图层管理类
 */
public class GisGeoDatabaseLayer extends GisFeatureLayer {
    public GisGeoDatabaseLayer(GisMapView gisMapView) {
        super(gisMapView);
    }

    /**
     * 根据key值批量从geodatabase中查询添加特征图层并添加到地图上
     */
    public void addFeatureLayerFromGeoDatabase(Geodatabase geoDatabase, String... keys) {
        List<GeodatabaseFeatureTable> list = geoDatabase.getGeodatabaseFeatureTables();
        if (keys == null || keys.length == 0) {
            for (int i = list.size() - 1; i >= 0; i--) {
                GeodatabaseFeatureTable gdbFeatureTable = list.get(i);
                if (gdbFeatureTable.hasGeometry()) {
                    FeatureLayer featureLayer = new FeatureLayer(gdbFeatureTable);
                    featureLayer.setLabelsEnabled(true);
                    String tableName = gdbFeatureTable.getTableName();
                    addLayer(tableName, featureLayer);
                }
            }
        } else {
            for (String key : keys) {
                for (int i = list.size() - 1; i >= 0; i--) {
                    GeodatabaseFeatureTable gdbFeatureTable = list.get(i);
                    if (gdbFeatureTable.getTableName().equals(key) && gdbFeatureTable.hasGeometry()) {
                        FeatureLayer featureLayer = new FeatureLayer(gdbFeatureTable);
                        featureLayer.setLabelsEnabled(true);
                        String tableName = gdbFeatureTable.getTableName();
                        addLayer(tableName, featureLayer);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 有进度条，异步添加图层
     */
    public void addFeatureLayerFromGeoDatabase(Context context, Geodatabase geoDatabase, GisOnLoadFinishListener listener, String... keys) {
        ZDialogAsyncProgress.instance(context).setDoInterface(new ZDialogAsyncProgress.DoInterface() {
            @Override
            public ZDialogAsyncProgress.ProcessInfo onDoInback() {
                addFeatureLayerFromGeoDatabase(geoDatabase, keys);
                return null;
            }

            @Override
            public void onPostExecute(ZDialogAsyncProgress.ProcessInfo info) {
                if (listener != null) {
                    listener.onLoadFinish();
                }
            }
        }).show();
    }
}
