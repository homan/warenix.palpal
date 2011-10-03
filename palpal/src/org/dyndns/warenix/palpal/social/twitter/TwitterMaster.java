package org.dyndns.warenix.palpal.social.twitter;

import java.math.BigInteger;
import java.util.ArrayList;

import org.dyndns.warenix.palpal.PalPal;
import org.dyndns.warenix.palpal.social.twitter.task.LoadConversationAsyncTask;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.Status;
import winterwell.jtwitter.TwitterException;
import android.util.Log;

public class TwitterMaster {
	public static class status {
		/**
		 * return complete conversation for a given status, including the status
		 * 
		 * @param status
		 * @return
		 */
		public static ArrayList<Status> getConvsersation(Status status,
				LoadConversationAsyncTask.LoadConversationListener listener) {
			ArrayList<Status> statusList = new ArrayList<Status>();
			statusList.add(status);
			listener.onConversationLoaded(status);

			Twitter twitter = PalPal.getTwitterClient();
			BigInteger inReplyToStatusId = status.inReplyToStatusId;
			while (inReplyToStatusId != null) {
				try {
					status = twitter.getStatus(inReplyToStatusId);
					statusList.add(status);
					inReplyToStatusId = status.inReplyToStatusId;

					listener.onConversationLoaded(status);
				} catch (TwitterException e) {
					Log.d("palpal",
							String.format("error on getting conversation %s",
									e.getMessage()));
					break;
				}
			}

			return statusList;
		}
		// BigInteger statusId = new BigInteger("");
		// Twitter twitter = PalPal.getTwitterClient();
		// Status status = twitter.getStatus(statusId);
		// ArrayList<Status> statusList = TwitterMaster.status
		// .getConvsersation(status);
		// for (Status status1 : statusList) {
		// Log.d("palpal", status1.toString());
		// }
	}
}
