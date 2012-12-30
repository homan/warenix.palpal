package org.dyndns.warenix.palpal.app;

import java.io.IOException;
import java.net.MalformedURLException;

import org.dyndns.warenix.lab.compat1.util.Memory;
import org.dyndns.warenix.mission.facebook.util.FacebookMaster;
import org.dyndns.warenix.mission.twitter.util.TwitterMaster;
import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.palpal.app.facebook.AuthenFacebookActivity;
import org.dyndns.warenix.palpal.app.twitter.AuthenTwitterActivity;
import org.dyndns.warenix.util.WLog;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.facebook.android.Facebook;

public class AccountsActivity extends AppActivity {
	private static final String TAG = "AccountsActivity";

	public static String BUNDLE_MESSAGE_OBJECT = "messageObject";
	public static String BUNDLE_FACEBOOK_GRAPH_ID = "facebookGraphId";

	public static String BUNDLE_LIST_VIEW_ADAPTER = "ListViewAdapter";
	public static final int PARAM_TWITTER_CONVERSATION_ADAPTER = 1;
	public static final int PARAM_FACEBOOK_POST_ADAPTER = 2;

	Button mAccountFacebook;
	Button mAccountTwitter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accounts);

		setTitle("Accounts");

		mAccountFacebook = (Button) findViewById(R.id.account_facebook);
		mAccountFacebook.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onFacebookClicked();
			}
		});

		mAccountTwitter = (Button) findViewById(R.id.account_twitter);
		mAccountTwitter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onTwitterClicked();
			}
		});

		Button done = (Button) findViewById(R.id.done);
		done.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AccountsActivity.this,
						SplashActivity.class);
				startActivity(intent);

				finish();
			}
		});
	}

	public void onResume() {
		super.onResume();
		updateButtonsText();
	}

	Handler mUpdateButtonHandler = new Handler() {
		public void handleMessage(Message msg) {
			mAccountFacebook
					.setText(FacebookMaster
							.restoreFacebook(getApplicationContext()) ? "Remove Facebook"
							: "Add Facebook");

			mAccountTwitter
					.setText(TwitterMaster
							.restoreTwitterClient(getApplicationContext()) ? "Remove Twitter"
							: "Add Twitter");
		}
	};

	private void updateButtonsText() {
		mUpdateButtonHandler.sendEmptyMessage(0);
	}

	private void onFacebookClicked() {
		if (FacebookMaster.restoreFacebook(getApplicationContext())) {

			new Thread() {
				public void run() {
					try {
						Facebook facebook = Memory.getFacebookClient();
						facebook.logout(getApplicationContext());
						WLog.d(TAG,
								String.format("logout facebook successfully"));
						FacebookMaster.removeFacebook(getApplicationContext());
						WLog.d(TAG, String
								.format("remove facebook token successfully"));
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						updateButtonsText();
					}
				}
			}.start();
		} else {
			Intent intent = new Intent(getApplicationContext(),
					AuthenFacebookActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
			startActivityForResult(intent, 2);
		}
	}

	private void onTwitterClicked() {

		if (TwitterMaster.restoreTwitterClient(getApplicationContext())) {
			new Thread() {
				public void run() {
					TwitterMaster.removeTwitterClient(getApplicationContext());
					WLog.d(TAG,
							String.format("remove facebook token successfully"));
					updateButtonsText();
				}
			}.start();
		} else {
			Intent intent = new Intent(getApplicationContext(),
					AuthenTwitterActivity.class);
			startActivityForResult(intent, 1);
		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getSupportMenuInflater();
		// menuInflater.inflate(R.menu.reply_menu, menu);

		// Calling super after populating the menu is necessary here to ensure
		// that the
		// action bar helpers have a chance to handle this event.
		return super.onCreateOptionsMenu(menu);
	}

}
