package com.qc.common.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.qc.common.constant.AppConstant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import top.luqichuang.common.model.Content;
import top.luqichuang.common.model.Entity;
import top.luqichuang.common.model.Source;
import top.luqichuang.common.util.NetUtil;

/**
 * 将当前话全部图片下载到本地目录（与 Glide 缓存无关，可长期保留）。
 */
public final class ComicChapterOfflineDownloader implements AppConstant {

    public interface Callback {
        void onSuccess(File chapterDir);

        void onError(String message);
    }

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    private ComicChapterOfflineDownloader() {
    }

    public static void download(Context context, Entity entity, List<Content> pages, Source source, Callback callback) {
        if (context == null) {
            runMain(() -> callback.onError("无法访问存储"));
            return;
        }
        if (entity == null || pages == null || pages.isEmpty()) {
            runMain(() -> callback.onError("没有可下载的页面"));
            return;
        }
        final Context appContext = context.getApplicationContext();
        Handler main = new Handler(Looper.getMainLooper());
        List<Content> snapshot = new ArrayList<>(pages);
        int chapterId = snapshot.get(0).getChapterId();
        EXECUTOR.execute(() -> {
            try {
                File dir = syncDownload(appContext, entity, snapshot, source, chapterId);
                main.post(() -> callback.onSuccess(dir));
            } catch (Exception e) {
                String msg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
                main.post(() -> callback.onError(msg));
            }
        });
    }

    /**
     * 优先使用 SD 卡上的 MyComic/OfflineChapter（需存储权限）；不可写时回退到应用专属外存目录（Android 13+ 等场景无需权限）。
     */
    private static File resolveWritableOfflineRoot(Context context) throws IOException {
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

    private static File syncDownload(Context context, Entity entity, List<Content> pages, Source source, int chapterId) throws IOException {
        File baseDir = resolveWritableOfflineRoot(context);
        String comicSeg = safeSegment(entity.getTitle(), 48) + "_id" + entity.getInfoId();
        String chapterSeg = "ch" + chapterId + "_" + safeSegment(entity.getCurChapterTitle(), 32);
        File root = new File(baseDir, comicSeg);
        File chapterDir = new File(root, chapterSeg);
        if (!chapterDir.exists() && !chapterDir.mkdirs()) {
            throw new IOException("无法创建目录: " + chapterDir.getAbsolutePath());
        }
        if (!chapterDir.canWrite()) {
            throw new IOException("目录不可写: " + chapterDir.getAbsolutePath());
        }
        Map<String, String> baseHeaders = source != null ? source.getImageHeaders() : null;
        int saved = 0;
        for (Content content : pages) {
            String url = content.getUrl();
            if (url == null || url.trim().isEmpty()) {
                continue;
            }
            Map<String, String> headers = new LinkedHashMap<>();
            if (baseHeaders != null) {
                headers.putAll(baseHeaders);
            }
            if (content.getHeaderMap() != null) {
                headers.putAll(content.getHeaderMap());
            }
            Request request = NetUtil.getRequestByHeader(url, headers);
            File outFile = new File(chapterDir, fileNameForPage(content, url));
            downloadOne(request, outFile);
            saved++;
        }
        if (saved == 0) {
            throw new IOException("本话没有有效图片地址");
        }
        return chapterDir;
    }

    private static void downloadOne(Request request, File outFile) throws IOException {
        try (Response response = NetUtil.getOkHttpClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("请求失败 " + response.code() + " " + request.url());
            }
            ResponseBody body = response.body();
            if (body == null) {
                throw new IOException("响应为空 " + request.url());
            }
            try (InputStream in = body.byteStream(); FileOutputStream out = new FileOutputStream(outFile)) {
                byte[] buf = new byte[8192];
                int n;
                while ((n = in.read(buf)) != -1) {
                    out.write(buf, 0, n);
                }
            }
        }
    }

    private static String fileNameForPage(Content content, String url) {
        String ext = extensionFromUrl(url);
        return String.format(Locale.US, "%05d%s", content.getCur() + 1, ext);
    }

    private static String extensionFromUrl(String url) {
        try {
            HttpUrl httpUrl = HttpUrl.parse(url);
            if (httpUrl == null) {
                return ".jpg";
            }
            String path = httpUrl.encodedPath();
            int dot = path.lastIndexOf('.');
            if (dot > 0 && dot < path.length() - 1) {
                String ext = path.substring(dot).toLowerCase(Locale.US);
                if (ext.matches("\\.(jpg|jpeg|png|webp|gif|bmp)")) {
                    return ext;
                }
            }
        } catch (Exception ignored) {
        }
        return ".jpg";
    }

    private static String safeSegment(String s, int maxLen) {
        if (s == null) {
            return "unknown";
        }
        String t = s.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
        if (t.isEmpty()) {
            return "unknown";
        }
        if (t.length() > maxLen) {
            return t.substring(0, maxLen);
        }
        return t;
    }

    private static void runMain(Runnable r) {
        new Handler(Looper.getMainLooper()).post(r);
    }
}
