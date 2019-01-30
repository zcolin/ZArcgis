/*
 * *********************************************************
 *   author   colin
 *   company  telchina
 *   email    wanglin2046@126.com
 *   date     19-1-23 下午2:47
 * ********************************************************
 */

package com.telchina.arcgis.core.layermgr;

import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.telchina.arcgis.core.GisMapView;
import com.zcolin.frame.util.LogUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 特征图层管理类
 */
public class GisFeatureLayer extends GisAbstractOperationalLayer {

    public GisFeatureLayer(GisMapView mapView) {
        super(mapView);
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

    /**
     * 增加要素
     */
    public void addFeatureToLayer(FeatureTable featureTable, Geometry geometry, HashMap<String, Object> attributes) {
        if (featureTable.isEditable() && featureTable.canAdd()) {
            Feature feature = featureTable.createFeature();
            feature.setGeometry(geometry);
            if (attributes != null && attributes.size() > 0) {
                for (Map.Entry<String, Object> stringObjectEntry : attributes.entrySet()) {
                    feature.getAttributes().put(stringObjectEntry.getKey(), stringObjectEntry.getValue());
                }
            }

            featureTable.addFeatureAsync(feature);
        } else {
            LogUtil.e("deleteFeatureFromLayer", "要素不支持新增");
        }
    }

    /**
     * 增加要素
     */
    public void addFeatureToLayer(FeatureTable featureTable, Feature feature) {
        featureTable.addFeatureAsync(feature);
    }

    /**
     * 增加要素
     */
    public void addFeatureToLayer(FeatureTable featureTable, Iterable<Feature> features) {
        featureTable.addFeaturesAsync(features);
    }

    /**
     * 更新要素
     */
    public void updateFeatureToLayer(FeatureTable featureTable, Feature feature) {
        if (featureTable.isEditable() && featureTable.canUpdate(feature)) {
            featureTable.updateFeatureAsync(feature);
        } else {
            LogUtil.e("deleteFeatureFromLayer", "要素不支持新增");
        }
    }

    /**
     * 更新要素
     */
    public void updateFeatureToLayer(FeatureTable featureTable, Iterable<Feature> features) {
        featureTable.updateFeaturesAsync(features);
    }

    /**
     * 删除要素
     */
    public void deleteFeatureFromLayer(FeatureTable featureTable, Feature feature) throws Exception {
        if (featureTable.isEditable() && featureTable.canDelete(feature)) {
            featureTable.deleteFeatureAsync(feature);
        } else {
            LogUtil.e("deleteFeatureFromLayer", "要素不支持删除");
        }
    }

    /**
     * 删除要素
     */
    public void deleteFeatureFromLayer(FeatureTable featureTable, Iterable<Feature> features) throws Exception {
        featureTable.deleteFeaturesAsync(features);
    }
}
