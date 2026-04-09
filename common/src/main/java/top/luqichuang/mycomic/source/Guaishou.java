package top.luqichuang.mycomic.source;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Request;
import top.luqichuang.common.en.SourceEnum;
import top.luqichuang.common.jsoup.JsoupNode;
import top.luqichuang.common.model.ChapterInfo;
import top.luqichuang.common.model.Content;
import top.luqichuang.common.util.NetUtil;
import top.luqichuang.common.util.SourceHelper;
import top.luqichuang.mycomic.model.BaseComicSource;
import top.luqichuang.mycomic.model.ComicInfo;

public class Guaishou extends BaseComicSource {

    private static final String INDEX = "http://www.guaishoumanhua.net";
    private static final Pattern SEARCH_ITEM_PATTERN = Pattern.compile(
            "\"comic_id\"\\s*:\\s*\"(\\d+)\"\\s*,\\s*\"comic_title\"\\s*:\\s*\"(.*?)\"\\s*,\\s*\"update_time\"\\s*:\\s*\"(.*?)\"\\s*,\\s*\"chapter_count\"\\s*:\\s*(\\d+)",
            Pattern.DOTALL
    );

    private volatile String lastSearchKeyword = "";

    @Override
    public SourceEnum getSourceEnum() {
        return SourceEnum.GUAI_SHOU;
    }

    @Override
    public String getIndex() {
        return INDEX;
    }

    @Override
    public Request getSearchRequest(String searchString) {
        lastSearchKeyword = searchString == null ? "" : searchString.trim();
        return NetUtil.getRequest(getIndex() + "/search.js");
    }

    @Override
    public List<ComicInfo> getInfoList(String html) {
        List<ComicInfo> list = new ArrayList<>();
        if (html == null || html.isEmpty()) {
            return list;
        }
        String keyword = lastSearchKeyword == null ? "" : lastSearchKeyword.trim().toLowerCase();
        Matcher matcher = SEARCH_ITEM_PATTERN.matcher(html);
        while (matcher.find()) {
            String comicId = matcher.group(1);
            String title = decodeJsString(matcher.group(2));
            String updateTime = decodeJsString(matcher.group(3));
            String chapterCount = matcher.group(4);
            if (!keyword.isEmpty() && (title == null || !title.toLowerCase().contains(keyword))) {
                continue;
            }
            String detailUrl = getIndex() + "/comics/" + comicId + "/index.html";
            String imgUrl = getIndex() + "/static/images/covers/cover_" + comicId + ".webp";
            String author = "共 " + chapterCount + " 章";
            list.add(new ComicInfo(getSourceId(), title, author, detailUrl, imgUrl, updateTime));
            if (list.size() >= 80) {
                break;
            }
        }
        return list;
    }

    @Override
    public void setInfoDetail(ComicInfo info, String html, Map<String, Object> map) {
        JsoupNode node = new JsoupNode(html);
        String title = node.ownText("h1.detail-title");
        if (title == null) {
            title = node.ownText("h1");
        }
        String imgUrl = normalizeUrl(info.getDetailUrl(), node.src("img.cover-img, img.cover"));
        String intro = node.ownText("div.details p");
        String updateStatus = "连载中";
        String updateTime = info.getUpdateTime();
        String author = node.ownText("span.alias");
        if (author != null) {
            author = author.replace("别名：", "").trim();
        }
        info.setDetail(title, imgUrl, author, updateTime, updateStatus, intro);

        Elements chapterElements = node.getElements("a.chapter-link, li.chapter-item a");
        List<ChapterInfo> chapterList = new ArrayList<>();
        int i = 0;
        for (Element element : chapterElements) {
            node.init(element);
            String chapterTitle = node.ownText("a");
            String chapterUrl = normalizeUrl(info.getDetailUrl(), node.href("a"));
            if (chapterTitle == null || chapterUrl == null) {
                continue;
            }
            chapterList.add(new ChapterInfo(i++, chapterTitle, chapterUrl));
        }
        SourceHelper.initChapterInfoList(info, chapterList, ComicInfo.ASC);
    }

