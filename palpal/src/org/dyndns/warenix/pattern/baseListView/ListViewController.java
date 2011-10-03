package org.dyndns.warenix.pattern.baseListView;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public abstract class ListViewController implements
		AdapterView.OnItemClickListener,
		AdapterView.OnCreateContextMenuListener {

	final Activity context;
	protected ListViewAdapter listAdapter;
	ListView listView;

	public ListViewController(final Activity context, int resourceId) {
		this.context = context;

		Log.d("warenix", "set list view");
		setListView(resourceId);
		Log.d("warenix", "bind list view");
		bindListView();
	}

	public void setListView(int resourceId) {
		listView = (ListView) context.findViewById(resourceId);

		listView.setOnItemClickListener(this);
		listView.setOnCreateContextMenuListener(this);
	}

	public abstract ListViewAdapter setupListViewAdapter(final Context context);

	public void bindListView() {
		if (listView != null) {
			listAdapter = setupListViewAdapter(context);
			listView.setAdapter(listAdapter);
			Log.d("warenix", "listview * adapter bind");
		}
	}

	// ++AdapterView.OnItemClickListener
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Log.d("warenix", String.format("onItemClick position[%d]", position));
	}

	// --AdapterView.OnItemClickListener

	// ++AdapterView.OnCreateContextMenuListener
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		int position = (int) info.id;

		Log.d("warenix",
				String.format("onCreateContextMenu position[%d]", position));

		ListViewItem item = (ListViewItem) listAdapter.getItem(position);
		item.showContextMenu(menu);
	}
	// --AdapterView.OnCreateContextMenuListener
}
