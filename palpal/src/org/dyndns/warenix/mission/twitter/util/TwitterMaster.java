package org.dyndns.warenix.mission.twitter.util;

import org.dyndns.warenix.lab.compat1.R;
import org.dyndns.warenix.lab.compat1.util.Memory;
import org.dyndns.warenix.lab.compat1.util.PreferenceMaster;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.content.Context;
import android.util.Log;

public class TwitterMaster {

	public static final String PREF_NAME = "twitter_pref";
	public static final String ACCESS_TOKEN0 = "access_token0";
	public static final String ACCESS_TOKEN1 = "access_token1";
	public static final String SCREEN_NAME = "screen_name";

	/**
	 * 
	 * @param context
	 *            getApplicationContext
	 * @return
	 */
	public static boolean restoreTwitterClient(Context context) {
		String access_token0 = PreferenceMaster.load(context, PREF_NAME,
				ACCESS_TOKEN0, null);
		String access_token1 = PreferenceMaster.load(context, PREF_NAME,
				ACCESS_TOKEN1, null);

		if (access_token0 != null) {
			Twitter twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(
					context.getText(R.string.JTWITTER_OAUTH_KEY).toString(),
					context.getText(R.string.JTWITTER_OAUTH_SECRET).toString());

			AccessToken accessToken = new AccessToken(access_token0,
					access_token1);
			twitter.setOAuthAccessToken(accessToken);

			Memory.setTwitterClient(twitter);
			Log.d("palpal", "setTwitter()");
			return true;
		}
		return false;
	}

	public static String getScreenName(Context context) {
		return PreferenceMaster.load(context, PREF_NAME, SCREEN_NAME, "");
	}

}
