package org.dyndns.warenix.mission.facebook.backgroundtask;

import org.dyndns.warenix.lab.taskservice.BackgroundTask;
import org.dyndns.warenix.mission.facebook.util.FacebookMaster;

import android.content.Context;

public class LikePostBackgroundTask implements BackgroundTask {
	String mPostId;

	public LikePostBackgroundTask(Context context, String postId) {
		mPostId = postId;
	}

	@Override
	public Object onExecute() throws Exception {
		boolean response = FacebookMaster.addLike(mPostId);
		return response;
	}

	@Override
	public Object getResult() {
		return null;
	}

}
