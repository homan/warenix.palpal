package org.dyndns.warenix.palpal.social.twitter.search;

import java.util.ArrayList;

import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;
import org.dyndns.warenix.pattern.baseListView.ListViewItem;

import android.content.Context;

public class SearchListViewAdapter extends ListViewAdapter {

	public SearchListViewAdapter(Context context) {
		super(context);
	}

	public void displayList(ArrayList<ListViewItem> newItemList) {
		itemList.clear();
		if (newItemList != null) {
			itemList.addAll(newItemList);
			runNotifyDataSetInvalidated();
		}
	}

}
