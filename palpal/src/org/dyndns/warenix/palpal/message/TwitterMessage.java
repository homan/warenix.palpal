package org.dyndns.warenix.palpal.message;

import java.io.Serializable;

import org.dyndns.warenix.palpal.PalPal;
import org.dyndns.warenix.palpal.service.SendMessageService.SendableMessage;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * send to UpdateMessageService
 * 
 * @author warenix
 * 
 */
public class TwitterMessage implements SendableMessage, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	StatusUpdate statusUpdate;

	public TwitterMessage(StatusUpdate statusUpdate) {
		this.statusUpdate = statusUpdate;
	}

	@Override
	public boolean send() {
		Twitter twitter = PalPal.getTwitterClient();
		if (twitter == null) {
			return false;
		}

		try {
			twitter.updateStatus(statusUpdate);
		} catch (TwitterException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}