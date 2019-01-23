/*
 * *********************************************************
 *   author   colin
 *   company  telchina
 *   email    wanglin2046@126.com
 *   date     19-1-23 下午2:48
 * ********************************************************
 */

package com.telchina.tharcgiscore.layermgr;

import android.content.Context;

import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.layers.SublayerList;
import com.esri.arcgisruntime.loadable.LoadStatusChangedListener;
import com.telchina.tharcgiscore.GisMapView;
import com.zcolin.frame.util.StringUtil;
import com.zcolin.gui.ZDialogAsyncProgress;
import com.zcolin.gui.ZDialogProgress;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 动态图层管理类
 */
public class MapImageLayerMgr extends AbstractOperationalLayerMgr {
    private ZDialogProgress progressDialog;

    public MapImageLayerMgr(GisMapView gisMapView) {
        super(gisMapView);
        progressDialog = new ZDialogProgress(gisMapView.getContext());
    }

    /**
     * 有进度条，异步添加图层
     */
    public void addDynamicLayersAsync(Context context, HashMap<String, String> layers, FeatureLayerMgr.OnLoadFinishListener listener) {
        ZDialogAsyncProgress.instance(context).setDoInterface(new ZDialogAsyncProgress.DoInterface() {
            @Override
            public ZDialogAsyncProgress.ProcessInfo onDoInback() {
                removeAllLayer();
                addDynamicLayers(layers);
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
     * 批量添加动态图层
     */
    public void addDynamicLayers(HashMap<String, String> layers) {
        if (layers == null) {
            return;
        }

        Set<Map.Entry<String, String>> entrySet = layers.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            addDynamicLayer(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 有进度条，异步添加图层
     */
    public void addDynamicLayerAsync(Context context, String layKey, String dynamicLayerUrl, LoadStatusChangedListener listener) {
        ZDialogAsyncProgress.instance(context).setDoInterface(new ZDialogAsyncProgress.DoInterface() {
            @Override
            public ZDialogAsyncProgress.ProcessInfo onDoInback() {
                addDynamicLayer(layKey, dynamicLayerUrl, listener);
                return null;
            }

            @Override
            public void onPostExecute(ZDialogAsyncProgress.ProcessInfo info) {

            }
        }).show();
    }

    /**
     * 添加图层时，没有传layerKey 默认的layerKey=dynamicLayer，
     * 相同layerKey的图层只能添加一次，否则之后添加的会覆盖上一次添加的图层
     */
    private void addDynamicLayer(String layKey, String dynamicLayerUrl) {
        addDynamicLayer(layKey, dynamicLayerUrl, null);
    }

    /**
     * 添加图层时，没有传layerKey 默认的layerKey=dynamicLayer，
     * 相同layerKey的图层只能添加一次，否则之后添加的会覆盖上一次添加的图层
     */
    private void addDynamicLayer(String layKey, String dynamicLayerUrl, LoadStatusChangedListener listener) {
        ArcGISMapImageLayer layer = new ArcGISMapImageLayer(dynamicLayerUrl);
        /*若果laykey为空，默认为dynamicLayer*/
        if (StringUtil.isEmpty(layKey)) {
            layKey = "dynamicLayer";
        }
        addLayer(layKey, layer);
        if (listener != null) {
            layer.addLoadStatusChangedListener(listener);
        }
    }

    /**
     * 添加图层时，没有传layerKey 默认的layerKey=dynamicLayer，
     * 相同layerKey的图层只能添加一次，否则之后添加的会覆盖上一次添加的图层
     */
    public void addDynamicLayer(String layKey, Layer layer, LoadStatusChangedListener listener) {
        /*若果laykey为空，默认为dynamicLayer*/
        if (StringUtil.isEmpty(layKey)) {
            layKey = "dynamicLayer";
        }
        addLayer(layKey, layer);
        if (listener != null) {
            layer.addLoadStatusChangedListener(listener);
        }
    }

    /**
     * @return 返回该图层下的所有子图层信息
     */
    public SublayerList getDynamicLayerChildLayer(String layerKey) {
        ArcGISMapImageLayer layer = getDynamicLayer(layerKey);
        return layer.getSublayers();
    }

    /**
     * 设置图层layer显示隐藏
     *
     * @param layerKey 图层的key或图层名称
     * @param layer    需要显示隐藏得图层
     *                 1、该图层需要显示
     *                 a、arcgis已添加该图层，先删除，再添加，最后添加缓冲区、绘画和高亮图层
     *                 b、arcgis未添加该图层，直接添加，最后添加缓冲区、绘画和高亮图层
     *                 2、该图层需要隐藏
     *                 a、arcgis已添加该图层，隐藏图层
     *                 b、arcgis未添加该图层，不做操作
     */
    public void setLayerVisibleOrHide(String layerKey, Layer layer, GraphicsLayerMgr graphicsLayerMgr) {
        //        graphicsLayerMgr.clearAllGraphic();
        //        if (!StringUtil.isEmpty(layerKey) && layer != null) {
        //            if (layer.isVisible()) {//需要显示图层
        //                if (mapLayer.get(layerKey) != null && arcGISMap.getOperationalLayers().contains(mapLayer.get(layerKey))) {
        //                    if (mapLayer.get(layerKey).isVisible()) {
        //                        if (mapLayer.get(layerKey).getOpacity() == layer.getOpacity()) {//主要用于已添加图层，透明度也没改变的显示
        //                            mapLayer.get(layerKey).setVisible(true);
        //                            arcGISMap.getLayerByURL(layer.getUrl()).setVisible(true);
        //                        } else {//主要用于图层树中已添加图层的图层透明度调整
        //                            mapLayer.get(layerKey).setOpacity(layer.getOpacity());
        //                            arcGISMap.getLayerByURL(layer.getUrl()).setOpacity(layer.getOpacity());
        //                        }
        //                    } else {//原来隐藏或者未添加的，添加到最上层
        //                        arcGISMap.getOperationalLayers().remove(mapLayer.get(layerKey));
        //                        mapLayer.remove(layerKey);
        //                        addLayer(layerKey, layer);
        //                        graphicsLayerMgr.addGraphicLayers();
        //                    }
        //                } else {
        //                    addLayer(layerKey, layer);
        //                    graphicsLayerMgr.addGraphicLayers();
        //                }
        //            } else {
        //                if (mapLayer.get(layerKey) != null && arcGISMap.getOperationalLayers().contains(mapLayer.get(layerKey))) {
        //                    mapLayer.get(layerKey).setVisible(false);
        //                    arcGISMap.getLayerByURL(layer.getUrl()).setVisible(false);
        //                }
        //            }
        //        }
    }

    /**
     * 图例控制集合要素的显示与隐藏
     *
     * @param condition 图例控制的图层及筛选条件
     * @param layerUrls 地图已添加的显示图层url
     *                  <p>
     *                  1、当condition不为空时，按legendLayers种的图例数据控制几何要素的显示
     *                  <p>
     *                  2、当condition为空时，就要显示layers中的所有几何要素
     */
    public void setLegendControl(Map<String, String> condition, List<String> layerUrls) {
        /*当condition不为空时，按condition种的图例数据控制几何要素的显示*/
        //        if (condition != null && condition.size() > 0) {
        //            for (Object obj : condition.entrySet()) {
        //                Map<Integer, String> layerDefs = new HashMap<>();
        //                        /*根据图层url获取到已经添加的图层服务*/
        //                ArcGISMapImageLayer layer = getDynamicLayer((String) ((Map.Entry) obj).getKey());
        //                if (layer != null && layer.getSublayers() != null) {
        //                            /*将查询条件，按子图层的id，插入到每一个图层*/
        //                    for (int i = 0; i < layer.getSublayers().size(); i++) {
        //                        layerDefs.put(i, (String) ((Map.Entry) obj).getValue());
        //                    }
        //                    layer.setLayerDefinitions(layerDefs);
        //                    layer.refresh();
        //                }
        //            }
        //        } else {/*condition为空时，就要显示condition中的所有几何要素*/
        //            if (layerUrls != null && layerUrls.size() > 0) {
        //                for (String layerUrl : layerUrls) {
        //                    Map<Integer, String> layerDefs = new HashMap<>();
        //                    ArcGISMapImageLayer layer = getDynamicLayer(layerUrl);
        //                    if (layer != null) {
        //                        SublayerList childLayers = layer.getSublayers();
        //                        if (childLayers != null && childLayers.size() > 0) {
        //                            for (int i = 0; i < childLayers.size(); i++) {
        //                                layerDefs.put(i, "1=1");
        //                            }
        //                            layer.setLayerDefinitions(layerDefs);
        //                        }
        //                        layer.setVisible(true);
        //                    }
        //                }
        //            }
        //        }
    }

    /**
     * 根据layerKey查获取动态图层
     */
    public ArcGISMapImageLayer getDynamicLayer(String layerKey) {
        if (StringUtil.isEmpty(layerKey)) {
            return (ArcGISMapImageLayer) mapLayer.get("dynamicLayer");
        } else {
            return (ArcGISMapImageLayer) mapLayer.get(layerKey);
        }
    }

    /**
     * 获取默认添加的动态图层
     */
    public ArcGISMapImageLayer getDefaultDynamicLayer() {
        return (ArcGISMapImageLayer) mapLayer.get("dynamicLayer");
    }
}
