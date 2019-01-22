/*
 * *********************************************************
 *   author   colin
 *   company  telchina
 *   email    wanglin2046@126.com
 *   date     19-1-22 下午3:00
 * ********************************************************
 */

package com.telchina.tharcgiscore;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.Constraints;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.telchina.tharcgiscore.util.ThemeUtil;
import com.telchina.tharcgiscore.util.Wkt2JsonUtil;
import com.zcolin.frame.util.DisplayUtil;
import com.zcolin.frame.util.ScreenUtil;
import com.zcolin.gui.ZDialog;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 地图弹出菜单管理类
 */

public class CalloutMgr {

    /**
     * 自定义CalloutStyle和view
     *
     * @param calloutStyle callout的样式
     * @param view         callout展示内容
     * @param centerPoint  callout的展示位置
     */
    public static void showCallout(Callout callout, Callout.Style calloutStyle, View view, Point centerPoint) {
        callout.setStyle(calloutStyle);
        callout.show(view, centerPoint);
    }

    /**
     * 自定义Callout展示内容 和 x、y偏移量
     *
     * @param view        callout展示内容
     * @param centerPoint callout的展示位置
     * @param offSetX     显示位置X方向偏移
     * @param offSetY     显示位置Y方向偏移
     */
    public static void showCallout(Context context, Callout callout, View view, Point centerPoint, int offSetX, int offSetY) {
        Callout.Style calloutStyle = new Callout.Style(context);
        calloutStyle.setLeaderPosition(Callout.Style.LeaderPosition.LOWER_MIDDLE);
        calloutStyle.setBackgroundColor(Color.WHITE);
        calloutStyle.setBorderColor(ThemeUtil.getColor(context, R.attr.calloutDividerColor));
        callout.setStyle(calloutStyle);
        centerPoint = new Point(centerPoint.getX() - offSetX, centerPoint.getY() - offSetY, centerPoint.getSpatialReference());
        callout.show(view, centerPoint);
    }

    /**
     * 只有标题，point显示无偏移
     * 显示坐标点弹出菜单, 显示在集合要素的中心点
     *
     * @param context         上下文
     * @param callout         地图callout对象
     * @param name            项目名称或者标题
     * @param geometry        要素信息
     * @param onCloseListener 关闭按钮监听
     */
    public static void showCallout(Context context, Callout callout, String name, Geometry geometry, View.OnClickListener onCloseListener) {
        showCallout(context, callout, name, null, Wkt2JsonUtil.getCenterGeometry(geometry), null, onCloseListener, null, null);
    }

    /**
     * 只有标题，point显示有偏移
     * 显示坐标点弹出菜单, 显示在集合要素的中心点
     *
     * @param context         上下文
     * @param callout         地图callout对象
     * @param name            项目名称或者标题
     * @param geometry        要素信息
     * @param onCloseListener 关闭按钮监听
     */
    public static void showCalloutWithOffSet(Context context, Callout callout, String name, int offSetX, int offSetY, Geometry geometry, View.OnClickListener onCloseListener) {
        if (geometry instanceof Point) {
            showCalloutWithOffSet(context, callout, name, null, Wkt2JsonUtil.getCenterGeometry(geometry), offSetX, offSetY, null, onCloseListener, null, null);
        } else {
            showCallout(context, callout, name, Wkt2JsonUtil.getCenterGeometry(geometry), onCloseListener);
        }
    }

    /**
     * 只有标题和内容，point显示无偏移
     * 显示坐标点弹出菜单, 显示在集合要素的中心点
     *
     * @param context         上下文
     * @param callout         地图callout对象
     * @param name            项目名称或者标题
     * @param info            展示信息的map
     * @param geometry        要素信息
     * @param onCloseListener 关闭按钮监听
     */
    public static void showCallout(Context context, Callout callout, String name, LinkedHashMap<String, String> info, Geometry geometry, View.OnClickListener onCloseListener) {
        showCallout(context, callout, name, info, Wkt2JsonUtil.getCenterGeometry(geometry), null, onCloseListener, null, null);
    }

