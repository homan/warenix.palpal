package org.dyndns.warenix.palpal.social.facebook;

import org.dyndns.warenix.palpal.social.facebook.util.FacebookUtil;
import org.json.JSONObject;

public class Like {
	public String id;
	public String name;

	public Like() {

	}

	public Like(JSONObject post) {
		id = FacebookUtil.getJSONString(post, "id", "");
		name = FacebookUtil.getJSONString(post, "name", "");
	}

	public String toString() {
		return String.format("id:%s name:%s", id, name);
	}
}
