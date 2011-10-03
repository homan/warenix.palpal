package org.dyndns.warenix.palpal.social.facebook.activity;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;

import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.palpal.animation.AnimationEffect;
import org.dyndns.warenix.palpal.social.facebook.FacebookException;
import org.dyndns.warenix.palpal.social.facebook.task.SearchFriendsAsyncTask;
import org.dyndns.warenix.palpal.social.facebook.task.SearchFriendsAsyncTask.SearchFriendsListener;
import org.dyndns.warenix.palpal.social.facebook.task.SearchGroupsAsyncTask;
import org.dyndns.warenix.palpal.social.facebook.task.SearchGroupsAsyncTask.SearchGroupsListener;
import org.dyndns.warenix.palpal.social.facebook.task.SearchLikesAsyncTask;
import org.dyndns.warenix.palpal.social.facebook.task.SearchLikesAsyncTask.SearchLikesListener;
import org.dyndns.warenix.palpal.social.facebook.vo.FacebookPost;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.Profile;
import org.dyndns.warenix.util.ToastUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.android.Facebook;

public class FriendsActivity extends PalPalFacebookActivity implements
		SearchFriendsListener, SearchGroupsListener, SearchLikesListener {

	public static final String MODE_FRIENDS = "mode_friends";
	public static final String MODE_GROUPS = "mode_groups";
	public static final String MODE_LIKES = "mode_likes";

	public static final String BUNDLE_MODE = "mode";

	/**
	 * required, the user being queried
	 */
	public static String BUNDLE_USER_ID = "user_id";

	// ui
	ImageView loadingImage;
	TextView keywordText;
	Button search;
	ListView resultListView;

	ArrayList<FacebookPost> resultList;
	SearchResultListViewAdapter adapter;

	static ArrayList<FacebookPost> copyResultList;

	// logic

	@Override
	void onFacebookReady(Facebook facebook,
			HashMap<String, SoftReference<Bitmap>> imagePool) {

		String userName = "me";
		String mode = MODE_FRIENDS;

		if (extras != null) {
			String passedMode = extras.getString(BUNDLE_MODE);
			if (passedMode != null) {
				mode = passedMode;
			}

			String passedUserId = extras.getString(BUNDLE_USER_ID);
			if (passedUserId != null) {
				userName = passedUserId;
			}
		}

		AnimationEffect.playFetchPageAnimation(FriendsActivity.this,
				loadingImage, true);

		Bundle parameters = new Bundle();

		if (mode.equals(MODE_FRIENDS)) {
			setTitle("PalPalFacebook - Friends");
			new SearchFriendsAsyncTask(this).execute(userName, parameters);
		} else if (mode.equals(MODE_GROUPS)) {
			setTitle("PalPalFacebook - Groups");
			new SearchGroupsAsyncTask(this).execute(userName, parameters);
		} else if (mode.equals(MODE_LIKES)) {
			setTitle("PalPalFacebook - Likes");
			new SearchLikesAsyncTask(this).execute(userName, parameters);
		}
	}

	@Override
	void setupUI() {
		setContentView(R.layout.facebook_search);

		loadingImage = (ImageView) findViewById(R.id.loadingImage);

		resultListView = (ListView) findViewById(R.id.result);

		resultListView
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {

						AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
						// minus one because of the header view
						int position = info.position;
						menu.setHeaderTitle("Actions");
						Log.d("palpal",
								String.format("long pressed on %d", position));

						// skip header
						if (position >= 0) {
							FacebookPost result = (FacebookPost) resultList
									.get(position);
							for (int i = 1; i < result.actionNameList.size(); ++i) {
								menu.add(0, i, 0, result.actionNameList.get(i));
							}
						}
					}
				});
		resultListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d("palpal", String.format("item %d is clicked", position));
				FacebookPost result = (FacebookPost) resultList.get(position);
				result.action(FriendsActivity.this, 0);
			}
		});

		resultListView.setTextFilterEnabled(true);

	}

	public boolean onContextItemSelected(MenuItem menuItem) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem
				.getMenuInfo();

		// minus one because of the header view
		int position = info.position;
		Log.d("palpal", String.format("selected position %d of list", position));

		if (position >= 0 && position < resultList.size()) {
			FacebookPost feed = (FacebookPost) resultList.get(position);

			if (feed.action(this, menuItem.getItemId())) {
				return true;
			}
			return super.onContextItemSelected(menuItem);
		}
		return false;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_SEARCH && event.getRepeatCount() == 0) {
			event.startTracking();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_SEARCH && event.isTracking()
				&& !event.isCanceled()) {
			onSearchKey();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	void onSearchKey() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}

	// SearchFriendsListener
	@Override
	public void onFriendsLoaded(ArrayList<FacebookPost> friendList) {
		AnimationEffect.playFetchPageAnimation(FriendsActivity.this,
				loadingImage, false);

		if (friendList == null || friendList.size() == 0) {
			ToastUtil.showQuickToast(this, "cannot load any friends");
			return;
		}

		adapter = new FriendResultListAdapter(this, friendList, imagePool);
		resultListView.setAdapter(adapter);

		resultList = friendList;
		copyResultList(resultList);
	}

	@Override
	public void onFriendsLoadedError(FacebookException e) {
		AnimationEffect.playFetchPageAnimation(FriendsActivity.this,
				loadingImage, false);

		ToastUtil.showNotification(this, "fail to load friends", e.type,
				e.error, null, 1000);
	}

	@Override
	public void onGroupLoadedError(FacebookException e) {
		AnimationEffect.playFetchPageAnimation(FriendsActivity.this,
				loadingImage, false);

		ToastUtil.showNotification(this, "fail to load groups", e.type,
				e.error, null, 1000);
	}

	@Override
	public void onGroupsLoaded(ArrayList<FacebookPost> friendList) {
		AnimationEffect.playFetchPageAnimation(FriendsActivity.this,
				loadingImage, false);

		if (friendList == null || friendList.size() == 0) {
			ToastUtil.showQuickToast(this, "cannot load any groups");
			return;
		}

		adapter = new FriendResultListAdapter(this, friendList, imagePool);
		resultListView.setAdapter(adapter);

		resultList = friendList;
		copyResultList(resultList);
	}

	@Override
	public void onLikesLoaded(ArrayList<FacebookPost> friendList) {
		AnimationEffect.playFetchPageAnimation(FriendsActivity.this,
				loadingImage, false);

		if (friendList == null || friendList.size() == 0) {
			ToastUtil.showQuickToast(this, "cannot load any likes pages");
			return;
		}

		adapter = new FriendResultListAdapter(this, friendList, imagePool);
		resultListView.setAdapter(adapter);

		resultList = friendList;
		copyResultList(resultList);
	}

	@Override
	public void onLikesLoadedError(FacebookException e) {
		AnimationEffect.playFetchPageAnimation(FriendsActivity.this,
				loadingImage, false);

		ToastUtil.showNotification(this, "fail to load likes", e.type, e.error,
				null, 1000);
	}

	@SuppressWarnings("unchecked")
	protected void copyResultList(ArrayList<FacebookPost> resultList) {
		copyResultList = (ArrayList<FacebookPost>) resultList.clone();
	}

	static abstract class SearchResultListViewAdapter extends BaseAdapter {

		Context context;
		ArrayList<FacebookPost> resultList;
		HashMap<String, SoftReference<Bitmap>> imagePool;

		public SearchResultListViewAdapter(Context context,
				ArrayList<FacebookPost> resultList,
				HashMap<String, SoftReference<Bitmap>> imagePool) {
			this.context = context;
			this.resultList = resultList;
			this.imagePool = imagePool;
		}

		@Override
		public int getCount() {
			if (resultList != null) {
				return resultList.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return resultList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public abstract View getView(int position, View convertView,
				ViewGroup parent);
	}

	static class FriendResultListAdapter extends SearchResultListViewAdapter
			implements Filterable {

		public FriendResultListAdapter(Context context,
				ArrayList<FacebookPost> resultList,
				HashMap<String, SoftReference<Bitmap>> imagePool) {
			super(context, resultList, imagePool);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Profile profile = (Profile) resultList.get(position);
			return profile.getView(context, convertView, imagePool);
		}

		@Override
		public Filter getFilter() {
			return new ListFilter();
		}

		class ListFilter extends Filter {

			protected FilterResults performFiltering(CharSequence prefix) {
				// NOTE: this function is *always* called from a background
				// thread,
				// and
				// not the UI thread.

				FilterResults results = new FilterResults();
				ArrayList<FacebookPost> i = new ArrayList<FacebookPost>();

				String prefixString = prefix.toString();
				if (prefix != null && prefixString.length() > 0) {

					for (int index = 0; index < copyResultList.size(); index++) {
						FacebookPost si = (FacebookPost) copyResultList
								.get(index);

						if (si.matchFilter(prefix)) {
							i.add(si);
						}
					}
					results.values = i;
					results.count = i.size();
				} else {
					synchronized (copyResultList) {
						results.values = copyResultList;
						results.count = copyResultList.size();
					}
				}

				return results;
			}

			@Override
			protected void publishResults(CharSequence prefix,
					FilterResults results) {
				// NOTE: this function is *always* called from the UI thread.

				if (results != null && results.count >= 0) {
					clearImagePool();
					resultList.clear();

					@SuppressWarnings("unchecked")
					ArrayList<FacebookPost> filterdResult = (ArrayList<FacebookPost>) results.values;
					resultList.addAll(filterdResult);
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}
		}
	}

}
