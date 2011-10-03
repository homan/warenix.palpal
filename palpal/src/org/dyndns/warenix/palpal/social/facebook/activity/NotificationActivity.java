package org.dyndns.warenix.palpal.social.facebook.activity;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;

import org.dyndns.warenix.palpal.PalPal;
import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.palpal.social.facebook.FacebookPostTypeResult;
import org.dyndns.warenix.palpal.social.facebook.task.MarkNotificationTask;
import org.dyndns.warenix.palpal.social.facebook.task.NotificationTask;
import org.dyndns.warenix.palpal.social.facebook.task.NotificationTask.NotificationTaskListener;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookUtil;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.NoteFeed;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.Notification;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.Profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.android.Facebook;

public class NotificationActivity extends PalPalFacebookActivity implements
		NotificationTaskListener {

	public static final String BUNDLE_MODE = "mode";
	public static final String MODE_SHOW_ALL = "0";
	public static final String MODE_SHOW_UNREAD_ONLY = "1";

	ArrayList<Notification> notificationList;
	NotificationListAdapter listAdapter;

	String CONFIG_FETCH_NOTIFICATION_LIMIT = "10";

	ListView listView;

	@Override
	void onFacebookReady(Facebook facebook,
			HashMap<String, SoftReference<Bitmap>> imagePool) {
		notificationList = new ArrayList<Notification>();
		listAdapter = new NotificationListAdapter(this, notificationList,
				imagePool);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Notification notification = notificationList.get(position);
				Log.d("palpal",
						String.format("tapped notification %s", notification));

				FacebookPostTypeResult result = FacebookUtil
						.determineFacebookPostType(notification);

				if (result != null) {
					Log.d("palpal", String.format(
							"determined type %s, postid %s", result.type,
							result.postId));

					if (result.type != null && result.type.equals("group")) {
						Intent intent = new Intent(NotificationActivity.this,
								NewsFeedActivity.class);
						Bundle extras = new Bundle();
						extras.putString(NewsFeedActivity.BUNDLE_GRAPH_PATH,
								String.format("%s/feed", result.postId));
						intent.putExtras(extras);
						startActivity(intent);
					} else {
						Intent intent = new Intent(NotificationActivity.this,
								CommentActivity.class);
						Bundle extras = new Bundle();
						extras.putString(CommentActivity.BUNDLE_POST_ID,
								result.postId);
						// "10150136122630522");
						extras.putString(CommentActivity.BUNDLE_POST_TYPE,
								result.type);
						// NoteFeed.TYPE);
						intent.putExtras(extras);
						startActivity(intent);
					}

					new MarkNotificationTask(NotificationActivity.this)
							.execute(notification.notificationId);
				} else {
					Log.v("palpal", String.format(
							"cannot deteremine post id for notification %s",
							notification));
				}

			}
		});

		String mode = MODE_SHOW_UNREAD_ONLY;
		if (extras != null) {
			String passedMode = extras.getString(BUNDLE_MODE);
			if (passedMode != null) {
				mode = passedMode;
			}
		}

		setProgressBarIndeterminateVisibility(true);
		Profile authenticatedUserProfile = PalPal.getAuthenticatedUserProfile();
		if (authenticatedUserProfile != null) {
			new NotificationTask(this.getApplicationContext(), this).execute(
					authenticatedUserProfile.id, mode,
					CONFIG_FETCH_NOTIFICATION_LIMIT);
		}
	}

	@Override
	void setupUI() {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.facebook_notification);

		listView = (ListView) findViewById(R.id.ListView01);
	}

	static class NotificationListAdapter extends BaseAdapter {
		Context context;
		ArrayList<Notification> notificationList;
		HashMap<String, SoftReference<Bitmap>> imagePool;

		public NotificationListAdapter(Context context,
				ArrayList<Notification> notificationList,
				HashMap<String, SoftReference<Bitmap>> imagePool) {

			this.context = context;
			this.notificationList = notificationList;
			this.imagePool = imagePool;
		}

		@Override
		public int getCount() {
			return notificationList.size();
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
			if (position >= notificationList.size()) {
				return null;
			}
			Notification notification = notificationList.get(position);
			TextView textView = new TextView(context);
			textView.setText(notification.toString());

			return notification.getView(context, convertView, imagePool);
		}
	}

	@Override
	public void onNotificationLoaded(final Notification notification) {
		runOnUiThread(new Runnable() {
			public void run() {
				notificationList.add(notification);
				// listAdapter.notifyDataSetChanged();
				listAdapter.notifyDataSetInvalidated();

				Log.d("palpal", String.format("notification list size = %d",
						notificationList.size()));
			}
		});

	}

	@Override
	public void onLoadNotificationCompleted() {
		setProgressBarIndeterminateVisibility(false);
	}

}
