package org.dyndns.warenix.palpal.social.twitter.task;

import java.util.ArrayList;

import org.dyndns.warenix.palpal.social.twitter.TwitterMaster;

import android.os.AsyncTask;
import android.util.Log;

public class LoadConversationAsyncTask extends AsyncTask<Object, Void, Void> {

	winterwell.jtwitter.Twitter.Status status;
	ArrayList<winterwell.jtwitter.Twitter.Status> statusList;

	LoadConversationListener listener;

	@Override
	protected Void doInBackground(Object... params) {
		status = (winterwell.jtwitter.Twitter.Status) params[0];
		listener = (LoadConversationListener) params[1];

		statusList = TwitterMaster.status.getConvsersation(status, listener);
		return null;
	}

	protected void onPostExecute(Void v) {
		for (winterwell.jtwitter.Twitter.Status status1 : statusList) {
			Log.d("palpal", status1.toString());
		}

		listener.onLoadConversationCompleted(statusList);
	}

	public interface LoadConversationListener {
		public void onLoadConversationCompleted(
				ArrayList<winterwell.jtwitter.Twitter.Status> statusList);

		public void onConversationLoaded(
				winterwell.jtwitter.Twitter.Status status);
	}
}
