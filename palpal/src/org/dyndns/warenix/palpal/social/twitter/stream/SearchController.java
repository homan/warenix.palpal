package org.dyndns.warenix.palpal.social.twitter.stream;

import java.util.ArrayList;

import org.dyndns.warenix.palpal.social.twitter.search.SearchListViewAdapter;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;
import org.dyndns.warenix.pattern.baseListView.ListViewController;
import org.dyndns.warenix.pattern.baseListView.ListViewItem;

import android.app.Activity;
import android.content.Context;
import android.widget.ListView;

public class SearchController extends ListViewController {

	StatusStreamController streamController;

	public SearchController(Activity context, int resourceId) {
		super(context, resourceId);
	}

	@Override
	public ListViewAdapter setupListViewAdapter(Context context) {
		((ListView) listView).setDividerHeight(0);
		listAdapter = new SearchListViewAdapter(context);
		return listAdapter;
	}

	public void displayList(ArrayList<ListViewItem> newItemList) {
		((SearchListViewAdapter) listAdapter).displayList(newItemList);
	}

}
