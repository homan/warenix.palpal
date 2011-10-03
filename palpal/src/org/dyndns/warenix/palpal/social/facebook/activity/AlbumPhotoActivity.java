package org.dyndns.warenix.palpal.social.facebook.activity;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.dyndns.warenix.gesture.SwipeGestureDetector;
import org.dyndns.warenix.gesture.SwipeGestureDetector.TimelineSwipeListener;
import org.dyndns.warenix.palpal.PalPal;
import org.dyndns.warenix.palpal.PalPalPreference;
import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.palpal.animation.AnimationEffect;
import org.dyndns.warenix.palpal.social.facebook.FacebookException;
import org.dyndns.warenix.palpal.social.facebook.Like;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookAPI;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.AlbumPhoto;
import org.dyndns.warenix.util.ToastUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.facebook.android.Facebook;

public class AlbumPhotoActivity extends PalPalFacebookActivity implements
		TimelineSwipeListener {

	// String CONFIG_LIMIT = "5";
	// constants
	public static final String BUNDLE_POST_ID = "post_id";

	// ui

	ListView listView;

	ImageView loadingImage;

	// data
	/**
	 * graph api object id of album
	 */
	String postId;
	String graphPath = "%s/photos";

	AlbumPhotoListViewAdapter AlbumPhotoListAdapter;
	/**
	 * List of AlbumPhotos this post owns
	 */
	ArrayList<AlbumPhoto> AlbumPhotoList = new ArrayList<AlbumPhoto>();

	// paging
	ArrayList<String> sinceStack = new ArrayList<String>();
	boolean isLoading;

	@Override
	void onFacebookReady(Facebook facebook,
			HashMap<String, SoftReference<Bitmap>> imagePool) {
		setContentView(R.layout.facebook_album_photo);

		loadingImage = (ImageView) findViewById(R.id.loadingImage);

		AlbumPhotoListAdapter = new AlbumPhotoListViewAdapter(this,
				AlbumPhotoList, imagePool);

		listView = (ListView) findViewById(R.id.album_photo_list);

		listView.setAdapter(AlbumPhotoListAdapter);
		listView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {

				AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

				AlbumPhoto AlbumPhoto = AlbumPhotoList.get((int) (info.id));
				Log.d("palpal", String.format("selected #%d on listview",
						(int) info.id));

				// Only create a context menu for child items
				// Array created earlier when we built the
				// expandable list
				menu.setHeaderTitle("Actions");

				for (int i = 1; i < AlbumPhoto.actionNameList.size(); ++i) {
					menu.add(0, i, 0, AlbumPhoto.actionNameList.get(i));
				}
			}

		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d("palpal", String.format("item %d is clicked", position));
				AlbumPhoto photo = AlbumPhotoList.get(position);
				photo.action(AlbumPhotoActivity.this, 0);
			}
		});

		SwipeGestureDetector swipeGestureDetector = new SwipeGestureDetector(
				this, listView);

		postId = extras.getString(BUNDLE_POST_ID);

		if (postId != null) {
			currentPageNumber = 1;
			sinceStack.clear();
			sinceStack.add("");
			loadPage(currentPageNumber);
		} else {
			ToastUtil.showQuickToast(this, "missing album post id");
		}
	}

	@Override
	void setupUI() {

	}

	static class AlbumPhotoListViewAdapter extends BaseAdapter {
		Context context;
		HashMap<String, SoftReference<Bitmap>> imagePool;
		ArrayList<AlbumPhoto> AlbumPhotoList;

		public AlbumPhotoListViewAdapter(Context context,
				ArrayList<AlbumPhoto> AlbumPhotoList,
				HashMap<String, SoftReference<Bitmap>> imagePool) {
			this.context = context;
			this.AlbumPhotoList = AlbumPhotoList;
			this.imagePool = imagePool;
		}

		@Override
		public int getCount() {
			return AlbumPhotoList.size();
		}

		@Override
		public Object getItem(int pos) {
			return AlbumPhotoList.get(pos);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (position >= AlbumPhotoList.size()) {
				return null;
			}

			AlbumPhoto AlbumPhoto = AlbumPhotoList.get(position);
			View view = AlbumPhoto.getView(context, convertView, imagePool);
			return view;

		}
	}

	public boolean onContextItemSelected(MenuItem menuItem) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem
				.getMenuInfo();

		AlbumPhoto AlbumPhoto = AlbumPhotoList.get((int) (info.id));

		if (AlbumPhoto.action(this, menuItem.getItemId())) {
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

	class FetchAlbumPhotoAsyncTask extends
			AsyncTask<Object, Void, FacebookException> {

		@Override
		protected FacebookException doInBackground(Object... params) {
			if (params.length < 2) {
				return null;
			}

			String postId = (String) params[0];
			Bundle parameters = (Bundle) params[1];
			try {
				fetchAlbumPhoto(postId, parameters);
			} catch (FacebookException e) {
				return e;
			}
			return null;
		}

		protected void onPostExecute(FacebookException e) {
			AnimationEffect.playFetchPageAnimation(AlbumPhotoActivity.this,
					loadingImage, false);
			isLoading = false;

			if (e != null) {
				ToastUtil.showNotification(AlbumPhotoActivity.this, e.type,
						e.type, e.error, null, 1000);
				hasNextPage = false;
			} else {
				if (AlbumPhotoList.size() > 0) {
					hasNextPage = true;
					AlbumPhoto albumPhoto = AlbumPhotoList.get(AlbumPhotoList
							.size() - 1);
					sinceStack.add(albumPhoto.createdTime);

					listView.setSelection(0);
				} else {
					ToastUtil.showQuickToast(AlbumPhotoActivity.this,
							"no photo");
					hasNextPage = false;
					sinceStack.add("");
				}
			}

		}

		void fetchAlbumPhoto(String post_id, Bundle parameters)
				throws FacebookException {
			Log.d("palpal", String.format("fetch album photo %s", post_id));

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					AnimationEffect.playFetchPageAnimation(
							AlbumPhotoActivity.this, loadingImage, true);
					isLoading = true;

					clearImagePool();
					AlbumPhotoList.clear();
					AlbumPhotoListAdapter.notifyDataSetChanged();
				}
			});

			String path = String.format(graphPath, post_id);
			try {

				String responseJSON = PalPal.getFacebook().request(path,
						parameters);
				Log.d("palpal", String.format("received AlbumPhoto string %s",
						responseJSON));

				JSONObject json = new JSONObject(responseJSON);
				final JSONArray data = json.getJSONArray("data");

				for (int i = 0; i < data.length(); ++i) {
					final AlbumPhoto AlbumPhotoFeed = new AlbumPhoto(
							data.getJSONObject(i));
					((Activity) AlbumPhotoActivity.this)
							.runOnUiThread(new Runnable() {
								@Override
								public void run() {

									AlbumPhotoList.add(AlbumPhotoFeed);

									ArrayList<Like> likeList = FacebookAPI
											.fetchListListOfAPost(AlbumPhotoFeed.id);
									AlbumPhotoFeed.likeList = likeList;

									AlbumPhotoListAdapter
											.notifyDataSetChanged();
								}

							});
				}

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	void loadPage(Integer currentPage) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				clearImagePool();
				AlbumPhotoList.clear();
				AlbumPhotoListAdapter.notifyDataSetInvalidated();
			}
		});

		Bundle parameters = new Bundle();
		parameters.putString("post_id", postId);
		String limit = PalPalPreference.loadPreferenceValue(
				this,
				getResources().getString(
						R.string.KEY_FACEBOOK_NUMBER_OF_ALBUM_PHOTOS), "5");
		parameters.putString("limit", limit);
		parameters.putString("since", sinceStack.get(sinceStack.size() - 1));
		new FetchAlbumPhotoAsyncTask().execute(postId, parameters);

	}

	boolean loadNextPage() {
		if (!hasNextPage || sinceStack.size() == 0 || isLoading) {
			return false;
		}

		currentPageNumber++;
		runOnUiThread(new Runnable() {
			public void run() {
				ToastUtil.showQuickToast(AlbumPhotoActivity.this,
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
					ToastUtil.showQuickToast(AlbumPhotoActivity.this,
							"no previous page");
				}
			});
			return false;
		}

		currentPageNumber--;
		runOnUiThread(new Runnable() {
			public void run() {
				ToastUtil.showQuickToast(AlbumPhotoActivity.this,
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
