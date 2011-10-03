package org.dyndns.warenix.palpal.social.facebook.task;

import java.util.ArrayList;

import org.dyndns.warenix.palpal.social.facebook.FacebookException;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookAPI;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookUtil;
import org.dyndns.warenix.palpal.social.facebook.vo.FacebookPost;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.Profile;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;

public class SearchLikesAsyncTask extends
		AsyncTask<Object, Void, FacebookException> {

	SearchLikesListener listener;
	ArrayList<FacebookPost> friendList;

	public SearchLikesAsyncTask(SearchLikesListener listener) {
		this.listener = listener;
	}

	@Override
	protected FacebookException doInBackground(Object... params) {

		if (params.length < 1) {
			return null;
		}

		String userName = (String) params[0];
		Bundle parameters = (Bundle) params[1];
		// String responseString = FacebookAPI
		// .getUserFriends(userName, parameters);
		String responseString = FacebookAPI.getUserLikes(userName, parameters);

		// parse response
		try {
			FacebookUtil.checkFacebookException(responseString);

			JSONObject json = new JSONObject(responseString);
			JSONArray data = json.getJSONArray("data");

			friendList = new ArrayList<FacebookPost>();
			Profile profile;

			for (int i = 0; i < data.length(); ++i) {
				profile = new Profile(data.getJSONObject(i));
				friendList.add(profile);
			}

		} catch (JSONException e) {
			return new FacebookException("fail to fetch friends",
					e.getMessage());
		} catch (FacebookException e) {
			return e;
		}

		return null;
	}

	protected void onPostExecute(FacebookException e) {
		if (e != null) {
			listener.onLikesLoadedError(e);
		}

		if (listener != null) {
			listener.onLikesLoaded(friendList);
		}
	}

	public interface SearchLikesListener {
		public void onLikesLoaded(ArrayList<FacebookPost> friendList);

		public void onLikesLoadedError(FacebookException e);

	}

}
