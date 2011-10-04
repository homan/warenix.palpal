package org.dyndns.warenix.palpal.social.twitter.storable;

import org.dyndns.warenix.db.SimpleStorable;

public class FriendStorable extends SimpleStorable {
	public static final String TYPE = "twitter_friend";

	public FriendStorable(String username) {
		super(TYPE, getKey(username), username);
	}

	public static String getKey(String username) {
		return TYPE + "_" + username;
	}

}