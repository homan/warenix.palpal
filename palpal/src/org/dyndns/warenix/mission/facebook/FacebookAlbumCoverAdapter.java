package org.dyndns.warenix.mission.facebook;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.dyndns.warenix.image.CachedWebImage;
import org.dyndns.warenix.image.WebImage.WebImageListener;
import org.dyndns.warenix.palpal.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class FacebookAlbumCoverAdapter extends BaseAdapter {
	Context context;
	ArrayList<Uri> imageUriList;

	int GalItemBg;
	LayoutInflater inflater;

	int PADDING_IN_DP = 10;

	static float scale;

	static HashMap<Uri, Bitmap> bitmapCache = new HashMap<Uri, Bitmap>();

	static class ViewHolder {
		public ImageView image;
	}

	public FacebookAlbumCoverAdapter(Context context) {
		super();
		this.context = context;
	}

	@Override
	public int getCount() {
		if (imageUriList != null) {
			return imageUriList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (imageUriList != null && position < imageUriList.size()) {
			return imageUriList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView != null) {
			Log.d("lab", "not null");
		}
		View view = convertView;
		if (view == null) {
			FacebookAlbumCoverAdapter.ViewHolder viewHolder = new ViewHolder();
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.image_preview, null);
			viewHolder.image = (ImageView) view.findViewById(R.id.imagePreview);
			viewHolder.image.setLayoutParams(new Gallery.LayoutParams(
					Gallery.LayoutParams.FILL_PARENT,
					Gallery.LayoutParams.FILL_PARENT));
			viewHolder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);

			int mPaddingInPixels;
			if (scale == 0) {
				scale = context.getResources().getDisplayMetrics().density;
			}
			mPaddingInPixels = (int) (PADDING_IN_DP * scale + 0.5f);
			mPaddingInPixels = mPaddingInPixels + 5;
			viewHolder.image.setPadding(mPaddingInPixels, mPaddingInPixels,
					mPaddingInPixels, mPaddingInPixels);

			view.setTag(viewHolder);
		}

		final FacebookAlbumCoverAdapter.ViewHolder viewHolder = (FacebookAlbumCoverAdapter.ViewHolder) view
				.getTag();

		Uri selectedImage = (Uri) getItem(position);
		String scheme = selectedImage.getScheme();
		if (scheme.equals("http") || scheme.equals("https")) {
			// web content
			CachedWebImage webImage2 = new CachedWebImage();
			webImage2.setWebImageListener(new WebImageListener() {

				@Override
				public void onImageSet(ImageView image, Bitmap bitmap) {
					viewHolder.image.setImageBitmap(bitmap);
				}

				@Override
				public void onImageSet(ImageView image) {
				}
			});

			webImage2.startDownloadImage("" + position,
					selectedImage.toString(), viewHolder.image, null);

		} else {
			// local content
			InputStream imageStream;
			try {

				Bitmap previewImage = null;

				previewImage = bitmapCache.get(selectedImage);

				if (previewImage == null) {
					imageStream = context.getContentResolver().openInputStream(
							selectedImage);
					Bitmap yourSelectedImage = BitmapFactory
							.decodeStream(imageStream);
					previewImage = Bitmap.createScaledBitmap(yourSelectedImage,
							120, 90, false);
					bitmapCache.put(selectedImage, previewImage);
					yourSelectedImage.recycle();
				}

				Log.d("lab", "selected " + selectedImage);

				viewHolder.image.setImageBitmap(previewImage);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		return view;
	}

	public void addImageUri(Uri imageUri) {
		if (imageUriList == null) {
			imageUriList = new ArrayList<Uri>();
		}

		imageUriList.add(imageUri);
		notifyDataSetChanged();
	}

	public void removeImageUri(Uri imageUri) {
		imageUriList.remove(imageUri);
		notifyDataSetChanged();
	}

	public ArrayList<Uri> getImageQueue() {
		return imageUriList;
	}

}