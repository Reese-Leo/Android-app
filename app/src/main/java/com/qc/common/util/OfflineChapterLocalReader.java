package com.qc.common.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.luqichuang.common.model.Content;

/**
 * 从离线话目录构建 {@link Content} 列表（url 为本地绝对路径，供 Glide 直接加载）。
 */
public final class OfflineChapterLocalReader {

    private static final Pattern IMAGE_EXT = Pattern.compile("(?i).+\\.(jpg|jpeg|png|webp|gif|bmp)$");
    private static final Pattern CHAPTER_PREFIX = Pattern.compile("^ch(\\d+)_");

    private OfflineChapterLocalReader() {
    }

    public static List<Content> buildContentList(String chapterDirAbsolutePath, int chapterId) {
        if (chapterDirAbsolutePath == null || chapterDirAbsolutePath.isEmpty()) {
            return Collections.emptyList();
        }
        File dir = new File(chapterDirAbsolutePath);
        if (!dir.isDirectory()) {
            return Collections.emptyList();
        }
        File[] files = dir.listFiles((d, name) -> IMAGE_EXT.matcher(name).matches());
        if (files == null || files.length == 0) {
            return Collections.emptyList();
        }
        Arrays.sort(files, Comparator.comparing(File::getName));
        int total = files.length;
        List<Content> list = new ArrayList<>(total);
        for (int i = 0; i < total; i++) {
            list.add(new Content(chapterId, i, total, files[i].getAbsolutePath()));
        }
        return list;
    }

    static boolean hasImageFile(File dir) {
        if (dir == null || !dir.isDirectory()) {
            return false;
        }
        File[] fs = dir.listFiles();
        if (fs == null) {
            return false;
        }
        for (File f : fs) {
            if (f.isFile() && IMAGE_EXT.matcher(f.getName()).matches()) {
                return true;
            }
        }
        return false;
    }

    static int parseChapterOrderKey(String folderName) {
        Matcher m = CHAPTER_PREFIX.matcher(folderName);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group(1));
            } catch (NumberFormatException ignored) {
            }
        }
        return 0;
    }

    static String chapterDisplayTitle(String folderName) {
        Matcher m = CHAPTER_PREFIX.matcher(folderName);
        if (m.find() && m.end() < folderName.length()) {
            return folderName.substring(m.end());
        }
        return folderName;
    }

    static String parseComicTitleFromFolder(String folderName) {
        int idx = folderName.lastIndexOf("_id");
        if (idx > 0) {
            return folderName.substring(0, idx);
        }
        return folderName;
    }
}
