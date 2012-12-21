package org.dyndns.warenix.mission.twitter;

import java.util.ArrayList;

import org.dyndns.warenix.lab.compat1.util.AndroidUtil;
import org.dyndns.warenix.mission.timeline.TimelineMessageListViewItem;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ListView;

/**
 * Read individual Facebook post detail
 * 
 * @author warenix
 * 
 */
public class TwitterDirectMessageAdapter extends ListViewAdapter {

	twitter4j.DirectMessage messageObject;
	boolean isRefreshing;

	public TwitterDirectMessageAdapter(Context context, ListView listView,
			twitter4j.DirectMessage messageObject) {
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
			dataList.add(new TwitterDirectMessageListItem(messageObject,
					TwitterDirectMessageAdapter.this));
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
