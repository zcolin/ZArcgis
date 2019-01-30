/*
 * *********************************************************
 *   author   colin
 *   company  telchina
 *   email    wanglin2046@126.com
 *   date     19-1-24 上午11:16
 * ********************************************************
 */

package com.telchina.arcgis.core;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.amap.api.location.AMapLocation;
import com.zcolin.frame.util.DisplayUtil;
import com.zcolin.libamaplocation.LocationUtil;


/**
 * 工具条封装
 */
public class GisMenuBar extends LinearLayout {
    private ImageView      ivReset;
    private ImageView      ivClear;
    private ImageView      ivLocation;
    private ImageView      ivMapType;
    private Drawable       mapTypeItemBackground;
    private ColorStateList mapTypeItemTextColor;
    private int            mapTypeAnim;
    private int            mapTypeGravity;
    private ProgressBar    progressBar;

    public GisMenuBar(Context context) {
        this(context, null);
    }

    public GisMenuBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GisMenuBar init(GisMapOperateView gisMapOperateView, int orientation) {
        LayoutInflater.from(getContext()).inflate(orientation == HORIZONTAL ? R.layout.zarcgis_menubar_hor : R.layout.zarcgis_menubar_ver, this);
        setOrientation(orientation);
        ivReset = findViewById(R.id.iv_reset);
        ivClear = findViewById(R.id.iv_clear);
        ivLocation = findViewById(R.id.iv_lcoation);
        ivMapType = findViewById(R.id.iv_maptype);
        progressBar = findViewById(R.id.progressbar_gps);

        ivReset.setOnClickListener(v -> gisMapOperateView.reset());
        ivClear.setOnClickListener(v -> gisMapOperateView.clear());
        ivLocation.setOnClickListener(v -> {
            progressBar.setVisibility(VISIBLE);
            gisMapOperateView.location(new LocationUtil.OnGetLocation() {
                @Override
                public void getLocation(AMapLocation aMapLocation) {
                    progressBar.setVisibility(GONE);
                }

                @Override
                public void locationFail() {
                    progressBar.setVisibility(GONE);
                }
            });
        });
        ivMapType.setOnClickListener(view -> {
            if (gisMapOperateView.getGisMapView().getGisMapConfig() != null) {
                GisMapTypeDialog gisMapTypeDialog = new GisMapTypeDialog(getContext(), gisMapOperateView.getGisMapView().getGisMapConfig().getBaseMapType(), t -> {
                    gisMapOperateView.switchBaseMap(t);
                    return false;
                });
                if (mapTypeItemBackground != null) {
                    gisMapTypeDialog.setItemBackground(mapTypeItemBackground);
                }
                if (mapTypeItemTextColor != null) {
                    gisMapTypeDialog.setItemTextColorStateList(mapTypeItemTextColor);
                }
                if (mapTypeAnim != 0) {
                    gisMapTypeDialog.setAnim(mapTypeAnim);
                }

                gisMapTypeDialog.setGravity(Gravity.LEFT | Gravity.TOP);//自己计算偏移
                gisMapTypeDialog.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                setWindowDeploy(gisMapTypeDialog);
                gisMapTypeDialog.show();
            }
        });
        return this;
    }

    private void setWindowDeploy(GisMapTypeDialog gisMapTypeDialog) {
        int[] ivMapTypeLocation = new int[2];
        ivMapType.getLocationInWindow(ivMapTypeLocation);
        int dialogOffset = DisplayUtil.dip2px(getContext(), 18);
        int[] dialogWidthHeight = gisMapTypeDialog.getWidthHeight();
        int dialogWidth = dialogWidthHeight[0];
        int dialogHeight = dialogWidthHeight[1];
        switch (mapTypeGravity) {
            case Gravity.TOP:
            case Gravity.LEFT:
            case Gravity.LEFT | Gravity.TOP:
                if (getOrientation() == VERTICAL) {
                    gisMapTypeDialog.setWindowDeploy(ivMapType.getWidth() + ivMapTypeLocation[0], ivMapTypeLocation[1] - dialogOffset);
                } else {
                    gisMapTypeDialog.setWindowDeploy(ivMapTypeLocation[0], ivMapTypeLocation[1] + ivMapType.getHeight() - dialogOffset);
                }
                break;
            case Gravity.RIGHT:
            case Gravity.RIGHT | Gravity.TOP:
                if (getOrientation() == VERTICAL) {
                    gisMapTypeDialog.setWindowDeploy(ivMapTypeLocation[0] - dialogWidth, ivMapTypeLocation[1] - dialogOffset);
                } else {
                    gisMapTypeDialog.setWindowDeploy(ivMapTypeLocation[0], ivMapTypeLocation[1] + ivMapType.getHeight() - dialogOffset);
                }
                break;
            case Gravity.BOTTOM:
            case Gravity.LEFT | Gravity.BOTTOM:
                if (getOrientation() == VERTICAL) {
                    gisMapTypeDialog.setWindowDeploy(ivMapType.getWidth() + ivMapTypeLocation[0], ivMapTypeLocation[1] - dialogOffset);
                } else {
                    gisMapTypeDialog.setWindowDeploy(ivMapTypeLocation[0], ivMapTypeLocation[1] - dialogHeight - dialogOffset - 5);
                }
                break;
            case Gravity.RIGHT | Gravity.BOTTOM:
                if (getOrientation() == VERTICAL) {
                    gisMapTypeDialog.setWindowDeploy(ivMapTypeLocation[0] - dialogWidth, ivMapTypeLocation[1] - dialogOffset);
                } else {
                    gisMapTypeDialog.setWindowDeploy(ivMapTypeLocation[0], ivMapTypeLocation[1] - dialogHeight - dialogOffset - 5);
                }
                break;
            default:
                break;
        }
    }

    public GisMenuBar setMenuBarPadding(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        return this;
    }

    public GisMenuBar setMapTypeItemBackground(Drawable mapTypeItemBackground) {
        if (mapTypeItemBackground != null) {
            this.mapTypeItemBackground = mapTypeItemBackground;
        }
        return this;
    }

    public GisMenuBar setMapTypeItemTextColor(ColorStateList mapTypeItemTextColor) {
        if (mapTypeItemTextColor != null) {
            this.mapTypeItemTextColor = mapTypeItemTextColor;
        }
        return this;
    }

    public GisMenuBar setMapTypeAnim(int mapTypeAnim) {
        if (mapTypeAnim != 0) {
            this.mapTypeAnim = mapTypeAnim;
        }
        return this;
    }

    public GisMenuBar setMapTypeGravity(int mapTypeGravity) {
        if (mapTypeGravity != 0) {
            this.mapTypeGravity = mapTypeGravity;
        }
        return this;
    }

    public GisMenuBar setLocationIcon(Drawable locationIcon) {
        if (locationIcon != null) {
            ivLocation.setImageDrawable(locationIcon);
        }
        return this;
    }

    public GisMenuBar setMapTypeIcon(Drawable mapTypeIcon) {
        if (mapTypeIcon != null) {
            ivMapType.setImageDrawable(mapTypeIcon);
        }
        return this;
    }

    public GisMenuBar setResetIcon(Drawable resetIcon) {
        if (resetIcon != null) {
            ivReset.setImageDrawable(resetIcon);
        }
        return this;
    }

    public GisMenuBar setClearIcon(Drawable clearIcon) {
        if (clearIcon != null) {
            ivClear.setImageDrawable(clearIcon);
        }
        return this;
    }
}
