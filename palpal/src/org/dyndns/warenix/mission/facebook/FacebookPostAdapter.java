package org.dyndns.warenix.mission.facebook;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.dyndns.warenix.lab.compat1.util.AndroidUtil;
import org.dyndns.warenix.lab.compat1.util.Memory;
import org.dyndns.warenix.mission.facebook.FacebookObject.Comment;
import org.dyndns.warenix.mission.timeline.TimelineMessageListViewItem;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.facebook.android.Facebook;

/**
 * Read individual Facebook post detail
 * 
 * @author warenix
 * 
 */
public class FacebookPostAdapter extends ListViewAdapter {

	String graphId;
	boolean isRefreshing;

	public FacebookPostAdapter(Context context, ListView listView,
			String graphId) {
		super(context, listView);
		this.graphId = graphId;
	}

	public void asyncRefresh() {
		if (!isRefreshing) {
			isRefreshing = true;
			new AsyncRefreshTask().execute();
		}

	}

	// public void refresh() {
	// if (!isRefreshing) {
	// isRefreshing = true;
	// new AsyncRefreshTask().execute();
	// }
	// }

	class AsyncRefreshTask extends AsyncTask<Void, Void, Void> {

		ArrayList<TimelineMessageListViewItem> dataList = new ArrayList<TimelineMessageListViewItem>();

		@Override
		protected Void doInBackground(Void... params) {

			Facebook facebook = Memory.getFacebookClient();
			if (facebook != null) {

				try {
					String graphPath = graphId;
					// graphPath = "128982007419_10150375358527420";
					Bundle parameters = new Bundle();
					// parameters.putString("limit", "50");
					String responseString = facebook.request(graphPath,
							parameters);

					// get individual facebook post
					try {
						JSONObject responseJSON = new JSONObject(responseString);
						FacebookObject facebookObject = new FacebookObject(
								responseJSON);
						dataList.add(new FacebookMessageListItem(
								facebookObject, FacebookPostAdapter.this));

						// add comments
						if (facebookObject.commentsList != null) {
							for (Comment comment : facebookObject.commentsList) {
								dataList.add(new FacebookCommentListItem(
										comment, FacebookPostAdapter.this));
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		protected void onPostExecute(Void v) {
			itemList.clear();
			itemList.addAll(dataList);
			notifyDataSetChanged();
			isRefreshing = false;

			AndroidUtil.playListAnimation(listView);
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