    /**
     * 只有标题和内容，point显示有偏移
     * 显示坐标点弹出菜单, 显示在集合要素的中心点
     *
     * @param context         上下文
     * @param callout         地图callout对象
     * @param name            项目名称或者标题
     * @param info            展示信息的map
     * @param geometry        要素信息
     * @param onCloseListener 关闭按钮监听
     */
    public static void showCalloutWithOffSet(Context context, Callout callout, String name, LinkedHashMap<String, String> info, int offSetX, int offSetY, Geometry geometry,
            View.OnClickListener onCloseListener) {
        if (geometry instanceof Point) {
            showCalloutWithOffSet(context, callout, name, info, Wkt2JsonUtil.getCenterGeometry(geometry), offSetX, offSetY, null, onCloseListener, null, null);
        } else {
            showCallout(context, callout, name, info, Wkt2JsonUtil.getCenterGeometry(geometry), onCloseListener);
        }
    }

    /**
     * 只有标题、内容、查看详情，point显示无偏移
     * 显示坐标点弹出菜单, 显示在集合要素的中心点
     *
     * @param context          上下文
     * @param callout          地图callout对象
     * @param name             项目名称或者标题
     * @param info             展示信息的map
     * @param geometry         要素信息
     * @param onCloseListener  关闭按钮监听
     * @param OnDetailListener 查看详情按钮监听
     */
    public static void showCallout(Context context, Callout callout, String name, LinkedHashMap<String, String> info, Geometry geometry, View.OnClickListener onCloseListener,
            View.OnClickListener OnDetailListener) {
        showCallout(context, callout, name, info, Wkt2JsonUtil.getCenterGeometry(geometry), null, onCloseListener, OnDetailListener, null);
    }

    /**
     * 只有标题、内容、查看详情，point显示有偏移
     * 显示坐标点弹出菜单, 显示在集合要素的中心点
     *
     * @param context          上下文
     * @param callout          地图callout对象
     * @param name             项目名称或者标题
     * @param info             展示信息的map
     * @param geometry         要素信息
     * @param onCloseListener  关闭按钮监听
     * @param OnDetailListener 查看详情按钮监听
     */
    public static void showCalloutWithOffSet(Context context, Callout callout, String name, LinkedHashMap<String, String> info, int offSetX, int offSetY, Geometry geometry,
            View.OnClickListener onCloseListener, View.OnClickListener OnDetailListener) {
        if (geometry instanceof Point) {
            showCalloutWithOffSet(context, callout, name, info, Wkt2JsonUtil.getCenterGeometry(geometry), offSetX, offSetY, null, onCloseListener, OnDetailListener, null);
        } else {
            showCallout(context, callout, name, info, Wkt2JsonUtil.getCenterGeometry(geometry), onCloseListener, OnDetailListener);
        }
    }

    /**
     * 只有标题、内容、搜索周边，point显示无偏移
     * 显示坐标点弹出菜单, 显示在集合要素的中心点
     *
     * @param context              上下文
     * @param callout              地图callout对象
     * @param name                 项目名称或者标题
     * @param info                 展示信息的map
     * @param geometry             要素信息
     * @param distanceList         缓冲区半径list
     * @param onCloseListener      关闭按钮的监听
     * @param onSearchNearListener 搜索周边按钮的监听
     */
    public static void showCallout(Context context, Callout callout, String name, LinkedHashMap<String, String> info, Geometry geometry, List<String> distanceList,
            View.OnClickListener onCloseListener, ZDialog.ZDialogParamSubmitListener<String> onSearchNearListener) {
        showCallout(context, callout, name, info, Wkt2JsonUtil.getCenterGeometry(geometry), distanceList, onCloseListener, null, onSearchNearListener);
    }

