package org.dyndns.warenix.palpal;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class PalPalPreference {
	static final String PREFS_NAME = "palpal_preferenceF";
	static final String ACCESS_TOKEN0 = "access_token0";
	static final String ACCESS_TOKEN1 = "access_token1";
	static final String SINCE_ID = "since_id";
	static final String FACEBOOK_SINCE_TIME = "facebook_since_time";

	// static final String KEY_FACEBOOK_NUMBER_OF_WALL_POSTS =
	// "facebook_number_of_wall_post";
	// static final String KEY_FACEBOOK_NUMBER_OF_ALBUM_PHOTOS =
	// "facebook_number_of_album_photos";

	public static String[] loadAccessTokenPreference(Context context) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		String[] accessToken = new String[2];
		accessToken[0] = settings.getString(ACCESS_TOKEN0, null);
		accessToken[1] = settings.getString(ACCESS_TOKEN1, null);

		return accessToken;
	}

	public static void writeAccessTokenPreference(Context context,
			String accessToken[]) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(ACCESS_TOKEN0, accessToken[0]);
		editor.putString(ACCESS_TOKEN1, accessToken[1]);
		editor.commit();
	}

	public static String loadTwitterSinceIdPreference(Context context) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		String sinceId = settings.getString(SINCE_ID, null);

		return sinceId;
	}

	public static void writeSinceIdPreference(Context context, String statusId) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(SINCE_ID, statusId);
		editor.commit();
	}

	public static String loadFacebookSinceTimePreference(Context context) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		String sinceId = settings.getString(FACEBOOK_SINCE_TIME, "");

		return sinceId;
	}

	public static void writeFacebookSinceTimePreference(Context context,
			String since) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(FACEBOOK_SINCE_TIME, since);
		editor.commit();
	}

	/**
	 * save string by key
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void savePreferenceValue(Context context, String key,
			String value) {
		SharedPreferences customSharedPreference = context
				.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = customSharedPreference.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * get string by key
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public static String loadPreferenceValue(Context context, String key,
			String defaultValue) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		return settings.getString(key, defaultValue);
	}

}
