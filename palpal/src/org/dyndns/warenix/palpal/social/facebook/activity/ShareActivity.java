package org.dyndns.warenix.palpal.social.facebook.activity;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;

import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.palpal.social.facebook.FacebookException;
import org.dyndns.warenix.palpal.social.facebook.LinkPreview;
import org.dyndns.warenix.palpal.social.facebook.PreviewImageAdapter;
import org.dyndns.warenix.palpal.social.facebook.task.ShareAsyncTask;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookAPI;
import org.dyndns.warenix.palpal.social.facebook.vo.FacebookPost;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.Album;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.LinkFeed;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.PhotoFeed;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.StatusFeed;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.VideoFeed;
import org.dyndns.warenix.util.ToastUtil;
import org.dyndns.warenix.widget.WebImage;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.facebook.android.Facebook;

public class ShareActivity extends PalPalFacebookActivity {

	// constants
	public static final int REQ_CODE_PICK_IMAGE = 54321;
	public static final int REQ_CODE_CHOOSE_FACEBOOK_ALBUM = 54322;

	public static final String MODE_SHARE_STATUS = "mode_share_status";
	public static final String MODE_SHARE_LINK = "mode_share_link";
	public static final String MODE_SHARE_PHOTO = "mode_share_photo";

	/**
	 * passed this when share link from browser, using Intent.Send
	 */
	public static final String BUNDLE_URL = Intent.EXTRA_TEXT;

	/**
	 * post on this profile
	 */
	public static final String BUNDLE_PROFILE_ID = "profile_id";
	/**
	 * required, determine the share mode
	 */
	public static final String BUNDLE_MODE = "mode";
	/**
	 * if there's an existing shared feed
	 */
	public static final String BUNDLE_FEED = "feed";

	// ui
	LinkPreview preview;

	WebImage picture;
	Gallery previewGallery;
	EditText link;
	EditText message;
	EditText name;
	EditText caption;
	EditText description;
	EditText source;

	View facebookEditable;

	// data
	ArrayList<String> profileImageList;
	PreviewImageAdapter imageAdapter;

	FacebookPost sharedFeed;

	/**
	 * the album to be uploaded to
	 */
	Album album;

	// PalPalFacebookActivity
	@Override
	void onFacebookReady(Facebook facebook,
			HashMap<String, SoftReference<Bitmap>> imagePool) {

		String url = extras.getString(BUNDLE_URL);

		if (url != null) {
			Log.d("palpal", String.format("share url [%s]", url));

			LinkFeed linkFeed = new LinkFeed();
			linkFeed.link = url;
			View view = linkFeed.getView(this, null, imagePool);
			facebookEditable = linkFeed.getFacebookEditable(linkFeed, view,
					this, imagePool);

			sharedFeed = linkFeed;

			ViewFlipper postView = (ViewFlipper) findViewById(R.id.post);
			postView.addView(facebookEditable);

		} else {
			String mode = extras.getString(BUNDLE_MODE);

			if (mode != null) {
				handleShareByMode(mode);
			} else {
				// get passed post
				handlePassedFeed();
			}
		}
	}

