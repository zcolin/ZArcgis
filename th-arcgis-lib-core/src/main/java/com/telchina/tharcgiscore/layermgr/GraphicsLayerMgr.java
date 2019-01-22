/*
 * *********************************************************
 *   author   zxt
 *   company  telchina
 *   email    zhuxuetong123@163.com
 *   date     18-12-20 下午3:27
 * ********************************************************
 */

/*
 * *********************************************************
 *   author   zhuxuetong
 *   company  telchina
 *   email    zhuxuetong123@163.com
 *   date     18-10-8 下午4:49
 * ********************************************************
 */
package com.telchina.tharcgiscore.layermgr;

import android.graphics.Bitmap;
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
import com.telchina.tharcgiscore.entity.GisMapBufferBean;
import com.telchina.tharcgiscore.entity.GisMapDrawBean;
import com.telchina.tharcgiscore.entity.GisMapHighlightBean;
import com.zcolin.frame.util.BitmapUtil;

import java.util.Map;

/**
 * 自定义图层操作类
 * 主要有缓冲区图层、绘画图层、高亮图层、定位图层
 */
public class GraphicsLayerMgr extends AbstractGraphicsOverlayMgr {
    /*默认提供以下三个图层，如果有更多需要，自己使用addLayer添加即可*/
    private GraphicsOverlay     bufferLayer;      //缓冲区图层
    private GraphicsOverlay     tempLayer;        //临时图层
    private GraphicsOverlay     highLightLayer;   //高亮图层
    private GraphicsOverlay     locationLayer;    //定位图层
    private PictureMarkerSymbol locationSymbol;
    private boolean isFirstLocation = true;//是否第一次定位，第一次需要延时设置中心点

    public GraphicsLayerMgr(GisMapView mapView) {
        super(mapView);
        resetAllLayers();
    }

    public GraphicsOverlay getBufferLayer() {
        return bufferLayer;
    }

    public GraphicsOverlay getGTempLayer() {
        return tempLayer;
    }

    public GraphicsOverlay getHighLightLayer() {
        return highLightLayer;
    }

