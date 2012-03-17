package org.dyndns.warenix.mission.facebook;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.dyndns.warenix.lab.compat1.util.Memory;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.facebook.android.Facebook;

public class FacebookHomeAdapter extends ListViewAdapter {

	boolean isRefreshing;

	public FacebookHomeAdapter(Context context, ListView listView) {
		super(context, listView);
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

		ArrayList<FacebookMessageListItem> dataList = new ArrayList<FacebookMessageListItem>();

		@Override
		protected Void doInBackground(Void... params) {

			Facebook facebook = Memory.getFacebookClient();
			if (facebook != null) {

				try {
					String graphPath = "me/home";
					// graphPath = "299272160096520";
					Bundle parameters = new Bundle();
					parameters.putString("limit", "50");
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
									facebookObject, FacebookHomeAdapter.this));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

					// get individual facebook post
//					try {
//						JSONObject responseJSON = new JSONObject(responseString);
//						FacebookObject facebookObject = new FacebookObject(
//								responseJSON);
//						dataList.add(new FacebookMessageListItem(
//								facebookObject, FacebookHomeAdapter.this));
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
					// WLog.d("warenix", responseString);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				// dataList.add(new FacebookMessageListItem(status,
				// FacebookHomeAdapter.this));
			}

			// for (int i = 0; i < 100; ++i) {
			// try {
			// Thread.sleep(10);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			// dataList.add(new TwitterMessageListItem("#" + i));
			// }
			return null;
		}

		protected void onPostExecute(Void v) {
			itemList.clear();
			itemList.addAll(dataList);
			notifyDataSetChanged();
			isRefreshing = false;
		}

	}

	public int getItemViewType(int position) {
		// ListViewItem item = itemList.get(position);
		return 0;
	}

	public int getViewTypeCount() {
		return 1;
	}
}
