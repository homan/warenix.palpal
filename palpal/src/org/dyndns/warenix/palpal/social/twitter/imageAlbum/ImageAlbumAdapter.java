package org.dyndns.warenix.palpal.social.twitter.imageAlbum;

import java.util.ArrayList;

import org.dyndns.warenix.db.SimpleStorable;
import org.dyndns.warenix.db.SimpleStorableManager;
import org.dyndns.warenix.embedly.Embedable;
import org.dyndns.warenix.palpal.social.twitter.storable.EmbedableMessageStorable;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;

import android.content.Context;

public class ImageAlbumAdapter extends ListViewAdapter {

	Context context;

	public ImageAlbumAdapter(Context context) {
		super(context);
		this.context = context;
	}

	public ArrayList<Embedable> getEmbedableList() {
		ArrayList<Embedable> embedableList = new ArrayList<Embedable>();

		SimpleStorableManager db = new SimpleStorableManager(context);

		ArrayList<SimpleStorable> storableList = db
				.getSimpleStorableList(EmbedableMessageStorable.TYPE);
		for (SimpleStorable storable : storableList) {
			Embedable embedable = EmbedableMessageStorable.factory(storable
					.getValue());
			embedableList.add(embedable);
		}

		return embedableList;
	}

	public void addImageItem(ImageAlbumThumbnailItem imageAlbumItem) {
		itemList.add(imageAlbumItem);
		runNotifyDataSetInvalidated();
	}

}
