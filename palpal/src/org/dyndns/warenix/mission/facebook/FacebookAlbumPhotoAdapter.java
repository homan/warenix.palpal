package org.dyndns.warenix.mission.facebook;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;

import org.dyndns.warenix.lab.compat1.util.Memory;
import org.dyndns.warenix.mission.timeline.TimelineAsyncAdapter;
import org.dyndns.warenix.mission.timeline.TimelineMessageListViewItem;
import org.dyndns.warenix.util.WLog;
import org.json.JSONArray;
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
public class FacebookAlbumPhotoAdapter extends TimelineAsyncAdapter {
	private static final String TAG = "FacebookAlbumPhotoAdapter";

	public static final String EXTRA_GRAPH_ID = "graph_id";
	public static final String EXTRA_PAGE_NO = "page_no";
	public static final String EXTRA_PAGE_COUNT = "page_count";

	String mGraphId;
	int mPageNo; // 0-based
	int mPageCount;

	private String responseString;

	public static Bundle getExtra(String graphId, int pageNo, int pageCount) {
		Bundle intent = new Bundle();
		intent.putString(EXTRA_GRAPH_ID, graphId);
		intent.putInt(EXTRA_PAGE_NO, pageNo);
		intent.putInt(EXTRA_PAGE_COUNT, pageCount);
		return intent;
	}

	//
	// public FacebookAlbumPhotoAdapter(Context context, ListView listView,
	// String graphId, int pageNo, int pageCount) {
	// super(context, listView);
	// mGraphId = graphId;
	// mPageNo = pageNo > 0 ? pageNo : 1;
	// mPageCount = pageCount > 0 ? pageCount : 10;
	//
	// Runnable facebook = new Runnable() {
	// public void run() {
	// WLog.i(TAG, (new Date()).toLocaleString()
	// + " facebook is running");
	// getFacebookFeed(String.format("%s/photos/", mGraphId), ""
	// + mPageCount, "" + (mPageNo * mPageCount - 1));
	// notifyRunnableDone();
	// }
	// };
	//
	// addRunnable(facebook);
	// }

	public FacebookAlbumPhotoAdapter(Context context, ListView listView,
			Bundle extras) {
		super(context, listView);
		mGraphId = extras.getString(EXTRA_GRAPH_ID);
		mPageNo = extras.getInt(EXTRA_PAGE_NO, 1);
		mPageCount = extras.getInt(EXTRA_PAGE_COUNT, 10);

		Runnable facebook = new Runnable() {
			public void run() {
				WLog.i(TAG, (new Date()).toLocaleString()
						+ " facebook is running");
				int offset = mPageNo * mPageCount;
				getFacebookFeed(String.format("%s/photos/", mGraphId), ""
						+ mPageCount, "" + offset);
				notifyRunnableDone();
			}
		};

		addRunnable(facebook);
	}

	void getFacebookFeed(String graphPath, String pageLimit, String offset) {
		WLog.d(TAG, String.format("pageNo[%s] offset[%s]", pageLimit, offset));
		Facebook facebook = Memory.getFacebookClient();
		if (facebook != null) {

			try {
				Bundle parameters = new Bundle();
				parameters.putString("limit", pageLimit);
				parameters.putString("offset", offset);
				responseString = facebook.request(graphPath, parameters);

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
			JSONArray dataArray = responseJSON.getJSONArray("data");
			if (dataArray != null) {
				for (int i = 0; i < dataArray.length(); ++i) {
					FacebookObject facebookObject = new FacebookObject(
							dataArray.getJSONObject(i));
					dataList.add(new FacebookMessageListItem(facebookObject,
							FacebookAlbumPhotoAdapter.this));

					// // add comments
					// if (facebookObject.commentsList != null) {
					// for (Comment comment : facebookObject.commentsList) {
					// dataList.add(new FacebookCommentListItem(comment,
					// FacebookAlbumPhotoAdapter.this));
					// }
					// }
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
