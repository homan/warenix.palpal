package org.dyndns.warenix.palpal.util;

import android.util.Log;

/**
 * Created by warenix on 8/23/14.
 */
public class LogUtil {

    public static void makeLog(String tag, String methodName, String format, Object... params) {
        Log.d(String.format("%s-%s", tag, methodName), String.format(format, params));
    }
}
