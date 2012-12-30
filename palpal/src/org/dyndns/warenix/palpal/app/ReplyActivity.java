package org.dyndns.warenix.palpal.app;

import org.dyndns.warenix.lab.compat1.util.AndroidUtil;
import org.dyndns.warenix.lab.compat1.util.IndicatorTokenizer;
import org.dyndns.warenix.lab.compat1.util.Memory;
import org.dyndns.warenix.lab.taskservice.TaskService;
import org.dyndns.warenix.mission.facebook.FacebookPostAdapter;
import org.dyndns.warenix.mission.facebook.backgroundtask.CommentPostBackgroundTask;
import org.dyndns.warenix.mission.twitter.TwitterConversationAdapter;
import org.dyndns.warenix.mission.twitter.TwitterDirectMessageAdapter;
import org.dyndns.warenix.mission.twitter.TwitterUserAutoCompleteAdapter;
import org.dyndns.warenix.mission.twitter.TwitterUserFilterQueryProvider;
import org.dyndns.warenix.mission.twitter.backgroundtask.ReplyDirectMessageBackgroundTask;
import org.dyndns.warenix.mission.twitter.backgroundtask.ReplyStatusBackgroundTask;
import org.dyndns.warenix.mission.twitter.util.TwitterMaster;
import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;
import org.dyndns.warenix.util.WLog;

import twitter4j.Twitter;
import twitter4j.UserMentionEntity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class ReplyActivity extends AppActivity {
	private static final String TAG = "ReplyActivity";
	public static String BUNDLE_MESSAGE_OBJECT = "messageObject";
	public static String BUNDLE_FACEBOOK_GRAPH_ID = "facebookGraphId";

	public static String BUNDLE_LIST_VIEW_ADAPTER = "ListViewAdapter";
	public static final int PARAM_TWITTER_CONVERSATION_ADAPTER = 1;
	public static final int PARAM_FACEBOOK_POST_ADAPTER = 2;
	public static final int PARAM_TWITTER_DIRECT_MESSAGE_ADAPTER = 3;

	ListViewAdapter listViewAdapter;

	Button compose;
	Gallery imageQueue;
	MultiAutoCompleteTextView commentTextView;

	int adapterType;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reply_post_detail);

		setTitle("Reply");
		AndroidUtil.hideSoftwareKeyboard(this);

		commentTextView = (MultiAutoCompleteTextView) findViewById(R.id.comment);
		commentTextView.setTokenizer(new IndicatorTokenizer('@'));

		// final String[] COUNTRIES = new String[] { "wayway",
		// "France", "Italy", "Germany", "warenix", "wong" };
		// ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		// android.R.layout.simple_dropdown_item_1line, COUNTRIES);

		TwitterUserAutoCompleteAdapter adapter = new TwitterUserAutoCompleteAdapter(
				this, null);
		adapter.setFilterQueryProvider(new TwitterUserFilterQueryProvider());
//		commentTextView.setAdapter(adapter);

		ListView listView = (ListView) findViewById(android.R.id.list);

		Bundle bundle = getIntent().getExtras();

		adapterType = bundle.getInt(BUNDLE_LIST_VIEW_ADAPTER);
		switch (adapterType) {
		case PARAM_TWITTER_CONVERSATION_ADAPTER:
			twitter4j.Status twitterMessageObject = (twitter4j.Status) bundle
					.get(BUNDLE_MESSAGE_OBJECT);
			listViewAdapter = new TwitterConversationAdapter(this, listView,
					twitterMessageObject);

			UserMentionEntity[] userMentionEntity = twitterMessageObject
					.getUserMentionEntities();
			try {
				Twitter twitter = Memory.getTwitterClient();
				// String myScreenname = twitter.getScreenName();
				String myScreenname = TwitterMaster
						.getScreenName(getApplicationContext());

				// default reply to poster
				String message = "@"
						+ twitterMessageObject.getUser().getScreenName() + " ";

				// // remove mentions self
				UserMentionEntity mentionedScreenname = null;
				for (int i = 0; i < userMentionEntity.length; ++i) {
					mentionedScreenname = userMentionEntity[i];
					if (!mentionedScreenname.getScreenName().equals(
							myScreenname)) {
						message += "@" + mentionedScreenname.getScreenName()
								+ " ";
					}
				}

				commentTextView.setText(message);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
			break;
		case PARAM_TWITTER_DIRECT_MESSAGE_ADAPTER:
			twitter4j.DirectMessage twitterDirectMessageObject = (twitter4j.DirectMessage) bundle
					.get(BUNDLE_MESSAGE_OBJECT);
			listViewAdapter = new TwitterDirectMessageAdapter(this, listView,
					twitterDirectMessageObject);
			break;
		case PARAM_FACEBOOK_POST_ADAPTER:
			String graphId = bundle.getString(BUNDLE_FACEBOOK_GRAPH_ID);
			listViewAdapter = new FacebookPostAdapter(this, listView, graphId);
			break;
		default:
			WLog.d(TAG, String.format("unknown adapter type[%d]", adapterType));
			return;
		}

		listView.setAdapter(listViewAdapter);
		listViewAdapter.asyncRefresh();

		listView.setEmptyView(findViewById(android.R.id.empty));

	}

	public void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getSupportMenuInflater();
		menuInflater.inflate(R.menu.reply_menu, menu);

		// Calling super after populating the menu is necessary here to ensure
		// that the
		// action bar helpers have a chance to handle this event.
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.menu_compose:
			// construct and send message
			Toast.makeText(this, "(fake) queued message for posting",
					Toast.LENGTH_SHORT).show();

			onReply();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	void onReply() {
		String comment = commentTextView.getText().toString().trim();

		switch (adapterType) {
		case PARAM_TWITTER_CONVERSATION_ADAPTER:
			twitter4j.Status twitterMessageObject = (twitter4j.Status) getIntent()
					.getExtras().get(BUNDLE_MESSAGE_OBJECT);
			long id = twitterMessageObject.getId();
			WLog.d(TAG,
					String.format("replying twitter status %d %s", id, comment));
			TaskService.addBackgroundTask(getApplicationContext(),
					new ReplyStatusBackgroundTask(id, comment));
			break;
		case PARAM_FACEBOOK_POST_ADAPTER:
			if (comment != "") {
				String graphId = getIntent().getStringExtra(
						BUNDLE_FACEBOOK_GRAPH_ID);
				WLog.i(TAG, "reply graph id:" + graphId);
				TaskService.addBackgroundTask(getApplicationContext(),
						new CommentPostBackgroundTask(graphId, comment));
			}
			break;
		case PARAM_TWITTER_DIRECT_MESSAGE_ADAPTER:
			twitter4j.DirectMessage twitterDirectMessageObject = (twitter4j.DirectMessage) getIntent()
					.getExtras().get(BUNDLE_MESSAGE_OBJECT);
			long id2 = twitterDirectMessageObject.getId();
			WLog.d(TAG, String.format("replying twitter direct message %d %s",
					id2, comment));
			TaskService.addBackgroundTask(getApplicationContext(),
					new ReplyDirectMessageBackgroundTask(
							twitterDirectMessageObject.getSender(), comment));
		}
	}
}
