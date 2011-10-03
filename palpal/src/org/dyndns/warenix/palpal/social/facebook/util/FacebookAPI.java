package org.dyndns.warenix.palpal.social.facebook.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.dyndns.warenix.palpal.PalPal;
import org.dyndns.warenix.palpal.social.facebook.FacebookException;
import org.dyndns.warenix.palpal.social.facebook.Like;
import org.dyndns.warenix.palpal.social.facebook.vo.FacebookPost;
import org.dyndns.warenix.palpal.social.facebook.vo.FacebookPostFactory;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.LinkFeed;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.PhotoFeed;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.Profile;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.StatusFeed;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.VideoFeed;
import org.dyndns.warenix.util.DownloadFileTool;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

public class FacebookAPI {
	public static class Feed {
		/**
		 * use graph api to get all comments of a facebook obejct
		 * 
		 * @param post_id
		 * @return
		 */
		public static String getAllComments(String post_id) {
			String path = String.format("%s/comments", post_id);
			String responseJSON = null;

			try {
				responseJSON = PalPal.getFacebook().request(path);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.d("palpal",
					String.format("received comment string %s", responseJSON));

			return responseJSON;
		}

		/**
		 * use graph api to get all comments of a facebook obejct
		 * 
		 * @param post_id
		 * @return
		 */
		public static String getAllComments(String post_id, Bundle parameters) {
			String path = String.format("%s/comments", post_id);
			String responseJSON = null;

			try {
				responseJSON = PalPal.getFacebook().request(path, parameters);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.d("palpal", String.format(
					"received comment (parameters) string %s", responseJSON));

			return responseJSON;
		}

		/**
		 * use graph api to get all likes of a facebook object
		 * 
		 * @param post_id
		 * @return
		 */
		public static String getAllLikes(String post_id) {
			String path = String.format("%s/likes", post_id);
			String responseJSON = null;

			try {
				responseJSON = PalPal.getFacebook().request(path);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.d("palpal",
					String.format("received likes string %s", responseJSON));

			return responseJSON;
		}

		/**
		 * user graph api to like a facbeook object
		 * 
		 * @param post_id
		 * @return
		 * @throws FacebookException
		 */
		public static boolean addLike(String post_id) throws FacebookException {
			String url = String.format("https://graph.facebook.com/%s/likes",
					post_id);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("access_token", PalPal
					.getFacebook().getAccessToken()));
			nameValuePairs.add(new BasicNameValuePair("format", "json"));

			// return "true" if ok
			String responseString = FacebookAPI.callHTTPS(url, nameValuePairs);
			Log.d("palpal",
					String.format("like (graph) response [%s]", responseString));
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
				throws FacebookException {
			String url = String.format(
					"https://graph.facebook.com/%s/comments", post_id);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("access_token", PalPal
					.getFacebook().getAccessToken()));
			nameValuePairs.add(new BasicNameValuePair("format", "json"));
			nameValuePairs.add(new BasicNameValuePair("message", comment));

			// return "true" if ok
			String responseString = FacebookAPI.callHTTPS(url, nameValuePairs);

			if (responseString != null) {
				FacebookUtil.checkFacebookException(responseString);
				String id = "";
				try {
					JSONObject json = new JSONObject(responseString);
					id = json.getString("id");
				} catch (JSONException e) {

				}
				Log.d("palpal", String.format("comment (graph) response [%s]",
						responseString));
				return id.equals("") == false;
			}
			return false;
		}

		/**
		 * use graph api to get friends of a user/group
		 * 
		 * @param userId
		 * @return
		 */
		public static String getUserFriends(String userId) {
			String path = String.format("%s/friends", userId);
			String responseJSON = null;

			try {
				responseJSON = PalPal.getFacebook().request(path);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.d("palpal",
					String.format("received comment string %s", responseJSON));

			return responseJSON;
		}

		/**
		 * post status, link, photo or video on wall. no upload of photo or
		 * video, only share the link
		 * 
		 * @param graphPath
		 * @param message
		 * @param picture
		 * @param link
		 * @param name
		 * @param caption
		 * @param description
		 * @return true if everything goes fine
		 * @throws FacebookException
		 */
		public static boolean post(String graphPath, String message,
				String picture, String link, String name, String caption,
				String description, String source) throws FacebookException {
			String url = String.format("https://graph.facebook.com/%s",
					graphPath);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("access_token", PalPal
					.getFacebook().getAccessToken()));
			nameValuePairs.add(new BasicNameValuePair("format", "json"));

			nameValuePairs.add(new BasicNameValuePair("message", message));
			nameValuePairs.add(new BasicNameValuePair("picture", picture));
			nameValuePairs.add(new BasicNameValuePair("link", link));
			nameValuePairs.add(new BasicNameValuePair("name", name));
			nameValuePairs.add(new BasicNameValuePair("caption", caption));
			nameValuePairs.add(new BasicNameValuePair("source", source));
			if (!link.equals("")) {
				nameValuePairs
						.add(new BasicNameValuePair(
								"actions",
								String.format(
										"{\"name\": \"PalPalShare\", \"link\": \"%s\"}",
										link)));
			}

			String responseString = FacebookAPI.callHTTPS(url, nameValuePairs);
			if (responseString == null) {
				return false;
			}
			FacebookUtil.checkFacebookException(responseString);
			return true;
		}

		// }

		public static boolean post(String graphPath, String message,
				String picture, String link, String name, String caption,
				String description, String source, String actions)
				throws FacebookException {
			String url = String.format("https://graph.facebook.com/%s",
					graphPath);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("access_token", PalPal
					.getFacebook().getAccessToken()));
			nameValuePairs.add(new BasicNameValuePair("format", "json"));

			nameValuePairs.add(new BasicNameValuePair("message", message));
			nameValuePairs.add(new BasicNameValuePair("picture", picture));
			nameValuePairs.add(new BasicNameValuePair("link", link));
			nameValuePairs.add(new BasicNameValuePair("name", name));
			nameValuePairs.add(new BasicNameValuePair("caption", caption));
			nameValuePairs.add(new BasicNameValuePair("source", source));
			if (!actions.equals("")) {
				nameValuePairs.add(new BasicNameValuePair("actions", actions));
			}

			String responseString = FacebookAPI.callHTTPS(url, nameValuePairs);
			if (responseString == null) {
				return false;
			}
			FacebookUtil.checkFacebookException(responseString);
			return true;
		}
	}

