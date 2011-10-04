package org.dyndns.warenix.palpal.social.twitter.activity;

import org.dyndns.warenix.db.SimpleStorableManager;
import org.dyndns.warenix.palpal.PalPal;
import org.dyndns.warenix.palpal.PalPalPreference;
import org.dyndns.warenix.palpal.message.TwitterDBManager;
import org.dyndns.warenix.palpal.social.twitter.Friend;
import org.dyndns.warenix.palpal.social.twitter.storable.FriendStorable;
import org.dyndns.warenix.palpaltwitter.R;
import org.dyndns.warenix.util.ToastUtil;

import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

public class AuthenTwitterActivity extends Activity {
	ProgressDialog pd;
	WebView browser;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twitter_login);

		browser = (WebView) findViewById(R.id.browser);
		browser.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				Log.d("palpal", "loaded url: " + url);
				browser.pageDown(true);
			}
		});
		browser.requestFocus();

		final EditText pin = (EditText) findViewById(R.id.pin);
		pin.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				browser.pageDown(true);
			}
		});
		Button submit = (Button) findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				pd = ProgressDialog.show(AuthenTwitterActivity.this,
						"Authenticating Twitter", "Please wait");

				Log.d("palpal", "login twitter with "
						+ pin.getText().toString());

				new AuthenTwitterAsyncTask(pin.getText().toString()).execute();
			}
		});

		try {
			onReady();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

	Twitter twitter;
	RequestToken requestToken;
	AccessToken accessToken;

	void onReady() throws TwitterException {
		twitter = null;
		requestToken = null;
		accessToken = null;

		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(PalPal.JTWITTER_OAUTH_KEY,
				PalPal.JTWITTER_OAUTH_SECRET);
		requestToken = twitter.getOAuthRequestToken();

		browser.loadUrl(requestToken.getAuthorizationURL());
	}

	class AuthenTwitterAsyncTask extends
			AsyncTask<Void, Void, TwitterException> {
		String pin;

		public AuthenTwitterAsyncTask(String pin) {
			this.pin = pin;
		}

		@Override
		protected TwitterException doInBackground(Void... arg0) {
			try {
				accessToken = twitter.getOAuthAccessToken(requestToken, pin);
				twitter.setOAuthAccessToken(accessToken);
				PalPal.setTwitter(twitter);

				String[] accessTokens = { accessToken.getToken(),
						accessToken.getTokenSecret() };
				PalPalPreference.writeAccessTokenPreference(
						getApplicationContext(), accessTokens);

				TwitterDBManager db = new TwitterDBManager(
						getApplicationContext());
				SimpleStorableManager simpleStorableDb = new SimpleStorableManager(
						getApplicationContext());

				Log.d("palpal", "fetching followers");
				long cursor = -1;
				PagableResponseList<User> result;

				do {
					result = twitter.getFriendsStatuses(cursor);
					for (User friend : result) {
						String username = friend.getScreenName();
						db.insertFriend(new Friend(username, friend
								.getProfileImageURL().toString(),
								Friend.FRIEND_RELATIONSHIP_FOLLOWING, 0));

						simpleStorableDb
								.insertItem(new FriendStorable(username));
					}
				} while ((cursor = result.getNextCursor()) != 0);

			} catch (TwitterException e) {
				return e;
			}
			return null;
		}

		@Override
		protected void onPostExecute(TwitterException e) {
			pd.dismiss();

			if (e == null) {
				Intent i = new Intent();
				Bundle b = new Bundle();
				b.putString("socialNetworkName", "twitter");
				try {
					b.putString("nick", twitter.verifyCredentials()
							.getScreenName());
				} catch (TwitterException e1) {
					e1.printStackTrace();
				}
				i.putExtras(b);
				setResult(RESULT_OK, i);
				finish();
			} else {
				ToastUtil.showQuickToast(getApplicationContext(),
						"Fail to login");

				try {
					onReady();
				} catch (TwitterException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
