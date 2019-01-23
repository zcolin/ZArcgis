/*
 * *********************************************************
 *   author   colin
 *   company  telchina
 *   email    wanglin2046@126.com
 *   date     19-1-23 上午10:25
 * ********************************************************
 */

package com.telchina.tharcgiscore.util;

import android.annotation.SuppressLint;

import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.Multipart;
import com.esri.arcgisruntime.geometry.Point;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * WKT转json字符串
 */
public class WktConvertUtil {

    public static String wkt2Json(String wkt, int wkid) {
        if (wkt == null || wkt.length() == 0) {
            return wkt;
        }

        if (wkt.contains("MULTIPOINT")) {
            return getMultipointWktToJson(wkt, wkid);
        } else if (wkt.contains("POINT")) {
            return getPointWktToJson(wkt, wkid);
        } else if (wkt.contains("MULTILINESTRING")) {
            return getMultilinestringWktToJson(wkt, wkid);
        } else if (wkt.contains("LINESTRING")) {
            return getLinestringWktToJson(wkt, wkid);
        } else if (wkt.contains("MULTIPOLYGON")) {
            return getMultipolygonWktToJson(wkt, wkid);
        } else if (wkt.contains("POLYGON")) {
            return getPolygonWktToJson(wkt, wkid);
        }
        return wkt;
    }

    /**
     * 点 转换 JSON
     */
    private static String getPointWktToJson(String wkt, int wkid) {
        String[] strResult = wkt.trim().split("\\(")[1].replace(")", "").trim().split(" ");
        PointBean pointBean = new PointBean();
        pointBean.x = Double.parseDouble(strResult[0]);
        pointBean.y = Double.parseDouble(strResult[1]);
        HashMap<String, Integer> spatialReference = new HashMap<>();
        spatialReference.put("wkid", wkid);
        pointBean.spatialReference = spatialReference;
        Gson gson = new Gson();
        return gson.toJson(pointBean);
    }

    /**
     * 多点 转换 JSON
     */
    private static String getMultipointWktToJson(String wkt, int wkid) {
        String[] strResult = wkt.trim().split("\\(")[1].replace(")", "").trim().split(",");
        List<Double[]> list = new ArrayList<>();
        for (String strMiddle1 : strResult) {
            String[] items = strMiddle1.trim().split(" ");
            Double[] listResult = new Double[]{Double.parseDouble(items[0]), Double.parseDouble(items[1])};
            list.add(listResult);
        }
        HashMap<String, Integer> spatialReference = new HashMap<>();
        spatialReference.put("wkid", wkid);
        MultiIPointBean multiIPointBean = new MultiIPointBean();
        multiIPointBean.points = list;
        multiIPointBean.spatialReference = spatialReference;
        Gson gson = new Gson();
        return gson.toJson(multiIPointBean);
    }

    /**
     * 线 转换 JSON
     */
    private static String getLinestringWktToJson(String wkt, int wkid) {
        List<List<Double[]>> lists = new ArrayList<>();
        List<Double[]> list = new ArrayList<>();
        String[] strHead = wkt.trim().split("\\(");
        String strContent = strHead[1].substring(0, strHead[1].length() - 1);
        String[] strResult = strContent.trim().split(",");
        for (String aStrResult : strResult) {
            String itme = aStrResult.trim();
            String[] items = itme.split(" ");
            Double[] listResult = new Double[]{Double.parseDouble(items[0]), Double.parseDouble(items[1])};
            list.add(listResult);
        }
        lists.add(list);
        HashMap<String, Integer> spatialReference = new HashMap<>();
        spatialReference.put("wkid", wkid);
        LineStringBean lineStringBean = new LineStringBean();
        lineStringBean.paths = lists;
        lineStringBean.spatialReference = spatialReference;
        Gson gson = new Gson();
        return gson.toJson(lineStringBean);
    }

