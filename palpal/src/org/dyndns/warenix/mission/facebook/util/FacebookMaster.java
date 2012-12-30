package org.dyndns.warenix.mission.facebook.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.lab.compat1.util.Memory;
import org.dyndns.warenix.lab.compat1.util.PreferenceMaster;
import org.dyndns.warenix.mission.facebook.FacebookObject;
import org.dyndns.warenix.util.WLog;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.facebook.android.Facebook;

public class FacebookMaster {
	private static final String TAG = "FacebookMaster";
	public static final String PREF_NAME = "facebook_pref";
	public static final String ACCESS_TOKEN = "access_token";
	public static final String ACCESS_EXPIRES = "access_expires";

	public static boolean restoreFacebook(Context context) {
		Facebook facebook = new Facebook(
				context.getString(R.string.FACEBOOK_API_KEY));
		/*
		 * Get existing access_token if any
		 */
		String access_token = PreferenceMaster.load(context, PREF_NAME,
				ACCESS_TOKEN, null);
		long expires = PreferenceMaster.load(context, PREF_NAME,
				ACCESS_EXPIRES, -1);

		if (access_token != null) {
			facebook.setAccessToken(access_token);
		}
		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}

		if (facebook.isSessionValid()) {
			Memory.setFacebookClient(facebook);
			Memory.setAccessToken(access_token);
			return true;
		}
		return false;
	}

	public static void removeFacebook(Context context) {
		PreferenceMaster.save(context, FacebookMaster.PREF_NAME,
				FacebookMaster.ACCESS_TOKEN, null);
		PreferenceMaster.save(context, FacebookMaster.PREF_NAME,
				FacebookMaster.ACCESS_EXPIRES, -1);
	}

	public static String getLargeImage(String picture) {
		if (picture != null) {
			if (picture.contains("app_full_proxy.php")) {
				String largePicture = getParameterValueByName(picture, "src");
				picture = URLDecoder.decode(largePicture);
			} else if (picture.contains("safe_image.php")
			// can't get image from yimg directly
			) {
				String largePicture = getParameterValueByName(picture, "url");
				picture = URLDecoder.decode(largePicture);
			}
		}
		if (picture.endsWith("_s.jpg")) {
			// hack to get normal size picture instead of small size
			picture = picture.replace("_s.jpg", "_n.jpg");
		}
		// youtube preview image
		// default.jpg is 120x90
		// 0.jpg is 480x360
		else if (picture.contains("ytimg.com")) {
			if (picture.endsWith("hqdefault.jpg")) {
				picture = picture.replace("hqdefault.jpg", "0.jpg");
			} else if (picture.endsWith("mqdefault.jpg")) {
				picture = picture.replace("mqdefault.jpg", "0.jpg");
			} else if (picture.endsWith("default.jpg")) {
				picture = picture.replace("default.jpg", "0.jpg");
			}
		}

		return picture;
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

	/**
	 * logic to determine the correct facebook graph id of a post
	 * 
	 * @param messageObject
	 * @return
	 */
	public static String determineGraphId(FacebookObject messageObject) {
		if (messageObject.link != null) {
			if (messageObject.link
					.startsWith("http://www.facebook.com/photo.php")) {
				// get the photo graph id
				return getParameterValueByName(messageObject.link, "fbid");
			} else if (messageObject.link
					.startsWith("http://www.facebook.com/groups")) {
				// get the group message
				String groupMessageId = messageObject.link.substring(
						"http://www.facebook.com/groups".length() + 1,
						messageObject.link.length() - 1);
				if (groupMessageId.contains("/")) {
					return groupMessageId.replace('/', '_');
				} else {
					// a link to the group profile
					// return groupMessageId + "/feed";
					return groupMessageId;
				}
			} else if (messageObject.link.contains("/posts/")) {

				// if (messageObject.message == null) {
				// // get the postId
				// return messageObject.link.substring(messageObject.link
				// .lastIndexOf('/') + 1);
				// } else {
				// get userId_postId
				int startPos = "http://www.facebook.com/".length();
				String username = messageObject.link.substring(startPos,
						messageObject.link.indexOf('/', startPos));
				Facebook facebook = Memory.getFacebookClient();
				Bundle params = new Bundle();
				params.putString("fields", "id");
				try {
					String responseString = facebook.request(username, params);
					JSONObject json = new JSONObject(responseString);
					String userId = json.getString("id");
					return userId
							+ "_"
							+ messageObject.link.substring(messageObject.link
									.lastIndexOf('/') + 1);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
					// }
				}
			} else if (messageObject.link
					.startsWith("http://www.facebook.com/permalink.php")) {
				String userId = getParameterValueByName(messageObject.link,
						"id");
				String story_fbid = getParameterValueByName(messageObject.link,
						"story_fbid");
				return userId + "_" + story_fbid;
			} else if (messageObject.link
					.startsWith("http://www.facebook.com/album.php")) {
				return getParameterValueByName(messageObject.link, "fbid");
			} else if (messageObject.link
					.startsWith("http://www.facebook.com/event.php")) {
				return getParameterValueByName(messageObject.link, "eid");
			}
		}
		return messageObject.id;
	}

	public static String determineAlbumGraphIdFromLink(
			FacebookObject messageObject) {
		String link = messageObject.link;
		if (link != null) {
			if (messageObject.link
					.startsWith("http://www.facebook.com/photo.php")) {
				// get the photo graph id
				String set = getParameterValueByName(messageObject.link, "set");
				return set.split("\\.")[1];
			} else if (messageObject.link
					.startsWith("http://www.facebook.com/album.php")) {
				return getParameterValueByName(messageObject.link, "fbid");
			} else {
				String lastPath = Uri.parse(link).getLastPathSegment();
				String[] toks = lastPath.split("_");
				if (toks.length >= 4) {
					return toks[1];
				}
			}
		}
		return null;

	}

	public static boolean post(String graphPath, String message,
			String picture, String link, String name, String caption,
			String description, String source) throws Exception {
		String url = String.format("https://graph.facebook.com/%s", graphPath);

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("access_token", Memory
				.getAccessToken()));
		nameValuePairs.add(new BasicNameValuePair("format", "json"));

		nameValuePairs.add(new BasicNameValuePair("message", message));

		if (!picture.equals("")) {
			nameValuePairs.add(new BasicNameValuePair("picture", picture));
		}
		if (!link.equals("")) {
			nameValuePairs.add(new BasicNameValuePair("link", link));
		}
		if (!name.equals("")) {
			nameValuePairs.add(new BasicNameValuePair("name", name));
		}
		if (!caption.equals("")) {
			nameValuePairs.add(new BasicNameValuePair("caption", caption));
		}
		if (!source.equals("")) {
			nameValuePairs.add(new BasicNameValuePair("source", source));
		}
		if (!link.equals("")) {
			nameValuePairs.add(new BasicNameValuePair("actions", String.format(
					"{\"name\": \"PalPalShare\", \"link\": \"%s\"}", link)));
		}

		String responseString = callHTTPS(url, nameValuePairs);
		if (responseString == null) {
			return false;
		}
		return true;
	}

	/**
	 * get response from url using https
	 * 
	 * @param url
	 * @param nameValuePairs
	 * @return
	 * @throws FacebookException
	 */
	public static String callHTTPS(String url,
			List<NameValuePair> nameValuePairs) throws Exception {
		HttpPost post = new HttpPost(url);
		try {
			// support Chinese
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

			HttpClient client = new DefaultHttpClient();
			HttpResponse response;
			response = client.execute(post);
			HttpEntity entity = response.getEntity();
			// return true or false
			String responseString = EntityUtils.toString(entity);
			WLog.d(TAG, "response json: " + responseString);
			return responseString;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getLinkpreview(String link) {

		String url = "https://api.facebook.com/method/links.preview";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("url", link));
		nameValuePairs.add(new BasicNameValuePair("access_token", Memory
				.getAccessToken()));
		nameValuePairs.add(new BasicNameValuePair("format", "json"));
		String responseString;
		try {
			responseString = callHTTPS(url, nameValuePairs);
			WLog.d(TAG,
					String.format("received links.preview %s", responseString));
			return responseString;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * user graph api to upload an image from local storage to facebook album.
	 * 
	 * @param photoFeed
	 * @param albumId
	 * @return
	 */
	public static String uploadPhotoFromFile(String fullLocalImagePath,
			String message, String albumId) {

		WLog.d(TAG, String.format("start upload photo source [%s]",
				fullLocalImagePath));

		try {
			File input = new File(fullLocalImagePath);
			FileInputStream fin = new FileInputStream(input);

			/*
			 * Create byte array large enough to hold the content of the file.
			 * Use File.length to determine size of the file in bytes.
			 */

			byte fileContent[] = new byte[(int) input.length()];
			fin.read(fileContent);

			// send to facebook
			Bundle params = new Bundle();
			params.putByteArray("picture", fileContent);
			params.putString("message", message);

			if (albumId == null) {
				albumId = "me";
			}

			String graphPath = String.format("%s/photos", albumId);

			try {
				Facebook facebook = Memory.getFacebookClient();

				String responseString = facebook.request(graphPath, params,
						"POST");
				WLog.d(TAG, String.format("upload photo response [%s]",
						responseString));
				return responseString;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * user graph api to like a facbeook object
	 * 
	 * @param post_id
	 * @return
	 * @throws Exception
	 * @throws FacebookException
	 */
	public static boolean addLike(String post_id) throws Exception {
		String url = String.format("https://graph.facebook.com/%s/likes",
				post_id);
		String access_token = Memory.getAccessToken();

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs
				.add(new BasicNameValuePair("access_token", access_token));
		nameValuePairs.add(new BasicNameValuePair("format", "json"));

		// return "true" if ok
		String responseString = callHTTPS(url, nameValuePairs);
		WLog.d(TAG, String.format("like (graph) response [%s]", responseString));
		if (responseString != null) {
			return responseString.equals("true");
		}
		return false;
	}

	/**
	 * user graph api to comment a facebook object
	 * 
	 * @param post_id
	 * @param comment
	 * @return
	 * @throws FacebookException
	 */
	public static boolean addComment(String post_id, String comment)
			throws Exception {
		String url = String.format("https://graph.facebook.com/%s/comments",
				post_id);
		String access_token = Memory.getAccessToken();

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs
				.add(new BasicNameValuePair("access_token", access_token));
		nameValuePairs.add(new BasicNameValuePair("format", "json"));
		nameValuePairs.add(new BasicNameValuePair("message", comment));

		// return "true" if ok
		String responseString = callHTTPS(url, nameValuePairs);

		if (responseString != null) {
			checkFacebookException(responseString);
			String id = "";
			try {
				JSONObject json = new JSONObject(responseString);
				id = json.getString("id");
			} catch (JSONException e) {

			}
			WLog.d(TAG, String.format("comment (graph) response [%s]",
					responseString));
			return id.equals("") == false;
		}
		return false;
	}

	/**
	 * given a json resposne string, check if the string contains error
	 */
	public static void checkFacebookException(String responseString)
			throws Exception {
		JSONObject json = null;
		try {
			json = new JSONObject(responseString);
			JSONObject errorJSON = json.getJSONObject("error");
			String errorType = errorJSON.getString("type");
			String errorMessage = errorJSON.getString("message");
			throw new Exception(errorJSON.toString());
		} catch (JSONException e) {
			try {
				json = new JSONObject(responseString);
				String errorType = json.getString("error_code");
				String errorMessage = json.getString("error_msg");
				throw new Exception(json.toString());
			} catch (JSONException e1) {
			}
		}
	}
}
