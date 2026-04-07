package com.qc.common.ui.adapter;

import com.qc.mycomic.R;

import org.jetbrains.annotations.NotNull;

import the.one.base.adapter.TheBaseQuickAdapter;
import the.one.base.adapter.TheBaseViewHolder;
import top.luqichuang.mycomic.model.Comic;

public class OfflineComicAdapter extends TheBaseQuickAdapter<Comic> {

    public OfflineComicAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull TheBaseViewHolder holder, Comic comic) {
        int n = comic.getInfo().getChapterInfoList().size();
        holder.setText(R.id.tvTitle, comic.getTitle());
        holder.setText(R.id.tvSubtitle, "共 " + n + " 话 · 本机离线");
    }
}
