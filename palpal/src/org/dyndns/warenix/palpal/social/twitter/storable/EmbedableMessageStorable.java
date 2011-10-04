package org.dyndns.warenix.palpal.social.twitter.storable;

import org.dyndns.warenix.db.SimpleStorable;
import org.dyndns.warenix.embedly.Embedable;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class EmbedableMessageStorable extends SimpleStorable {
	public static final String TYPE = "twitter_embedable_message";

	public EmbedableMessageStorable(String socialNetworkMessageId) {
		super(TYPE, getKey(socialNetworkMessageId), socialNetworkMessageId);
	}

	public static String getKey(String socialNetworkMessageId) {
		return TYPE + "_" + socialNetworkMessageId;
	}

	public EmbedableMessageStorable(String socialNetworkMessageId,
			Embedable embedable) {
		super(TYPE, getKey(socialNetworkMessageId), String.format(
				"{messageId:%s,embedable:%s}", socialNetworkMessageId,
				embedable.toString()));
		Log.d("warenix", this.toString());
	}

	public static Embedable factory(String jsonString) {
		try {
			JSONObject json = new JSONObject(jsonString);
			String messageId = json.getString("messageId");
			String embeableJSONString = json.getString("embedable");
			return new Embedable(embeableJSONString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}
}