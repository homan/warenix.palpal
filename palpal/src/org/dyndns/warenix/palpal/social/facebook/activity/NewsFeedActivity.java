package org.dyndns.warenix.palpal.social.facebook.activity;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dyndns.warenix.gesture.ShakeDetector;
import org.dyndns.warenix.gesture.ShakeDetector.ShakeListener;
import org.dyndns.warenix.gesture.SwipeGestureDetector;
import org.dyndns.warenix.palpal.PalPal;
import org.dyndns.warenix.palpal.PalPalPreference;
import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.palpal.animation.AnimationEffect;
import org.dyndns.warenix.palpal.social.facebook.FacebookException;
import org.dyndns.warenix.palpal.social.facebook.TimelineAdapter;
import org.dyndns.warenix.palpal.social.facebook.task.NotificationTask;
import org.dyndns.warenix.palpal.social.facebook.task.NotificationTask.NotificationTaskListener;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookMaster;
import org.dyndns.warenix.palpal.social.facebook.vo.FacebookPost;
import org.dyndns.warenix.palpal.social.facebook.vo.FacebookPostFactory;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.Notification;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.Profile;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.UserGroupHeader;
import org.dyndns.warenix.util.ToastUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;

import com.facebook.android.Facebook;

public class NewsFeedActivity extends PalPalFacebookActivity implements
		SwipeGestureDetector.TimelineSwipeListener, NotificationTaskListener,
		ShakeListener {

	// constants
	// String CONFIG_LIMIT = "10";
	boolean CONFIG_ENABLE_NOTIFICATION = true;

	// only check one notification to see if there is new one
	String CONFIG_FETCH_NOTIFICATION_LIMIT = "1";

	public static final String BUNDLE_GRAPH_PATH = "graph_path";

	public static final String MODE_SHOW_UPDATE_ONLY = "show_update_only";
	public static final String MODE_SHOW_ALL = "show_all";

	// ui
	ImageView loadingImage;
	ExpandableListView timelineListView;

	// data
	HashMap<String, ArrayList<FacebookPost>> timeline = new HashMap<String, ArrayList<FacebookPost>>();
	ArrayList<String> usernameList = new ArrayList<String>();
	TimelineAdapter timelineAdapter;
	Profile currentUserFeedProfile;

	String graphPath = "me/home";

	String mode;
	/**
	 * since last status creation time
	 */
	String since;

	private FacebookPost firstPost;

	// paging
	ArrayList<String> sinceStack = new ArrayList<String>();
	boolean isLoading;

	ShakeDetector shakeDetector;

	protected void onResume() {
		super.onResume();

		if (shakeDetector == null) {
			shakeDetector = new ShakeDetector(this, this);
		}
		shakeDetector.start();

		Profile authenticatedUserProfile = PalPal.getAuthenticatedUserProfile();
		if (authenticatedUserProfile != null) {
			if (CONFIG_ENABLE_NOTIFICATION) {
				// Log.v("palpal", "start task to get unread notification");
				new NotificationTask(this, this).execute(
						authenticatedUserProfile.id,
						// NotificationTask.SHOW_ALL_NOTIFICATION,
						NotificationTask.SHOW_UNREAD_ONLY_NOTIFICATION,
						CONFIG_FETCH_NOTIFICATION_LIMIT);
			}
		}

	}

	protected void onPause() {
		super.onPause();

		if (shakeDetector != null) {
			shakeDetector.stop();
		}
	}

	@Override
	void onFacebookReady(Facebook facebook,
			HashMap<String, SoftReference<Bitmap>> imagePool) {

		timelineAdapter = new TimelineAdapter(this, usernameList, timeline,
				imagePool);
		timelineListView.setAdapter(timelineAdapter);

		since = getSince();

		// paging
		currentPageNumber = 1;
		sinceStack.add("");

		loadPage(currentPageNumber);
	}

	@Override
	void setupUI() {
		setContentView(R.layout.facebook_timeline);

		loadingImage = (ImageView) findViewById(R.id.loadingImage);

		timelineListView = (ExpandableListView) findViewById(R.id.timeline);

		timelineListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

				FacebookPost feed = getPostOnTimeline(groupPosition,
						childPosition);
				if (feed == null) {
					return false;
				}
				return feed.action(NewsFeedActivity.this, 0);
			}
		});

		SwipeGestureDetector swipeGestureDetector = new SwipeGestureDetector(
				this, timelineListView);

		timelineListView
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
						int type = ExpandableListView
								.getPackedPositionType(info.packedPosition);
						int groupPosition = ExpandableListView
								.getPackedPositionGroup(info.packedPosition);
						int childPosition = ExpandableListView
								.getPackedPositionChild(info.packedPosition);

						Log.d("palpal", String.format(
								"show context menu for group [%d] child[%d]",
								groupPosition, childPosition));

						if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
							menu.setHeaderTitle("Actions");

							// String createdBy = usernameList.get(groupPos);
							// List<FacebookPost> statusList = timeline
							// .get(createdBy);
							// FacebookPost feed = statusList.get(childPos);
							FacebookPost feed = getPostOnTimeline(
									groupPosition, childPosition);

							for (int i = 1; i < feed.actionNameList.size(); ++i) {
								menu.add(0, i, 0, feed.actionNameList.get(i));
							}
						} else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
							menu.setHeaderTitle("Actions");

							// get tapped user
							String createdById = usernameList
									.get(groupPosition);
							List<FacebookPost> statusList = timeline
									.get(createdById);
							FacebookPost feed = statusList.get(0);
							String createdByUserName = feed.createdByName;
							statusList = null;
							feed = null;

							UserGroupHeader user = new UserGroupHeader(
									createdById, createdByUserName, "0");

							for (int i = 1; i < user.actionLinkList.size(); ++i) {
								menu.add(0, i, 0, user.actionNameList.get(i));
							}
						}
					}
				});
	}

	void loadPage(Integer pageNumber) {
		Log.v("palpal", "loading page " + pageNumber);

		clearImagePool();
		usernameList.clear();
		timeline.clear();
		timelineAdapter.notifyDataSetInvalidated();

		try {
			Bundle parameters = new Bundle();
			// limit don't set too high or cause out of memory exception
			String limit = PalPalPreference.loadPreferenceValue(
					this,
					getResources().getString(
							R.string.KEY_FACEBOOK_NUMBER_OF_WALL_POSTS), "10");

			parameters.putString("limit", limit);

			// show profile
			if (extras != null) {
				String bundledGraphPath = extras.getString(BUNDLE_GRAPH_PATH);
				if (bundledGraphPath != null) {
					graphPath = bundledGraphPath;
				}
			}

			parameters
					.putString("until", sinceStack.get(sinceStack.size() - 1));

			if (graphPath.equals("me/home")) {

				if (mode == null) {
					mode = MODE_SHOW_UPDATE_ONLY;
				}
				if (mode.equals(MODE_SHOW_UPDATE_ONLY)) {
					parameters.putString("since", since);
				}
			} else if (mode == null) {
				mode = MODE_SHOW_ALL;
			}

			ToastUtil.showQuickToast(this,
					mode.equals(MODE_SHOW_ALL) ? "showing all posts"
							: "showing updated posts only");

			updateApplicationTitle();

			new FetchPageAsyncTask().execute(graphPath, parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	boolean loadNextPage() {
		if (!hasNextPage || sinceStack.size() == 0 || isLoading) {
			return false;
		}

		currentPageNumber++;
		runOnUiThread(new Runnable() {
			public void run() {
				ToastUtil.showQuickToast(NewsFeedActivity.this,
						"loading next page " + (currentPageNumber));
			}
		});

		loadPage(currentPageNumber);
		return true;
	}

	boolean loadPreviousPage() {
		if (currentPageNumber == null || isLoading) {
			return false;
		}

		if (currentPageNumber == 1) {
			runOnUiThread(new Runnable() {
				public void run() {
					ToastUtil.showQuickToast(NewsFeedActivity.this,
							"no previous page");
				}
			});
			return false;
		}

		currentPageNumber--;
		runOnUiThread(new Runnable() {
			public void run() {
				ToastUtil.showQuickToast(NewsFeedActivity.this,
						"loading previous page " + (currentPageNumber));
			}
		});

		// remove the "since" that loads next page
		sinceStack.remove(sinceStack.size() - 1);
		// remove the "since" that loads current
		sinceStack.remove(sinceStack.size() - 1);
		loadPage(currentPageNumber);
		return true;

	}

	void updateApplicationTitle() {
		String username = graphPath.split("/")[0];
		Profile currentUserProfile;
		try {
			currentUserProfile = FacebookMaster.profile.getProfile(username);
			setTitle(String.format("PalPalFacebook - %s",
					currentUserProfile.name));
			PalPal.setCurrentUserProfile(currentUserProfile);
		} catch (FacebookException e) {
			ToastUtil.showNotification(this, e.type, e.type, e.error, null,
					1000);
		}

	}

	String getSince() {
		return PalPalPreference.loadFacebookSinceTimePreference(this);
	}

	private void parseHome(String responseJSON) {
		try {

			JSONObject json = new JSONObject(responseJSON);
			JSONArray data = json.getJSONArray("data");

			int postCount = data.length();

			for (int i = postCount - 1; i >= 0; --i) {
				JSONObject post = (JSONObject) data.get(i);
				String type = post.getString("type");
				FacebookPost feed = FacebookPostFactory.factory(type, post);

				if (feed != null) {
					String key = feed.createdById;

					ArrayList<FacebookPost> statusList = timeline.get(key);

					if (statusList == null) {
						statusList = new ArrayList<FacebookPost>();
						timeline.put(key, statusList);
					} else {
						// move updated user to the most recent place
						usernameList.remove(key);
					}
					usernameList.add(key);
					statusList.add(feed);

					if (i == (postCount - 1)) {
						firstPost = feed;
						Log.v("palpal",
								String.format("first post %s",
										firstPost.toString()));
					}
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		Log.d("palpal", "parseHome completed");
	}

	public boolean onContextItemSelected(MenuItem menuItem) {
		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) menuItem
				.getMenuInfo();
		int groupPos = 0, childPos = 0;
		int type = ExpandableListView
				.getPackedPositionType(info.packedPosition);

		groupPos = ExpandableListView
				.getPackedPositionGroup(info.packedPosition);
		childPos = ExpandableListView
				.getPackedPositionChild(info.packedPosition);

		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
			String createdBy = usernameList.get(groupPos);
			List<FacebookPost> statusList = timeline.get(createdBy);
			FacebookPost feed = statusList.get(childPos);

			if (feed.action(this, menuItem.getItemId())) {
				return true;
			}
			return super.onContextItemSelected(menuItem);
		} else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
			// get tapped user
			String createdById = usernameList.get(groupPos);
			List<FacebookPost> statusList = timeline.get(createdById);
			FacebookPost feed = statusList.get(0);
			String createdByUserName = feed.createdByName;

			UserGroupHeader user = new UserGroupHeader(createdById,
					createdByUserName, String.format("(%s)", statusList.size()));
			statusList = null;
			feed = null;

			return user.action(this, menuItem.getItemId());

		}

		return false;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.newsfeed_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent intent;
		Bundle extras;

		switch (item.getItemId()) {
		case R.id.menu_next_page:
			loadNextPage();
			break;
		case R.id.menu_previous_page:
			loadPreviousPage();
			break;
		case R.id.menu_share_link:
			intent = new Intent(this, ShareActivity.class);
			extras = new Bundle();
			extras.putString(ShareActivity.BUNDLE_MODE,
					ShareActivity.MODE_SHARE_LINK);
			extras.putString(ShareActivity.BUNDLE_PROFILE_ID,
					graphPath.split("/")[0]);
			intent.putExtras(extras);
			startActivity(intent);
			return true;
		case R.id.menu_share_status:
			intent = new Intent(this, ShareActivity.class);
			extras = new Bundle();
			extras.putString(ShareActivity.BUNDLE_MODE,
					ShareActivity.MODE_SHARE_STATUS);
			extras.putString(ShareActivity.BUNDLE_PROFILE_ID,
					graphPath.split("/")[0]);
			intent.putExtras(extras);
			startActivity(intent);
			return true;
		case R.id.menu_share_photo:
			intent = new Intent(this, ShareActivity.class);
			extras = new Bundle();
			extras.putString(ShareActivity.BUNDLE_MODE,
					ShareActivity.MODE_SHARE_PHOTO);
			extras.putString(ShareActivity.BUNDLE_PROFILE_ID,
					graphPath.split("/")[0]);
			intent.putExtras(extras);
			startActivity(intent);
			return true;
		case R.id.menu_trigger_mode:
			if (mode != null) {
				if (mode.equals(MODE_SHOW_ALL)) {
					mode = MODE_SHOW_UPDATE_ONLY;
					since = getSince();
				} else {
					mode = MODE_SHOW_ALL;
					sinceStack.clear();
					sinceStack.add("");
				}

				clearImagePool();
				usernameList.clear();
				timeline.clear();
				timelineAdapter.notifyDataSetInvalidated();

				// load from the frist page
				currentPageNumber = 1;
				loadPage(currentPageNumber);
				return true;
			}
		}

		Log.i("warenix", "" + item.getItemId());
		return super.onOptionsItemSelected(item);
	}

	void fetchPage(String graphPath, Bundle parameters) {
		Log.v("palal", "fetch page start");

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AnimationEffect.playFetchPageAnimation(NewsFeedActivity.this,
						loadingImage, true);
				isLoading = true;
				usernameList.clear();
				timeline.clear();
			}
		});

		String responseJSON;
		try {
			responseJSON = facebook.request(graphPath, parameters);
			parseHome(responseJSON);
			Log.d("palpal", "finished parseHome in fetchPage()");

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	FacebookPost getPostOnTimeline(int groupPosition, int childPosition) {
		String createdBy = usernameList.get(groupPosition);
		List<FacebookPost> statusList = timeline.get(createdBy);
		FacebookPost feed = statusList.get(childPosition);
		return feed;
	}

	/* Async Task */
	class FetchPageAsyncTask extends AsyncTask<Object, Void, Void> {

		@Override
		protected Void doInBackground(Object... params) {
			if (params.length < 2) {
				return null;
			}

			String graphPath = (String) params[0];
			Bundle parameters = (Bundle) params[1];

			fetchPage(graphPath, parameters);
			Log.d("palpal", "asynctask after fetched");

			return null;
		}

		protected void onPostExecute(Void v) {
			Log.d("palpal", String.format("fetched %d username in this page",
					usernameList.size()));
			if (usernameList.size() > 0) {
				hasNextPage = true;

				if (firstPost != null) {
					sinceStack.add(firstPost.createdTime);
					Log.v("palpal", String.format("sinceStack add %s",
							firstPost.createdTime));
				}

				for (int i = 0; i < usernameList.size(); ++i) {
					timelineListView.collapseGroup(i);
				}
				timelineListView.setSelectedGroup(0);
				Log.d("palpal", "end notifyDataSetChanged");
				timelineAdapter.notifyDataSetChanged();

				if (graphPath.equals("me/home")) {
					if (currentPageNumber == 1) {
						// show last feed
						Log.d("palpal", String.format("mark last read %s",
								getLastPost().toString()));
						PalPalPreference.writeFacebookSinceTimePreference(
								NewsFeedActivity.this,
								getLastPost().createdTime);
					}
				}

			} else {
				hasNextPage = false;
				sinceStack.add("");

				ToastUtil.showQuickToast(NewsFeedActivity.this, "no more post");
			}

			Log.v("palal", "fetch page end");
			AnimationEffect.playFetchPageAnimation(NewsFeedActivity.this,
					loadingImage, false);
			isLoading = false;
		}
	}

	FacebookPost getFirstPost() {
		String createdBy = usernameList.get(0);
		List<FacebookPost> statusList = timeline.get(createdBy);
		FacebookPost feed = statusList.get(statusList.size() - 1);
		return feed;
	}

	FacebookPost getLastPost() {
		String createdBy = usernameList.get(usernameList.size() - 1);
		List<FacebookPost> statusList = timeline.get(createdBy);
		FacebookPost feed = statusList.get(statusList.size() - 1);
		return feed;
	}

	@Override
	public void onLeftSwipe() {
		loadNextPage();
	}

	@Override
	public void onRightSwipe() {
		loadPreviousPage();
	}

	@Override
	public void onNotificationLoaded(Notification notification) {
		String message = notification.getTitle();
		String title = notification.getTitle();
		String text = notification.getBody();
		String notificationId = notification.getNotificationId();

		Context context = getApplicationContext();
		Intent notificationIntent = new Intent(context,
				NotificationActivity.class);

		ToastUtil.showNotification(context, message, title, text,
				notificationIntent, Integer.parseInt(notificationId));
	}

	@Override
	public void onLoadNotificationCompleted() {

	}

	@Override
	public void onShake() {
		Log.v("palpal", String.format(
				"NewsFeedActivity onShake(), collpase %d groups",
				usernameList.size()));
		for (int i = 0; i < usernameList.size(); ++i) {
			timelineListView.collapseGroup(i);
		}

	}
}
