package com.dewarder.pickerkit.panel;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.annimon.stream.Stream;
import com.dewarder.pickerkit.HashSetPickerDataController;
import com.dewarder.pickerkit.OnPickerItemCheckListener;
import com.dewarder.pickerkit.OnPickerItemClickListener;
import com.dewarder.pickerkit.PickerAdapter;
import com.dewarder.pickerkit.PickerData;
import com.dewarder.pickerkit.PickerDataPreviewFetcher;
import com.dewarder.pickerkit.PreviewFetcher;
import com.dewarder.pickerkit.R;
import com.dewarder.pickerkit.SpaceItemDecoration;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class AttachmentPanelView extends LinearLayout {

    private PreviewFetcher<PickerData> mPreviewFetcher;
    private PickerAdapter.Controller<PickerData> mPickedController;

    private RecyclerView mPickerRecycler;
    private LinearLayoutManager mPickerLayoutManager;
    private PickerAdapter<PickerData> mPickerAdapter;

    private RecyclerView mCategoryRecycler;
    private GridLayoutManager mCategoryLayoutManager;
    private AttachmentPanelCategoryAdapter mCategoryAdapter;

    private int mPickerSpacing;
    private int mCategoryMinWidth;

    private int mCategorySpanCount = -1;
    private int mLimit;
    private boolean mInitialized;

    public AttachmentPanelView(Context context) {
        super(context);
        init(context);
    }

    public AttachmentPanelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AttachmentPanelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AttachmentPanelView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        View view = inflate(context, R.layout.content_attachment_panel, this);
        setBackgroundColor(Color.WHITE);
        setOrientation(VERTICAL);

        mCategoryMinWidth = getResources().getDimensionPixelSize(R.dimen.item_attachment_panel_category_min_width);
        mPickerSpacing = getResources().getDimensionPixelSize(R.dimen.attachment_panel_picker_spacing);

        mPreviewFetcher = new PickerDataPreviewFetcher(context);
        mPickedController = new HashSetPickerDataController();

        mPickerRecycler = (RecyclerView) view.findViewById(R.id.attachment_panel_picker_recycler);
        mPickerLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mPickerRecycler.setLayoutManager(mPickerLayoutManager);
        mPickerAdapter = new PickerAdapter<>(mPreviewFetcher);
        mPickerAdapter.setPickerController(mPickedController);
        mPickerRecycler.setAdapter(mPickerAdapter);
        mPickerRecycler.addItemDecoration(SpaceItemDecoration.horizontalAll(mPickerSpacing));

        mCategoryRecycler = (RecyclerView) view.findViewById(R.id.attachment_panel_category_recycler);
        mCategoryRecycler.setItemAnimator(null);
        mCategoryAdapter = new AttachmentPanelCategoryAdapter();
        mCategoryRecycler.setAdapter(mCategoryAdapter);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!mInitialized) {
            int spanCount = mCategorySpanCount != -1 ? mCategorySpanCount : getWidth() / mCategoryMinWidth;
            mCategoryLayoutManager = new GridLayoutManager(getContext(), spanCount);
            mCategoryRecycler.setLayoutManager(mCategoryLayoutManager);
            mPickerAdapter.setCategoryItemSize(mPickerRecycler.getHeight() - mPickerSpacing * 2);
            mInitialized = true;
        }
    }

    public void setCategories(List<AttachmentPanelCategory> categories) {
        mCategoryAdapter.setCategories(categories);
    }

    public void addCategory(@NonNull AttachmentPanelCategory category) {
        mCategoryAdapter.add(category);
    }

    public void addCategories(@NonNull AttachmentPanelCategory... categories) {
        mCategoryAdapter.addAll(Arrays.asList(categories));
    }

    public void replaceCategory(@IdRes int oldCategoryId, @NonNull AttachmentPanelCategory newCategory) {
        mCategoryAdapter.replace(oldCategoryId, newCategory);
    }

    public void removeCategory(@IdRes int categoryId) {
        mCategoryAdapter.remove(categoryId);
    }

    public void setData(@NonNull List<PickerData> data) {
        mPickerAdapter.setData(data);
    }

    public int getCategorySpanCount() {
        return mCategorySpanCount;
    }

    public void setCategorySpanCount(int spanCount) {
        mCategorySpanCount = spanCount;
    }

    public void clearPicked() {
        mPickedController.clearPicked();
    }

    public List<File> getPicked() {
        return Stream.of(mPickedController.getPicked()).map(PickerData::getFile).toList();
    }

    public void setOnAttachmentPanelCategoryClickListener(@Nullable OnAttachmentPanelCategoryClickListener listener) {
        mCategoryAdapter.setOnAttachmentPanelCategoryClickListener(listener);
    }

    public void setOnPickerItemClickListener(OnPickerItemClickListener<PickerData> listener) {
        mPickerAdapter.setOnPickerItemClickListener(listener);
    }

    public void setOnPickerItemCheckListener(OnPickerItemCheckListener<PickerData> listener) {
        mPickerAdapter.setOnPickerItemCheckListener(listener);
    }

    public void setPickerController(PickerAdapter.Controller<PickerData> controller) {
        mPickedController = controller;
        mPickerAdapter.setPickerController(controller);
    }

    public void setLimit(int limit) {
        mLimit = limit;
        mPickerAdapter.setPickEnabled(mLimit > 0 && mLimit - mPickedController.getPicked().size() > 0);
    }

    public int getLimit() {
        return mLimit;
    }

    public void setPickEnabled(boolean enabled) {
        mPickerAdapter.setPickEnabled(enabled);
    }
}
