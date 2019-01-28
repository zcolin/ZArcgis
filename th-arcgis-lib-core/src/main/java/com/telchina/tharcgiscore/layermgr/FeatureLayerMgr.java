/*
 * *********************************************************
 *   author   colin
 *   company  telchina
 *   email    wanglin2046@126.com
 *   date     19-1-23 下午2:47
 * ********************************************************
 */

package com.telchina.tharcgiscore.layermgr;

import android.content.Context;

import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.Geodatabase;
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.telchina.tharcgiscore.GisMapView;
import com.zcolin.gui.ZDialogAsyncProgress;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 特征图层管理类
 */
public class FeatureLayerMgr extends AbstractOperationalLayerMgr {
    private String geoDatabase;

    public FeatureLayerMgr(GisMapView mapView, String geoDatabase) {
        super(mapView);
        this.geoDatabase = geoDatabase;
    }

    public Geodatabase getGeoDatabase(String path) {
        return new Geodatabase(path);
    }

    /**
     * 根据名称获取特征图层
     * <p>
     * 如果key对应的是FeatureLayer ，则直接返回，否则返回null
     */
    public FeatureLayer getFeatureLayer(String key) {
        if (getLayer(key) != null && getLayer(key) instanceof FeatureLayer) {
            return (FeatureLayer) getLayer(key);
        }
        return null;
    }

    /**
     * 根据key值批量从geodatabase中查询添加特征图层并添加到地图上
     */
    public void addFeatureLayer(String... keys) {
        Geodatabase localGdb = getGeoDatabase(geoDatabase);
        if (localGdb == null || keys == null) {
            return;
        }

        List<GeodatabaseFeatureTable> list = localGdb.getGeodatabaseFeatureTables();
        for (String key : keys) {
            for (GeodatabaseFeatureTable gdbFeatureTable : list) {
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

    /**
     * 有进度条，异步添加图层
     */
    public void addFeatureLayerAsync(Context context, OnLoadFinishListener listener, String... keys) {
        ZDialogAsyncProgress.instance(context).setDoInterface(new ZDialogAsyncProgress.DoInterface() {
            @Override
            public ZDialogAsyncProgress.ProcessInfo onDoInback() {
                addFeatureLayer(keys);
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

    /**
     * 添加矢量数据
     */
    public void addAllFeatureLayer() {
        Geodatabase localGdb = getGeoDatabase(geoDatabase);
        if (localGdb == null) {
            return;
        }
        List<GeodatabaseFeatureTable> list = localGdb.getGeodatabaseFeatureTables();
        for (int i = list.size() - 1; i >= 0; i--) {
            GeodatabaseFeatureTable gdbFeatureTable = list.get(i);
            if (gdbFeatureTable.hasGeometry()) {
                FeatureLayer featureLayer = new FeatureLayer(gdbFeatureTable);
                featureLayer.setLabelsEnabled(true);
                String tableName = gdbFeatureTable.getTableName();
                addLayer(tableName, featureLayer);
            }
        }
    }

    /**
     * 有进度条，异步添加图层
     */
    public void addAllFeatureLayerAsync(Context context, OnLoadFinishListener listener) {
        ZDialogAsyncProgress.instance(context).setDoInterface(new ZDialogAsyncProgress.DoInterface() {
            @Override
            public ZDialogAsyncProgress.ProcessInfo onDoInback() {
                addAllFeatureLayer();
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

    /**
     * 清除缓存选择的对象
     */
    public void clearSectionFeatureLayer() {
        Set<Map.Entry<String, Layer>> entry = mapLayer.entrySet();
        for (Map.Entry<String, Layer> stringFeatureLayerEntry : entry) {
            if (stringFeatureLayerEntry.getValue() instanceof FeatureLayer) {
                ((FeatureLayer) stringFeatureLayerEntry.getValue()).clearSelection();
            }
        }
    }

    /**
     * 根据点坐标查询feature要素
     */
    public Feature getFeatureByPoint(FeatureLayer featureLayer, Point point) {
        FeatureQueryResult featureResult = getFeatureResultByGeometry(featureLayer, new Envelope(point, 10, 10));
        for (Feature feature : featureResult) {
            return feature;
        }
        return null;
    }

    /**
     * 根据位置信息查询获取特征图层元素信息
     */
    public FeatureQueryResult getFeatureResultByGeometry(FeatureLayer layer, Geometry geometry) {
        if (layer == null || geometry == null) {
            return null;
        }

        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setGeometry(geometry);
        Future<FeatureQueryResult> result = layer.selectFeaturesAsync(queryParameters, FeatureLayer.SelectionMode.NEW);

        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据名字模糊查询获取特征图层元素信息
     */
    public FeatureQueryResult getFeatureResultLike(FeatureLayer layer, String key, String value) {
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setWhereClause(key + " like '%" + value + "%'");
        Future<FeatureQueryResult> result = layer.selectFeaturesAsync(queryParameters, FeatureLayer.SelectionMode.NEW);

        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据UUID获取特征图层元素信息
     */
    public FeatureQueryResult getFeatureResult(FeatureLayer layer, String key, String value) {
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setWhereClause(key + "='" + value + "'");
        Future<FeatureQueryResult> result = layer.selectFeaturesAsync(queryParameters, FeatureLayer.SelectionMode.NEW);

        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取所有特征图层元素信息
     */
    public FeatureQueryResult getAllFeatureResult(FeatureLayer layer) {
        if (layer == null) {
            return null;
        }

        QueryParameters queryParameters = new QueryParameters();
        Future<FeatureQueryResult> result = layer.selectFeaturesAsync(queryParameters, FeatureLayer.SelectionMode.NEW);

        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
