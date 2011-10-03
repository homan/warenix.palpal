package org.dyndns.warenix.palpal.social.facebook.vo.graph;

import java.lang.ref.SoftReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.dyndns.warenix.palpal.PalPal;
import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.palpal.dialog.PalPalDialog;
import org.dyndns.warenix.palpal.social.facebook.FacebookException;
import org.dyndns.warenix.palpal.social.facebook.activity.AlbumActivity;
import org.dyndns.warenix.palpal.social.facebook.activity.AlbumPhotoActivity;
import org.dyndns.warenix.palpal.social.facebook.activity.CommentActivity;
import org.dyndns.warenix.palpal.social.facebook.activity.ShareActivity;
import org.dyndns.warenix.palpal.social.facebook.util.DialogUtil;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookUtil;
import org.dyndns.warenix.palpal.social.facebook.vo.FacebookEditable;
import org.dyndns.warenix.palpal.social.facebook.vo.FacebookPost;
import org.dyndns.warenix.widget.WebImage;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
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

public class PhotoFeed extends FacebookPost implements FacebookEditable {

	public static final String TYPE = "photo";
	static final String ACTION_VIEW_ALBUM = "View Album";
	static final String ACTION_OPEN_BROWSER = "Open in Browser";
	public static final String ACTION_SHARE = "Share";
	public static final String ACTION_ZOOM = "Zoom";

	public String link;
	public String name;
	public String picture;
	public String message;

	class ViewHolder {
		TextView messageText;
		WebImage pictureImage;
		TextView nameText;
		TextView statsText;
		TextView createdTimeText;
	}

	public PhotoFeed() {
		super(TYPE);
	}

	public PhotoFeed(Parcel in) throws FacebookException {
		super(TYPE, in);
	}

	public PhotoFeed(JSONObject post) throws FacebookException {
		super(TYPE, post);
	}

	public static PhotoFeed factory(JSONObject post) throws FacebookException {
		PhotoFeed statusFeed = new PhotoFeed(post);
		return statusFeed;
	}

	@Override
	public void parseJSONObject(JSONObject json) {
		link = FacebookUtil.getJSONString(json, "link", null);
		name = FacebookUtil.getJSONString(json, "name", null);
		picture = FacebookUtil.getJSONString(json, "picture", null);
		picture = FacebookUtil.getLargeImage(picture);

		try {
			likesCount = json.getJSONObject("likes").getString("count");
		} catch (JSONException e) {
		}
		message = FacebookUtil.getJSONString(json, "message", null);
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
			view = inflater.inflate(R.layout.photo_feed, null);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.nameText = (TextView) view.findViewById(R.id.name);
			viewHolder.pictureImage = (WebImage) view
					.findViewById(R.id.picture);
			viewHolder.messageText = (TextView) view.findViewById(R.id.message);
			viewHolder.statsText = (TextView) view.findViewById(R.id.stats);
			viewHolder.createdTimeText = (TextView) view
					.findViewById(R.id.createdTime);
			view.setTag(R.id.picture, viewHolder);
		}

		ViewHolder viewHolder = (ViewHolder) view.getTag(R.id.picture);

		viewHolder.nameText.setText(name);

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

		viewHolder.statsText.setText(String.format("%s likes | %s comments",
				likesCount, commentsCount));

		String createdTimeString = FacebookUtil
				.formatFacebookTimeToLocaleString(createdTime);
		viewHolder.createdTimeText.setText(createdTimeString);

