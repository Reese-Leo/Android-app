package com.qc.common.constant;

import the.one.base.util.SdCardUtil;

/**
 * @author LuQiChuang
 * @desc
 * @date 2021/1/27 21:39
 * @ver 1.0
 */
public interface AppConstant {

    String NORMAL_PATH = SdCardUtil.getNormalSDCardPath();

    String APP_PATH = NORMAL_PATH + "/MyComic";

    String SHELF_IMG_PATH = APP_PATH + "/ShelfImg";

    String IMG_PATH = APP_PATH + "/Image";

    /** 漫画整话离线图，按作品/章节分子目录保存 */
    String OFFLINE_CHAPTER_PATH = APP_PATH + "/OfflineChapter";

    String AUTO_SAVE_PATH = APP_PATH + "/AutoBackup";

    int COMIC_CODE = 1;

    int READER_CODE = 2;

    int VIDEO_CODE = 3;
}
