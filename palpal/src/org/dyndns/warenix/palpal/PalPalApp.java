package org.dyndns.warenix.palpal;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.dyndns.warenix.palpal.social.facebook.activity.AlbumActivity;
import org.dyndns.warenix.palpal.social.facebook.activity.CheckinsActivity;
import org.dyndns.warenix.palpal.social.facebook.activity.FriendsActivity;
import org.dyndns.warenix.palpal.social.facebook.activity.NewsFeedActivity;
import org.dyndns.warenix.palpal.social.facebook.activity.NotificationActivity;
import org.dyndns.warenix.palpal.social.twitter.TwitterStreamActivity;
import org.dyndns.warenix.palpal.social.twitter.TwitterTimelineActivity;
import org.dyndns.warenix.palpal.social.twitter.task.LoadTimerlineAsyncTask;
import org.dyndns.warenix.util.ToastUtil;
import org.dyndns.warenix.widget.WebImage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class PalPalApp extends Activity {

	// ui
	ListView listView;

	// data
	ArrayList<Shortcut> shortcutList;
	protected static LayoutInflater inflater;
	ShortcutListAdapter adapter;

	/**
	 * shared pool of images for this activity context
	 */
	HashMap<String, SoftReference<Bitmap>> imagePool;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setupUI();

		onReady();
	}

	protected void onDestroy() {
		clearImagePool();
		super.onDestroy();
	}

	void clearImagePool() {
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

	void setupUI() {
		setContentView(R.layout.palpal_app);
		listView = (ListView) findViewById(R.id.listView);
	}

	void onReady() {
		imagePool = new HashMap<String, SoftReference<Bitmap>>();

		addShortcut();

		adapter = new ShortcutListAdapter(this, shortcutList, imagePool);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Shortcut shortcut = shortcutList.get(position);
				startActivity(shortcut.intent);
			}

		});

	}

	void addShortcut() {
		shortcutList = new ArrayList<Shortcut>();
		Bundle extras;

		extras = new Bundle();
		shortcutList.add(new Shortcut("Twitter Stream", R.drawable.t, this,
				TwitterStreamActivity.class, extras));

		extras = new Bundle();
		shortcutList.add(new Shortcut("Facebook Checkins", R.drawable.w, this,
				CheckinsActivity.class, extras));

		extras = new Bundle();
		shortcutList.add(new Shortcut("Facebook Wall", R.drawable.w, this,
				NewsFeedActivity.class, extras));

		extras = new Bundle();
		extras.putString(TwitterTimelineActivity.BUNDLE_MODE,
				LoadTimerlineAsyncTask.MODE_HOME_TIMELINE);
		shortcutList.add(new Shortcut("Twitter Home Timeline", R.drawable.t,
				this, TwitterTimelineActivity.class, extras));

		extras = new Bundle();
		extras.putString(NotificationActivity.BUNDLE_MODE,
				NotificationActivity.MODE_SHOW_ALL);
		shortcutList.add(new Shortcut("Facebook Notifications", R.drawable.n,
				this, NotificationActivity.class, extras));

		extras = new Bundle();
		extras.putString(TwitterTimelineActivity.BUNDLE_MODE,
				LoadTimerlineAsyncTask.MODE_MENTIONS_TIMELINE);
		shortcutList.add(new Shortcut("Twitter Mentions", R.drawable.m, this,
				TwitterTimelineActivity.class, extras));

		extras = new Bundle();
		extras.putString(FriendsActivity.BUNDLE_MODE,
				FriendsActivity.MODE_FRIENDS);
		shortcutList.add(new Shortcut("Facebook Friends", R.drawable.f, this,
				FriendsActivity.class, extras));

		extras = new Bundle();
		extras.putString(AlbumActivity.BUNDLE_MODE,
				AlbumActivity.MODE_FETCH_USER_ALBUMS);
		extras.putString(AlbumActivity.BUNDLE_USER_ID, "me");
		extras.putString(AlbumActivity.BUNDLE_USER_NAME, "me");
		shortcutList.add(new Shortcut("Facebook Albums", R.drawable.a, this,
				AlbumActivity.class, extras));

		extras = new Bundle();
		extras.putString(FriendsActivity.BUNDLE_MODE,
				FriendsActivity.MODE_GROUPS);
		shortcutList.add(new Shortcut("Facebook Groups", R.drawable.g, this,
				FriendsActivity.class, extras));

		extras = new Bundle();
		extras.putString(FriendsActivity.BUNDLE_MODE,
				FriendsActivity.MODE_LIKES);
		shortcutList.add(new Shortcut("Facebook Likes", R.drawable.l, this,
				FriendsActivity.class, extras));

	}

	static class Shortcut {
		String name;
		int icon;
		Intent intent;

		public Shortcut(String name, int icon, Context context,
				Class<?> activityClass, Bundle extras) {
			this.name = name;
			this.icon = icon;

			intent = new Intent(context, activityClass);
			intent.putExtras(extras);
		}
	}

	static class ViewHolder {
		TextView nameText;
		WebImage profileImage;
	}

	static class ShortcutListAdapter extends BaseAdapter {

		// data
		Context context;
		ArrayList<Shortcut> shortcutList;;
		HashMap<String, SoftReference<Bitmap>> imagePool;

		public ShortcutListAdapter(Context context,
				ArrayList<Shortcut> shortcutList,
				HashMap<String, SoftReference<Bitmap>> imagePool) {
			this.context = context;
			this.shortcutList = shortcutList;
			this.imagePool = imagePool;

		}

		@Override
		public int getCount() {
			return shortcutList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (inflater == null) {
				inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}

			View view = convertView;
			if (view == null) {
				view = inflater.inflate(R.layout.shortcut, null);

				ViewHolder viewHolder = new ViewHolder();
				viewHolder.nameText = (TextView) view
						.findViewById(R.id.username);
				viewHolder.profileImage = (WebImage) view
						.findViewById(R.id.profileImage);
				view.setTag(viewHolder);
			}

			ViewHolder viewHolder = (ViewHolder) view.getTag();
			Shortcut shortcut = shortcutList.get(position);
			viewHolder.nameText.setText(shortcut.name);
			try {
				viewHolder.profileImage.setImageResource(shortcut.icon);
			} catch (Throwable e) {
				e.printStackTrace();
			}

			return view;
		}

	}

	// option menu

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.palpal_preference, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent intent = new Intent(this, PalPalPreferenceActivity.class);
			startActivity(intent);
			break;
		}
		return true;
	}

	long lastBackPressTime;

	@Override
	public void onBackPressed() {
		if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
			ToastUtil
					.showQuickToast(this, "Press back again to close this app");
			this.lastBackPressTime = System.currentTimeMillis();
		} else {
			finish();
			System.exit(0);
		}
	}
}
