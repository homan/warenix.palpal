package org.dyndns.warenix.tagdef;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class HashTagDef {
	/**
	 * text description of the hashtag
	 */
	String text;

	/**
	 * the hashtag without # sign
	 */
	String hashtag;
	/**
	 * HashTag added time
	 */
	Date time;

	public static HashTagDef factory(String responseJSON) {
		try {
			JSONObject json = new JSONObject(responseJSON);
			JSONObject def = json.getJSONObject("defs").getJSONObject("def");

			HashTagDef hashtagDef = new HashTagDef();
			hashtagDef.text = def.getString("text");
			String timeString = def.getString("time");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			hashtagDef.time = sdf.parse(timeString);
			hashtagDef.hashtag = def.getString("hashtag");

			return hashtagDef;

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String toString() {
		return String.format("%s [%s] added on [%s]", hashtag, text, time);

	}
}
