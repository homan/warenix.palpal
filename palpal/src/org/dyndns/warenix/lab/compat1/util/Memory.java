package org.dyndns.warenix.lab.compat1.util;

import twitter4j.Twitter;

import com.facebook.android.Facebook;

public class Memory {
	static Twitter twitter;
	static Facebook facebook;
	static String accessToken;

	public static void setTwitterClient(Twitter t) {
		twitter = t;
	}

	public static Twitter getTwitterClient() {
		return twitter;
	}

	public static void setFacebookClient(Facebook f) {
		facebook = f;
	}

	public static Facebook getFacebookClient() {
		return facebook;
	}

	public static void setAccessToken(String a) {
		accessToken = a;
	}

	public static String getAccessToken() {
		return accessToken;
	}

}
