package org.dyndns.warenix.palpal.social.twitter.activity;

import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;

import org.dyndns.warenix.palpal.PalPal;
import org.dyndns.warenix.palpaltwitter.R;
import org.dyndns.warenix.util.ToastUtil;
import org.dyndns.warenix.widget.WebImage;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

public class PersonActivity extends Activity {

	public static String BUNDLE_SCREEN_NAME = "screen_name";
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
	WebImage starChart;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_activity);

		setupUI();
		onTwitterClientReady(PalPal.getTwitterClient(), null);
	}

	protected void onDestroy() {
		super.onDestroy();

		if (profileImage != null) {
			profileImage.recycleBitmap();
		}
		if (starChart != null) {
			starChart.recycleBitmap();
		}
	}

	void setupUI() {
		setContentView(R.layout.twitter_person);

		username = (TextView) findViewById(R.id.username);
		profileImage = (WebImage) findViewById(R.id.profileImage);
		// statusCounter = (TextView) findViewById(R.id.statusCounter);
		// followerCounter = (TextView) findViewById(R.id.followerCounter);
		// followingCounter = (TextView) findViewById(R.id.followingCounter);
		// favouriteCounter = (TextView) findViewById(R.id.favouriteCounter);
		website = (TextView) findViewById(R.id.website);
		description = (TextView) findViewById(R.id.description);
		starChart = (WebImage) findViewById(R.id.starChart);

	}

	User user;

	void onTwitterClientReady(Twitter twitter,
			HashMap<String, SoftReference<Bitmap>> imagePool) {
		screenName = getIntent().getExtras().getString(BUNDLE_SCREEN_NAME);
		if (screenName == null) {
			Uri data = getIntent().getData();
			if (data != null) {
				String uri = data.toString();
				screenName = uri.substring(7);
			} else {
				screenName = "warenix";
			}
		}

		new FetchProfileAsyncTask().execute();

	}

	class FetchProfileAsyncTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			Twitter twitter = PalPal.getTwitterClient();
			try {
				user = twitter.showUser(screenName);
			} catch (TwitterException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Void v) {
			if (user == null) {
				ToastUtil
						.showQuickToast(getApplicationContext(), String.format(
								"Fail to show profile of %s", screenName));
				return;
			}
			String normalProfileImageUrl = user.getProfileImageURL().toString();
			String bigProfileImageUrl = normalProfileImageUrl.replace(
					"_normal", "");
			profileImage.startLoading(bigProfileImageUrl);

			username.setText(screenName);
			URL url = user.getURL();
			website.setText(url == null ? "N/A" : url.toString());
			Linkify.addLinks(website, Linkify.WEB_URLS);
			int statusesCount = user.getStatusesCount();
			int favouriteCount = user.getFavouritesCount();
			int followersCount = user.getFollowersCount();
			int friendsCount = user.getFriendsCount();

			int maxCount = statusesCount;
			if (favouriteCount > maxCount) {
				maxCount = favouriteCount;
			}
			if (followersCount > maxCount) {
				maxCount = followersCount;
			}
			if (friendsCount > maxCount) {
				maxCount = friendsCount;
			}
			double ratio = 100.0 / maxCount;
			// statusCounter.setText(statusesCount + "");
			// favouriteCounter.setText(favouriteCount + "");
			// followerCounter.setText(followersCount + "");
			// followingCounter.setText(friendsCount + "");

			starChart.startLoading(getStarChart((int) (statusesCount * ratio),
					(int) (favouriteCount * ratio),
					(int) (followersCount * ratio),
					(int) (friendsCount * ratio), statusesCount,
					favouriteCount, followersCount, friendsCount));

			description.setText(user.getDescription());
		}
	}

	String getStarChart(int statusesCount, int favouritesCount,
			int followersCount, int friendsCount, int statusesLabel,
			int favouritesLabel, int followersLabel, int friendsLabel) {
		String chartURL = "https://chart.googleapis.com/chart?cht=r"
				+ "&chs=378x378&chd=t:%d,%d,%d,%d,%d"
				+ "&chco=00CCFF,FF9900&chls=2.0,4.0,0.0"
				+ "&chxt=x&chxl=0:|statuses(%d)|followers(%d)|favourites(%d)|friends(%d)&chxr=0,0.0,360.0"
				// transparent background
				+ "&chf=bg,s,00000000" + "&chxs=0,FFFFFF,14,0";

		return String.format(chartURL, statusesCount, followersCount,
				favouritesCount, friendsCount, statusesCount, statusesLabel,
				followersLabel, favouritesLabel, friendsLabel);
	}
}
