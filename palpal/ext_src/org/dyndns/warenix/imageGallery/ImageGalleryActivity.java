package org.dyndns.warenix.imageGallery;

import org.dyndns.warenix.palpaltwitter.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class ImageGalleryActivity extends Activity {
	public final String BUNDLE_IMAGE_LIST = "image_list";

	ImageGalleryController controller;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.image_gallery);

		setupUI();
	}

	void setupUI() {
		controller = new ImageGalleryController(this, R.id.imageList);
		controller.refresh();
	}
}