/*
 * *********************************************************
 *   author   zhuxuetong
 *   company  telchina
 *   email    zhuxuetong123@163.com
 *   date     18-6-19 下午2:02
 * ********************************************************
 */

/*
 * *********************************************************
 *   author   colin
 *   company  telchina
 *   email    wanglin2046@126.com
 *   date     18-5-10 下午3:27
 * ********************************************************
 */
package com.telchina.tharcgiscore;

import android.content.Context;
import android.graphics.Color;
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
import com.esri.arcgisruntime.mapping.view.ViewpointChangedListener;
import com.telchina.tharcgiscore.entity.GisMapDrawBean;
import com.telchina.tharcgiscore.entity.GisMapHighlightBean;
import com.telchina.tharcgiscore.layermgr.GraphicsLayerMgr;
import com.zcolin.frame.permission.PermissionHelper;
import com.zcolin.frame.permission.PermissionsResultAction;
import com.zcolin.frame.util.ToastUtil;
import com.zcolin.gui.ZDialog;
import com.zcolin.libamaplocation.LocationUtil;

import java.util.Map;


/**
 * 地图界面，操作按钮封装
 */
public class GisMapOperateView extends RelativeLayout {
    private GisMapView       gisMapView;
    private LocationUtil     locationUtil;
    private GraphicsLayerMgr graphicsLayerMgr;

