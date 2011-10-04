package org.dyndns.warenix.palpal.social.twitter.activity;

import java.util.ArrayList;

import org.dyndns.warenix.db.SimpleStorable;
import org.dyndns.warenix.db.SimpleStorableManager;
import org.dyndns.warenix.embedly.Embedable;
import org.dyndns.warenix.imageGallery.ImageGalleryController;
import org.dyndns.warenix.imageGallery.ImageList;
import org.dyndns.warenix.palpal.bubbleMessage.BubbleMessage;
import org.dyndns.warenix.palpal.social.twitter.storable.EmbedableMessageStorable;
import org.dyndns.warenix.palpaltwitter.R;
import org.dyndns.warenix.widget.WebImage;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

public class ImageGalleryActivity extends Activity {
	ArrayList<Embedable> embedableList = new ArrayList<Embedable>();
	WebImage image;
	ImageButton next;
	ImageButton prev;
	int currentIndex;
	TextView page;

	ImageGalleryController controller;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_gallery);

		image = (WebImage) findViewById(R.id.image);

		ImageList imageList = getImageListFromEmbedableList(getEmbedableList());
		controller = new ImageGalleryController(ImageGalleryActivity.this,
				R.id.imageList);
		controller.displayImageList(imageList);
	}

	void storeEmbedableMessage(BubbleMessage message, Embedable embedable) {
		SimpleStorableManager db = new SimpleStorableManager(
				getApplicationContext());
		db.insertItem(new EmbedableMessageStorable(
				message.socialNetworkMessageId, embedable));
	}

	public ArrayList<Embedable> getEmbedableList() {
		ArrayList<Embedable> embedableList = new ArrayList<Embedable>();

		SimpleStorableManager db = new SimpleStorableManager(
				getApplicationContext());

		ArrayList<SimpleStorable> storableList = db
				.getSimpleStorableList(EmbedableMessageStorable.TYPE);
		for (SimpleStorable storable : storableList) {
			Embedable embedable = EmbedableMessageStorable.factory(storable
					.getValue());
			if (embedable != null) {
				embedableList.add(embedable);
			}
		}

		return embedableList;
	}

	ImageList getImageListFromEmbedableList(ArrayList<Embedable> embedableList) {
		ImageList imageList = new ImageList();
		for (Embedable embedable : embedableList) {
			imageList.addImage(embedable.url);
		}
		return imageList;
	}
}
