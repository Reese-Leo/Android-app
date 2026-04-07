package com.qc.common.ui.presenter;

import android.content.Context;

import com.qc.common.ui.view.ReaderView;
import com.qc.common.util.EntityHelper;
import com.qc.common.util.OfflineChapterLocalReader;

import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import okhttp3.Request;
import the.one.base.ui.presenter.BasePresenter;
import top.luqichuang.common.en.SourceEnum;
import top.luqichuang.common.model.ChapterInfo;
import top.luqichuang.common.model.Content;
import top.luqichuang.common.model.Entity;
import top.luqichuang.common.model.Source;
import top.luqichuang.common.self.CommonCallback;
import top.luqichuang.common.util.NetUtil;

/**
 * @author LuQiChuang
 * @desc
 * @date 2021/6/10 22:30
 * @ver 1.0
 */
public class ReaderPresenter extends BasePresenter<ReaderView> {

    public void loadContentInfoList(Entity entity, Context context) {
        if (entity != null && entity.getSourceId() == SourceEnum.LOCAL_OFFLINE.ID) {
            loadLocalOfflineChapter(entity, context);
            return;
        }
        List<ChapterInfo> chapterInfoList = entity.getInfo().getChapterInfoList();
        int position = EntityHelper.getPosition(entity.getInfo());
        int chapterId = entity.getInfo().getCurChapterId();
        String url = chapterInfoList.get(position).getChapterUrl();
        Source source = EntityHelper.commonSource(entity);
        Request request = source.getContentRequest(url);
        NetUtil.startLoad(new CommonCallback(request, source, Source.CONTENT) {
            @Override
            protected void initData(Map<String, Object> data) {
                super.initData(data);
                data.put("chapterId", chapterId);
            }

            @Override
            public void onFailure(String errorMsg) {
                ReaderView view = getView();
                AndroidSchedulers.mainThread().scheduleDirect(() -> {
                    if (view != null) {
                        view.loadReadContentComplete(null, errorMsg);
                    }
                });
            }

            @Override
            public void onResponse(String html, Map<String, Object> map) {
                ReaderView view = getView();
                List<Content> list = EntityHelper.getContentList(entity, html, chapterId, map);
                if (list.isEmpty()) {
                    onFailure("解析失败！");
                } else {
                    AndroidSchedulers.mainThread().scheduleDirect(() -> {
                        if (view != null) {
                            view.loadReadContentComplete(list, null);
                        }
                    });
                }
            }
        });
    }

    private void loadLocalOfflineChapter(Entity entity, Context context) {
        ReaderView view = getView();
        if (view == null) {
            return;
        }
        if (context == null) {
            AndroidSchedulers.mainThread().scheduleDirect(() -> {
                ReaderView v = getView();
                if (v != null) {
                    v.loadReadContentComplete(null, "无法读取本地文件");
                }
            });
            return;
        }
        List<ChapterInfo> chapterInfoList = entity.getInfo().getChapterInfoList();
        int position = EntityHelper.getPosition(entity.getInfo());
        int chapterId = entity.getInfo().getCurChapterId();
        if (position < 0 || position >= chapterInfoList.size()) {
            AndroidSchedulers.mainThread().scheduleDirect(() -> {
                ReaderView v = getView();
                if (v != null) {
                    v.loadReadContentComplete(null, "章节索引错误");
                }
            });
            return;
        }
        String dirPath = chapterInfoList.get(position).getChapterUrl();
        List<Content> list = OfflineChapterLocalReader.buildContentList(dirPath, chapterId);
        AndroidSchedulers.mainThread().scheduleDirect(() -> {
            ReaderView v = getView();
            if (v == null) {
                return;
            }
            if (list.isEmpty()) {
                v.loadReadContentComplete(null, "本话目录无图片");
            } else {
                v.loadReadContentComplete(list, null);
            }
        });
    }

}
