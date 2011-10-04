package org.dyndns.warenix.imageGallery;

import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;

import android.content.Context;

public class ImageGalleryAdapter extends ListViewAdapter {

	public ImageGalleryAdapter(Context context) {
		super(context);
	}

	public void addImage(String url) {
		this.itemList.add(new ImageListItem(url));
	}

}
