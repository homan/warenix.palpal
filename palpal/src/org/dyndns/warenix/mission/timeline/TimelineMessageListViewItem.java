package org.dyndns.warenix.mission.timeline;

import java.util.Date;

import org.dyndns.warenix.image.CachedWebImage;
import org.dyndns.warenix.image.WebImage.WebImageListener;
import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;
import org.dyndns.warenix.pattern.baseListView.ListViewItem;
import org.dyndns.warenix.util.WLog;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Centalized message item view
 * 
 * @author warenix
 * 
 */
public abstract class TimelineMessageListViewItem extends ListViewItem
		implements Comparable<TimelineMessageListViewItem> {

	private static final String TAG = "TimelineMessageListViewItem";
	protected int messageType;
	protected ListViewAdapter adapter;

	// public TimelineMessageListViewItem() {
	// messageType = setMessageType();
	// }

	public TimelineMessageListViewItem(ListViewAdapter adapter) {
		this.adapter = adapter;
		messageType = setMessageType();
	}

	public int compareTo(TimelineMessageListViewItem another) {
		// sort in descending order
		return getDate().before(another.getDate()) ? 1 : -1;
	}

	public abstract Date getDate();

	public abstract int setMessageType();

	public void setProfileImage(final ImageView imageView, final int position,
			String imageUrl) {
//		if (!adapter.isIdle()) {
//			imageView.setImageResource(R.drawable.ic_launcher);
//			WLog.d(TAG, "warenix, list is not ready, skip " + position);
//			return;
//		}

		imageView.setImageResource(R.drawable.ic_launcher);
		CachedWebImage webImage2 = new CachedWebImage();
		webImage2.setWebImageListener(new WebImageListener() {

			@Override
			public void onImageSet(ImageView image, Bitmap bitmap) {
				// if (adapter.isChildVisible(position)) {
				WLog.d(TAG, "onImageSet for position " + position
						+ " set bitmap");
				imageView.setImageBitmap(bitmap);
				// } else {
				// WLog.d(TAG, "onImageSet for position " + position
				// + " recycle bitmap");
				// ImageUtil.recycleBitmap(bitmap);
				// }
			}

			@Override
			public void onImageSet(ImageView image) {
			}
		});

		webImage2.startDownloadImage("" + position, imageUrl, imageView, null);
	}

}