    @Override
    public List<Content> getContentList(String html, int chapterId, Map<String, Object> map) {
        JsoupNode node = new JsoupNode(html);
        Elements elements = node.getElements("img.chapter-img");
        List<String> urlList = new ArrayList<>();
        for (Element element : elements) {
            node.init(element);
            String url = node.attr("img", "data-src");
            if (url == null || url.isEmpty()) {
                url = node.src("img");
            }
            url = normalizeUrl(getIndex() + "/", url);
            if (url != null && !url.isEmpty()) {
                urlList.add(url);
            }
        }
        return SourceHelper.getContentList(urlList, chapterId);
    }

    @Override
    public Map<String, String> getRankMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("分类热门", getIndex() + "/");
        map.put("日漫", getIndex() + "/classifications/37/index.html");
        map.put("国漫", getIndex() + "/classifications/38/index.html");
        map.put("韩漫", getIndex() + "/classifications/42/index.html");
        map.put("美漫", getIndex() + "/classifications/41/index.html");
        map.put("少年漫", getIndex() + "/classifications/44/index.html");
        map.put("少女漫", getIndex() + "/classifications/45/index.html");
        map.put("热血", getIndex() + "/classifications/81/index.html");
        map.put("冒险", getIndex() + "/classifications/69/index.html");
        map.put("科幻", getIndex() + "/classifications/60/index.html");
        map.put("奇幻", getIndex() + "/classifications/147/index.html");
        map.put("搞笑", getIndex() + "/classifications/62/index.html");
        map.put("爱情", getIndex() + "/classifications/128/index.html");
        map.put("日常", getIndex() + "/classifications/132/index.html");
        map.put("悬疑", getIndex() + "/classifications/144/index.html");
        return map;
    }

    @Override
    public List<ComicInfo> getRankInfoList(String html) {
        JsoupNode node = new JsoupNode(html);
        Elements elements = node.getElements("div.comic-card");
        List<ComicInfo> list = new ArrayList<>();
        for (Element element : elements) {
            node.init(element);
            String title = node.ownText("span.comic-name");
            String href = node.href("a");
            String imgUrl = node.src("img.comic-cover");
            String detailUrl = toAbsoluteUrl(href);
            imgUrl = toAbsoluteUrl(imgUrl);
            if (title == null || detailUrl == null) {
                continue;
            }
            list.add(new ComicInfo(getSourceId(), title, null, detailUrl, imgUrl, null));
        }
        return list;
    }

    private String toAbsoluteUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        String cleaned = url.replaceAll("^(\\.\\./)+", "");
        if (!cleaned.startsWith("/")) {
            cleaned = "/" + cleaned;
        }
        return getIndex() + cleaned;
    }

    private String normalizeUrl(String baseUrl, String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        if (url.startsWith("//")) {
            return "http:" + url;
        }
        String base = baseUrl;
        if (base == null || base.isEmpty()) {
            base = getIndex() + "/";
        }
        if (url.startsWith("/")) {
            return getIndex() + url;
        }
        int idx = base.lastIndexOf('/');
        if (idx >= 0) {
            base = base.substring(0, idx + 1);
        } else if (!base.endsWith("/")) {
            base = base + "/";
        }
        while (url.startsWith("../")) {
            url = url.substring(3);
            String tmp = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
            int p = tmp.lastIndexOf('/');
            if (p > "http://".length()) {
                base = tmp.substring(0, p + 1);
            } else {
                base = getIndex() + "/";
            }
        }
        return base + url;
    }

    private String decodeJsString(String value) {
        if (value == null) {
            return null;
        }
        return value
                .replace("\\\\", "\\")
                .replace("\\\"", "\"")
                .replace("\\'", "'");
    }
}
