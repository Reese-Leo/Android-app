package com.qc.common.ui.adapter;

import android.content.res.ColorStateList;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qc.mycomic.R;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundLinearLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Random;

import the.one.base.adapter.TheBaseQuickAdapter;
import the.one.base.adapter.TheBaseViewHolder;
import top.luqichuang.common.model.ChapterInfo;
import top.luqichuang.common.model.Entity;

/**
 * @author LuQiChuang
 * @desc
 * @date 2021/6/10 17:50
 * @ver 1.0
 */
public class ChapterAdapter extends TheBaseQuickAdapter<ChapterInfo> {

    private final Entity entity;

    private int chapterId;

    public ChapterAdapter(Entity entity) {
        super(R.layout.item_chapter);
        this.entity = entity;
        this.chapterId = entity.getInfo().getCurChapterId();
    }

    private Random random = new Random();

    @Override
    protected void convert(@NotNull TheBaseViewHolder holder, ChapterInfo chapterInfo) {
        holder.setText(R.id.tvTitle, chapterInfo.getTitle());
        TextView tvTitle = holder.findView(R.id.tvTitle);
        if (random.nextBoolean()) {
            QMUIRoundLinearLayout linearLayout = holder.findView(R.id.linearLayout);
            ViewGroup.LayoutParams lp = linearLayout.getLayoutParams();
            lp.width = QMUIDisplayHelper.getScreenWidth(getContext());
        }
        if (Objects.equals(chapterInfo.getTitle(), entity.getInfo().getCurChapterTitle())) {
            tvTitle.setTextColor(getColor(R.color.white));
            QMUIRoundLinearLayout linearLayout = holder.findView(R.id.linearLayout);
            QMUIRoundButtonDrawable drawable = (QMUIRoundButtonDrawable) linearLayout.getBackground();
            ColorStateList colorStateList = ColorStateList.valueOf(getColor(R.color.colorPrimary));
            drawable.setBgData(colorStateList);
            chapterId = chapterInfo.getId();
        } else {
            tvTitle.setTextColor(getColor(R.color.black));
            QMUIRoundLinearLayout linearLayout = holder.findView(R.id.linearLayout);
            QMUIRoundButtonDrawable drawable = (QMUIRoundButtonDrawable) linearLayout.getBackground();
            ColorStateList colorStateList = ColorStateList.valueOf(getColor(R.color.white));
            drawable.setBgData(colorStateList);
        }
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public int getChapterId() {
        return chapterId;
    }

    private static final int NORMAL = 0;
    private static final int NEW_LINE = 1;

    @Override
    protected int getDefItemViewType(int position) {
        return getData().get(position).getStatus();
    }

//    @NotNull
//    @Override
//    protected TheBaseViewHolder onCreateDefViewHolder(@NotNull ViewGroup parent, int viewType) {
//        if (viewType == NEW_LINE) {
//            return super.createBaseViewHolder(parent, R.layout.item_rank_right_simple);
//        } else {
//            return super.onCreateDefViewHolder(parent, viewType);
//        }
//    }

}
