/*
 * *********************************************************
 *   author   colin
 *   company  telchina
 *   email    wanglin2046@126.com
 *   date     19-1-30 下午1:32
 * ********************************************************
 */

package com.telchina.arcgis.core.measure;

import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PolygonBuilder;
import com.esri.arcgisruntime.geometry.PolylineBuilder;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.mapping.view.MapView;


public class GisMeasureDraw extends MeasureDraw {
    private MapView mapView;
    private MeasureVariable.Measure measureLengthType = MeasureVariable.Measure.KM;
    private MeasureVariable.Measure measureAreaType   = MeasureVariable.Measure.KM2;
    private double                  lineLength        = 0;

    public GisMeasureDraw(MapView mapView) {
        super(mapView);
        this.mapView = mapView;
    }

    public void startMeasuredLength(float screenX, float screenY) {
        if (getDrawType() == null) {
            super.startLine();
        }
        drawScreenXY(screenX, screenY);
    }

    public void startMeasuredArea(float screenX, float screenY) {
        if (getDrawType() == null) {
            super.startPolygon();
        }
        drawScreenXY(screenX, screenY);
    }

    public void setLengthType(MeasureVariable.Measure type) {
        this.measureLengthType = type;
    }

    public void setAreaType(MeasureVariable.Measure type) {
        this.measureAreaType = type;
    }

    private void drawScreenXY(float x, float y) {
        Point point = super.screenXYtoPpoint(x, y);
        if (mapView.getSpatialReference().getWkid() == 4490 || mapView.getSpatialReference().getWkid() == 4326) {
            point = (Point) GeometryEngine.project(point, SpatialReference.create(102100));
            super.setSpatialReference(SpatialReference.create(102100));
        }

        if (getDrawType() == MeasureVariable.DrawType.LINE) {
            PolylineBuilder line = (PolylineBuilder) super.drawByGisPoint(point);
            showLength(line, point);
        } else if (getDrawType() == MeasureVariable.DrawType.POLYGON) {
            PolygonBuilder polygon = (PolygonBuilder) super.drawByGisPoint(point);
            showArea(polygon);
        }

    }

    private void showLength(PolylineBuilder line, Point point) {
        if (line != null) {
            double length = GeometryEngine.length(line.toGeometry());
            lineLength += length;
            String s = MeasureUtil.forMatDouble(Math.abs(MeasureUtil.lengthChange(lineLength, measureLengthType)));
            super.drawText(point, s + MeasureUtil.lengthEnameToCname(measureLengthType), false);
        }
    }

    private void showArea(PolygonBuilder polygon) {
        if (polygon != null) {
            double area = GeometryEngine.area(polygon.toGeometry());
            String s = MeasureUtil.forMatDouble(Math.abs(MeasureUtil.areaChange(area, measureAreaType)));
            super.drawText(polygon.toGeometry().getExtent().getCenter(), s + MeasureUtil.lengthEnameToCname(measureAreaType), true);
        }
    }
}
