package org.dyndns.warenix.palpal.social.twitter.activity;

import java.util.ArrayList;
import java.util.List;

import org.dyndns.warenix.palpal.PalPal;
import org.dyndns.warenix.palpal.bubbleMessage.BubbleMessage;
import org.dyndns.warenix.palpal.message.MessageDBManager;
import org.dyndns.warenix.palpal.social.twitter.TwitterBubbleMessage;
import org.dyndns.warenix.palpal.social.twitter.stream.SearchController;
import org.dyndns.warenix.palpal.social.twitter.stream.StatusStreamController;
import org.dyndns.warenix.palpaltwitter.R;
import org.dyndns.warenix.pattern.baseListView.ListViewController;
import org.dyndns.warenix.pattern.baseListView.ListViewItem;

import twitter4j.ConnectionLifeCycleListener;
import twitter4j.DirectMessage;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends Activity implements UserStreamListener {

	public static final String BUNDLE_SEARCH_TYPE = "search_type";
	public static final String SEARCH_TYPE_LOCAL = "local";
	public static final String SEARCH_TYPE_USER_HOME_TIMELINE = "user_home_timeline";
	public static final String SEARCH_TYPE_REALTIME = "realtime";
	public static final String SEARCH_TYPE_NEAR = "near";
	ListViewController controller;
	Button refresh;

	ProgressDialog progressDialog;

	boolean isStreamingStarted = false;

	final int MESSAGE_QUOTA = 10;
	int quota = MESSAGE_QUOTA;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_list_activity);
		Log.d("palpal", "onCreate()");

		Button add = (Button) findViewById(R.id.add);
		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						ComposeMessageActivity.class));
			}
		});
		handleIntent(getIntent());

	}

	@Override
	public void onDestroy() {
		super.onStop();

		if (controller != null) {
			if (controller instanceof StatusStreamController) {
				((StatusStreamController) controller).stopStreaming();
			}
		}
	}

	private void handleIntent(Intent intent) {

		// Get the intent, verify the action and get the query
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

			String searchType = intent
					.getStringExtra(SearchManager.EXTRA_DATA_KEY);
			String keyword = intent.getStringExtra(SearchManager.QUERY);

			if (searchType != null && searchType.equals(SEARCH_TYPE_REALTIME)) {
				TextView activityTitle = (TextView) findViewById(R.id.activityTitle);
				activityTitle.setText("Realtime:" + keyword);

				progressDialog = new ProgressDialog(this);
				progressDialog.setTitle("");
				progressDialog.setMessage("connecting... please wait");
				progressDialog.show();

				controller = new StatusStreamController(this, R.id.listView1);
				((StatusStreamController) controller).startKeywordStreaming(
						keyword, connectionLiftCycleListener, this);

				refresh = (Button) findViewById(R.id.refresh);
				refresh.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						quota = MESSAGE_QUOTA;
						// refresh.setVisibility(View.GONE);
						((StatusStreamController) controller).clearStatus();
					}
				});
			} else if (searchType != null
					&& searchType.equals(SEARCH_TYPE_NEAR)) {
				TextView activityTitle = (TextView) findViewById(R.id.activityTitle);
				activityTitle.setText("Near Hong Kong");

				progressDialog = new ProgressDialog(this);
				progressDialog.setTitle("");
				progressDialog.setMessage("connecting... please wait");
				progressDialog.show();

				controller = new StatusStreamController(this, R.id.listView1);
				((StatusStreamController) controller).startNearStreaming(
						keyword, connectionLiftCycleListener, this);

				refresh = (Button) findViewById(R.id.refresh);
				refresh.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						quota = MESSAGE_QUOTA;
						// refresh.setVisibility(View.GONE);
						((StatusStreamController) controller).clearStatus();
					}
				});
			} else {
				TextView activityTitle = (TextView) findViewById(R.id.activityTitle);

				progressDialog = new ProgressDialog(this);
				progressDialog.setTitle("");
				progressDialog.setMessage("searching... please wait");
				progressDialog.show();

				refresh = (Button) findViewById(R.id.refresh);
				refresh.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						handleIntent(getIntent());
					}

				});

				if (searchType == null) {
					activityTitle.setText("" + keyword);
					new PublicSearchAsyncTask(keyword).execute();
				} else if (searchType.equals(SEARCH_TYPE_USER_HOME_TIMELINE)) {
					activityTitle.setText("User:" + keyword);
					new UserSearchAsyncTask(keyword).execute();
				} else if (searchType.equals(SEARCH_TYPE_LOCAL)) {
					activityTitle.setText("Local:" + keyword);
					new LocalSearchAsyncTask(keyword).execute();
				} else {
					activityTitle.setText("Search ?");
					progressDialog.cancel();
				}
				controller = new SearchController(this, R.id.listView1);
			}

		}
	}

	protected void onResume() {
		super.onResume();
		Log.d("palpal", "onResume()");
	}

	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.d("palpal", "onNewIntent()");
		// setIntent(intent);
		handleIntent(intent);
	}

	void onReady() {
		Log.d("palpal", "onReady()");
	}

	protected void onPause() {
		super.onPause();
		Log.d("palpal", "onPause()");
		// UpdateMessageService.stop(getApplicationContext());
	}

	class PublicSearchAsyncTask extends
			AsyncTask<Void, Void, ArrayList<ListViewItem>> {
		String keyword;

		public PublicSearchAsyncTask(String keyword) {
			this.keyword = keyword;
		}

		@Override
		protected ArrayList<ListViewItem> doInBackground(Void... params) {
			try {
				Twitter twitter = PalPal.getTwitterClient();
				Query query = new Query(keyword);
				QueryResult queryResult = twitter.search(query);
				List<Tweet> tweetList = queryResult.getTweets();

				ArrayList<ListViewItem> messageList = new ArrayList<ListViewItem>();

				for (Tweet tweet : tweetList) {

					TwitterBubbleMessage message = new TwitterBubbleMessage(
							tweet.getFromUser(), tweet.getText(), twitter
									.showUser(tweet.getFromUser())
									.getProfileImageURL().toString(),
							new java.sql.Date(tweet.getCreatedAt().getTime()),
							"twitter", tweet.getId() + "");
					messageList.add(message);
				}

				return messageList;
			} catch (TwitterException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(ArrayList<ListViewItem> result) {
			((SearchController) controller).displayList(result);
			progressDialog.cancel();
		}
	}

	class LocalSearchAsyncTask extends
			AsyncTask<Void, Void, ArrayList<ListViewItem>> {
		String keyword;

		public LocalSearchAsyncTask(String keyword) {
			this.keyword = keyword;
		}

		@Override
		protected ArrayList<ListViewItem> doInBackground(Void... params) {
			MessageDBManager messageDb = new MessageDBManager(
					getApplicationContext());

			ArrayList<ListViewItem> messageList = new ArrayList<ListViewItem>();
			ArrayList<BubbleMessage> resultList = messageDb
					.getMessageListByKeyword(999, -1, keyword);

			for (BubbleMessage message : resultList) {
				messageList.add(message);
			}

			return messageList;
		}

		protected void onPostExecute(ArrayList<ListViewItem> result) {
			((SearchController) controller).displayList(result);
			progressDialog.cancel();
		}
	}

	class UserSearchAsyncTask extends
			AsyncTask<Void, Void, ArrayList<ListViewItem>> {
		String screenName;

		public UserSearchAsyncTask(String screenName) {
			this.screenName = screenName;
		}

		@Override
		protected ArrayList<ListViewItem> doInBackground(Void... params) {
			try {
				Twitter twitter = PalPal.getTwitterClient();
				ResponseList<twitter4j.Status> statusList = twitter
						.getUserTimeline(screenName);
				// Query query = new Query(screenName);
				// QueryResult queryResult = twitter.search(query);
				// List<Tweet> tweetList = queryResult.getTweets();

				ArrayList<ListViewItem> messageList = new ArrayList<ListViewItem>();

				for (twitter4j.Status status : statusList) {

					TwitterBubbleMessage message = new TwitterBubbleMessage(
							screenName, status.getText(), twitter
									.showUser(screenName).getProfileImageURL()
									.toString(), new java.sql.Date(status
									.getCreatedAt().getTime()), "twitter",
							status.getId() + "");
					messageList.add(message);
				}

				return messageList;
			} catch (TwitterException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(ArrayList<ListViewItem> result) {
			((SearchController) controller).displayList(result);
			progressDialog.cancel();
		}
	}

	ConnectionLifeCycleListener connectionLiftCycleListener = new ConnectionLifeCycleListener() {

		@Override
		public void onCleanUp() {
		}

		@Override
		public void onConnect() {
			SearchActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					// TextView connectionStatus = (TextView)
					// findViewById(R.id.connectionStatus);
					// connectionStatus.setText("connected");

					progressDialog.cancel();
					Toast.makeText(SearchActivity.this, "connected", 1000)
							.show();
				}
			});
		}

		@Override
		public void onDisconnect() {
			SearchActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					if (!isStreamingStarted) {
						// TextView connectionStatus = (TextView)
						// findViewById(R.id.connectionStatus);
						// connectionStatus.setText("disconnected");

						Toast.makeText(SearchActivity.this, "disconnected",
								1000).show();
					}
				}
			});
		}

	};

	@Override
	public void onDeletionNotice(StatusDeletionNotice arg0) {

	}

	@Override
	public void onScrubGeo(long arg0, long arg1) {

	}

	@Override
	public void onStatus(Status status) {
		if (quota > 0) {
			((StatusStreamController) controller).addStatusToBuffer(status);
			((StatusStreamController) controller).showBufferredStatus();

			--quota;
			if (quota == 0) {
				refresh.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onTrackLimitationNotice(int arg0) {

	}

	@Override
	public void onException(Exception arg0) {

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

}