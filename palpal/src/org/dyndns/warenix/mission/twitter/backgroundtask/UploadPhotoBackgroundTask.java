package org.dyndns.warenix.mission.twitter.backgroundtask;

import java.util.ArrayList;

import org.dyndns.warenix.lab.taskservice.BackgroundTask;
import org.dyndns.warenix.mission.twitter.util.TwitterMaster;
import org.dyndns.warenix.util.WLog;

public class UploadPhotoBackgroundTask implements BackgroundTask {
	String message;
	ArrayList<String> imageFileList;

	public UploadPhotoBackgroundTask(String message,
			ArrayList<String> imageFileList) {
		this.message = message;
		this.imageFileList = imageFileList;
	}

	@Override
	public Object onExecute() throws Exception {

		for (int i = 0; i < imageFileList.size(); ++i) {

			// FacebookMaster.uploadPhotoFromFile(imageFileList.get(i), message,
			// null);
			//

			String url = TwitterMaster.uploadPhotoFromFileToTwitter(
					imageFileList.get(i), message);
			WLog.i("palpal", "uploaded photo to " + url);
			Thread.sleep(500);
		}

		return "ok";
	}

	@Override
	public Object getResult() {
		return null;
	}

}