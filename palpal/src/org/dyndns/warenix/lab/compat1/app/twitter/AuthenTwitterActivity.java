package org.dyndns.warenix.lab.compat1.app.twitter;

import org.dyndns.warenix.lab.compat1.R;
import org.dyndns.warenix.lab.compat1.util.Memory;
import org.dyndns.warenix.lab.compat1.util.PreferenceMaster;
import org.dyndns.warenix.palpal.AppActivity;
import org.dyndns.warenix.util.WLog;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

public class AuthenTwitterActivity extends AppActivity {
	private static final String TAG = "AuthenTwitterActivity";
	public static final String PREF_NAME = "twitter_pref";
	public static final String ACCESS_TOKEN0 = "access_token0";
	public static final String ACCESS_TOKEN1 = "access_token1";
	public static final String SCREEN_NAME = "screen_name";

	ProgressDialog pd;
	WebView browser;
	Button submit;
	EditText pin;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twitter_login);
		// if (TwitterMaster.restoreTwitterClient(getApplicationContext())) {
		// finish();
		// }

		browser = (WebView) findViewById(R.id.browser);
		browser.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				WLog.d(TAG, "loaded url: " + url);
				if (url.equals("https://api.twitter.com/oauth/authorize")) {
					showPinInput(true);
				} else {
					showPinInput(false);
				}
			}
		});

		pin = (EditText) findViewById(R.id.pin);
		pin.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				browser.pageDown(true);
			}
		});
		submit = (Button) findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				pd = ProgressDialog.show(AuthenTwitterActivity.this,
						"Authenticating Twitter", "Please wait");

				WLog.d(TAG, "login twitter with " + pin.getText().toString());

				// new
				// AuthenTwitterAsyncTask(pin.getText().toString()).execute();
				new Thread() {
					public void run() {
						try {
							accessToken = twitter.getOAuthAccessToken(
									requestToken, pin.getText().toString());
							twitter.setOAuthAccessToken(accessToken);

							PreferenceMaster.save(getApplicationContext(),
									PREF_NAME, ACCESS_TOKEN0,
									accessToken.getToken());
							PreferenceMaster.save(getApplicationContext(),
									PREF_NAME, ACCESS_TOKEN1,
									accessToken.getTokenSecret());

							nick = twitter.verifyCredentials().getScreenName();
							PreferenceMaster.save(getApplicationContext(),
									PREF_NAME, SCREEN_NAME, nick);

							mTwitterAuthenticatedHandler.sendEmptyMessage(0);
						} catch (TwitterException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}.start();
			}
		});

	}

	public void onStart() {
		super.onStart();
		browser.loadData("<p>Now loading Twitter... Please wait.</p>",
				"text/html", "utf-8");

		// new SetupWebViewAsyncTask().execute();
		browser.requestFocus();

		new Thread() {
			public void run() {
				WLog.d(TAG, "SetupWebViewAsyncTask do in background");
				twitter = new TwitterFactory().getInstance();
				twitter.setOAuthConsumer(
						getResources().getText(R.string.JTWITTER_OAUTH_KEY)
								.toString(),
						getResources().getText(R.string.JTWITTER_OAUTH_SECRET)
								.toString());
				try {
					requestToken = twitter.getOAuthRequestToken();
					authorizationUrl = requestToken.getAuthorizationURL();

					mTwitterPageLoadedHandler.sendEmptyMessage(0);
				} catch (TwitterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}.start();

	}

	Handler mTwitterPageLoadedHandler = new Handler() {
		public void handleMessage(Message msg) {
			browser.loadUrl(authorizationUrl);
		}
	};

	Handler mTwitterAuthenticatedHandler = new Handler() {
		public void handleMessage(Message msg) {
			pd.dismiss();

			Memory.setTwitterClient(twitter);
			Intent i = new Intent();
			Bundle b = new Bundle();
			b.putString("socialNetworkName", "twitter");
			b.putString("nick", nick);
			i.putExtras(b);
			setResult(RESULT_OK, i);
			finish();
		}
	};

	void showPinInput(boolean visible) {
		if (visible) {
			pin.setVisibility(View.VISIBLE);
			submit.setVisibility(View.VISIBLE);
		} else {
			pin.setVisibility(View.GONE);
			submit.setVisibility(View.GONE);
		}
	}

	Twitter twitter;
	RequestToken requestToken;
	AccessToken accessToken;
	String authorizationUrl;
	String nick;

	void onReady() throws TwitterException {
		twitter = null;
		requestToken = null;
		accessToken = null;

		browser.loadUrl(requestToken.getAuthorizationURL());
	}

	class SetupWebViewAsyncTask extends AsyncTask<Void, Void, Void> {
		String authorizationUrl;

		@Override
		protected Void doInBackground(Void... arg0) {
			WLog.d(TAG, "SetupWebViewAsyncTask do in background");
			twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(
					getResources().getText(R.string.JTWITTER_OAUTH_KEY)
							.toString(),
					getResources().getText(R.string.JTWITTER_OAUTH_SECRET)
							.toString());
			try {
				requestToken = twitter.getOAuthRequestToken();
				authorizationUrl = requestToken.getAuthorizationURL();

			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(Void v) {
			WLog.d(TAG, "trying to authen twitter at " + authorizationUrl);
			browser.loadUrl(authorizationUrl);
		}

	}

	class AuthenTwitterAsyncTask extends
			AsyncTask<Void, Void, TwitterException> {
		String pin;
		String nick;

		public AuthenTwitterAsyncTask(String pin) {
			this.pin = pin;
		}

		@Override
		protected TwitterException doInBackground(Void... arg0) {
			try {
				accessToken = twitter.getOAuthAccessToken(requestToken, pin);
				twitter.setOAuthAccessToken(accessToken);

				PreferenceMaster.save(getApplicationContext(), PREF_NAME,
						ACCESS_TOKEN0, accessToken.getToken());
				PreferenceMaster.save(getApplicationContext(), PREF_NAME,
						ACCESS_TOKEN1, accessToken.getTokenSecret());

				nick = twitter.verifyCredentials().getScreenName();
				PreferenceMaster.save(getApplicationContext(), PREF_NAME,
						SCREEN_NAME, nick);

			} catch (TwitterException e) {
				return e;
			}
			return null;
		}

		@Override
		protected void onPostExecute(TwitterException e) {
			pd.dismiss();

			if (e == null) {
				Memory.setTwitterClient(twitter);
				Intent i = new Intent();
				Bundle b = new Bundle();
				b.putString("socialNetworkName", "twitter");
				b.putString("nick", nick);
				i.putExtras(b);
				setResult(RESULT_OK, i);
				finish();
			} else {
				WLog.d("warenix", "Fail to login");

				try {
					onReady();
				} catch (TwitterException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
