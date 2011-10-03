package org.dyndns.warenix.palpal.social.twitter;

import java.util.ArrayList;

import org.dyndns.warenix.palpal.PalPal;
import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.util.ToastUtil;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.Message;
import winterwell.jtwitter.Twitter.Status;
import winterwell.jtwitter.Twitter.User;
import winterwell.jtwitter.TwitterException;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class ComposeTwitterStatusActivity extends Activity {
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

	public static String BUNDLE_STATUS = "status";
	public static String BUNDLE_PARTICIPANTS = "participants";
	public static String BUNDLE_RECIPIENT = "recipient";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.compose_twitter_status);

		charCounter = (TextView) findViewById(R.id.charCounter);

		statusText = (EditText) findViewById(R.id.status);
		statusText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				charCounter.setText(String.format("%s", statusText.getText()
						.length()));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}
		});

		tweetButton = (ImageButton) findViewById(R.id.submit);
		tweetButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				submitStatus();
			}

		});

		Bundle params = getIntent().getExtras();

		if (params != null) {

			mode = params.getString(BUNDLE_MODE);

			if (mode.equals(MODE_REPLY_ALL)) {
				replyTostatus = (Status) params.get(BUNDLE_STATUS);
				User createdBy = replyTostatus.user;
				@SuppressWarnings("unchecked")
				ArrayList<String> participants = (ArrayList<String>) params
						.get(BUNDLE_PARTICIPANTS);
				String replyAllString = "";
				for (String participant : participants) {
					replyAllString += String.format("@%s ", participant);
				}
				statusText.setText(String.format("@%s %s",
						createdBy.screenName, replyAllString));

				setTitle(String.format("Reply: %s", createdBy.screenName));
			} else if (mode.equals(MODE_RETWEET_RT)) {
				replyTostatus = (Status) params.get(BUNDLE_STATUS);
				User createdBy = replyTostatus.user;
				statusText.setText(String.format("RT @%s: %s",
						createdBy.screenName, replyTostatus.text));

				setTitle("Retweet");
			} else if (mode.equals(MODE_DIRECT_MESSAGE)) {
				recipient = (String) params.getString(BUNDLE_RECIPIENT);

				setTitle(String.format("Direct Message: %s", recipient));
			}
		}

	}

	void submitStatus() {
		Status updatedStatus = null;
		Twitter twitter = PalPal.getTwitterClient();
		if (twitter != null) {
			if (mode.equals(MODE_COMPOSE) || mode.equals(MODE_RETWEET_RT)
					|| mode.equals(MODE_REPLY_ALL)) {
				if (mode.equals(MODE_COMPOSE)) {
					// update status
					updatedStatus = twitter.updateStatus(statusText.getText()
							.toString());
				} else if (mode.equals(MODE_REPLY_ALL)) {
					// reply status
					Number inReplyToStatusId = replyTostatus.id;
					updatedStatus = twitter.updateStatus(statusText.getText()
							.toString(), inReplyToStatusId);
				} else if (mode.equals(MODE_RETWEET_RT)) {
					Number inReplyToStatusId = replyTostatus.id;
					// retweet
					updatedStatus = twitter.updateStatus(statusText.getText()
							.toString(), inReplyToStatusId);
				}

				if (updatedStatus == null) {
					ToastUtil.showQuickToast(ComposeTwitterStatusActivity.this,
							"Cannot post to twitter.");
				} else {
					finish();
				}
			} else if (mode.equals(MODE_DIRECT_MESSAGE)) {
				try {
					Message message = twitter.sendMessage(recipient, statusText
							.getText().toString());

					if (message == null) {
						ToastUtil.showQuickToast(
								ComposeTwitterStatusActivity.this,
								String.format("Cannot dm %s", recipient));
					} else {
						finish();
					}
				} catch (TwitterException e) {
					ToastUtil.showNotification(this,
							String.format("fail to dm %s", recipient),
							String.format("fail to dm %s", recipient),
							e.getMessage(), null, 1000);
				}
			}
		}

	}
}
