package org.dyndns.warenix.palpal.social.facebook.task;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import org.dyndns.warenix.palpal.social.facebook.FacebookException;
import org.dyndns.warenix.palpal.social.facebook.LinkPreview;
import org.dyndns.warenix.palpal.social.facebook.PreviewImageAdapter;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookAPI;
import org.dyndns.warenix.widget.WebImage;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.Gallery;

public class FetchLinkPreviewAsyncTask extends
		AsyncTask<String, Void, LinkPreview> {

	Context context;
	WebImage picture;
	HashMap<String, SoftReference<Bitmap>> imagePool;
	AlertDialog.Builder alert;

	Gallery previewGallery;

	public FetchLinkPreviewAsyncTask(Context context,
			AlertDialog.Builder alert, WebImage picture,
			Gallery previewGallery,
			HashMap<String, SoftReference<Bitmap>> imagePool) {
		this.context = context;
		this.picture = picture;
		this.imagePool = imagePool;
		this.alert = alert;
		this.previewGallery = previewGallery;
	}

	@Override
	protected LinkPreview doInBackground(String... params) {

		String link = params[0];

		String responseString;
		try {
			responseString = FacebookAPI.linksPreview(link);

			if (responseString == null) {
			}
			JSONObject json = new JSONObject(responseString);
			LinkPreview preview = new LinkPreview(json);
			return preview;

		} catch (FacebookException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	protected void onPostExecute(final LinkPreview linkPreview) {
		PreviewImageAdapter imageAdapter = new PreviewImageAdapter(context,
				linkPreview.imageList, imagePool);

		previewGallery.setAdapter(imageAdapter);

		previewGallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View v, int position,
					long id) {
				linkPreview.selectedImageURL = linkPreview.imageList
						.get(position);

			}
		});

		// Set an EditText view to get user input
		final EditText input = new EditText(context);
		alert.setView(input);		
	}

}
