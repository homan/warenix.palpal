package org.dyndns.warenix.palpal.social.twitter;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Set;

import org.dyndns.warenix.palpal.PalPal;
import org.dyndns.warenix.palpal.PalPalPreference;
import org.dyndns.warenix.util.ToastUtil;

import winterwell.jtwitter.OAuthSignpostClient;
import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

public abstract class PalPalTwitterActivity extends Activity {

	static private LayoutInflater mChildInflater;
	static private LayoutInflater mGroupInflater;
	OAuthSignpostClient oauthClient;
	HashMap<String, SoftReference<Bitmap>> imagePool;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setupUI();

		mGroupInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mChildInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		checkTwitterSession();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == 1) {

			String[] accessToken = PalPalPreference
					.loadAccessTokenPreference(this);
			oauthClient = new OAuthSignpostClient(PalPal.JTWITTER_OAUTH_KEY,
					PalPal.JTWITTER_OAUTH_SECRET, accessToken[0],
					accessToken[1]);

			Twitter twitter = new Twitter("", oauthClient);
			PalPal.setTwitter(twitter);
			onTwitterClientReady();
		}
	}

	protected void onDestroy() {
		clearImagePool();
		super.onDestroy();

	}

	void clearImagePool() {
		if (imagePool != null) {
			Set<String> keySet = imagePool.keySet();
			for (String key : keySet) {
				SoftReference<Bitmap> ref = imagePool.get(key);
				if (ref != null) {
					Bitmap bm = ref.get();
					if (bm != null) {
						Log.d("warenix", "recycle bm " + key);
						bm.recycle();
					}
				}
			}
			imagePool.clear();
		}
	}

	abstract void setupUI();

	void checkTwitterSession() {
		Log.v("palpal", "checkTwitterSession");

		Twitter twitter = PalPal.getTwitterClient();
		if (twitter != null) {
			try {
				twitter.isValidLogin();
				Log.v("palpal",
						"checkTwitterSession: found stored valid twitter client");

				Log.v("palpal", "checkTwitterSession: twitter session is valid");
				PalPal.setTwitter(twitter);
				onTwitterClientReady();
			} catch (TwitterException e) {
				e.printStackTrace();

				ToastUtil.showNotification(this,
						"fail to validate twitter session",
						"fail to validate twitter session", e.getMessage(),
						null, 1000);
			}

		} else {
			String[] accessToken = PalPalPreference
					.loadAccessTokenPreference(this);
			if (accessToken[0] == null || accessToken[1] == null) {
				Log.v("palpal",
						"checkTwitterSession: no access token stored, going to start login dialog");
				startActivityForResult(new Intent(this,
						OAuthTwitterActivity.class), 1);
			} else {
				Log.v("palpal",
						String.format(
								"checkTwitterSession: found access token [%s] [%s], try oauth",
								accessToken[0], accessToken[1]));
				oauthClient = new OAuthSignpostClient(
						PalPal.JTWITTER_OAUTH_KEY,
						PalPal.JTWITTER_OAUTH_SECRET, accessToken[0],
						accessToken[1]);

				Twitter twitter1 = new Twitter("", oauthClient);
				try {
					if (twitter1.isValidLogin()) {
						Log.v("palpal",
								"checkTwitterSession: twitter session is valid");
						PalPal.setTwitter(twitter1);
						onTwitterClientReady();
					} else {
						Log.v("palpal",
								"checkTwitterSession: twitter session is invalid, going to start login dialog");
						startActivityForResult(new Intent(this,
								OAuthTwitterActivity.class), 1);
					}
				} catch (TwitterException e) {
					e.printStackTrace();

					ToastUtil.showNotification(this,
							"fail to validate twitter session",
							"fail to validate twitter session", e.getMessage(),
							null, 1000);
				} catch (Exception e) {
					e.printStackTrace();

					startActivityForResult(new Intent(this,
							OAuthTwitterActivity.class), 1);
				}
			}
		}

	}

	void onTwitterClientReady() {
		imagePool = new HashMap<String, SoftReference<Bitmap>>();

		setupUI();
		onTwitterClientReady(PalPal.getTwitterClient(), imagePool);
	}

	abstract void onTwitterClientReady(Twitter twitter,
			HashMap<String, SoftReference<Bitmap>> imagePool);

}
