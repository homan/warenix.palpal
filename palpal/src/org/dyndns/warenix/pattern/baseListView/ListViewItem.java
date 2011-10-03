package org.dyndns.warenix.pattern.baseListView;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class ListViewItem {
	protected static LayoutInflater inflater;

	/**
	 * Get a view of this item
	 * 
	 * @param context
	 * @param convertView
	 * @param parent
	 * @return
	 */
	public View getView(Context context, View convertView, ViewGroup parent) {
		if (inflater == null) {
			Log.d("warenix", "init inflater");
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		View view = convertView;
		if (view == null) {
			Log.d("warenix", "create empty view");
			view = createEmptyView(context);
		}
		Log.d("warenix", "fill view with content");
		view = fillViewWithContent(context, view);
		return view;
	}

	/**
	 * create an empty view, with ViewHolder set in the tag
	 * 
	 * @param context
	 * @return
	 */
	abstract protected View createEmptyView(Context context);

	/**
	 * fill content to the provided view, with ViewHolder set in the tag
	 * 
	 * @param context
	 * @param view
	 * @return
	 */
	abstract protected View fillViewWithContent(Context context, View view);

	/**
	 * show context menu when long pressed on this item
	 * 
	 * @param menu
	 */
	abstract public void showContextMenu(ContextMenu menu);
}
