package org.dyndns.warenix.palpal.social.facebook.task;

import org.dyndns.warenix.palpal.social.facebook.FacebookException;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookAPI;
import org.dyndns.warenix.palpal.social.facebook.vo.FacebookPost;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.Album;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.LinkFeed;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.PhotoFeed;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.StatusFeed;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.VideoFeed;
import org.dyndns.warenix.util.ToastUtil;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class ShareAsyncTask extends AsyncTask<Object, Void, FacebookException> {

	Context context;

	public ShareAsyncTask(Context context) {
		this.context = context;
	}

	@Override
	protected FacebookException doInBackground(Object... params) {
		if (params.length < 3) {

			return new FacebookException("palpal api error",
					"not enough parameters, expected 3");
		}

		FacebookPost sharedFeed = (FacebookPost) params[0];
		Album album = (Album) params[1];
		String profileId = (String) params[2];

		boolean success = true;
		try {
			if (sharedFeed instanceof LinkFeed) {
				success = FacebookAPI.share((LinkFeed) sharedFeed, profileId);
			} else if (sharedFeed instanceof VideoFeed) {
				success = FacebookAPI.share((VideoFeed) sharedFeed, profileId);
			} else if (sharedFeed instanceof PhotoFeed) {

				if (album == null) {
					return new FacebookException("forget to pick an album",
							"please pick an album");
				}

				// determine upload from url or local file
				Log.d("palpal",
						String.format("share photo to album %s", album.id));

				if (((PhotoFeed) sharedFeed).picture.startsWith("http")) {
					FacebookAPI.uploadPhotoFromURL((PhotoFeed) sharedFeed,
							album.id);
				} else {
					FacebookAPI.uploadPhotoFromFile((PhotoFeed) sharedFeed,
							album.id);
				}
			} else if (sharedFeed instanceof StatusFeed) {
				success = FacebookAPI.share((StatusFeed) sharedFeed, profileId);
			}
		} catch (FacebookException e) {
			return e;
		}
		if (!success) {
			return new FacebookException("facebook api error",
					"facebook returns false");
		}

		return null;
	}

	protected void onPostExecute(FacebookException e) {
		if (e != null) {
			ToastUtil.showNotification(context, "fail to share", e.type,
					e.error, null, 1000);
		} else {
			ToastUtil.showQuickToast(context, "share +ed");
		}
	}
}
