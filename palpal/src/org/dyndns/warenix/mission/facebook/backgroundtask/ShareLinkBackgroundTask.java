package org.dyndns.warenix.mission.facebook.backgroundtask;

import org.dyndns.warenix.lab.taskservice.BackgroundTask;
import org.dyndns.warenix.mission.facebook.LinkPreview;
import org.dyndns.warenix.mission.facebook.util.FacebookMaster;

public class ShareLinkBackgroundTask implements BackgroundTask {
	String graphPath;
	String message;
	LinkPreview linkPreview;

	public ShareLinkBackgroundTask(String graphPath, String message,
			LinkPreview linkPreview) {
		this.graphPath = graphPath;
		this.message = message;
		this.linkPreview = linkPreview;
	}

	@Override
	public Object onExecute() throws Exception {
		boolean response = FacebookMaster.post(graphPath, message,
				linkPreview.picture, linkPreview.link, linkPreview.name,
				linkPreview.caption, linkPreview.description,
				linkPreview.source);
		return response;
	}

	@Override
	public Object getResult() {
		return null;
	}

}
