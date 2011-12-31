package org.dyndns.warenix.mission.facebook;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.dyndns.warenix.util.DateUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FacebookObject implements Serializable {

	public String id;
	public String message;
	public String story;
	public String title;
	public String type;
	public User fromUser;
	public ArrayList<User> toUserList;
	public ArrayList<Action> actionList;
	public ArrayList<Comment> commentsList;
	public ArrayList<Like> likesList;
	public ArrayList<StoryTag> storyTagList;
	public long commentTotalCount;
	public long likeTotalCount;
	public Date created_time;
	public Date updated_time;
	public String name;
	public String caption;
	public String description;
	public String picture;
	public String link;
	public long unread;

	public Place place;

	public Error error;

	public static class User {
		public String name;
		public String id;
	}

	public static class Action {
		public String name;
		public String id;
	}

	public static class Privacy {
		public String description;
		public String value;
		public String allow;
		public String deny;
	}

	public static class Application {
		public String name;
		public String id;
	}

	public static class Comment {
		public String id;
		public User fromUser;
		public String message;
		public Date created_time;
		public long likeTotalCount;
	}

	public static class Like {
		public String id;
		public String name;
	}

	public static class Place {
		public String id;
		public String name;
		public Location location;
	}

	public static class Location {
		public String city;
		public String country;
		public String latitude;
		public String longitude;
		public String zip;
	}

	public static class StoryTag {
		public String id;
		public String name;
		public long offset;
		public long length;
	}

	public static class Error {
		public String message;
		public String type;
	}

	public FacebookObject(JSONObject jsonObject) {
		try {
			try {
				JSONObject errorJson = jsonObject.getJSONObject("error");
				error = new Error();
				error.message = errorJson.getString("message");
				error.type = errorJson.getString("message");
			} catch (JSONException e) {

			}
			setFromUser(jsonObject);

			id = jsonObject.getString("id");
			try {
				message = jsonObject.getString("message");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				story = jsonObject.getString("story");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				title = jsonObject.getString("title");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				type = jsonObject.getString("type");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				String created_timeString = jsonObject
						.getString("created_time");
				created_time = DateUtil.parseISODate(created_timeString);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				String updated_timeString = jsonObject
						.getString("updated_time");
				try {
					updated_time = DateUtil.parseISODate(updated_timeString);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} catch (JSONException e) {
				updated_time = created_time;
				e.printStackTrace();
			}

			// Post object may not always have created_time
			if (created_time == null) {
				created_time = updated_time;
			}
			try {
				name = jsonObject.getString("name");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			try {
				caption = jsonObject.getString("caption");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			try {
				description = jsonObject.getString("description");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				picture = jsonObject.getString("picture");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				link = jsonObject.getString("link");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			try {
				setComment(jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				setLike(jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			try {
				unread = jsonObject.getLong("unread");
			} catch (JSONException e) {
			}
			try {
				setPlace(jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			try {
				setToUserList(jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			try {
				setStoryTagsList(jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setFromUser(JSONObject jsonObject) throws JSONException {
		fromUser = getUser(jsonObject);
	}

	protected User getUser(JSONObject jsonObject) throws JSONException {
		try {
			JSONObject fromJSON = jsonObject.getJSONObject("from");
			User user = new User();

			user.name = fromJSON.getString("name");
			user.id = fromJSON.getString("id");
			return user;
		} catch (JSONException e) {
			User user = new User();
			user.name = jsonObject.getString("name");
			user.id = jsonObject.getString("id");
			return user;
		}
	}

	public void setComment(JSONObject jsonObject) throws JSONException {
		jsonObject = jsonObject.getJSONObject("comments");

		JSONArray dataArray = jsonObject.getJSONArray("data");
		int l = dataArray.length();
		if (l > 0) {
			commentsList = new ArrayList<Comment>();
			for (int i = 0; i < l; ++i) {
				JSONObject commentJSON = dataArray.getJSONObject(i);
				Comment comment = new Comment();
				comment.fromUser = getUser(commentJSON);
				comment.id = commentJSON.getString("id");
				comment.message = commentJSON.getString("message");

				try {
					comment.created_time = DateUtil.parseISODate(commentJSON
							.getString("created_time"));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				try {
					comment.likeTotalCount = commentJSON.getLong("likes");
				} catch (JSONException e) {
					comment.likeTotalCount = 0;
				}

				commentsList.add(comment);
			}
		}

		try {
			commentTotalCount = jsonObject.getLong("count");
		} catch (JSONException e) {
			commentTotalCount = l;
		}
	}

	public void setPlace(JSONObject jsonObject) throws JSONException {
		JSONObject placeJSON = jsonObject.getJSONObject("place");

		place = new Place();
		place.id = placeJSON.getString("id");
		place.name = placeJSON.getString("name");
		place.location = getLocation(placeJSON);
	}

	public void setLike(JSONObject jsonObject) throws JSONException {
		jsonObject = jsonObject.getJSONObject("likes");

		JSONArray dataArray = jsonObject.getJSONArray("data");
		int l = dataArray.length();
		if (l > 0) {
			likesList = new ArrayList<Like>();
			for (int i = 0; i < l; ++i) {
				JSONObject likeJSON = dataArray.getJSONObject(i);
				Like like = new Like();
				like.id = likeJSON.getString("id");
				like.name = likeJSON.getString("name");
			}
		}

		try {
			likeTotalCount = jsonObject.getLong("count");
		} catch (JSONException e) {
			likeTotalCount = l;
		}
	}

	public Location getLocation(JSONObject jsonObject) throws JSONException {
		JSONObject locationJSON = jsonObject.getJSONObject("location");
		Location location = new Location();
		try {
			location.city = locationJSON.getString("city");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			location.country = locationJSON.getString("country");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			location.zip = locationJSON.getString("zip");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			location.latitude = locationJSON.getString("latitude");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			location.longitude = locationJSON.getString("longitude");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return location;
	}

	public void setPrivacy(JSONObject jsonObject) throws JSONException {
		JSONObject privacyJSON = jsonObject.getJSONObject("privacy");

		Privacy privacy = new Privacy();
		privacy.description = privacyJSON.getString("description");
		privacy.value = privacyJSON.getString("value");
		privacy.allow = privacyJSON.getString("allow");
		privacy.deny = privacyJSON.getString("deny");
	}

	public void setToUserList(JSONObject jsonObject) throws JSONException {
		jsonObject = jsonObject.getJSONObject("to");
		JSONArray dataJSONArray = jsonObject.getJSONArray("data");

		int l = dataJSONArray.length();
		if (l > 0) {
			toUserList = new ArrayList<User>();
			for (int i = 0; i < l; ++i) {
				toUserList.add(getUser(dataJSONArray.getJSONObject(i)));
			}
		}
	}

	/**
	 * parse 'story_tags' fields from post json. The story tag will discard the
	 * post user himself/ herself
	 * 
	 * @param jsonObject
	 * @throws JSONException
	 */
	public void setStoryTagsList(JSONObject jsonObject) throws JSONException {
		JSONObject storyTagsJSONObject = jsonObject.getJSONObject("story_tags");

		JSONArray namesJSONArray = storyTagsJSONObject.names();
		for (int i = 0; i < namesJSONArray.length(); ++i) {
			String name = namesJSONArray.getString(i);
			JSONArray storyTagJSONArray = storyTagsJSONObject
					.getJSONArray(name);

			JSONObject storyTagJSON = storyTagJSONArray.getJSONObject(0);
			StoryTag storyTag = new StoryTag();
			storyTag.id = storyTagJSON.getString("id");
			if (!storyTag.id.equals(fromUser.id)) {
				storyTag.name = storyTagJSON.getString("name");
				storyTag.offset = storyTagJSON.getLong("offset");
				storyTag.length = storyTagJSON.getLong("length");

				if (storyTagList == null) {
					storyTagList = new ArrayList<StoryTag>();
				}
				storyTagList.add(storyTag);
			}
		}
	}
}