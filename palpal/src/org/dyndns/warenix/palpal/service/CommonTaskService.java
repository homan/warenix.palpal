package org.dyndns.warenix.palpal.service;

import org.dyndns.warenix.palpal.social.twitter.commonTask.CommonTask;

import android.app.IntentService;
import android.content.Intent;

public class CommonTaskService extends IntentService {

	public static final String BUNDLE_TASK = "task";
	public static final String BUNDLE_LISTENER = "listener";

	public CommonTaskService() {
		super("Common Task Service");
	}

	public CommonTaskService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		CommonTask commonTask = (CommonTask) intent
				.getSerializableExtra(BUNDLE_TASK);
		commonTask.execute(getApplicationContext());
	}

}
