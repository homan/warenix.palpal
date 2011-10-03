package org.dyndns.warenix.palpal.social.facebook.vo.graph;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.palpal.social.facebook.FacebookException;
import org.dyndns.warenix.palpal.social.facebook.activity.NewsFeedActivity;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookUtil;
import org.dyndns.warenix.palpal.social.facebook.vo.FacebookPost;
import org.dyndns.warenix.widget.WebImage;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Filter;
import android.widget.TextView;

/**
 * a profile of a facebook friend, group & liked page
 * 
 * @author warenix
 * 
 */
public class Profile extends FacebookPost {

	public static final String TYPE = "profile";

	public String name;

	static class ViewHolder {
		TextView nameText;
		WebImage profileImage;
	}

	public Profile() {
		super(TYPE);
	}

	public Profile(Parcel in) throws FacebookException {
		super(TYPE, in);
	}

	public Profile(JSONObject post) throws FacebookException {
		super(TYPE, post);
	}

	/*
	 * Parcelable
	 */

	public static final Parcelable.Creator<Profile> CREATOR = new Parcelable.Creator<Profile>() {
		public Profile createFromParcel(Parcel in) {
			try {
				return new Profile(in);
			} catch (FacebookException e) {
				return null;
			}
		}

		public Profile[] newArray(int size) {
			return new Profile[size];
		}
	};

	@Override
	public void parseJSONObject(JSONObject post) {
		name = FacebookUtil.getJSONString(post, "name", "");

		createdById = id;
		createdByName = name;
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
			view = inflater.inflate(R.layout.facebook_profile, null);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.nameText = (TextView) view.findViewById(R.id.username);
			viewHolder.profileImage = (WebImage) view
					.findViewById(R.id.profileImage);
			view.setTag(viewHolder);
		}

		ViewHolder viewHolder = (ViewHolder) view.getTag();
		viewHolder.nameText.setText(name);

		String profileImageURL = FacebookUtil.getUserProfileImage(id,
				FacebookUtil.USER_PROFILE_IMAGE_LARGE);
		SoftReference<Bitmap> ref = imagePool.get(profileImageURL);
		if (ref == null) {
			viewHolder.profileImage.startLoading(profileImageURL, imagePool);
		} else {
			Bitmap bm = ref.get();
			if (bm == null) {
				viewHolder.profileImage
						.startLoading(profileImageURL, imagePool);
			} else {
				viewHolder.profileImage.setImageBitmap(bm);
			}
		}

		return view;
	}

	@Override
	public void addCustomAction() {
		this.actionNameList.add(String.format(ACTION_VIEW_USER_WALL,
				createdByName));
		this.actionLinkList.add(ACTION_VIEW_USER_WALL);

		this.actionNameList.add(String.format(ACTION_VIEW_USER_ALBUM,
				createdByName));
		this.actionLinkList.add(ACTION_VIEW_USER_ALBUM);
	}

	@Override
	public boolean handleCustomAction(Context context, int actionIndex) {
		String actionName = actionNameList.get(actionIndex);
		String actionLink = actionLinkList.get(actionIndex);

		if (actionName.equals(ACTION_DEFAULT)) {
			Intent intent = new Intent(context, NewsFeedActivity.class);
			Bundle extras = new Bundle();
			extras.putString(NewsFeedActivity.BUNDLE_GRAPH_PATH,
					String.format("%s/feed", id));
			intent.putExtras(extras);
			context.startActivity(intent);
			return true;
		}

		return false;
	}

	@Override
	public FacebookPost factory() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean matchFilter(CharSequence prefix) {
		return name.contains(prefix);
	}
}
