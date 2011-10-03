package org.dyndns.warenix.palpal.social.facebook.vo.graph;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.palpal.social.facebook.FacebookException;
import org.dyndns.warenix.palpal.social.facebook.activity.CommentActivity;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookUtil;
import org.dyndns.warenix.palpal.social.facebook.vo.FacebookPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class StoryFeed extends FacebookPost {

	public static final String TYPE = "story";

	String message;

	static class ViewHolder {
		TextView messageText;
		TextView statsText;
		TextView createdTimeText;
	}

	public StoryFeed() {
		super(TYPE);
	}

	public StoryFeed(Parcel in) throws FacebookException {
		super(TYPE, in);
	}

	public StoryFeed(JSONObject post) throws FacebookException {
		super(TYPE, post);
	}

	public static StoryFeed factory(JSONObject post) throws FacebookException {
		StoryFeed statusFeed = new StoryFeed(post);
		return statusFeed;
	}

	@Override
	public void parseJSONObject(JSONObject post) {
		try {
			message = post.getString("message");
		} catch (JSONException e) {
		}

		String likesString = FacebookUtil.getJSONString(post, "likes", null);
		if (likesString != null) {
			try {
				JSONObject data = new JSONObject(likesString);
				JSONArray likesJSON = data.getJSONArray("data");
				likesCount = "" + likesJSON.length();
			} catch (JSONException e) {
			}
		}

		String commentsString = FacebookUtil.getJSONString(post, "comments",
				null);
		if (commentsString != null) {
			try {
				JSONObject data = new JSONObject(commentsString);
				JSONArray commentsJSON = data.getJSONArray("data");
				commentsCount = "" + commentsJSON.length();
			} catch (JSONException e) {
			}
		}
	}

	@Override
	public View factory(Context context, View convertView,
			HashMap<String, SoftReference<Bitmap>> imagePool) {
		View view = convertView;
		if (view == null
				|| (view != null && (view.getTag().equals(type) == false))) {
			view = inflater.inflate(R.layout.status_feed, null);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.createdTimeText = (TextView) view
					.findViewById(R.id.createdTime);
			viewHolder.messageText = (TextView) view.findViewById(R.id.message);
			viewHolder.statsText = (TextView) view.findViewById(R.id.stats);
			view.setTag(R.id.picture, viewHolder);
		}

		ViewHolder viewHolder = (ViewHolder) view.getTag(R.id.picture);

		viewHolder.messageText.setText(message);
		viewHolder.statsText.setText(String.format("%s likes | %s comments",
				likesCount, commentsCount));

		String createdTimeString = FacebookUtil
				.formatFacebookTimeToLocaleString(updatedTime);
		viewHolder.createdTimeText.setText(createdTimeString);

		view.setTag(type);
		return view;
	}

	public void addCustomAction() {
	}

	@Override
	public boolean handleCustomAction(Context context, int actionIndex) {
		String actionName = actionNameList.get(actionIndex);
		String actionLink = actionLinkList.get(actionIndex);

		Log.d("palpal", String.format(
				"StatusFeed try to handle custom action %d %s", actionIndex,
				actionName));

		if (actionName.equals(ACTION_DEFAULT)) {
			Intent intent = new Intent(context, CommentActivity.class);
			Bundle extras = new Bundle();
			extras.putString(CommentActivity.BUNDLE_POST_ID, id);
			extras.putParcelable(CommentActivity.BUNDLE_FEED, this);
			intent.putExtras(extras);
			return true;
		}

		return false;
	}

	/*
	 * Parcelable
	 */

	public static final Parcelable.Creator<FacebookPost> CREATOR = new Parcelable.Creator<FacebookPost>() {
		public FacebookPost createFromParcel(Parcel in) {
			try {
				return new StoryFeed(in);
			} catch (FacebookException e) {
				return null;
			}
		}

		public FacebookPost[] newArray(int size) {
			return new StoryFeed[size];
		}
	};

	@Override
	public FacebookPost factory() {
		// TODO Auto-generated method stub
		return null;
	}

}
