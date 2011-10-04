package org.dyndns.warenix.palpal.twitter.userList;

import org.dyndns.warenix.palpal.selectableList.SelectableListController;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;

import android.app.Activity;
import android.content.Context;
import android.widget.ListView;

public class SelectableUserItemListController extends SelectableListController {

	public SelectableUserItemListController(Activity context, int resourceId) {
		super(context, resourceId);
	}

	@Override
	public ListViewAdapter setupListViewAdapter(Context context) {
		((ListView) listView).setDividerHeight(0);
		listView.setTextFilterEnabled(true);
		SelectableUserItemListAdapter adapter = new SelectableUserItemListAdapter(
				context);
		return adapter;
	}

	public void refresh() {
		((SelectableUserItemListAdapter) listAdapter).refresh();
	}

	public void filterList() {

	}

}
