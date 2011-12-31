package org.dyndns.warenix.mission.facebook.backgroundtask;

import org.dyndns.warenix.lab.taskservice.BackgroundTask;
import org.dyndns.warenix.mission.facebook.LinkPreview;
import org.dyndns.warenix.mission.facebook.util.FacebookMaster;

public class LinkPreviewBackgroundTask implements BackgroundTask {
	String link;

	public LinkPreviewBackgroundTask(String link) {
		this.link = link;
	}

	LinkPreview linkPreview;

	@Override
	public Object onExecute() throws Exception {

		String response = FacebookMaster.getLinkpreview(link);

		linkPreview = new LinkPreview(response);
		return linkPreview;
	}

	@Override
	public Object getResult() {
		return linkPreview;
	}

	public String toString() {
		return "LinkPreviewBackgroundTask " + link;
	}

}