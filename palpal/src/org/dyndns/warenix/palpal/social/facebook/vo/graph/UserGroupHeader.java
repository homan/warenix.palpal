package org.dyndns.warenix.palpal.social.facebook.vo.graph;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.palpal.social.facebook.FacebookException;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookUtil;
import org.dyndns.warenix.palpal.social.facebook.vo.FacebookPost;
import org.dyndns.warenix.widget.WebImage;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class UserGroupHeader extends FacebookPost {

	public static final String TYPE = "user_group_header";

	public String userId;
	public String userName;
	public String postCount;

	static class ViewHolder {

		WebImage profileImage;
		TextView username;
		TextView postCount;
	}

	public UserGroupHeader() {
		super(TYPE);
	}

	public UserGroupHeader(Parcel in) throws FacebookException {
		super(TYPE, in);
	}

	public UserGroupHeader(JSONObject post) throws FacebookException {
		super(TYPE, post);
	}

	public UserGroupHeader(String createdById, String createdByUserName,
			String postCount) {
		super(TYPE);

		this.userId = createdById;
		this.userName = createdByUserName;
		this.postCount = postCount;

		this.createdById = createdById;
		this.createdByName = createdByUserName;

		// since UserGroupHeader is usually not created by factory method
		// addCustomAction is not called. need to be called explicitly
		addCustomAction();
	}

	public static UserGroupHeader factory(JSONObject post)
			throws FacebookException {
		UserGroupHeader statusFeed = new UserGroupHeader(post);
		return statusFeed;
	}

	@Override
	public void parseJSONObject(JSONObject json) {
		userId = FacebookUtil.getJSONString(json, "id", null);
		userName = FacebookUtil.getJSONString(json, "name", null);
	}

	@Override
	public View factory(Context context, View convertView,
			HashMap<String, SoftReference<Bitmap>> imagePool) {
		View view = convertView;
		if (view == null
				|| (view != null && (view.getTag().equals(type) == false))) {
			view = inflater.inflate(R.layout.user_group_header, null);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.profileImage = (WebImage) view
					.findViewById(R.id.profileImage);
			viewHolder.username = (TextView) view.findViewById(R.id.username);
			viewHolder.postCount = (TextView) view.findViewById(R.id.postCount);
			view.setTag(R.id.picture, viewHolder);
		}

		ViewHolder viewHolder = (ViewHolder) view.getTag(R.id.picture);

		viewHolder.username.setText(userName);
		viewHolder.postCount.setText(postCount);
		String profileImageURL = FacebookUtil.getUserProfileImage(userId,
				FacebookUtil.USER_PROFILE_IMAGE_LARGE);
		viewHolder.profileImage.startLoading(profileImageURL, imagePool);
		view.setTag(type);
		return view;
	}

	public void addCustomAction() {
		// this.actionNameList.remove(0);
		this.actionNameList.add(String.format(ACTION_VIEW_USER_WALL,
				createdByName));
		this.actionLinkList.add(ACTION_VIEW_USER_WALL);

		// // this.actionNameList.remove(1);
		this.actionNameList.add(String.format(ACTION_VIEW_USER_ALBUM,
				createdByName));
		this.actionLinkList.add(ACTION_VIEW_USER_ALBUM);

		// this.actionNameList.remove(2);
		this.actionNameList.add(String
				.format(ACTION_VIEW_GROUPS, createdByName));
		this.actionLinkList.add(ACTION_VIEW_GROUPS);

		// this.actionNameList.add(String.format(ACTION_VIEW_FRIENDS,
		// createdByName));
		// this.actionLinkList.add(ACTION_VIEW_FRIENDS);

		this.actionNameList
				.add(String.format(ACTION_VIEW_LIKES, createdByName));
		this.actionLinkList.add(ACTION_VIEW_LIKES);
	}

	@Override
	public boolean handleCustomAction(Context context, int actionIndex) {
		String actionName = actionNameList.get(actionIndex);
		Log.d("palpal", String.format(
				"StatusFeed try to handle custom action %d %s", actionIndex,
				actionName));

		return false;
	}

	/*
	 * Parcelable
	 */

	public static final Parcelable.Creator<FacebookPost> CREATOR = new Parcelable.Creator<FacebookPost>() {
		public FacebookPost createFromParcel(Parcel in) {
			try {
				return new UserGroupHeader(in);
			} catch (FacebookException e) {
				return null;
			}
		}

		public FacebookPost[] newArray(int size) {
			return new UserGroupHeader[size];
		}
	};

	@Override
	public FacebookPost factory() {
		// TODO Auto-generated method stub
		return null;
	}

}