    /**
     * 多线 转换 JSON
     */
    private static String getMultilinestringWktToJson(String wkt, int wkid) {
        List<List<Double[]>> lists = new ArrayList<>();
        String[] strList = wkt.trim().substring(0, wkt.length() - 1).trim().split("\\(", 2)[1].trim().split("\\),\\(");
        for (String aStrList : strList) {
            String[] items = aStrList.trim().substring(1, aStrList.length() - 1).trim().split(",");
            List<Double[]> list = new ArrayList<>();
            for (String item1 : items) {
                String[] jItems = item1.trim().split(" ");
                Double[] listResult = new Double[]{Double.parseDouble(jItems[0]), Double.parseDouble(jItems[1])};
                list.add(listResult);
            }
            lists.add(list);
        }
        HashMap<String, Integer> spatialReference = new HashMap<>();
        spatialReference.put("wkid", wkid);
        MultLinesStringBean lineStringObject = new MultLinesStringBean();
        lineStringObject.rings = lists;
        lineStringObject.spatialReference = spatialReference;
        Gson gson = new Gson();
        return gson.toJson(lineStringObject);
    }

    /**
     * 多边形 转换 JSON
     */
    private static String getPolygonWktToJson(String wkt, int wkid) {
        List<List<Double[]>> lists = new ArrayList<>();
        String[] strList = wkt.trim().substring(0, wkt.length() - 1).trim().split("\\(", 2)[1].trim().split("\\),\\(");
        for (String aStrList : strList) {
            String[] items = aStrList.trim().replace("(", "").replace(")", "").trim().split(",");
            List<Double[]> list = new ArrayList<>();
            for (String item1 : items) {
                String[] jItems = item1.trim().split(" ");
                Double[] listResult = new Double[]{Double.parseDouble(jItems[0]), Double.parseDouble(jItems[1])};
                list.add(listResult);
            }
            lists.add(list);
        }
        HashMap<String, Integer> spatialReference = new HashMap<>();
        spatialReference.put("wkid", wkid);
        PolygonBean polygonBean = new PolygonBean();
        polygonBean.rings = lists;
        polygonBean.spatialReference = spatialReference;
        Gson gson = new Gson();
        return gson.toJson(polygonBean);
    }

    /**
     * 多个多边形 转换 JSON
     */
    private static String getMultipolygonWktToJson(String wkt, int wkid) {
        PolygonBean polygonBean = new PolygonBean();
        List<List<Double[]>> lists = new ArrayList<>();
        String[] strList = wkt.trim().substring(0, wkt.length() - 1).trim().split("\\(", 2)[1].trim().split("\\),\\(");
        for (String aStrList : strList) {
            aStrList = aStrList.trim().replace("(", "").replace(")", "");
            String[] items = aStrList.split(",");
            List<Double[]> list = new ArrayList<>();
            for (String item1 : items) {
                String[] jItems = item1.trim().split(" ");
                Double[] listResult = new Double[]{Double.parseDouble(jItems[0]), Double.parseDouble(jItems[1])};
                list.add(listResult);
            }
            lists.add(list);
        }
        HashMap<String, Integer> spatialReference = new HashMap<>();
        spatialReference.put("wkid", wkid);
        polygonBean.rings = lists;
        polygonBean.spatialReference = spatialReference;
        Gson gson = new Gson();
        return gson.toJson(polygonBean);
    }

    public static List<List<Double[]>> wkt2List(String wkt) {
        if (wkt == null || wkt.length() == 0) {
            return null;
        }

        if (wkt.contains("MULTILINESTRING")) {
            return getMultilinestringWktToList(wkt);
        } else if (wkt.contains("LINESTRING")) {
            return getLinestringWktToList(wkt);
        } else if (wkt.contains("MULTIPOLYGON")) {
            return getMultipolygonWktToList(wkt);
        } else if (wkt.contains("POLYGON")) {
            return getPolygonWktToList(wkt);
        }
        return null;
    }

    private static List<List<Double[]>> getMultilinestringWktToList(String wkt) {
        List<List<Double[]>> lists = new ArrayList<>();
        String[] strList = wkt.substring(0, wkt.length() - 1).split("\\(", 2)[1].split("\\),\\(");
        for (String aStrList : strList) {
            String[] items = aStrList.trim().substring(1, aStrList.length() - 1).split(",");
            List<Double[]> list = new ArrayList<>();
            for (String item1 : items) {
                String[] jItems = item1.trim().split(" ");
                Double[] listResult = new Double[]{Double.parseDouble(jItems[0]), Double.parseDouble(jItems[1])};
                list.add(listResult);
            }
            lists.add(list);
        }
        return lists;
    }

