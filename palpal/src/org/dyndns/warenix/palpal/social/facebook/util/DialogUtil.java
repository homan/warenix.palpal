package org.dyndns.warenix.palpal.social.facebook.util;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import org.dyndns.warenix.palpal.social.facebook.LinkPreview;
import org.dyndns.warenix.palpal.social.facebook.PreviewImageAdapter;
import org.dyndns.warenix.palpal.social.facebook.vo.FacebookEditable;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.LinkFeed;
import org.dyndns.warenix.widget.WebImage;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.TextView;

public class DialogUtil {

	/**
	 * show a dialog for user to enter text. if user press ok, the entered text
	 * will be set to the textView
	 * 
	 * @param context
	 * @param title
	 *            dialog title
	 * @param message
	 *            dialog message
	 * @param textView
	 *            the entered text will then set to the textView
	 * @param defaultValue
	 *            default text set to the textView and displayed to user
	 * @param key
	 *            the FacebookPost attribute key so the user entered text will
	 *            update the FacebookPost object
	 * @param post
	 *            the FacebookPost object being updated
	 */
	public static void showInputDialogForTextView(final Context context,
			final String title, final String message, final TextView textView,
			final String defaultValue, final String key,
			final FacebookEditable post) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);

		alert.setTitle(title);
		alert.setMessage(message);

		// Set an EditText view to get user input
		final EditText input = new EditText(context);
		input.setText(defaultValue);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				// Do something with value!
				textView.setText(value);
				post.updateAttributeByKey(key, value);
				// update data model

			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});

		alert.show();
	}

	/**
	 * show a dialog of previews of a given link. used in
	 * showLinkPreviewForWebImage()
	 */
	static boolean isCancelled = false;

	/**
	 * routine to fetch preview of linkFeed, display previews in gallery, update
	 * the selected image to webImage.
	 * 
	 * @param context
	 * @param title
	 * @param message
	 * @param linkFeed
	 * @param webImage
	 * @param imagePool
	 */
	public static void showLinkPreviewForWebImage(final Context context,
			final String title, final String message, final LinkFeed linkFeed,
			final WebImage webImage,
			final HashMap<String, SoftReference<Bitmap>> imagePool) {

		isCancelled = false;

		final ProgressDialog pd = ProgressDialog.show(context, "Working..",
				"Fetching link previews", true, true);

		WindowManager.LayoutParams lp = pd.getWindow().getAttributes();
		lp.dimAmount = 0.0f;
		pd.getWindow().setAttributes(lp);
		pd.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

		pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				isCancelled = true;
			}
		});

		final Handler messageHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				if (isCancelled) {
					return;
				}

				pd.dismiss();

				AlertDialog.Builder alert = new AlertDialog.Builder(context);

				alert.setTitle(title);
				alert.setMessage(message);

				final LinkPreview linkPreview = (LinkPreview) msg.obj;
				PreviewImageAdapter imageAdapter = new PreviewImageAdapter(
						context, linkPreview.imageList, imagePool);

				Gallery previewGallery = new Gallery(context);
				previewGallery.setAdapter(imageAdapter);

				previewGallery
						.setOnItemClickListener(new OnItemClickListener() {
							public void onItemClick(AdapterView parent, View v,
									int position, long id) {
								linkPreview.selectedImageURL = linkPreview.imageList
										.get(position);

							}
						});

				alert.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// String value = input.getText().toString();
								// // Do something with value!
								// textView.setText(value);
								webImage.startLoading(linkPreview.selectedImageURL);

								linkFeed.updateAttributeByKey("picture",
										linkPreview.selectedImageURL);

							}
						});

				alert.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// Canceled.
							}
						});

				alert.setView(previewGallery);
				alert.show();

			}

		};

		// Here is the heavy-duty thread
		Thread t = new Thread() {

			public void run() {

				// do expensive
				final LinkPreview linkPreview = linkFeed.fetchLinkPreview();

				// Send update to the main thread
				Message message = Message.obtain();
				message.obj = linkPreview;
				message.what = 1;

				messageHandler.sendMessage(message);
			}
		};
		t.start();

		// new FetchLinkPreviewAsyncTask(context, alert, webImage,
		// previewGallery,
		// imagePool).execute(link);
	}

}
