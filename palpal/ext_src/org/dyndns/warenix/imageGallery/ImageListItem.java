package org.dyndns.warenix.imageGallery;

import org.dyndns.warenix.palpaltwitter.R;
import org.dyndns.warenix.pattern.baseListView.ListViewItem;
import org.dyndns.warenix.util.DownloadImageTask.DownloadImageTaskCallback;
import org.dyndns.warenix.widget.WebImage;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ContextMenu;
import android.view.View;

public class ImageListItem extends ListViewItem implements
		DownloadImageTaskCallback {

	String url;

	static class ViewHolder {
		WebImage image;
	}

	public ImageListItem(String url) {
		this.url = url;
	}

	@Override
	protected View createEmptyView(Context context) {
		View view = inflater.inflate(R.layout.image, null);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.image = (WebImage) view.findViewById(R.id.image);
		view.setTag(viewHolder);
		return view;
	}

	@Override
	protected View fillViewWithContent(Context context, View view) {
		ViewHolder viewHolder = (ViewHolder) view.getTag();
		WebImage image = viewHolder.image;
		image.recycleBitmap();
		image.startLoading(url);
		return view;
	}

	@Override
	public void showContextMenu(ContextMenu menu) {

	}

	@Override
	public void onDownloadComplete(String url, Bitmap bitmap) {

	}

}
