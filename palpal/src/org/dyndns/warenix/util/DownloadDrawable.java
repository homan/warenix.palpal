package org.dyndns.warenix.util;

import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;

public class DownloadDrawable extends Drawable {

	String url;

	public DownloadDrawable(String url) {
		this.url = url;
	}

	@Override
	public void draw(Canvas canvas) {

		try {
			Bitmap bitmap = WebContent.loadPhotoBitmap(new URL(url),
					"palpal/caches", url.hashCode() + ".jpg");

			canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2,
					-bitmap.getHeight() / 2, null);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getOpacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setAlpha(int alpha) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		// TODO Auto-generated method stub

	}

}
