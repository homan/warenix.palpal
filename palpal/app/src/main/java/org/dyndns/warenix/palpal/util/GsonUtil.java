package org.dyndns.warenix.palpal.util;

import com.google.gson.Gson;

/**
 * Created by warenix on 8/23/14.
 */
public class GsonUtil {

    private static Gson sGson;

    public static Gson getGson() {
        if (sGson == null) {
            sGson = new Gson();
        }
        return sGson;
    }
}
