package org.dyndns.warenix.palpal.social.twitter.activity;

import org.dyndns.warenix.palpal.PalPal;
import org.dyndns.warenix.palpaltwitter.R;
import org.dyndns.warenix.tagdef.HashTagDef;
import org.dyndns.warenix.tagdef.HashTagMaster;
import org.dyndns.warenix.util.ToastUtil;
import org.dyndns.warenix.widget.WebImage;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

public class TagDefActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bubble_message);

		setupUI();

		Uri data = getIntent().getData();
		if (data != null) {
			String uri = data.toString();
			String hashtag = uri.substring(10);
			Log.d("palpal", "received hashtag: " + hashtag);
			new FetchTagDefAsyncTask(hashtag).execute();
		}
	}

	void setupUI() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
	}

	private class FetchTagDefAsyncTask extends AsyncTask<Void, Void, Void> {
		String hashtag;
		HashTagDef hashtagDef;

		public FetchTagDefAsyncTask(String hashtag) {
			this.hashtag = hashtag;
		}

		@Override
		protected Void doInBackground(Void... params) {
			hashtagDef = HashTagMaster.queryHashtag(hashtag);
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {

			if (hashtagDef != null) {
				TextView message = (TextView) findViewById(R.id.message);
				message.setText(hashtagDef.getText());

				TextView postDate = (TextView) findViewById(R.id.postDate);
				postDate.setText(hashtagDef.getTime().toLocaleString());

				TextView username = (TextView) findViewById(R.id.username);
				username.setText("TagDef");

				WebImage profileImage = (WebImage) findViewById(R.id.profileImage);
				Twitter twitter = PalPal.getTwitterClient();
				User user;
				try {
					user = twitter.showUser("tagdef");
					profileImage.startLoading(user.getProfileImageURL()
							.toString().replace("_normal", ""));
				} catch (TwitterException e) {
					e.printStackTrace();
				}

			} else {
				ToastUtil
						.showQuickToast(getApplicationContext(), "Not defined");
				finish();
			}
		}

	}
}
