package top.luqichuang.mycomic.source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;
import top.luqichuang.common.en.SourceEnum;
import top.luqichuang.common.model.Content;
import top.luqichuang.common.util.NetUtil;
import top.luqichuang.mycomic.model.BaseComicSource;
import top.luqichuang.mycomic.model.ComicInfo;

/**
 * 本机已下载章节阅读用，不参与网络搜索与榜单。
 */
public class LocalOfflineSource extends BaseComicSource {

    @Override
    public SourceEnum getSourceEnum() {
        return SourceEnum.LOCAL_OFFLINE;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public String getIndex() {
        return "";
    }

    @Override
    public Request getSearchRequest(String searchString) {
        return NetUtil.getRequest("https://127.0.0.1/");
    }

    @Override
    public List<ComicInfo> getInfoList(String html) {
        return new ArrayList<>();
    }

    @Override
    public void setInfoDetail(ComicInfo info, String html, Map<String, Object> map) {
    }

    @Override
    public List<Content> getContentList(String html, int chapterId, Map<String, Object> map) {
        return Collections.emptyList();
    }

    @Override
    public Map<String, String> getRankMap() {
        return new LinkedHashMap<>();
    }

    @Override
    public List<ComicInfo> getRankInfoList(String html) {
        return new ArrayList<>();
    }
}
