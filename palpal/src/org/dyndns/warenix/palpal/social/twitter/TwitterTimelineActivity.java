package org.dyndns.warenix.palpal.social.twitter;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.dyndns.warenix.gesture.ShakeDetector;
import org.dyndns.warenix.gesture.ShakeDetector.ShakeListener;
import org.dyndns.warenix.gesture.SwipeGestureDetector;
import org.dyndns.warenix.gesture.SwipeGestureDetector.TimelineSwipeListener;
import org.dyndns.warenix.palpal.PalPal;
import org.dyndns.warenix.palpal.PalPalPreference;
import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.palpal.animation.AnimationEffect;
import org.dyndns.warenix.palpal.social.twitter.task.FetchImageTask;
import org.dyndns.warenix.palpal.social.twitter.task.LoadTimerlineAsyncTask;
import org.dyndns.warenix.palpal.social.twitter.task.LoadTimerlineAsyncTask.LoadTimerlineListener;
import org.dyndns.warenix.util.ToastUtil;
import org.dyndns.warenix.widget.WebImage;

import winterwell.jtwitter.OAuthSignpostClient;
import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.Status;
import winterwell.jtwitter.Twitter.User;
import winterwell.jtwitter.TwitterException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Pal are sorted by update time and all unread updates are grouped
 * 
 * @author warenix
 * 
 */
