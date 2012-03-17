package org.dyndns.warenix.mission.timeline;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;

import org.dyndns.warenix.lab.compat1.util.Memory;
import org.dyndns.warenix.mission.facebook.FacebookMessageListItem;
import org.dyndns.warenix.mission.facebook.FacebookObject;
import org.dyndns.warenix.mission.twitter.TwitterMessageListItem;
import org.dyndns.warenix.util.WLog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.Paging;
import twitter4j.ResponseList;
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

	public StreamAdapter(Context context, ListView listView) {
		super(context, listView);

		Runnable facebook = new Runnable() {
			public void run() {
				WLog.i(TAG, (new Date()).toLocaleString()
						+ " facebook is running");
				getFacebookFeed("me/home", "50");
				WLog.i(TAG, (new Date()).toLocaleString() + " facebook is done");

				notifyRunnableDone();

			}
		};

		Runnable twitter = new Runnable() {
			public void run() {
				WLog.i(TAG, (new Date()).toLocaleString()
						+ " twitter is running");
				getTwitterFeed(1, 50);
				WLog.i(TAG, (new Date()).toLocaleString() + " twitter is done");

				notifyRunnableDone();
			}
		};

		addRunnable(facebook);
		addRunnable(twitter);
	}

	void getFacebookFeed(String graphPath, String pageLimit) {
		Facebook facebook = Memory.getFacebookClient();
		if (facebook != null) {

			try {
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

	protected String responseString;
	protected ResponseList<twitter4j.Status> statusList;

	public Serializable getItemList() {
		if (responseString != null && statusList != null) {
			return new Object[] { responseString, statusList };
		}
		return super.getItemList();
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

}
