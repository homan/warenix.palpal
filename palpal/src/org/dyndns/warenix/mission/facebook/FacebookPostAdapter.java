package org.dyndns.warenix.mission.facebook;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.dyndns.warenix.lab.compat1.util.Memory;
import org.dyndns.warenix.mission.facebook.FacebookObject.Comment;
import org.dyndns.warenix.mission.timeline.TimelineAsyncAdapter;
import org.dyndns.warenix.mission.timeline.TimelineMessageListViewItem;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;

import com.facebook.android.Facebook;

/**
 * Read individual Facebook post detail
 * 
 * @author warenix
 * 
 */
public class FacebookPostAdapter extends TimelineAsyncAdapter {

	String graphId;
	boolean isRefreshing;

	public FacebookPostAdapter(Context context, ListView listView,
			String graphId) {
		super(context, listView);
		this.graphId = graphId;

		setNeedSortingByTime(false);

		Runnable facebook = new Runnable() {
			public void run() {
				getFacebookFeed(FacebookPostAdapter.this.graphId, "" + 25,
						"" + 0);
				notifyRunnableDone();
			}
		};

		addRunnable(facebook);
	}

	void getFacebookFeed(String graphPath, String pageLimit, String offset) {
		Facebook facebook = Memory.getFacebookClient();
		if (facebook != null) {

			try {
				// graphPath = "128982007419_10150375358527420";
				Bundle parameters = new Bundle();
				parameters.putString("limit", pageLimit);
				parameters.putString("offset", offset);
				String responseString = facebook.request(graphPath, parameters);

				constructFacebookListItem(responseString, dataList);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	void constructFacebookListItem(String responseString,
			ArrayList<TimelineMessageListViewItem> dataList) {
		// get individual facebook post
		try {
			JSONObject responseJSON = new JSONObject(responseString);
			FacebookObject facebookObject = new FacebookObject(responseJSON);
			dataList.add(new FacebookMessageListItem(facebookObject,
					FacebookPostAdapter.this));

			// add comments
			if (facebookObject.commentsList != null) {
				for (Comment comment : facebookObject.commentsList) {
					dataList.add(new FacebookCommentListItem(comment,
							FacebookPostAdapter.this));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public int getItemViewType(int position) {
		// this is original post
		if (position == 0) {
			return 0;
		}
		// others are comments
		return 1;
	}

	public int getViewTypeCount() {
		return 2;
	}
}
