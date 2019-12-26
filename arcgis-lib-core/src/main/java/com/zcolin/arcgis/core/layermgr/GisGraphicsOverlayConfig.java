/*
 * *********************************************************
 *   author   colin
 *   company  telchina
 *   email    wanglin2046@126.com
 *   date     19-12-26 下午8:54
 * ********************************************************
 */

/*
 * *********************************************************
 *   author   colin
 *   email    wanglin2046@126.com
 *   date     19-1-23 下午1:50
 * ********************************************************
 */

package com.zcolin.arcgis.core.layermgr;

import android.graphics.Color;

import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.zcolin.arcgis.core.R;

/**
 * ArcGis地图绘画缓冲区属性设置
 */
public class GisGraphicsOverlayConfig {

    /**
     * 点图标的设置,图片的优先级高于纯色点
     */
    private int pointPic       = R.drawable.zarcgis_icon_point_blue;//点图标
    private int pointPicWidth  = 60;//点图表的宽度
    private int pointPicHeight = 60;//点图表的高度

    /**
     * 纯色点的设置
     */
    private int                      pointColor;//点颜色
    private SimpleMarkerSymbol.Style pointType = SimpleMarkerSymbol.Style.CIRCLE;//点的形状， 默认是圆点
    private int                      pointSize = 25;

    private float pointOffSetX;    //点x偏移
    private float pointOffSetY;    //点y偏移


    /**
     * 线设置
     */
    private int                          lineColor       = Color.argb(90, 255, 0, 0);//线的颜色
    private int                          lineWidth       = 4;//线宽度
    private SimpleLineSymbol.Style       lineType        = SimpleLineSymbol.Style.SOLID;//线的类型，默认是实心线
    private SimpleLineSymbol.MarkerStyle lineMarkerStyle = SimpleLineSymbol.MarkerStyle.NONE; //无箭头

    /**
     * 面设置
     */
    private int                    polygonLineColor = Color.argb(90, 0, 191, 225);//面的边线的颜色
    private int                    polygonLineWidth = 2;//面的边线宽度
    private SimpleLineSymbol.Style polygonLineType  = SimpleLineSymbol.Style.SOLID;//面的边线的类型，默认是实心线
    private int                    polygonFillColor = Color.argb(90, 0, 191, 225);//面的填充的颜色
    private SimpleFillSymbol.Style polygonFillType  = SimpleFillSymbol.Style.SOLID;//填充面的类型，默认实心面

    public static GisGraphicsOverlayConfig instanceBuffer() {
        return new GisGraphicsOverlayConfig();
    }

    public static GisGraphicsOverlayConfig instanceHighlight() {
        GisGraphicsOverlayConfig overlayConfig = new GisGraphicsOverlayConfig();
        overlayConfig.pointColor = Color.RED;//点颜色
        overlayConfig.pointPic = R.drawable.zarcgis_icon_point_red;//点图标
        overlayConfig.lineColor = Color.argb(255, 65, 105, 225);//线的颜色
        overlayConfig.lineWidth = 6;//线宽度
        overlayConfig.polygonLineColor = Color.argb(255, 65, 105, 225);//面的边线的颜色
        overlayConfig.polygonFillColor = Color.argb(90, 0, 191, 225);//面的填充的颜色
        return overlayConfig;
    }

    public static GisGraphicsOverlayConfig instanceDraw() {
        GisGraphicsOverlayConfig overlayConfig = new GisGraphicsOverlayConfig();
        overlayConfig.pointColor = Color.argb(90, 255, 0, 0);//点颜色
        overlayConfig.pointPic = R.drawable.zarcgis_icon_point_blue;//点图标
        overlayConfig.lineColor = Color.argb(90, 255, 0, 0);//线的颜色
        overlayConfig.lineWidth = 4;//线宽度
        overlayConfig.polygonLineColor = Color.RED;//面的边线的颜色
        overlayConfig.polygonFillColor = Color.argb(90, 255, 0, 0);//面的填充的颜色
        return overlayConfig;
    }

    public GisGraphicsOverlayConfig setPointColor(int pointColor) {
        this.pointColor = pointColor;
        return this;
    }

    public GisGraphicsOverlayConfig setPointType(SimpleMarkerSymbol.Style pointType) {
        this.pointType = pointType;
        return this;
    }

    public GisGraphicsOverlayConfig setPointSize(int pointSize) {
        this.pointSize = pointSize;
        return this;
    }

    public GisGraphicsOverlayConfig setPointPic(int pointPic) {
        this.pointPic = pointPic;
        return this;
    }

    public GisGraphicsOverlayConfig setPointPicWidth(int pointPicWidth) {
        this.pointPicWidth = pointPicWidth;
        return this;
    }

    public GisGraphicsOverlayConfig setPointPicHeight(int pointPicHeight) {
        this.pointPicHeight = pointPicHeight;
        return this;
    }

    public GisGraphicsOverlayConfig setLineColor(int lineColor) {
        this.lineColor = lineColor;
        return this;
    }

    public GisGraphicsOverlayConfig setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
        return this;
    }

    public GisGraphicsOverlayConfig setLineType(SimpleLineSymbol.Style lineType) {
        this.lineType = lineType;
        return this;
    }

    public GisGraphicsOverlayConfig setPolygonLineColor(int polygonLineColor) {
        this.polygonLineColor = polygonLineColor;
        return this;
    }

    public GisGraphicsOverlayConfig setPolygonLineWidth(int polygonLineWidth) {
        this.polygonLineWidth = polygonLineWidth;
        return this;
    }

    public GisGraphicsOverlayConfig setPolygonLineType(SimpleLineSymbol.Style polygonLineType) {
        this.polygonLineType = polygonLineType;
        return this;
    }

    public GisGraphicsOverlayConfig setPolygonFillColor(int polygonFillColor) {
        this.polygonFillColor = polygonFillColor;
        return this;
    }

    public GisGraphicsOverlayConfig setPolygonFillType(SimpleFillSymbol.Style polygonFillType) {
        this.polygonFillType = polygonFillType;
        return this;
    }

    public int getPointPic() {
        return pointPic;
    }

    public int getPointPicWidth() {
        return pointPicWidth;
    }

    public int getPointPicHeight() {
        return pointPicHeight;
    }

    public int getPointColor() {
        return pointColor;
    }

    public SimpleMarkerSymbol.Style getPointType() {
        return pointType;
    }

    public int getPointSize() {
        return pointSize;
    }

    public int getLineColor() {
        return lineColor;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public SimpleLineSymbol.Style getLineType() {
        return lineType;
    }

    public int getPolygonLineColor() {
        return polygonLineColor;
    }

    public int getPolygonLineWidth() {
        return polygonLineWidth;
    }

    public SimpleLineSymbol.Style getPolygonLineType() {
        return polygonLineType;
    }

    public int getPolygonFillColor() {
        return polygonFillColor;
    }

    public SimpleFillSymbol.Style getPolygonFillType() {
        return polygonFillType;
    }

}
