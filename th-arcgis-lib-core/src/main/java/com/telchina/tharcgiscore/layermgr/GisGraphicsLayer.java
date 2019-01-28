/*
 * *********************************************************
 *   author   colin
 *   company  telchina
 *   email    wanglin2046@126.com
 *   date     19-1-23 下午2:47
 * ********************************************************
 */

package com.telchina.tharcgiscore.layermgr;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.telchina.tharcgiscore.GisMapView;
import com.telchina.tharcgiscore.R;
import com.zcolin.frame.util.BitmapUtil;

import java.util.Map;

/**
 * 自定义图层操作类
 * 主要有缓冲区图层、高亮图层、定位图层
 * 默认提供四个个图层，如果有更多需要，自己使用addLayer添加即可
 */
public class GisGraphicsLayer extends GisAbstractGraphicsOverlay {
    private GraphicsOverlay     locationLayer;    //定位图层
    private GraphicsOverlay     highLightLayer;   //高亮图层
    private GraphicsOverlay     drawLayer;        //绘画图层
    private GraphicsOverlay     bufferLayer;      //缓冲区图层
    private PictureMarkerSymbol locationSymbol;
    private boolean isFirstLocation = true;//是否第一次定位，第一次需要延时设置中心点

    public GisGraphicsLayer(GisMapView mapView) {
        super(mapView);
        resetAllLayers();
    }

    public GraphicsOverlay getBufferLayer() {
        return bufferLayer;
    }

    public GraphicsOverlay getHighLightLayer() {
        return highLightLayer;
    }

    public GraphicsOverlay getLocationLayer() {
        return locationLayer;
    }

    public GraphicsOverlay geDrawLayer() {
        return drawLayer;
    }

    /**
     * 重置新添加图层
     */
    public void resetAllLayers() {
        removeAllGraphicsOverlay();
        if (bufferLayer == null) {
            bufferLayer = new GraphicsOverlay(GraphicsOverlay.RenderingMode.STATIC);
        }
        addGraphicsLayer("bufferLayer", bufferLayer);
        if (drawLayer == null) {
            drawLayer = new GraphicsOverlay(GraphicsOverlay.RenderingMode.STATIC);
        }
        addGraphicsLayer("drawLayer", drawLayer);
        if (highLightLayer == null) {
            highLightLayer = new GraphicsOverlay(GraphicsOverlay.RenderingMode.STATIC);
        }
        addGraphicsLayer("highLightLayer", highLightLayer);
        if (locationLayer == null) {
            locationLayer = new GraphicsOverlay(GraphicsOverlay.RenderingMode.DYNAMIC);
        }
        addGraphicsLayer("locationLayer", locationLayer);
    }


    /**
     * 清理缓冲区图层
     */
    public void clearBufferLayer() {
        if (bufferLayer != null) {
            bufferLayer.getGraphics().clear();
        }
    }

    /**
     * 清除定位图层
     */
    public void clearDrawLayer() {
        if (drawLayer != null) {
            drawLayer.getGraphics().clear();
        }
    }

    /**
     * 清空高亮图层
     */
    public void clearHighlightLayer() {
        if (highLightLayer != null) {
            highLightLayer.getGraphics().clear();
        }
    }

    /**
     * 清除定位图层
     */
    public void clearLocationLayer() {
        if (locationLayer != null) {
            locationLayer.getGraphics().clear();
        }
    }

    /**
     * 清空所有绘画图层图层
     */
    public void clearAll() {
        for (Map.Entry<String, GraphicsOverlay> stringGraphicsOverlayEntry : mapOverlay.entrySet()) {
            stringGraphicsOverlayEntry.getValue().getGraphics().clear();
        }
    }

    /**
     * 清空某个图层的元素
     */
    public void clear(GraphicsOverlay layer) {
        layer.getGraphics().clear();
    }

    /**
     * 根据key清空某个图层的元素
     */
    public void clear(String layerKey) {
        GraphicsOverlay overlay = getGraphicsLayer(layerKey);
        if (overlay != null) {
            clear(overlay);
        }
    }

    /**
     * 高亮某个区域
     *
     * @param geometry 区域地理信息
     * @param config   设置的高亮属性
     */
    public void highLightGeometry(Geometry geometry, GisGraphicsOverlayConfig config) {
        highLightGeometry(highLightLayer, geometry, config, false);
    }

    /**
     * 高亮某个区域
     *
     * @param geometry 区域地理信息
     * @param config   设置的高亮属性
     */
    public void highLightGeometry(GraphicsOverlay overlay, Geometry geometry, GisGraphicsOverlayConfig config, boolean isMoveToDest) {
        if (isMoveToDest) {
            mapView.zoomToGeometry(geometry);
        }

        config = config == null ? GisGraphicsOverlayConfig.instanceHighlight() : config;
        drawGeometry(overlay, geometry, null, config);
    }

