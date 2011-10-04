package org.dyndns.warenix.palpal.selectableList;

import java.util.List;

import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;
import org.dyndns.warenix.pattern.baseListView.ListViewController;

import android.app.Activity;
import android.content.Context;
import android.widget.ListView;

public class SelectableListController extends ListViewController {

	public SelectableListController(Activity context, int resourceId) {
		super(context, resourceId);
	}

	@Override
	public ListViewAdapter setupListViewAdapter(Context context) {
		((ListView) listView).setDividerHeight(0);
		SelectableListAdapter adapter = new SelectableListAdapter(context);
		return adapter;
	}

	public List<SelectableItem> getSelectedList() {
		return ((SelectableListAdapter) listAdapter).getSelectedList();
	}

}
