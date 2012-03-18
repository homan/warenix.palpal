package org.dyndns.warenix.mission.timeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.dyndns.warenix.lab.compat1.util.AndroidUtil;
import org.dyndns.warenix.pattern.baseListView.AsyncListAdapter;
import org.dyndns.warenix.pattern.baseListView.AsyncListAdapter.BackgroundLogic;
import org.dyndns.warenix.util.WLog;

import android.content.Context;
import android.widget.ListView;

/**
 * Load timeline messagess asynchronously by running multiple threads in
 * parallel and return result once all of them finished or a timeout has
 * reached.
 * 
 * @author warenix
 * 
 */
public class TimelineAsyncAdapter extends AsyncListAdapter implements
		BackgroundLogic {
	private static final String TAG = "TimelineAsyncAdapter";

	/**
	 * When the whole refresh timeout. Unit is in millionsecond.
	 */
	protected final int TIMEOUT = 3 * 60 * 1000;

	/**
	 * Load timeline messages in multiple threads.
	 */
	private ArrayList<Runnable> runnableList = new ArrayList<Runnable>();

	public TimelineAsyncAdapter(Context context, ListView listView) {
		super(context, listView);
		setBackgroundLogic(this);
	}

	private Object lock = new Object();

	protected ArrayList<TimelineMessageListViewItem> dataList = new ArrayList<TimelineMessageListViewItem>();

	// +exp
	// try to let runnable to knwo when to not update dateList after timeout.
	protected enum RefreshState {
		NOT_STARTED, DONE;
	};

	protected RefreshState mRefreshState;

	// -exp

	/**
	 * caller runnable notify adapter that it has finished
	 */
	protected void notifyRunnableDone() {
		synchronized (lock) {
			lock.notify();
		}
	}

	/**
	 * all runnables will be executed in parallel.
	 * 
	 * @param runnable
	 */
	protected void addRunnable(Runnable runnable) {
		runnableList.add(runnable);
	}

	// +AsyncRefersh
	@Override
	public void onPostExecut(Object result) {
		WLog.i(TAG, "onPostExecute");

		itemList.clear();
		if (dataList.size() > 0) {
			Collections.sort(dataList);
			itemList.addAll(dataList);
			notifyDataSetChanged();
		}
		AndroidUtil.playListAnimation(listView);
	}

	public Object doInBackground() {
		WLog.i(TAG, "doInBackground");
		mRefreshState = RefreshState.NOT_STARTED;
		dataList.clear();
		
		int count = runnableList.size();
		for (int i = 0; i < count; ++i) {
			new Thread(runnableList.get(i)).start();
		}
		while (count > 0) {
			WLog.d(TAG, new Date().toLocaleString() + " " + count
					+ " runnable remaining");
			try {
				synchronized (lock) {
					lock.wait(TIMEOUT);
					count--;
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}

		}
		mRefreshState = RefreshState.DONE;
		WLog.i(TAG, new Date().toLocaleString() + " doInBackground finished");
		return null;
	}

}
