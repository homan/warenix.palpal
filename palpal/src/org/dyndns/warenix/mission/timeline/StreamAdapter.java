package org.dyndns.warenix.mission.timeline;

import java.io.IOException;
import java.io.Serializable;
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
import twitter4j.Status;
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

	private Object lock = new Object();

	class AsyncRefreshTask extends AsyncTask<Void, Void, Void> {

		ArrayList<TimelineMessageListViewItem> dataList = new ArrayList<TimelineMessageListViewItem>();

		@Override
		protected Void doInBackground(Void... params) {
			Runnable facebook = new Runnable() {
				public void run() {
					System.out.println((new Date()).toLocaleString()
							+ " facebook is running");
					getFacebookFeed("me/home", "50");
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
					getTwitterFeed(1, 50);
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

		protected void onPostExecute(Void v) {
			combineListItem(dataList);
		}

		void getFacebookFeed(String graphPath, String pageLimit) {
			Facebook facebook = Memory.getFacebookClient();
			if (facebook != null) {

				try {
					// graphPath = "299272160096520";
					Bundle parameters = new Bundle();
					parameters.putString("limit", pageLimit);
					responseString = facebook.request(graphPath, parameters);

					constructFacebookListItem(responseString, dataList);
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
					statusList = twitter.getHomeTimeline(paging);
					constructTwitterListItem(statusList, dataList);
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

	protected String responseString;
	protected ResponseList<twitter4j.Status> statusList;

	public Serializable getItemList() {
		if (responseString != null && statusList != null) {
			return new Object[] { responseString, statusList };
		}
		return super.getItemList();
	}

	/**
	 * subclass show override this method to recreate list view item from raw
	 * item list
	 * 
	 * @return
	 */
	public void setItemList(Serializable newItemList) {
		Object[] savedItemList = (Object[]) newItemList;

		final String responseString = (String) savedItemList[0];
		final ResponseList<twitter4j.Status> statusList = (ResponseList<Status>) savedItemList[1];

		if (savedItemList[0] instanceof String
				&& savedItemList[1] instanceof ArrayList) {
			new Thread(new Runnable() {
				public void run() {

					ArrayList<TimelineMessageListViewItem> dataList = new ArrayList<TimelineMessageListViewItem>();
					constructFacebookListItem(responseString, dataList);

					constructTwitterListItem(statusList, dataList);

					combineListItem(dataList);

				}
			}).start();
		} else {
			asyncRefresh();
		}
	}

	void constructFacebookListItem(String responseString,
			ArrayList<TimelineMessageListViewItem> dataList) {
		try {
			JSONObject responseJSON = new JSONObject(responseString);
			JSONArray dataJSONArray = responseJSON.getJSONArray("data");
			for (int i = 0; i < dataJSONArray.length(); ++i) {
				FacebookObject facebookObject = new FacebookObject(
						dataJSONArray.getJSONObject(i));
				dataList.add(new FacebookMessageListItem(facebookObject,
						StreamAdapter.this));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	void constructTwitterListItem(ResponseList<twitter4j.Status> statusList,
			ArrayList<TimelineMessageListViewItem> dataList) {
		for (twitter4j.Status status : statusList) {
			dataList.add(new TwitterMessageListItem(status, StreamAdapter.this));
		}
	}

	void combineListItem(final ArrayList<TimelineMessageListViewItem> dataList) {
		listView.post(new Runnable() {
			public void run() {
				itemList.clear();
				Collections.sort(dataList);
				itemList.addAll(dataList);
				notifyDataSetChanged();

				isRefreshing = false;
				AndroidUtil.playListAnimation(listView);
			}
		});

	}
}
