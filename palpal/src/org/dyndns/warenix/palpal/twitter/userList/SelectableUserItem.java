package org.dyndns.warenix.palpal.twitter.userList;

import org.dyndns.warenix.palpal.selectableList.SelectableItem;
import org.dyndns.warenix.palpal.social.twitter.Friend;
import org.dyndns.warenix.palpaltwitter.R;
import org.dyndns.warenix.widget.WebImage;

import android.content.Context;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

public class SelectableUserItem extends SelectableItem {
	Friend friend;

	public SelectableUserItem(int position, boolean selected,
			SelectableItemObserver observer, Friend friend) {
		super(position, selected, observer);
		this.friend = friend;
		// this.username = "#" + position;
	}

	public static class ViewHolder extends SelectableItem.ViewHolder {
		public TextView username;
		public WebImage profileImage;

		public ViewHolder(SelectableItem.ViewHolder viewHolder) {
			super();
			super.checkbox = viewHolder.checkbox;
		}
	}

	@Override
	protected View createEmptyView(Context context) {
		View view = super.createEmptyView(context);

		final ViewHolder viewHolder = new ViewHolder(
				(SelectableItem.ViewHolder) view.getTag());
		viewHolder.username = (TextView) view.findViewById(R.id.username);
		viewHolder.profileImage = (WebImage) view
				.findViewById(R.id.profileImage);
		view.setTag(viewHolder);
		return view;
	}

	@Override
	protected View fillViewWithContent(Context context, View view) {
		view = super.fillViewWithContent(context, view);
		final ViewHolder viewHolder = (ViewHolder) view.getTag();
		viewHolder.username.setText(friend.username);
		viewHolder.profileImage.startLoading(friend.profileImageUrl);

		return view;
	}

	@Override
	public void showContextMenu(ContextMenu menu) {

	}

	public String getUsername() {
		return friend.username;
	}

	@Override
	public boolean matchFilter(CharSequence prefix) {
		return friend.username.contains(prefix);
	}

}