public class TwitterTimelineActivity extends Activity implements
		LoadTimerlineListener, TimelineSwipeListener, ShakeListener {

	public static final String BUNDLE_MODE = "mode";
	public static final String BUNDLE_SCREEN_NAME = "screen_name";

	String username;
	List<Status> fetchedTimeline;
	Number sinceId;

	/**
	 * which user profile to view
	 */
	String screenName;

	// Integer CONFIG_COUNT = 50;
	/**
	 * current page number, one-based
	 */
	Integer currentPageNumber;

	String showMode;

	HashMap<String, SoftReference<Bitmap>> imagePool;

	HashMap<User, List<Status>> timeline = new HashMap<User, List<Status>>();
	ArrayList<User> usernameList = new ArrayList<User>();

	TimelineAdapter adapter;

	ImageView loadingImage;
	ExpandableListView timelineListView;

	static private LayoutInflater mChildInflater;
	static private LayoutInflater mGroupInflater;

	final int MENU_REPLY_ALL = 1;
	final int MENU_RETWEET_RT = 2;
	final int MENU_CONVERSATION = 3;
	final int MENU_VIEW_USER = 11;
	final int MENU_DIRECT_MESSAGE = 21;
	final int MENU_VIEW_USER_PROFILE = 31;

	SwipeGestureDetector swipeGestureDetector;
	ShakeDetector shakeDetector;

	OAuthSignpostClient oauthClient;

	String mode;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setupUI();

		mGroupInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mChildInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		checkTwitterSession();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == 1) {

			String[] accessToken = PalPalPreference
					.loadAccessTokenPreference(this);
			oauthClient = new OAuthSignpostClient(PalPal.JTWITTER_OAUTH_KEY,
					PalPal.JTWITTER_OAUTH_SECRET, accessToken[0],
					accessToken[1]);

			Twitter twitter = new Twitter("", oauthClient);
			PalPal.setTwitter(twitter);
			onTwitterClientReady();
		}
	}

	protected void onResume() {
		super.onResume();
		if (shakeDetector == null) {
			shakeDetector = new ShakeDetector(this, this);
		}
		shakeDetector.start();
	}

	protected void onPause() {
		super.onPause();

		if (shakeDetector != null) {
			shakeDetector.stop();
		}
	}

	protected void onDestroy() {
		clearImagePool();
		super.onDestroy();

	}

	void clearImagePool() {
		if (imagePool != null) {
			Set<String> keySet = imagePool.keySet();
			for (String key : keySet) {
				SoftReference<Bitmap> ref = imagePool.get(key);
				if (ref != null) {
					Bitmap bm = ref.get();
					if (bm != null) {
						Log.d("warenix", "recycle bm " + key);
						bm.recycle();
					}
				}
			}
			imagePool.clear();
		}
	}

	void setupUI() {
		setContentView(R.layout.grouped_pal_timeline);

		loadingImage = (ImageView) findViewById(R.id.loadingImage);
		timelineListView = (ExpandableListView) findViewById(R.id.timeline);
		timelineListView.setDividerHeight(0);

		timelineListView
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
						int type = ExpandableListView
								.getPackedPositionType(info.packedPosition);
						int groupPos = ExpandableListView
								.getPackedPositionGroup(info.packedPosition);
						int childPos = ExpandableListView
								.getPackedPositionChild(info.packedPosition);

						Log.d("palpal", String.format(
								"show context menu for group [%d] child[%d]",
								groupPos, childPos));

						// Only create a context menu for child items
						if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {

							User createdBy = usernameList.get(groupPos);
							List<Status> statusList = timeline.get(createdBy);
							Status status = statusList.get(childPos);

							// Array created earlier when we built the
							// expandable list
							menu.setHeaderTitle("Actions");
							menu.add(0, MENU_REPLY_ALL, 0, "Reply All");
							menu.add(0, MENU_RETWEET_RT, 0, "Retweet (RT)");
							if (status.inReplyToStatusId != null) {
								menu.add(0, MENU_CONVERSATION, 0,
										"Conversation");
							}
						} else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
							User createdBy = usernameList.get(groupPos);
							screenName = createdBy.screenName;

							menu.setHeaderTitle("Actions");
							menu.add(0, MENU_VIEW_USER, 0,
									String.format("@%s", screenName));

							menu.add(0, MENU_DIRECT_MESSAGE, 0,
									String.format("DM %s", screenName));

							menu.add(0, MENU_VIEW_USER_PROFILE, 0, "Profile");
						}
					}

				});
	}

	void checkTwitterSession() {
		Log.v("palpal", "checkTwitterSession");

		Twitter twitter = PalPal.getTwitterClient();
		if (twitter != null) {
			try {
				twitter.isValidLogin();
				Log.v("palpal",
						"checkTwitterSession: found stored valid twitter client");

				Log.v("palpal", "checkTwitterSession: twitter session is valid");
				PalPal.setTwitter(twitter);
				onTwitterClientReady();
			} catch (TwitterException e) {
				e.printStackTrace();

				ToastUtil.showNotification(this,
						"fail to validate twitter session",
						"fail to validate twitter session", e.getMessage(),
						null, 1000);
			}

		} else {
			String[] accessToken = PalPalPreference
					.loadAccessTokenPreference(this);
			if (accessToken[0] == null || accessToken[1] == null) {
				Log.v("palpal",
						"checkTwitterSession: no access token stored, going to start login dialog");
				startActivityForResult(new Intent(this,
						OAuthTwitterActivity.class), 1);
			} else {
				Log.v("palpal",
						String.format(
								"checkTwitterSession: found access token [%s] [%s], try oauth",
								accessToken[0], accessToken[1]));
				oauthClient = new OAuthSignpostClient(
						PalPal.JTWITTER_OAUTH_KEY,
						PalPal.JTWITTER_OAUTH_SECRET, accessToken[0],
						accessToken[1]);

				Twitter twitter1 = new Twitter("", oauthClient);
				try {
					if (twitter1.isValidLogin()) {
						Log.v("palpal",
								"checkTwitterSession: twitter session is valid");
						PalPal.setTwitter(twitter1);
						onTwitterClientReady();
					} else {
						Log.v("palpal",
								"checkTwitterSession: twitter session is invalid, going to start login dialog");
						startActivityForResult(new Intent(this,
								OAuthTwitterActivity.class), 1);
					}
				} catch (TwitterException e) {
					e.printStackTrace();

					ToastUtil.showNotification(this,
							"fail to validate twitter session",
							"fail to validate twitter session", e.getMessage(),
							null, 1000);
				} catch (Exception e) {
					e.printStackTrace();

					startActivityForResult(new Intent(this,
							OAuthTwitterActivity.class), 1);
				}
			}
		}

	}

	void onTwitterClientReady() {
		Log.v("palpal", "onTwitterClientReady");

		imagePool = new HashMap<String, SoftReference<Bitmap>>();

		swipeGestureDetector = new SwipeGestureDetector(this, timelineListView);

		timeline = new HashMap<Twitter.User, List<Status>>();
		usernameList = new ArrayList<Twitter.User>();

		adapter = new TimelineAdapter(timeline, usernameList);
		timelineListView.setAdapter(adapter);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mode = extras.getString(BUNDLE_MODE);
			screenName = extras.getString(BUNDLE_SCREEN_NAME);
		}

		// default
		showMode = LoadTimerlineAsyncTask.SHOW_MODE_UPDATE_ONLY;
		currentPageNumber = 1;
		getLastStatusId();

		executeModeAction(mode);
	}

	void executeModeAction(String mode) {
		if (mode == null
				|| mode.equals(LoadTimerlineAsyncTask.MODE_HOME_TIMELINE)) {
			loadHomeTimeline(currentPageNumber);
		} else if (mode.equals(LoadTimerlineAsyncTask.MODE_USER_TIMELINE)) {
			loadUserTimeline(screenName, currentPageNumber);
		} else if (mode.equals(LoadTimerlineAsyncTask.MODE_MENTIONS_TIMELINE)) {
			loadMentionsTimeline(currentPageNumber);
		}
	}

	void loadHomeTimeline(int pageNumber) {
		ToastUtil.showQuickToast(this,
				String.format("loading page %d", pageNumber));

		AnimationEffect.playFetchPageAnimation(TwitterTimelineActivity.this,
				loadingImage, true);

		// clear list
		timeline.clear();
		usernameList.clear();
		adapter.notifyDataSetInvalidated();
		clearImagePool();

		LoadTimerlineAsyncTask task = new LoadTimerlineAsyncTask(
				LoadTimerlineAsyncTask.MODE_HOME_TIMELINE, this);
		String limit = PalPalPreference
				.loadPreferenceValue(
						this,
						getResources().getString(
								R.string.KEY_TWITTER_NUMBER_OF_TWEETS), "10");
		task.setCount(Integer.parseInt(limit));
		task.setPageNumber(pageNumber);
		task.setShowMode(showMode, null);
		if (showMode.equals(LoadTimerlineAsyncTask.SHOW_MODE_UPDATE_ONLY)) {

			task.setShowMode(showMode, sinceId);
		}

		if (showMode.equals(LoadTimerlineAsyncTask.SHOW_MODE_ALL)) {
			ToastUtil.showQuickToast(this, "show all");
		} else {
			ToastUtil.showQuickToast(this, "show updated only");
		}
		task.execute();
	}

	void loadUserTimeline(String screenName, Integer pageNumber) {
		AnimationEffect.playFetchPageAnimation(TwitterTimelineActivity.this,
				loadingImage, true);

		// clear list
		timeline.clear();
		usernameList.clear();
		adapter.notifyDataSetInvalidated();
		clearImagePool();

		LoadTimerlineAsyncTask task = new LoadTimerlineAsyncTask(
				LoadTimerlineAsyncTask.MODE_USER_TIMELINE, this);

		task.setScreenName(screenName);
		task.setPageNumber(pageNumber);

		task.execute();
	}

	void loadMentionsTimeline(Integer pageNumber) {
		AnimationEffect.playFetchPageAnimation(TwitterTimelineActivity.this,
				loadingImage, true);

		// clear list
		timeline.clear();
		usernameList.clear();
		adapter.notifyDataSetInvalidated();
		clearImagePool();

		LoadTimerlineAsyncTask task = new LoadTimerlineAsyncTask(
				LoadTimerlineAsyncTask.MODE_MENTIONS_TIMELINE, this);
		task.setPageNumber(pageNumber);

		task.execute();
	}

	static class StatusViewHolder {
		TextView statusText;
		TextView postDate;
		Gallery preview;
	}

	static class UserViewHolder {
		WebImage profileImage;
		TextView createdByText;
		TextView statusCount;
	}

	class TimelineAdapter extends BaseExpandableListAdapter {

		HashMap<User, List<Status>> timeline = new HashMap<User, List<Status>>();
		ArrayList<User> usernameList = new ArrayList<User>();

		public TimelineAdapter(HashMap<User, List<Status>> timeline,
				ArrayList<User> usernameList) {
			this.timeline = timeline;
			this.usernameList = usernameList;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			User createdBy = usernameList.get(groupPosition);
			List<Status> statusList = timeline.get(createdBy);
			return statusList.get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return groupPosition * 10000 + childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			View view = convertView;

			if (view == null) {
				view = mChildInflater.inflate(R.layout.bubble_message, null);

				StatusViewHolder viewHolder = new StatusViewHolder();
				viewHolder.statusText = (TextView) view
						.findViewById(R.id.message);
				viewHolder.postDate = (TextView) view
						.findViewById(R.id.postDate);
				viewHolder.preview = (Gallery) view.findViewById(R.id.preview);

				view.setTag(viewHolder);
			}

			StatusViewHolder viewHolder = (StatusViewHolder) view.getTag();

			// bind values to view holder
			Status status = (Status) getChild(groupPosition, childPosition);

			String text = String.format("%s", status.getText());
			viewHolder.statusText.setText(text);
			Linkify.addLinks(viewHolder.statusText, Linkify.WEB_URLS);

			Date createdAt = status.getCreatedAt();
			viewHolder.postDate.setText(createdAt.toLocaleString());

			viewHolder.preview.setVisibility(View.GONE);
			FetchImageTask task = new FetchImageTask(
					TwitterTimelineActivity.this, viewHolder.preview, imagePool);
			task.execute(text);

			return view;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			User createdBy = usernameList.get(groupPosition);
			List<Status> statusList = timeline.get(createdBy);
			return statusList.size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			User createdBy = usernameList.get(groupPosition);
			return createdBy.screenName;
		}

		@Override
		public int getGroupCount() {
			return usernameList.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {

			View view = convertView;
			if (view == null) {
				view = mGroupInflater.inflate(R.layout.user_group, null);

				UserViewHolder viewHolder = new UserViewHolder();
				viewHolder.profileImage = (WebImage) view
						.findViewById(R.id.profileImage);
				viewHolder.createdByText = (TextView) view
						.findViewById(R.id.username);
				viewHolder.statusCount = (TextView) view
						.findViewById(R.id.statusCount);

				view.setTag(viewHolder);
			}

			User createdBy = usernameList.get(groupPosition);

			UserViewHolder viewHolder = (UserViewHolder) view.getTag();
			String normalProfileImageUrl = createdBy.profileImageUrl.toString();
			String bigProfileImageUrl = normalProfileImageUrl.replace(
					"_normal", "");
			viewHolder.profileImage.startLoading(bigProfileImageUrl, imagePool);
			viewHolder.createdByText.setText(createdBy.screenName);
			List<Status> statusList = timeline.get(createdBy);
			viewHolder.statusCount.setText(String.format("(%d)",
					statusList.size()));

			return view;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	public boolean onContextItemSelected(MenuItem menuItem) {
		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) menuItem
				.getMenuInfo();
		int groupPos = 0, childPos = 0;
		int type = ExpandableListView
				.getPackedPositionType(info.packedPosition);
		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
			groupPos = ExpandableListView
					.getPackedPositionGroup(info.packedPosition);
			childPos = ExpandableListView
					.getPackedPositionChild(info.packedPosition);
		}

		User createdBy = usernameList.get(groupPos);
		List<Status> statusList = timeline.get(createdBy);
		Status status = statusList.get(childPos);

		Log.d("palpal", String.format("selected item is group [%d] child[%d]",
				groupPos, childPos));
		// Pull values from the array we built when we created the list
		Intent intent;
		Bundle extras;

		switch (menuItem.getItemId()) {
		case MENU_REPLY_ALL:
			replyStatus(status);
			return true;
		case MENU_RETWEET_RT:
			retweetStatus(status);
			return true;
		case MENU_CONVERSATION:
			showConversation(status);
			return true;
		case MENU_VIEW_USER:
			intent = new Intent(this, TwitterTimelineActivity.class);
			extras = new Bundle();
			extras.putString(TwitterTimelineActivity.BUNDLE_MODE,
					LoadTimerlineAsyncTask.MODE_USER_TIMELINE);
			extras.putString(TwitterTimelineActivity.BUNDLE_SCREEN_NAME,
					screenName);
			intent.putExtras(extras);
			startActivity(intent);
			return true;
		case MENU_DIRECT_MESSAGE:
			directMessage(status);
			return true;
		case MENU_VIEW_USER_PROFILE:
			intent = new Intent(this, PersonActivity.class);
			extras = new Bundle();
			extras.putString(PersonActivity.BUNDLE_SCREEN_NAME, screenName);
			intent.putExtras(extras);
			startActivity(intent);
			return true;
		}
		return super.onContextItemSelected(menuItem);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.twitter_timeline_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_previous_page:
			loadPreviousPage();
			return true;
		case R.id.menu_next_page:
			loadNextPage();
			return true;
		case R.id.menu_share_status:
			composeStatus();
			return true;
		case R.id.menu_mentions:
			showMentions();
			return true;
		case R.id.menu_trigger_mode:
			if (showMode.equals(LoadTimerlineAsyncTask.SHOW_MODE_ALL)) {
				getLastStatusId();
			} else {
				showMode = LoadTimerlineAsyncTask.SHOW_MODE_ALL;
			}

			currentPageNumber = 1;
			executeModeAction(mode);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	void replyStatus(Status status) {
		Intent intent = new Intent(this, ComposeTwitterStatusActivity.class);
		intent.putExtra(ComposeTwitterStatusActivity.BUNDLE_MODE,
				ComposeTwitterStatusActivity.MODE_REPLY_ALL);
		intent.putExtra(ComposeTwitterStatusActivity.BUNDLE_STATUS, status);

		ArrayList<String> participants = findParticipantsInMessage(status.text);
		intent.putExtra(ComposeTwitterStatusActivity.BUNDLE_PARTICIPANTS,
				participants);
		startActivity(intent);
	}

	void retweetStatus(Status status) {
		Intent intent = new Intent(this, ComposeTwitterStatusActivity.class);
		intent.putExtra(ComposeTwitterStatusActivity.BUNDLE_MODE,
				ComposeTwitterStatusActivity.MODE_RETWEET_RT);
		intent.putExtra(ComposeTwitterStatusActivity.BUNDLE_STATUS, status);

		startActivity(intent);
	}

	void composeStatus() {
		Intent intent = new Intent(this, ComposeTwitterStatusActivity.class);
		intent.putExtra(ComposeTwitterStatusActivity.BUNDLE_MODE,
				ComposeTwitterStatusActivity.MODE_COMPOSE);
		startActivity(intent);
	}

	void directMessage(Status status) {
		Intent intent = new Intent(this, ComposeTwitterStatusActivity.class);
		intent.putExtra(ComposeTwitterStatusActivity.BUNDLE_MODE,
				ComposeTwitterStatusActivity.MODE_DIRECT_MESSAGE);
		intent.putExtra(ComposeTwitterStatusActivity.BUNDLE_RECIPIENT,
				status.user.getScreenName());
		startActivity(intent);
	}

	ArrayList<String> findParticipantsInMessage(String message) {
		ArrayList<String> participants = new ArrayList<String>();

		String[] tokens = message.split(" ");
		for (String token : tokens) {
			if (token.startsWith("@")) {
				participants.add(token.split("\\p{Punct}")[1]);
			}
		}

		return participants;
	}

	void showConversation(Status status) {
		Intent intent = new Intent(this, ConversationActivity.class);
		intent.putExtra(ConversationActivity.BUNDLE_STATUS, status);
		startActivity(intent);
	}

	void showMentions() {
		Intent intent = new Intent(this, TwitterTimelineActivity.class);
		intent.putExtra(BUNDLE_MODE,
				LoadTimerlineAsyncTask.MODE_MENTIONS_TIMELINE);
		startActivity(intent);
	}

	@Override
	public void onLoadTimelineCompleted(
			final HashMap<User, List<Status>> timeline2,
			final ArrayList<User> usernameList2) {

		runOnUiThread(new Runnable() {
			public void run() {

				Log.v("palpal",
						String.format("load completed, size: %d",
								timeline2.size()));

				AnimationEffect.playFetchPageAnimation(
						TwitterTimelineActivity.this, loadingImage, false);

				if (timeline2.size() > 0) {
					timeline.putAll(timeline2);
					usernameList.addAll(usernameList2);
					adapter.notifyDataSetChanged();

					timelineListView.setSelection(0);

					// last tweet
					if (mode.equals(LoadTimerlineAsyncTask.MODE_HOME_TIMELINE)
							&& currentPageNumber == 1) {
						User createdBy = usernameList
								.get(usernameList.size() - 1);
						List<Status> statusList = timeline.get(createdBy);
						Status status = statusList.get(statusList.size() - 1);
						Log.v("palpal",
								String.format("set last tweet %s",
										status.toString()));

						PalPalPreference.writeSinceIdPreference(
								TwitterTimelineActivity.this,
								status.id.toString());
					}
				} else {
					ToastUtil.showQuickToast(TwitterTimelineActivity.this,
							"no tweet in timeline");
				}
			}
		});

	}

	void loadPreviousPage() {
		if (currentPageNumber == null) {
			return;
		}

		if (currentPageNumber > 1) {

			timeline.clear();
			usernameList.clear();
			adapter.notifyDataSetInvalidated();
			clearImagePool();

			currentPageNumber--;
			executeModeAction(mode);
			// loadHomeTimeline(currentPageNumber);
		} else {
			ToastUtil.showQuickToast(this, "no previous page");
		}
	}

	void loadNextPage() {
		clearImagePool();

		currentPageNumber++;
		executeModeAction(mode);
		// loadHomeTimeline(currentPageNumber);
	}

	// Swipe Gesture Detector
	@Override
	public void onLeftSwipe() {
		loadNextPage();
	}

	@Override
	public void onRightSwipe() {
		loadPreviousPage();
	}

	@Override
	public void onShake() {
		Log.v("palpal", "twitter timeline onShake");

		for (int i = 0; i < usernameList.size(); ++i) {
			timelineListView.collapseGroup(i);
		}
	}

	/**
	 * get last read status id
	 */
	void getLastStatusId() {
		showMode = LoadTimerlineAsyncTask.SHOW_MODE_UPDATE_ONLY;
		String sinceIdString = PalPalPreference
				.loadTwitterSinceIdPreference(this);
		sinceId = sinceIdString != null ? Long.parseLong(sinceIdString) : null;
	}
}
