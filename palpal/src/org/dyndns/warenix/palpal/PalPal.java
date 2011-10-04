package org.dyndns.warenix.palpal;

import java.util.HashSet;
import java.util.Set;
import org.dyndns.warenix.palpaltwitter.R;

import twitter4j.Twitter;

public class PalPal {

	// twitter
	public static final String JTWITTER_OAUTH_KEY = "";
	public static final String JTWITTER_OAUTH_SECRET = "";

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