	/**
	 * use rest api to comment a facabook object
	 * 
	 * @param post_id
	 * @param comment
	 * @return
	 * @throws FacebookException
	 */
	public static boolean commentPost(String post_id, String comment)
			throws FacebookException {
		// https://api.facebook.com/method/stream.addComment?post_id=<?>&comment=<?>&access_token=<?>&format=json//
		String url = "https://api.facebook.com/method/stream.addComment";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("post_id", post_id));
		nameValuePairs.add(new BasicNameValuePair("comment", comment));
		nameValuePairs.add(new BasicNameValuePair("access_token", PalPal
				.getFacebook().getAccessToken()));
		nameValuePairs.add(new BasicNameValuePair("format", "json"));

		return callHTTPS(url, nameValuePairs) != null;
	}

	/**
	 * user rest api to like a facabook object
	 * 
	 * @param post_id
	 * @return
	 * @throws FacebookException
	 */
	public static boolean likePost(String post_id) throws FacebookException {
		// sample url
		// https://api.facebook.com/method/stream.addLike?post_id=<?>&access_token=<?>&format=json
		String url = "https://api.facebook.com/method/stream.addLike";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("post_id", post_id));
		nameValuePairs.add(new BasicNameValuePair("access_token", PalPal
				.getFacebook().getAccessToken()));
		nameValuePairs.add(new BasicNameValuePair("format", "json"));
		String errorMsg = "";
		String responseString = callHTTPS(url, nameValuePairs);
		Log.d("palpal",
				String.format("like (rest) response [%s]", responseString));
		FacebookUtil.checkFacebookException(responseString);
		return errorMsg.equals("") || responseString.equals("true");
	}

	/**
	 * 
	 * @param userId
	 * @param showUnread
	 *            0: show all; 1: show unread only
	 * @param limit
	 *            less than 0 means 100
	 * @return
	 * @throws FacebookException
	 */
	public static String notificationsGetListByFQL(String userId,
			int showUnread, int limit) throws FacebookException {
		if (limit < 0) {
			limit = 100;
		}
		String query = String
				.format("SELECT notification_id, sender_id, created_time, title_text, body_text, href, object_id FROM notification WHERE recipient_id = %s AND is_unread = %d AND is_hidden = 0 order by created_time desc limit %d ",
						userId, showUnread, limit);

		return fqlQuery(query);
	}

	/**
	 * mark notification read
	 * 
	 * @param notificationId
	 * @return
	 * @throws FacebookException
	 */
	public static boolean notificationMarkRead(String notificationId)
			throws FacebookException {
		String url = "https://api.facebook.com/method/notifications.markRead";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("notification_ids",
				notificationId));
		nameValuePairs.add(new BasicNameValuePair("access_token", PalPal
				.getFacebook().getAccessToken()));
		nameValuePairs.add(new BasicNameValuePair("format", "json"));
		String responseString = callHTTPS(url, nameValuePairs);

		return responseString.equals("true");
	}

	/**
	 * Execute raw query
	 * 
	 * @param query
	 * @return
	 * @throws FacebookException
	 */
	public static String fqlQuery(String query) throws FacebookException {
		String url = "https://api.facebook.com/method/fql.query";
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("query", query));
		nameValuePairs.add(new BasicNameValuePair("access_token", PalPal
				.getFacebook().getAccessToken()));
		nameValuePairs.add(new BasicNameValuePair("format", "json"));

		return callHTTPS(url, nameValuePairs);
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
			List<NameValuePair> nameValuePairs) throws FacebookException {
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
			Log.d("palpal", "response json: " + responseString);
			FacebookUtil.checkFacebookException(responseString);
			return responseString;

		} catch (FacebookException e) {
			throw e;
		} catch (Exception e) {
			throw new FacebookException(String.format("fail to call %s", url),
					e.getMessage());
		}
	}

	public static String callHTTPSDelete(String url,
			List<NameValuePair> nameValuePairs) {
		HttpDelete post = new HttpDelete(url);
		// try {
		// support Chinese
		// post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		try {
			response = client.execute(post);
			HttpEntity entity = response.getEntity();
			// return true or false
			String responseJSON = EntityUtils.toString(entity);
			Log.d("palpal", "response json: " + responseJSON);
			return responseJSON;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }
		return null;
	}

	public static String getProfile(String userName) throws FacebookException {
		String path = String.format("%s", userName);
		String responseJSON = null;

		try {
			responseJSON = PalPal.getFacebook().request(path);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FacebookException(String.format(
					"fail to get profile of %s", userName), e.getMessage());
		}
		Log.d("palpal",
				String.format("received profile string %s", responseJSON));

		return responseJSON;
	}

	public static String getPost(String postId) {
		String path = String.format("%s", postId);
		String responseJSON = null;

		try {
			responseJSON = PalPal.getFacebook().request(path);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d("palpal", String.format("received post string %s", responseJSON));

		return responseJSON;
	}

	/**
	 * fetch a list of like for a given post
	 * 
	 * @param postId
	 * @return
	 */
	public static ArrayList<Like> fetchListListOfAPost(String postId) {
		String responseString = FacebookAPI.Feed.getAllLikes(postId);
		ArrayList<Like> likeList = FacebookUtil.Factory
				.factoryLikeList(responseString);
		return likeList;
	}

	/**
	 * use rest api to get links preview
	 * 
	 * @param link
	 * @return
	 * @throws FacebookException
	 */
	public static String linksPreview(String link) throws FacebookException {
		String url = "https://api.facebook.com/method/links.preview";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("url", link));
		nameValuePairs.add(new BasicNameValuePair("access_token", PalPal
				.getFacebook().getAccessToken()));
		nameValuePairs.add(new BasicNameValuePair("format", "json"));
		String responseString = callHTTPS(url, nameValuePairs);
		Log.d("palpal",
				String.format("received links.preview %s", responseString));
		return responseString;
	}

	/**
	 * get the facebook post id from a notification href. however the post id
	 * doesn't regonize the type of facebook object.
	 * 
	 * @param link
	 *            http://www.facebook.com/{username}/posts/{post_item_id}
	 * @return
	 * @throws FacebookException
	 */
	public static String getPostIdFromNotificationUrl(String link)
			throws FacebookException {
		Log.d("palpal", String.format(
				"extract post_id from notification link [%s]", link));

		if (link.contains("album.php")) {
			String startString = "fbid=";
			int startIndex = link.indexOf(startString);
			int endIndex = link.indexOf("&");

			Log.d("palpal",
					String.format("extract %d - %d", startIndex, endIndex));
			return link.substring(startIndex + startString.length(), endIndex);
		} else if (link.contains("story_fbid=")) {
			return FacebookUtil.getParameterValueByName(link, "story_fbid");
		} else if (link.contains("photo.php")) {
			return FacebookUtil.getParameterValueByName(link, "fbid");
		}
		try {
			String[] parts = link.split("/");
			String userName = parts[3];
			String postItemId = parts[5];

			String responseString = FacebookAPI.getProfile(userName);
			Profile profile = new Profile(new JSONObject(responseString));
			Log.d("palpal", profile.toString());

			return String.format("%s_%s", profile.id, postItemId);
		} catch (JSONException e) {
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static FacebookPost factoryFacebookPost(String postId,
			HashMap<String, SoftReference<Bitmap>> imagePool) {

		String responseString = FacebookAPI.getPost(postId);
		JSONObject post;
		try {
			post = new JSONObject(responseString);
			String type = FacebookUtil.getJSONString(post, "type", null);
			FacebookPost feed = FacebookPostFactory.factory(type, post);
			return feed;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	// helper

	/**
	 * first download an image to local storage, then use graph api to upload it
	 * to facebook album
	 * 
	 * @param photoFeed
	 * @param albumId
	 * @return
	 */
	public static String uploadPhotoFromURL(PhotoFeed photoFeed, String albumId) {

		// download url file to sdcard
		URL uploadFileUrl = null;
		try {
			uploadFileUrl = new URL(photoFeed.picture);

			try {
				HttpURLConnection conn = (HttpURLConnection) uploadFileUrl
						.openConnection();
				conn.setDoInput(true);
				conn.connect();
				int length = conn.getContentLength();

				if (length == -1) {
					return null;
				}

				String full_local_file_path = new DownloadFileTool()
						.downloadFile(new URL(photoFeed.picture),
								"palpal/caches", photoFeed.link.hashCode()
										+ ".tmp");

				photoFeed.picture = full_local_file_path;

				return uploadPhotoFromFile(photoFeed, albumId);

			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
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
	public static String uploadPhotoFromFile(PhotoFeed photoFeed, String albumId) {

		try {
			File input = new File(photoFeed.picture);
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
			params.putString("message", photoFeed.message);

			if (albumId == null) {
				albumId = "me";
			}

			String graphPath = String.format("%s/photos", albumId);

			try {
				String responseString = PalPal.getFacebook().request(graphPath,
						params, "POST");
				Log.v("palpal", String.format("upload photo response [%s]",
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

	public static String getUserAlbum(String userName, Bundle parameters) {
		String path = String.format("%s/albums", userName);
		String responseJSON = null;

		try {
			responseJSON = PalPal.getFacebook().request(path, parameters);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d("palpal", String.format("received album string %s", responseJSON));

		return responseJSON;
	}

	public static boolean share(VideoFeed video, String profileId)
			throws FacebookException {

		boolean success = FacebookAPI.Feed.post(
				String.format("%s/feed", profileId), video.message,
				video.picture, video.link, video.name, video.caption,
				video.description, video.source);

		Log.d("palpal", String.format("share video response %s", success));
		return success;
	}

	public static boolean share(LinkFeed video, String profileId)
			throws FacebookException {
		String actions = "";

		if (video.link != null) {
			// String actionLink = "";
			// actionLink = "http://www.facebook.com/dialog/feed?";
			// actionLink += String.format("app_id=%s&", PalPal.APP_ID);
			// actionLink += String.format("link=%s&", video.link);
			// actionLink += String.format("picture=%s&",
			// video.picture == null ? "" : video.picture);
			// actionLink += String.format("name=%s&", video.name == null ? ""
			// : video.name);
			// actionLink += String.format("caption=&");
			// actionLink += String.format("description=%s&",
			// video.description == null ? "" : video.description);
			// actionLink += String.format("message=&", video.message == null ?
			// ""
			// : video.message);
			// actionLink += String
			// .format("redirect_uri=http://www.facebook.com/");
			//
			// actions = String
			// .format("{\"name\": \"PalPalShare\", \"link\": \"%s\"}",
			// actionLink);
			//
			// Log.d("palpal", String.format("share action link [%s]",
			// actionLink));

			String actionLink = String.format(
					"http://www.facebook.com/sharer.php?u=%s&", video.link);
			actions = String
					.format("{\"name\": \"PalPalShare\", \"link\": \"%s\"}",
							actionLink);
		}

		boolean success = FacebookAPI.Feed.post(
				String.format("%s/feed", profileId), video.message,
				video.picture, video.link, video.name, "", video.description,
				"", actions);
		Log.d("palpal", String.format("share video response %s", success));
		return success;
	}

	public static boolean share(PhotoFeed video) throws FacebookException {

		String actions = "";

		if (video.link != null) {
			String actionLink = String.format(
					"http://www.facebook.com/sharer.php?u=%s&", video.link);
			actions = String
					.format("{\"name\": \"PalPalShare\", \"link\": \"%s\"}",
							actionLink);
		}

		boolean success = FacebookAPI.Feed.post("me/feed", video.message,
				video.picture, video.link, video.name, "", "", "", actions);

		Log.d("palpal", String.format("share photo response %s", success));
		return success;
	}

	public static boolean share(StatusFeed status, String profileId)
			throws FacebookException {

		boolean success = FacebookAPI.Feed.post(
				String.format("%s/feed", profileId), status.message, "", "",
				"", "", "", "");

		Log.d("palpal", String.format("share status response %s", success));
		return success;
	}

	public static String getUserFriends(String userName, Bundle parameters) {
		String path = String.format("%s/friends", userName);
		String responseJSON = null;

		try {
			responseJSON = PalPal.getFacebook().request(path, parameters);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d("palpal",
				String.format("received friends string %s", responseJSON));

		return responseJSON;
	}

	public static String getUserGroups(String userName, Bundle parameters) {
		String path = String.format("%s/groups", userName);
		String responseJSON = null;

		try {
			responseJSON = PalPal.getFacebook().request(path, parameters);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d("palpal", String.format("received group string %s", responseJSON));

		return responseJSON;
	}

	public static String getUserLikes(String userName, Bundle parameters) {
		String path = String.format("%s/likes", userName);
		String responseJSON = null;

		try {
			responseJSON = PalPal.getFacebook().request(path, parameters);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d("palpal", String.format("received group string %s", responseJSON));

		return responseJSON;
	}

	/**
	 * find recent checkins of a user's friends
	 * 
	 * @param userId
	 * @param limit
	 * @return
	 * @throws FacebookException
	 */
	public static String getUserFriendsCheckinsByFQL(String userId, int limit)
			throws FacebookException {
		if (limit < 0) {
			limit = 100;
		}
		String query = String
				.format("SELECT coords,tagged_uids,author_uid,page_id,app_id,post_id,timestamp,message FROM checkin WHERE author_uid IN (select uid1 from friend where uid2='%s') order by timestamp desc limit %d",
						userId, limit);

		return fqlQuery(query);
	}
}
