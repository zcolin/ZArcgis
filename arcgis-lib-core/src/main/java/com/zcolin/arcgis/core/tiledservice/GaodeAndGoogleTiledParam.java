/*
 * *********************************************************
 *   author   colin
 *   email    wanglin2046@126.com
 *   date     19-1-23 下午2:48
 * ********************************************************
 */

package com.zcolin.arcgis.core.tiledservice;

import com.zcolin.arcgis.core.GisMapConfig;

import java.util.Random;

public class GaodeAndGoogleTiledParam extends BaseTiledParam {
    private static final double[] SCALE = {295828763.795777, 147914381.897889, 73957190.948944, 36978595.474472, 18489297.737236, 9244648.868618, 4622324.434309, 2311162.217155, 1155581.108577, 
            577790.554289, 288895.277144, 144447.638572, 72223.819286, 36111.9096437, 18055.9548224, 9027.977411, 4513.988705, 2256.994353, 1128.497176};
    private static final double[] RES   = {78271.5169639999, 39135.7584820001, 19567.8792409999, 9783.93962049996, 4891.96981024998, 2445.98490512499, 1222.99245256249, 611.49622628138, 
            305.748113140558, 152.874056570411, 76.4370282850732, 38.2185141425366, 19.1092570712683, 9.55462853563415, 4.77731426794937, 2.38865713397468, 1.19432856685505, 0.597164283559817, 
            0.298582141647617};

    private static final double[] ORIGN_POINT = {-20037508.3427892, 20037508.3427892};
    private static final double[] FULL_EXTENT = {-20037508.3427892, -20037508.3427892, 20037508.3427892, 20037508.3427892};
    private static final int      WKID        = 3857;

    public GaodeAndGoogleTiledParam(GisMapConfig gisMapConfig) {
        super(gisMapConfig);
    }

    @Override
    public double[] getRes() {
        return RES;
    }

    @Override
    public double[] getScale() {
        return SCALE;
    }

    @Override
    public double getInitScale() {
        return gisMapConfig.getInitScale() == 0 ? 200000 : gisMapConfig.getInitScale();
    }

    @Override
    public double[] getCenterPoint() {
        return gisMapConfig.getCenterPoint() == null ? new double[]{12803520.041638518, 4273976.43656453} : gisMapConfig.getCenterPoint();
    }

    @Override
    public double[] getFullExtent() {
        return gisMapConfig.getFullExtent() == null ? FULL_EXTENT : gisMapConfig.getFullExtent();
    }

    @Override
    public double[] getOrignPoint() {
        return ORIGN_POINT;
    }

    @Override
    public int getWkid() {
        return WKID;
    }

    @Override
    public String getUrl(int level, int col, int row, BaseTiledType tiledType) {
        StringBuilder url = new StringBuilder();
        Random random = new Random();
        int subdomain = (random.nextInt(4));
        switch (tiledType) {
            case VEC_C:
                url = url.append("http://wprd0")
                         .append(subdomain + 1)
                         .append(".is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=7&x=")
                         .append(col)
                         .append("&y=")
                         .append(row)
                         .append("&z=")
                         .append(level);
                break;
            case IMG_C:
                url = url.append("http://mt")
                         .append(subdomain)
                         .append(".google.cn/vt/lyrs=s@126&hl=zh-CN&gl=CN&src=app&x=")
                         .append(col)
                         .append("&y=")
                         .append(row)
                         .append("&z=")
                         .append(level)
                         .append("&s==Galil");
                break;
            case CVA_C:
                break;
            case CIA_C:
                url = url.append("http://mt.google.cn/vt/imgtp=png32&lyrs=h@212000000&hl=zh-CN&gl=CN&src=app&x=").append(col).append("&y=").append(row).append("&z=").append(level).append("&s==Galil");
                break;
            default:
                return null;
        }
        return url.toString();
    }

    @Override
    public BaseTiledLayer[] getVecBaseTileLayer() {
        BaseTiledLayer baseLayer[] = new BaseTiledLayer[1];
        baseLayer[0] = BaseTiledLayer.createLayer(this, BaseTiledType.VEC_C);
        return baseLayer;
    }

    @Override
    public BaseTiledLayer[] getImgBaseTileLayer() {
        BaseTiledLayer baseLayer[] = new BaseTiledLayer[2];
        baseLayer[0] = BaseTiledLayer.createLayer(this, BaseTiledType.IMG_C);
        baseLayer[1] = BaseTiledLayer.createLayer(this, BaseTiledType.CIA_C);
        return baseLayer;
    }
}