    /**
     * 增加定位成功位置图层, 不进行缩放
     */
    public boolean drawLocationSymbol(double longitude, double latitude) {
        return drawLocationSymbol(longitude, latitude, mapView.getMapScale());
    }

    /**
     * 增加定位成功位置图层,未进行坐标系转换，自行转换
     *
     * @param longitude 坐标系 经度
     * @param latitude  坐标系 纬度
     * @param scale     缩放级别
     */
    public boolean drawLocationSymbol(double longitude, double latitude, double scale) {
        return drawLocationSymbol(longitude, latitude, scale, null);
    }

    /**
     * 增加定位成功位置图层,未进行坐标系转换，自行转换
     *
     * @param longitude    坐标系 经度
     * @param latitude     坐标系 纬度
     * @param scale        缩放级别
     * @param locationIcon 定位图标
     */
    public boolean drawLocationSymbol(double longitude, double latitude, double scale, Drawable locationIcon) {
        locationLayer.getGraphics().clear();
        return drawLocationSymbolWithoutClear(longitude, latitude, scale, locationIcon);
    }

    /**
     * 增加定位成功位置图层,未进行坐标系转换，自行转换
     *
     * @param longitude    坐标系 经度
     * @param latitude     坐标系 纬度
     * @param scale        缩放级别
     * @param locationIcon 定位图标
     */
    public boolean drawLocationSymbolWithoutClear(double longitude, double latitude, double scale, Drawable locationIcon) {
        if (0 != longitude && 0 != latitude) {
            if (locationSymbol == null) {
                locationIcon = locationIcon == null ? mapView.getResources().getDrawable(R.drawable.zarcgis_icon_point) : locationIcon;
                Bitmap map = BitmapUtil.drawableToBitmap(BitmapUtil.zoomDrawable(locationIcon, 100, 100));
                locationSymbol = new PictureMarkerSymbol(new BitmapDrawable(map));
            }

            Point mapPoint = new Point(longitude, latitude, SpatialReference.create(mapView.getWkid()));
            Graphic graphicPoint = new Graphic(mapPoint, locationSymbol);
            locationLayer.getGraphics().add(graphicPoint);
            if (isFirstLocation) {
                isFirstLocation = false;
                mapView.postDelayed(() -> mapView.zoomToPoint(mapPoint, scale), 500);
            } else {
                mapView.zoomToPoint(mapPoint, scale);
            }
            return true;
        }
        return false;
    }


    /**
     * 绘画或渲染缓冲区 清处原缓冲图层
     *
     * @param geometry 缓冲区区域信息
     * @param config   样式配置
     */
    public void bufferGeometryWithClear(Geometry geometry, GisGraphicsOverlayConfig config) {
        clearBufferLayer();
        bufferGeometry(geometry, config);
    }

    /**
     * 绘画或渲染缓冲区 清处原缓冲图层
     *
     * @param geometry 缓冲区区域信息
     * @param config   样式配置
     * @param distance 缓冲范围
     */
    public void bufferGeometryWithClear(Geometry geometry, GisGraphicsOverlayConfig config, double distance) {
        clearBufferLayer();
        bufferGeometry(geometry, config, distance);
    }


    /**
     * 绘画或渲染缓冲区
     *
     * @param geometry 点线面信息
     * @param config   样式配置
     * @param distance 缓冲距离
     */
    public Geometry bufferGeometry(Geometry geometry, GisGraphicsOverlayConfig config, double distance) {
        if (distance > 0) {
            Geometry projectedGeometry = GeometryEngine.project(geometry, getArcMap().getSpatialReference());
            Polygon polygon = GeometryEngine.buffer(projectedGeometry, distance);
            geometry = GeometryEngine.project(polygon, getArcMap().getSpatialReference());
        }

        bufferGeometry(geometry, config);
        return geometry;
    }

    /**
     * 绘画或渲染缓冲区
     *
     * @param geometry 缓冲区区域信息
     * @param config   样式配置
     */
    public void bufferGeometry(Geometry geometry, GisGraphicsOverlayConfig config) {
        bufferGeometry(bufferLayer, geometry, config);
    }

    /**
     * 绘画或渲染缓冲区
     *
     * @param geometry 缓冲区区域信息
     * @param config   样式配置
     */
    public void bufferGeometry(GraphicsOverlay overlay, Geometry geometry, GisGraphicsOverlayConfig config) {
        config = config == null ? GisGraphicsOverlayConfig.instanceBuffer() : config;
        drawGeometry(overlay, geometry, null, config);
    }

    /**
     * 绘制几何要素
     *
     * @param geometry 区域地理信息
     * @param config   绘画样式配置
     */
    public void drawGeometry(Geometry geometry, GisGraphicsOverlayConfig config) {
        drawGeometry(geometry, null, config);
    }

