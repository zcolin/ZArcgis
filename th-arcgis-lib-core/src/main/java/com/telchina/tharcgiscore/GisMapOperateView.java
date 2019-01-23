/*
 * *********************************************************
 *   author   colin
 *   company  telchina
 *   email    wanglin2046@126.com
 *   date     19-1-23 上午10:24
 * ********************************************************
 */

package com.telchina.tharcgiscore;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.loadable.LoadStatusChangedListener;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.telchina.tharcgiscore.layermgr.GraphicsLayerMgr;
import com.telchina.tharcgiscore.layermgr.GraphicsOverlayConfig;
import com.zcolin.frame.interfaces.ZSubmitListener;
import com.zcolin.frame.permission.PermissionHelper;
import com.zcolin.frame.permission.PermissionsResultAction;
import com.zcolin.frame.util.ToastUtil;
import com.zcolin.libamaplocation.LocationUtil;

import java.util.Map;


/**
 * 地图界面，操作按钮封装
 */
public class GisMapOperateView extends RelativeLayout {
    private GisMapView       gisMapView;
    private LocationUtil     locationUtil;
    private GraphicsLayerMgr graphicsLayerMgr;

    private ZSubmitListener            onClearLister;    //点击清除数据按钮监听
    private LocationUtil.OnGetLocation onGetLocationListener;//定位回调

    public GisMapOperateView(Context context) {
        this(context, null);
    }

