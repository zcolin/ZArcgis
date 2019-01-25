/*
 * *********************************************************
 *   author   colin
 *   company  telchina
 *   email    wanglin2046@126.com
 *   date     19-1-23 下午2:48
 * ********************************************************
 */

package com.telchina.tharcgiscore.tiledservice;

import com.esri.arcgisruntime.arcgisservices.LevelOfDetail;
import com.esri.arcgisruntime.arcgisservices.TileInfo;
import com.esri.arcgisruntime.data.TileKey;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.layers.ImageTiledLayer;
import com.zcolin.frame.util.FileUtil;
import com.zcolin.frame.util.LogUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * 天地图图层服务类
 */
public class BaseTiledLayer extends ImageTiledLayer {
    private BaseTiledType  tiledType;
    private BaseTiledParam tileParam;

    public static BaseTiledLayer createLayer(BaseTiledParam tileParam, BaseTiledType tiledType) {
        List<LevelOfDetail> levelOfDetails = new ArrayList<>();
        for (int i = 0; i < tileParam.getScale().length; i++) {
            LevelOfDetail detail = new LevelOfDetail(i + 1, tileParam.getRes()[i], tileParam.getScale()[i]);
            levelOfDetails.add(detail);
        }

        Point originalPoint = new Point(tileParam.getOrignPoint()[0], tileParam.getOrignPoint()[1], SpatialReference.create(tileParam.getWkid()));
        TileInfo tileInfo = new TileInfo(96, TileInfo.ImageFormat.PNG, levelOfDetails, originalPoint, SpatialReference.create(tileParam.getWkid()), 256, 256);
        Envelope fullExtent = new Envelope(tileParam.getFullExtent()[0], tileParam.getFullExtent()[1], tileParam.getFullExtent()[2], tileParam.getFullExtent()[3], SpatialReference.create(tileParam
                .getWkid()));
        return new BaseTiledLayer(tileParam, tiledType, tileInfo, fullExtent);
    }

    public BaseTiledLayer(BaseTiledParam tileParam, BaseTiledType tiledType, TileInfo tileInfo, Envelope fullExtent) {
        super(tileInfo, fullExtent);
        this.tileParam = tileParam;
        this.tiledType = tiledType;
    }

    @Override
    protected byte[] getTile(TileKey tileKey) {
        byte[] result = null;
        if (tileParam.isCacheEnabled()) {
            result = getOfflineCacheFile(tileKey.getLevel(), tileKey.getColumn(), tileKey.getRow(), tiledType);  // 看本地是否有
        }

        // 从网络获取
        if (result == null) {
            ByteArrayOutputStream bos = null;
            BufferedInputStream bis = null;
            HttpURLConnection httpURLConnection = null;
            try {
                bos = new ByteArrayOutputStream();
                byte[] byteBuffer = new byte[1024];

                URL url = new URL(tileParam.getUrl(tileKey.getLevel(), tileKey.getColumn(), tileKey.getRow(), tiledType));
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();
                bis = new BufferedInputStream(httpURLConnection.getInputStream());

                int bytesRead;
                while ((bytesRead = bis.read(byteBuffer)) > 0) {
                    bos.write(byteBuffer, 0, bytesRead);
                }
                result = bos.toByteArray();

                if (tileParam.isCacheEnabled()) {
                    addOfflineCacheFile(tileKey.getLevel(), tileKey.getColumn(), tileKey.getRow(), tiledType, result);
                }
            } catch (Exception ex) {
                LogUtil.w("TianDiTuTiledServiceLayer.getTile", ex.getMessage());
                ex.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }

    public BaseTiledParam getTileParam() {
        return this.tileParam;
    }

    public BaseTiledType getTiledType() {
        return this.tiledType;
    }

    public String getCachePath() {
        return this.tileParam.getCachePath();
    }

    /**
     * 将图片保存到本地 目录结构可以随便定义 只要你找得到对应的图片
     */
    private void addOfflineCacheFile(int level, int col, int row, BaseTiledType mapType, byte[] bytes) {
        File rowfile = new File(getCachePath() + level + File.separator + col + File.separator + row + File.separator + mapType + ".dat");
        FileUtil.checkFilePath(rowfile, false);
        if (!rowfile.exists()) {
            FileOutputStream out = null;
            try {
                rowfile.createNewFile();
                out = new FileOutputStream(rowfile);
                out.write(bytes);
            } catch (Exception e) {
                e.printStackTrace();
                rowfile.delete();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    /**
     * 从本地获取图片
     */
    private byte[] getOfflineCacheFile(int level, int col, int row, BaseTiledType maptype) {
        File rowfile = new File(getCachePath() + level + File.separator + col + File.separator + row + File.separator + maptype + ".dat");
        if (rowfile.exists()) {
            FileInputStream in = null;
            try {
                in = new FileInputStream(rowfile);
                ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
                byte[] temp = new byte[1024];
                int size = 0;
                while ((size = in.read(temp)) != -1) {
                    out.write(temp, 0, size);
                }
                return out.toByteArray();
            } catch (IOException e) {
                rowfile.delete();
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
