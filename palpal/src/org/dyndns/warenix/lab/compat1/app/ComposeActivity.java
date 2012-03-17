package org.dyndns.warenix.lab.compat1.app;

import java.util.ArrayList;

import org.dyndns.warenix.imageuploader.ImageQueueAdapter;
import org.dyndns.warenix.lab.compat1.R;
import org.dyndns.warenix.lab.compat1.util.AndroidUtil;
import org.dyndns.warenix.lab.taskservice.BackgroundTask;
import org.dyndns.warenix.lab.taskservice.TaskService;
import org.dyndns.warenix.lab.taskservice.TaskServiceStateListener;
import org.dyndns.warenix.mission.facebook.LinkPreview;
import org.dyndns.warenix.mission.facebook.backgroundtask.LinkPreviewBackgroundTask;
import org.dyndns.warenix.mission.facebook.backgroundtask.ShareLinkBackgroundTask;
import org.dyndns.warenix.mission.facebook.backgroundtask.ShareMessageBackgroundTask;
import org.dyndns.warenix.mission.facebook.backgroundtask.SharePhotoBackgroundTask;
import org.dyndns.warenix.mission.twitter.backgroundtask.UpdateStatusBackgroundTask;
import org.dyndns.warenix.mission.twitter.backgroundtask.UploadPhotoBackgroundTask;
import org.dyndns.warenix.util.ImageUtil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.Toast;

import com.example.android.actionbarcompat.ActionBarActivity;

public class ComposeActivity extends ActionBarActivity {

	public static final String BUNDLE_GRAPH_ID = "graphId";

	public static final String BUNDLE_MESSAGE_OBJECT = "messageObject";
	public static final String BUNDLE_SHARE_ACTION = "shareAction";
	public static final int PARAM_SHARE_ACTION_TWITTER_REPLY = 1;
	public static final int PARAM_SHARE_ACTION_FACEBOOK_POST = 2;

	ImageQueueAdapter imageQueueAdapter;

	Button compose;
	Gallery imageQueue;

	AutoCompleteTextView commentTextView;

	CheckBox sendFacebook;
	CheckBox sendTwitter;

	// share facebook links
	EditText albumText;
	EditText linkText;
	EditText nameText;
	EditText captionText;
	EditText descriptionText;
	EditText sourceText;

	String mSharedLink;

	LinkPreview mLinkPreview;

	public static final int PARAM_SHARE_MODE_MESSAGE = 1;
	public static final int PARAM_SHARE_MODE_LINK = 2;
	public static final int PARAM_SHARE_MODE_PHOTO = 3;

