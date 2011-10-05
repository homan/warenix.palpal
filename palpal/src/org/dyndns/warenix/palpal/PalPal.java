package org.dyndns.warenix.palpal;

import java.util.HashSet;
import java.util.Set;
import org.dyndns.warenix.palpaltwitter.R;

import twitter4j.Twitter;

public class PalPal {

	// twitter
	public static final String JTWITTER_OAUTH_KEY = "zmSG7FZFWZadc7V73G0g";
	public static final String JTWITTER_OAUTH_SECRET = "vUGNI8MW6HQc88F11arfOWNfW7UyufrNuOEIrZF840";

	private static Twitter twitter;

	public static Twitter getTwitterClient() {
		return twitter;
	}

	public static void setTwitter(Twitter newtwitter) {
		twitter = newtwitter;
	}

	public static int unreadMessageCount = 0;

	public Set<String> favouriteSet = new HashSet<String>();
}
