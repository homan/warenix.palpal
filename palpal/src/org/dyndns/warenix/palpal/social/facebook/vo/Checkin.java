package org.dyndns.warenix.palpal.social.facebook.vo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Checkin {
	public static int LATITUDE = 0;
	public static int LONGITUDE = 1;

	public double coords[];
	public String author_uid;
	public String page_id;
	public String post_id;
	public long timestamp;
	public String message;
	public ArrayList<String> tagged_uid_list;

	public Checkin(String jsonString) {
		try {
			JSONObject json = new JSONObject(jsonString);

			JSONObject coordsJSON = json.getJSONObject("coords");
			coords = new double[2];
			coords[LATITUDE] = coordsJSON.getDouble("latitude") * 1e6;
			coords[LONGITUDE] = coordsJSON.getDouble("longitude") * 1e6;

			try {
				JSONArray tagged_uids = json.getJSONArray("tagged_uids");
				tagged_uid_list = new ArrayList<String>();
				for (int i = 0; i < tagged_uids.length(); ++i) {
					tagged_uid_list.add(tagged_uids.getString(i));
				}
			} catch (JSONException e) {

			}

			author_uid = json.getString("author_uid");
			post_id = json.getString("post_id");
			page_id = json.getString("page_id");
			timestamp = json.getLong("timestamp");
			message = json.getString("message");
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
