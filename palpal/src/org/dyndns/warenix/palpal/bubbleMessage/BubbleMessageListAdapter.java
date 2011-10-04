package org.dyndns.warenix.palpal.bubbleMessage;

import java.util.ArrayList;

import org.dyndns.warenix.palpal.PalPal;
import org.dyndns.warenix.palpal.message.MessageDBManager;
import org.dyndns.warenix.palpal.social.twitter.TitleBubbleMessage;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;
import org.dyndns.warenix.pattern.baseListView.ListViewItem;

import android.content.Context;

public class BubbleMessageListAdapter extends ListViewAdapter {

	static int FETCH_MESSAGE_LIMIT = 200;

	public BubbleMessageListAdapter(Context context) {
		super(context);
		// refresh();
	}

	/**
	 * refresh list view. load the latest N message
	 */
	public void refresh() {
		loadMessageFromDB(FETCH_MESSAGE_LIMIT, -1);

		// only show when there's unread messages
		if (PalPal.unreadMessageCount > 0) {
			itemList.add(PalPal.unreadMessageCount, new TitleBubbleMessage(
					"last refresh"));
		}
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
		itemList.addAll(db.getMessageList(rowCount, fromRow));
		runNotifyDataSetInvalidated();
	}

	public void loadMessageFromDB() {
		MessageDBManager db = new MessageDBManager(context);
		itemList.clear();
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
		itemList.addAll(db.getMessageList(rowCount, fromRow));
		runNotifyDataSetInvalidated();
	}

	public void displayList(ArrayList<ListViewItem> newItemList) {
		itemList.clear();
		itemList.addAll(newItemList);
		runNotifyDataSetInvalidated();
	}

}
