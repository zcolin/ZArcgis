/*
 * *********************************************************
 *   author   colin
 *   company  telchina
 *   email    wanglin2046@126.com
 *   date     19-1-30 下午1:32
 * ********************************************************
 */

package com.telchina.arcgis.core.measure;

import android.graphics.Color;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PolygonBuilder;
import com.esri.arcgisruntime.geometry.PolylineBuilder;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;

import java.util.ArrayList;
import java.util.List;


public class MeasureDraw {
    private MapView          mapView;
    private SpatialReference spatialReference;
    private GraphicsOverlay       drawTextGraphicOverlay    = null;//绘制面板
    private GraphicsOverlay       drawPointGraphicOverlay   = null;
    private GraphicsOverlay       drawLineGraphicOverlay    = null;
    private GraphicsOverlay       drawPolygonGraphicOverlay = null;
    private List<GraphicsOverlay> textGraphic               = null;//文字集合
    private List<GraphicsOverlay> polygonGraphic            = null;//面集合
    private List<GraphicsOverlay> lineGraphic               = null;//线集合
    private List<GraphicsOverlay> pointGraphic              = null;//点集合
    private TextSymbol               textSymbol    = null;//标注样式
    private SimpleMarkerSymbol       pointSymbol   = null;//点样式
    private SimpleLineSymbol         lineSymbol    = null;//线样式
    private SimpleFillSymbol         polygonSymbol = null;//面样式
    private List<List<Point>>        pointGroup    = null;//绘制点的集合的集合
    private List<Point>              pointList     = null;//每次绘制点的集合
    private List<TextSymbol>         textList      = null;//每次绘制文字的集合
    private MeasureVariable.DrawType drawType      = null;//绘制类型

    public MeasureDraw(MapView mapView) {
        this.mapView = mapView;
        init();
    }

