package org.dyndns.warenix.mission.sample;

import java.util.ArrayList;

import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;
import org.dyndns.warenix.pattern.baseListView.ListViewItem;

import android.content.Context;
import org.dyndns.warenix.util.AsyncTask;
import android.widget.ListView;

public class SampleListAdapter extends ListViewAdapter {

	boolean isRefreshing;

	public SampleListAdapter(Context context, ListView listView) {
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

		ArrayList<SampleListItem> dataList = new ArrayList<SampleListItem>();

		@Override
		protected Void doInBackground(Void... params) {
			for (int i = 0; i < 100; ++i) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				dataList.add(new SampleListItem("#" + i, "message"));
			}
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
		ListViewItem item = itemList.get(position);
		return position % 2;
	}

	public int getViewTypeCount() {
		return 2;
	}

}
