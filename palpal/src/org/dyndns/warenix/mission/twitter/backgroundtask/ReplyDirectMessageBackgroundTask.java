package org.dyndns.warenix.mission.twitter.backgroundtask;

import org.dyndns.warenix.lab.compat1.util.Memory;
import org.dyndns.warenix.lab.taskservice.BackgroundTask;

import twitter4j.DirectMessage;
import twitter4j.User;

public class ReplyDirectMessageBackgroundTask implements BackgroundTask {
	String message;
	User inReplyToUser;

	public ReplyDirectMessageBackgroundTask(User inReplyToUser, String message) {
		this.message = message;
		this.inReplyToUser = inReplyToUser;
	}

	@Override
	public Object onExecute() throws Exception {
		DirectMessage myReply = Memory.getTwitterClient().sendDirectMessage(
				inReplyToUser.getScreenName(), message);
		return myReply;
	}

	@Override
	public Object getResult() {
		return null;
	}

}
