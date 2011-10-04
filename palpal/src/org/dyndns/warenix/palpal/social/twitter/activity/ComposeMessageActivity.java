package org.dyndns.warenix.palpal.social.twitter.activity;

import java.util.ArrayList;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;

import org.dyndns.warenix.palpal.map.activity.LocationPickerActivity;
import org.dyndns.warenix.palpal.message.TwitterDBManager;
import org.dyndns.warenix.palpal.message.TwitterMessage;
import org.dyndns.warenix.palpal.service.SendMessageService;
import org.dyndns.warenix.palpaltwitter.R;
import org.dyndns.warenix.util.ToastUtil;
import org.dyndns.warenix.widget.WebImage;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class ComposeMessageActivity extends Activity {
	EditText statusText;
	ImageButton tweetButton;
	Status replyTostatus;
	String recipient;
	TextView charCounter;

	String mode;

	/**
	 * reply all, required bundle: BUNDLE_STATUS, BUNDLE_PARTICIPANTS
	 */
	public static String MODE_REPLY_ALL = "mode_reply_all";
	/**
	 * retweet status, required bundle: BUNDLE_STATUS
	 */
	public static String MODE_RETWEET_RT = "mode_retweet";
	/**
	 * compose a tweet
	 */
	public static String MODE_COMPOSE = "mode_compose";

	public static String MODE_DIRECT_MESSAGE = "mode_direct_message";

	public static String BUNDLE_MODE = "mode";

	public static String BUNDLE_SOCIAL_NETWORK_ID = "social_network_id";
	public static String BUNDLE_STATUS = "status";
	public static String BUNDLE_PARTICIPANTS = "participants";
	public static String BUNDLE_RECIPIENT = "recipient";

	public static String BUNDLE_REPLY_TO_USERNAME = "reply_to_username";
	public static String BUNDLE_REPLY_TO_STATUS = "reply_to_status";
	public static String BUNDLE_REPLY_TO_POST_DATE = "reply_to_post_date";
	public static String BUNDLE_REPLY_TO_PROFILE_IMAGE_URL = "reply_to_user_profile_image_url";

	int currentCursorPosition;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.compose_message_activity);

		charCounter = (TextView) findViewById(R.id.charCounter);

		statusText = (EditText) findViewById(R.id.message);
		statusText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				charCounter.setText(String.format("%s", statusText.getText()
						.length()));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}
		});

		Button send = (Button) findViewById(R.id.send);
		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendMessage();
				finish();
			}
		});

		Button hashtag = (Button) findViewById(R.id.hashtag);
		hashtag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showRecentHashtags(v);
			}
		});

		Button user = (Button) findViewById(R.id.user);
		user.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final QuickAction qa = new QuickAction(v);
				ActionItem actionItem;
				// actionItem = new ActionItem();
				// actionItem.setTitle("list");
				// actionItem.setIcon(getResources().getDrawable(
				// R.drawable.dashboard));
				// actionItem.setOnClickListener(new OnClickListener() {
				//
				// @Override
				// public void onClick(View v) {
				// showUserList(v);
				// qa.dismiss();
				// }
				// });
				// qa.addActionItem(actionItem);

				actionItem = new ActionItem();
				actionItem.setTitle("followers");
				actionItem.setIcon(getResources().getDrawable(
						R.drawable.profile));
				actionItem.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						showUser(v);
						qa.dismiss();
					}
				});
				qa.addActionItem(actionItem);
				qa.show();
			}
		});

		Button location = (Button) findViewById(R.id.location);
		location.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final QuickAction qa = new QuickAction(v);
				ActionItem actionItem;
				// actionItem = new ActionItem();
				// actionItem.setTitle("list");
				// actionItem.setIcon(getResources().getDrawable(
				// R.drawable.dashboard));
				// actionItem.setOnClickListener(new OnClickListener() {
				//
				// @Override
				// public void onClick(View v) {
				// showUserList(v);
				// qa.dismiss();
				// }
				// });
				// qa.addActionItem(actionItem);

				actionItem = new ActionItem();
				actionItem.setTitle("map");
				actionItem.setIcon(getResources().getDrawable(
						R.drawable.dashboard));
				actionItem.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						showPickLocation();
						qa.dismiss();
					}
				});
				qa.addActionItem(actionItem);
				qa.show();
			}
		});

		String message = getIntent().getStringExtra(BUNDLE_STATUS);
		statusText.setText(message);

		// reply to status
		String mode = getIntent().getStringExtra(BUNDLE_MODE);
		if (mode != null) {
			if (mode.equals(MODE_RETWEET_RT)) {
				View replyToMessage = (View) findViewById(R.id.replyToMessage);
				replyToMessage.setVisibility(View.VISIBLE);

				WebImage profileImage = (WebImage) replyToMessage
						.findViewById(R.id.profileImage);
				String profileImageUrl = getIntent().getStringExtra(
						BUNDLE_REPLY_TO_PROFILE_IMAGE_URL);
				if (profileImageUrl != null) {
					profileImage.startLoading(profileImageUrl);
				}

				TextView replyToUsernametext = (TextView) replyToMessage
						.findViewById(R.id.username);
				replyToUsernametext.setText(getIntent().getStringExtra(
						BUNDLE_REPLY_TO_USERNAME));

				TextView replyToPostDateText = (TextView) replyToMessage
						.findViewById(R.id.postDate);
				replyToPostDateText.setText(getIntent().getStringExtra(
						BUNDLE_REPLY_TO_POST_DATE));

				TextView replyToStatusText = (TextView) replyToMessage
						.findViewById(R.id.message);
				replyToStatusText.setText(getIntent().getStringExtra(
						BUNDLE_REPLY_TO_STATUS));
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case UserListActivity.REQUEST_CODE_LIST_FOLLOWERS:
			if (resultCode == RESULT_OK) {
				String selectedUser = data.getExtras().getString(
						"selected_user");

				statusText.setText(statusText.getText().toString());
				Editable newText = statusText.getText().insert(
						currentCursorPosition, selectedUser);
				statusText.setText(newText);
				statusText.setSelection(currentCursorPosition
						+ selectedUser.length());

			}
			break;
		case LocationPickerActivity.REQUEST_CODE_PICK_LOCATION:
			if (resultCode == RESULT_OK) {
				int late6 = data.getExtras().getInt(
						LocationPickerActivity.BUNDLE_LATE6);
				int lnge6 = data.getExtras().getInt(
						LocationPickerActivity.BUNDLE_LNGE6);

				Log.d("palpal",
						String.format("picked location: %d,%d", late6, lnge6));

			}
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
		}

	}

	void sendMessage() {
		String message = statusText.getText().toString();
		if (message.length() == 0) {
			return;
		}

		StatusUpdate statusUpdate = new StatusUpdate(message);

		String mode = getIntent().getStringExtra(BUNDLE_MODE);
		if (mode != null) {
			if (mode.equals(MODE_RETWEET_RT)) {
				String replyToId = getIntent().getStringExtra(
						BUNDLE_SOCIAL_NETWORK_ID);
				if (replyToId != null) {
					statusUpdate
							.setInReplyToStatusId(Long.parseLong(replyToId));
				}
			}
		}

		TwitterDBManager db = new TwitterDBManager(getApplicationContext());

		ArrayList<String> hashtagList = findHashtagsInMessage(message);
		for (String hashtag : hashtagList) {

			int affectedRow = db.updateHashtag(hashtag);
			if (affectedRow == 0) {
				db.insertHashtag(hashtag);
			}

		}

		Intent intent = new Intent(getApplicationContext(),
				SendMessageService.class);
		intent.putExtra("message", new TwitterMessage(statusUpdate));
		startService(intent);
	}

	void showRecentHashtags(View v) {
		currentCursorPosition = statusText.getSelectionStart();

		TwitterDBManager db = new TwitterDBManager(getApplicationContext());
		ArrayList<String> hashtagList = db.getHashtagList();

		if (hashtagList.size() == 0) {
			ToastUtil.showQuickToast(getApplicationContext(),
					"You have no recently used #hashtag");
			return;
		}

		final QuickAction qa = new QuickAction(v);
		for (String hashtagName : hashtagList) {
			final ActionItem hashtag = new ActionItem();
			hashtag.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					statusText.setText(statusText.getText().toString());
					Editable newText = statusText.getText().insert(
							currentCursorPosition, hashtag.getTitle());
					statusText.setText(newText);
					statusText.setSelection(currentCursorPosition
							+ hashtag.getTitle().length());
					qa.dismiss();
				}
			});
			hashtag.setTitle("#" + hashtagName);
			hashtag.setIcon(getResources().getDrawable(R.drawable.list));
			qa.addActionItem(hashtag);
		}

		qa.show();
	}

	ArrayList<String> findHashtagsInMessage(String message) {
		ArrayList<String> participants = new ArrayList<String>();

		String[] tokens = message.split(" ");
		for (String token : tokens) {
			if (token.startsWith("#")) {
				participants.add(token.split("\\p{Punct}")[1]);
			}
		}

		return participants;
	}

	void showUser(View v) {

		currentCursorPosition = statusText.getSelectionStart();
		Intent intent = new Intent(getApplicationContext(),
				UserListActivity.class);
		intent.putExtra("requestCode",
				UserListActivity.REQUEST_CODE_LIST_FOLLOWERS);
		startActivityForResult(intent,
				UserListActivity.REQUEST_CODE_LIST_FOLLOWERS);
	}

	/**
	 * show twitter list user's having
	 * 
	 * @param v
	 */
	void showUserList(View v) {

		currentCursorPosition = statusText.getSelectionStart();
		Intent intent = new Intent(getApplicationContext(),
				UserListActivity.class);
		intent.putExtra("requestCode",
				UserListActivity.REQUEST_CODE_LIST_TWITTER_LISTS);
		startActivityForResult(intent,
				UserListActivity.REQUEST_CODE_LIST_TWITTER_LISTS);
	}

	void showPickLocation() {
		Intent intent = new Intent(getApplicationContext(),
				LocationPickerActivity.class);
		startActivityForResult(intent,
				LocationPickerActivity.REQUEST_CODE_PICK_LOCATION);
	}
}
