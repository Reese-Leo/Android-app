package com.qc.common.util;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import top.luqichuang.common.en.SourceEnum;
import top.luqichuang.common.model.ChapterInfo;
import top.luqichuang.mycomic.model.Comic;
import top.luqichuang.mycomic.model.ComicInfo;

/**
 * 扫描 {@link OfflineStorageHelper#listExistingOfflineRoots(Context)} 下的漫画/话目录，构建可阅读的 {@link Comic}。
 */
public final class OfflineChapterScanner {

    private OfflineChapterScanner() {
    }

    public static List<Comic> scan(Context context) {
        if (context == null) {
            return Collections.emptyList();
        }
        Set<String> seenPaths = new HashSet<>();
        List<Comic> out = new ArrayList<>();
        for (File root : OfflineStorageHelper.listExistingOfflineRoots(context)) {
            File[] comicDirs = root.listFiles(File::isDirectory);
            if (comicDirs == null) {
                continue;
            }
            for (File comicDir : comicDirs) {
                String key = comicDir.getAbsolutePath();
                if (seenPaths.contains(key)) {
                    continue;
                }
                Comic comic = buildComic(comicDir);
                if (comic != null) {
                    seenPaths.add(key);
                    out.add(comic);
                }
            }
        }
        out.sort(Comparator.comparing(c -> c.getTitle() != null ? c.getTitle() : "", String.CASE_INSENSITIVE_ORDER));
        return out;
    }

    private static Comic buildComic(File comicDir) {
        File[] chDirs = comicDir.listFiles(File::isDirectory);
        if (chDirs == null || chDirs.length == 0) {
            return null;
        }
        List<File> withImages = new ArrayList<>();
        for (File d : chDirs) {
            if (OfflineChapterLocalReader.hasImageFile(d)) {
                withImages.add(d);
            }
        }
        if (withImages.isEmpty()) {
            return null;
        }
        withImages.sort((a, b) -> {
            int ia = OfflineChapterLocalReader.parseChapterOrderKey(a.getName());
            int ib = OfflineChapterLocalReader.parseChapterOrderKey(b.getName());
            if (ia != ib) {
                return Integer.compare(ia, ib);
            }
            return a.getName().compareToIgnoreCase(b.getName());
        });
        List<ChapterInfo> chapters = new ArrayList<>();
        for (int i = 0; i < withImages.size(); i++) {
            File ch = withImages.get(i);
            String title = OfflineChapterLocalReader.chapterDisplayTitle(ch.getName());
            chapters.add(new ChapterInfo(i, title, ch.getAbsolutePath()));
        }
        ComicInfo info = new ComicInfo();
        info.setSourceId(SourceEnum.LOCAL_OFFLINE.ID);
        info.setTitle(OfflineChapterLocalReader.parseComicTitleFromFolder(comicDir.getName()));
        info.setId(Math.abs(comicDir.getAbsolutePath().hashCode()));
        info.setAuthor("");
        info.setDetailUrl("offline-local://" + comicDir.getAbsolutePath());
        info.setImgUrl("");
        info.setChapterInfoList(chapters);
        info.setChapterNum(chapters.size());
        info.setOrder(ComicInfo.ASC);
        info.setCurChapterId(0);
        info.setCurChapterTitle(chapters.get(0).getTitle());
        info.setUpdateChapter(chapters.get(chapters.size() - 1).getTitle());
        info.setUpdateStatus("离线");
        info.setUpdateTime("");
        Comic comic = new Comic(info);
        comic.setSourceId(SourceEnum.LOCAL_OFFLINE.ID);
        return comic;
    }
}
