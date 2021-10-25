package com.sky.xposed.rimet.util;

import android.util.Log;

import com.sky.xposed.rimet.Constant;

import de.robv.android.xposed.XposedBridge;

/**
 * LogUtils
 *
 * @author why
 * @since 2021/10/25
 */
public class LogUtils {
    public static void log(String text) {
        XposedBridge.log(Constant.TAG + ": " + text);
        Log.e(Constant.TAG, Constant.TAG + ": " + text);
    }
}
