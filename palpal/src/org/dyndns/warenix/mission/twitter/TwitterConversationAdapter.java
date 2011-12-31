package org.dyndns.warenix.mission.twitter;

import java.util.ArrayList;

import org.dyndns.warenix.lab.compat1.util.AndroidUtil;
import org.dyndns.warenix.lab.compat1.util.Memory;
import org.dyndns.warenix.mission.timeline.TimelineMessageListViewItem;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ListView;

/**
 * Read individual Facebook post detail
 * 
 * @author warenix
 * 
 */
public class TwitterConversationAdapter extends ListViewAdapter {

	twitter4j.Status messageObject;
	boolean isRefreshing;

	public TwitterConversationAdapter(Context context, ListView listView,
			twitter4j.Status messageObject) {
		super(context, listView);
		this.messageObject = messageObject;
	}

	public void asyncRefresh() {
		if (!isRefreshing) {
			isRefreshing = true;
			new AsyncRefreshTask().execute();
		}

	}

	class AsyncRefreshTask extends AsyncTask<Void, Void, Void> {

		ArrayList<TimelineMessageListViewItem> dataList = new ArrayList<TimelineMessageListViewItem>();

		@Override
		protected Void doInBackground(Void... params) {
			dataList.add(new TwitterMessageListItem(messageObject,
					TwitterConversationAdapter.this));

			Twitter twitter = Memory.getTwitterClient();
			if (twitter != null) {
				try {
					long inReplyToStatusId = messageObject
							.getInReplyToStatusId();

					twitter4j.Status status = null;
					while (inReplyToStatusId != -1) {
						status = twitter.showStatus(inReplyToStatusId);
						dataList.add(new TwitterMessageListItem(status,
								TwitterConversationAdapter.this));
						inReplyToStatusId = status.getInReplyToStatusId();
					}

				} catch (TwitterException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			return null;
		}

		protected void onPostExecute(Void v) {
			itemList.clear();
			itemList.addAll(dataList);
			notifyDataSetChanged();
			isRefreshing = false;

			AndroidUtil.playListAnimation(listView);
		}

	}

	public int getItemViewType(int position) {
		return 0;
	}

	public int getViewTypeCount() {
		return 1;
	}
}
