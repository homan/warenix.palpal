package org.dyndns.warenix.mission.sample;

import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.pattern.baseListView.IViewHolder;
import org.dyndns.warenix.pattern.baseListView.ListViewItem;

import android.content.Context;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

public class SampleListItem extends ListViewItem {

	protected String username;
	protected String message;

	public static class ViewHolder implements IViewHolder {
		public TextView username;
		public TextView message;

		@Override
		public void releaseMemory() {

		}
	}

	public static class ViewHolder2 implements IViewHolder {
		// WebImage image;
		public TextView username;
		public TextView message;

		@Override
		public void releaseMemory() {

		}
	}

	public SampleListItem(String username, String message) {
		this.username = username;
		this.message = message;
	}

	@Override
	protected View createEmptyView(Context context, int position, int type) {
		View view = null;
		if (type == 0) {
			view = inflater.inflate(R.layout.sample_message, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.username = (TextView) view.findViewById(R.id.username);
			viewHolder.message = (TextView) view.findViewById(R.id.message);
			// viewHolder.message = (TextView) view.findViewById(R.id.message);
			view.setTag(viewHolder);
		} else {
			view = inflater.inflate(R.layout.sample_message, null);
			ViewHolder2 viewHolder = new ViewHolder2();
			viewHolder.username = (TextView) view.findViewById(R.id.username);
			viewHolder.message = (TextView) view.findViewById(R.id.message);
			// viewHolder.message = (TextView) view.findViewById(R.id.message);
			// viewHolder.image = (WebImage) view.findViewById(R.id.image);
			view.setTag(viewHolder);
		}

		return view;
	}

	@Override
	protected View fillViewWithContent(Context context, View view,
			int position, int type) {
		if (type == 0) {
			ViewHolder viewHolder = (ViewHolder) view.getTag();
			viewHolder.username.setText(username);
			viewHolder.message.setText(message);
			// viewHolder.message.setText("line 1\nline2\nline3\nline4\n");
		} else if (type == 1) {
			ViewHolder2 viewHolder = (ViewHolder2) view.getTag();
			viewHolder.username.setText(username);
			viewHolder.message.setText(message);
			// viewHolder.message.setText("line 1\nline2\nline3\nline4\n");
			// viewHolder.image
			// .startLoading("https://graph.facebook.com/warenix/picture");
		}
		return view;
	}

	@Override
	public void showContextMenu(ContextMenu menu) {

	}

}
