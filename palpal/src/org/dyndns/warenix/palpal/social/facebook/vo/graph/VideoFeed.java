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
import org.dyndns.warenix.widget.WebImage;
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
import android.view.View.OnClickListener;
import android.widget.TextView;

public class VideoFeed extends FacebookPost implements FacebookEditable {

	public static final String TYPE = "video";
	public static final String ACTION_WATCH = "Watch";
	public static final String ACTION_SHARE = "Share";
	static final String ACTION_OPEN_BROWSER = "Open in Browser";

	public String link;
	public String name;
	public String description;
	public String picture;
	public String message;
	public String source;
	public String caption;

	public class ViewHolder {
		public TextView messageText;
		public WebImage pictureImage;
		public TextView descriptionText;
		public TextView nameText;
		public TextView captionText;
		public TextView linkText;
		public TextView sourceText;
		public TextView statsText;
		public TextView createdTimeText;
	}

	public VideoFeed() {
		super(TYPE);
	}

	public VideoFeed(Parcel in) throws FacebookException {
		super(TYPE, in);
	}

	public VideoFeed(JSONObject post) throws FacebookException {
		super(TYPE, post);
	}

	public static VideoFeed factory(JSONObject post) throws FacebookException {
		VideoFeed statusFeed = new VideoFeed(post);
		return statusFeed;
	}

	@Override
	public void parseJSONObject(JSONObject json) {
		link = FacebookUtil.getJSONString(json, "link", null);
		name = FacebookUtil.getJSONString(json, "name", null);
		description = FacebookUtil.getJSONString(json, "description", null);
		picture = FacebookUtil.getJSONString(json, "picture", null);
		picture = FacebookUtil.getLargeImage(picture);
		message = FacebookUtil.getJSONString(json, "message", null);
		source = FacebookUtil.getJSONString(json, "source", null);
		caption = FacebookUtil.getJSONString(json, "caption", null);
		try {
			likesCount = json.getJSONObject("likes").getString("count");
		} catch (JSONException e) {
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
			view = inflater.inflate(R.layout.video_feed, null);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.nameText = (TextView) view.findViewById(R.id.name);
			viewHolder.descriptionText = (TextView) view
					.findViewById(R.id.description);
			viewHolder.pictureImage = (WebImage) view
					.findViewById(R.id.picture);
			viewHolder.messageText = (TextView) view.findViewById(R.id.message);
			viewHolder.captionText = (TextView) view.findViewById(R.id.caption);
			viewHolder.linkText = (TextView) view.findViewById(R.id.link);
			viewHolder.sourceText = (TextView) view.findViewById(R.id.source);
			viewHolder.statsText = (TextView) view.findViewById(R.id.stats);
			viewHolder.createdTimeText = (TextView) view
					.findViewById(R.id.createdTime);
			view.setTag(R.id.picture, viewHolder);
		}

		ViewHolder viewHolder = (ViewHolder) view.getTag(R.id.picture);

		viewHolder.nameText.setText(name);
		viewHolder.descriptionText.setText(description);

		if (picture != null) {
			viewHolder.pictureImage.setVisibility(View.VISIBLE);

			SoftReference<Bitmap> ref = imagePool.get(picture);
			if (ref == null) {
				viewHolder.pictureImage.startLoading(picture, imagePool);
			} else {
				Bitmap bm = ref.get();
				if (bm == null) {
					viewHolder.pictureImage.startLoading(picture, imagePool);
				} else {
					viewHolder.pictureImage.setImageBitmap(bm);
				}
			}

		} else {
			viewHolder.pictureImage.setVisibility(View.GONE);
		}

		viewHolder.messageText.setText(message);

		viewHolder.captionText.setText(caption);

		viewHolder.linkText.setText(link);
		viewHolder.sourceText.setText(source);

		viewHolder.statsText.setText(String.format("%s likes | %s comments",
				likesCount, commentsCount));

		String createdTimeString = FacebookUtil
				.formatFacebookTimeToLocaleString(createdTime);
		viewHolder.createdTimeText.setText(createdTimeString);

		view.setTag(type);
		return view;
	}

	public void addCustomAction() {
		actionNameList.add(ACTION_WATCH);
		actionLinkList.add(ACTION_WATCH);

		actionNameList.add(ACTION_OPEN_BROWSER);
		actionLinkList.add(ACTION_OPEN_BROWSER);

		actionNameList.add(ACTION_SHARE);
		actionLinkList.add(ACTION_SHARE);
	}

