package com.qc.common.constant;

import android.util.Log;

import com.qc.common.en.SettingEnum;
import com.qc.common.util.SettingUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LuQiChuang
 * @desc
 * @date 2021/1/27 22:06
 * @ver 1.0
 */
public class TmpData {

    private static final String TAG = "TmpData";

    public static int toStatus = Constant.NORMAL;

    public static boolean isLight = true;

    public static boolean isFull;

    public static int contentCode;

    public static String content;

    public static int videoSpeed = 2;

    public static Map<String, String> map = new HashMap<>();

    // 延迟初始化，避免静态初始化时失败
    static {
        try {
            isFull = (boolean) SettingUtil.getSettingKey(SettingEnum.IS_FULL_SCREEN);
            contentCode = (int) SettingUtil.getSettingKey(SettingEnum.READ_CONTENT);
            content = SettingUtil.getSettingDesc(SettingEnum.READ_CONTENT);
            Log.d(TAG, "TmpData initialized: contentCode=" + contentCode + ", content=" + content);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing TmpData, using defaults", e);
            // 使用默认值
            isFull = false;
            contentCode = AppConstant.COMIC_CODE; // 默认使用漫画模式
            content = "漫画";
        }
    }
}
