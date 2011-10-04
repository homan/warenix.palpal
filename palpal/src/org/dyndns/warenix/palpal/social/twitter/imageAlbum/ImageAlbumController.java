package org.dyndns.warenix.palpal.social.twitter.imageAlbum;

import java.util.ArrayList;

import org.dyndns.warenix.embedly.Embedable;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;

import android.app.Activity;
import android.content.Context;
import android.widget.Gallery;

public class ImageAlbumController {
	ListViewAdapter listAdapter;
	Gallery gallery;

	public ImageAlbumController(Activity context, int resourceId) {
		listAdapter = setupListViewAdapter(context);
		gallery = (Gallery) context.findViewById(resourceId);
		gallery.setAdapter(listAdapter);
	}

	public ListViewAdapter setupListViewAdapter(Context context) {
		ImageAlbumAdapter listAdapter = new ImageAlbumAdapter(context);
		return listAdapter;
	}

	public void showEmbdableImage() {
		ArrayList<Embedable> embedableList = ((ImageAlbumAdapter) listAdapter)
				.getEmbedableList();

		for (Embedable embedable : embedableList) {
			ImageAlbumThumbnailItem imageAlbumItem = new ImageAlbumThumbnailItem(
					embedable);
			((ImageAlbumAdapter) listAdapter).addImageItem(imageAlbumItem);
		}
	}
}
