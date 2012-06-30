package org.dyndns.warenix.mission.timeline;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;

import org.dyndns.warenix.lab.compat1.util.Memory;
import org.dyndns.warenix.mission.facebook.FacebookMessageListItem;
import org.dyndns.warenix.mission.facebook.FacebookObject;
import org.dyndns.warenix.mission.facebook.util.FacebookMaster;
import org.dyndns.warenix.mission.twitter.TwitterMessageListItem;
import org.dyndns.warenix.mission.twitter.util.TwitterMaster;
import org.dyndns.warenix.util.WLog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;

import com.facebook.android.Facebook;

/**
 * Load Facebook home feed and Twitter home timeline.
 * 
 * @author warenix
 * 
 */
public class StreamAdapter extends TimelineAsyncAdapter {

	private static final String TAG = "StreamAdapter";

	public static int MESSAGE_TYPE_TWITTER = 1;
	public static int MESSAGE_TYPE_FACEBOOK = 2;

	public StreamAdapter(final Context context, ListView listView) {
		super(context, listView);

		Runnable facebook = new Runnable() {
			public void run() {
				WLog.i(TAG, (new Date()).toLocaleString()
						+ " facebook is running");
				if (FacebookMaster.restoreFacebook(context)) {
					homeResponseString = getFacebookFeed("me/home", "50");
					feedResponseString = getFacebookFeed("me/feed", "10");
				} else {
					WLog.i(TAG, (new Date()).toLocaleString()
							+ " facebook is not linked");
				}
				WLog.i(TAG, (new Date()).toLocaleString() + " facebook is done");

				notifyRunnableDone();

			}
		};

		Runnable twitter = new Runnable() {
			public void run() {
				WLog.i(TAG, (new Date()).toLocaleString()
						+ " twitter is running");
				if (TwitterMaster.restoreTwitterClient(context)) {
					getTwitterFeed(1, 50);
				} else {
					WLog.i(TAG, (new Date()).toLocaleString()
							+ " twitter is not linked");
				}
				WLog.i(TAG, (new Date()).toLocaleString() + " twitter is done");

				notifyRunnableDone();
			}
		};

		clearRunnables();
		addRunnable(facebook);
		addRunnable(twitter);
	}

	String getFacebookFeed(String graphPath, String pageLimit) {
		Facebook facebook = Memory.getFacebookClient();
		if (facebook != null) {
			try {
				Bundle parameters = new Bundle();
				parameters.putString("limit", pageLimit);
				String responseString = facebook.request(graphPath, parameters);

				constructFacebookListItem(responseString, dataList);
				return responseString;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;

	}

	void getTwitterFeed(int page, int limit) {
		Twitter twitter = Memory.getTwitterClient();
		if (twitter != null) {
			try {
				Paging paging = new Paging(page, limit);
				statusList = twitter.getHomeTimeline(paging);
				constructTwitterListItem(statusList, dataList);
			} catch (TwitterException e1) {
				e1.printStackTrace();
			}
		}

	}

	private static TimelineMessageListViewItem sTempItem;

	public int getItemViewType(int position) {
		sTempItem = (TimelineMessageListViewItem) getItem(position);
		if (sTempItem.messageType == MESSAGE_TYPE_TWITTER) {
			return 0;
		} else if (sTempItem.messageType == MESSAGE_TYPE_FACEBOOK) {
			return 1;
		}
		return 0;
	}

	public int getViewTypeCount() {
		return 2;
	}

	protected String homeResponseString;
	protected String feedResponseString;
	protected ResponseList<twitter4j.Status> statusList;

	public Serializable getItemList() {
		return new Object[] { feedResponseString, homeResponseString,
				statusList };
	}

	public void setItemList(Serializable newItemList) {
		if (newItemList != null) {
			Object[] obj = (Object[]) newItemList;
			feedResponseString = (String) obj[0];
			homeResponseString = (String) obj[1];
			statusList = (ResponseList<Status>) obj[2];

			dataList.clear();
			constructFacebookListItem(feedResponseString, dataList);
			constructFacebookListItem(homeResponseString, dataList);
			constructTwitterListItem(statusList, dataList);
			this.onPostExecut(null);
		}
	}

	void constructFacebookListItem(String responseString,
			ArrayList<TimelineMessageListViewItem> dataList) {
		if (responseString != null) {
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
	}

	void constructTwitterListItem(ResponseList<twitter4j.Status> statusList,
			ArrayList<TimelineMessageListViewItem> dataList) {
		for (twitter4j.Status status : statusList) {
			dataList.add(new TwitterMessageListItem(status, StreamAdapter.this));
		}
	}

}
