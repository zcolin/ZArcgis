/*
 * *********************************************************
 *   author   colin
 *   email    wanglin2046@126.com
 *   date     19-1-23 下午2:47
 * ********************************************************
 */

package com.zcolin.arcgis.core.layermgr;

import android.content.Context;

import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.zcolin.arcgis.core.GisMapView;
import com.zcolin.gui.ZDialogAsyncProgress;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 业务图层管理抽象类
 */
public abstract class GisAbstractOperationalLayer {

    protected HashMap<String, Layer> mapLayer = new HashMap<>();//所有特征图层的集合
    protected GisMapView mapView;

    public GisAbstractOperationalLayer(GisMapView mapView) {
        this.mapView = mapView;
    }

    public ArcGISMap getArcMap() {
        return mapView.getMap();
    }

    public Basemap getBaseMap() {
        return mapView.getMap().getBasemap();
    }

    public Layer getOperationalLayer(int index) {
        return getArcMap().getOperationalLayers().get(index);
    }

    /**
     * 根据名称获取图层
     */
    public Layer getLayer(String key) {
        return mapLayer.get(key);
    }

    public boolean addLayer(String key, Layer layer) {
        mapLayer.put(key, layer);
        return getArcMap().getOperationalLayers().add(layer);
    }

    public void addLayer(int index, String key, Layer layer) {
        mapLayer.put(key, layer);
        getArcMap().getOperationalLayers().add(index, layer);
    }

    /**
     * 有进度条，异步添加图层
     */
    public void addLayerAsync(Context context, String key, Layer layer, GisOnLoadFinishListener listener) {
        HashMap<String, Layer> hashMap = new HashMap<>(1);
        hashMap.put(key, layer);
        addLayerAsync(context, hashMap, listener);
    }

    /**
     * 有进度条，异步添加图层
     */
    public void addLayerAsync(Context context, HashMap<String, Layer> mapLayer, GisOnLoadFinishListener listener) {
        ZDialogAsyncProgress.instance(context).setDoInterface(new ZDialogAsyncProgress.DoInterface() {
            @Override
            public ZDialogAsyncProgress.ProcessInfo onDoInback() {
                for (Map.Entry<String, Layer> stringLayerEntry : mapLayer.entrySet()) {
                    addLayer(stringLayerEntry.getKey(), stringLayerEntry.getValue());
                }
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
     * 移除图层，可传入多个图层
     */
    public void removeLayer(String... keys) {
        if (keys == null) {
            return;
        }

        Set<Map.Entry<String, Layer>> entrySet = mapLayer.entrySet();
        for (String key : keys) {
            for (Map.Entry<String, Layer> entry : entrySet) {
                if (entry.getKey().equals(key)) {
                    getArcMap().getOperationalLayers().remove(entry.getValue());
                    break;
                }
            }
        }
    }

    /**
     * 移除所有图层
     */
    public void removeAllLayer() {
        getArcMap().getOperationalLayers().clear();
        mapLayer.clear();
    }

    /**
     * 设置是否显示图层，可传入多个图层
     */
    public void setLayerVisible(boolean isVisible, String... keys) {
        if (keys == null) {
            return;
        }

        Set<Map.Entry<String, Layer>> entrySet = mapLayer.entrySet();
        for (Map.Entry<String, Layer> entry : entrySet) {
            for (String key : keys) {
                if (key.equals(entry.getKey())) {
                    if (getArcMap().getOperationalLayers().contains(entry.getValue())) {
                        entry.getValue().setVisible(isVisible);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 设置所有图层可见性
     */
    public void setAllLayerVisible(boolean isVisible) {
        Set<Map.Entry<String, Layer>> entrySet = mapLayer.entrySet();
        for (Map.Entry<String, Layer> entry : entrySet) {
            if (getArcMap().getOperationalLayers().contains(entry.getValue())) {
                entry.getValue().setVisible(isVisible);
            }
        }
    }

    /**
     * 除了传入的值，其他的都设置为相反
     */
    public void setLayerVisibleAndOtherInverse(boolean isVisible, String... keys) {
        Set<Map.Entry<String, Layer>> entrySet = mapLayer.entrySet();
        for (Map.Entry<String, Layer> entry : entrySet) {
            if (getArcMap().getOperationalLayers().contains(entry.getValue())) {
                if (keys == null) {
                    entry.getValue().setVisible(!isVisible);
                } else {
                    boolean isHave = false;
                    for (String key : keys) {
                        if (entry.getKey().equals(key)) {
                            entry.getValue().setVisible(isVisible);
                            isHave = true;
                            break;
                        }
                    }

                    if (!isHave) {
                        entry.getValue().setVisible(!isVisible);
                    }
                }
            }
        }
    }
}
