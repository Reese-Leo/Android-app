package top.luqichuang.mycomic.source;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;
import top.luqichuang.common.en.SourceEnum;
import top.luqichuang.common.jsoup.JsoupNode;
import top.luqichuang.common.jsoup.JsoupStarter;
import top.luqichuang.common.model.ChapterInfo;
import top.luqichuang.common.model.Content;
import top.luqichuang.common.util.NetUtil;
import top.luqichuang.common.util.SourceHelper;
import top.luqichuang.mycomic.model.BaseComicSource;
import top.luqichuang.mycomic.model.ComicInfo;

/**
 * @author LuQiChuang
 * @desc MOJOIN 漫画源
 * @date 2026/02/28
 * @ver 1.0
 */
public class MoJoin extends BaseComicSource {
    
    @Override
    public SourceEnum getSourceEnum() {
        return SourceEnum.MO_JOIN;
    }

    @Override
    public String getIndex() {
        return "https://mojoin.com";
    }

    @Override
    public Request getSearchRequest(String searchString) {
        // 根据实际网站搜索接口调整
        String url = getIndex() + "/comics/search?q=" + searchString;
        return NetUtil.getRequest(url);
    }

    @Override
    public List<ComicInfo> getInfoList(String html) {
        JsoupStarter<ComicInfo> starter = new JsoupStarter<ComicInfo>() {
            @Override
            protected ComicInfo dealElement(JsoupNode node, int elementId) {
                // 根据实际网站 HTML 结构调整选择器
                // 以下是常见结构的示例，需要根据实际网站调整
                String title = node.title("a.comic-title, a.title, h3 a, .comic-item a");
                String author = node.ownText(".author, .comic-author, span.author");
                String updateTime = node.ownText(".update-time, .time, span.time");
                String imgUrl = node.src("img.cover, img.comic-cover, .comic-item img");
                String detailUrl = node.href("a.comic-title, a.title, .comic-item a");
                
                // 处理相对路径
                if (detailUrl != null && !detailUrl.startsWith("http")) {
                    detailUrl = getIndex() + detailUrl;
                }
                
                return new ComicInfo(getSourceId(), title, author, detailUrl, imgUrl, updateTime);
            }
        };
        // 根据实际网站结构调整容器选择器
        // 常见选择器：div.comic-item, div.comic-card, li.comic-item, .comic-list > div
        return starter.startElements(html, "div.comic-item, div.comic-card, .comic-list > div");
    }

    @Override
    public void setInfoDetail(ComicInfo info, String html, Map<String, Object> map) {
        JsoupStarter<ChapterInfo> starter = new JsoupStarter<ChapterInfo>() {
            @Override
            protected boolean isDESC() {
                // true: 章节倒序（最新在前），false: 正序（最早在前）
                // 根据网站实际情况调整
                return false;
            }

            @Override
            protected void dealInfo(JsoupNode node) {
                // 根据实际网站 HTML 结构调整选择器
                String title = node.ownText("h1.title, h1.comic-title, .comic-title");
                String imgUrl = node.src("img.cover, img.comic-cover, .comic-cover img");
                String author = node.ownText(".author, .comic-author, span.author");
                String intro = node.ownText(".intro, .description, .comic-intro, p.intro");
                String updateStatus = node.ownText(".status, .comic-status, span.status");
                String updateTime = node.ownText(".update-time, .time, span.time");
                
                info.setDetail(title, imgUrl, author, updateTime, updateStatus, intro);
            }

            @Override
            protected ChapterInfo dealElement(JsoupNode node, int elementId) {
                // 根据实际网站 HTML 结构调整选择器
                String title = node.ownText("a.chapter-title, a, span.chapter-name, .chapter-item a");
                String chapterUrl = node.href("a.chapter-title, a, .chapter-item a");
                
                // 处理相对路径
                if (chapterUrl != null && !chapterUrl.startsWith("http")) {
                    chapterUrl = getIndex() + chapterUrl;
                }
                
                return new ChapterInfo(elementId, title, chapterUrl);
            }
        };
        starter.startInfo(html);
        // 根据实际网站结构调整章节列表选择器
        // 常见选择器：div.chapter-list a, ul.chapters li a, .chapter-list > a
        SourceHelper.initChapterInfoList(
            info, 
            starter.startElements(html, "div.chapter-list a, ul.chapters li a, .chapter-list > a")
        );
    }

    @Override
    public List<Content> getContentList(String html, int chapterId, Map<String, Object> map) {
        // 解析章节内容（图片列表）
        String[] urls = null;
        JsoupNode node = new JsoupNode(html);
        
        // 根据实际网站结构调整图片选择器
        // 常见选择器：div.comic-images img, .comic-content img, img.comic-page
        Elements elements = node.getElements("div.comic-images img, .comic-content img, img.comic-page");
        
        if (!elements.isEmpty()) {
            urls = new String[elements.size()];
            for (int i = 0; i < urls.length; i++) {
                node.init(elements.get(i));
                String imgUrl = node.src("img");
                // 处理相对路径
                if (imgUrl != null && !imgUrl.startsWith("http")) {
                    imgUrl = getIndex() + imgUrl;
                }
                urls[i] = imgUrl;
            }
        }
        return SourceHelper.getContentList(urls, chapterId);
    }

    @Override
    public Map<String, String> getRankMap() {
        // 如果网站有排行榜功能，在这里实现
        // 否则返回空 Map
        Map<String, String> map = new LinkedHashMap<>();
        // 示例：
        // map.put("热门", getIndex() + "/rank/hot");
        // map.put("最新", getIndex() + "/rank/new");
        return map;
    }

    @Override
    public List<ComicInfo> getRankInfoList(String html) {
        // 排行榜列表通常与搜索结果格式相同
        return getInfoList(html);
    }

    @Override
    public Map<String, String> getImageHeaders() {
        // 如果需要特殊的图片请求头（如 Referer），在这里实现
        Map<String, String> headers = new LinkedHashMap<>();
        // 示例：
        // headers.put("Referer", getIndex() + "/");
        // headers.put("User-Agent", "Mozilla/5.0...");
        return headers;
    }
}
