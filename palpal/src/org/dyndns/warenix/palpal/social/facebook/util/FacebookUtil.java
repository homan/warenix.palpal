package org.dyndns.warenix.palpal.social.facebook.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.dyndns.warenix.palpal.social.facebook.FacebookException;
import org.dyndns.warenix.palpal.social.facebook.FacebookPostTypeResult;
import org.dyndns.warenix.palpal.social.facebook.Like;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.NoteFeed;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.Notification;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.PhotoFeed;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;

public class FacebookUtil {
	public static String formatFacebookTimeToUserTimeZone(String postDate,
			int myTimezone) {
		myTimezone *= 3600000;
		// String postDate = "2010-12-29T08:27:03+0000";
		postDate = postDate.replace('T', ' ');
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
		Date date = null;
		try {
			date = sdf.parse(postDate);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		String availableIDs[] = TimeZone.getAvailableIDs(myTimezone);
		if (availableIDs.length > 0) {
			TimeZone timeZone = TimeZone.getTimeZone(availableIDs[0]);
			sdf.setTimeZone(timeZone);

			SimpleDateFormat userSDF = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			userSDF.setTimeZone(timeZone);

			return userSDF.format(date);
		}
		return postDate;
	}

	public static String formatFacebookTimeToLocaleString(String postDate) {
		// String postDate = "2010-12-29T08:27:03+0000";
		if (postDate == null) {
			return new Date().toLocaleString();
		}
		postDate = postDate.replace('T', ' ');
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
		Date date = null;
		try {
			date = sdf.parse(postDate);
			return date.toLocaleString();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		return postDate;
	}

	public static String formatDateToFacebookTime(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
		String dateString = sdf.format(date);
		return dateString.replace(' ', 'T');
	}

	public static String getJSONString(final JSONObject json,
			final String name, final String defaultValue) {
		try {
			return json.getString(name);
		} catch (JSONException e) {
		}
		return defaultValue;
	}

	public static JSONObject getJSONObject(final JSONObject json,
			final String name, final JSONObject defaultValue) {
		try {
			return json.getJSONObject(name);
		} catch (JSONException e) {
		}
		return defaultValue;
	}

	public static JSONArray getJSONArray(final JSONObject json,
			final String name, final JSONArray defaultValue) {
		try {
			return json.getJSONArray(name);
		} catch (JSONException e) {
		}
		return defaultValue;
	}

	public static Object getJSONArrayAtIndex(final JSONArray json,
			final int index, final JSONObject defaultValue) {
		try {
			return json.get(index);
		} catch (JSONException e) {
		}
		return defaultValue;
	}

	public static class Factory {
		public static ArrayList<Like> factoryLikeList(String dataString) {
			ArrayList<Like> likeList = new ArrayList<Like>();

			JSONObject json;
			try {
				json = new JSONObject(dataString);
				JSONArray data = json.getJSONArray("data");

				JSONObject likeJSON = null;
				for (int i = 0; i < data.length(); ++i) {

					likeJSON = (JSONObject) getJSONArrayAtIndex(data, i, null);

					Like like = new Like(likeJSON);
					likeList.add(like);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return likeList;
		}
	}

	public static String USER_PROFILE_IMAGE_LARGE = "large";
	public static String USER_PROFILE_IMAGE_NORMAL = "normal";
	public static String USER_PROFILE_IMAGE_SMALL = "small";
	/**
	 * 50x50
	 */
	public static String USER_PROFILE_IMAGE_SQUARE = "square";

	public static String getUserProfileImage(String userId, String type) {
		return String.format("http://graph.facebook.com/%s/picture?type=%s",
				userId, type);
	}

	public static String getAlbumIdFromPhotoLink(String link) {
		URL linkURL;
		try {
			linkURL = new URL(link);
			String query = linkURL.getQuery();
			String params[] = query.split("&");
			String pair[] = null;

			for (int i = 0; i < params.length; ++i) {
				pair = params[i].split("=");
				if (pair[0].equals("set")) {
					return (pair[1].split("\\."))[1];
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getFacebookURLFromCommentId(String commentId) {
		String parts[] = commentId.split("_");
		return String.format("http://www.facebook.com/%s/posts/%s", parts[0],
				parts[1]);
	}

	/**
	 * construct a bundle to be sent to facebook request for fetching the given
	 * url
	 * 
	 * @param url
	 *            the url being fetched
	 * @return
	 */
	public static Bundle getFetchPageBundleFromURL(String urlString) {
		Bundle parameters = new Bundle();
		try {
			URL url = new URL(urlString);

			String queryString = url.getQuery();
			String params[] = queryString.split("&");
			String pair[] = null;

			String paramName = "";
			String paramValue = "";
			for (int i = 0; i < params.length; ++i) {
				pair = params[i].split("=");
				paramName = pair[0];
				if (paramName.equals("access_token")
						|| paramName.equals("format")
						|| paramName.equals("offset")) {
					// these parameters are already provided by facebook object
					continue;
				}

				paramValue = URLDecoder.decode(pair[1]);
				parameters.putString(paramName, paramValue);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return parameters;
	}

	public static String getParameterValueByName(String link, String paramName) {
		try {
			URL url = new URL(link);

			String queryString = url.getQuery();
			String parameters[] = queryString.split("&");
			for (int i = 0; i < parameters.length; ++i) {
				String parts[] = parameters[i].split("=");
				if (paramName.equals(parts[0])) {
					return parts[1];
				}
			}

		} catch (MalformedURLException e) {
		}

		return null;

	}

	public static String getLargeImage(String picture) {
		if (picture != null) {
			if (picture.endsWith("_s.jpg")) {
				// hack to get normal size picture instead of small size
				picture = picture.replace("_s.jpg", "_n.jpg");
			} else if (picture.contains("app_full_proxy.php")) {
				String largePicture = FacebookUtil.getParameterValueByName(
						picture, "src");
				picture = URLDecoder.decode(largePicture);
			} else if (picture.contains("safe_image.php")
			// can't get image from yimg directly
					&& picture.contains("yimg.com") == false) {
				String largePicture = FacebookUtil.getParameterValueByName(
						picture, "url");
				picture = URLDecoder.decode(largePicture);
			}
		}

		return picture;
	}

	/**
	 * determine facebook post type from a link
	 * 
	 * @param href
	 * @return null if can't be determine, probably can be determine by fetching
	 *         the post id
	 */
	public static FacebookPostTypeResult determineFacebookPostType(
			Notification notification) {

		FacebookPostTypeResult result = new FacebookPostTypeResult();
		String href = notification.href;
		Log.d("palpal", String.format("determine postid from href %s", href));

		if (href.contains("photo.php")) {
			result.type = PhotoFeed.TYPE;

			String postId = FacebookUtil.getParameterValueByName(href, "fbid");
			result.postId = postId;
			return result;
		} else if (href.contains("posts/")) {
			// unknown
			result.type = null;
			String[] tokens = href.split("posts/");
			result.postId = tokens[1];
			// result.postId = notification.objectId != null ?
			// notification.objectId
			// : tokens[1];
			return result;
		} else if (href.contains("notes/")) {
			// unknown
			result.type = NoteFeed.TYPE;
			String[] tokens = href.split("/");
			result.postId = notification.objectId != null ? notification.objectId
					: tokens[tokens.length - 1];
			return result;
		} else if (href.contains("sk=group")) {
			String groupString = FacebookUtil.getParameterValueByName(href,
					"sk");
			String groupId = groupString.split("_")[1];
			result.postId = notification.objectId != null ? notification.objectId
					: groupId;
			result.type = "group";
			return result;
		} else if (href.contains("story_fbid")) {
			String postId = FacebookUtil.getParameterValueByName(href,
					"story_fbid");
			result.postId = postId;
			result.type = null;
			return result;
		}

		return null;
	}

	/**
	 * given a json resposne string, check if the string contains error
	 */
	public static void checkFacebookException(String responseString)
			throws FacebookException {
		JSONObject json = null;
		try {
			json = new JSONObject(responseString);
			JSONObject errorJSON = json.getJSONObject("error");
			String errorType = errorJSON.getString("type");
			String errorMessage = errorJSON.getString("message");
			throw new FacebookException(errorType, errorMessage);
		} catch (JSONException e) {
			try {
				json = new JSONObject(responseString);
				String errorType = json.getString("error_code");
				String errorMessage = json.getString("error_msg");
				throw new FacebookException(errorType, errorMessage);
			} catch (JSONException e1) {
			}
		}
	}
}
