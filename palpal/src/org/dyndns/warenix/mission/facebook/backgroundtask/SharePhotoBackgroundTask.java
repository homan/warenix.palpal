package org.dyndns.warenix.mission.facebook.backgroundtask;

import java.util.ArrayList;

import org.dyndns.warenix.lab.taskservice.BackgroundTask;
import org.dyndns.warenix.mission.facebook.util.FacebookMaster;

public class SharePhotoBackgroundTask implements BackgroundTask {
	String graphPath;
	String message;
	ArrayList<String> imageFileList;

	public SharePhotoBackgroundTask(String graphPath, String message,
			ArrayList<String> imageFileList) {
		this.graphPath = graphPath;
		this.message = message;
		this.imageFileList = imageFileList;
	}

	@Override
	public Object onExecute() throws Exception {

		for (int i = 0; i < imageFileList.size(); ++i) {

			FacebookMaster.uploadPhotoFromFile(imageFileList.get(i), message,
					null);
		}

		return "ok";
	}

	@Override
	public Object getResult() {
		return null;
	}

}
