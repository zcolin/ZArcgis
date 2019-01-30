/*
 * *********************************************************
 *   author   colin
 *   company  telchina
 *   email    wanglin2046@126.com
 *   date     19-1-22 上午9:06
 * ********************************************************
 */

package com.telchina.arcgis.core;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.loadable.LoadStatusChangedListener;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.BackgroundGrid;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.ViewpointChangedListener;
import com.esri.arcgisruntime.util.ListenableList;
import com.telchina.arcgis.core.tiledservice.BaseTiledLayer;
import com.telchina.arcgis.core.tiledservice.BaseTiledParam;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装Arcgis MapView
 */
public class GisMapView extends FrameLayout {
    private static final String MAP_LICENSE        = "runtimelite,1000,rud7416273699,none,4N5X0H4AH7AH6XCFK036";
    public static final  int    TYPE_BASETILED_VEC = 0;//普通地图
    public static final  int    TYPE_BASETILED_IMG = 1;//影像地图

    private BaseTiledParam tileParam;//底图参数类
    private GisMapConfig   gisMapConfig;//地图相关配置类

    private List<SingleTapListener> singleTapListener = new ArrayList<>();
    private List<DoubleTapListener> doubleTapListener = new ArrayList<>();
    private List<LongPressListener> longPressListener = new ArrayList<>();

    private int     curMapType;
    private MapView mapView;

    static {
        ArcGISRuntimeEnvironment.setLicense(MAP_LICENSE);
    }

    public GisMapView(@NonNull Context context) {
        this(context, null);
    }

