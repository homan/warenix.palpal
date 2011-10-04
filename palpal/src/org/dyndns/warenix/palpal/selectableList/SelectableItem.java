package org.dyndns.warenix.palpal.selectableList;

import java.util.List;

import org.dyndns.warenix.palpaltwitter.R;
import org.dyndns.warenix.pattern.baseListView.ListViewItem;

import android.content.Context;
import android.view.ContextMenu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SelectableItem extends ListViewItem {
	int position;
	boolean selected;
	SelectableItemObserver observer;

	public SelectableItem(int position, boolean selected,
			SelectableItemObserver observer) {
		this.position = position;
		this.selected = selected;
		this.observer = observer;
	}

	public static class ViewHolder {
		public CheckBox checkbox;
	}

	@Override
	protected View createEmptyView(Context context) {
		View view = inflater.inflate(R.layout.selectable_user, null);
		final ViewHolder viewHolder = new ViewHolder();
		viewHolder.checkbox = (CheckBox) view.findViewById(R.id.checkbox);
		view.setTag(viewHolder);
		return view;
	}

	@Override
	protected View fillViewWithContent(Context context, View view) {
		final ViewHolder viewHolder = (ViewHolder) view.getTag();
		// viewHolder.checkbox.setOnCheckedChangeListener(null);

		viewHolder.checkbox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						observer.onSelected(position, isChecked);
						viewHolder.checkbox.setSelected(isChecked);
						selected = isChecked;
					}
				});
		viewHolder.checkbox.setChecked(observer.getSelected(position));

		return view;
	}

	@Override
	public void showContextMenu(ContextMenu menu) {

	}

	public interface SelectableItemObserver {
		public void onSelected(int position, boolean isChecked);

		public boolean getSelected(int position);

		public List<SelectableItem> getSelectedList();
	}

	public boolean matchFilter(CharSequence prefix) {
		return true;
	}
}
