package org.dyndns.warenix.lab.compat1.app;

import org.dyndns.warenix.lab.compat1.R;
import org.dyndns.warenix.lab.compat1.util.AndroidUtil;
import org.dyndns.warenix.lab.compat1.util.Memory;
import org.dyndns.warenix.mission.facebook.FacebookPostAdapter;
import org.dyndns.warenix.mission.twitter.TwitterConversationAdapter;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.UserMentionEntity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ListView;

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
		int adapterType = bundle.getInt(BUNDLE_LIST_VIEW_ADAPTER);
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
				// String myScreenname = twitter.getScreenName();
				// UserMentionEntity mentionedScreenname = null;
				// for (int i = 0; i < userMentionEntity.length; ++i) {
				// mentionedScreenname = userMentionEntity[i];
				// if (!mentionedScreenname.getScreenName().equals(
				// myScreenname)) {
				// message += "@" + mentionedScreenname.getScreenName()
				// + " ";
				// }
				// }

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.reply_menu, menu);

		// Calling super after populating the menu is necessary here to ensure
		// that the
		// action bar helpers have a chance to handle this event.
		return super.onCreateOptionsMenu(menu);
	}

}
