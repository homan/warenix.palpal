package org.dyndns.warenix.mission.timeline;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

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

public class NotificationsAdapter extends ListViewAdapter {

	boolean isRefreshing;

	public NotificationsAdapter(Context context, ListView listView) {
		super(context, listView);
	}

	public void asyncRefresh() {
		if (!isRefreshing) {
			isRefreshing = true;

			listView.post(new Runnable() {

				@Override
				public void run() {
					itemList.clear();
					notifyDataSetChanged();
				}

			});
			new AsyncRefreshTask().execute();
		}

	}

	// public void refresh() {
	// if (!isRefreshing) {
	// isRefreshing = true;
	// new AsyncRefreshTask().execute();
	// }
	// }

	private Object lock = new Object();

	class AsyncRefreshTask extends AsyncTask<Void, Void, Void> {

		ArrayList<TimelineMessageListViewItem> dataList = new ArrayList<TimelineMessageListViewItem>();

		@Override
		protected Void doInBackground(Void... params) {

			Runnable facebook = new Runnable() {
				public void run() {
					System.out.println((new Date()).toLocaleString()
							+ " facebook is running");
					getFacebookNotifications("10", true);
					System.out.println((new Date()).toLocaleString()
							+ " facebook is done");
					synchronized (lock) {
						lock.notify();
					}

				}
			};

			Runnable twitter = new Runnable() {
				public void run() {
					System.out.println((new Date()).toLocaleString()
							+ " twitter is running");
					getTwitterMentions(1, 10);
					System.out.println((new Date()).toLocaleString()
							+ " twitter is done");
					synchronized (lock) {
						lock.notify();
					}
				}
			};

			new Thread(facebook).start();
			new Thread(twitter).start();

			int count = 2;
			while (count > 0) {
				System.out.println((new Date()).toLocaleString()
						+ " refreshing " + count);
				try {
					synchronized (lock) {
						lock.wait();
						count--;
					}

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}

			}

			System.out.println((new Date()).toLocaleString()
					+ " refreshing done");

			return null;
		}

		void getFacebookNotifications(String pageLimit, boolean includeRead) {
			String graphPath = "me/notifications";
			Facebook facebook = Memory.getFacebookClient();
			if (facebook != null) {
				try {
					// graphPath = "299272160096520";
					Bundle parameters = new Bundle();
					parameters.putString("include_read", includeRead ? "1"
							: "0");
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
									facebookObject, NotificationsAdapter.this));
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

		void getTwitterMentions(int pageNo, int pageLimit) {
			Twitter twitter = Memory.getTwitterClient();
			if (twitter != null) {
				try {
					Paging paging = new Paging(pageNo, pageLimit);
					ResponseList<twitter4j.Status> statusList = twitter
							.getMentions(paging);

					for (twitter4j.Status status : statusList) {
						dataList.add(new TwitterMessageListItem(status,
								NotificationsAdapter.this));
					}
				} catch (TwitterException e1) {
					e1.printStackTrace();
				}
			}
		}

		protected void onPostExecute(Void v) {
			itemList.clear();
			Collections.sort(dataList);
			itemList.addAll(dataList);
			notifyDataSetChanged();
			isRefreshing = false;

			AndroidUtil.playListAnimation(listView);
		}

	}

	static TimelineMessageListViewItem tempItem;

	public int getItemViewType(int position) {
		tempItem = (TimelineMessageListViewItem) getItem(position);
		if (tempItem.messageType == StreamAdapter.MESSAGE_TYPE_TWITTER) {
			return 0;
		} else if (tempItem.messageType == StreamAdapter.MESSAGE_TYPE_FACEBOOK) {
			return 1;
		}
		return 0;
	}

	public int getViewTypeCount() {
		return 2;
	}

}
