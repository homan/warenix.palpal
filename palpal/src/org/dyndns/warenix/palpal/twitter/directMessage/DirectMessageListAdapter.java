package org.dyndns.warenix.palpal.twitter.directMessage;

import java.util.ArrayList;

import org.dyndns.warenix.palpal.PalPal;
import org.dyndns.warenix.palpal.message.MessageDBManager;
import org.dyndns.warenix.palpal.social.twitter.TitleBubbleMessage;
import org.dyndns.warenix.palpal.social.twitter.TwitterBubbleMessage;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;
import org.dyndns.warenix.pattern.baseListView.ListViewItem;

import twitter4j.DirectMessage;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.content.Context;
import android.os.AsyncTask;

public class DirectMessageListAdapter extends ListViewAdapter {

	private static final String TITLE = "Direct Messages";
	static int FETCH_MESSAGE_LIMIT = 200;

	public DirectMessageListAdapter(Context context) {
		super(context);
		// refresh();
	}

	/**
	 * refresh list view. load the latest N message
	 */
	public void refresh() {
		itemList.add(0, new TitleBubbleMessage(TITLE));
		new FetchDirectMessageAsyncTask().execute();
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

	class FetchDirectMessageAsyncTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			Twitter twitter = PalPal.getTwitterClient();

			try {
				ResponseList<DirectMessage> messageList = twitter
						.getDirectMessages();

				for (DirectMessage directMessage : messageList) {
					TwitterBubbleMessage bubbleMessage = new TwitterBubbleMessage(
							directMessage.getSender().getScreenName(),
							directMessage.getText(), directMessage.getSender()
									.getProfileImageURL().toString(),
							new java.sql.Date(directMessage.getCreatedAt()
									.getTime()), "twitter",
							directMessage.getId() + "");
					bubbleMessage.setInReplyToStatusId(directMessage
							.getRecipientId());
					itemList.add(bubbleMessage);
					runNotifyDataSetInvalidated();
				}
			} catch (TwitterException e1) {
				e1.printStackTrace();
			}

			return null;
		}

	}

}