    private static List<List<Double[]>> getLinestringWktToList(String wkt) {
        List<List<Double[]>> lists = new ArrayList<>();
        List<Double[]> list = new ArrayList<>();
        String[] strHead = wkt.split("\\(");
        String strContent = strHead[1].substring(0, strHead[1].length() - 1);
        String[] strResult = strContent.split(",");
        for (String aStrResult : strResult) {
            String itme = aStrResult.trim();
            String[] items = itme.split(" ");
            Double[] listResult = new Double[]{Double.parseDouble(items[0]), Double.parseDouble(items[1])};
            list.add(listResult);
        }
        lists.add(list);
        return lists;
    }


    private static List<List<Double[]>> getPolygonWktToList(String wkt) {
        List<List<Double[]>> lists = new ArrayList<>();
        String[] strList = wkt.substring(0, wkt.length() - 1).split("\\(", 2)[1].split("\\),\\(");
        for (String aStrList : strList) {
            String[] items = aStrList.trim().replace("(", "").replace(")", "").split(",");
            List<Double[]> list = new ArrayList<>();
            for (String item1 : items) {
                String[] jItems = item1.trim().split(" ");
                Double[] listResult = new Double[]{Double.parseDouble(jItems[0]), Double.parseDouble(jItems[1])};
                list.add(listResult);
            }
            lists.add(list);
        }
        return lists;
    }

    private static List<List<Double[]>> getMultipolygonWktToList(String wkt) {
        List<List<Double[]>> lists = new ArrayList<>();
        String[] strList = wkt.substring(0, wkt.length() - 1).split("\\(", 2)[1].split("\\),\\(");
        for (String aStrList : strList) {
            aStrList = aStrList.trim().replace("(", "").replace(")", "");
            String[] items = aStrList.split(",");
            List<Double[]> list = new ArrayList<>();
            for (String item1 : items) {
                String[] jItems = item1.trim().split(" ");
                Double[] listResult = new Double[]{Double.parseDouble(jItems[0]), Double.parseDouble(jItems[1])};
                list.add(listResult);
            }
            lists.add(list);
        }
        return lists;
    }


