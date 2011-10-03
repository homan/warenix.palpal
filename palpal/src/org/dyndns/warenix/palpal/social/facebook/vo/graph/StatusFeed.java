package org.dyndns.warenix.palpal.social.facebook.vo.graph;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.palpal.social.facebook.FacebookException;
import org.dyndns.warenix.palpal.social.facebook.activity.CommentActivity;
import org.dyndns.warenix.palpal.social.facebook.activity.ShareActivity;
import org.dyndns.warenix.palpal.social.facebook.util.DialogUtil;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookUtil;
import org.dyndns.warenix.palpal.social.facebook.vo.FacebookEditable;
import org.dyndns.warenix.palpal.social.facebook.vo.FacebookPost;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class StatusFeed extends FacebookPost implements FacebookEditable {

	public static final String TYPE = "status";
	static final String ACTION_OPEN_BROWSER = "Open in Browser";
	public static final String ACTION_SHARE = "Share";

	public String message;

	static class ViewHolder {
		TextView messageText;
		TextView statsText;
		TextView createdTimeText;
	}

	public StatusFeed() {
		super(TYPE);
	}

	public StatusFeed(Parcel in) throws FacebookException {
		super(TYPE, in);
	}

	public StatusFeed(JSONObject post) throws FacebookException {
		super(TYPE, post);
	}

	public static StatusFeed factory(JSONObject post) throws FacebookException {
		StatusFeed statusFeed = new StatusFeed(post);
		return statusFeed;
	}

	@Override
	public void parseJSONObject(JSONObject post) {
		try {
			message = post.getString("message");
			likesCount = post.getJSONObject("likes").getString("count");
		} catch (JSONException e) {
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
				return new StatusFeed(in);
			} catch (FacebookException e) {
				return null;
			}
		}

		public FacebookPost[] newArray(int size) {
			return new StatusFeed[size];
		}
	};

	@Override
	public FacebookPost factory() {
		StatusFeed clone = new StatusFeed();
		clone.message = this.message;
		return clone;
	}

	// FacebookEditable

	@Override
	public View getFacebookEditable(final FacebookPost feed,
			final View postView, final Context context,
			final HashMap<String, SoftReference<Bitmap>> imagePool) {

		final StatusFeed self = this;

		// add listener
		final TextView messageText = (TextView) postView
				.findViewById(R.id.message);
		messageText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogUtil.showInputDialogForTextView(context, "message",
						"What's in your mind?", messageText, self.message,
						"message", self);
			}
		});
		messageText
				.setBackgroundResource(R.drawable.facebook_editable_field_background);
		messageText.setHint("message");

		return postView;
	}

	@Override
	public FacebookEditable getFacebookFeed(View postView) {
		// original post values
		StatusFeed statusFeed = (StatusFeed) this.factory();

		final TextView messageText = (TextView) postView
				.findViewById(R.id.message);
		statusFeed.message = messageText.getText().toString();

		return statusFeed;
	}

	@Override
	public void updateAttributeByKey(String key, String value) {
		if (key.equals("message")) {
			this.message = value;
		}
	}

}
