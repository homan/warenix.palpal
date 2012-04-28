package org.dyndns.warenix.mission.twitter.util;

import java.io.File;
import java.io.FileInputStream;

import org.dyndns.warenix.image.ImageUtil;
import org.dyndns.warenix.lab.compat1.R;
import org.dyndns.warenix.lab.compat1.util.Memory;
import org.dyndns.warenix.lab.compat1.util.PreferenceMaster;
import org.dyndns.warenix.util.WLog;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.media.ImageUpload;
import twitter4j.media.ImageUploadFactory;
import twitter4j.media.MediaProvider;
import android.content.Context;

public class TwitterMaster {
	private static final String TAG = "TwitterMaster";
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
			WLog.i(TAG, "setTwitter()");
			return true;
		}
		return false;
	}

	public static void removeTwitterClient(Context context) {
		PreferenceMaster.save(context, PREF_NAME, ACCESS_TOKEN0, null);
		PreferenceMaster.save(context, PREF_NAME, ACCESS_TOKEN1, null);
		PreferenceMaster.save(context, PREF_NAME, SCREEN_NAME, null);
	}

	public static String getScreenName(Context context) {
		return PreferenceMaster.load(context, PREF_NAME, SCREEN_NAME, "");
	}

	/**
	 * update status with photo uploaded to twitter
	 * 
	 * @param fullLocalImagePath
	 * @param message
	 * @return
	 * @throws Exception
	 */
	public static String uploadPhotoFromFileToTwitter(
			String fullLocalImagePath, String message) throws Exception {

		String url = null;
		FileInputStream fin = null;
		try {
			File input = new File(fullLocalImagePath);
			input = createResizedPhotoIfNeeded(fullLocalImagePath);
			fin = new FileInputStream(input);
			ImageUploadFactory factory = new ImageUploadFactory();
			ImageUpload upload = factory.getInstance(MediaProvider.TWITTER,
					Memory.getTwitterClient().getAuthorization());
			url = upload.upload(fullLocalImagePath, fin, message);
		} finally {
			fin.close();
		}

		return url;
	}

	private static File createResizedPhotoIfNeeded(String fullLocalFilePath) {
		int maxWidth = 1024;
		int maxHeight = 2048;
		return ImageUtil.createResizedPhotoIfNeeded(fullLocalFilePath,
				maxWidth, maxHeight);
	}

	public static String createQuoteTweetStatus(Status message) {
		return String.format("RT @%s: %s", message.getUser().getScreenName(),
				message.getText());
	}
}
