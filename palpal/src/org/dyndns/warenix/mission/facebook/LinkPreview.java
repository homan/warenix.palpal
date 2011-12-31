package org.dyndns.warenix.mission.facebook;

import java.io.Serializable;
import java.net.URLDecoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("serial")
public class LinkPreview implements Serializable {
	public String name = "";
	public String caption = "";
	public String link = "";
	public String description = "";
	public String picture = "";
	public String source = "";

	public ArrayList<String> previewImageList;

	public LinkPreview(String jsonString) {
		JSONObject json;
		try {
			json = new JSONObject(jsonString);

			try {
				name = json.getString("name");
			} catch (JSONException e) {
			}
			try {
				caption = json.getString("caption");
			} catch (JSONException e) {
			}
			try {
				link = json.getString("href");
			} catch (JSONException e) {
			}
			try {
				description = json.getString("description");
			} catch (JSONException e) {
			}
			try {
				source = json.getString("source");
			} catch (JSONException e) {
			}

			JSONArray mediaJSONArray = json.getJSONArray("media");

			previewImageList = new ArrayList<String>();
			for (int i = 0; i < mediaJSONArray.length(); ++i) {
				JSONObject previewJSON = mediaJSONArray.getJSONObject(i);

				previewImageList.add(URLDecoder.decode(previewJSON
						.getString("src")));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}