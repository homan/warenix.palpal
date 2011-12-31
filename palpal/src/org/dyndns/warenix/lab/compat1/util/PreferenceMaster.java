package org.dyndns.warenix.lab.compat1.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceMaster {
	public static void save(Context context, String PREFS_NAME, String key,
			String value) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static void save(Context context, String PREFS_NAME, String key,
			long value) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public static String load(Context context, String PREFS_NAME, String key,
			String defValue) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		return settings.getString(key, defValue);
	}

	public static long load(Context context, String PREFS_NAME, String key,
			long defValue) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		return settings.getLong(key, defValue);
	}
}
