package org.dyndns.warenix.imageGallery;

import java.util.ArrayList;

import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;
import org.dyndns.warenix.pattern.baseListView.ListViewController;

import android.app.Activity;
import android.content.Context;
import android.widget.ListView;

public class ImageGalleryController extends ListViewController {

	public ImageGalleryController(Activity context, int resourceId) {
		super(context, resourceId);
	}

	@Override
	public ListViewAdapter setupListViewAdapter(Context context) {
		((ListView) listView).setDividerHeight(0);

		ImageGalleryAdapter listAdapter = new ImageGalleryAdapter(context);
		return listAdapter;
	}

	public void displayImageList(ImageList imageList) {
		ArrayList<String> imageUrl = imageList.getImageList();
		for (String url : imageUrl) {
			((ImageGalleryAdapter) listAdapter).addImage(url);
		}
		((ImageGalleryAdapter) listAdapter).notifyDataSetInvalidated();
	}

	public void refresh() {
		String imageUrl[] = {
				"http://www.mustreadstuff.com/wp-content/uploads/Android/Wallpapers/4290_3D-duck-iphone-wallpaper.jpg",
				"https://lh6.googleusercontent.com/-DF-VcOIA_dw/Tjzy6nLiCcI/AAAAAAAAIeE/YFxPkVgx9yU/s800/2011-08-06-13-13-17-110.jpg",
				"https://lh6.googleusercontent.com/-Rm_aSJzMzR0/TjzyV6rdVDI/AAAAAAAAIc8/RJXdeIcF3BI/s1024/2011-08-06-15-04-13-111.jpg",
				"https://lh6.googleusercontent.com/-cXmEzsXYUOs/TjzyTgAbztI/AAAAAAAAIaA/IW79CZEES3Q/s800/2011-08-06-15-05-06-240.jpg",
				"https://lh5.googleusercontent.com/-bD4ZEB0OUaY/TjzyRd5scmI/AAAAAAAAIdE/g4xo1zVgzgI/s1024/2011-08-06-15-05-53-331.jpg",
				"https://lh6.googleusercontent.com/-c_q_H5j610w/Tj0Vwh6mQyI/AAAAAAAAIbo/i26zPZk8GN8/s800/2011-08-06-17-49-03-210.jpg",
				"https://lh4.googleusercontent.com/-UqqZYrmm6ks/Tj0VpwCQIpI/AAAAAAAAIbk/-eTT7jSIs2E/s800/2011-08-06-16-34-03-554.jpg",
				"https://lh5.googleusercontent.com/-wy0VI-MP9LA/Tj0VdcdXuhI/AAAAAAAAIbg/m_6C6arFI4c/s800/2011-08-06-16-49-24-412.jpg",
				"https://lh6.googleusercontent.com/-MWsD8dtVl0M/Tj0VTvLBoGI/AAAAAAAAIbc/D8GsfXRvBBY/s800/2011-08-06-17-01-36-887.jpg" };

		for (int i = 0; i < imageUrl.length; ++i) {
			((ImageGalleryAdapter) listAdapter).addImage(imageUrl[i]);
		}
	}
}
