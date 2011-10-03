package org.dyndns.warenix.palpal.social.facebook.vo;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;

import org.dyndns.warenix.palpal.social.facebook.FacebookException;
import org.dyndns.warenix.palpal.social.facebook.activity.AlbumActivity;
import org.dyndns.warenix.palpal.social.facebook.activity.CommentActivity;
import org.dyndns.warenix.palpal.social.facebook.activity.FriendsActivity;
import org.dyndns.warenix.palpal.social.facebook.activity.NewsFeedActivity;
import org.dyndns.warenix.palpal.social.facebook.task.LikePostAsyncTask;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Filter;

public abstract class FacebookPost implements Parcelable {

	public static final String ACTION_DEFAULT = "Default Action";
	public static final String ACTION_VIEW_USER_WALL = "View %s's Wall";
	public static final String ACTION_VIEW_USER_ALBUM = "View %s's Albums";

	public static final String ACTION_VIEW_FRIENDS = "View %s's Friends";
	public static final String ACTION_VIEW_GROUPS = "View %s's Groups";
	public static final String ACTION_VIEW_LIKES = "View %s's Likes";

	public String jsonString;
	public String type;

	public String id;
	public String createdTime;
	public String createdById;
	public String createdByName;
	public String updatedTime;

	public String likesCount;
	public String commentsCount;

	public ArrayList<String> actionNameList = new ArrayList<String>();
	public ArrayList<String> actionLinkList = new ArrayList<String>();

	protected static LayoutInflater inflater;

	public FacebookPost() {
		addDefaultAction();
	}

	public FacebookPost(String type) {
		this.type = type;
		addDefaultAction();
	}

