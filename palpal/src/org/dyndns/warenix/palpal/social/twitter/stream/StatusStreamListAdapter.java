package org.dyndns.warenix.palpal.social.twitter.stream;

import java.util.List;

import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;

import twitter4j.Status;
import android.app.Activity;
import android.content.Context;

public class StatusStreamListAdapter extends ListViewAdapter {

	public StatusStreamListAdapter(Context context) {
		super(context);
	}

	// adapter methods
	public void addStatus(Status status) {
		itemList.add(new StatusItem(status));
		((Activity) context).runOnUiThread(new Runnable() {
			public void run() {
				notifyDataSetChanged();
			}
		});

	}

	public void addAllStatus(List<StatusItem> statusList) {
		itemList.addAll(statusList);
		((Activity) context).runOnUiThread(new Runnable() {
			public void run() {
				notifyDataSetChanged();
			}
		});

	}

	public void clearStatus() {
		itemList.clear();
		((Activity) context).runOnUiThread(new Runnable() {
			public void run() {
				notifyDataSetChanged();
			}
		});
	}
}
