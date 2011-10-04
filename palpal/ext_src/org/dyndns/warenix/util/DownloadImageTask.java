package org.dyndns.warenix.util;

import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

/**
 * download image in background using AsyncTask with sd card cache.
 * 
 * optionally execute(String saveAsFileName)
 * 
 * @author warenix
 * 
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

	final String LOG_TAG = this.getClass().getSimpleName().toString();

	public static String CACHE_DIR = "palpal/caches";

	String product_image_url;
	// WebImage product_image_view;
	SoftReference<DownloadImageTaskCallback> product_image_view_reference;

	boolean isCancelled = false;

	/**
	 * download an image from web and attach the image to the image view
	 * 
	 * @param image_view
	 *            the image will be displayed in this image view
	 * @param url
	 *            the url to the image needed to be downloaded
	 */
	public DownloadImageTask(DownloadImageTaskCallback image_view, String url) {
		super();

		// this.product_image_view = image_view;
		this.product_image_url = url;

		product_image_view_reference = new SoftReference<DownloadImageTaskCallback>(
				image_view);
	}

	public void setImageViewAndURL(DownloadImageTaskCallback image_view,
			String url) {
		// this.product_image_view = image_view;
		product_image_view_reference = new SoftReference<DownloadImageTaskCallback>(
				image_view);
		this.product_image_url = url;
	}

	protected void onPostExecute(Bitmap result) {
		Log.i(LOG_TAG, "set image " + product_image_url);
		DownloadImageTaskCallback product_image_view = product_image_view_reference
				.get();
		if (result != null && product_image_view != null) {

			// notify completed
			// this.product_image_view.onDownloadComplete(product_image_url,
			// result);
			product_image_view.onDownloadComplete(product_image_url, result);
		}
	}

	protected Bitmap doInBackground(String... urls) {

		String saveAsFileName = Uri.encode(product_image_url);
		if (urls.length > 0) {
			saveAsFileName = urls[0];
		}
		Bitmap bitmap = null;
		try {
			bitmap = WebContent.loadPhotoBitmap(new URL(product_image_url),
					CACHE_DIR, saveAsFileName);
			Log.i("warenix", "downloadImageTask finish " + product_image_url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return bitmap;
	}

	/**
	 * cancel aysnctask
	 * 
	 * @param mayInterruptIfRunning
	 * @return
	 */
	public boolean cancelTask(Boolean mayInterruptIfRunning) {
		isCancelled = true;
		return super.cancel(mayInterruptIfRunning);
	}

	public interface DownloadImageTaskCallback {
		public void onDownloadComplete(String url, Bitmap bitmap);

	}

}
