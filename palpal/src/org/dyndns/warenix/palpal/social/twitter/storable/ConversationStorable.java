package org.dyndns.warenix.palpal.social.twitter.storable;

import org.dyndns.warenix.db.SimpleStorable;

public class ConversationStorable extends SimpleStorable {
	public static final String TYPE = "twitter_conversation";

	public ConversationStorable(String socialNetworkMessageId) {
		super(TYPE, getKey(socialNetworkMessageId), socialNetworkMessageId);
	}

	public static String getKey(String socialNetworkMessageId) {
		return TYPE + "_" + socialNetworkMessageId;
	}

}