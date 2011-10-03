package org.dyndns.warenix.palpal.social.facebook.vo.graph;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;

import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.palpal.social.facebook.FacebookException;
import org.dyndns.warenix.palpal.social.facebook.Like;
import org.dyndns.warenix.palpal.social.facebook.activity.CommentActivity;
import org.dyndns.warenix.palpal.social.facebook.activity.ShareActivity;
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
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class NoteFeed extends FacebookPost {

	public static final String TYPE = "note";
	static final String ACTION_OPEN_BROWSER = "Open in Browser";
	public static final String ACTION_SHARE = "Share";

	public String subject;
	public String message;

	static class ViewHolder {
		TextView subjectText;
		TextView messageText;
		TextView statsText;
		TextView createdTimeText;
	}

	public NoteFeed() {
		super(TYPE);
	}

	public NoteFeed(Parcel in) throws FacebookException {
		super(TYPE, in);
	}

	public NoteFeed(JSONObject post) throws FacebookException {
		super(TYPE, post);
	}

	public static NoteFeed factory(JSONObject post) throws FacebookException {
		NoteFeed statusFeed = new NoteFeed(post);
		return statusFeed;
	}

	@Override
	public void parseJSONObject(JSONObject post) {
		try {
			subject = post.getString("subject");
			message = post.getString("message");
			// likesCount = post.getJSONObject("likes").getString("count");
			ArrayList<Like> likeList = FacebookAPI.fetchListListOfAPost(id);
			likesCount = "" + likeList.size();
		} catch (JSONException e) {
		}

		try {
			JSONObject commentJSON = post.getJSONObject("comments");
			JSONArray dataJSON = commentJSON.getJSONArray("data");
			commentsCount = "" + dataJSON.length();
		} catch (JSONException e) {
		}
	}

	@Override
	public View factory(Context context, View convertView,
			HashMap<String, SoftReference<Bitmap>> imagePool) {
		View view = convertView;
		if (view == null
				|| (view != null && (view.getTag().equals(type) == false))) {
			view = inflater.inflate(R.layout.note_feed, null);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.createdTimeText = (TextView) view
					.findViewById(R.id.createdTime);

			viewHolder.subjectText = (TextView) view.findViewById(R.id.subject);
			viewHolder.messageText = (TextView) view.findViewById(R.id.message);
			viewHolder.statsText = (TextView) view.findViewById(R.id.stats);
			view.setTag(R.id.picture, viewHolder);
		}

		ViewHolder viewHolder = (ViewHolder) view.getTag(R.id.picture);

		viewHolder.subjectText.setText(subject);
		viewHolder.messageText.setText(Html.fromHtml(message));
		viewHolder.statsText.setText(String.format("%s likes | %s comments",
				likesCount, commentsCount));

		String createdTimeString = FacebookUtil
				.formatFacebookTimeToLocaleString(createdTime);
		viewHolder.createdTimeText.setText(createdTimeString);

		view.setTag(type);
		return view;
	}

	public void addCustomAction() {
		// open browser to view the status post
		// the url is the same as the comment & like action
		if (actionLinkList.size() > 0) {
			actionNameList.add(ACTION_OPEN_BROWSER);
			actionLinkList.add(actionLinkList.get(0));

			actionNameList.add(ACTION_SHARE);
			actionLinkList.add(ACTION_SHARE);
		}
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
			context.startActivity(intent);
			return true;
		} else if (actionName.equals(ACTION_OPEN_BROWSER)) {
			// open browser to view the status post
			// the url is the same as the comment & like action

			Intent intent = new Intent(Intent.ACTION_VIEW);
			String link = FacebookUtil.getFacebookURLFromCommentId(id);
			Uri data = Uri.parse(link);
			intent.setData(data);
			context.startActivity(intent);
			return true;
		} else if (actionLink.equals(ACTION_SHARE)) {
			Intent intent = new Intent(context, ShareActivity.class);
			Bundle extras = new Bundle();
			extras.putParcelable(ShareActivity.BUNDLE_FEED, this);
			intent.putExtras(extras);
			// intent.putExtra("feed", this);
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
				return new NoteFeed(in);
			} catch (FacebookException e) {
				return null;
			}
		}

		public FacebookPost[] newArray(int size) {
			return new NoteFeed[size];
		}
	};

	@Override
	public FacebookPost factory() {
		NoteFeed clone = new NoteFeed();
		clone.message = this.message;
		return clone;
	}
}
