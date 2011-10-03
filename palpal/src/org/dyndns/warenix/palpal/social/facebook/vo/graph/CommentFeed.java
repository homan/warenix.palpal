package org.dyndns.warenix.palpal.social.facebook.vo.graph;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;

import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.palpal.social.facebook.FacebookException;
import org.dyndns.warenix.palpal.social.facebook.ImageAdapter;
import org.dyndns.warenix.palpal.social.facebook.Like;
import org.dyndns.warenix.palpal.social.facebook.LikeGalleryOnItemClickListener;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookAPI;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookUtil;
import org.dyndns.warenix.palpal.social.facebook.vo.FacebookPost;
import org.dyndns.warenix.widget.WebImage;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Gallery;
import android.widget.TextView;

public class CommentFeed extends FacebookPost {

	public static final String TYPE = "comment";
	String message;

	/**
	 * list of userid liked this comment
	 */
	ArrayList<Like> likeList;
	ArrayList<String> profileImageList;

	public static final String ACTION_LIKE = "Like";

	static class ViewHolder {
		TextView nameText;
		TextView commentText;
		TextView postDateText;
		WebImage pictureImage;
		Gallery likeGallery;
	}

	public CommentFeed() {
		super(TYPE);
	}

	public CommentFeed(Parcel in) throws FacebookException {
		super(TYPE, in);
	}

	public CommentFeed(JSONObject post) throws FacebookException {
		super(TYPE, post);
	}

	public static CommentFeed factory(JSONObject post) throws FacebookException {
		CommentFeed statusFeed = new CommentFeed(post);
		return statusFeed;
	}

	@Override
	public void parseJSONObject(JSONObject json) {
		message = FacebookUtil.getJSONString(json, "message", null);

		// fetch who like this
		String responseString = FacebookAPI.Feed.getAllLikes(id);
		likeList = FacebookUtil.Factory.factoryLikeList(responseString);
		likesCount = "" + likeList.size();
		profileImageList = new ArrayList<String>();
		for (Like like : likeList) {
			String likeProfileImageURL = FacebookUtil.getUserProfileImage(
					like.id, FacebookUtil.USER_PROFILE_IMAGE_SQUARE);
			profileImageList.add(likeProfileImageURL);
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
		if (view == null) {
			view = inflater.inflate(R.layout.comment, null);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.commentText = (TextView) view.findViewById(R.id.comment);
			viewHolder.nameText = (TextView) view.findViewById(R.id.username);
			viewHolder.postDateText = (TextView) view
					.findViewById(R.id.postDate);
			viewHolder.pictureImage = (WebImage) view
					.findViewById(R.id.profileImage);
			viewHolder.likeGallery = (Gallery) view
					.findViewById(R.id.like_gallery);
			view.setTag(R.id.picture, viewHolder);
		}

		ViewHolder viewHolder = (ViewHolder) view.getTag(R.id.picture);
		viewHolder.postDateText.setText(FacebookUtil
				.formatFacebookTimeToLocaleString(createdTime));

		// String profileImageURL = String.format(
		// "http://graph.facebook.com/%s/picture?type=large", createdById);
		String profileImageURL = FacebookUtil.getUserProfileImage(createdById,
				FacebookUtil.USER_PROFILE_IMAGE_LARGE);

		SoftReference<Bitmap> ref = imagePool.get(profileImageURL);
		if (ref == null) {
			viewHolder.pictureImage.startLoading(profileImageURL, imagePool);
		} else {
			Bitmap bm = ref.get();
			if (bm == null) {
				viewHolder.pictureImage
						.startLoading(profileImageURL, imagePool);
			} else {
				viewHolder.pictureImage.setImageBitmap(bm);
			}
		}

		viewHolder.nameText.setText(createdByName);
		if (likesCount.equals("0")) {
			viewHolder.commentText.setText(message);
			viewHolder.likeGallery.setVisibility(View.GONE);
		} else {
			viewHolder.commentText.setText(String.format("%s +%s", message,
					likesCount));

			if (profileImageList.size() > 0) {
				viewHolder.likeGallery.setVisibility(View.VISIBLE);
				viewHolder.likeGallery.setAdapter(new ImageAdapter(context,
						profileImageList, imagePool));

				viewHolder.likeGallery
						.setOnItemClickListener(new LikeGalleryOnItemClickListener(
								likeList));
			}
		}
		Linkify.addLinks(viewHolder.commentText, Linkify.WEB_URLS);
		return view;
	}

	public void addCustomAction() {
		actionLinkList.add(ACTION_VIEW_USER_WALL);
		actionNameList.add(String.format(ACTION_VIEW_USER_WALL, createdByName));

		actionLinkList.add(ACTION_VIEW_USER_ALBUM);
		actionNameList
				.add(String.format(ACTION_VIEW_USER_ALBUM, createdByName));

		actionNameList.add(ACTION_LIKE);
		actionLinkList.add(ACTION_LIKE);
	}

	@Override
	public boolean handleCustomAction(Context context, int actionIndex) {
		String actionName = actionNameList.get(actionIndex);
		String actionLink = actionLinkList.get(actionIndex);

		Log.d("palpal", String.format(
				"CommentFeed try to handle custom action %d %s", actionIndex,
				actionName));

		return false;
	}

	/*
	 * Parcelable
	 */

	public static final Parcelable.Creator<FacebookPost> CREATOR = new Parcelable.Creator<FacebookPost>() {
		public FacebookPost createFromParcel(Parcel in) {
			try {
				return new CommentFeed(in);
			} catch (FacebookException e) {
				return null;
			}
		}

		public FacebookPost[] newArray(int size) {
			return new CommentFeed[size];
		}
	};

	@Override
	public FacebookPost factory() {
		// TODO Auto-generated method stub
		return null;
	}

}
