package org.dyndns.warenix.mission.twitter.backgroundtask;

import org.dyndns.warenix.lab.compat1.util.Memory;
import org.dyndns.warenix.lab.taskservice.BackgroundTask;

import twitter4j.Status;
import twitter4j.StatusUpdate;

public class ReplyStatusBackgroundTask implements BackgroundTask {
	String message;
	long inReplyToStatusId;

	public ReplyStatusBackgroundTask(long inReplyToStatusId, String message) {
		this.message = message;
		this.inReplyToStatusId = inReplyToStatusId;
	}

	@Override
	public Object onExecute() throws Exception {
		StatusUpdate statusUpdate = new StatusUpdate(message);
		statusUpdate.setInReplyToStatusId(inReplyToStatusId);
		Status response = Memory.getTwitterClient().updateStatus(statusUpdate);
		return response;
	}

	@Override
	public Object getResult() {
		return null;
	}

}
