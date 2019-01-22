/*
 * *********************************************************
 *   author   colin
 *   company  telchina
 *   email    wanglin2046@126.com
 *   date     19-1-19 下午2:35
 * ********************************************************
 */
package com.telchina.tharcgiscore.layermgr;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.telchina.tharcgiscore.GisMapView;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 绘画图层管理抽象类
 */
public abstract class AbstractGraphicsOverlayMgr {

    protected HashMap<String, GraphicsOverlay> mapOverlay = new HashMap<>();//所有特征图层的集合
    protected GisMapView mapView;

    public AbstractGraphicsOverlayMgr(GisMapView mapView) {
        this.mapView = mapView;
    }

    public ArcGISMap getArcMap() {
        return mapView.getMap();
    }

    public Basemap getBaseMap() {
        return mapView.getMap().getBasemap();
    }

    public GraphicsOverlay getGraphicsLayer(int index) {
        return mapView.getGraphicsOverlays().get(index);
    }

    /**
     * 根据名称获取图层
     */
    public GraphicsOverlay getGraphicsLayer(String key) {
        return mapOverlay.get(key);
    }

    /**
     * 增加图层
     */
    public boolean addGraphicsLayer(String key, GraphicsOverlay overlay) {
        mapOverlay.put(key, overlay);
        return mapView.getGraphicsOverlays().add(overlay);
    }

    public void addGraphicsLayer(int index, String key, GraphicsOverlay overlay) {
        mapOverlay.put(key, overlay);
        mapView.getGraphicsOverlays().add(index, overlay);
    }

    /**
     * 移除图层，可传入多个图层
     */
    public void removeGraphicsOverlay(String... keys) {
        if (keys == null) {
            return;
        }

        Set<Map.Entry<String, GraphicsOverlay>> entrySet = mapOverlay.entrySet();
        for (String key : keys) {
            for (Map.Entry<String, GraphicsOverlay> entry : entrySet) {
                if (entry.getKey().equals(key)) {
                    mapView.getGraphicsOverlays().remove(entry.getValue());
                    break;
                }
            }
        }
    }

    /**
     * 移除所有图层
     */
    public void removeAllGraphicsOverlay() {
        mapView.getGraphicsOverlays().clear();
        mapOverlay.clear();
    }

    /**
     * 设置是否显示图层，可传入多个图层
     */
    public void setGraphicsOverlayVisible(boolean isVisible, String... keys) {
        if (keys == null) {
            return;
        }

        Set<Map.Entry<String, GraphicsOverlay>> entrySet = mapOverlay.entrySet();
        for (Map.Entry<String, GraphicsOverlay> entry : entrySet) {
            for (String key : keys) {
                if (key.equals(entry.getKey())) {
                    if (mapView.getGraphicsOverlays().contains(entry.getValue())) {
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
    public void setAllGraphicsOverlayVisible(boolean isVisible) {
        Set<Map.Entry<String, GraphicsOverlay>> entrySet = mapOverlay.entrySet();
        for (Map.Entry<String, GraphicsOverlay> entry : entrySet) {
            if (mapView.getGraphicsOverlays().contains(entry.getValue())) {
                entry.getValue().setVisible(isVisible);
            }
        }
    }

    /**
     * 除了传入的值，其他的都设置为相反
     */
    public void setGraphicsOverlayVisibleAndOtherInverse(boolean isVisible, String... keys) {
        Set<Map.Entry<String, GraphicsOverlay>> entrySet = mapOverlay.entrySet();
        for (Map.Entry<String, GraphicsOverlay> entry : entrySet) {
            if (mapView.getGraphicsOverlays().contains(entry.getValue())) {
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
