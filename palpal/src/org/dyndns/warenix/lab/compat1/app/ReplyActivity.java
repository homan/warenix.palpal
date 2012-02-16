package org.dyndns.warenix.lab.compat1.app;

import org.dyndns.warenix.lab.compat1.R;
import org.dyndns.warenix.lab.compat1.util.AndroidUtil;
import org.dyndns.warenix.lab.compat1.util.Memory;
import org.dyndns.warenix.lab.taskservice.TaskService;
import org.dyndns.warenix.mission.facebook.FacebookPostAdapter;
import org.dyndns.warenix.mission.facebook.backgroundtask.CommentPostBackgroundTask;
import org.dyndns.warenix.mission.twitter.TwitterConversationAdapter;
import org.dyndns.warenix.mission.twitter.backgroundtask.ReplyStatusBackgroundTask;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;

import twitter4j.Twitter;
import twitter4j.UserMentionEntity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.actionbarcompat.ActionBarActivity;

public class ReplyActivity extends ActionBarActivity {

	public static String BUNDLE_MESSAGE_OBJECT = "messageObject";
	public static String BUNDLE_FACEBOOK_GRAPH_ID = "facebookGraphId";

	public static String BUNDLE_LIST_VIEW_ADAPTER = "ListViewAdapter";
	public static final int PARAM_TWITTER_CONVERSATION_ADAPTER = 1;
	public static final int PARAM_FACEBOOK_POST_ADAPTER = 2;

	ListViewAdapter listViewAdapter;

	Button compose;
	Gallery imageQueue;
	AutoCompleteTextView commentTextView;

	int adapterType;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reply_post_detail);

		setTitle("Reply");
		AndroidUtil.hideSoftwareKeyboard(this);

		commentTextView = (AutoCompleteTextView) findViewById(R.id.comment);

		ListView listView = (ListView) findViewById(android.R.id.list);

		Bundle bundle = getIntent().getExtras();

		twitter4j.Status twitterMessageObject = null;
		adapterType = bundle.getInt(BUNDLE_LIST_VIEW_ADAPTER);
		switch (adapterType) {
		case PARAM_TWITTER_CONVERSATION_ADAPTER:
			twitterMessageObject = (twitter4j.Status) bundle
					.get(BUNDLE_MESSAGE_OBJECT);
			listViewAdapter = new TwitterConversationAdapter(this, listView,
					twitterMessageObject);

			UserMentionEntity[] userMentionEntity = twitterMessageObject
					.getUserMentionEntities();
			try {
				Twitter twitter = Memory.getTwitterClient();

				// default reply to poster
				String message = "@"
						+ twitterMessageObject.getUser().getScreenName() + " ";

				// // remove mentions self
				String myScreenname = "";// twitter.getScreenName();
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
		case PARAM_FACEBOOK_POST_ADAPTER:
			String graphId = bundle.getString(BUNDLE_FACEBOOK_GRAPH_ID);
			listViewAdapter = new FacebookPostAdapter(this, listView, graphId);
		}

		listView.setAdapter(listViewAdapter);
		listViewAdapter.asyncRefresh();

		listView.setEmptyView(findViewById(android.R.id.empty));

		// compose = (Button) findViewById(R.id.image);
		// compose.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// pickMultipleLocalImage();
		// }
		// });
		// imageQueueAdapter = new ImageQueueAdapter(this);
		//
		// imageQueue = (Gallery) findViewById(R.id.imageQueue);
		// imageQueue.setAdapter(imageQueueAdapter);
		// imageQueue.setOnItemLongClickListener(new OnItemLongClickListener() {
		//
		// @Override
		// public boolean onItemLongClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// Log.d("lab", "" + position);
		// imageQueueAdapter.removeImageUri((Uri) imageQueueAdapter
		// .getItem(position));
		// return false;
		// }
		// });
		//
		// Intent imageReturnedIntent = getIntent();
		// if (imageReturnedIntent != null) {
		// if (Intent.ACTION_SEND_MULTIPLE.equals(imageReturnedIntent
		// .getAction())
		// && imageReturnedIntent.hasExtra(Intent.EXTRA_STREAM)) {
		// ArrayList<Parcelable> list = imageReturnedIntent
		// .getParcelableArrayListExtra(Intent.EXTRA_STREAM);
		// for (Parcelable p : list) {
		// Uri uri = (Uri) p;
		// imageQueueAdapter.addImageUri(uri);
		// Log.d("lab", "onActivityResult:" + uri);
		// }
		// }
		// }

	}

	public void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
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

			Log.d("palpal",
					String.format("replying twitter status %d %s", id, comment));
			TaskService.addBackgroundTask(getApplicationContext(),
					new ReplyStatusBackgroundTask(id, comment));
			break;
		case PARAM_FACEBOOK_POST_ADAPTER:

			if (comment != "") {
				String graphId = getIntent().getStringExtra(
						BUNDLE_FACEBOOK_GRAPH_ID);
				Log.i("palpal", "reply graph id:" + graphId);
				TaskService.addBackgroundTask(getApplicationContext(),
						new CommentPostBackgroundTask(graphId, comment));
			}
			break;
		}
	}
}