	int mShareMode = PARAM_SHARE_MODE_MESSAGE;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.compose);

		super.setTitle("Share");
		AndroidUtil.hideSoftwareKeyboard(this);

		setupUI();

		Intent imageReturnedIntent = getIntent();
		if (imageReturnedIntent != null) {
			if (Intent.ACTION_SEND_MULTIPLE.equals(imageReturnedIntent
					.getAction())
					&& imageReturnedIntent.hasExtra(Intent.EXTRA_STREAM)) {
				ArrayList<Parcelable> list = imageReturnedIntent
						.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
				for (Parcelable p : list) {
					Uri uri = (Uri) p;
					imageQueueAdapter.addImageUri(uri);
					Log.d("lab", "onActivityResult:" + uri);
				}
				onSharePhoto();
			} else if (Intent.ACTION_SEND.equals(imageReturnedIntent
					.getAction())) {
				Bundle bundle = imageReturnedIntent.getExtras();
				onShareLink(bundle.getString(Intent.EXTRA_TEXT));
			}
		} else {
			// show default share message UI
			onShareMessage();
		}
	}

	public void onResume() {
		super.onResume();

		TaskService.setStateListener(new TaskServiceStateListener() {

			@Override
			public void onQueueSizeChanged(int newQueueSize) {
				Log.d("taskservice", "onQueueSizeChanged " + newQueueSize);
			}

			@Override
			public void onBackgroundTaskRemoved(final BackgroundTask task) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						if (task instanceof LinkPreviewBackgroundTask) {
							mLinkPreview = (LinkPreview) task.getResult();

							if (mLinkPreview.previewImageList != null) {
								for (int i = 0; i < mLinkPreview.previewImageList
										.size(); ++i) {
									imageQueueAdapter.addImageUri(Uri
											.parse(mLinkPreview.previewImageList
													.get(i)));
								}
							}

							nameText.setText(mLinkPreview.name);
							captionText.setText(mLinkPreview.caption);
							linkText.setText(mLinkPreview.link);
							descriptionText.setText(mLinkPreview.description);
							sourceText.setText(mLinkPreview.source);
						} else if (task instanceof ShareLinkBackgroundTask) {
							Toast.makeText(ComposeActivity.this,
									"Link is shared!", Toast.LENGTH_SHORT)
									.show();
						} else if (task instanceof ShareMessageBackgroundTask) {
							Toast.makeText(ComposeActivity.this,
									"Message is shared!", Toast.LENGTH_SHORT)
									.show();
						} else if (task instanceof SharePhotoBackgroundTask) {
							Toast.makeText(
									ComposeActivity.this,
									imageQueueAdapter.getCount()
											+ " photos are shared!",
									Toast.LENGTH_SHORT).show();
						} else if (task instanceof UpdateStatusBackgroundTask) {
							Toast.makeText(
									ComposeActivity.this,
									imageQueueAdapter.getCount()
											+ "Twitter status is updated!",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
			}

			@Override
			public void onBackgroundTaskExecuted(final BackgroundTask task) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getApplicationContext(),
								"executing " + task.toString(),
								Toast.LENGTH_SHORT).show();
					}
				});
			}

			@Override
			public void onBackgroundTaskAdded(BackgroundTask task) {

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.post_menu, menu);

		// Calling super after populating the menu is necessary here to ensure
		// that the
		// action bar helpers have a chance to handle this event.
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.menu_location:
			Toast.makeText(this, "(fake) detecting your current location",
					Toast.LENGTH_SHORT).show();
			break;

		case R.id.menu_album:
			pickMultipleLocalImage();
			break;
		case R.id.menu_share:
			// construct and send message
			Toast.makeText(this, "(fake) queued message for posting",
					Toast.LENGTH_SHORT).show();

			onShare();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	static final int SELECT_PHOTO = 1;

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		switch (requestCode) {
		case SELECT_PHOTO:
			if (resultCode == RESULT_OK) {
				Uri selectedImage = imageReturnedIntent.getData();
				imageQueueAdapter.addImageUri(selectedImage);

				onSharePhoto();
			}
		}
	}

	void setupUI() {
		commentTextView = (AutoCompleteTextView) findViewById(R.id.comment);

		sendFacebook = (CheckBox) findViewById(R.id.sendFacebook);
		sendTwitter = (CheckBox) findViewById(R.id.sendTwitter);

		imageQueueAdapter = new ImageQueueAdapter(this);

		imageQueue = (Gallery) findViewById(R.id.imageQueue);
		imageQueue.setAdapter(imageQueueAdapter);
		imageQueue.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d("lab", "" + position);
				imageQueueAdapter.removeImageUri((Uri) imageQueueAdapter
						.getItem(position));
				return false;
			}
		});

		albumText = (EditText) findViewById(R.id.album);
		nameText = (EditText) findViewById(R.id.name);
		captionText = (EditText) findViewById(R.id.caption);
		descriptionText = (EditText) findViewById(R.id.description);
		linkText = (EditText) findViewById(R.id.link);
		sourceText = (EditText) findViewById(R.id.source);
	}

	void pickMultipleLocalImage() {
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, SELECT_PHOTO);
	}

	void onShare() {
		String message = getComment();
		Log.d("ComposeActivity",
				String.format("sending message: [%s]", message.toString()));

		if (sendFacebook.isChecked()) {
			onShareFacebook(message);
		}

		if (sendTwitter.isChecked()) {
			onShareTwitter(message);

		}
	}

	String getComment() {
		CharSequence comment = commentTextView.getText();
		if (comment == null) {
			comment = "";
		}
		return comment.toString();
	}

	ArrayList<Uri> getImage() {
		return imageQueueAdapter.getImageQueue();
	}

	// void addBackgroundTask(BackgroundTask task) {
	// Intent intent = new Intent(this, TaskService.class);
	// intent.putExtra("task", task);
	// startService(intent);
	// }

	void onShareFacebook(String message) {
		Log.d("ComposeActivity", String.format("sending message: to facebook"));

		String graphPath = "me/feed";
		String picture = "";
		String link = "";
		String name = "";
		String caption = "";
		String description = "";
		String source = "";

		BackgroundTask task = null;

		switch (mShareMode) {
		case PARAM_SHARE_MODE_LINK:
			int selectedImagePosition = imageQueue.getSelectedItemPosition();
			if (selectedImagePosition == Gallery.INVALID_POSITION) {
				// default is the first image
				selectedImagePosition = 0;
			}

			Uri selectedPictureUri = ((Uri) imageQueueAdapter
					.getItem(selectedImagePosition));
			if (selectedPictureUri != null) {
				mLinkPreview.picture = selectedPictureUri.toString();
			} else {
				mLinkPreview.picture = "";
			}
			// send link
			task = new ShareLinkBackgroundTask(graphPath, message, mLinkPreview);
			TaskService.addBackgroundTask(getApplicationContext(), task);
			break;
		case PARAM_SHARE_MODE_MESSAGE:
			// send link
			task = new ShareMessageBackgroundTask(graphPath, message);
			TaskService.addBackgroundTask(getApplicationContext(), task);
			break;
		case PARAM_SHARE_MODE_PHOTO:
			ArrayList<Uri> imageQueue = imageQueueAdapter.getImageQueue();

			ArrayList<String> imageFileList = new ArrayList<String>(
					imageQueue.size());
			String fullFilePath = null;
			Object[] fields = null;
			for (int i = 0; i < imageQueue.size(); ++i) {
				fields = ImageUtil
						.convertUriToFullPath(this, imageQueue.get(i));
				fullFilePath = (String) fields[ImageUtil.FIELD_IMAGE_FILE_PATH];
				imageFileList.add(fullFilePath);
			}
			task = new SharePhotoBackgroundTask(graphPath, message,
					imageFileList);
			TaskService.addBackgroundTask(getApplicationContext(), task);
			break;
		}
	}

	void onShareTwitter(String message) {
		Log.d("ComposeActivity", String.format("sending message: to Twitter"));

		BackgroundTask task = null;
		switch (mShareMode) {
		case PARAM_SHARE_MODE_MESSAGE:
			task = new UpdateStatusBackgroundTask(message);
			TaskService.addBackgroundTask(getApplicationContext(), task);
			break;
		case PARAM_SHARE_MODE_PHOTO:
			ArrayList<Uri> imageQueue = imageQueueAdapter.getImageQueue();

			ArrayList<String> imageFileList = new ArrayList<String>(
					imageQueue.size());
			String fullFilePath = null;
			Object[] fields = null;
			for (int i = 0; i < imageQueue.size(); ++i) {
				fields = ImageUtil
						.convertUriToFullPath(this, imageQueue.get(i));
				fullFilePath = (String) fields[ImageUtil.FIELD_IMAGE_FILE_PATH];
				imageFileList.add(fullFilePath);
			}
			task = new UploadPhotoBackgroundTask(message, imageFileList);
			TaskService.addBackgroundTask(getApplicationContext(), task);
			break;
		}
	}

	void onShareMessage() {
		mShareMode = PARAM_SHARE_MODE_MESSAGE;

		showShareMessageUI();
	}

	void onSharePhoto() {
		mShareMode = PARAM_SHARE_MODE_PHOTO;

		showSharePhotoUI();
	}

	void onShareLink(String link) {
		mShareMode = PARAM_SHARE_MODE_LINK;
		// setup ui
		showShareLinkUI();

		// process logic
		mSharedLink = link;

		BackgroundTask task = null;
		task = new LinkPreviewBackgroundTask(mSharedLink);
		TaskService.addBackgroundTask(getApplicationContext(), task);
	}

	void showShareLinkUI() {
		albumText.setVisibility(View.GONE);
		nameText.setVisibility(View.VISIBLE);
		captionText.setVisibility(View.VISIBLE);
		descriptionText.setVisibility(View.VISIBLE);
		linkText.setVisibility(View.VISIBLE);
		sourceText.setVisibility(View.VISIBLE);
	}

	void showShareMessageUI() {
		albumText.setVisibility(View.GONE);
		nameText.setVisibility(View.GONE);
		captionText.setVisibility(View.GONE);
		descriptionText.setVisibility(View.GONE);
		linkText.setVisibility(View.GONE);
		sourceText.setVisibility(View.GONE);
	}

	void showSharePhotoUI() {
		albumText.setVisibility(View.VISIBLE);
		nameText.setVisibility(View.GONE);
		captionText.setVisibility(View.GONE);
		descriptionText.setVisibility(View.GONE);
		linkText.setVisibility(View.GONE);
		sourceText.setVisibility(View.GONE);
	}
}
