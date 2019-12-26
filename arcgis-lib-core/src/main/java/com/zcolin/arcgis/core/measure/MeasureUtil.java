/*
 * *********************************************************
 *   author   colin
 *   email    wanglin2046@126.com
 *   date     19-1-30 下午1:39
 * ********************************************************
 */

package com.zcolin.arcgis.core.measure;

import java.math.BigDecimal;

public class MeasureUtil {

    public static String forMatDouble(double num) {
        BigDecimal b = new BigDecimal(num);
        double df = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return df + "";
    }

    public static double lengthChange(double length, MeasureVariable.Measure type) {
        if (type == MeasureVariable.Measure.M) {
            return length;
        }
        if (type == MeasureVariable.Measure.KM || type == MeasureVariable.Measure.KIM) {
            return length / 1000;
        }
        return 0;
    }

    public static String lengthEnameToCname(MeasureVariable.Measure type) {
        switch (type) {
            case M:
                return "米";
            case KM:
                return "千米";
            case KIM:
                return "公里";
            case M2:
                return "平方米";
            case KM2:
                return "平方千米";
            case HM2:
                return "公顷";
            case A2:
                return "亩";
            default:
                break;
        }
        return null;
    }

    public static double areaChange(double area, MeasureVariable.Measure type) {
        switch (type) {
            case M2:
                return area;
            case KM2:
                return area / 1000;
            case HM2:
                return area / 10000;
            case A2:
                return area / 666.67;
            default:
                break;
        }
        return 0;
    }
}
