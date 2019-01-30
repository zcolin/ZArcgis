/*
 * *********************************************************
 *   author   colin
 *   company  telchina
 *   email    wanglin2046@126.com
 *   date     19-1-23 上午10:24
 * ********************************************************
 */

package com.telchina.arcgis.core;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.amap.api.location.AMapLocation;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.loadable.LoadStatusChangedListener;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.telchina.arcgis.core.layermgr.GisGraphicsLayer;
import com.telchina.arcgis.core.layermgr.GisGraphicsOverlayConfig;
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
    private GisGraphicsLayer graphicsLayerMgr;

    private ZSubmitListener            onClearLister;         //点击清除数据按钮监听
    private LocationUtil.OnGetLocation onGetLocationListener;//定位回调
    private RelativeLayout             menuBarContainer;
    private GisMapConfig               gisMapConfig;

    public GisMapOperateView(Context context) {
        this(context, null);
    }

    public GisMapOperateView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.GisMapOperateView);
        int menuBarPaddingTop = (int) array.getDimension(R.styleable.GisMapOperateView_menuBarPaddingTop, 10);
        int menuBarPaddingBottom = (int) array.getDimension(R.styleable.GisMapOperateView_menuBarPaddingBottom, 10);
        int menuBarPaddingLeft = (int) array.getDimension(R.styleable.GisMapOperateView_menuBarPaddingLeft, 10);
        int menuBarPaddingRight = (int) array.getDimension(R.styleable.GisMapOperateView_menuBarPaddingRight, 10);
        int menuBarGravity = array.getInt(R.styleable.GisMapOperateView_menuBarGravity, Gravity.TOP | Gravity.RIGHT);//右上

        int menuBarOrientation = array.getInt(R.styleable.GisMapOperateView_menuBarOrientation, LinearLayout.VERTICAL);//纵向
        Drawable locationIcon = array.getDrawable(R.styleable.GisMapOperateView_locationIcon);
        Drawable mapTypeIcon = array.getDrawable(R.styleable.GisMapOperateView_mapTypeIcon);
        Drawable resetIcon = array.getDrawable(R.styleable.GisMapOperateView_resetIcon);
        Drawable clearIcon = array.getDrawable(R.styleable.GisMapOperateView_clearIcon);
        Drawable mapTypeItemBackground = array.getDrawable(R.styleable.GisMapOperateView_mapTypeItemBackground);
        ColorStateList mapTypeItemTextColor = array.getColorStateList(R.styleable.GisMapOperateView_mapTypeItemTextColor);
        int mapTypeAnim = array.getResourceId(R.styleable.GisMapOperateView_mapTypeDialogAnim, 0);
        array.recycle();

        LayoutInflater.from(context).inflate(R.layout.zarcgis_gisoperate, this);
        gisMapView = findViewById(R.id.gis_mapview);
        menuBarContainer = findViewById(R.id.menubar_container);
        LayoutParams params = ((LayoutParams) menuBarContainer.getLayoutParams());
        switch (menuBarGravity) {
            case Gravity.TOP:
            case Gravity.LEFT:
            case Gravity.LEFT | Gravity.TOP:
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                break;
            case Gravity.RIGHT:
            case Gravity.RIGHT | Gravity.TOP:
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                break;
            case Gravity.BOTTOM:
            case Gravity.LEFT | Gravity.BOTTOM:
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;
            case Gravity.RIGHT | Gravity.BOTTOM:
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;
            default:
                break;
        }

        GisMenuBar menubar = findViewById(R.id.gis_menubar);
        if (menubar != null) {
            menubar.init(this, menuBarOrientation)
                   .setClearIcon(clearIcon)
                   .setLocationIcon(locationIcon)
                   .setResetIcon(resetIcon)
                   .setMapTypeIcon(mapTypeIcon)
                   .setMapTypeAnim(mapTypeAnim)
                   .setMapTypeGravity(menuBarGravity)
                   .setMapTypeItemBackground(mapTypeItemBackground)
                   .setMenuBarPadding(menuBarPaddingLeft, menuBarPaddingTop, menuBarPaddingRight, menuBarPaddingBottom)
                   .setMapTypeItemTextColor(mapTypeItemTextColor);
        }

        graphicsLayerMgr = new GisGraphicsLayer(gisMapView);
    }


    /**
     * 初始化arcgis地图及地图相关的监听事件
     */
    public void initMapViews() {
        this.gisMapConfig = new GisMapConfig();
        this.initMapViews(gisMapConfig);
    }

    public void initMapViews(GisMapConfig gisMapConfig) {
        this.gisMapConfig = gisMapConfig;
        gisMapView.init(gisMapConfig);
    }

    public void pause() {
        gisMapView.pause();
    }

    public void resume() {
        gisMapView.resume();
    }

    public void reset() {
        zoomToCenterScale();
    }

    public void clear() {
        if (onClearLister != null) {
            if (onClearLister.submit()) {
                return;
            }
        }
        clearTempLayer();
    }

    public void switchBaseMap(int baseMapType) {
        if (baseMapType == GisMapView.TYPE_BASETILED_VEC) {
            if (gisMapConfig.getBaseMapType() != GisMapView.TYPE_BASETILED_VEC) {
                gisMapConfig.setBaseMapType(GisMapView.TYPE_BASETILED_VEC);
                gisMapView.init(gisMapConfig);
            }
        } else {
            if (gisMapConfig.getBaseMapType() != GisMapView.TYPE_BASETILED_IMG) {
                gisMapConfig.setBaseMapType(GisMapView.TYPE_BASETILED_IMG);
                gisMapView.init(gisMapConfig);
            }
        }
    }

    /**
     * 设置工具条,替换默认工具条，参考{@link GisMenuBar}
     */
    public void setMenuBar(View menuBar) {
        menuBarContainer.removeAllViews();
        menuBarContainer.addView(menuBar);
    }

    /**
     * 获取工具条
     */
    public View getMenuBar() {
        return menuBarContainer.getChildAt(0);
    }

    /**
     * 获取工具条容器
     */
    public RelativeLayout getMenuBarContainer() {
        return menuBarContainer;
    }

    /**
     * 图层管理类
     */
    public GisGraphicsLayer getGraphicsLayerMgr() {
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

    public void clearDrawLayer() {
        graphicsLayerMgr.clearDrawLayer();
    }

    public void clearHighLightLayer() {
        graphicsLayerMgr.clearHighlightLayer();
    }

    public void clearLocationLayer() {
        graphicsLayerMgr.clearLocationLayer();
    }

    public void clearBufferLayer() {
        graphicsLayerMgr.clearBufferLayer();
    }

    public void setOnClearLister(ZSubmitListener onClearLister) {
        this.onClearLister = onClearLister;
    }

    public void setOnGetLocationListener(LocationUtil.OnGetLocation listener) {
        this.onGetLocationListener = listener;
    }

    public void addStatusChangedListener(LoadStatusChangedListener onStatusChangedListener) {
        gisMapView.addLoadStatusChangedListener(onStatusChangedListener);
    }

    public void removeStatusChangedListener(LoadStatusChangedListener onStatusChangedListener) {
        gisMapView.removeLoadStatusChangedListener(onStatusChangedListener);
    }

    public void addDoneLoadingListener(Runnable runnable) {
        gisMapView.addDoneLoadingListener(runnable);
    }

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

    public void zoomToCenterScale() {
        gisMapView.zoomToCenterScale();
    }

    public void zoomToPoint(Point point) {
        gisMapView.zoomToPoint(point);
    }

    public void zoomToPoint(double x, double y) {
        gisMapView.zoomToPoint(x, y);
    }

    public void zoomToPoint(double x, double y, double scale) {
        gisMapView.zoomToPoint(x, y, scale);
    }

    public void zoomToGeometry(Geometry geometry) {
        gisMapView.zoomToGeometry(geometry);
    }


    /**
     * 设置缩放比和中心点
     */
    public void setResetScaleAndCenter(double resetScale, double[] resetCenter) {
        gisMapView.setInitScale(resetScale);
        gisMapView.setCenterPoint(resetCenter);
    }

    public void highLightGeometry(Geometry geometry) {
        highLightGeometry(geometry, 0);
    }

    public void highLightGeometry(Geometry geometry, @DrawableRes int pointPic) {
        graphicsLayerMgr.highLightGeometry(geometry, GisGraphicsOverlayConfig.instanceHighlight().setPointPic(pointPic));
    }

    public void highLightGeometry(Geometry geometry, GisGraphicsOverlayConfig config) {
        graphicsLayerMgr.highLightGeometry(geometry, config);
    }

    public void highLightGeometry(GraphicsOverlay graphicsLayer, Geometry geometry, GisGraphicsOverlayConfig config) {
        graphicsLayerMgr.highLightGeometry(graphicsLayer, geometry, config, false);
    }

    /**
     * 高亮指定图层某个区域 以及指定样式
     *
     * @param isMoveToDest 是否移动到高亮区域
     */
    public void highLightGeometry(GraphicsOverlay graphicsLayer, Geometry geometry, GisGraphicsOverlayConfig config, boolean isMoveToDest) {
        graphicsLayerMgr.highLightGeometry(graphicsLayer, geometry, config, isMoveToDest);
    }

    public boolean drawLocationSymbol(double longitude, double latitude) {
        return graphicsLayerMgr.drawLocationSymbol(longitude, latitude);
    }

    public boolean drawLocationSymbol(double longitude, double latitude, long scale) {
        return graphicsLayerMgr.drawLocationSymbol(longitude, latitude, scale);
    }

    public void drawText(Point point, String str) {
        graphicsLayerMgr.drawText(point, str);
    }

    public void drawText(Point point, String string, int color, int textsize) {
        graphicsLayerMgr.drawText(point, string, color, textsize);
    }

    public void drawText(GraphicsOverlay graphicsLayer, Point point, String string) {
        graphicsLayerMgr.drawText(graphicsLayer, point, string, Color.RED, 28);
    }

    public void drawText(GraphicsOverlay graphicsLayer, Point point, String string, int color, int textsize) {
        graphicsLayerMgr.drawText(graphicsLayer, point, string, color, textsize);
    }

    /**
     * 绘制图片
     */
    public void drawPictureMarker(Point point, Drawable drawable) {
        graphicsLayerMgr.drawPictureMarker(point, drawable);
    }

    /**
     * 绘制图片
     */
    public void drawPictureMarker(GraphicsOverlay graphicsOverlay, Point point, Drawable drawable) {
        graphicsLayerMgr.drawPictureMarker(graphicsOverlay, point, drawable);
    }

    /**
     * 绘制图片
     */
    public void drawPictureMarker(Point point, String url) {
        graphicsLayerMgr.drawPictureMarker(point, url);
    }

    /**
     * 绘制图片
     */
    public void drawPictureMarker(GraphicsOverlay graphicsOverlay, Point point, String url) {
        graphicsLayerMgr.drawPictureMarker(graphicsOverlay, point, url);
    }

    public void drawGeometry(Geometry geometry, GisGraphicsOverlayConfig config) {
        graphicsLayerMgr.drawGeometry(geometry, config);
    }

    public void drawGeometry(Geometry geometry, Map<String, Object> attr, GisGraphicsOverlayConfig config) {
        graphicsLayerMgr.drawGeometry(geometry, attr, config);
    }

    public void drawGeometry(GraphicsOverlay overlay, Geometry geometry, GisGraphicsOverlayConfig config) {
        graphicsLayerMgr.drawGeometry(overlay, geometry, null, config);
    }

    public void drawGeometry(GraphicsOverlay overlay, Geometry geometry, Map<String, Object> attr, GisGraphicsOverlayConfig config) {
        graphicsLayerMgr.drawGeometry(overlay, geometry, attr, config);
    }

    /**
     * 地图绘制元素, 清除原图层
     */
    public void drawGeometryWithClear(Geometry geometry, GisGraphicsOverlayConfig config) {
        graphicsLayerMgr.clearDrawLayer();
        graphicsLayerMgr.drawGeometry(geometry, config);
    }

    /**
     * 地图绘制元素, 清除原图层
     */
    public void drawGeometryWithClear(Geometry geometry, Map<String, Object> attr, GisGraphicsOverlayConfig config) {
        graphicsLayerMgr.clearDrawLayer();
        graphicsLayerMgr.drawGeometry(geometry, attr, config);
    }

    /**
     * 地图绘制元素, 清除原图层
     */
    public void drawGeometryWithClear(GraphicsOverlay overlay, Geometry geometry, GisGraphicsOverlayConfig config) {
        graphicsLayerMgr.clearDrawLayer();
        graphicsLayerMgr.drawGeometry(overlay, geometry, null, config);
    }

    /**
     * 地图绘制元素, 清除原图层
     */
    public void drawGeometryWithClear(GraphicsOverlay overlay, Geometry geometry, Map<String, Object> attr, GisGraphicsOverlayConfig config) {
        graphicsLayerMgr.clearDrawLayer();
        graphicsLayerMgr.drawGeometry(overlay, geometry, attr, config);
    }


    /**
     * 执行定位操作
     */
    public void location() {
        location(null);
    }

    public void location(LocationUtil.OnGetLocation locationListener) {
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
                        if (onGetLocationListener != null) {
                            onGetLocationListener.getLocation(location);
                        }
                        if (locationListener != null) {
                            locationListener.getLocation(location);
                        }
                    }

                    @Override
                    public void locationFail() {
                        if (onGetLocationListener != null) {
                            onGetLocationListener.locationFail();
                        }
                        if (locationListener != null) {
                            locationListener.locationFail();
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