    public GisMapOperateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.th_arcgis_view_gisoperate, this);
        initView();
    }

    private void initView() {
        gisMapView = findViewById(R.id.gis_mapview);
        findViewById(R.id.imb_layear_remove).setOnClickListener(v -> {
            if (onClearLister != null) {
                if (onClearLister.submit()) {
                    return;
                }
            }
            clearTempLayer();
        });
        findViewById(R.id.btn_reset).setOnClickListener(v -> zoomToCenterScale());
        findViewById(R.id.imb_map_zoomin).setOnClickListener(v -> gisMapView.zoomIn());
        findViewById(R.id.imb_map_zoomout).setOnClickListener(v -> gisMapView.zoomOut());
        findViewById(R.id.btn_get_lcoation).setOnClickListener(v -> location(onGetLocationListener));
        graphicsLayerMgr = new GraphicsLayerMgr(gisMapView);
    }

    /**
     * 初始化arcgis地图及地图相关的监听事件
     */
    public void initMapViews() {
        this.initMapViews(new GisMapConfig());
    }

    public void initMapViews(GisMapConfig gisMapConfig) {
        gisMapView.init(gisMapConfig);
        
          /*图层切换按钮组*/
        TextView tvMapTypeVec = findViewById(R.id.tv_maptype_vec);
        TextView tvMapTypeImg = findViewById(R.id.tv_maptype_img);
        tvMapTypeVec.setSelected(gisMapConfig.getBaseMapType() == GisMapView.TYPE_BASETILED_VEC);
        tvMapTypeImg.setSelected(gisMapConfig.getBaseMapType() == GisMapView.TYPE_BASETILED_IMG);
        tvMapTypeVec.setOnClickListener(v -> {
            if (gisMapConfig.getBaseMapType() != GisMapView.TYPE_BASETILED_VEC) {
                gisMapConfig.setBaseMapType(GisMapView.TYPE_BASETILED_VEC);
                gisMapView.init(gisMapConfig);
                tvMapTypeVec.setSelected(true);
                tvMapTypeImg.setSelected(false);
            }
        });
        tvMapTypeImg.setOnClickListener(v -> {
            if (gisMapConfig.getBaseMapType() != GisMapView.TYPE_BASETILED_IMG) {
                gisMapConfig.setBaseMapType(GisMapView.TYPE_BASETILED_IMG);
                gisMapView.init(gisMapConfig);
                tvMapTypeVec.setSelected(false);
                tvMapTypeImg.setSelected(true);
            }
        });
    }


    /**
     * 图层管理类
     */
    public GraphicsLayerMgr getGraphicsLayerMgr() {
        return graphicsLayerMgr;
    }

    /**
     * 地图控件
     */
    public GisMapView getGisMapView() {
        return gisMapView;
    }

    /**
     * 清除地图所有要素，包括drawlayer
     */
    public void clearAllGraphicsLayer() {
        graphicsLayerMgr.clearAll();
        gisMapView.getCallout().dismiss();
    }

    /**
     * 清除地图所有临时要素，不包括drawlayer
     */
    public void clearTempLayer() {
        graphicsLayerMgr.clearBufferLayer();
        graphicsLayerMgr.clearHighlightLayer();
        graphicsLayerMgr.clearLocationLayer();
        gisMapView.getCallout().dismiss();
    }

    /**
     * 清除按钮回调
     */
    public void setOnClearLister(ZSubmitListener onClearLister) {
        this.onClearLister = onClearLister;
    }

    /**
     * 设置状态监听
     */
    public void addStatusChangedListener(LoadStatusChangedListener onStatusChangedListener) {
        gisMapView.addLoadStatusChangedListener(onStatusChangedListener);
    }

    /**
     * 移除状态监听
     */
    public void removeStatusChangedListener(LoadStatusChangedListener onStatusChangedListener) {
        gisMapView.removeLoadStatusChangedListener(onStatusChangedListener);
    }

    /**
     * 设置加载监听
     */
    public void addDoneLoadingListener(Runnable runnable) {
        gisMapView.addDoneLoadingListener(runnable);
    }

    /**
     * 移除加载监听
     */
    public void removeDoneLoadingListener(Runnable runnable) {
        gisMapView.removeDoneLoadingListener(runnable);
    }

    public void addSingleTapListener(GisMapView.SingleTapListener singleTapListener) {
        gisMapView.addSingleTapListener(singleTapListener);
    }

    public void addDoubleTapListener(GisMapView.DoubleTapListener doubleTapListener) {
        gisMapView.addDoubleTapListener(doubleTapListener);
    }

    public void addLongPressListener(GisMapView.LongPressListener longPressListener) {
        gisMapView.addLongPressListener(longPressListener);
    }

    public void removeSingleTapListener(GisMapView.SingleTapListener singleTapListener) {
        gisMapView.removeSingleTapListener(singleTapListener);
    }

    public void removeDoubleTapListener(GisMapView.DoubleTapListener doubleTapListener) {
        gisMapView.removeDoubleTapListener(doubleTapListener);
    }

    public void removeLongPressListener(GisMapView.LongPressListener longPressListener) {
        gisMapView.removeLongPressListener(longPressListener);
    }

    /**
     * 获取元素的中心坐标点
     */
    public Point getCenterPoint(Feature feature) {
        return gisMapView.getCenterPoint(feature);
    }

    /**
     * 获取元素的中心坐标点
     */
    public Point getCenterPoint(Geometry geometry) {
        return gisMapView.getCenterPoint(geometry);
    }

    public Callout getCallout() {
        return gisMapView.getCallout();
    }

    /**
     * 获取地图初始缩放比
     */
    public double getInitScale() {
        return gisMapView.getInitScale();
    }

    /**
     * 获取地图的中心点坐标
     */
    public Point getMapCenterPoint() {
        return gisMapView.getMapCenterPoint();
    }

    public void pause() {
        gisMapView.pause();
    }

    public void resume() {
        gisMapView.resume();
    }

    /**
     * 缩放到预设的中心点
     */
    public void zoomToCenterScale() {
        gisMapView.zoomToCenterScale();
    }

    /**
     * 移动到指定位置
     */
    public void zoomToPoint(Point point) {
        gisMapView.zoomToPoint(point);
    }

    /**
     * 移动到指定位置
     */
    public void zoomToPoint(double x, double y) {
        gisMapView.zoomToPoint(x, y);
    }

    /**
     * 移动到指定位置
     */
    public void zoomToPoint(double x, double y, double scale) {
        gisMapView.zoomToPoint(x, y, scale);
    }

    /**
     * 移动到指定位置
     */
    public void zoomToGeometry(Geometry geometry) {
        gisMapView.zoomToGeometry(geometry);
    }


    /**
     * 设置中心点和缩放比
     */
    public void setResetScaleAndCenter(double resetScale, double[] resetCenter) {
        gisMapView.setInitScale(resetScale);
        gisMapView.setCenterPoint(resetCenter);
    }

    /**
     * 高亮某个区域
     */
    public void highLightGeometry(Geometry geometry) {
        highLightGeometry(geometry, 0);
    }

    /**
     * 高亮某个区域
     */
    public void highLightGeometry(Geometry geometry, @DrawableRes int pointPic) {
        graphicsLayerMgr.highLightGeometry(geometry, GraphicsOverlayConfig.instanceHighlight().setPointPic(pointPic));
    }

    /**
     * 高亮指定图层某个区域 以及指定颜色值
     *
     * @param geometry 区域地理信息
     */
    public void highLightGeometry(Geometry geometry, GraphicsOverlayConfig config) {
        graphicsLayerMgr.highLightGeometry(geometry, config);
    }

    /**
     * 高亮指定图层某个区域 以及指定颜色值
     *
     * @param graphicsLayer 制定图层图层（绘画图层、高亮图层）
     * @param geometry      区域地理信息
     */
    public void highLightGeometry(GraphicsOverlay graphicsLayer, Geometry geometry, GraphicsOverlayConfig config) {
        graphicsLayerMgr.highLightGeometry(graphicsLayer, geometry, config, false);
    }

    /**
     * 高亮指定图层某个区域 以及指定颜色值
     *
     * @param graphicsLayer 制定图层图层（绘画图层、高亮图层）
     * @param geometry      区域地理信息
     */
    public void highLightGeometry(GraphicsOverlay graphicsLayer, Geometry geometry, GraphicsOverlayConfig config, boolean isMoveToDest) {
        graphicsLayerMgr.highLightGeometry(graphicsLayer, geometry, config, isMoveToDest);
    }

    /**
     * 清除高亮
     */
    public void clearHighLightLayer() {
        graphicsLayerMgr.clearHighlightLayer();
    }

    public boolean drawLocationSymbol(double longitude, double latitude) {
        return graphicsLayerMgr.drawLocationSymbol(longitude, latitude);
    }

    public boolean drawLocationSymbol(double longitude, double latitude, long scale) {
        return graphicsLayerMgr.drawLocationSymbol(longitude, latitude, scale);
    }

    public boolean drawText(Point point, String string) {
        return graphicsLayerMgr.drawText(point, string);
    }

    public boolean drawText(Point point, String string, int color, int textsize) {
        return graphicsLayerMgr.drawText(point, string, color, textsize);
    }

    public boolean drawText(GraphicsOverlay graphicsLayer, Point point, String string) {
        return graphicsLayerMgr.drawText(graphicsLayer, point, string, Color.RED, 28);
    }

    public boolean drawText(GraphicsOverlay graphicsLayer, Point point, String string, int color, int textsize) {
        return graphicsLayerMgr.drawText(graphicsLayer, point, string, color, textsize);
    }

    public void clearLocationLayer() {
        graphicsLayerMgr.clearLocationLayer();
    }

    /**
     * 地图绘制元素
     */
    public void drawGeometry(Geometry geometry, GraphicsOverlayConfig config) {
        graphicsLayerMgr.drawGeometry(geometry, config);
    }

    /**
     * 地图绘制元素
     */
    public void drawGeometry(Geometry geometry, Map<String, Object> attr, GraphicsOverlayConfig config) {
        graphicsLayerMgr.drawGeometry(geometry, attr, config);
    }

    /**
     * 地图绘制元素
     */
    public void drawGeometry(GraphicsOverlay overlay, Geometry geometry, GraphicsOverlayConfig config) {
        graphicsLayerMgr.drawGeometry(overlay, geometry, null, config);
    }

    /**
     * 地图绘制元素
     */
    public void drawGeometry(GraphicsOverlay overlay, Geometry geometry, Map<String, Object> attr, GraphicsOverlayConfig config) {
        graphicsLayerMgr.drawGeometry(overlay, geometry, attr, config);
    }

    /**
     * 地图绘制元素, 清除原图层
     */
    public void drawGeometryWithClear(Geometry geometry, GraphicsOverlayConfig config) {
        graphicsLayerMgr.clearDrawLayer();
        graphicsLayerMgr.drawGeometry(geometry, config);
    }

    /**
     * 地图绘制元素, 清除原图层
     */
    public void drawGeometryWithClear(Geometry geometry, Map<String, Object> attr, GraphicsOverlayConfig config) {
        graphicsLayerMgr.clearDrawLayer();
        graphicsLayerMgr.drawGeometry(geometry, attr, config);
    }

    /**
     * 地图绘制元素, 清除原图层
     */
    public void drawGeometryWithClear(GraphicsOverlay overlay, Geometry geometry, GraphicsOverlayConfig config) {
        graphicsLayerMgr.clearDrawLayer();
        graphicsLayerMgr.drawGeometry(overlay, geometry, null, config);
    }

    /**
     * 地图绘制元素, 清除原图层
     */
    public void drawGeometryWithClear(GraphicsOverlay overlay, Geometry geometry, Map<String, Object> attr, GraphicsOverlayConfig config) {
        graphicsLayerMgr.clearDrawLayer();
        graphicsLayerMgr.drawGeometry(overlay, geometry, attr, config);
    }


    /**
     * 执行定位操作
     */
    public void location(LocationUtil.OnGetLocation onGetLocation) {
        PermissionHelper.requestLocationPermission(getContext(), new PermissionsResultAction() {
            @Override
            public void onGranted() {
                locationUtil = new LocationUtil(getContext());
                locationUtil.startLocation(new LocationUtil.OnGetLocation() {
                    @Override
                    public void getLocation(AMapLocation location) {
                        boolean isSuccess = graphicsLayerMgr.drawLocationSymbol(location.getLongitude(), location.getLatitude());
                        if (!isSuccess) {
                            ToastUtil.toastShort("无法获取当前位置");
                        }
                        if (onGetLocation != null) {
                            onGetLocation.getLocation(location);
                        }
                    }

                    @Override
                    public void locationFail() {
                        if (onGetLocation != null) {
                            onGetLocation.locationFail();
                        }
                        ToastUtil.toastShort("无法获取当前位置");
                    }
                });
            }

            @Override
            public void onDenied(String permission) {
                ToastUtil.toastShort("请授予本程序[定位]权限]");
            }
        });
    }
}
