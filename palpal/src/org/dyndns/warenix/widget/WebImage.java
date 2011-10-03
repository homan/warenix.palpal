package org.dyndns.warenix.widget;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import org.dyndns.warenix.util.DownloadImageTask;
import org.dyndns.warenix.util.DownloadImageTask.DownloadImageTaskCallback;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * An ImageView which manages itself loading of web image
 * 
 * @author warenix
 * 
 */
public class WebImage extends ImageView implements DownloadImageTaskCallback {
	String url;
	/**
	 * a download image task which actually download an image
	 */
	private DownloadImageTask task;

	public WebImage(Context context) {
		super(context);
	}

	public WebImage(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WebImage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * Call this to load photo either from the web or cache
	 * 
	 * @param url
	 */
	public void startLoading(String url) {
		if (task != null) {
			task.cancel(true);
		}

		this.url = url;

		// display loading icon
		this.setImageBitmap(null);

		task = new DownloadImageTask(this, url);
		task.execute(hashUrl(url));
	}

	HashMap<String, SoftReference<Bitmap>> imagePool;

	public void startLoading(String url,
			HashMap<String, SoftReference<Bitmap>> imagePool) {
		this.imagePool = imagePool;
		startLoading(url);
	}

	public void onDownloadComplete(String url, Bitmap bitmap) {
		if (imagePool != null) {
			imagePool.put(url, new SoftReference<Bitmap>(bitmap));
			if (task != null) {
				task.cancel(true);
				task = null;
			}
		}
	}

	public String toString() {
		return url;
	}

	public static String hashUrl(String url) {
		int to = url.length() - 1;
		int from = url.length() > 6 ? to - 6 : 0;
		String prefix = url.substring(from, to);
		return (prefix.hashCode() + "_" + url.hashCode() + ".jpg");
	}
}
