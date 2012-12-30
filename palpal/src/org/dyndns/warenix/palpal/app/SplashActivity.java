package org.dyndns.warenix.palpal.app;

import org.dyndns.warenix.image.CachedWebImage;
import org.dyndns.warenix.lab.taskservice.TaskService;
import org.dyndns.warenix.mission.facebook.util.FacebookMaster;
import org.dyndns.warenix.mission.twitter.util.TwitterMaster;
import org.dyndns.warenix.palpal.intent.PalPalIntent;
import org.dyndns.warenix.util.WLog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Window;

/**
 * The first activity launched from launcher. If all conditions are fulfilled,
 * it will start palpal main. Otherwise it will redirect user to fix those
 * conditions.
 * 
 * @author warren
 * 
 */
public class SplashActivity extends Activity {

	static {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectNetwork().penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
				.build());
	}

	/**
	 * init code
	 */
	static {
		CachedWebImage.setCacheDir(".palpal");
		WLog.setAppName("palpal");
		TaskService.setRunning(true);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		if (init()) {
			Intent intent = new Intent(PalPalIntent.ACTION_PALPAL_MAIN);
			startActivity(intent);
		}

		finish();
	}

	/**
	 * Init palpal. If all conditions ok, it will launch palpal main. Otherwise
	 * corresponding activity will be launched.
	 * 
	 * @return true if all conditoins ok.
	 */
	private boolean init() {
		if (!checkLinkedAccount()) {
			Intent intent = new Intent(this, AccountsActivity.class);
			startActivity(intent);
			return false;
		}
		return true;
	}

	boolean checkLinkedAccount() {
		Context appContext = getApplicationContext();
		if (TwitterMaster.restoreTwitterClient(appContext)
				|| FacebookMaster.restoreFacebook(appContext)) {
			return true;
		}
		return false;
	}
}