    /**
     * 只有标题、内容、搜索周边，point显示有偏移
     * 显示坐标点弹出菜单, 显示在集合要素的中心点
     *
     * @param context              上下文
     * @param callout              地图callout对象
     * @param name                 项目名称或者标题
     * @param info                 展示信息的map
     * @param geometry             要素信息
     * @param distanceList         缓冲区半径list
     * @param onCloseListener      关闭按钮的监听
     * @param onSearchNearListener 搜索周边按钮的监听
     */
    public static void showCalloutWithOffSet(Context context, Callout callout, String name, LinkedHashMap<String, String> info, int offSetX, int offSetY, Geometry geometry, List<String> distanceList,
            View.OnClickListener onCloseListener, ZDialog.ZDialogParamSubmitListener<String> onSearchNearListener) {
        if (geometry instanceof Point) {
            showCalloutWithOffSet(context, callout, name, info, Wkt2JsonUtil.getCenterGeometry(geometry), offSetX, offSetY, distanceList, onCloseListener, null, onSearchNearListener);
        } else {
            showCallout(context, callout, name, info, Wkt2JsonUtil.getCenterGeometry(geometry), distanceList, onCloseListener, null, onSearchNearListener);
        }
    }

    /**
     * 显示坐标点弹出菜单。无偏移
     *
     * @param name                 项目名称或者标题
     * @param info                 展示信息的map
     * @param location             显示位置
     * @param distanceList         缓冲区半径list
     * @param onCloseListener      关闭按钮的监听
     * @param onDetailListener     详情按钮的监听
     * @param onSearchNearListener 搜索周边按钮的监听
     */
    public static void showCallout(Context context, Callout callout, String name, LinkedHashMap<String, String> info, Point location, List<String> distanceList, View.OnClickListener onCloseListener,
            View.OnClickListener onDetailListener, ZDialog.ZDialogParamSubmitListener<String> onSearchNearListener) {
        showCalloutWithOffSet(context, callout, name, info, location, 0, 0, distanceList, onCloseListener, onDetailListener, onSearchNearListener);
    }

    /**
     * 显示坐标点弹出菜单。可设置偏移
     *
     * @param name                 项目名称或者标题
     * @param info                 展示信息的map
     * @param location             显示位置
     * @param offSetX              X方向的偏移量
     * @param offSetY              Y方向的偏移量
     * @param distanceList         缓冲区半径list
     * @param onCloseListener      关闭按钮的监听
     * @param onDetailListener     详情按钮的监听
     * @param onSearchNearListener 搜索周边按钮的监听
     */
    public static void showCalloutWithOffSet(Context context, Callout callout, String name, LinkedHashMap<String, String> info, Point location, int offSetX, int offSetY, List<String> distanceList,
            View.OnClickListener onCloseListener, View.OnClickListener onDetailListener, ZDialog.ZDialogParamSubmitListener<String> onSearchNearListener) {
        View view;
        if (info != null && info.size() > 0) {
            if (onSearchNearListener != null || onDetailListener != null) {
                view = getCalloutNearBy(context, name, info, onCloseListener, onDetailListener, onSearchNearListener, distanceList);
            } else {
                view = getCalloutWithContent(context, name, info, onCloseListener);
            }
        } else {
            if (onSearchNearListener != null || onDetailListener != null) {
                view = getCalloutWithoutContent(context, name, onCloseListener, onDetailListener, onSearchNearListener, distanceList);
            } else {
                view = getCalloutTitle(context, name, onCloseListener);
            }
        }

        showCallout(context, callout, view, location, offSetX, offSetY);
    }