    /**
     * 绘制几何要素
     *
     * @param geometry 区域地理信息
     * @param attr     属性信息
     * @param config   绘画样式配置
     */
    public void drawGeometry(Geometry geometry, Map<String, Object> attr, GisGraphicsOverlayConfig config) {
        drawGeometry(drawLayer, geometry, null, config);
    }

    /**
     * 绘制几何要素
     *
     * @param geometry 区域地理信息
     * @param attr     属性信息
     * @param config   绘画样式配置
     */
    public void drawGeometry(GraphicsOverlay overlay, Geometry geometry, Map<String, Object> attr, GisGraphicsOverlayConfig config) {
        config = config == null ? GisGraphicsOverlayConfig.instanceDraw() : config;
        Graphic graphic = null;
        if (geometry.getGeometryType() == GeometryType.POINT || geometry.getGeometryType() == GeometryType.MULTIPOINT) {
            Point point = (Point) geometry;
            if (config.getPointPic() != 0 && config.getPointPic() != -1) {
                Drawable drawable = mapView.getResources().getDrawable(config.getPointPic());
                drawable = BitmapUtil.zoomDrawable(drawable, config.getPointPicWidth(), config.getPointPicHeight());
                PictureMarkerSymbol pictureMarker = new PictureMarkerSymbol(new BitmapDrawable(BitmapUtil.drawableToBitmap(drawable)));
                if (attr == null) {
                    graphic = new Graphic(point, pictureMarker);
                } else {
                    graphic = new Graphic(point, attr, pictureMarker);
                }
            } else {
                SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(config.getPointType(), config.getPointColor(), config.getPointSize());
                if (attr == null) {
                    graphic = new Graphic(point, pointSymbol);
                } else {
                    graphic = new Graphic(point, attr, pointSymbol);
                }
            }
        } else if (geometry.getGeometryType() == GeometryType.POLYLINE) {
            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(config.getLineType(), config.getLineColor(), config.getLineWidth());
            Polyline polyline = (Polyline) geometry;
            if (attr == null) {
                graphic = new Graphic(polyline, lineSymbol);
            } else {
                graphic = new Graphic(polyline, attr, lineSymbol);
            }
        } else if (geometry.getGeometryType() == GeometryType.POLYGON) {
            Polygon polygon = (Polygon) geometry;
            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(config.getPolygonLineType(), config.getPolygonLineColor(), config.getPolygonLineWidth());
            SimpleFillSymbol fillSymbol = new SimpleFillSymbol(config.getPolygonFillType(), config.getPolygonFillColor(), lineSymbol);
            if (attr == null) {
                graphic = new Graphic(polygon, fillSymbol);
            } else {
                graphic = new Graphic(polygon, attr, fillSymbol);
            }
        }

        if (graphic != null) {
            overlay.getGraphics().add(graphic);
        }
    }

    /**
     * 绘制文字
     */
    public void drawText(Point point, String str) {
        drawText(point, str, Color.RED, 18);
    }

    /**
     * 绘制文字
     */
    public void drawText(Point point, String str, int color, int textSize) {
        drawText(drawLayer, point, str, color, textSize);
    }

    /**
     * 绘制文字
     */
    public void drawText(GraphicsOverlay graphicsOverlay, Point point, String str, int color, int textSize) {
        TextSymbol ts = new TextSymbol(textSize, str, color, TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
        Graphic gText = new Graphic(point, ts);
        graphicsOverlay.getGraphics().add(gText);
    }

    /**
     * 绘制图片
     */
    public void drawPictureMarker(Point point, Drawable drawable) {
        drawPictureMarker(drawLayer, point, drawable);
    }

    /**
     * 绘制图片
     */
    public void drawPictureMarker(GraphicsOverlay graphicsOverlay, Point point, Drawable drawable) {
        PictureMarkerSymbol markerSymbol = new PictureMarkerSymbol(new BitmapDrawable(BitmapUtil.drawableToBitmap(drawable)));
        markerSymbol.addDoneLoadingListener(() -> {
            Graphic graphic = new Graphic(point, markerSymbol);
            graphicsOverlay.getGraphics().add(graphic);
        });
        markerSymbol.loadAsync();
    }

    /**
     * 绘制图片
     */
    public void drawPictureMarker(Point point, String url) {
        drawPictureMarker(drawLayer, point, url);
    }

    /**
     * 绘制图片
     */
    public void drawPictureMarker(GraphicsOverlay graphicsOverlay, Point point, String url) {
        PictureMarkerSymbol markerSymbol = new PictureMarkerSymbol(url);
        markerSymbol.addDoneLoadingListener(() -> {
            Graphic graphic = new Graphic(point, markerSymbol);
            graphicsOverlay.getGraphics().add(graphic);
        });
        markerSymbol.loadAsync();
    }
}
