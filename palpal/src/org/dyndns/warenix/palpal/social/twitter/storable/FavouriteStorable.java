package org.dyndns.warenix.palpal.social.twitter.storable;

import org.dyndns.warenix.db.SimpleStorable;

public class FavouriteStorable extends SimpleStorable {
	public static final String TYPE = "twitter_favourite";

	public FavouriteStorable(String socialNetworkMessageId) {
		super(TYPE, getKey(socialNetworkMessageId), socialNetworkMessageId);
	}

	public static String getKey(String socialNetworkMessageId) {
		return TYPE + "_" + socialNetworkMessageId;
	}

}