    /**
     * 获取只有标题的CalloutVIew
     */
    private static View getCalloutTitle(Context context, String name, View.OnClickListener onCloseListener) {
        CalloutView view = new CalloutView(context);
        view.setLayoutParams(new Constraints.LayoutParams(ScreenUtil.getScreenWidth(context) / (ScreenUtil.isTablet(context) ? 3 : 2), ViewGroup.LayoutParams.WRAP_CONTENT));
        view.setOnCloseListener(onCloseListener);
        view.setOnDetailListener(null);
        view.setOnSearchListener(null);
        view.setDividerTopVisible(View.GONE);
        view.setContentVisible(View.GONE);
        view.setDividerBottomVisible(View.GONE);
        view.setTitle(name);
        return view;
    }

    /**
     * 获取有标题和内容的CalloutVIew
     */
    private static View getCalloutWithContent(Context context, String name, LinkedHashMap<String, String> info, View.OnClickListener onCloseListener) {
        CalloutView view = new CalloutView(context);
        view.getTitleLayout().getLayoutParams().width = ScreenUtil.getScreenWidth(context) / (ScreenUtil.isTablet(context) ? 3 : 2);
        view.getDividerTopView().getLayoutParams().width = ScreenUtil.getScreenWidth(context) / (ScreenUtil.isTablet(context) ? 3 : 2);
        view.setOnCloseListener(onCloseListener);
        view.setOnDetailListener(null);
        view.setOnSearchListener(null);
        view.setDividerTopVisible(View.VISIBLE);
        view.setDividerBottomVisible(View.GONE);
        view.setTitle(name);
        LinearLayout llContent = view.getContentLayout();
        llContent.getLayoutParams().width = ScreenUtil.getScreenWidth(context) / (ScreenUtil.isTablet(context) ? 3 : 2);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int i = 0;
        LinearLayout leftView = null;
        LinearLayout rightView = null;
        for (Object obj : info.entrySet()) {
            Map.Entry entry = (Map.Entry) obj;
            if (ScreenUtil.isTablet(context)) {
                if (i % 2 == 0) {
                    rightView = null;
                    leftView = createView(context, String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
                } else if (i % 2 == 1) {
                    rightView = createView(context, String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
                    llContent.addView(createLinearLayout(context, leftView, rightView), layoutParams);
                }
                if (i == info.size() - 1 && info.size() % 2 == 1) {
                    llContent.addView(createLinearLayout(context, leftView, rightView), layoutParams);
                }
            } else {
                llContent.addView(createView(context, String.valueOf(entry.getKey()), String.valueOf(entry.getValue())), layoutParams);
            }
            i++;
        }
        return view;
    }

    /**
     * 获取有标题和（搜索或查看详情）的CalloutVIew
     */
    private static View getCalloutWithoutContent(Context context, String name, View.OnClickListener onCloseListener, View.OnClickListener onDetailListener,
            ZDialog.ZDialogParamSubmitListener<String> onSearchNearListener, List<String> distanceList) {
        CalloutView view = new CalloutView(context);
        view.getTitleLayout().getLayoutParams().width = ScreenUtil.getScreenWidth(context) / (ScreenUtil.isTablet(context) ? 3 : 2);
        view.getDividerBottomView().getLayoutParams().width = ScreenUtil.getScreenWidth(context) / (ScreenUtil.isTablet(context) ? 3 : 2);
        view.setOnCloseListener(onCloseListener);
        view.setOnDetailListener(onDetailListener);
        view.setOnSearchListener(onSearchNearListener);
        view.setDividerTopVisible(View.GONE);
        view.setContentVisible(View.GONE);
        view.setDividerBottomVisible(View.VISIBLE);
        view.setTitle(name);
        view.initRecyclerBuffer(distanceList);
        return view;
    }

    /**
     * 获取有标题、内容和（搜周边或详情）的CalloutView
     */
    private static View getCalloutNearBy(Context context, String name, LinkedHashMap<String, String> info, View.OnClickListener onCloseListener, View.OnClickListener onDetailListener,
            ZDialog.ZDialogParamSubmitListener<String> onSearchNearListener, List<String> distanceList) {

        CalloutView view = new CalloutView(context);
        view.getTitleLayout().getLayoutParams().width = ScreenUtil.getScreenWidth(context) / (ScreenUtil.isTablet(context) ? 3 : 2);
        view.getDividerTopView().getLayoutParams().width = ScreenUtil.getScreenWidth(context) / (ScreenUtil.isTablet(context) ? 3 : 2);
        view.getDividerBottomView().getLayoutParams().width = ScreenUtil.getScreenWidth(context) / (ScreenUtil.isTablet(context) ? 3 : 2);
        view.setOnCloseListener(onCloseListener);
        view.setOnDetailListener(onDetailListener);
        view.setOnSearchListener(onSearchNearListener);
        view.setDividerTopVisible(View.VISIBLE);
        view.setContentVisible(View.VISIBLE);
        view.setDividerBottomVisible(View.VISIBLE);
        view.setTitle(name);
        view.initRecyclerBuffer(distanceList);
        LinearLayout llContent = view.getContentLayout();
        llContent.getLayoutParams().width = ScreenUtil.getScreenWidth(context) / (ScreenUtil.isTablet(context) ? 3 : 2);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int i = 0;
        LinearLayout leftView = null;
        LinearLayout rightView = null;
        for (Object obj : info.entrySet()) {
            Map.Entry entry = (Map.Entry) obj;
            if (ScreenUtil.isTablet(context)) {
                if (i % 2 == 0) {
                    rightView = null;
                    leftView = createView(context, String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
                } else if (i % 2 == 1) {
                    rightView = createView(context, String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
                    llContent.addView(createLinearLayout(context, leftView, rightView), layoutParams);
                }
                if (i == info.size() - 1 && info.size() % 2 == 1) {
                    llContent.addView(createLinearLayout(context, leftView, rightView), layoutParams);
                }
            } else {
                llContent.addView(createView(context, String.valueOf(entry.getKey()), String.valueOf(entry.getValue())), layoutParams);
            }
            i++;
        }

        return view;
    }

    /**
     * 新建展示LinearLayout
     */
    private static View createLinearLayout(Context context, LinearLayout leftView, LinearLayout rightView) {
        LinearLayout llChildParent = new LinearLayout(context);
        llChildParent.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        llChildParent.addView(leftView, layoutParams);
        if (rightView != null) {
            llChildParent.addView(rightView, layoutParams);
        } else {
            llChildParent.addView(new LinearLayout(context), layoutParams);
        }
        return llChildParent;
    }

    /**
     * 新建展示view
     */
    private static LinearLayout createView(Context context, String key, String value) {
        LinearLayout llChild = new LinearLayout(context);
        llChild.setOrientation(LinearLayout.HORIZONTAL);
        int padding = DisplayUtil.dip2px(context, 2);
        llChild.setPadding(0, padding, 0, 0);
        TextView tvKey = new TextView(context);
        tvKey.setTextAppearance(context, ThemeUtil.getResource(context, R.attr.calloutTextSubPrimaryStyle));
        tvKey.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
        tvKey.setText(String.format("%s:", key));
        tvKey.setPadding(0, 0, padding, 0);
        LinearLayout.LayoutParams layoutParamsKey = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        layoutParamsKey.gravity = Gravity.CENTER_VERTICAL;
        llChild.addView(tvKey, layoutParamsKey);
        TextView tvValue = new TextView(context);
        tvValue.setTextAppearance(context, ThemeUtil.getResource(context, R.attr.calloutTextSubPrimaryStyle));
        tvValue.setGravity(Gravity.CENTER_VERTICAL);
        tvValue.setText(value);
        tvValue.setPadding(padding, padding, padding, padding);
        LinearLayout.LayoutParams layoutParamsValue = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        layoutParamsValue.gravity = Gravity.CENTER_VERTICAL;
        llChild.addView(tvValue, layoutParamsValue);
        return llChild;
    }
}
