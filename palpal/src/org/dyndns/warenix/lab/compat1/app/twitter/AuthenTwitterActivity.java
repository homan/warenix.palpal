package org.dyndns.warenix.lab.compat1.app.twitter;

import org.dyndns.warenix.lab.compat1.R;
import org.dyndns.warenix.lab.compat1.util.Memory;
import org.dyndns.warenix.lab.compat1.util.PreferenceMaster;
import org.dyndns.warenix.mission.twitter.util.TwitterMaster;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
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

import com.example.android.actionbarcompat.ActionBarActivity;

public class AuthenTwitterActivity extends ActionBarActivity {
	public static final String PREF_NAME = "twitter_pref";
	public static final String ACCESS_TOKEN0 = "access_token0";
	public static final String ACCESS_TOKEN1 = "access_token1";

	ProgressDialog pd;
	WebView browser;
	Button submit;
	EditText pin;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twitter_login);
		if (TwitterMaster.restoreTwitterClient(getApplicationContext())) {
			finish();
		}

		browser = (WebView) findViewById(R.id.browser);
		browser.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				Log.d("palpal", "loaded url: " + url);
				if (url.equals("https://api.twitter.com/oauth/authorize")) {
					showPinInput(true);
				} else {
					showPinInput(false);
				}
			}
		});
		browser.requestFocus();

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

				Log.d("palpal", "login twitter with "
						+ pin.getText().toString());

				new AuthenTwitterAsyncTask(pin.getText().toString()).execute();
			}
		});

		browser.loadData("<p>Now loading Twitter... Please wait.</p>",
				"text/html", "utf-8");

		new SetupWebViewAsyncTask().execute();
	}

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
			Log.d("palpal", "trying to authen twitter at " + authorizationUrl);
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
				Log.d("warenix", "Fail to login");

				try {
					onReady();
				} catch (TwitterException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
