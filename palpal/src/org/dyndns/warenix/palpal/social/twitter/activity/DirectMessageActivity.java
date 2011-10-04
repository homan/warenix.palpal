package org.dyndns.warenix.palpal.social.twitter.activity;

import org.dyndns.warenix.palpal.twitter.directMessage.DirectMessageListController;
import org.dyndns.warenix.palpaltwitter.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DirectMessageActivity extends Activity {

	public static final String BUNDLE_PAGE_NO = "page_no";
	DirectMessageListController controller;

	// BubbleMessageListAdapter adapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.conversation_activity);

		Log.d("palpal", "onCreate()");

		controller = new DirectMessageListController(this, R.id.listView1);

		Button add = (Button) findViewById(R.id.add);
		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						ComposeMessageActivity.class));
			}
		});

		onReady();
	}

	protected void onResume() {
		super.onResume();
		Log.d("palpal", "onResume()");
	}

	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.d("palpal", "onNewIntent()");
		onReady();
	}

	void onReady() {
		Log.d("palpal", "onReady()");
		controller.refresh(1);
	}

	protected void onPause() {
		super.onPause();
		Log.d("palpal", "onPause()");
	}

	protected void onStop() {
		super.onStop();
		controller.stop();
	}
}