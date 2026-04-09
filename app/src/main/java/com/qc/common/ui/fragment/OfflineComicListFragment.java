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

public class OfflineComicListFragment extends BaseDataFragment<Comic> {

    private boolean dataLoaded;

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
    }

    @Override
    protected void onLazyInit() {
        loadOfflineComics();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!dataLoaded && recycleView != null) {
            loadOfflineComics();
        }
    }

    @Override
    protected BaseQuickAdapter getAdapter() {
        return adapter;
    }

    @Override
    protected void requestServer() {
        loadOfflineComics();
    }

    private void loadOfflineComics() {
        if (dataLoaded) {
            return;
        }
        showContentPage();
        List<Comic> list;
        try {
            list = OfflineChapterScanner.scan(getContext());
        } catch (Throwable t) {
            String msg = t.getMessage() != null ? t.getMessage() : "扫描本机目录失败";
            showErrorPage(msg, v -> {
                dataLoaded = false;
                loadOfflineComics();
            });
            return;
        }
        dataLoaded = true;
        if (list.isEmpty()) {
            showEmptyPage("暂无离线漫画\n请先在阅读器中「下载本话」");
        } else {
            onFirstComplete(list);
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
