package com.qc.common.util;

import android.content.Context;

import com.qc.common.constant.AppConstant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 离线下载根目录：与 {@link ComicChapterOfflineDownloader} 使用相同解析规则。
 */
public final class OfflineStorageHelper implements AppConstant {

    private OfflineStorageHelper() {
    }

    public static File resolveWritableOfflineRoot(Context context) throws IOException {
        File legacy = new File(OFFLINE_CHAPTER_PATH);
        if (isDirWritableOrCreatable(legacy)) {
            return legacy;
        }
        File ext = context.getExternalFilesDir(null);
        if (ext == null) {
            ext = context.getFilesDir();
        }
        File fallback = new File(ext, "OfflineChapter");
        if (!isDirWritableOrCreatable(fallback)) {
            throw new IOException("无法创建目录（存储权限或磁盘空间）: " + fallback.getAbsolutePath());
        }
        return fallback;
    }

    /**
     * 已存在的离线根目录（用于扫描，只读）。
     */
    public static List<File> listExistingOfflineRoots(Context context) {
        List<File> roots = new ArrayList<>();
        File legacy = new File(OFFLINE_CHAPTER_PATH);
        if (legacy.isDirectory()) {
            roots.add(legacy);
        }
        if (context != null) {
            File ext = context.getExternalFilesDir(null);
            if (ext != null) {
                File f = new File(ext, "OfflineChapter");
                if (f.isDirectory() && !roots.contains(f)) {
                    roots.add(f);
                }
            }
        }
        return roots;
    }

    private static boolean isDirWritableOrCreatable(File dir) {
        if (dir == null) {
            return false;
        }
        try {
            if (dir.exists()) {
                return dir.isDirectory() && dir.canWrite();
            }
            if (dir.mkdirs() || dir.exists()) {
                return dir.isDirectory() && dir.canWrite();
            }
        } catch (SecurityException ignored) {
            return false;
        }
        return false;
    }
}
