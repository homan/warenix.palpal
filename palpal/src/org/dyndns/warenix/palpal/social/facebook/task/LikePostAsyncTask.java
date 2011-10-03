package org.dyndns.warenix.palpal.social.facebook.task;

import org.dyndns.warenix.palpal.social.facebook.FacebookException;
import org.dyndns.warenix.palpal.social.facebook.activity.CommentActivity;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookAPI;
import org.dyndns.warenix.util.ToastUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class LikePostAsyncTask extends AsyncTask<String, Void, Void> {

	public static final String GRAPH_API = "graph_api";
	public static final String REST_API = "rest_api";

	Context context;

	public LikePostAsyncTask(Context context) {
		this.context = context;
	}

	@Override
	protected Void doInBackground(String... params) {
		String postId = params[0];
		// default using graph api
		String apiType = GRAPH_API;

		if (params.length > 1) {
			apiType = params[1];
		}

		if (context instanceof Activity) {
			((Activity) (context)).runOnUiThread(new Runnable() {
				public void run() {
					ToastUtil.showQuickToast(context, "like +ing");
				}
			});

		}

		boolean success = false;
		try {
			if (apiType.equals(GRAPH_API)) {
				// graph api to like post
				success = FacebookAPI.Feed.addLike(postId);
			} else if (apiType.equals(REST_API)) {
				success = FacebookAPI.likePost(postId);
			}
			if (success) {
				((Activity) (context)).runOnUiThread(new Runnable() {
					public void run() {
						ToastUtil.showQuickToast(context, "like +ed");
					}
				});
			}
		} catch (FacebookException e) {
			Intent notificationIntent = new Intent(context
					.getApplicationContext(), CommentActivity.class);
			notificationIntent.putExtra("post_id", postId);
			ToastUtil.showNotification(context, "Fail to like", e.type,
					e.error, notificationIntent, 1000);
		}
		Log.d("palpal", String.format("like post result [%s]", success));

		return null;
	}
}
