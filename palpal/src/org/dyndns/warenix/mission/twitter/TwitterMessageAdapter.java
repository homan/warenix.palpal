package org.dyndns.warenix.mission.twitter;

import java.util.ArrayList;

import org.dyndns.warenix.lab.compat1.util.Memory;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.content.Context;
import org.dyndns.warenix.util.AsyncTask;
import android.widget.ListView;

public class TwitterMessageAdapter extends ListViewAdapter {

	boolean isRefreshing;

	public TwitterMessageAdapter(Context context, ListView listView) {
		super(context, listView);
	}

	public void asyncRefresh() {
		if (!isRefreshing) {
			isRefreshing = true;
			new AsyncRefreshTask().execute();
		}

	}

	// public void refresh() {
	// if (!isRefreshing) {
	// isRefreshing = true;
	// new AsyncRefreshTask().execute();
	// }
	// }

	class AsyncRefreshTask extends AsyncTask<Void, Void, Void> {

		ArrayList<TwitterMessageListItem> dataList = new ArrayList<TwitterMessageListItem>();

		@Override
		protected Void doInBackground(Void... params) {

			Twitter twitter = Memory.getTwitterClient();
			if (twitter != null) {
				try {
					Paging paging = new Paging(1, 20);
					ResponseList<twitter4j.Status> statusList = twitter
							.getMentions(paging);

					for (twitter4j.Status status : statusList) {
						dataList.add(new TwitterMessageListItem(status,
								TwitterMessageAdapter.this));
					}
				} catch (TwitterException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			// for (int i = 0; i < 100; ++i) {
			// try {
			// Thread.sleep(10);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			// dataList.add(new TwitterMessageListItem("#" + i));
			// }
			return null;
		}

		protected void onPostExecute(Void v) {
			itemList.clear();
			itemList.addAll(dataList);
			notifyDataSetChanged();
			isRefreshing = false;
		}

	}

	public int getItemViewType(int position) {
		// ListViewItem item = itemList.get(position);
		return 0;
	}

	public int getViewTypeCount() {
		return 1;
	}

}
