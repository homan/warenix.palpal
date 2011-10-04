package org.dyndns.warenix.palpal.twitter.conversation;

import org.dyndns.warenix.palpal.PalPal;
import org.dyndns.warenix.palpal.bubbleMessage.BubbleMessageListAdapter;
import org.dyndns.warenix.palpal.social.twitter.TwitterBubbleMessage;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.content.Context;
import android.os.AsyncTask;

public class ConversationListAdapter extends BubbleMessageListAdapter {

	long statusId;

	public ConversationListAdapter(Context context) {
		super(context);
	}

	public void refresh(long statusId) {
		this.statusId = statusId;
		loadMessageFromDB();
	}

	FetchConversationAsyncTask task = null;

	public void loadMessageFromDB() {
		itemList.clear();
		runNotifyDataSetInvalidated();

		task = new FetchConversationAsyncTask();
		task.execute();
	}

	public void stop() {
		if (task != null && task.isCancelled() == false) {
			task.cancel(true);
		}
	}

	class FetchConversationAsyncTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			Twitter twitter = PalPal.getTwitterClient();
			twitter4j.Status status = null;
			do {
				try {
					status = twitter.showStatus(statusId);

					TwitterBubbleMessage bubbleMessage = new TwitterBubbleMessage(
							status.getUser().getScreenName(), status.getText(),
							status.getUser().getProfileImageURL().toString(),
							new java.sql.Date(status.getCreatedAt().getTime()),
							"twitter", status.getId() + "");
					itemList.add(bubbleMessage);
					runNotifyDataSetInvalidated();

				} catch (TwitterException e) {
					e.printStackTrace();
					break;
				}
			} while ((statusId = status.getInReplyToStatusId()) != -1);

			return null;
		}

	}
}