	public FacebookPost(String type, Parcel in) throws FacebookException {
		try {
			factory(type, new JSONObject(in.readString()));
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public FacebookPost(String type, JSONObject post) throws FacebookException {
		factory(type, post);
	}

	public void factory(String type, JSONObject post) throws FacebookException {
		this.type = type;
		this.jsonString = post.toString();

		// parse json for FacebookPost
		id = FacebookUtil.getJSONString(post, "id", null);

		try {
			JSONObject from = post.getJSONObject("from");
			createdById = from.getString("id");
			createdByName = from.getString("name");
		} catch (JSONException e) {
		}
		createdTime = FacebookUtil.getJSONString(post, "created_time", null);
		updatedTime = FacebookUtil.getJSONString(post, "updated_time", null);
		likesCount = FacebookUtil.getJSONString(post, "likes", "0");

		commentsCount = "0";
		try {
			JSONObject commentJSON = post.getJSONObject("comments");
			// commentsCount = commentJSON.getString("count");
			commentsCount = FacebookUtil.getJSONString(commentJSON, "count",
					"0");
			if (commentsCount.equals("0")) {
				JSONArray data = commentJSON.getJSONArray("data");
				commentsCount = data.length() + "";
			}
		} catch (JSONException e) {
		}

		// parse json of custom part
		parseJSONObject(post);

		addDefaultAction();

		try {
			JSONArray actions = post.getJSONArray("actions");
			for (int i = 0; i < actions.length(); ++i) {
				JSONObject action = (JSONObject) actions.get(i);
				actionLinkList.add(action.getString("link"));
				actionNameList.add(action.getString("name"));
			}
		} catch (JSONException e) {
		}

		addCustomAction();

	}

	/**
	 * set this object properties according to the given json child override
	 * this to handle its own section of json
	 * 
	 * @param post
	 * @throws FacebookException
	 */
	public abstract void parseJSONObject(JSONObject post)
			throws FacebookException;

	public String toString() {
		return String.format("type:%s id:%s created by:%s", type, id,
				createdByName);
	}

	/**
	 * how many default actions. actionIndex greater than or equal to this count
	 * is considered to be children custom actions.
	 * 
	 * @return
	 */
	public int getDefaultActionCount() {
		return 2;
	}

	/**
	 * Get a view of this object.
	 * 
	 * @param context
	 * @param convertView
	 * @param imagePool
	 * @return
	 */
	public View getView(Context context, View convertView,
			HashMap<String, SoftReference<Bitmap>> imagePool) {
		if (inflater == null) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		return factory(context, convertView, imagePool);
	}

	/**
	 * construct custom view
	 * 
	 * @param context
	 * @param convertView
	 * @param imagePool
	 * @return null object if can't be constructed
	 */
	public abstract View factory(Context context, View convertView,
			HashMap<String, SoftReference<Bitmap>> imagePool);

	void addDefaultAction() {
		// add default actions
		actionLinkList.add(ACTION_DEFAULT);
		actionNameList.add(ACTION_DEFAULT);

		// actionLinkList.add(ACTION_VIEW_USER_WALL);
		// actionNameList.add(String.format(ACTION_VIEW_USER_WALL,
		// createdByName));
		//
		// actionLinkList.add(ACTION_VIEW_USER_ALBUM);
		// actionNameList
		// .add(String.format(ACTION_VIEW_USER_ALBUM, createdByName));
		//
		// actionLinkList.add(ACTION_VIEW_GROUPS);
		// actionNameList.add(String.format(ACTION_VIEW_GROUPS, createdByName));

	}

	/**
	 * child overrides this to add its own custom actions
	 */
	public abstract void addCustomAction();

	/**
	 * handle custom action. custom actions are handle prior to default actions
	 * 
	 * @param context
	 * @param actionIndex
	 * @return handled or not
	 */
	public abstract boolean handleCustomAction(Context context, int actionIndex);

	/**
	 * handle actoin
	 * 
	 * @param actionIndex
	 * @return handled or not
	 */
	public boolean action(Context context, int actionIndex) {
		String actionLink = actionLinkList.get(actionIndex);
		String actionName = actionNameList.get(actionIndex);

		if (handleCustomAction(context, actionIndex)) {
			Log.d("palpal", String.format("custom actoin is handled %d %s",
					actionIndex, actionName));
		} else {
			// handle common actions
			Log.d("palpal", String.format("FacebookPost handled actoin %d %s",
					actionIndex, actionName));
			if (actionName.equals(ACTION_DEFAULT)) {

			} else if (actionLink.equals(ACTION_VIEW_USER_WALL)) {
				Intent intent = new Intent(context, NewsFeedActivity.class);
				Bundle extras = new Bundle();
				extras.putString(NewsFeedActivity.BUNDLE_GRAPH_PATH,
						String.format("%s/feed", createdById));
				intent.putExtras(extras);
				context.startActivity(intent);
			} else if (actionName.equals("Like")) {
				new LikePostAsyncTask(context).execute(id);
			} else if (actionName.equals("Comment")) {
				Intent intent = new Intent(context, CommentActivity.class);
				Bundle extras = new Bundle();
				extras.putString(CommentActivity.BUNDLE_POST_ID, id);
				extras.putParcelable(CommentActivity.BUNDLE_FEED, this);
				intent.putExtras(extras);
				context.startActivity(intent);
			} else if (actionLink.equals(ACTION_VIEW_USER_ALBUM)) {
				Intent intent = new Intent(context, AlbumActivity.class);
				// intent.putExtra("userId", createdById);
				Bundle extras = new Bundle();
				extras.putString(AlbumActivity.BUNDLE_MODE,
						AlbumActivity.MODE_FETCH_USER_ALBUMS);
				extras.putString(AlbumActivity.BUNDLE_USER_ID, createdById);
				extras.putString(AlbumActivity.BUNDLE_USER_NAME, createdByName);
				intent.putExtras(extras);
				context.startActivity(intent);
			} else if (actionLink.equals(ACTION_VIEW_FRIENDS)) {
				Intent intent = new Intent(context, FriendsActivity.class);
				Bundle extras = new Bundle();
				extras.putString(FriendsActivity.BUNDLE_MODE,
						FriendsActivity.MODE_FRIENDS);
				extras.putString(AlbumActivity.BUNDLE_USER_ID, createdById);
				intent.putExtras(extras);
				context.startActivity(intent);
			} else if (actionLink.equals(ACTION_VIEW_GROUPS)) {
				Intent intent = new Intent(context, FriendsActivity.class);
				Bundle extras = new Bundle();
				extras.putString(FriendsActivity.BUNDLE_MODE,
						FriendsActivity.MODE_GROUPS);
				extras.putString(AlbumActivity.BUNDLE_USER_ID, createdById);
				intent.putExtras(extras);
				context.startActivity(intent);
			} else if (actionLink.equals(ACTION_VIEW_LIKES)) {
				Intent intent = new Intent(context, FriendsActivity.class);
				Bundle extras = new Bundle();
				extras.putString(FriendsActivity.BUNDLE_MODE,
						FriendsActivity.MODE_LIKES);
				extras.putString(AlbumActivity.BUNDLE_USER_ID, createdById);
				intent.putExtras(extras);
				context.startActivity(intent);
			} else {
				// execute actionLink

				Intent intent = new Intent(Intent.ACTION_VIEW);
				Uri data = Uri.parse(actionLink);
				intent.setData(data);
				context.startActivity(intent);
			}
		}

		return true;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(jsonString);
	}

	public abstract FacebookPost factory();

	/**
	 * include or not when doing adapter filter
	 * 
	 * @param prefix
	 * @return true to be included
	 */
	public boolean matchFilter(CharSequence prefix) {
		return false;
	}
}
