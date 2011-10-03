package org.dyndns.warenix.palpal.social.twitter;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.palpal.social.twitter.stream.StatusStreamController;

import twitter4j.ConnectionLifeCycleListener;
import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.Twitter;
import twitter4j.TwitterStream;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;
import twitter4j.auth.AccessToken;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class TwitterStreamActivity extends PalPalTwitterActivity implements
		UserStreamListener {
	StatusStreamController listController;
	Button displayButton;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("palpal", "onCreate");

		// setupUI();
		// onReady();
		//
		// Log.i("palpal", "play");
	}

	protected void onPause() {
		Log.i("palpal", "onPause");
		if (listController != null) {
			listController.stopStreaming();
		}

		ToggleButton onairButton = (ToggleButton) findViewById(R.id.onairButton);
		if (isStreamingStarted) {
			onairButton.toggle();
		}
		isStreamingStarted = false;

		super.onPause();
	}

	protected void onResume() {
		Log.i("palpal", "onResume");
		// startStreaming(streamingMode);

		super.onResume();
	}

	@Override
	public void onBlock(User arg0, User arg1) {

	}

	@Override
	public void onDeletionNotice(long arg0, long arg1) {

	}

	@Override
	public void onDirectMessage(DirectMessage arg0) {

	}

	@Override
	public void onFavorite(User arg0, User arg1, Status arg2) {

	}

	@Override
	public void onFollow(User arg0, User arg1) {

	}

	@Override
	public void onFriendList(long[] arg0) {

	}

	@Override
	public void onRetweet(User arg0, User arg1, Status arg2) {

	}

	@Override
	public void onUnblock(User arg0, User arg1) {

	}

	@Override
	public void onUnfavorite(User arg0, User arg1, Status arg2) {

	}

	@Override
	public void onUserListCreation(User arg0, UserList arg1) {

	}

	@Override
	public void onUserListDeletion(User arg0, UserList arg1) {

	}

	@Override
	public void onUserListMemberAddition(User arg0, User arg1, UserList arg2) {

	}

	@Override
	public void onUserListMemberDeletion(User arg0, User arg1, UserList arg2) {

	}

	@Override
	public void onUserListSubscription(User arg0, User arg1, UserList arg2) {

	}

	@Override
	public void onUserListUnsubscription(User arg0, User arg1, UserList arg2) {

	}

	@Override
	public void onUserListUpdate(User arg0, UserList arg1) {

	}

	@Override
	public void onUserProfileUpdate(User arg0) {

	}

	@Override
	public void onDeletionNotice(StatusDeletionNotice arg0) {

	}

	@Override
	public void onScrubGeo(long arg0, long arg1) {

	}

	@Override
	public void onStatus(Status status) {
		// timelineAdapter.addStatus(status);
		listController.addStatusToBuffer(status);

		runOnUiThread(new Runnable() {
			public void run() {
				if (displayButton.getVisibility() != View.VISIBLE) {
					displayButton.setVisibility(View.VISIBLE);
				}
				displayButton.setText(listController.getBufferedStatusSize()
						+ "+");
			}
		});
	}

	@Override
	public void onTrackLimitationNotice(int arg0) {

	}

	@Override
	public void onException(Exception arg0) {

	}

	void setupUI() {
		setContentView(R.layout.twitter_stream);

		ListView timelineListView = (ListView) findViewById(R.id.timeline);
		timelineListView.setDivider(null);

		displayButton = (Button) findViewById(R.id.displayButton);
		displayButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				listController.showBufferredStatus();
				displayButton.setVisibility(View.GONE);
			}

		});

		Button clearButton = (Button) findViewById(R.id.clearButton);
		clearButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				listController.clearStatus();
			}

		});

		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		radioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						Log.d("palpal", "checked changed " + checkedId);
						streamingMode = checkedId;

						if (isStreamingStarted) {
							startStreaming(checkedId);
						}
					}
				});

		ToggleButton onairButton = (ToggleButton) findViewById(R.id.onairButton);
		onairButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Log.d("palpal", "toggle to " + isChecked + " streaming mode "
						+ streamingMode);

				isStreamingStarted = isChecked;
				if (isChecked) {
					startStreaming(streamingMode);
				} else {
					stopStreaming();
				}

			}
		});
	}

	void onReady() {
		Log.i("palpal", "onReady");

		listController = new StatusStreamController(this, R.id.timeline);
	}

	long[] followArray;
	AccessToken accessToken;
	boolean isStreamingStarted = false;
	int streamingMode = R.id.radio0;

	Twitter twitter;
	TwitterStream twitterStream;

	/* */
	public void startStreaming(int checkedId) {
		if (isStreamingStarted) {
			TextView connectionStatus = (TextView) findViewById(R.id.connectionStatus);
			connectionStatus.setText("connecting...");

			listController.startStreaming(checkedId,
					connectionLiftCycleListener, this);
		}
	}

	public void stopStreaming() {
		Log.d("palpal", "stop streaming");
		TextView connectionStatus = (TextView) findViewById(R.id.connectionStatus);
		connectionStatus.setText("stopping...");

		listController.stopStreaming();
	}

	ConnectionLifeCycleListener connectionLiftCycleListener = new ConnectionLifeCycleListener() {

		@Override
		public void onCleanUp() {
		}

		@Override
		public void onConnect() {
			TwitterStreamActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					TextView connectionStatus = (TextView) findViewById(R.id.connectionStatus);
					connectionStatus.setText("connected");

					Toast.makeText(TwitterStreamActivity.this, "connected",
							1000).show();
				}
			});
		}

		@Override
		public void onDisconnect() {
			TwitterStreamActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					if (!isStreamingStarted) {
						TextView connectionStatus = (TextView) findViewById(R.id.connectionStatus);
						connectionStatus.setText("disconnected");

						Toast.makeText(TwitterStreamActivity.this,
								"disconnected", 1000).show();
					}
				}
			});
		}

	};

	@Override
	void onTwitterClientReady(winterwell.jtwitter.Twitter twitter,
			HashMap<String, SoftReference<Bitmap>> imagePool) {
		onReady();
	}
}
