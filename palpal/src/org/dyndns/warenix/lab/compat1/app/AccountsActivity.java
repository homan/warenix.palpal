package org.dyndns.warenix.lab.compat1.app;

import org.dyndns.warenix.lab.compat1.R;
import org.dyndns.warenix.lab.compat1.app.facebook.AuthenFacebookActivity;
import org.dyndns.warenix.lab.compat1.app.twitter.AuthenTwitterActivity;
import org.dyndns.warenix.mission.facebook.util.FacebookMaster;
import org.dyndns.warenix.mission.twitter.util.TwitterMaster;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Gallery;

import com.example.android.actionbarcompat.ActionBarActivity;

public class AccountsActivity extends ActionBarActivity {

	public static String BUNDLE_MESSAGE_OBJECT = "messageObject";
	public static String BUNDLE_FACEBOOK_GRAPH_ID = "facebookGraphId";

	public static String BUNDLE_LIST_VIEW_ADAPTER = "ListViewAdapter";
	public static final int PARAM_TWITTER_CONVERSATION_ADAPTER = 1;
	public static final int PARAM_FACEBOOK_POST_ADAPTER = 2;

	ListViewAdapter listViewAdapter;

	Button compose;
	Gallery imageQueue;
	AutoCompleteTextView commentTextView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accounts);

		setTitle("Accounts");

		Button accountFacebook = (Button) findViewById(R.id.account_facebook);
		accountFacebook.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getApplicationContext(),
						AuthenFacebookActivity.class);
				startActivityForResult(intent, 2);
			}
		});
		accountFacebook.setEnabled(!FacebookMaster
				.restoreFacebook(getApplicationContext()));

		Button accountTwitter = (Button) findViewById(R.id.account_twitter);
		accountTwitter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getApplicationContext(),
						AuthenTwitterActivity.class);
				startActivityForResult(intent, 1);
			}
		});
		accountTwitter.setEnabled(!TwitterMaster
				.restoreTwitterClient(getApplicationContext()));
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		// menuInflater.inflate(R.menu.reply_menu, menu);

		// Calling super after populating the menu is necessary here to ensure
		// that the
		// action bar helpers have a chance to handle this event.
		return super.onCreateOptionsMenu(menu);
	}

}
