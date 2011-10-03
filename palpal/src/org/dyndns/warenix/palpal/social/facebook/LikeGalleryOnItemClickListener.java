package org.dyndns.warenix.palpal.social.facebook;

import java.util.ArrayList;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

/**
 * adapter for the like comment gallery.
 * 
 * @author warenix
 * 
 */
public class LikeGalleryOnItemClickListener implements
		AdapterView.OnItemClickListener {

	ArrayList<Like> likeList;

	public LikeGalleryOnItemClickListener(ArrayList<Like> likeList) {
		this.likeList = likeList;
	}

	public void onItemClick(AdapterView parent, View v, int position, long id) {
		Like like = likeList.get(position);
		Log.v("palpal", like.toString());
	}

}
