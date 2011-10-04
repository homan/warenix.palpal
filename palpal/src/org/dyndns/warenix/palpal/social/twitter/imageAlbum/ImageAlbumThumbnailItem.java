package org.dyndns.warenix.palpal.social.twitter.imageAlbum;

import org.dyndns.warenix.embedly.Embedable;
import org.dyndns.warenix.palpaltwitter.R;
import org.dyndns.warenix.pattern.baseListView.ListViewItem;
import org.dyndns.warenix.widget.WebImage;

import android.content.Context;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

public class ImageAlbumThumbnailItem extends ListViewItem {
	Embedable embedable;

	public ImageAlbumThumbnailItem(Embedable embedable) {
		this.embedable = embedable;
		// this.username = "#" + position;
	}

	public static class ViewHolder {
		public TextView username;
		public WebImage profileImage;
	}

	@Override
	protected View createEmptyView(Context context) {
		View view = inflater.inflate(R.layout.mygrid, null);

		ViewHolder viewHolder = new ViewHolder();
		// viewHolder.username = (TextView) view.findViewById(R.id.username);
		viewHolder.profileImage = (WebImage) view.findViewById(R.id.imagepart);
		view.setTag(viewHolder);
		return view;
	}

	@Override
	protected View fillViewWithContent(Context context, View view) {
		final ViewHolder viewHolder = (ViewHolder) view.getTag();
		// viewHolder.username.setText(friend.username);
		viewHolder.profileImage.startLoading(embedable.thumbnailUrl);

		return view;
	}

	@Override
	public void showContextMenu(ContextMenu menu) {

	}

}
