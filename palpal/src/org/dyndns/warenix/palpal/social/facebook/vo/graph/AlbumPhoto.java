package org.dyndns.warenix.palpal.social.facebook.vo.graph;

import java.lang.ref.SoftReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.palpal.dialog.PalPalDialog;
import org.dyndns.warenix.palpal.social.facebook.FacebookException;
import org.dyndns.warenix.palpal.social.facebook.Like;
import org.dyndns.warenix.palpal.social.facebook.activity.CommentActivity;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookUtil;
import org.dyndns.warenix.palpal.social.facebook.vo.FacebookPost;
import org.dyndns.warenix.widget.WebImage;
import org.json.JSONArray;
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

public class AlbumPhoto extends FacebookPost {

	public static final String TYPE = "photo";

	public static final String ACTION_COMMENT = "Read Comments";
	public static final String ACTION_OPEN_BROWSER = "Open in Browser";
	public static final String ACTION_ZOOM = "Zoom";

	// common
	String type = "photo_object";

	String name;
	/**
	 * The full-sized source of the photo
	 */
	String picture;
	/**
	 * The full-sized source of the photo
	 */
	String source;
	String link;
	String icon;
	String position;
	String updatedTime;

	String commentCount;

	public ArrayList<Like> likeList;

	static class ViewHolder {
		WebImage pictureImage;
		TextView createdTimeText;
		TextView nameText;
		TextView statsText;
	}

	public AlbumPhoto() {
		super(TYPE);
	}

	public AlbumPhoto(Parcel in) throws FacebookException {
		super(TYPE, in);
	}

	public AlbumPhoto(JSONObject post) throws FacebookException {
		super(TYPE, post);
	}

	public static AlbumPhoto factory(JSONObject post) throws FacebookException {
		AlbumPhoto statusFeed = new AlbumPhoto(post);
		return statusFeed;
	}

	@Override
	public void parseJSONObject(JSONObject json) {
		picture = FacebookUtil.getJSONString(json, "picture", "");
		source = FacebookUtil.getJSONString(json, "source", "");
		name = FacebookUtil.getJSONString(json, "name", "");
		link = FacebookUtil.getJSONString(json, "link", "");
		icon = FacebookUtil.getJSONString(json, "icon", "");
		updatedTime = FacebookUtil.getJSONString(json, "updated_time", "");
		position = FacebookUtil.getJSONString(json, "position", "");

		JSONObject commentJSON = FacebookUtil.getJSONObject(json, "comments",
				null);
		if (commentJSON != null) {
			JSONArray data = FacebookUtil.getJSONArray(commentJSON, "data",
					null);

			commentCount = "" + data.length();
		} else {
			commentCount = "0";
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
			view = inflater.inflate(R.layout.facebook_photo, null);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.nameText = (TextView) view.findViewById(R.id.name);
			viewHolder.pictureImage = (WebImage) view
					.findViewById(R.id.picture);
			viewHolder.createdTimeText = (TextView) view
					.findViewById(R.id.createdTime);
			viewHolder.statsText = (TextView) view.findViewById(R.id.stats);
			view.setTag(R.id.picture, viewHolder);
		}

		ViewHolder viewHolder = (ViewHolder) view.getTag(R.id.picture);

		viewHolder.nameText.setText(name);

		if (picture != null) {
			viewHolder.pictureImage.setVisibility(View.VISIBLE);
			SoftReference<Bitmap> ref = imagePool.get(source);
			if (ref == null) {
				viewHolder.pictureImage.startLoading(source, imagePool);
			} else {
				Bitmap bm = ref.get();
				if (bm == null) {
					viewHolder.pictureImage.startLoading(source, imagePool);
				} else {
					viewHolder.pictureImage.setImageBitmap(bm);
				}
			}
		} else {
			viewHolder.pictureImage.setVisibility(View.GONE);
		}

		if (likeList == null) {
			viewHolder.statsText.setText(String.format(
					"%s likes | %s comments", "?", commentCount));
		} else {
			viewHolder.statsText.setText(String.format(
					"%s likes | %s comments", likeList.size(), commentCount));
		}

		String createdTimeString = FacebookUtil
				.formatFacebookTimeToLocaleString(createdTime);
		viewHolder.createdTimeText.setText(createdTimeString);

		view.setTag(type);
		return view;
	}

	public void addCustomAction() {
		actionNameList.add(ACTION_COMMENT);
		actionLinkList.add(ACTION_COMMENT);

		actionNameList.add(ACTION_OPEN_BROWSER);
		actionLinkList.add(ACTION_OPEN_BROWSER);

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

		if (actionLink.equals(ACTION_COMMENT)
				|| actionLink.equals(ACTION_DEFAULT)) {

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
		} else if (actionName.equals(ACTION_ZOOM)) {
			try {
				PalPalDialog.showImageDialog(context,
						new URI(FacebookUtil.getLargeImage(picture)));
				return true;

			} catch (URISyntaxException e) {
				e.printStackTrace();
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
				return new AlbumPhoto(in);
			} catch (FacebookException e) {
				return null;
			}
		}

		public FacebookPost[] newArray(int size) {
			return new AlbumPhoto[size];
		}
	};

	@Override
	public FacebookPost factory() {
		// TODO Auto-generated method stub
		return null;
	}

}
