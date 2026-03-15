package com.qc.common.ui.fragment;

import android.view.View;

import com.qc.common.self.CommonData;
import com.qc.mycomic.R;

import java.util.ArrayList;

import the.one.base.ui.fragment.BaseFragment;
import the.one.base.ui.fragment.BaseHomeFragment;

/**
 * @author LuQiChuang
 * @desc HOME界面
 * @date 2020/8/12 15:26
 * @ver 1.0
 */
public class MyHomeFragment extends BaseHomeFragment {

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        // 基类对根 Fragment 依赖 onEnterAnimationEnd() 触发 onLazyInit() → startInit()，
        // 但该回调可能不触发导致白屏。这里用 post 兜底，mIsFirstLayInit 保证只执行一次。
        rootView.post(() -> {
            if (mIsFirstLayInit) {
                mIsFirstLayInit = false;
                onLazyInit();
            }
        });
    }

    @Override
    protected boolean isExitFragment() {
        return true;
    }

    @Override
    protected boolean isNeedChangeStatusBarMode() {
        return true;
    }

    @Override
    protected boolean isViewPagerSwipe() {
        return false;
    }

    @Override
    protected boolean isDestroyItem() {
        return false;
    }

    @Override
    protected void addTabs() {
        String[] tabBars = CommonData.getTabBars();
        if (tabBars == null || tabBars.length < 3) {
            tabBars = new String[]{"主页", "搜索", "个人"};
        }
        addTab(R.drawable.ic_baseline_home_24, R.drawable.ic_baseline_home_select_24, tabBars[0]);
        addTab(R.drawable.ic_baseline_search_24, R.drawable.ic_baseline_search_select_24, tabBars[1]);
        addTab(R.drawable.ic_baseline_person_24, R.drawable.ic_baseline_person_select_24, tabBars[2]);
    }

    @Override
    protected void addFragment(ArrayList<BaseFragment> fragments) {
        fragments.add(new ShelfFragment());
        fragments.add(new SearchBaseFragment());
        fragments.add(new PersonFragment());
    }

}
