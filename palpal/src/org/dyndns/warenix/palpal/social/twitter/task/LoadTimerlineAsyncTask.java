package org.dyndns.warenix.palpal.social.twitter.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dyndns.warenix.palpal.PalPal;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.User;
import android.os.AsyncTask;
import android.util.Log;

/**
 * load timeline and notify listener the progress
 * 
 * @author warenix
 * 
 */
public class LoadTimerlineAsyncTask extends AsyncTask<Void, Void, Void> {

	public static final String MODE_PUBLIC_TIMELINE = "mode_public_timeline";
	public static final String MODE_USER_TIMELINE = "mode_user_timeline";
	public static final String MODE_HOME_TIMELINE = "mode_home_timeline";
	public static final String MODE_MENTIONS_TIMELINE = "mode_mentions_timeline";

	public static final String SHOW_MODE_UPDATE_ONLY = "show_update_only";
	public static final String SHOW_MODE_ALL = "show_all_only";

	Integer count;
	Integer pageNumber;
	Number sinceId;
	String mode;
	String screenName;
	String showMode;

	/**
	 * the result timeline
	 */
	// HashMap<User, List<Status>> timeline;

	LoadTimerlineListener listener;

	/**
	 * the user name list
	 */
	// ArrayList<User> usernameList;

	public LoadTimerlineAsyncTask(String mode, LoadTimerlineListener listener) {
		this.listener = listener;
		this.mode = mode;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void setSinceId(Number sinceId) {
		this.sinceId = sinceId;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public void setShowMode(String showMode, Number sinceId) {
		this.showMode = showMode;
		this.sinceId = sinceId;
	}

	void loadTwitterTimeline() {

		Twitter twitter = PalPal.getTwitterClient();
		if (twitter == null) {
			return;
		}
		twitter.setCount(count);
		twitter.setPageNumber(pageNumber);
		twitter.setSinceId(sinceId);
		Log.v("palpal", String.format("count:%d page:%d since:%d", count,
				pageNumber, sinceId));

		List<Twitter.Status> fetchedTimeline = null;
		if (mode.equals(MODE_HOME_TIMELINE)) {
			fetchedTimeline = twitter.getHomeTimeline();
		} else if (mode.equals(MODE_PUBLIC_TIMELINE)) {
			fetchedTimeline = twitter.getPublicTimeline();
		} else if (mode.equals(MODE_USER_TIMELINE)) {
			fetchedTimeline = twitter.getUserTimeline(screenName);
		} else if (mode.equals(MODE_MENTIONS_TIMELINE)) {
			fetchedTimeline = twitter.getReplies();
		}

		HashMap<User, List<Twitter.Status>> timeline = new HashMap<User, List<Twitter.Status>>();
		ArrayList<User> usernameList = new ArrayList<User>();

		if (fetchedTimeline.size() > 0) {

			// sort distinct parents
			// recent on top
			// for (Status status : fetchedTimeline) {

			Twitter.Status status = null;
			int statusSize = fetchedTimeline.size();
			Log.d("palpal", String.format("fetched %d status", statusSize));

			for (int i = statusSize - 1; i >= 0; --i) {
				status = fetchedTimeline.get(i);
				User createdBy = status.getUser();
				List<Twitter.Status> statusList = timeline.get(createdBy);
				if (statusList == null) {
					statusList = new ArrayList<Twitter.Status>();
					timeline.put(createdBy, statusList);
				} else {
					usernameList.remove(createdBy);
				}
				usernameList.add(createdBy);

				// append
				statusList.add(status);
			}
		}

		if (listener != null) {
			listener.onLoadTimelineCompleted(timeline, usernameList);
		}

	}

	public interface LoadTimerlineListener {

		public void onLoadTimelineCompleted(
				HashMap<User, List<Twitter.Status>> timeline,
				ArrayList<User> usernameList);
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		loadTwitterTimeline();
		return null;
	}

}
