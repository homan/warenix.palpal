package org.dyndns.warenix.palpal.social.twitter.activity;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dyndns.warenix.db.SimpleStorable;
import org.dyndns.warenix.db.SimpleStorableManager;
import org.dyndns.warenix.embedly.Embedable;
import org.dyndns.warenix.embedly.EmbedlyMaster;
import org.dyndns.warenix.palpal.bubbleMessage.BubbleMessage;
import org.dyndns.warenix.palpal.message.MessageDBManager;
import org.dyndns.warenix.palpal.social.twitter.storable.EmbedableMessageStorable;
import org.dyndns.warenix.palpaltwitter.R;
import org.dyndns.warenix.widget.WebImage;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class ImageAlbumActivity extends Activity {
	ArrayList<Embedable> embedableList = new ArrayList<Embedable>();
	WebImage image;
	ImageButton next;
	ImageButton prev;
	int currentIndex;
	TextView page;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_album);

		image = (WebImage) findViewById(R.id.image);

		embedableList.addAll(getEmbedableList());

		next = (ImageButton) findViewById(R.id.next);
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showOriginalImage(++currentIndex);
				updatePagingButton();
			}
		});

		prev = (ImageButton) findViewById(R.id.prev);
		prev.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showOriginalImage(--currentIndex);
				updatePagingButton();
			}
		});

		page = (TextView) findViewById(R.id.page);

		updatePagingButton();

		currentIndex = 0;
		showOriginalImage(currentIndex);

		// ImageAlbumController controller = new ImageAlbumController(this,
		// R.id.gridview);
		// controller.showEmbdableImage();

		// refreshMessage();
		// embedableList.clear();
		// embedableList.addAll(getEmbedableList());
		// Log.d("palpal", "embedableList size:" + embedableList.size());
		// showOriginalImage();
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

	void showOriginalImage(int index) {

		image.recycleBitmap();
		// try {
		Embedable embedable = embedableList.get(index);
		image.startLoading(embedable.url);
		// PalPalDialog.showImageDialog(this, new URI(embedable.url));
		// } catch (URISyntaxException e) {
		// e.printStackTrace();
		// }

		WebImage profileImage = (WebImage) findViewById(R.id.profileImage);
		profileImage.recycleBitmap();
		String profileImageUrl = embedable.thumbnailUrl;
		if (profileImageUrl != null) {
			profileImage.startLoading(profileImageUrl);
		} else {
			profileImage.setImageBitmap(null);
		}

		TextView replyToUsernametext = (TextView) findViewById(R.id.username);
		replyToUsernametext.setText(embedable.authorName);

		TextView replyToStatusText = (TextView) findViewById(R.id.message);
		replyToStatusText.setText(embedable.description + embedable.url);

	}

	void refreshMessage() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				// fetch message
				Context appContext = getApplicationContext();
				MessageDBManager messageDb = new MessageDBManager(appContext);

				ArrayList<BubbleMessage> messageList = messageDb
						.getMessageList(50, -1);

				int count = 1;
				for (BubbleMessage message : messageList) {
					Log.d("warenix", "check embedly #" + count++);

					Pattern pattern = Pattern.compile("http://(\\S+)");
					Matcher matcher = pattern.matcher(message.message);

					while (matcher.find()) {
						String mediaUrl = matcher.group();
						Embedable embedable = EmbedlyMaster
								.getEmbedable(mediaUrl);
						if (embedable != null
								&& embedable.type.equals(Embedable.TYPE_PHOTO)) {
							Log.d("warenix", String.format(
									"embedly title %s url %s", embedable.title,
									embedable.url));

							storeEmbedableMessage(message, embedable);

							embedableList.add(embedable);
						}
					}
				}

				return null;
			}

			protected void onPostExecute(Void v) {
				Log.d("warenix",
						"embedly refreshed count:" + embedableList.size());
				// show();
			}

		}.execute();

	}

	void updatePagingButton() {
		next.setEnabled((currentIndex + 1) < embedableList.size());
		prev.setEnabled(currentIndex > 0);

		page.setText(String.format("%d/%d", currentIndex + 1,
				embedableList.size()));
	}
}
