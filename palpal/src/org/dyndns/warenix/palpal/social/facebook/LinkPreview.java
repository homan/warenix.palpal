package org.dyndns.warenix.palpal.social.facebook;

import java.util.ArrayList;

import org.dyndns.warenix.palpal.social.facebook.util.FacebookUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LinkPreview {
	public String href;
	public String name;
	public String caption;
	public String description;
	public ArrayList<String> imageList;

	public String selectedImageURL;

	public LinkPreview(JSONObject json) {
		href = FacebookUtil.getJSONString(json, "href", null);
		name = FacebookUtil.getJSONString(json, "name", null);
		caption = FacebookUtil.getJSONString(json, "caption", null);
		description = FacebookUtil.getJSONString(json, "description", null);

		try {
			JSONArray media = json.getJSONArray("media");
			JSONObject imageJSON;

			imageList = new ArrayList<String>();
			for (int i = 0; i < media.length(); ++i) {
				imageJSON = media.getJSONObject(i);
				String url = FacebookUtil.getJSONString(imageJSON, "src", null);
				if (url != null) {
					imageList.add(url);
				}
			}
		} catch (JSONException e) {
		}

	}
}
