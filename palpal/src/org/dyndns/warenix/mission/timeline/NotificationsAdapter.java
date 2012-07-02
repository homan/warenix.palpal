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
 * Load Facebook notifications and Twitter mentions.
 * 
 * @author warenix
 * 
 */
public class NotificationsAdapter extends TimelineAsyncAdapter {

	private static final String TAG = "NotificationsAdapter";

	String facebookNotificationResponseString;
	ResponseList<twitter4j.Status> statusList;

	public NotificationsAdapter(final Context context, ListView listView) {
		super(context, listView);

		Runnable facebook = new Runnable() {
			public void run() {
				WLog.i(TAG, (new Date()).toLocaleString()
						+ " facebook is running");
				if (FacebookMaster.restoreFacebook(context)) {
					facebookNotificationResponseString = getFacebookNotifications(
							"20", true);
					constructFacebookListItem(
							facebookNotificationResponseString, dataList);
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
					statusList = getTwitterMentions(1, 20);
					constructTwitterListItem(statusList, dataList);
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

	String getFacebookNotifications(String pageLimit, boolean includeRead) {
		String graphPath = "me/notifications";
		Facebook facebook = Memory.getFacebookClient();
		if (facebook != null) {
			try {
				Bundle parameters = new Bundle();
				parameters.putString("include_read", includeRead ? "1" : "0");
				parameters.putString("limit", pageLimit);
				String responseString = facebook.request(graphPath, parameters);
				return responseString;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	ResponseList<twitter4j.Status> getTwitterMentions(int pageNo, int pageLimit) {
		Twitter twitter = Memory.getTwitterClient();
		if (twitter != null) {
			try {
				Paging paging = new Paging(pageNo, pageLimit);
				ResponseList<twitter4j.Status> statusList = twitter
						.getMentions(paging);
				return statusList;
			} catch (TwitterException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}

	void constructTwitterListItem(ResponseList<twitter4j.Status> statusList,
			ArrayList<TimelineMessageListViewItem> dataList) {
		if (statusList != null) {
			for (twitter4j.Status status : statusList) {
				if (mRefreshState == RefreshState.DONE) {
					WLog.d(TAG,
							String.format("twitter sync stop due to timeout."));
					break;
				}
				dataList.add(new TwitterMessageListItem(status,
						NotificationsAdapter.this));
			}
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
					if (mRefreshState == RefreshState.DONE) {
						WLog.d(TAG,
								String.format(
										"facebook sync stop due to timeout. Ignore %d messages of total %d",
										dataJSONArray.length() - i,
										dataJSONArray.length()));
						break;
					}
					dataList.add(new FacebookMessageListItem(facebookObject,
							NotificationsAdapter.this));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private static TimelineMessageListViewItem sTempItem;

	public int getItemViewType(int position) {
		sTempItem = (TimelineMessageListViewItem) getItem(position);
		if (sTempItem.messageType == StreamAdapter.MESSAGE_TYPE_TWITTER) {
			return 0;
		} else if (sTempItem.messageType == StreamAdapter.MESSAGE_TYPE_FACEBOOK) {
			return 1;
		}
		return 0;
	}

	public int getViewTypeCount() {
		return 2;
	}

	public Serializable getItemList() {
		return new Object[] { facebookNotificationResponseString, statusList };
	}

	public void setItemList(Serializable newItemList) {
		if (newItemList != null) {
			Object[] obj = (Object[]) newItemList;
			facebookNotificationResponseString = (String) obj[0];
			statusList = (ResponseList<Status>) obj[1];

			dataList.clear();
			constructFacebookListItem(facebookNotificationResponseString,
					dataList);
			constructTwitterListItem(statusList, dataList);
			this.onPostExecut(null);
		}
	}

}
