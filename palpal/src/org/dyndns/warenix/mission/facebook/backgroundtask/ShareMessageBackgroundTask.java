package org.dyndns.warenix.mission.facebook.backgroundtask;

import org.dyndns.warenix.lab.taskservice.BackgroundTask;
import org.dyndns.warenix.mission.facebook.util.FacebookMaster;

public class ShareMessageBackgroundTask implements BackgroundTask {
	String graphPath;
	String message;

	public ShareMessageBackgroundTask(String graphPath, String message) {
		this.graphPath = graphPath;
		this.message = message;
	}

	@Override
	public Object onExecute() throws Exception {
		boolean response = FacebookMaster.post(graphPath, message, "", "", "",
				"", "", "");
		return response;
	}

	@Override
	public Object getResult() {
		return null;
	}

}