    private ZDialog.ZDialogSubmitListener onRemoveLister;//点击清除数据按钮监听
    private LoadStatusChangedListener     onStatusChangedListener;//地图变化监听事件

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
            clearAllGraphicsLayerElement();
            if (onRemoveLister != null) {
                onRemoveLister.submit();
            }
        });
        findViewById(R.id.btn_reset).setOnClickListener(v -> zoomToCenterScale());
        findViewById(R.id.imb_map_zoomin).setOnClickListener(v -> gisMapView.zoomIn());
        findViewById(R.id.imb_map_zoomout).setOnClickListener(v -> gisMapView.zoomOut());
        findViewById(R.id.btn_get_lcoation).setOnClickListener(v -> loacation(null));
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
        gisMapView.setOnStatusChangedListener((status) -> {
            if (onStatusChangedListener != null) {
                onStatusChangedListener.loadStatusChanged(status);
            }
        });
        
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
     * 清除地图所有要素
     */
    public void clearAllGraphicsLayerElement() {
        graphicsLayerMgr.clearAll();
        gisMapView.getCallout().dismiss();
    }

    /**
     * 清除地图所有要素
     */
    public void clearGraphicsLayerElement() {
        graphicsLayerMgr.clearBufferLayer();
        graphicsLayerMgr.clearHighlightLayer();
        graphicsLayerMgr.clearLocationLayer();
        gisMapView.getCallout().dismiss();
    }

    /**
     * 清除按钮回调
     */
    public void setOnRemoveLister(ZDialog.ZDialogSubmitListener onRemoveLister) {
        this.onRemoveLister = onRemoveLister;
    }

    /**
     * 设置加载状态监听
     */
    public void setOnStatusChangedListener(LoadStatusChangedListener onStatusChangedListener) {
        this.onStatusChangedListener = onStatusChangedListener;
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
     * 设置缩放监听
     */
    public void setViewpointChangedListener(ViewpointChangedListener viewpointChangedListener) {
        gisMapView.setViewpointChangedListener(viewpointChangedListener);
    }

    public boolean addLocationLayer(double longitude, double latitude) {
        return graphicsLayerMgr.addLocationSymbol(longitude, latitude);
    }

    public boolean addLocationLayer(double longitude, double latitude, long scale) {
        return graphicsLayerMgr.addLocationSymbol(longitude, latitude, scale);
    }

    public boolean addTextSymbol(Point point, String string) {
        return graphicsLayerMgr.addTextSymbol(point, string, Color.RED, 35);
    }

    public boolean addTextSymbol(Point point, String string, int color, int textsize) {
        return graphicsLayerMgr.addTextSymbol(point, string, color, textsize);
    }

    public boolean addTextSymbol(GraphicsOverlay graphicsLayer, Point point, String string) {
        return graphicsLayerMgr.addTextSymbol(point, string, Color.RED, 35);
    }

    public boolean addTextSymbol(GraphicsOverlay graphicsLayer, Point point, String string, int color, int textsize) {
        return graphicsLayerMgr.addTextSymbol(point, string, color, textsize);
    }

    /**
     * 移除定位图标
     */
    public void clearLocationLayer() {
        graphicsLayerMgr.clearLocationLayer();
    }

    /**
     * 重新设置中心点和缩放比
     */
    public void setResetScaleAndCenter(double resetScale, double[] resetCenter) {
        gisMapView.setInitScale(resetScale);
        gisMapView.setCenterPoint(resetCenter);
    }

    /**
     * 高亮某个区域
     */
    public void highLight(Geometry geometry) {
        highLight(geometry, -1);
    }

    /**
     * 高亮某个区域
     */
    public void highLight(Geometry geometry, int pointPic) {
        GisMapHighlightBean bean = new GisMapHighlightBean();
        bean.pointPic = pointPic;
        graphicsLayerMgr.highLight(geometry, bean);
    }

    /**
     * 高亮指定图层某个区域 以及指定颜色值
     *
     * @param graphicsLayer 制定图层图层（绘画图层、高亮图层）
     * @param geometry      区域地理信息
     */
    public void highLightWithColor(GraphicsOverlay graphicsLayer, Geometry geometry, GisMapHighlightBean hilightBean) {
        graphicsLayerMgr.highLight(geometry, hilightBean);
    }

    /**
     * 清除高亮
     */
    public void clearHighLightLayer() {
        graphicsLayerMgr.clearHighlightLayer();
    }

    /**
     * 地图绘制元素
     *
     * @param isClear 是否清除原来的元素
     */
    public void drawToMap(Geometry geometry, boolean isClear, Map<String, Object> attr, GisMapDrawBean gisMapDrawBean) {
        if (isClear) {
            graphicsLayerMgr.clearTempLayer();
        }
        graphicsLayerMgr.addSymbol(geometry, attr, gisMapDrawBean);
    }


    /**
     * 设置地图点击事件
     */
    public void setOnSingleTapListener(GisMapView.SingleTapListener listener) {
        gisMapView.setOnSingleTapListener(listener);
    }

    public void setOnDoubleTapListener(GisMapView.DoubleTapListener doubleTapListener) {
        gisMapView.setOnDoubleTapListener(doubleTapListener);
    }

    public void setOnLongPressListener(GisMapView.LongPressListener longPressListener) {
        gisMapView.setOnLongPressListener(longPressListener);
    }

    /**
     * 获取元素的中心坐标点
     */
    public Point getRectCenterPoint(Feature feature) {
        return gisMapView.getRectCenterPoint(feature);
    }

    /**
     * 获取元素的中心坐标点
     */
    public Point getRectCenterPoint(Geometry geometry) {
        return gisMapView.getRectCenterPoint(geometry);
    }

    public Callout getCallout() {
        return gisMapView.getCallout();
    }

    public GisMapView getGisMapView() {
        return gisMapView;
    }

    public void pause() {
        gisMapView.pause();
    }

    public void resume() {
        gisMapView.resume();
    }

    public void loacation(LocationUtil.OnGetLocation onGetLocation) {
        PermissionHelper.requestLocationPermission(getContext(), new PermissionsResultAction() {
            @Override
            public void onGranted() {
                locationUtil = new LocationUtil(getContext());
                locationUtil.startLocation(new LocationUtil.OnGetLocation() {
                    @Override
                    public void getLocation(AMapLocation location) {
                        boolean isSuccess = graphicsLayerMgr.addLocationSymbol(location.getLongitude(), location.getLatitude());
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
