package org.dyndns.warenix.mission.timeline;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;

import org.dyndns.warenix.lab.compat1.util.AndroidUtil;
import org.dyndns.warenix.lab.compat1.util.Memory;
import org.dyndns.warenix.mission.facebook.FacebookMessageListItem;
import org.dyndns.warenix.mission.facebook.FacebookObject;
import org.dyndns.warenix.mission.twitter.TwitterMessageListItem;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.facebook.android.Facebook;

public class StreamAdapter extends ListViewAdapter {

	public static int MESSAGE_TYPE_TWITTER = 1;
	public static int MESSAGE_TYPE_FACEBOOK = 2;

	boolean isRefreshing;

	public StreamAdapter(Context context, ListView listView) {
		super(context, listView);
	}

	public void asyncRefresh() {
		if (!isRefreshing) {
			listView.post(new Runnable() {

				@Override
				public void run() {
					itemList.clear();
					notifyDataSetChanged();
				}

			});
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
			getFacebookFeed("me/home", "50");
			getTwitterFeed(1, 50);
			return null;
		}

		protected void onPostExecute(Void v) {
			itemList.clear();
			Collections.sort(dataList);
			itemList.addAll(dataList);
			notifyDataSetChanged();

			isRefreshing = false;
			
			AndroidUtil.playListAnimation(listView);
		}

		void getFacebookFeed(String graphPath, String pageLimit) {
			Facebook facebook = Memory.getFacebookClient();
			if (facebook != null) {

				try {
					// graphPath = "299272160096520";
					Bundle parameters = new Bundle();
					parameters.putString("limit", pageLimit);
					String responseString = facebook.request(graphPath,
							parameters);

					try {
						JSONObject responseJSON = new JSONObject(responseString);
						JSONArray dataJSONArray = responseJSON
								.getJSONArray("data");
						for (int i = 0; i < dataJSONArray.length(); ++i) {
							FacebookObject facebookObject = new FacebookObject(
									dataJSONArray.getJSONObject(i));
							dataList.add(new FacebookMessageListItem(
									facebookObject, StreamAdapter.this));
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

		}

		void getTwitterFeed(int page, int limit) {
			Twitter twitter = Memory.getTwitterClient();
			if (twitter != null) {
				try {
					Paging paging = new Paging(page, limit);
					ResponseList<twitter4j.Status> statusList = twitter
							.getHomeTimeline(paging);

					for (twitter4j.Status status : statusList) {
						dataList.add(new TwitterMessageListItem(status,
								StreamAdapter.this));
					}
				} catch (TwitterException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		}

	}

	static TimelineMessageListViewItem tempItem;

	public int getItemViewType(int position) {
		tempItem = (TimelineMessageListViewItem) getItem(position);
		if (tempItem.messageType == MESSAGE_TYPE_TWITTER) {
			return 0;
		} else if (tempItem.messageType == MESSAGE_TYPE_FACEBOOK) {
			return 1;
		}
		return 0;
	}

	public int getViewTypeCount() {
		return 2;
	}
}