    public GisMapView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mapView = new MapView(context);
        mapView.setMap(new ArcGISMap());
        mapView.setBackgroundGrid(new BackgroundGrid(Color.rgb(239, 239, 239), Color.rgb(239, 239, 239), 0.1f, 15f));
        mapView.setAttributionTextVisible(false);
        mapView.setOnTouchListener(new DefaultMapViewOnTouchListener(getContext(), mapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (singleTapListener.size() > 0) {
                    for (SingleTapListener tapListener : singleTapListener) {
                        if (tapListener.onSingleTap(e)) {
                            break;
                        }
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                if (doubleTapListener.size() > 0) {
                    for (DoubleTapListener tapListener : doubleTapListener) {
                        tapListener.onDoubleTap(e);
                    }
                    return true;
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                if (longPressListener.size() > 0) {
                    for (LongPressListener pressListener : longPressListener) {
                        pressListener.onLongPress(e);
                    }
                }
            }
        });
        addView(mapView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 初始化地图
     */
    public void init(GisMapConfig gisMapConfig) {
        this.gisMapConfig = gisMapConfig;
        this.tileParam = gisMapConfig.getTileParam();
        curMapType = gisMapConfig.getBaseMapType();
        setInitViewPoint();
        switchBaseTiled();
    }

    /**
     * 设置初始缩放
     */
    private void setInitViewPoint() {
        Point point = new Point(tileParam.getCenterPoint()[0], tileParam.getCenterPoint()[1], SpatialReference.create(tileParam.getWkid()));
        Viewpoint viewpoint = new Viewpoint(point, tileParam.getInitScale());
        getMap().setInitialViewpoint(viewpoint);
    }

    /**
     * 切换底图类型（普通/影像）
     */
    private void switchBaseTiled() {
        BaseTiledLayer[] baseLayer = curMapType == TYPE_BASETILED_VEC ? tileParam.getVecBaseTileLayer() : tileParam.getImgBaseTileLayer();
        if (baseLayer != null && baseLayer.length > 0) {
            Basemap basemap = new Basemap();
            for (BaseTiledLayer aBaseLayer : baseLayer) {
                aBaseLayer.loadAsync();
                aBaseLayer.addDoneLoadingListener(() -> basemap.getBaseLayers().add(aBaseLayer));
            }
            setBaseMap(basemap);
        }
    }

    public GisMapConfig getGisMapConfig() {
        return gisMapConfig;
    }

    /**
     * 移动到初始化地图的中心点，缩放比为初始缩放比
     */
    public void zoomToCenterScale() {
        Point mPoint = new Point(tileParam.getCenterPoint()[0], tileParam.getCenterPoint()[1], SpatialReference.create(tileParam.getWkid()));
        zoomToPoint(mPoint, tileParam.getInitScale());
    }

    /**
     * 移动到地图指定点
     *
     * @param point 坐标点
     */
    public void zoomToPoint(Point point) {
        zoomToPoint(point, mapView.getMapScale());
    }

    /**
     * 移动到地图指定点
     *
     * @param x 坐标点x坐标
     * @param y 坐标点也坐标
     */
    public void zoomToPoint(double x, double y) {
        zoomToPoint(new Point(x, y, SpatialReference.create(tileParam.getWkid())));
    }

    /**
     * 移动到地图指定点，并指定缩放比
     *
     * @param x     坐标点x坐标
     * @param y     坐标点也坐标
     * @param scale 缩放比，当scale==0.0D时，scale=当前缩放比
     */
    public void zoomToPoint(double x, double y, double scale) {
        zoomToPoint(new Point(x, y, SpatialReference.create(tileParam.getWkid())), scale);
    }

    /**
     * 移动到地图指定点，并指定缩放比
     *
     * @param point 坐标点
     * @param scale 缩放比，当scale==0.0D时，scale=当前缩放比
     */
    public void zoomToPoint(Point point, double scale) {
        mapView.setViewpointAsync(new Viewpoint(point, scale == 0.0D ? mapView.getMapScale() : scale));
    }

    /**
     * 放大
     */
    public void zoomIn() {
        mapView.setViewpointScaleAsync(mapView.getMapScale() * 2);
    }

    /**
     * 缩小
     */
    public void zoomOut() {
        mapView.setViewpointScaleAsync(mapView.getMapScale() * 0.5);
    }

    /**
     * 移动到地图指定几何图形的中点
     *
     * @param geometry 几何图形的Geometry对象
     */
    public void zoomToGeometry(Geometry geometry) {
        Point mPoint = getCenterPoint(geometry);
        zoomToPoint(mPoint, mapView.getMapScale());
    }

    /**
     * 移动到地图指定几何图形的中点，并指定缩放比
     *
     * @param geometry 几何图形的Geometry对象
     * @param scale    缩放比，当scale==0.0D时，scale=当前缩放比
     */
    public void zoomToGeometry(Geometry geometry, double scale) {
        Point mPoint = getCenterPoint(geometry);
        zoomToPoint(mPoint, scale);
    }

    /**
     * 获取当前底图类型
     */
    public int getCurBaseMapType() {
        return curMapType;
    }


    /**
     * 获取元素的中心坐标点
     */
    public Point getCenterPoint(Feature feature) {
        return getCenterPoint(feature.getGeometry());
    }

    /**
     * 获取元素的中心坐标点
     */
    public Point getCenterPoint(Geometry geometry) {
        return geometry.getExtent().getCenter();
    }

    /**
     * 获取绘画图层集合
     */
    public ListenableList<GraphicsOverlay> getGraphicsOverlays() {
        return mapView.getGraphicsOverlays();
    }

    /**
     * 获取地图对象
     */
    public ArcGISMap getMap() {
        return mapView.getMap();
    }

    /**
     * 获取底图对象
     */
    public Basemap getBaseMap() {
        return getMap().getBasemap();
    }

    /**
     * 设置底图对象
     */
    public void setBaseMap(Basemap baseMap) {
        getMap().setBasemap(baseMap);
    }

    /**
     * 获取底图图层集合
     */
    public LayerList getBaseLayers() {
        return getBaseMap() == null ? null : mapView.getMap().getBasemap().getBaseLayers();
    }

    /**
     * 获取业务图层集合
     */
    public LayerList getOperationalLayers() {
        return mapView.getMap().getOperationalLayers();
    }

    /**
     * 获取mapView对象
     */
    public MapView getMapView() {
        return mapView;
    }

    /**
     * 设置初始缩放比
     */
    public void setInitScale(double initScale) {
        gisMapConfig.setInitScale(initScale);
    }

    /**
     * 获取初始缩放比
     */
    public double getInitScale() {
        return tileParam.getInitScale();
    }

    /**
     * 设置中心点
     */
    public void setCenterPoint(double[] centerPoint) {
        gisMapConfig.setCenterPoint(centerPoint);
    }

    /**
     * 获取中心点
     */
    public Point getMapCenterPoint() {
        double[] point = tileParam.getCenterPoint();
        return new Point(point[0], point[1], SpatialReference.create(tileParam.getWkid()));
    }

    public Callout getCallout() {
        return mapView.getCallout();
    }


    public void pause() {
        mapView.pause();
    }

    public void resume() {
        mapView.resume();
    }

    /**
     * 设置地图放大缩小监听
     */
    public void addViewpointChangedListener(ViewpointChangedListener viewpointChangedListener) {
        mapView.addViewpointChangedListener(viewpointChangedListener);
    }

    /**
     * 移除设置地图放大缩小监听
     */
    public void removeViewpointChangedListener(ViewpointChangedListener viewpointChangedListener) {
        mapView.removeViewpointChangedListener(viewpointChangedListener);
    }

    /**
     * 设置状态监听
     */
    public void addLoadStatusChangedListener(LoadStatusChangedListener onStatusChangedListener) {
        getMap().addLoadStatusChangedListener(onStatusChangedListener);
    }

    /**
     * 移除状态监听
     */
    public void removeLoadStatusChangedListener(LoadStatusChangedListener onStatusChangedListener) {
        getMap().removeLoadStatusChangedListener(onStatusChangedListener);
    }

    /**
     * 设置加载监听
     */
    public void addDoneLoadingListener(Runnable runnable) {
        getMap().addDoneLoadingListener(runnable);
    }

    /**
     * 移除加载监听
     */
    public void removeDoneLoadingListener(Runnable runnable) {
        getMap().removeDoneLoadingListener(runnable);
    }


    public void addSingleTapListener(SingleTapListener singleTapListener) {
        if (!this.singleTapListener.contains(singleTapListener)) {
            this.singleTapListener.add(singleTapListener);
        }
    }

    public void addSingleTapListener(SingleTapListener singleTapListener, int priority) {
        if (!this.singleTapListener.contains(singleTapListener)) {
            this.singleTapListener.add(priority, singleTapListener);
        }
    }

    public void addDoubleTapListener(DoubleTapListener doubleTapListener) {
        if (!this.doubleTapListener.contains(doubleTapListener)) {
            this.doubleTapListener.add(doubleTapListener);
        }
    }

    public void addDoubleTapListener(DoubleTapListener doubleTapListener, int priority) {
        if (!this.doubleTapListener.contains(doubleTapListener)) {
            this.doubleTapListener.add(priority, doubleTapListener);
        }
    }

    public void addLongPressListener(LongPressListener longPressListener) {
        if (!this.longPressListener.contains(longPressListener)) {
            this.longPressListener.add(longPressListener);
        }
    }

    public void addLongPressListener(LongPressListener longPressListener, int priority) {
        if (!this.longPressListener.contains(longPressListener)) {
            this.longPressListener.add(priority, longPressListener);
        }
    }

    public void removeSingleTapListener(SingleTapListener singleTapListener) {
        if (this.singleTapListener.contains(singleTapListener)) {
            this.singleTapListener.remove(singleTapListener);
        }
    }

    public void removeDoubleTapListener(DoubleTapListener doubleTapListener) {
        if (this.doubleTapListener.contains(doubleTapListener)) {
            this.doubleTapListener.remove(doubleTapListener);
        }
    }

    public void removeLongPressListener(LongPressListener longPressListener) {
        if (this.longPressListener.contains(longPressListener)) {
            this.longPressListener.remove(longPressListener);
        }
    }

    public double getMapScale() {
        return mapView.getMapScale();
    }

    public int getWkid() {
        return tileParam.getWkid();
    }

    public interface SingleTapListener {
        /**
         * @return 是否拦截后续请求
         */
        boolean onSingleTap(MotionEvent p);
    }

    /**
     * @return 是否拦截后续请求
     */
    public interface DoubleTapListener {
        boolean onDoubleTap(MotionEvent p);
    }

    /**
     * @return 是否拦截后续请求
     */
    public interface LongPressListener {
        boolean onLongPress(MotionEvent p);
    }
}
