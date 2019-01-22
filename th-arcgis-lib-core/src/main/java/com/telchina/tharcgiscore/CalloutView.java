package com.telchina.tharcgiscore;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.telchina.tharcgiscore.adapter.BufferDistanceAdapter;
import com.zcolin.gui.ZDialog;

import java.util.Arrays;
import java.util.List;

/**
 * ArcGis Callout 展示内容
 */

public class CalloutView extends ConstraintLayout {

    private int iconSearch = 0;
    private int iconDetail = 0;

    private int backgroundColor        = -1;
    private int textPrimaryStyle       = -1;
    private int textSubPrimaryStyle    = -1;
    private int dividerColor           = -1;
    private int recyclerViewBackground = -1;
    private int buttonBackground       = -1;
    private int buttonTextStyle        = -1;

    private LinearLayout llTitle;
    private TextView     tvTitle;
    private ImageView    ivClose;
    private View         dividerTop;
    private LinearLayout llContent;
    private View         dividerBottom;
    private TextView     tvBuffer;
    private TextView     spinnerBuffer;
    private RecyclerView recyclerView;
    private TextView     tvSearchAround;
    private TextView     tvDetail;

    private ZDialog.ZDialogParamSubmitListener<String> onSearchListener;


    public CalloutView(Context context) {
        this(context, null);
    }

    public CalloutView(Context context, AttributeSet attrs) {
        this(context, null, 0);
    }

    public CalloutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.th_arcgis_callout_view, this);
        llTitle = findViewById(R.id.ll_title);
        tvTitle = findViewById(R.id.tv_title);
        ivClose = findViewById(R.id.iv_close);
        dividerTop = findViewById(R.id.divider_top);
        llContent = findViewById(R.id.ll_content);
        dividerBottom = findViewById(R.id.divider_bottom);
        tvBuffer = findViewById(R.id.tv_buffer);
        spinnerBuffer = findViewById(R.id.spinner_buffer);
        recyclerView = findViewById(R.id.rv_buffer);
        tvSearchAround = findViewById(R.id.tv_search_around);
        tvDetail = findViewById(R.id.tv_detail);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CalloutView, defStyle, 0);
        int iconClose = a.getResourceId(R.styleable.CalloutView_calloutIconClose, R.drawable.ic_gismap_callout_close);
        int iconSearch = a.getResourceId(R.styleable.CalloutView_calloutIconSearch, R.drawable.ic_gismap_search_around);
        int iconDetail = a.getResourceId(R.styleable.CalloutView_calloutIconDetail, 0);

        backgroundColor = a.getInt(R.styleable.CalloutView_calloutBackgroundColor, R.color.calloutBackgroundColor);
        textPrimaryStyle = a.getResourceId(R.styleable.CalloutView_calloutTextPrimaryStyle, R.style.Callout_TextStyle_Primary_Small);
        textSubPrimaryStyle = a.getResourceId(R.styleable.CalloutView_calloutTextSubPrimaryStyle, R.style.Callout_TextStyle_SubPrimary_Small);
        dividerColor = a.getInt(R.styleable.CalloutView_calloutDividerColor, R.color.calloutDividerColor);
        recyclerViewBackground = a.getResourceId(R.styleable.CalloutView_calloutRecyclerViewBackground, 0);
        buttonBackground = a.getResourceId(R.styleable.CalloutView_calloutButtonBackground, 0);
        buttonTextStyle = a.getResourceId(R.styleable.CalloutView_calloutButtonTextStyle, 0);
        a.recycle();

        setBackgroundColor(backgroundColor);

        tvTitle.setTextAppearance(getContext(), textPrimaryStyle);
        spinnerBuffer.setTextAppearance(getContext(), textSubPrimaryStyle);
        tvBuffer.setTextAppearance(getContext(), textSubPrimaryStyle);
        dividerTop.setBackgroundColor(dividerColor);
        dividerBottom.setBackgroundColor(dividerColor);
        spinnerBuffer.setBackgroundResource(recyclerViewBackground);
        recyclerView.setBackgroundResource(recyclerViewBackground);
        tvSearchAround.setBackgroundResource(buttonBackground);
        tvDetail.setBackgroundResource(buttonBackground);
        tvSearchAround.setTextAppearance(getContext(), buttonTextStyle);
        tvDetail.setTextAppearance(getContext(), buttonTextStyle);

        ivClose.setImageResource(iconClose);
        tvSearchAround.setCompoundDrawablesWithIntrinsicBounds(iconSearch, 0, 0, 0);
        tvDetail.setCompoundDrawablesWithIntrinsicBounds(iconDetail, 0, 0, 0);
    }

    public void setOnCloseListener(OnClickListener onCloseListener) {
        if (onCloseListener != null) {
            ivClose.setOnClickListener(onCloseListener);
        }
    }

    public void setOnDetailListener(OnClickListener onDetailListener) {
        if (onDetailListener != null) {
            tvDetail.setOnClickListener(onDetailListener);
        } else {
            tvDetail.setVisibility(GONE);
        }
    }

    public void setOnSearchListener(ZDialog.ZDialogParamSubmitListener<String> onSearchListener) {
        this.onSearchListener = onSearchListener;
        if (onSearchListener == null) {
            spinnerBuffer.setVisibility(View.GONE);
            tvSearchAround.setVisibility(View.GONE);
        } else {
            spinnerBuffer.setVisibility(View.VISIBLE);
            tvSearchAround.setVisibility(View.VISIBLE);
        }
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setContentVisible(int visible) {
        llContent.setVisibility(visible);
    }

    public void setDividerTopVisible(int visible) {
        dividerTop.setVisibility(visible);
    }

    public void setDividerBottomVisible(int visible) {
        dividerBottom.setVisibility(visible);
    }

    public void setBufferVisible(int visible) {
        tvBuffer.setVisibility(visible);
        spinnerBuffer.setVisibility(visible);
    }

    public void setBtnSearchAroundVisible(int visible) {
        tvSearchAround.setVisibility(visible);
    }

    public void setBtnDetailVisible(int visible) {
        tvDetail.setVisibility(visible);
    }

    public LinearLayout getTitleLayout() {
        return llTitle;
    }

    public View getDividerTopView() {
        return dividerTop;
    }

    public LinearLayout getContentLayout() {
        return llContent;
    }

    public View getDividerBottomView() {
        return dividerBottom;
    }

    public void initRecyclerBuffer(List<String> distanceList) {
        if (onSearchListener != null) {
            spinnerBuffer.setVisibility(View.VISIBLE);
            tvSearchAround.setVisibility(View.VISIBLE);
            spinnerBuffer.setOnClickListener(v -> recyclerView.setVisibility(View.VISIBLE));
            BufferDistanceAdapter bufferDistanceAdapter = new BufferDistanceAdapter();
            if (distanceList == null || distanceList.size() <= 0) {
                distanceList = Arrays.asList(getResources().getStringArray(R.array.search_around_radius));
            }
            bufferDistanceAdapter.setDatas(distanceList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(bufferDistanceAdapter);
            bufferDistanceAdapter.setOnItemClickListener((covertView, position, data) -> {
                spinnerBuffer.setText(data);
                recyclerView.setVisibility(View.GONE);
            });
            setOnClickListener(v -> recyclerView.setVisibility(View.GONE));
        } else {
            spinnerBuffer.setVisibility(View.GONE);
            tvSearchAround.setVisibility(View.GONE);
        }

        tvSearchAround.setOnClickListener(view -> {
            if (onSearchListener != null) {
                onSearchListener.submit(spinnerBuffer.getText().toString().trim());
            }
        });
    }
}
