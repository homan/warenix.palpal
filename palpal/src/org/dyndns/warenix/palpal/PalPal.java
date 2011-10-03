package org.dyndns.warenix.palpal;

import org.dyndns.warenix.palpal.social.facebook.vo.graph.Profile;

import winterwell.jtwitter.Twitter;

import com.facebook.android.Facebook;

public class PalPal {

	// palpal
	// public static final String APP_ID = "147484888643908";
	// testing
	public static final String APP_ID = "169780459723879";

	public static final String permissions[] = { "read_stream", "user_photos",
			"friends_photos", "publish_stream", "user_groups",
			"friends_groups", "user_status", "friends_status", "user_notes",
			"friends_notes", "user_likes", "friends_likes", "user_checkins",
			"friends_checkins", "offline_access"
	// test permissions
	// "user_events", "friends_events",
	// "user_photo_video_tags", "friends_photo_video_tags", "user_status",
	// "friends_status", "user_videos", "friends_videos", "user_website",
	// "friends_website", "read_mailbox"
	};

	// twitter
	public static final String JTWITTER_OAUTH_KEY = "zmSG7FZFWZadc7V73G0g";
	public static final String JTWITTER_OAUTH_SECRET = "vUGNI8MW6HQc88F11arfOWNfW7UyufrNuOEIrZF840";

	private static Facebook facebook;
	private static Twitter twitter;
	private static Profile authenticatedUserProfile;
	private static Profile currentUserProfile;

	public static Profile getAuthenticatedUserProfile() {
		return authenticatedUserProfile;
	}

	public static void setAuthenticatedUserProfile(Profile profile) {
		PalPal.authenticatedUserProfile = profile;
	}

	public static Profile getCurrentUserProfile() {
		return currentUserProfile;
	}

	public static void setCurrentUserProfile(Profile profile) {
		PalPal.currentUserProfile = profile;
	}

	public static Twitter getTwitterClient() {
		return twitter;
	}

	public static void setTwitter(Twitter newtwitter) {
		twitter = newtwitter;
	}

	public static void setFacebook(Facebook newfacebook) {
		facebook = newfacebook;
	}

	public static Facebook getFacebook() {
		return facebook;
	}
}
