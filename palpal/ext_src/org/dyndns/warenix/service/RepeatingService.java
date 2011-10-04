package org.dyndns.warenix.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

/**
 * Repeating Service perform background job repeatedly until
 * RepeatingService.stop() is called. Subclass can change the period inside
 * doInBackground()
 * 
 * @author warenix
 * 
 */
public abstract class RepeatingService extends IntentService {

	public RepeatingService(String name) {
		super(name);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private Handler handler = new Handler();

	private long updatePeriodInSecond = 5000;

	private Runnable runningJob = new Runnable() {
		public void run() {
			if (doInBackground()) {
				handler.postDelayed(this, updatePeriodInSecond);
			}
		}
	};

	public void setPeriodInSecond(long newUpdatePeriodInSecond) {
		updatePeriodInSecond = newUpdatePeriodInSecond;
	}

	public long getPeriodInSecond() {
		return updatePeriodInSecond;
	}

	/**
	 * perform job in background
	 * 
	 * @return
	 */
	public abstract boolean doInBackground();

	@Override
	public void onStart(Intent intent, int startId) {
		// handler.postDelayed(runningJob, updatePeriodInSecond);
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		// handler.removeCallbacks(runningJob);
		super.onDestroy();
	}

}
