package org.dyndns.warenix.widget;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import org.dyndns.warenix.palpal.animation.AnimationEffect;
import org.dyndns.warenix.util.DownloadImageTask;
import org.dyndns.warenix.util.DownloadImageTask.DownloadImageTaskCallback;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * An ImageView which manages itself loading of web image
 * 
 * @author warenix
 * 
 */
public class WebImage extends ImageView implements DownloadImageTaskCallback {
	String url;
	Context context;
	/**
	 * a download image task which actually download an image
	 */
	private DownloadImageTask task;

	public WebImage(Context context) {
		super(context);
		this.context = context;
	}

	public WebImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public WebImage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	/**
	 * Call this to load photo either from the web or cache
	 * 
	 * @param url
	 */
	public void startLoading(String url) {
		if (url.equals(this.url)) {
			// return;
		}

		if (task != null) {
			task.cancelTask(true);
		}

		// if (imagePool != null) {
		// SoftReference<Bitmap> ref = imagePool.get(url);
		// if (ref != null) {
		// Bitmap bitmap = ref.get();
		// if (bitmap != null) {
		// Log.d("warenix", "webimage use cache");
		// setImageBitmap(bitmap);
		// return;
		// }
		// }
		// }
		this.url = url;

		// display loading icon
		this.setImageBitmap(null);

		task = new DownloadImageTask(this, url);
		task.execute(hashUrl(url));
	}

	HashMap<String, SoftReference<Bitmap>> imagePool;

	// public void startLoading(String url,
	// HashMap<String, SoftReference<Bitmap>> imagePool) {
	// this.imagePool = imagePool;
	// startLoading(url);
	// }

	public void onDownloadComplete(String url, final Bitmap bitmap) {
		((Activity) context).runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// setScaleType(ImageView.ScaleType.CENTER_CROP);

				setAdjustViewBounds(true);
				setImageBitmap(bitmap);
				AnimationEffect.playFetchPageAnimation(context, WebImage.this,
						true);
			}
		});

		if (imagePool != null) {
			imagePool.put(url, new SoftReference<Bitmap>(bitmap));
			if (task != null) {
				task.cancel(true);
				task = null;
			}
		}

		imagePool = null;

	}

	public String toString() {
		return url;
	}

	public static String hashUrl(String url) {
		int to = url.length() - 1;
		int from = url.length() > 6 ? to - 6 : 0;
		String prefix = url.substring(from, to);
		return (prefix.hashCode() + "_" + url.hashCode() + url.substring(url
				.length() - 4));
	}

	public void recycleBitmap() {
		BitmapDrawable bitmapDrawable = (BitmapDrawable) getDrawable();
		if (bitmapDrawable != null) {
			Bitmap bitmap = bitmapDrawable.getBitmap();
			if (bitmap != null && !bitmap.isRecycled()) {
				Log.d("warenix", "recycled bitmap before use");
				bitmap.recycle();
				bitmap = null;
			}
			bitmapDrawable = null;
		}
	}
}
