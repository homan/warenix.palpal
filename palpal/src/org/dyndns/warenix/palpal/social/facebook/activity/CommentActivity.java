package org.dyndns.warenix.palpal.social.facebook.activity;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;

import org.dyndns.warenix.gesture.SwipeGestureDetector;
import org.dyndns.warenix.gesture.SwipeGestureDetector.TimelineSwipeListener;
import org.dyndns.warenix.palpal.PalPalPreference;
import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.palpal.social.facebook.FacebookException;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookAPI;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookUtil;
import org.dyndns.warenix.palpal.social.facebook.vo.FacebookPost;
import org.dyndns.warenix.palpal.social.facebook.vo.FacebookPostFactory;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.CommentFeed;
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
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.facebook.android.Facebook;

public class CommentActivity extends PalPalFacebookActivity implements
		TimelineSwipeListener {

	// constants
	public static final String BUNDLE_POST_ID = "post_id";
	public static final String BUNDLE_POST_TYPE = "post_type";
	public static final String BUNDLE_FEED = "feed";

	// ui
	ListView listView;

	EditText commentText;
	ImageButton submitButton;

	static FacebookPost headerFeed;

	static View headerView;

	// data
	static String postId;
	static String comment;

	HashMap<String, SoftReference<Bitmap>> headerImagePool;

	/**
	 * List of comments this post owns
	 */
	ArrayList<CommentFeed> commentList = new ArrayList<CommentFeed>();

	CommentListViewAdapter commentListAdapter;

	FacebookPost feed;
	String postType;

	static ArrayList<String> sinceStack = new ArrayList<String>();
	static boolean isLoading;

	@Override
	void onFacebookReady(Facebook facebook,
			HashMap<String, SoftReference<Bitmap>> imagePool) {
		postId = extras.getString(BUNDLE_POST_ID);
		postType = extras.getString(BUNDLE_POST_TYPE);

		if (postId.equals("")) {
			ToastUtil.showQuickToast(this,
					String.format("Cannot display post id = ", postId));
			try {
				finish();
				return;
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		isLoading = true;
		setProgressBarIndeterminateVisibility(true);
		commentListAdapter = new CommentListViewAdapter(this, commentList,
				imagePool);
		headerImagePool = new HashMap<String, SoftReference<Bitmap>>();

		feed = extras.getParcelable(BUNDLE_FEED);

		// paging
		currentPageNumber = 1;
		sinceStack.clear();
		sinceStack.add("");

		if (feed == null) {
			new FetchPostAsyncTask(this, listView, commentListAdapter,
					commentList, imagePool).execute(postId, postType,
					currentPageNumber.toString());
		} else {
			headerFeed = feed;

			View feedView = feed.getView(this, null, headerImagePool);
			listView.addHeaderView(feedView);
			listView.setAdapter(commentListAdapter);

			loadPage(currentPageNumber);
		}

	}

	@Override
	void setupUI() {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		// hide keyboard until user click textfield
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		setContentView(R.layout.facebook_comment);

		listView = (ListView) findViewById(R.id.comment_list);

		commentText = (EditText) findViewById(R.id.comment);

		submitButton = (ImageButton) findViewById(R.id.subimt);
		submitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// remove focus
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(commentText.getWindowToken(), 0);

				String commentURLEncoded = commentText.getText().toString();
				Log.d("palpal", String.format("comment post id [%s] of [%s]",
						postId, commentText.getText()));

				ToastUtil.showQuickToast(CommentActivity.this, "post +ing");

				// use application context instead of activity context
				new PostCommentAsyncTask(CommentActivity.this
						.getApplicationContext()).execute(postId,
						commentURLEncoded);
			}
		});

		listView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {

				AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
				// minus one because of the header view
				int position = info.position - 1;
				menu.setHeaderTitle("Actions");
				Log.d("palpal", String.format("long pressed on %d", position));

				// skip header
				if (position >= 0) {
					CommentFeed comment = commentList.get(position);
					for (int i = 1; i < comment.actionNameList.size(); ++i) {
						menu.add(0, i, 0, comment.actionNameList.get(i));
					}
				} else {
					if (headerFeed != null) {
						for (int i = 1; i < headerFeed.actionNameList.size(); ++i) {
							menu.add(0, i, 0, headerFeed.actionNameList.get(i));
						}
					}
				}
			}
		});
		SwipeGestureDetector swipeGestureDetector = new SwipeGestureDetector(
				this, listView);

	}

	public boolean onContextItemSelected(MenuItem menuItem) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem
				.getMenuInfo();

		// minus one because of the header view
		int position = info.position - 1;
		Log.d("palpal", String.format("selected position %d of list", position));

		if (position >= 0 && position < commentList.size()) {
			CommentFeed comment = commentList.get(position);

			if (comment.action(this, menuItem.getItemId())) {
				return true;
			}
			return super.onContextItemSelected(menuItem);
		} else if (position == -1) {
			return headerFeed.action(this, menuItem.getItemId());
		}
		return false;
	}

	// option menu

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.newsfeed_menu, menu);

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
		case R.id.menu_share:
			Intent intent = new Intent(this, ShareActivity.class);
			startActivity(intent);
			break;
		}
		return true;
	}

	void loadPage(Integer pageNumber) {
		super.loadPage(pageNumber);

		runOnUiThread(new Runnable() {
			public void run() {
				isLoading = true;
				setProgressBarIndeterminateVisibility(true);
				clearImagePool();
				commentList.clear();
				commentListAdapter.notifyDataSetInvalidated();
			}
		});

		// paging
		Bundle parameters = new Bundle();
		parameters.putString("since", sinceStack.get(sinceStack.size() - 1));
		String limit = PalPalPreference.loadPreferenceValue(
				this,
				getResources().getString(
						R.string.KEY_FACEBOOK_NUMBER_OF_COMMENTS), "5");
		parameters.putString("limit", limit);
		// paging

		new FetchCommentAsyncTask(this, commentListAdapter, commentList)
				.execute(postId, parameters);
	}

	boolean loadNextPage() {
		if (!hasNextPage || sinceStack.size() == 0 || isLoading) {
			return false;
		}

		currentPageNumber++;
		runOnUiThread(new Runnable() {
			public void run() {
				ToastUtil.showQuickToast(CommentActivity.this,
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
					ToastUtil.showQuickToast(CommentActivity.this,
							"no previous page");
				}
			});
			return false;
		}

		currentPageNumber--;
		runOnUiThread(new Runnable() {
			public void run() {
				ToastUtil.showQuickToast(CommentActivity.this,
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

	static class CommentListViewAdapter extends BaseAdapter {
		Context context;
		HashMap<String, SoftReference<Bitmap>> imagePool;
		ArrayList<CommentFeed> commentList;

		public CommentListViewAdapter(Context context,
				ArrayList<CommentFeed> commentList,
				HashMap<String, SoftReference<Bitmap>> imagePool) {
			this.context = context;
			this.commentList = commentList;
			this.imagePool = imagePool;
		}

		@Override
		public int getCount() {
			return commentList.size();
		}

		@Override
		public Object getItem(int pos) {
			return commentList.get(pos);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (position >= commentList.size()) {
				return null;
			}

			CommentFeed comment = commentList.get(position);
			View view = comment.getView(context, convertView, imagePool);
			return view;

		}
	}

	/* Async Task */

	/**
	 * fetch comments of a given post and setup the listadapter
	 */
	class FetchCommentAsyncTask extends
			AsyncTask<Object, Void, FacebookException> {

		Context context;
		ArrayList<CommentFeed> commentList = new ArrayList<CommentFeed>();
		CommentListViewAdapter commentListAdapter;

		public FetchCommentAsyncTask(Context context,
				CommentListViewAdapter commentListAdapter,
				ArrayList<CommentFeed> commentList) {
			this.context = context;
			this.commentListAdapter = commentListAdapter;
			this.commentList = commentList;
		}

		@Override
		protected FacebookException doInBackground(Object... params) {
			if (params.length < 1) {
				return null;
			}

			String postId = (String) params[0];
			Bundle parameters = (Bundle) params[1];

			String responseString = FacebookAPI.Feed.getAllComments(postId,
					parameters);
			try {
				JSONObject json = new JSONObject(responseString);

				JSONArray data = json.getJSONArray("data");

				for (int i = 0; i < data.length(); ++i) {
					JSONObject commentJSON = (JSONObject) data.get(i);
					CommentFeed comment = new CommentFeed(commentJSON);
					commentList.add(comment);

					if (commentListAdapter != null) {
						((Activity) context).runOnUiThread(new Runnable() {
							public void run() {
								commentListAdapter.notifyDataSetChanged();

							}
						});

					}
				}
				if (commentList.size() == 0) {
					hasNextPage = false;
					sinceStack.add("");

					((Activity) context).runOnUiThread(new Runnable() {
						public void run() {
							ToastUtil
									.showQuickToast(context, "no more comment");

						}
					});
				} else {
					hasNextPage = true;

					CommentFeed comment = commentList
							.get(commentList.size() - 1);
					sinceStack.add(comment.createdTime);
				}
				((Activity) context).runOnUiThread(new Runnable() {
					public void run() {
						((Activity) context)
								.setProgressBarIndeterminateVisibility(false);
						isLoading = false;
					}
				});

			} catch (JSONException e) {
			} catch (FacebookException e) {
				return e;
			}
			return null;
		}

		@Override
		protected void onPostExecute(FacebookException e) {
			if (e != null) {
				ToastUtil.showNotification(context, e.type, e.type, e.error,
						null, 1000);
			}
		}
	}

	/**
	 * post a comment to a post and make a toast on the result
	 */
	static class PostCommentAsyncTask extends
			AsyncTask<String, Void, FacebookException> {

		Context context;

		public PostCommentAsyncTask(Context context) {
			this.context = context;
		}

		@Override
		protected FacebookException doInBackground(String... params) {
			postId = params[0];
			comment = params[1];

			try {
				if (FacebookAPI.Feed.addComment(postId, comment) == false) {
					return new FacebookException("fail to post comment", "");
				}
			} catch (FacebookException e) {
				return e;
			}
			return null;

		}

		@Override
		protected void onPostExecute(FacebookException e) {
			if (e != null) {
				Intent notificationIntent = new Intent(
						context.getApplicationContext(), CommentActivity.class);
				notificationIntent.putExtra("post_id", postId);
				notificationIntent.putExtra("feed", headerFeed);
				notificationIntent.putExtra("comment", comment);
				ToastUtil.showNotification(context, "Fail to post comment",
						e.type, e.error, notificationIntent, 1000);
			} else {
				ToastUtil.showQuickToast(context, "post +ed");
			}
		}
	}

	class FetchPostAsyncTask extends AsyncTask<String, Void, Void> {

		Context context;
		ListView listView;
		HashMap<String, SoftReference<Bitmap>> imagePool;
		CommentListViewAdapter commentListAdapter;
		ArrayList<CommentFeed> commentList;

		public FetchPostAsyncTask(Context context, ListView listView,
				CommentListViewAdapter commentListAdapter,
				ArrayList<CommentFeed> commentList,
				HashMap<String, SoftReference<Bitmap>> imagePool) {
			this.context = context;
			this.listView = listView;
			this.commentListAdapter = commentListAdapter;
			this.commentList = commentList;
			this.imagePool = imagePool;
		}

		@Override
		protected Void doInBackground(String... params) {
			if (params.length < 2) {
				return null;
			}

			String postId = params[0];
			String postType = params[1];
			String pageNumber = params[2];

			if (postId == null) {
				Log.d("palpal", "missing post id");
				((Activity) (context)).runOnUiThread(new Runnable() {

					@Override
					public void run() {
						ToastUtil.showQuickToast(context, "missing post id");
					}
				});
				return null;
			}
			// headerFeed = FacebookAPI.factoryFacebookPost(postId, imagePool);

			String response = FacebookAPI.getPost(postId);

			try {
				JSONObject json = new JSONObject(response);
				if (postType == null) {
					postType = FacebookUtil.getJSONString(json, "type", null);
				}
				headerFeed = FacebookPostFactory.factory(postType, json);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if (headerFeed == null) {
				Log.d("palpal", "cannot create facebook post");
				((Activity) (context)).runOnUiThread(new Runnable() {

					@Override
					public void run() {
						ToastUtil.showQuickToast(context,
								"cannot create facebook post");
					}
				});
				return null;
			}

			((Activity) (context)).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					View feedView = headerFeed.getView(context, null,
							headerImagePool);
					listView.addHeaderView(feedView);
					listView.setAdapter(commentListAdapter);
				}
			});

			loadPage(Integer.parseInt(pageNumber));
			return null;
		}
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
