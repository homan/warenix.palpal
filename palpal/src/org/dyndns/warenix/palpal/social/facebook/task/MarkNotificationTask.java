package org.dyndns.warenix.palpal.social.facebook.task;

import org.dyndns.warenix.palpal.social.facebook.FacebookException;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookAPI;
import org.dyndns.warenix.util.ToastUtil;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class MarkNotificationTask extends AsyncTask<String, Void, String> {

	final String LOG_TAG = this.getClass().getSimpleName().toString();

	boolean isCancelled = false;
	Context context;

	public MarkNotificationTask(Context context) {
		super();
		this.context = context;
	}

	protected String doInBackground(String... params) {
		if (isCancelled || params.length < 1) {
			return null;
		}
		try {
			String notificationId = params[0];
			boolean isMarked = FacebookAPI.notificationMarkRead(notificationId);

			Log.d("palpal", String.format("mark notification %s = %s",
					notificationId, isMarked));

		} catch (FacebookException e) {
			ToastUtil.showQuickToast(context, e.toString());
		}
		return null;
	}

	protected void onPostExecute(String responseString) {
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
}
