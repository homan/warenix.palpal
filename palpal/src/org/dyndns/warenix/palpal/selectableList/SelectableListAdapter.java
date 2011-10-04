package org.dyndns.warenix.palpal.selectableList;

import java.util.ArrayList;
import java.util.List;

import org.dyndns.warenix.palpal.selectableList.SelectableItem.SelectableItemObserver;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;
import org.dyndns.warenix.pattern.baseListView.ListViewItem;

import android.content.Context;
import android.util.Log;
import android.widget.Filter;
import android.widget.Filterable;

public class SelectableListAdapter extends ListViewAdapter implements
		SelectableItemObserver, Filterable {
	protected boolean selectedIndex[];
	protected ArrayList<ListViewItem> originalItemList = new ArrayList<ListViewItem>();

	public SelectableListAdapter(Context context) {
		super(context);

	}

	public void clear() {
		super.clear();
		originalItemList.clear();
	}

	protected void initSelectedIndex(int size) {
		selectedIndex = new boolean[size];
	}

	public interface Selectable {
		void onSelected(int position, boolean selected);
	}

	@Override
	public void onSelected(int position, boolean isChecked) {
		selectedIndex[position] = isChecked;
	}

	@Override
	public boolean getSelected(int position) {
		return selectedIndex[position];
	}

	@Override
	public List<SelectableItem> getSelectedList() {
		ArrayList<SelectableItem> selectedItemList = new ArrayList<SelectableItem>();
		for (int i = 0; i < selectedIndex.length; ++i) {
			if (selectedIndex[i]) {
				selectedItemList.add((SelectableItem) originalItemList.get(i));
			}
		}
		return selectedItemList;
	}

	public void addSelectableItem(SelectableItem item) {
		itemList.add(item);
		originalItemList.add(item);
	}

	Filter filter;

	@Override
	public Filter getFilter() {
		if (filter == null) {
			filter = new ListFilter();
		}
		return filter;
	}

	class ListFilter extends Filter {

		protected FilterResults performFiltering(CharSequence prefix) {
			// NOTE: this function is *always* called from a background
			// thread,
			// and
			// not the UI thread.

			ArrayList<ListViewItem> i = new ArrayList<ListViewItem>();
			FilterResults results = new FilterResults();

			String prefixString = prefix.toString();
			if (prefix != null && prefixString.length() > 0) {

				Log.d("palpal", "filter item list " + originalItemList.size());
				for (int index = 0; index < originalItemList.size(); index++) {
					SelectableItem si = (SelectableItem) originalItemList
							.get(index);

					if (si.matchFilter(prefix)) {
						i.add(si);
					}
				}
				results.values = i;
				results.count = i.size();
			} else {
				synchronized (originalItemList) {
					results.values = originalItemList;
					results.count = originalItemList.size();
				}
			}

			return results;
		}

		@Override
		protected void publishResults(CharSequence prefix, FilterResults results) {
			// NOTE: this function is *always* called from the UI thread.

			if (results != null && results.count >= 0) {
				itemList.clear();

				@SuppressWarnings("unchecked")
				ArrayList<ListViewItem> filterdResult = (ArrayList<ListViewItem>) results.values;
				itemList.addAll(filterdResult);
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}
	}
}
