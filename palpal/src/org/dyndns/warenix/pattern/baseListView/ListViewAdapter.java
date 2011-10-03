package org.dyndns.warenix.pattern.baseListView;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ListViewAdapter extends BaseAdapter {
	protected Context context;
	protected ArrayList<ListViewItem> itemList;

	public ListViewAdapter(Context context) {
		super();
		this.context = context;
		itemList = new ArrayList<ListViewItem>();
	}

	// ++BaseAdapter

	@Override
	public int getCount() {
		if (itemList != null) {
			return itemList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (itemList != null) {
			return itemList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListViewItem item = itemList.get(position);
		Log.d("warenix", String.format("get item %d view", position));
		return item.getView(context, convertView, parent);
	}

	// --BaseAdapter
}