    private void init() {
        pointList = new ArrayList<>();
        textList = new ArrayList<>();
        textGraphic = new ArrayList<>();
        polygonGraphic = new ArrayList<>();
        lineGraphic = new ArrayList<>();
        pointGraphic = new ArrayList<>();
        pointGroup = new ArrayList<>();
        pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.GRAY, 8);
        lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 2);
        polygonSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.argb(40, 0, 0, 0), null);
        textSymbol = new TextSymbol(12, "", android.R.color.black, TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.BOTTOM);
    }

    protected void startLine() {
        drawType = MeasureVariable.DrawType.LINE;
    }

    protected void startPolygon() {
        drawType = MeasureVariable.DrawType.POLYGON;
    }

    public MeasureVariable.DrawType getDrawType() {
        return drawType;
    }

    public void setSpatialReference(SpatialReference spatialReference) {
        this.spatialReference = spatialReference;
    }

    private SpatialReference getSpatialReference() {
        if (spatialReference == null) {
            return mapView.getSpatialReference();
        }
        return spatialReference;
    }

    public Object drawByGisPoint(Point point) {
        if (drawType == MeasureVariable.DrawType.POINT) {
            return drawPointByGisPoint(point);
        } else if (drawType == MeasureVariable.DrawType.LINE) {
            return drawLineByGisPoint(point);
        } else if (drawType == MeasureVariable.DrawType.POLYGON) {
            return drawPolygonByGisPoint(point);
        }
        return null;
    }

    private void drawText(Point point) {
        if (drawTextGraphicOverlay == null) {
            drawTextGraphicOverlay = new GraphicsOverlay();
            mapView.getGraphicsOverlays().add(drawTextGraphicOverlay);
            textGraphic.add(drawTextGraphicOverlay);
        }
        Graphic pointGraphic = new Graphic(point, textSymbol);
        drawTextGraphicOverlay.getGraphics().add(pointGraphic);
        textList.add(textSymbol);
    }

    private void drawPoint(Point point) {
        if (drawPointGraphicOverlay == null) {
            drawPointGraphicOverlay = new GraphicsOverlay();
            mapView.getGraphicsOverlays().add(drawPointGraphicOverlay);
            pointGraphic.add(drawPointGraphicOverlay);
        }
        Graphic pointGraphic = new Graphic(point, pointSymbol);
        drawPointGraphicOverlay.getGraphics().add(pointGraphic);
        pointList.add(point);
    }

    private PolylineBuilder drawLine(Point point1, Point point2) {
        //绘制面板为空，说明重新绘制一个linr，在地图和线集合里添加一个新line
        if (drawLineGraphicOverlay == null) {
            drawLineGraphicOverlay = new GraphicsOverlay();
            mapView.getGraphicsOverlays().add(drawLineGraphicOverlay);
            lineGraphic.add(drawLineGraphicOverlay);
        }

        PolylineBuilder lineGeometry = new PolylineBuilder(getSpatialReference());
        lineGeometry.addPoint(point1);
        lineGeometry.addPoint(point2);
        Graphic lineGraphic = new Graphic(lineGeometry.toGeometry(), lineSymbol);
        drawLineGraphicOverlay.getGraphics().add(lineGraphic);
        return lineGeometry;
    }

    private PolygonBuilder drawPolygon() {
        //绘制面板为空，说明重新绘制一个Polyline，在地图和面集合里添加一个新Polyline
        if (drawPolygonGraphicOverlay == null) {
            drawPolygonGraphicOverlay = new GraphicsOverlay();
            mapView.getGraphicsOverlays().add(drawPolygonGraphicOverlay);
            polygonGraphic.add(drawPolygonGraphicOverlay);
        }
        PolygonBuilder polygonGeometry = new PolygonBuilder(getSpatialReference());
        for (Point point : pointList) {
            polygonGeometry.addPoint(point);
        }
        drawPolygonGraphicOverlay.getGraphics().clear();
        Graphic polygonGraphic = new Graphic(polygonGeometry.toGeometry(), polygonSymbol);
        drawPolygonGraphicOverlay.getGraphics().add(polygonGraphic);
        return polygonGeometry;

    }

    private Point drawPointByGisPoint(Point point) {
        this.drawPoint(point);
        return point;
    }

    private PolylineBuilder drawLineByGisPoint(Point point) {
        Point nextPoint = this.drawPointByGisPoint(point);
        if (getPointSize() > 1) {
            Point prvPoint = getLastPoint();
            return this.drawLine(prvPoint, nextPoint);
        }
        return null;
    }

    private PolylineBuilder drawLineByGisPoint(Point point1, Point point2) {
        return this.drawLine(point1, point2);
    }

    private PolygonBuilder drawPolygonByGisPoint(Point point) {
        drawLineByGisPoint(point);
        if (getPointSize() >= 3) {
            return drawPolygon();
        }
        return null;
    }

    public Point screenXYtoPpoint(float x, float y) {
        android.graphics.Point point = new android.graphics.Point(Math.round(x), Math.round(y));
        return mapView.screenToLocation(point);
    }

    public void drawText(Point point, String text, boolean replaceOld) {
        textSymbol = getTextSymbol();
        if (replaceOld) {
            textSymbol.setHorizontalAlignment(TextSymbol.HorizontalAlignment.CENTER);
            if (drawTextGraphicOverlay != null) {
                drawTextGraphicOverlay.getGraphics().clear();
            }
        }
        textSymbol.setText(text);
        drawText(point);
    }

    public MeasureDrawEntity completeDraw() {
        if (drawType == MeasureVariable.DrawType.POLYGON) {
            if (getPointSize() >= 3) {
                this.drawLineByGisPoint(getEndPoint(), getFristPoint());
            }
        }
        if (pointList.size() > 0) {
            pointGroup.add(pointList);
        }
        MeasureDrawEntity de = allDraw();
        drawType = null;
        drawPolygonGraphicOverlay = null;
        drawLineGraphicOverlay = null;
        drawPointGraphicOverlay = null;
        drawTextGraphicOverlay = null;
        pointList = new ArrayList<>();
        return de;
    }

    public MeasureDrawEntity clear() {
        MeasureDrawEntity de = allDraw();
        removeAllGraphics(lineGraphic);
        removeAllGraphics(polygonGraphic);
        removeAllGraphics(pointGraphic);
        removeAllGraphics(textGraphic);
        drawPolygonGraphicOverlay = null;
        drawLineGraphicOverlay = null;
        drawPointGraphicOverlay = null;
        drawTextGraphicOverlay = null;
        drawType = null;
        pointGroup.clear();
        pointList.clear();
        lineGraphic.clear();
        pointGraphic.clear();
        textGraphic.clear();
        pointGraphic.clear();
        pointGroup = new ArrayList<>();
        pointList = new ArrayList<>();
        lineGraphic = new ArrayList<>();
        pointGraphic = new ArrayList<>();
        textGraphic = new ArrayList<>();
        pointGraphic = new ArrayList<>();
        return de;
    }

    private MeasureDrawEntity allDraw() {
        MeasureDrawEntity de = new MeasureDrawEntity();
        de.setLineGraphic(lineGraphic);
        de.setPointGraphic(pointGraphic);
        de.setTextGraphic(textGraphic);
        de.setPolygonGraphic(polygonGraphic);
        de.setPointGroup(pointGroup);
        return de;
    }

    private void removeAllGraphics(List<GraphicsOverlay> go) {
        if (go.size() > 0) {
            for (GraphicsOverlay graphics : go) {
                mapView.getGraphicsOverlays().remove(graphics);
            }
        }
    }

    private TextSymbol getTextSymbol() {
        TextSymbol textSymbol = new TextSymbol(12, "", Color.BLACK, TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.BOTTOM);
        textSymbol.setColor(Color.WHITE);
        textSymbol.setHaloColor(Color.WHITE);
        textSymbol.setHaloWidth(1);
        textSymbol.setOutlineColor(Color.BLACK);
        textSymbol.setOutlineWidth(1);
        return textSymbol;
    }

    private int getPointSize() {
        return pointList.size();
    }

    public Point getEndPoint() {
        int index = getPointSize() > 1 ? getPointSize() - 1 : 0;
        return pointList.get(index);
    }

    private Point getLastPoint() {
        int index = getPointSize() > 1 ? getPointSize() - 2 : 0;
        return pointList.get(index);
    }

    private Point getFristPoint() {
        if (getPointSize() == 0) {
            return null;
        }
        return pointList.get(0);
    }

}
