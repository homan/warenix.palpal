package org.dyndns.warenix.palpal.social.twitter;

import java.lang.ref.SoftReference;
import java.net.URI;
import java.util.HashMap;

import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.widget.WebImage;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.User;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.TextView;

public class PersonActivity extends PalPalTwitterActivity {

	public static String BUNDLE_SCREEN_NAME;
	// data
	String screenName;

	// ui
	WebImage profileImage;
	TextView username;
	TextView statusCounter;
	TextView followerCounter;
	TextView followingCounter;
	TextView favouriteCounter;
	TextView website;
	TextView description;

	void setupUI() {
		setContentView(R.layout.twitter_person);

		username = (TextView) findViewById(R.id.username);
		profileImage = (WebImage) findViewById(R.id.profileImage);
		statusCounter = (TextView) findViewById(R.id.statusCounter);
		followerCounter = (TextView) findViewById(R.id.followerCounter);
		followingCounter = (TextView) findViewById(R.id.followingCounter);
		favouriteCounter = (TextView) findViewById(R.id.favouriteCounter);
		website = (TextView) findViewById(R.id.website);
		description = (TextView) findViewById(R.id.description);

	}

	@Override
	void onTwitterClientReady(Twitter twitter,
			HashMap<String, SoftReference<Bitmap>> imagePool) {
		screenName = getIntent().getExtras().getString(BUNDLE_SCREEN_NAME);
		if (screenName == null) {
			// user:// protocol
			Uri data = getIntent().getData();
			if (data != null) {
				String uri = data.toString();
				// uri = user://warenix
				screenName = uri.substring(uri.indexOf("@") + 1);
			} else {
				screenName = "warenix";
			}
		}

		User user = twitter.getUser(screenName);

		String normalProfileImageUrl = user.profileImageUrl.toString();
		String bigProfileImageUrl = normalProfileImageUrl
				.replace("_normal", "");
		profileImage.startLoading(bigProfileImageUrl, imagePool);

		username.setText(screenName);
		URI url = user.getWebsite();
		website.setText(url == null ? "N/A" : url.toString());
		statusCounter.setText(user.statusesCount + "");
		favouriteCounter.setText(user.getFavoritesCount() + "");
		followerCounter.setText(user.getFollowersCount() + "");
		followingCounter.setText(user.getFriendsCount() + "");

		description.setText(user.description);

	}
}
