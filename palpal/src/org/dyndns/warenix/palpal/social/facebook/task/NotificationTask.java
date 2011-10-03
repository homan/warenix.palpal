package org.dyndns.warenix.palpal.social.facebook.task;

import org.dyndns.warenix.palpal.social.facebook.FacebookException;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookAPI;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.Notification;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.facebook.android.FacebookError;

/**
 * download image in background using AsyncTask with sd card cache.
 * 
 * optionally execute(String saveAsFileName)
 * 
 * @author warenix
 * 
 */
public class NotificationTask extends
		AsyncTask<String, Void, FacebookException> {

	final String LOG_TAG = this.getClass().getSimpleName().toString();

	public static String SHOW_UNREAD_ONLY_NOTIFICATION = "1";
	public static String SHOW_ALL_NOTIFICATION = "0";

	boolean isCancelled = false;
	Context context;

	NotificationTaskListener listener;

	/**
	 * download an image from web and attach the image to the image view
	 * 
	 * @param image_view
	 *            the image will be displayed in this image view
	 * @param url
	 *            the url to the image needed to be downloaded
	 */
	public NotificationTask(Context context, NotificationTaskListener listener) {
		super();
		this.context = context;
		this.listener = listener;
	}

	protected FacebookException doInBackground(String... params) {
		if (isCancelled) {
			return null;
		}
		try {
			String responseString = FacebookAPI.notificationsGetListByFQL(
					params[0], Integer.parseInt(params[1]),
					Integer.parseInt(params[2]));

			if (responseString == null || isCancelled
					|| responseString.equals("{}")) {
				Log.d("palpal", "do not have notification");
				return null;
			}

			JSONArray notificationJSON = new JSONArray(responseString);
			notificationJSON.length();

			for (int i = 0; i < notificationJSON.length(); ++i) {
				Notification notification = new Notification(
						(JSONObject) notificationJSON.get(i));

				Log.d("palpal",
						String.format("got notification:%s",
								notification.toString()));

				listener.onNotificationLoaded(notification);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (FacebookException e) {
			return e;
		} catch (JSONException e) {
			return new FacebookException("json parse exception", e.getMessage());
		}
		return null;
	}

	protected void onPostExecute(FacebookException e) {
		listener.onLoadNotificationCompleted();
		Log.v("palpal", "retrieve notification task end");
	}

	/**
	 * cancel aysnctask
	 * 
	 * @param mayInterruptIfRunning
	 * @return
	 */
	boolean cancel(Boolean mayInterruptIfRunning) {
		isCancelled = true;
		return super.cancel(mayInterruptIfRunning);
	}

	public interface NotificationTaskListener {
		public void onNotificationLoaded(Notification notification);

		public void onLoadNotificationCompleted();
	}
}
