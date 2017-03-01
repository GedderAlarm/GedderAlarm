package com.gedder.gedderalarm.util;

/**
 * USER: mslm
 * DATE: 3/1/2017
 * FROM: http://stackoverflow.com/a/4592958
 */

/**
 * A wrapper on android.util.Log's functionality, to
 * make it easier to disable/enable logging (i.e. for
 * pushing to production, or debugging).
 */
public class Log {
    private static final boolean LOG = true;

    public static void i(String tag, String string) {
        if (LOG) android.util.Log.i(tag, string);
    }

    public static void e(String tag, String string) {
        if (LOG) android.util.Log.e(tag, string);
    }

    public static void d(String tag, String string) {
        if (LOG) android.util.Log.d(tag, string);
    }

    public static void v(String tag, String string) {
        if (LOG) android.util.Log.v(tag, string);
    }

    public static void w(String tag, String string) {
        if (LOG) android.util.Log.w(tag, string);
    }
}
