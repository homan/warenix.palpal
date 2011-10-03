package org.dyndns.warenix.palpal.social.twitter.task;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dyndns.warenix.palpal.social.facebook.PreviewImageAdapter;
import org.dyndns.warenix.palpal.social.twitter.imageURLParser.CamplusParser;
import org.dyndns.warenix.palpal.social.twitter.imageURLParser.InstagramParser;
import org.dyndns.warenix.palpal.social.twitter.imageURLParser.TwitpicParser;
import org.dyndns.warenix.palpal.social.twitter.imageURLParser.YfrogParser;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Gallery;

public class FetchImageTask extends AsyncTask<String, Void, Void> {
	Gallery preview;
	final ArrayList<String> imageList = new ArrayList<String>();
	Context context;
	HashMap<String, SoftReference<Bitmap>> imagePool;

	public FetchImageTask(Context context, Gallery preview,
			HashMap<String, SoftReference<Bitmap>> imagePool) {
		this.context = context;
		this.preview = preview;
		this.imagePool = imagePool;
	}

	@Override
	protected Void doInBackground(String... params) {

		String text = params[0];
		Pattern pattern = Pattern
				.compile(
						"\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
						Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher(text);
		String httpURL = "";
		String imageURL = null;
		while (m.find()) {
			httpURL = m.group();
			Log.d("warenix", httpURL);
			if (httpURL.contains("twitpic.com")
					|| httpURL.contains("instagr.am")
					|| httpURL.contains("campl.us")
					|| httpURL.contains("yfrog.com")) {
				imageURL = TwitpicParser.extractImageUrl(httpURL);
				if (imageURL != null) {
					imageList.add(imageURL);
				} else {
					imageURL = InstagramParser.extractImageUrl(httpURL);
					if (imageURL != null) {
						imageList.add(imageURL);
					} else {
						imageURL = CamplusParser.extractImageUrl(httpURL);
						if (imageURL != null) {
							imageList.add(imageURL);
						} else {
							imageURL = YfrogParser.extractImageUrl(httpURL);
							if (imageURL != null) {
								imageList.add(imageURL);
							}
						}
					}
				}
			}
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (imageList.size() > 0) {
			preview.setVisibility(View.VISIBLE);
			preview.setAdapter(new PreviewImageAdapter(context, imageList,
					imagePool));
		}
	}
}
