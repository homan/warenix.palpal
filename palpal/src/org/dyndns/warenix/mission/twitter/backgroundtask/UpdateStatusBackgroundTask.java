package org.dyndns.warenix.mission.twitter.backgroundtask;

import org.dyndns.warenix.lab.compat1.util.Memory;
import org.dyndns.warenix.lab.taskservice.BackgroundTask;

import twitter4j.Status;

public class UpdateStatusBackgroundTask implements BackgroundTask {
	String message;

	public UpdateStatusBackgroundTask(String message) {
		this.message = message;
	}

	@Override
	public Object onExecute() throws Exception {
		Status response = Memory.getTwitterClient().updateStatus(message);
		return response;
	}

	@Override
	public Object getResult() {
		return null;
	}

}
