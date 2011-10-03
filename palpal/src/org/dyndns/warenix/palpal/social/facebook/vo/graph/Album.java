package org.dyndns.warenix.palpal.social.facebook.vo.graph;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.palpal.social.facebook.FacebookException;
import org.dyndns.warenix.palpal.social.facebook.activity.AlbumPhotoActivity;
import org.dyndns.warenix.palpal.social.facebook.activity.CommentActivity;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookAPI;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookUtil;
import org.dyndns.warenix.palpal.social.facebook.vo.FacebookPost;
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
import android.widget.TextView;

public class Album extends FacebookPost {

	public static final String TYPE = "album";

	public static final String ACTION_COMMENT = "Read Comments";
	public static final String ACTION_OPEN_BROWSER = "Open in Browser";

	public String name;
	public String link;
	public String description;
	public String photoCount;

	static class ViewHolder {
		TextView nameText;
		TextView createdTimeText;
		TextView statText;
	}

	public Album() {
		super(TYPE);
	}

	public Album(Parcel in) throws FacebookException {
		super(TYPE, in);
	}

	public Album(JSONObject post) throws FacebookException {
		super(TYPE, post);
	}

	public static Album factory(JSONObject post) throws FacebookException {
		Album statusFeed = new Album(post);
		return statusFeed;
	}

	@Override
	public void parseJSONObject(JSONObject json) {
		name = FacebookUtil.getJSONString(json, "name", "");
		link = FacebookUtil.getJSONString(json, "link", "");
		description = FacebookUtil.getJSONString(json, "description", "");
		photoCount = FacebookUtil.getJSONString(json, "count", "");

		JSONObject commentJSON = FacebookUtil.getJSONObject(json, "comments",
				null);
		if (commentJSON != null) {
			JSONArray data = FacebookUtil.getJSONArray(commentJSON, "data",
					null);

			commentsCount = "" + data.length();
		} else {
			commentsCount = "0";
		}

		String responseString = FacebookAPI.Feed.getAllLikes(id);
		JSONObject post;
		try {
			post = new JSONObject(responseString);
			JSONArray data = post.getJSONArray("data");
			likesCount = "" + data.length();
			Log.d("palpal",
					String.format("likesCount:%d, data:%s", data.length(),
							data.toString()));
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public View factory(Context context, View convertView,
			HashMap<String, SoftReference<Bitmap>> imagePool) {
		if (inflater == null) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		View view = convertView;
		if (view == null
				|| (view != null && (view.getTag().equals(type) == false))) {
			view = inflater.inflate(R.layout.album, null);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.nameText = (TextView) view.findViewById(R.id.name);
			viewHolder.createdTimeText = (TextView) view
					.findViewById(R.id.createdTime);
			viewHolder.statText = (TextView) view.findViewById(R.id.stats);
			view.setTag(R.id.picture, viewHolder);
		}

		ViewHolder viewHolder = (ViewHolder) view.getTag(R.id.picture);

		viewHolder.nameText.setText(String.format("%s x %s", name, photoCount));
		viewHolder.createdTimeText
				.setText(FacebookUtil
						.formatFacebookTimeToLocaleString(createdTime != null ? createdTime
								: updatedTime));

		viewHolder.statText.setText(String.format("%s likes | %s comments",
				likesCount, commentsCount));

		view.setTag(type);
		return view;
	}

	public void addCustomAction() {
		actionNameList.add(ACTION_COMMENT);
		actionLinkList.add(ACTION_COMMENT);

		actionNameList.add(ACTION_OPEN_BROWSER);
		actionLinkList.add(ACTION_OPEN_BROWSER);
	}

	@Override
	public boolean handleCustomAction(Context context, int actionIndex) {
		String actionName = actionNameList.get(actionIndex);
		String actionLink = actionLinkList.get(actionIndex);

		Log.d("palpal", String.format(
				"PhotoFeed try to handle custom action %d %s", actionIndex,
				actionName));
		if (actionLink.equals(ACTION_DEFAULT)) {
			Intent intent = new Intent(context, AlbumPhotoActivity.class);
			intent.putExtra("post_id", id);
			Bundle extras = new Bundle();
			extras.putString(AlbumPhotoActivity.BUNDLE_POST_ID, id);
			intent.putExtras(extras);
			context.startActivity(intent);
			return true;
		} else if (actionLink.equals(ACTION_COMMENT)) {
			Intent intent = new Intent(context, CommentActivity.class);
			Bundle extras = new Bundle();
			extras.putString(CommentActivity.BUNDLE_POST_ID, id);
			extras.putParcelable(CommentActivity.BUNDLE_FEED, this);
			intent.putExtras(extras);
			context.startActivity(intent);
			return true;
		} else if (actionLink.equals(ACTION_OPEN_BROWSER)) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			Uri data = Uri.parse(link);
			intent.setData(data);
			context.startActivity(intent);
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
				return new Album(in);
			} catch (FacebookException e) {
				return null;
			}
		}

		public FacebookPost[] newArray(int size) {
			return new Album[size];
		}
	};

	@Override
	public FacebookPost factory() {
		// TODO Auto-generated method stub
		return null;
	}

}