		view.setTag(type);
		return view;
	}

	public void addCustomAction() {
		actionNameList.add(ACTION_VIEW_ALBUM);
		actionLinkList.add(ACTION_VIEW_ALBUM);

		actionNameList.add(ACTION_OPEN_BROWSER);
		actionLinkList.add(ACTION_OPEN_BROWSER);

		actionNameList.add(ACTION_SHARE);
		actionLinkList.add(ACTION_SHARE);

		actionNameList.add(ACTION_ZOOM);
		actionLinkList.add(ACTION_ZOOM);
	}

	@Override
	public boolean handleCustomAction(Context context, int actionIndex) {
		String actionName = actionNameList.get(actionIndex);
		String actionLink = actionLinkList.get(actionIndex);

		Log.d("palpal", String.format(
				"PhotoFeed try to handle custom action %d %s", actionIndex,
				actionName));

		if (actionLink.equals(ACTION_DEFAULT)) {
			Intent intent = new Intent(context, CommentActivity.class);
			Bundle extras = new Bundle();
			extras.putString(CommentActivity.BUNDLE_POST_ID, id);
			extras.putParcelable(CommentActivity.BUNDLE_FEED, this);
			intent.putExtras(extras);
			context.startActivity(intent);
		} else if (actionLink.equals(ACTION_OPEN_BROWSER)) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			Uri data = Uri.parse(link);
			intent.setData(data);
			context.startActivity(intent);
			return true;
		} else if (actionLink.equals(ACTION_VIEW_ALBUM)) {
			String albumId = FacebookUtil.getAlbumIdFromPhotoLink(link);
			Intent intent = new Intent(context, AlbumPhotoActivity.class);
			Bundle extras = new Bundle();
			extras.putString(AlbumPhotoActivity.BUNDLE_POST_ID, albumId);
			intent.putExtras(extras);
			context.startActivity(intent);
			return true;
		} else if (actionLink.equals(ACTION_SHARE)) {
			Intent intent = new Intent(context, ShareActivity.class);
			Bundle extras = new Bundle();
			extras.putParcelable(ShareActivity.BUNDLE_FEED, this);
			intent.putExtras(extras);
			context.startActivity(intent);
			return true;
		} else if (actionName.equals(ACTION_ZOOM)) {
			if (picture != null) {
				try {
					PalPalDialog.showImageDialog(context,
							new URI(FacebookUtil.getLargeImage(picture)));
					return true;

				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	/*
	 * Parcelable
	 */

	public static final Parcelable.Creator<FacebookPost> CREATOR = new Parcelable.Creator<FacebookPost>() {
		public FacebookPost createFromParcel(Parcel in) {
			try {
				return new PhotoFeed(in);
			} catch (FacebookException e) {
				return null;
			}
		}

		public FacebookPost[] newArray(int size) {
			return new PhotoFeed[size];
		}
	};

	@Override
	public FacebookPost factory() {
		PhotoFeed clone = new PhotoFeed();
		clone.name = this.name;
		clone.link = this.link;
		clone.message = this.message;
		clone.picture = this.picture;
		return clone;
	}

	boolean isCancelled;

	@Override
	public View getFacebookEditable(final FacebookPost feed,
			final View postView, final Context context,
			final HashMap<String, SoftReference<Bitmap>> imagePool) {

		final PhotoFeed self = this;

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

		final TextView nameText = (TextView) postView.findViewById(R.id.name);
		nameText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, AlbumActivity.class);
				Bundle extras = new Bundle();
				extras.putString(AlbumActivity.BUNDLE_MODE,
						AlbumActivity.MODE_CHOOSE_ALBUM);
				extras.putString(AlbumActivity.BUNDLE_USER_ID,
						PalPal.getAuthenticatedUserProfile().id);
				extras.putString(AlbumActivity.BUNDLE_USER_NAME,
						PalPal.getAuthenticatedUserProfile().name);
				intent.putExtras(extras);

				((Activity) context).startActivityForResult(intent,
						ShareActivity.REQ_CODE_CHOOSE_FACEBOOK_ALBUM);
			}
		});

		// nameText.setText("palpal");
		nameText.setBackgroundResource(R.drawable.facebook_editable_field_background);
		nameText.setHint("album name");

		isCancelled = false;

		final WebImage picture = (WebImage) postView.findViewById(R.id.picture);
		picture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
				((Activity) context).startActivityForResult(i,
						ShareActivity.REQ_CODE_PICK_IMAGE);
			}

		});

		picture.setVisibility(View.VISIBLE);

		return postView;
	}

	@Override
	public FacebookEditable getFacebookFeed(View postView) {

		// original post values
		PhotoFeed PhotoFeed = (PhotoFeed) this.factory();

		// update with new values
		final TextView messageText = (TextView) postView
				.findViewById(R.id.message);
		PhotoFeed.message = messageText.getText().toString();

		final TextView nameText = (TextView) postView.findViewById(R.id.name);
		PhotoFeed.name = nameText.getText().toString();

		return PhotoFeed;
	}

	@Override
	public void updateAttributeByKey(String key, String value) {
		if (key.equals("message")) {
			this.message = value;
		} else if (key.equals("link")) {
			this.link = value;
		} else if (key.equals("name")) {
			this.name = value;
		} else if (key.equals("picture")) {
			this.picture = value;
		}
	}
}
