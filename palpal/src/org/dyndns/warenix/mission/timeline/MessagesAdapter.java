package org.dyndns.warenix.mission.timeline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.dyndns.warenix.lab.compat1.util.Memory;
import org.dyndns.warenix.mission.twitter.TwitterDirectMessageListItem;
import org.dyndns.warenix.mission.twitter.util.TwitterMaster;
import org.dyndns.warenix.util.WLog;

import twitter4j.DirectMessage;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.content.Context;
import android.widget.ListView;

/**
 * Load Facebook home feed and Twitter home timeline.
 * 
 * @author warenix
 * 
 */
public class MessagesAdapter extends TimelineAsyncAdapter {

	private static final String TAG = "MessagesAdapter";

	public static int MESSAGE_TYPE_TWITTER = 1;
	public static int MESSAGE_TYPE_FACEBOOK = 2;

	public MessagesAdapter(final Context context, ListView listView) {
		super(context, listView);

		Runnable twitter = new Runnable() {
			public void run() {
				WLog.i(TAG, (new Date()).toLocaleString()
						+ " twitter is running");
				if (TwitterMaster.restoreTwitterClient(context)) {
					getTwitterFeed(1, 20);
				} else {
					WLog.i(TAG, (new Date()).toLocaleString()
							+ " twitter is not linked");
				}
				WLog.i(TAG, (new Date()).toLocaleString() + " twitter is done");

				notifyRunnableDone();
			}
		};

		clearRunnables();
		addRunnable(twitter);
	}

	void getTwitterFeed(int page, int limit) {
		Twitter twitter = Memory.getTwitterClient();
		if (twitter != null) {
			try {
				Paging paging = new Paging(page, limit);
				statusList = twitter.getDirectMessages(paging);
				constructTwitterListItem(statusList, dataList);
			} catch (TwitterException e1) {
				e1.printStackTrace();
			}
		}

	}

	private static TimelineMessageListViewItem sTempItem;

	public int getItemViewType(int position) {
		sTempItem = (TimelineMessageListViewItem) getItem(position);
		if (sTempItem.messageType == MESSAGE_TYPE_TWITTER) {
			return 0;
		} else if (sTempItem.messageType == MESSAGE_TYPE_FACEBOOK) {
			return 1;
		}
		return 0;
	}

	public int getViewTypeCount() {
		return 2;
	}

	protected ResponseList<DirectMessage> statusList;

	public Serializable getItemList() {
		if (statusList == null) {
			return null;
		}
		return new Object[] { statusList };
	}

	public void setItemList(Serializable newItemList) {
		if (newItemList != null) {
			Object[] obj = (Object[]) newItemList;
			statusList = (ResponseList<DirectMessage>) obj[0];

			dataList.clear();
			constructTwitterListItem(statusList, dataList);
			this.onPostExecut(null);
		}
	}

	void constructTwitterListItem(ResponseList<DirectMessage> statusList,
			ArrayList<TimelineMessageListViewItem> dataList) {
		if (statusList != null) {
			for (twitter4j.DirectMessage status : statusList) {
				dataList.add(new TwitterDirectMessageListItem(status,
						MessagesAdapter.this));
			}
		}
	}

}
