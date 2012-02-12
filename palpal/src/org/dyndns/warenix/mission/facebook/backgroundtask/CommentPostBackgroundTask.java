package org.dyndns.warenix.mission.facebook.backgroundtask;

import org.dyndns.warenix.lab.taskservice.BackgroundTask;
import org.dyndns.warenix.mission.facebook.util.FacebookMaster;

public class CommentPostBackgroundTask implements BackgroundTask {
	String mPostId;
	String mComment;

	public CommentPostBackgroundTask(String postId, String comment) {
		mPostId = postId;
		mComment = comment;
	}

	@Override
	public Object onExecute() throws Exception {
		boolean response = FacebookMaster.addComment(mPostId, mComment);
		return response;
	}

	@Override
	public Object getResult() {
		return null;
	}

}
