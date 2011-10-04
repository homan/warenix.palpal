package org.dyndns.warenix.embedly;

import org.json.JSONException;
import org.json.JSONObject;

public class Embedable {
	public Embedable(String jsonString, String url, String title,
			String description) {
		this.jsonString = jsonString;

		this.url = url;
		this.title = title;
		this.description = description;
	}

	public String jsonString;
	public String providerdUrl;
	public String description;
	public String title;
	public String url;
	public String authorName;
	public int height;
	public int width;
	public String thumbnailUrl;
	public int thumbnailWidth;
	public int thumbnailHeight;
	public String version;
	public String providerName;
	public String type;
	public String authorUrl;

	public static String PROVIDER_URL = "provider_url";
	public static String DESCRIPTION = "description";
	public static String TITLE = "title";
	public static String URL = "url";
	public static String AUTHOR_NAME = "author_name";
	public static String HEIGHT = "height";
	public static String WIDTH = "width";
	public static String THUMBNAIL_URL = "thumbnail_url";
	public static String THUMBNAIL_WIDTH = "thumbnail_width";
	public static String VERSION = "version";
	public static String PROVIDER_NAME = "provider_name";
	public static String TYPE = "type";
	public static String THUMBNAIL_HEIGHT = "thumbnail_height";
	public static String AUTHOR_URL = "author_url";

	public static String TYPE_PHOTO = "photo";

	public String toString() {
		return jsonString;
	}

	public Embedable(String jsonString) throws JSONException {

		JSONObject json;
		json = new JSONObject(jsonString);

		this.jsonString = jsonString;
		type = json.getString(TYPE);
		url = json.getString(URL);
		try {
			title = json.getString(TITLE);
		} catch (JSONException e) {
		}
		try {
			description = json.getString(DESCRIPTION);
		} catch (JSONException e) {
		}
		try {
			thumbnailUrl = json.getString(THUMBNAIL_URL);
		} catch (JSONException e) {
		}
		try {
			authorName = json.getString(AUTHOR_NAME);
		} catch (JSONException e) {
		}
	}

	public Embedable(String type, String url, String title, String description,
			String authorName) {
		JSONObject json = new JSONObject();
		try {
			json.put(TYPE, type);
			json.put(URL, url);
			json.put(TITLE, title);
			json.put(DESCRIPTION, description);
			json.put(THUMBNAIL_URL, thumbnailUrl);
			json.put(AUTHOR_NAME, authorName);

			this.jsonString = json.toString();
			this.type = type;
			this.url = url;
			this.description = description;
			this.authorName = authorName;

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
