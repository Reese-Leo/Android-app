package com.qc.common.ui.fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.qc.common.constant.Constant;
import com.qc.common.constant.TmpData;
import com.qc.common.self.CommonData;
import com.qc.mycomic.R;
import com.qmuiteam.qmui.alpha.QMUIAlphaImageButton;
import com.qmuiteam.qmui.qqface.QMUIQQFaceView;
import com.qmuiteam.qmui.util.QMUIColorHelper;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;

import java.util.ArrayList;

import the.one.base.ui.fragment.BaseFragment;
import the.one.base.ui.fragment.BaseTitleTabFragment;
import the.one.base.util.QMUIPopupUtil;

/**
 * @author LuQiChuang
 * @desc
 * @date 2021/6/9 16:26
 * @ver 1.0
 */
public class ShelfFragment extends BaseTitleTabFragment {

    private static final String TAG = "ShelfFragment";
    private QMUIQQFaceView mTitle;
    private QMUIAlphaImageButton mSettingIcon;
    private QMUIPopup mSettingPopup;

    @Override
    protected boolean isAdjustMode() {
        return true;
    }

    @Override
    protected boolean showElevation() {
        return true;
    }

    @Override
    protected boolean isFoldTitleBar() {
        return true;
    }

    @Override
    protected void onScrollChanged(float percent) {
        if (mTitle != null) {
            mTitle.setTextColor(QMUIColorHelper.setColorAlpha(getColorr(R.color.qmui_config_color_gray_1), percent));
        }
        if (mSettingIcon != null) {
            mSettingIcon.setAlpha(percent);
        }
    }

    @Override
    protected void initView(View rootView) {
        try {
            super.initView(rootView);
            String titleStr = "我的收藏";
            try {
                String[] tabs = CommonData.getTabBars();
                if (tabs != null && tabs.length > 0) {
                    titleStr = tabs[0];
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting title from CommonData", e);
            }
            
            mTitle = mTopLayout.setTitle(titleStr);
            mTopLayout.setNeedChangedWithTheme(false);
            mTopLayout.setTitleGravity(Gravity.CENTER);
            mTitle.setTextColor(getColorr(R.color.qmui_config_color_gray_1));
            mTitle.getPaint().setFakeBoldText(true);

            mSettingIcon = mTopLayout.addRightImageButton(R.drawable.ic_baseline_menu_24, R.id.topbar_right_button1);
            mSettingIcon.setOnClickListener(v -> {
                showSettingPopup();
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in initView", e);
        }
    }

    private final String[] menus = {
            "检查更新",
            "筛选" + TmpData.content,
            "取消筛选",
            "导入" + TmpData.content,
    };

    private void showSettingPopup() {
        if (null == mSettingPopup) {
            mSettingPopup = QMUIPopupUtil.createListPop(_mActivity, menus, (adapter, view, position) -> {
                ShelfItemFragment fragment = (ShelfItemFragment) fragments.get(INDEX);
                if (position == 0) {
                    fragment.startCheckUpdate();
                } else if (position == 1) {
                    fragment.screen(true);
                } else if (position == 2) {
                    fragment.screen(false);
                } else if (position == 3) {
                    fragment.importMH();
                } else if (position == 4) {
                }
                mSettingPopup.dismiss();
            });
        }
        mSettingPopup.show(mSettingIcon);
    }

    @Override
    protected void addTabs() {
        addTab("收藏" + TmpData.content);
        addTab("历史" + TmpData.content);
    }

    @Override
    protected void addFragment(ArrayList<BaseFragment> fragments) {
        fragments.add(ShelfItemFragment.getInstance(Constant.STATUS_FAV));
        fragments.add(ShelfItemFragment.getInstance(Constant.STATUS_HIS));
    }

}
