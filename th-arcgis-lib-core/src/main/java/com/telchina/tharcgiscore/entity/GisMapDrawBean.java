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
 *   date     18-10-31 下午7:49
 * ********************************************************
 */
package com.telchina.tharcgiscore.entity;

import android.graphics.Color;

import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.telchina.tharcgiscore.R;

/**
 * ArcGis地图绘画属性设置
 */

public class GisMapDrawBean {
    public static int POINT_COLOR = Color.argb(90, 255, 0, 0);//默认点高亮颜色
    public static int POINT_PIC   = R.drawable.ic_gismap_point_blue;//默认点的高亮图标

    /*点分两周，一种是纯色点， 一种是图片
    * 图片的优先级高于纯色点
    * 若使用纯色点，请务必将pointPic设置为-1，或默认不设置*/
    /**
     * 纯色点的设置
     */
    public int                      pointColor = -1;//点颜色
    public SimpleMarkerSymbol.Style pointType  = SimpleMarkerSymbol.Style.CIRCLE;//点的形状， 默认是圆点
    public int                      pointSize  = 25;

    /**
     * 点图标的设置
     */
    public int pointPic       = -1;//点图标
    public int pointPicWidth  = 60;//点图表的宽度
    public int pointPicHeight = 60;//点图表的高度

    /**
     * 线设置
     */
    public int                    lineColor = Color.argb(90, 255, 0, 0);//线的颜色
    public int                    lineWidth = 4;//线宽度
    public SimpleLineSymbol.Style lineType  = SimpleLineSymbol.Style.SOLID;//线的类型，默认是实心线

    /**
     * 面设置
     */
    public int                    polygonLineColor = Color.RED;//面的边线的颜色
    public int                    polygonLineWidth = 2;//面的边线宽度
    public SimpleLineSymbol.Style polygonLineType  = SimpleLineSymbol.Style.SOLID;//面的边线的类型，默认是实心线
    public int                    polygonFillColor = Color.argb(90, 255, 0, 0);//面的填充的颜色
    public SimpleFillSymbol.Style polygonFillType  = SimpleFillSymbol.Style.SOLID;//填充面的类型，默认实心面

    /**
     * 初始化全部默认绘画属性集合，展示默认点图标
     */
    public static GisMapDrawBean instance() {
        return new GisMapDrawBean();
    }

    /**
     * 初始化纯色点的绘画属性集合
     */
    public static GisMapDrawBean instancePointColor() {
        return new GisMapDrawBean().setPointColor(POINT_COLOR);
    }

    /**
     * 初始化自定义点的绘画属性集合
     */
    public static GisMapDrawBean instancePointColor(int pointColor) {
        return new GisMapDrawBean().setPointColor(pointColor);
    }

    /*初始化图标点的绘画属性集合*/
    public static GisMapDrawBean instancePointPic() {
        return new GisMapDrawBean().setPointPic(POINT_PIC);
    }

    /**
     * 初始化自定义图标点的绘画属性集合
     */
    public static GisMapDrawBean instancePointPic(int pointPic) {
        return new GisMapDrawBean().setPointPic(pointPic);
    }

    public GisMapDrawBean setPointColor(int pointColor) {
        this.pointColor = pointColor;
        this.pointPic = -1;
        return this;
    }

    public GisMapDrawBean setPointPic(int pointPic) {
        this.pointPic = pointPic;
        this.pointColor = -1;
        return this;
    }

    public GisMapDrawBean setLineColor(int lineColor) {
        this.lineColor = lineColor;
        return this;
    }

    public GisMapDrawBean setLineType(SimpleLineSymbol.Style lineType) {
        this.lineType = lineType;
        return this;
    }

    public GisMapDrawBean setPolygonLineColor(int polygonLineColor) {
        this.polygonLineColor = polygonLineColor;
        return this;
    }

    public GisMapDrawBean setPolygonFillColor(int polygonFillColor) {
        this.polygonFillColor = polygonFillColor;
        return this;
    }
}
