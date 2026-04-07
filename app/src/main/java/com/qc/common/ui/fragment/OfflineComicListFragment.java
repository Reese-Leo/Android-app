package com.qc.common.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qc.common.ui.adapter.OfflineComicAdapter;
import com.qc.common.util.OfflineChapterScanner;
import com.qc.mycomic.R;

import java.util.List;

import the.one.base.ui.fragment.BaseDataFragment;
import the.one.base.ui.presenter.BasePresenter;
import top.luqichuang.mycomic.model.Comic;

/**
 * 浏览并打开本机 OfflineChapter 中已下载的漫画。
 */
public class OfflineComicListFragment extends BaseDataFragment<Comic> {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        adapter = new OfflineComicAdapter(R.layout.item_offline_comic);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        addTopBarBackBtn();
        mTopLayout.setTitle("离线漫画");
        // 1) 与 MyHomeFragment 相同：push 进入时 onEnterAnimationEnd 可能不触发，需 post 里手动 onLazyInit()，
        //    否则 startInit() 未执行，RecyclerView 未就绪，onFirstComplete 无法结束 loading。
        // 2) 基类在 getPresenter()==null 时可能不会自动 requestServer，这里 onLazyInit 之后再拉一次数据。
        rootView.post(() -> {
            if (!isAdded()) {
                return;
            }
            if (mIsFirstLayInit) {
                mIsFirstLayInit = false;
                onLazyInit();
            }
            requestServer();
        });
    }

    @Override
    protected BaseQuickAdapter getAdapter() {
        return adapter;
    }

    @Override
    protected void requestServer() {
        // 与 SearchResultFragment 一致：先切到内容区，否则可能一直盖在全屏 loading 下
        showContentPage();
        List<Comic> list;
        try {
            list = OfflineChapterScanner.scan(getContext());
        } catch (Throwable t) {
            String msg = t.getMessage() != null ? t.getMessage() : "扫描本机目录失败";
            showErrorPage(msg, v -> requestServer());
            return;
        }
        if (list.isEmpty()) {
            showEmptyPage("暂无离线漫画\n请先在阅读器中「下载本话」");
        } else {
            onFirstComplete(list);
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
        Comic comic = (Comic) adapter.getItem(position);
        if (comic != null) {
            startFragment(ComicReaderFragment.getInstance(comic));
        }
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    @Override
    public boolean onItemLongClick(@NonNull BaseQuickAdapter baseQuickAdapter, @NonNull View view, int i) {
        return false;
    }
}
