package org.dyndns.warenix.palpal.social.facebook.activity;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;

import org.dyndns.warenix.gesture.SwipeGestureDetector;
import org.dyndns.warenix.gesture.SwipeGestureDetector.TimelineSwipeListener;
import org.dyndns.warenix.palpal.PalPalPreference;
import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.palpal.animation.AnimationEffect;
import org.dyndns.warenix.palpal.social.facebook.FacebookException;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookAPI;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.Album;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.facebook.android.Facebook;

/**
 * list albums of a given user
 * 
 * @author warenix
 * 
 */
public class AlbumActivity extends PalPalFacebookActivity implements
		TimelineSwipeListener {

	// String CONFIG_LIMIT = "10";

	// ui
	ImageView loadingImage;
	ListView listView;

	// data
	ArrayList<Album> albumList = new ArrayList<Album>();

	AlbumListAdapter albumListAdapter;

	String userId;

	// constants
	public static final String MODE_CHOOSE_ALBUM = "choose_album";
	public static final String MODE_FETCH_USER_ALBUMS = "fetch_user_albums";

	/**
	 * required, mode
	 */
	public static String BUNDLE_MODE = "mode";

	/**
	 * required, the user being queried
	 */
	public static String BUNDLE_USER_ID = "user_id";
	public static String BUNDLE_USER_NAME = "user_name";

	// paging
	ArrayList<String> sinceStack = new ArrayList<String>();
	boolean isLoading;

	@Override
	void setupUI() {
		setContentView(R.layout.facebook_album);
		listView = (ListView) findViewById(R.id.album_list);

		setTitle(String
				.format("View %s's Albums", extras.get(BUNDLE_USER_NAME)));
		listView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {

				AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
				// minus one because of the header view
				int position = info.position;
				menu.setHeaderTitle("Actions");
				Log.d("palpal", String.format("long pressed on %d", position));

				Album album = albumList.get(position);

				for (int i = 1; i < album.actionNameList.size(); ++i) {
					menu.add(0, i, 0, album.actionNameList.get(i));
				}
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d("palpal", String.format("item %d is clicked", position));
				Album album = albumList.get(position);

				String mode = extras.getString(BUNDLE_MODE);

				if (mode.equals(MODE_FETCH_USER_ALBUMS)) {
					// read album
					album.action(AlbumActivity.this, 0);
				} else if (mode.equals(MODE_CHOOSE_ALBUM)) {
					Intent intent = getIntent();
					intent.putExtra("album", album);
					setResult(RESULT_OK, intent);
					finish();
				}
			}
		});

		SwipeGestureDetector swipeGestureDetector = new SwipeGestureDetector(
				this, listView);

	}

	@Override
	void onFacebookReady(Facebook facebook,
			HashMap<String, SoftReference<Bitmap>> imagePool) {
		albumListAdapter = new AlbumListAdapter(this, albumList, imagePool);
		listView.setAdapter(albumListAdapter);
		loadingImage = (ImageView) findViewById(R.id.loadingImage);
		AnimationEffect.playFetchPageAnimation(this, loadingImage, true);
		isLoading = true;

		userId = extras.getString(BUNDLE_USER_ID);

		if (userId != null) {
			Log.d("palpal", String.format("fetching album of user %s", userId));

			currentPageNumber = 1;
			sinceStack.clear();
			sinceStack.add("");
			loadPage(currentPageNumber);
		}
	}

	static class AlbumListAdapter extends BaseAdapter {

		Context context;
		HashMap<String, SoftReference<Bitmap>> imagePool;
		ArrayList<Album> albumList;

		public AlbumListAdapter(Context context, ArrayList<Album> albumList,
				HashMap<String, SoftReference<Bitmap>> imagePool) {
			this.context = context;
			this.albumList = albumList;
			this.imagePool = imagePool;
		}

		@Override
		public int getCount() {
			return albumList.size();
		}

		@Override
		public Object getItem(int position) {
			if (position >= albumList.size()) {
				return null;
			}
			return albumList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (position >= albumList.size()) {
				return null;
			}

			Album album = albumList.get(position);
			return album.getView(context, convertView, imagePool);
		}
	}

	class FetchAlbumAsyncTask extends
			AsyncTask<Object, Void, FacebookException> {

		Context context;
		ArrayList<Album> albumList = new ArrayList<Album>();
		ListView listView;
		AlbumListAdapter albumListAdapter;
		HashMap<String, SoftReference<Bitmap>> imagePool;

		public FetchAlbumAsyncTask(Context context, ListView listView,
				AlbumListAdapter albumListAdapter, ArrayList<Album> albumList,
				HashMap<String, SoftReference<Bitmap>> imagePool) {
			this.context = context;
			this.listView = listView;
			this.albumListAdapter = albumListAdapter;
			this.albumList = albumList;
			this.imagePool = imagePool;
		}

		@Override
		protected FacebookException doInBackground(Object... params) {
			String userName = (String) params[0];
			Bundle parameters = (Bundle) params[1];

			String responseString = FacebookAPI.getUserAlbum(userName,
					parameters);
			JSONObject json;
			try {
				json = new JSONObject(responseString);

				JSONArray data = json.getJSONArray("data");

				for (int i = 0; i < data.length(); ++i) {
					// Album album = Album.factory((JSONObject) data.get(i));
					Album album = new Album((JSONObject) data.get(i));
					albumList.add(album);
					Log.d("palpal", String.format("album %s", album.toString()));
				}

			} catch (JSONException e) {
				e.printStackTrace();
			} catch (FacebookException e) {
				e.printStackTrace();
				return e;
			} catch (Exception e) {
				return new FacebookException("fail to get album",
						e.getMessage());
			}

			return null;
		}

		protected void onPostExecute(FacebookException e) {
			albumListAdapter.notifyDataSetChanged();
			AnimationEffect
					.playFetchPageAnimation(context, loadingImage, false);
			isLoading = false;

			if (e != null) {
				ToastUtil.showNotification(context, e.type, e.type, e.error,
						null, 1000);
			} else {
				if (albumList.size() == 0) {
					ToastUtil.showQuickToast(context, "no album");
					hasNextPage = false;
					sinceStack.add("");
				} else {
					hasNextPage = true;
					Album album = albumList.get(albumList.size() - 1);
					sinceStack.add(album.createdTime);
					Log.v("palpal", String.format("sinceStack add %s",
							album.createdTime));
				}
			}
		}

	}

	public boolean onContextItemSelected(MenuItem menuItem) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem
				.getMenuInfo();

		Album album = albumList.get((int) info.id);

		if (album.action(this, menuItem.getItemId())) {
			return true;
		}
		return super.onContextItemSelected(menuItem);
	}

	// option menu

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.facebook_album_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_next_page:
			loadNextPage();
			break;
		case R.id.menu_previous_page:
			loadPreviousPage();
			break;
		}
		return true;
	}

	void loadPage(Integer currentPage) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AnimationEffect.playFetchPageAnimation(AlbumActivity.this,
						loadingImage, true);
				isLoading = true;

				clearImagePool();
				albumList.clear();
				albumListAdapter.notifyDataSetInvalidated();
			}
		});

		Bundle parameters = new Bundle();
		String limit = PalPalPreference
				.loadPreferenceValue(
						this,
						getResources().getString(
								R.string.KEY_FACEBOOK_NUMBER_OF_ALBUMS), "5");
		parameters.putString("limit", limit);
		parameters.putString("until", sinceStack.get(sinceStack.size() - 1));
		// parameters.putString("limit", CONFIG_LIMIT);
		// parameters.putString("offset",
		// getOffSet(currentPageNumber, Integer.parseInt(limit)) + "");
		new FetchAlbumAsyncTask(this, listView, albumListAdapter, albumList,
				imagePool).execute(userId, parameters);
	}

	boolean loadNextPage() {
		if (!hasNextPage || sinceStack.size() == 0 || isLoading) {
			return false;
		}

		currentPageNumber++;
		runOnUiThread(new Runnable() {
			public void run() {
				ToastUtil.showQuickToast(AlbumActivity.this,
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
					ToastUtil.showQuickToast(AlbumActivity.this,
							"no previous page");
				}
			});
			return false;
		}

		currentPageNumber--;
		runOnUiThread(new Runnable() {
			public void run() {
				ToastUtil.showQuickToast(AlbumActivity.this,
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

	@Override
	public void onLeftSwipe() {
		loadNextPage();
	}

	@Override
	public void onRightSwipe() {
		loadPreviousPage();
	}
}
