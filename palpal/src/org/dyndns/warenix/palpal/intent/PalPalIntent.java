package org.dyndns.warenix.palpal.intent;

public class PalPalIntent {
	/**
	 * Action to quote tweet. Must bundle a "message" twitter4j.Status instance
	 */
	public static final String ACTION_TWITTER_QUOTE_TWEET = "org.dyndns.warenix.palpal.twitter.ACTION_QUOTE_TWEET";

	/**
	 * Action to quote tweet. Must bundle a "message" FacebookObject instance
	 */
	public static final String ACTION_FACEBOOK_RESHARE_POST = "org.dyndns.warenix.palpal.facebook.ACTION_RESHARE_POST";

	/**
	 * Action to launch palpal main.
	 */
	public static final String ACTION_PALPAL_MAIN = "org.dyndns.warenix.palpal.ACTION_PALPAL_MAIN";

}
