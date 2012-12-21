package org.dyndns.warenix.lab.compat1.app.facebook;

import org.dyndns.warenix.lab.compat1.R;
import org.dyndns.warenix.lab.compat1.util.PreferenceMaster;
import org.dyndns.warenix.mission.facebook.util.FacebookMaster;
import org.dyndns.warenix.palpal.AppActivity;
import org.dyndns.warenix.util.WLog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class AuthenFacebookActivity extends AppActivity {
	private static final String TAG = "AuthenFacebookActivity";
	static final String[] permissions = new String[] { "user_about_me",
			"friends_about_me", "user_activities", "friends_activities",
			"user_birthday", "friends_birthday", "user_checkins",
			"friends_checkins", "user_education_history",
			"friends_education_history", "user_events", "friends_events",
			"user_groups", "friends_groups", "user_hometown",
			"friends_hometown", "user_interests", "friends_interests",
			"user_likes", "friends_likes", "user_location", "friends_location",
			"user_notes", "friends_notes", "user_online_presence",
			"friends_online_presence", "user_photos",
			"friends_photos",
			"user_questions",
			"friends_questions",
			"user_relationships",
			"friends_relationships",
			"user_relationship_details",
			"friends_relationship_details",
			"user_religion_politics",
			"friends_religion_politics",
			"user_status",
			"friends_status",
			"user_videos",
			"friends_videos",
			"user_website",
			"friends_website",
			"user_work_history",
			"friends_work_history",
			// "email",
			// Extended Permissions
			"read_friendlists", "read_insights", "read_mailbox",
			"read_requests", "read_stream", "xmpp_login", "ads_management",
			"create_event", "manage_friendlists", "manage_notifications",
			"offline_access", "publish_checkins", "publish_stream",
			"rsvp_event",
			"sms",
			"publish_actions",
			// Page Permissions
			"manage_pages",
			// Not Documented,
			"user_subscriptions", "friends_subscriptions",
			"user_games_activity", "friends_games_activity", "export_stream",
			"publish_actions"

	};

	Facebook facebook;

	private SharedPreferences mPrefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		/*
		 * Only call authorize if the access_token has expired.
		 */
		if (!FacebookMaster.restoreFacebook(getApplicationContext())) {

			facebook = new Facebook(getString(R.string.FACEBOOK_API_KEY));

			facebook.authorize(this, permissions, new DialogListener() {
				@Override
				public void onComplete(Bundle values) {
					WLog.d(TAG, "Facebook authorize onComplete");
					// SharedPreferences.Editor editor = mPrefs.edit();
					// editor.putString("access_token",
					// facebook.getAccessToken());
					// editor.putLong("access_expires",
					// facebook.getAccessExpires());
					// editor.commit();

					PreferenceMaster.save(getApplicationContext(),
							FacebookMaster.PREF_NAME,
							FacebookMaster.ACCESS_TOKEN,
							facebook.getAccessToken());
					PreferenceMaster.save(getApplicationContext(),
							FacebookMaster.PREF_NAME,
							FacebookMaster.ACCESS_EXPIRES,
							facebook.getAccessExpires());

					onReady();
				}

				@Override
				public void onFacebookError(FacebookError error) {
				}

				@Override
				public void onError(DialogError e) {
				}

				@Override
				public void onCancel() {
					setResult(RESULT_CANCELED);
					finish();
				}
			});
		} else {
			onReady();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		facebook.authorizeCallback(requestCode, resultCode, data);

		onReady();
	}

	public boolean restoreFacebookClient() {
		facebook = new Facebook(getString(R.string.FACEBOOK_API_KEY));
		/*
		 * Get existing access_token if any
		 */
		mPrefs = getPreferences(MODE_PRIVATE);
		String access_token = mPrefs.getString("access_token", null);
		long expires = mPrefs.getLong("access_expires", 0);
		if (access_token != null) {
			facebook.setAccessToken(access_token);
		}
		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}

		return facebook.isSessionValid();
	}

	void onReady() {
		WLog.d(TAG, "Facebook onReady");
		setResult(RESULT_OK);
		finish();
	}

}
