/*
 * *********************************************************
 *   author   colin
 *   email    wanglin2046@126.com
 *   date     19-1-23 下午2:48
 * ********************************************************
 */

package com.zcolin.arcgis.core.tiledservice;

import com.zcolin.arcgis.core.GisMapConfig;

/**
 * 地图参数
 */
public abstract class BaseTiledParam {

    protected GisMapConfig gisMapConfig;

    public BaseTiledParam(GisMapConfig gisMapConfig) {
        this.gisMapConfig = gisMapConfig;
    }

    public boolean isCacheEnabled() {
        return gisMapConfig.isCacheBaseMap();
    }

    public String getCachePath() {
        return gisMapConfig.getBaseMapCachePath();
    }

    /**
     * 地图初始缩放
     */
    public abstract double getInitScale();

    /**
     * 地图中心点
     */
    public abstract double[] getCenterPoint();

    /**
     * 缩放分辨率
     */
    public abstract double[] getRes();

    /**
     * 缩放级别
     */
    public abstract double[] getScale();

    /**
     * 地图最大范围
     */
    public abstract double[] getFullExtent();

    /**
     * 地图原点坐标
     */
    public abstract double[] getOrignPoint();

    /**
     * 获取Wkid
     */
    public abstract int getWkid();

    /**
     * 获取图片加载地址
     */
    public abstract String getUrl(int level, int col, int row, BaseTiledType tiledType);

    /**
     * 获取矢量地图底图
     */
    public abstract BaseTiledLayer[] getVecBaseTileLayer();

    /**
     * 获取影像地图底图
     */
    public abstract BaseTiledLayer[] getImgBaseTileLayer();
}
