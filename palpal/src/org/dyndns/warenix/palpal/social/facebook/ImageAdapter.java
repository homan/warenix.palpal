package org.dyndns.warenix.palpal.social.facebook;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;

import org.dyndns.warenix.widget.WebImage;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Gallery.LayoutParams;

public class ImageAdapter extends BaseAdapter {
	int mGalleryItemBackground;
	private Context mContext;
	private ArrayList<String> mImageList;
	HashMap<String, SoftReference<Bitmap>> imagePool;

	public ImageAdapter(Context context, ArrayList<String> imageList,
			HashMap<String, SoftReference<Bitmap>> imagePool) {
		super();
		mContext = context;
		mImageList = imageList;
		this.imagePool = imagePool;
	}

	// private Integer[] mImageIds = { R.drawable.icon };

	public int getCount() {
		if (mImageList == null) {
			return 0;
		}
		return mImageList.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		WebImage webImage = new WebImage(mContext);

		SoftReference<Bitmap> ref = imagePool.get(mImageList.get(position));
		if (ref == null) {
			webImage.startLoading(mImageList.get(position), imagePool);
		} else {
			Bitmap bm = ref.get();
			if (bm == null) {
				webImage.startLoading(mImageList.get(position), imagePool);
			} else {
				webImage.setImageBitmap(bm);
			}
		}

		webImage.setLayoutParams(new Gallery.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		webImage.setScaleType(ImageView.ScaleType.FIT_XY);

		return webImage;
	}
}