	@Override
	void setupUI() {
		setContentView(R.layout.facebook_share);

		ImageButton submitButton = (ImageButton) findViewById(R.id.submit);
		submitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				ToastUtil.showQuickToast(ShareActivity.this, "share +ing");

				String profileId = extras.getString(BUNDLE_PROFILE_ID);
				if (profileId == null) {
					profileId = "me";
				}
				new ShareAsyncTask(ShareActivity.this).execute(sharedFeed,
						album, profileId);
			}
		});

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		facebook.authorizeCallback(requestCode, resultCode, data);

		switch (requestCode) {
		case REQ_CODE_PICK_IMAGE:
			if (resultCode == RESULT_OK) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String filePath = cursor.getString(columnIndex);
				cursor.close();

				// update image with picked image
				Log.v("palpal",
						String.format("picked image from gallery %s", filePath));
				((PhotoFeed) sharedFeed).picture = filePath;

				// Only decode image size. Not whole image
				BitmapFactory.Options option = new BitmapFactory.Options();
				option.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(filePath, option);

				// The new size to decode to
				final int NEW_SIZE = 800;

				// Now we have image width and height. We should find the
				// correct scale value. (power of 2)
				int width = option.outWidth;
				int height = option.outHeight;
				int scale = 1;

				while (true) {
					if (width / 2 < NEW_SIZE || height / 2 < NEW_SIZE)
						break;
					width /= 2;
					height /= 2;
					scale++;
				}

				// Decode again with inSampleSize
				option = new BitmapFactory.Options();
				option.inSampleSize = scale;

				Bitmap bitmap = BitmapFactory.decodeFile(filePath, option);

				picture = (WebImage) facebookEditable
						.findViewById(R.id.picture);

				picture.setImageBitmap(bitmap);

				break;
			}
		case REQ_CODE_CHOOSE_FACEBOOK_ALBUM:
			if (resultCode == RESULT_OK) {
				album = (Album) data.getParcelableExtra("album");
				Log.v("palpal",
						String.format("chosen album %s", album.toString()));

				// show album name to which the photo will be uploaded
				TextView name = (TextView) facebookEditable
						.findViewById(R.id.name);
				name.setText(album.name);

				break;
			}

		}

	}

	void fetchPreview(String url) {

		try {
			String responseString = FacebookAPI.linksPreview(url);
			if (responseString == null) {
				ToastUtil.showQuickToast(this, "cannot fetch preview");
				return;
			}
			JSONObject json = new JSONObject(responseString);
			preview = new LinkPreview(json);

			imageAdapter = new PreviewImageAdapter(this, preview.imageList,
					imagePool);

			previewGallery.setAdapter(imageAdapter);

			previewGallery.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View v,
						int position, long id) {
					String url = preview.imageList.get(position);
					picture.startLoading(url);
				}
			});

			if (name.getText().equals("")) {
				name.setText(preview.name);
			}
			if (caption.getText().equals("")) {
				caption.setText(preview.caption);
			}
			if (description.getText().equals("")) {
				description.setText(preview.description);
			}

		} catch (JSONException e) {
		} catch (FacebookException e) {
			ToastUtil.showQuickToast(this, e.toString());
		}
	}

	void handleShareByMode(String mode) {
		if (mode.equals(MODE_SHARE_STATUS)) {
			StatusFeed linkFeed = new StatusFeed();
			View view = linkFeed.getView(this, null, imagePool);
			facebookEditable = linkFeed.getFacebookEditable(linkFeed, view,
					this, imagePool);

			sharedFeed = linkFeed;

			ViewFlipper postView = (ViewFlipper) findViewById(R.id.post);
			postView.addView(facebookEditable);
		} else if (mode.equals(MODE_SHARE_LINK)) {
			LinkFeed linkFeed = new LinkFeed();
			View view = linkFeed.getView(this, null, imagePool);
			facebookEditable = linkFeed.getFacebookEditable(linkFeed, view,
					this, imagePool);

			sharedFeed = linkFeed;

			ViewFlipper postView = (ViewFlipper) findViewById(R.id.post);
			postView.addView(facebookEditable);

		} else if (mode.equals(MODE_SHARE_PHOTO)) {
			PhotoFeed linkFeed = new PhotoFeed();
			View view = linkFeed.getView(this, null, imagePool);
			facebookEditable = linkFeed.getFacebookEditable(linkFeed, view,
					this, imagePool);

			sharedFeed = linkFeed;

			ViewFlipper postView = (ViewFlipper) findViewById(R.id.post);
			postView.addView(facebookEditable);

		}
	}

	void handlePassedFeed() {
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			FacebookPost feed = bundle.getParcelable(BUNDLE_FEED);

			if (feed != null) {
				if (feed.type.equals(LinkFeed.TYPE)) {

					View view = feed.getView(this, null, imagePool);
					LinkFeed linkFeed = (LinkFeed) feed.factory();
					facebookEditable = linkFeed.getFacebookEditable(linkFeed,
							view, this, imagePool);

					sharedFeed = linkFeed;

					ViewFlipper postView = (ViewFlipper) findViewById(R.id.post);
					postView.addView(facebookEditable);

				} else if (feed.type.equals(PhotoFeed.TYPE)) {
					View view = feed.getView(this, null, imagePool);
					PhotoFeed videoFeed = (PhotoFeed) feed.factory();
					facebookEditable = videoFeed.getFacebookEditable(videoFeed,
							view, this, imagePool);

					sharedFeed = videoFeed;

					ViewFlipper postView = (ViewFlipper) findViewById(R.id.post);
					postView.addView(facebookEditable);
				} else if (feed.type.equals(VideoFeed.TYPE)) {

					View view = feed.getView(this, null, imagePool);
					VideoFeed videoFeed = (VideoFeed) feed.factory();
					facebookEditable = videoFeed.getFacebookEditable(videoFeed,
							view, this, imagePool);

					sharedFeed = videoFeed;

					ViewFlipper postView = (ViewFlipper) findViewById(R.id.post);
					postView.addView(facebookEditable);

				} else if (feed.type.equals(StatusFeed.TYPE)) {

					View view = feed.getView(this, null, imagePool);
					StatusFeed videoFeed = (StatusFeed) feed.factory();
					facebookEditable = videoFeed.getFacebookEditable(videoFeed,
							view, this, imagePool);

					sharedFeed = videoFeed;

					ViewFlipper postView = (ViewFlipper) findViewById(R.id.post);
					postView.addView(facebookEditable);

				}
			}
		}
	}

}