	@Override
	public boolean handleCustomAction(Context context, int actionIndex) {
		String actionName = actionNameList.get(actionIndex);
		Log.d("palpal", String.format(
				"VideoFeed try to handle custom action %d %s", actionIndex,
				actionName));
		String actionLink = actionLinkList.get(actionIndex);
		if (actionName.equals(ACTION_DEFAULT)) {
			Intent intent = new Intent(context, CommentActivity.class);
			Bundle extras = new Bundle();
			extras.putString(CommentActivity.BUNDLE_POST_ID, id);
			extras.putParcelable(CommentActivity.BUNDLE_FEED, this);
			intent.putExtras(extras);
			context.startActivity(intent);
			return true;
		} else if (actionLink.equals(ACTION_WATCH)) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			Uri data = Uri.parse(source);
			intent.setData(data);
			context.startActivity(intent);
			return true;
		} else if (actionLink.equals(ACTION_OPEN_BROWSER)) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			Uri data = Uri.parse(link);
			intent.setData(data);
			context.startActivity(intent);
			return true;
		} else if (actionLink.equals(ACTION_SHARE)) {
			Intent intent = new Intent(context, ShareActivity.class);
			Bundle extras = new Bundle();
			extras.putParcelable(ShareActivity.BUNDLE_FEED, this);
			intent.putExtras(extras);
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
				return new VideoFeed(in);
			} catch (FacebookException e) {
				return null;
			}
		}

		public FacebookPost[] newArray(int size) {
			return new VideoFeed[size];
		}
	};

	@Override
	public FacebookPost factory() {
		VideoFeed clone = new VideoFeed();
		clone.description = this.description;
		clone.name = this.name;
		clone.caption = this.caption;
		clone.link = this.link;
		clone.source = this.source;
		clone.message = this.message;
		clone.picture = this.picture;
		return clone;
	}

	@Override
	public View getFacebookEditable(final FacebookPost feed,
			final View postView, final Context context,
			final HashMap<String, SoftReference<Bitmap>> imagePool) {

		final VideoFeed self = this;

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

		final TextView captionText = (TextView) postView
				.findViewById(R.id.caption);
		captionText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogUtil.showInputDialogForTextView(context, "caption",
						"Any tag line?", captionText, self.caption, "caption",
						self);
			}
		});
		captionText
				.setBackgroundResource(R.drawable.facebook_editable_field_background);
		captionText.setHint("caption");

		final TextView nameText = (TextView) postView.findViewById(R.id.name);
		nameText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogUtil.showInputDialogForTextView(context, "name",
						"How do you call this video?", nameText, self.name,
						"name", self);
			}
		});
		nameText.setBackgroundResource(R.drawable.facebook_editable_field_background);
		nameText.setHint("name");

		final TextView sourceText = (TextView) postView
				.findViewById(R.id.source);
		sourceText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogUtil.showInputDialogForTextView(context, "source",
						"Where to watch the video?", sourceText, self.source,
						"source", self);
			}
		});
		sourceText
				.setBackgroundResource(R.drawable.facebook_editable_field_background);
		sourceText.setHint("source");

		final TextView linkText = (TextView) postView.findViewById(R.id.link);
		linkText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogUtil.showInputDialogForTextView(context, "link",
						"What's the link?", linkText, self.link, "link", self);
			}
		});
		linkText.setBackgroundResource(R.drawable.facebook_editable_field_background);
		linkText.setHint("link");

		final TextView descriptionText = (TextView) (View) postView
				.findViewById(R.id.description);
		descriptionText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogUtil.showInputDialogForTextView(context, "description",
						"What's the video about?", descriptionText,
						self.description, "description", self);
			}
		});
		descriptionText
				.setBackgroundResource(R.drawable.facebook_editable_field_background);
		descriptionText.setHint("description");

		return postView;
	}

	@Override
	public FacebookEditable getFacebookFeed(View postView) {

		// original post values
		VideoFeed videoFeed = (VideoFeed) this.factory();

		// update with new values
		final TextView messageText = (TextView) postView
				.findViewById(R.id.message);
		videoFeed.message = messageText.getText().toString();

		final TextView nameText = (TextView) postView.findViewById(R.id.name);
		videoFeed.name = nameText.getText().toString();

		final TextView descriptionText = (TextView) postView
				.findViewById(R.id.description);
		videoFeed.description = descriptionText.getText().toString();

		final TextView captionText = (TextView) postView
				.findViewById(R.id.caption);
		videoFeed.caption = captionText.getText().toString();

		final TextView sourceText = (TextView) postView
				.findViewById(R.id.source);
		videoFeed.source = sourceText.getText().toString();

		final TextView linkText = (TextView) postView.findViewById(R.id.link);
		videoFeed.link = linkText.getText().toString();

		return videoFeed;
	}

	@Override
	public void updateAttributeByKey(String key, String value) {
		if (key.equals("message")) {
			this.message = value;
		} else if (key.equals("link")) {
			this.link = value;
		} else if (key.equals("description")) {
			this.description = value;
		} else if (key.equals("name")) {
			this.name = value;
		} else if (key.equals("picture")) {
			this.picture = value;
		} else if (key.equals("caption")) {
			this.caption = value;
		} else if (key.equals("source")) {
			this.source = value;
		}
	}
}
