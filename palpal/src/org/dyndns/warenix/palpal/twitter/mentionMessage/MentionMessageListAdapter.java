package org.dyndns.warenix.palpal.twitter.mentionMessage;

import java.util.ArrayList;

import org.dyndns.warenix.palpal.PalPal;
import org.dyndns.warenix.palpal.message.MessageDBManager;
import org.dyndns.warenix.palpal.social.twitter.TitleBubbleMessage;
import org.dyndns.warenix.palpal.social.twitter.TwitterBubbleMessage;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;
import org.dyndns.warenix.pattern.baseListView.ListViewItem;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.content.Context;
import android.os.AsyncTask;

public class MentionMessageListAdapter extends ListViewAdapter {
	private static final String TITLE = "Mentions";
	static int FETCH_MESSAGE_LIMIT = 200;

	public MentionMessageListAdapter(Context context) {
		super(context);
		// refresh();
	}

	/**
	 * refresh list view. load the latest N message
	 */
	public void refresh() {
		itemList.add(0, new TitleBubbleMessage(TITLE));
		new FetchMentionMessageAsyncTask().execute();
		// loadMessageFromDB(FETCH_MESSAGE_LIMIT, -1);
		//
		// // only show when there's unread messages
		// if (PalPal.unreadMessageCount > 0) {
		// itemList.add(PalPal.unreadMessageCount, new TitleBubbleMessage(
		// "last refresh"));
		// }
	}

	/**
	 * load message by page. append message
	 * 
	 * @param pageNo
	 *            zero based
	 */
	public void loadMessageByPage(int pageNo) {
		int fromRow = pageNo == 0 ? -1 : (pageNo * FETCH_MESSAGE_LIMIT);
		int rowCount = FETCH_MESSAGE_LIMIT;
		MessageDBManager db = new MessageDBManager(context);
		itemList.add(0, new TitleBubbleMessage(TITLE));
		itemList.addAll(db.getMessageList(rowCount, fromRow));
		runNotifyDataSetInvalidated();
	}

	public void loadMessageFromDB() {
		MessageDBManager db = new MessageDBManager(context);
		itemList.clear();
		itemList.add(0, new TitleBubbleMessage(TITLE));
		itemList.addAll(db.getMessageList());
		runNotifyDataSetInvalidated();
	}

	/**
	 * clear existing and load messages from database
	 * 
	 * @param rowCount
	 * @param fromRow
	 */
	public void loadMessageFromDB(int rowCount, int fromRow) {
		MessageDBManager db = new MessageDBManager(context);
		itemList.clear();
		itemList.add(0, new TitleBubbleMessage(TITLE));
		itemList.addAll(db.getMessageList(rowCount, fromRow));
		runNotifyDataSetInvalidated();
	}

	public void displayList(ArrayList<ListViewItem> newItemList) {
		itemList.clear();
		itemList.add(0, new TitleBubbleMessage(TITLE));
		itemList.addAll(newItemList);
		runNotifyDataSetInvalidated();
	}

	class FetchMentionMessageAsyncTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			Twitter twitter = PalPal.getTwitterClient();

			ResponseList<twitter4j.Status> statusList;
			try {

				statusList = twitter.getMentions();
				for (twitter4j.Status status : statusList) {

					// if (latestPostDate != null &&
					// !postDate.after(latestPostDate)) {
					// break;
					// }

					String message = status.isRetweet() ? status
							.getRetweetedStatus().getText() : status.getText();
					TwitterBubbleMessage bubbleMessage = new TwitterBubbleMessage(
							status.getUser().getScreenName(), message, status
									.getUser().getProfileImageURL().toString(),
							new java.sql.Date(status.getCreatedAt().getTime()),
							"twitter", status.getId() + "");
					bubbleMessage.setInReplyToStatusId(status
							.getInReplyToStatusId());
					itemList.add(bubbleMessage);
					runNotifyDataSetInvalidated();
				}

			} catch (TwitterException e) {
				e.printStackTrace();
			}

			return null;
		}

	}

}
