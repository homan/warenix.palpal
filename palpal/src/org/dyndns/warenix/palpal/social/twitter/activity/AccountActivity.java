package org.dyndns.warenix.palpal.social.twitter.activity;

import org.dyndns.warenix.palpal.account.Account;
import org.dyndns.warenix.palpal.account.AccountController;
import org.dyndns.warenix.palpaltwitter.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AccountActivity extends Activity {

	public static final int REQUEST_CODE_AUTHEN_TWITTER = 5000;

	AccountController controller;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_activity);

		setupUI();

		controller = new AccountController(this, R.id.accountList);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_AUTHEN_TWITTER:
			if (resultCode == RESULT_OK) {
				String nick = data.getExtras().getString("nick");
				String socialNetworkName = data.getExtras().getString(
						"socialNetworkName");

				controller.addAccount(new Account(socialNetworkName, nick));

			}
		default:
			super.onActivityResult(requestCode, resultCode, data);
		}

	}

	void setupUI() {
		Button add = (Button) findViewById(R.id.add);
		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivityForResult(new Intent(AccountActivity.this,
						AuthenTwitterActivity.class),
						REQUEST_CODE_AUTHEN_TWITTER);
			}
		});
	}
}
