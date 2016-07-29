package com.lwm.guesssong.util;

import android.util.Log;

/**
 * @author：lwm on 2016/7/25 21:00
 * @updateDate：2016/7/25
 * @version：1.0
 * @email：846988094@qq.com
 */
public class SLog {
    //记录log信息Util
    public static final boolean DEBUG = true;

    public static void d(String tag, String message) {
        if (DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void w(String tag, String message) {
        if (DEBUG) {
            Log.w(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (DEBUG) {
            Log.e(tag, message);
        }
    }

    public static void i(String tag, String message) {
        if (DEBUG) {
            Log.i(tag, message);
        }
    }
}