    public static String geomToWKT(Geometry geom) {
        // ============================================================
        // 'They' say StringBuoder is faster than StringBuffer
        // ===========================================================
        StringBuilder wkt = new StringBuilder();

        // ============================================================
        // Convert an ARJ point to a WKT point
        // ===========================================================
        if (geom.getGeometryType() == GeometryType.POINT) {
            Point tmpPT = (Point) geom;
            wkt.append(String.format("POINT(%s %s)", tmpPT.getX(), tmpPT.getY()));

        }
        // ============================================================
        // Convert an ARJ polygon to a WKT polygon
        // ============================================================
        else if (geom.getGeometryType() == GeometryType.POLYGON) {
            Multipart tmpPath = (Multipart) geom;
            //            printAllPoints(tmpPath);

            // ============================================================
            // Start the WKT for the polygon
            // ===========================================================
            wkt.append("POLYGON(");

            int pathStart, pathEnd;
            for (int i = 0; i < tmpPath.getParts().size(); i++) {
                wkt.append("(");
                // ===========================================================
                // Depending which drawing mode was used to capture the polygon
                // there are some geometry issues to solve:
                // DrawingMode.POLYGON_RECTANGLE has the traditional connect the
                // dot
                // finish where you end geometry --> normal conversion
                // DrawingMode.POLYGON does not and there are 2 issues:
                // 1.) There is a stutter: the first point/node is duplicated
                // 2.) The paths do not explicitly close
                // Currently I leave the duplicate in to keep the logic simple
                // Also, if the last point of the path does not equal the first
                // (it will not for DrawingMode.POLYGON) then punch out the
                // start point to close the polygon
                // ===========================================================
                // ===========================================================
                // There can be multiple paths in the polygon:
                // 1.) disjoint rings
                // 2.) donuts
                // Set the start and end for the current path
                // ===========================================================
                // ===========================================================
                // Set the start and end points
                // ===========================================================
                Point startPT = tmpPath.getParts().get(i).getStartPoint();
                Point endPT = tmpPath.getParts().get(i).getEndPoint();
                // ===========================================================
                // punch out all the points for a path starting at the first
                // point
                // ===========================================================
                for (int j = 0; j < tmpPath.getParts().get(i).getPointCount(); j++) {
                    wkt.append(getWKTxy(tmpPath.getParts().get(i).getPoint(j)));
                    // ===========================================================
                    // do not add a comma if this is the last point
                    // ===========================================================
                    if (j < tmpPath.getParts().get(i).getPointCount() - 1) {
                        wkt.append(" , ");
                    }
                } // end the for all the points in a ring
                // ===========================================================
                // close the ring using the start point if the end sand
                // start
                // points are not the same
                // ===========================================================
                if (startPT.getX() != endPT.getX() || startPT.getY() != endPT.getY()) {
                    wkt.append(" , ").append(getWKTxy(startPT));
                }
                // ===========================================================
                // close the ring
                // ===========================================================
                wkt.append(")");
                // ===========================================================
                // add a separator if there is more than one ring and not the
                // this ring in not the last ring
                // ===========================================================
                if (tmpPath.getParts().size() > 0 && i < tmpPath.getParts().size() - 1) {
                    wkt.append(" , ");
                }

            } // end the for all the rings in a polygon
            // ===========================================================
            // close the polygon
            // ===========================================================
            wkt.append(")");

        }
        // ============================================================
        // Convert an ARJ Polyline to a WKT polygon
        // ============================================================
        else if (geom.getGeometryType() == GeometryType.POLYLINE) {
            Multipart tmpPath = (Multipart) geom;
            //            printAllPoints(tmpPath);
            // ============================================================
            // Start the WKT for the polygon
            // ===========================================================
            wkt.append("MULTILINESTRING(");

            int pathStart, pathEnd;
            for (int i = 0; i < tmpPath.getParts().size(); i++) {
                wkt.append("(");
                // ===========================================================
                // Get the starting index and set the end
                // =============================================================
                // ===========================================================
                // punch out all the points for a path
                // ===========================================================
                for (int j = 0; j < tmpPath.getParts().get(i).getPointCount(); j++) {
                    wkt.append(getWKTxy(tmpPath.getParts().get(i).getPoint(j)));
                    if (j < tmpPath.getParts().get(i).getPointCount() - 1) {
                        wkt.append(" , ");
                    }
                } // end the for all the points in a path

                // ===========================================================
                // close the WKT line-string within the multi-line-string
                // ===========================================================
                wkt.append(")");
                // ===========================================================
                // add a separator if there is more than one line-string
                // in the multi-line-string
                // ===========================================================
                if (tmpPath.getParts().size() > 0 && i < tmpPath.getParts().size() - 1) {
                    wkt.append(" , ");
                }

            } // end the for each path

            // ===========================================================
            // close the WKT multi-line-string
            // ===========================================================
            wkt.append(")");

        }
        return wkt.toString();
    }

    // ===========================================================
    // convenience method to keep the code clean
    // ===========================================================
    @SuppressLint("DefaultLocale")
    private static String getWKTxy(Point pt) {
        return String.format("%.6f %.6f", pt.getX(), pt.getY());
    }

    /**
     * wkt字符串的点线面数据 转换成json并生成相应的Geometry对象
     */
    public static Geometry getGeometryFromWkt(String wktstr, int wkid) {
        String strJson = wkt2Json(wktstr, wkid);
        return Geometry.fromJson(strJson);
    }

    /**
     * 获取缓冲区的
     */
    public static Geometry getBufferGeometry(Geometry geometry, double distance) {
        return GeometryEngine.buffer(geometry, distance);
    }

    /**
     * 获取Geometry中心点坐标
     */
    public static Point getCenterGeometry(String wkt, int wkid) {
        Geometry geometry = getGeometryFromWkt(wkt, wkid);
        return geometry == null ? null : geometry.getExtent().getCenter();
    }

    /**
     * wkt转json的线对象
     */

    public static class LineStringBean {
        public List<List<Double[]>>     paths;
        public HashMap<String, Integer> spatialReference;
    }

    /**
     * wkt转json的多点对象
     */
    public static class MultiIPointBean {
        public List<Double[]>           points;
        public HashMap<String, Integer> spatialReference;
    }

    /**
     * wkt转json的多线对象
     */
    public static class MultLinesStringBean {
        public List<List<Double[]>>     rings;
        public HashMap<String, Integer> spatialReference;
    }

    /**
     * wkt转json的点对象
     */
    public static class PointBean {
        public double                   x;
        public double                   y;
        public HashMap<String, Integer> spatialReference;
    }

    /**
     * wkt转json的面对象
     */
    public static class PolygonBean {
        public List<List<Double[]>>     rings;
        public HashMap<String, Integer> spatialReference;
    }
}