    public void resetAllLayers() {
        removeAllGraphicsOverlay();
        if (bufferLayer == null) {
            bufferLayer = new GraphicsOverlay(GraphicsOverlay.RenderingMode.STATIC);
        }
        addGraphicsLayer("bufferLayer", bufferLayer);
        if (tempLayer == null) {
            tempLayer = new GraphicsOverlay(GraphicsOverlay.RenderingMode.STATIC);
        }
        addGraphicsLayer("tempLayer", tempLayer);
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
     * 清理缓冲区
     */
    public void clearBufferLayer() {
        bufferLayer.getGraphics().clear();
    }

    /**
     * 清空tempLayer图层的元素
     */
    public void clearTempLayer() {
        tempLayer.getGraphics().clear();
    }

    /**
     * 清空highLightLayer
     */
    public void clearHighlightLayer() {
        highLightLayer.getGraphics().clear();
    }


    /**
     * 绘画或渲染缓冲区
     *
     * @param geometry   点线面信息
     * @param bufferBean 高亮点或线的颜色 默认传-1
     * @param distance   缓冲距离
     */
    public Geometry bufferLayer(Geometry geometry, GisMapBufferBean bufferBean, double distance) {
        Geometry projectedGeometry = GeometryEngine.project(geometry, getArcMap().getSpatialReference());
        Polygon polygon = GeometryEngine.buffer(projectedGeometry, distance);
        Geometry bufferGeometry = GeometryEngine.project(polygon, getArcMap().getSpatialReference());

        bufferLayer(bufferGeometry, bufferBean);
        return bufferGeometry;
    }

    /**
     * 绘画或渲染缓冲区
     *
     * @param geometry   缓冲区区域信息
     * @param bufferBean 高亮点或线的颜色
     */
    public void bufferLayer(Geometry geometry, GisMapBufferBean bufferBean) {
        Graphic graphic = null;
        if (geometry.getGeometryType() == GeometryType.POINT || geometry.getGeometryType() == GeometryType.MULTIPOINT) {
            Point point = (Point) geometry;
            if (bufferBean.pointPic == -1 && bufferBean.pointColor != -1) {
                SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(bufferBean.pointType, bufferBean.pointColor == -1 ? GisMapHighlightBean.POINT_COLOR : bufferBean.pointColor, bufferBean
                        .pointSize);
                graphic = new Graphic(point, pointSymbol);
            } else {
                Drawable drawable = mapView.getResources().getDrawable(bufferBean.pointPic == -1 ? GisMapHighlightBean.POINT_PIC : bufferBean.pointPic);
                drawable = BitmapUtil.zoomDrawable(drawable, bufferBean.pointPicWidth, bufferBean.pointPicHeight);
                PictureMarkerSymbol pictureMarker = new PictureMarkerSymbol(new BitmapDrawable(BitmapUtil.drawableToBitmap(drawable)));
                graphic = new Graphic(point, pictureMarker);
            }
        } else if (geometry.getGeometryType() == GeometryType.POLYLINE) {
            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(bufferBean.lineType, bufferBean.lineColor, bufferBean.lineWidth);
            Polyline polyline = (Polyline) geometry;
            graphic = new Graphic(polyline, lineSymbol);
        } else if (geometry.getGeometryType() == GeometryType.POLYGON) {
            Polygon polygon = (Polygon) geometry;
            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(bufferBean.polygonLineType, bufferBean.polygonLineColor, bufferBean.polygonLineWidth);
            SimpleFillSymbol fillSymbol = new SimpleFillSymbol(bufferBean.polygonFillType, bufferBean.polygonFillColor, lineSymbol);
            graphic = new Graphic(polygon, fillSymbol);
        }
        if (graphic != null) {
            bufferLayer.getGraphics().add(graphic);
        }
    }

    /**
     * 绘画或渲染缓冲区 不需要清缓冲图层
     *
     * @param geometry   缓冲区区域信息
     * @param bufferBean 高亮点或线的颜色
     */
    public void bufferLayerWithClear(Geometry geometry, GisMapBufferBean bufferBean) {
        clearBufferLayer();
        bufferLayer(geometry, bufferBean);
    }


    /**
     * 高亮某个区域
     *
     * @param geometry    区域地理信息
     * @param hilightBean 设置的高亮属性
     */
    public void highLight(Geometry geometry, GisMapHighlightBean hilightBean) {
        Graphic graphic = null;
        if (geometry.getGeometryType() == GeometryType.POINT || geometry.getGeometryType() == GeometryType.MULTIPOINT) {
            Point point = (Point) geometry;
            if (hilightBean.pointPic == -1) {
                SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(hilightBean.pointType, hilightBean.pointColor == -1 ? GisMapHighlightBean.POINT_COLOR : hilightBean.pointColor, hilightBean
                        .pointSize);
                graphic = new Graphic(point, pointSymbol);
            } else {
                Drawable drawable = mapView.getResources().getDrawable(hilightBean.pointPic == -1 ? GisMapHighlightBean.POINT_PIC : hilightBean.pointPic);
                drawable = BitmapUtil.zoomDrawable(drawable, hilightBean.pointPicWidth, hilightBean.pointPicHeight);
                PictureMarkerSymbol pictureMarker = new PictureMarkerSymbol(new BitmapDrawable(BitmapUtil.drawableToBitmap(drawable)));
                graphic = new Graphic(point, pictureMarker);
            }
        } else if (geometry.getGeometryType() == GeometryType.POLYLINE) {
            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(hilightBean.lineType, hilightBean.lineColor, hilightBean.lineWidth);
            Polyline polyline = (Polyline) geometry;
            graphic = new Graphic(polyline, lineSymbol);
        } else if (geometry.getGeometryType() == GeometryType.POLYGON) {
            Polygon polygon = (Polygon) geometry;
            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(hilightBean.polygonLineType, hilightBean.polygonLineColor, hilightBean.polygonLineWidth);
            SimpleFillSymbol fillSymbol = new SimpleFillSymbol(hilightBean.polygonFillType, hilightBean.polygonFillColor, lineSymbol);
            graphic = new Graphic(polygon, fillSymbol);
        }
        if (graphic != null) {
            highLightLayer.getGraphics().add(graphic);
        }
    }


    /**
     * 高亮某个区域
     *
     * @param geometry      区域信息
     * @param highlightBean 点、线、面的高亮属性
     * @param isMoveToGeo   是否移动到geometry
     */
    public void highLight(Geometry geometry, GisMapHighlightBean highlightBean, boolean isMoveToGeo) {
        if (isMoveToGeo) {
            mapView.zoomToGeometry(geometry);
        }
        highLight(geometry, highlightBean);
    }

    /**
     * 增加定位成功位置图层
     */
    public boolean addLocationSymbol(double longitude, double latitude) {
        return addLocationSymbol(longitude, latitude, mapView.getMapScale());
    }

    /**
     * 增加定位成功位置图层,未进行坐标系转换，自行转换
     *
     * @param longitude 坐标系 经度
     * @param latitude  坐标系 纬度
     * @param scale     缩放级别
     */
    public boolean addLocationSymbol(double longitude, double latitude, double scale) {
        return addLocationSymbol(longitude, latitude, scale, null);
    }

    /**
     * 增加定位成功位置图层,未进行坐标系转换，自行转换
     *
     * @param longitude    坐标系 经度
     * @param latitude     坐标系 纬度
     * @param scale        缩放级别
     * @param locationIcon 定位图标
     */
    public boolean addLocationSymbol(double longitude, double latitude, double scale, Drawable locationIcon) {
        locationLayer.getGraphics().clear();

        if (locationSymbol == null) {
            locationIcon = locationIcon == null ? mapView.getResources().getDrawable(R.drawable.ic_gismap_location) : locationIcon;
            Bitmap map = BitmapUtil.drawableToBitmap(BitmapUtil.zoomDrawable(locationIcon, 100, 100));
            locationSymbol = new PictureMarkerSymbol(new BitmapDrawable(map));
        }

        if (0.0 != longitude && 0.0 != latitude) {
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
     * 移除定位图标
     */
    public void clearLocationLayer() {
        locationLayer.getGraphics().clear();
    }

    /**
     * 绘画几何要素
     *
     * @param geometry 区域地理信息
     * @param drawBean 设置的绘画属性
     */
    public void addSymbol(Geometry geometry, Map<String, Object> attr, GisMapDrawBean drawBean) {
        Graphic graphic = null;
        if (geometry.getGeometryType() == GeometryType.POINT || geometry.getGeometryType() == GeometryType.MULTIPOINT) {
            Point point = (Point) geometry;
            if (drawBean.pointPic == -1 && drawBean.pointColor != -1) {
                SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(drawBean.pointType, drawBean.pointColor == -1 ? GisMapDrawBean.POINT_COLOR : drawBean.pointColor, drawBean.pointSize);
                if (attr == null) {
                    graphic = new Graphic(point, pointSymbol);
                } else {
                    graphic = new Graphic(point, attr, pointSymbol);
                }
            } else {
                Drawable drawable = mapView.getResources().getDrawable(drawBean.pointPic == -1 ? GisMapHighlightBean.POINT_PIC : drawBean.pointPic);
                drawable = BitmapUtil.zoomDrawable(drawable, drawBean.pointPicWidth, drawBean.pointPicHeight);
                PictureMarkerSymbol pictureMarker = new PictureMarkerSymbol(new BitmapDrawable(BitmapUtil.drawableToBitmap(drawable)));
                if (attr == null) {
                    graphic = new Graphic(point, pictureMarker);
                } else {
                    graphic = new Graphic(point, attr, pictureMarker);
                }
            }
        } else if (geometry.getGeometryType() == GeometryType.POLYLINE) {
            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(drawBean.lineType, drawBean.lineColor, drawBean.lineWidth);
            Polyline polyline = (Polyline) geometry;
            if (attr == null) {
                graphic = new Graphic(polyline, lineSymbol);
            } else {
                graphic = new Graphic(polyline, attr, lineSymbol);
            }
        } else if (geometry.getGeometryType() == GeometryType.POLYGON) {
            Polygon polygon = (Polygon) geometry;
            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(drawBean.polygonLineType, drawBean.polygonLineColor, drawBean.polygonLineWidth);
            SimpleFillSymbol fillSymbol = new SimpleFillSymbol(drawBean.polygonFillType, drawBean.polygonFillColor, lineSymbol);
            if (attr == null) {
                graphic = new Graphic(polygon, fillSymbol);
            } else {
                graphic = new Graphic(polygon, attr, fillSymbol);
            }
        }
        if (graphic != null) {
            tempLayer.getGraphics().add(graphic);
        }
    }

    /**
     * 写文字
     */
    public boolean addTextSymbol(Point point, String string, int color, int textSize) {
        TextSymbol ts = new TextSymbol(textSize, string, color, TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
        Graphic gText = new Graphic(point, ts);
        return tempLayer.getGraphics().add(gText);
    }

    /**
     * 增加图片
     */
    public boolean addPictureMarkerSymbol(Point point, Drawable drawable) {
        PictureMarkerSymbol markerSymbol = new PictureMarkerSymbol(new BitmapDrawable(BitmapUtil.drawableToBitmap(drawable)));
        Graphic graphic = new Graphic(point, markerSymbol);
        return tempLayer.getGraphics().add(graphic);
    }

    /**
     * 增加图片
     */
    public boolean addPictureMarkerSymbol(Point point, String url) {
        PictureMarkerSymbol markerSymbol = new PictureMarkerSymbol(url);
        Graphic graphic = new Graphic(point, markerSymbol);
        return tempLayer.getGraphics().add(graphic);
    }
}
