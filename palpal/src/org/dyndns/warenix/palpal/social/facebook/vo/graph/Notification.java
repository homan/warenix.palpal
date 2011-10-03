package org.dyndns.warenix.palpal.social.facebook.vo.graph;

import java.lang.ref.SoftReference;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.palpal.social.facebook.FacebookException;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookAPI;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookUtil;
import org.dyndns.warenix.palpal.social.facebook.vo.FacebookPost;
import org.dyndns.warenix.util.ToastUtil;
import org.dyndns.warenix.widget.WebImage;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

public class Notification extends FacebookPost {
	public static final String TYPE = "notification";

	public String notificationId;

	public String senderId;
	public String title;
	public String body;
	public String href;

	public String profileImageURL;
	public String senderProfileName;

	public String objectId;

	/**
	 * timestamp
	 */
	String createdTime;

	static class ViewHolder {
		WebImage profileImage;
		TextView username;
		TextView postDate;
		TextView title;
		TextView body;
	}

	public Notification() {
		super(TYPE);
	}

	public Notification(Parcel in) throws FacebookException {
		super(TYPE, in);
	}

	public Notification(JSONObject post) throws FacebookException {
		super(TYPE, post);
	}

	public String getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String toString() {
		return String.format("[%s] wrote [%s] [%s] [%s]", senderId, title,
				body, href);
	}

	public View factory(Context context, View convertView,
			HashMap<String, SoftReference<Bitmap>> imagePool) {
		View view = convertView;
		if (view == null
				|| (view != null && (view.getTag().equals(type) == false))) {
			view = inflater.inflate(R.layout.notification, null);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.profileImage = (WebImage) view
					.findViewById(R.id.profileImage);
			viewHolder.username = (TextView) view.findViewById(R.id.username);
			viewHolder.postDate = (TextView) view.findViewById(R.id.postDate);
			viewHolder.title = (TextView) view.findViewById(R.id.title);
			viewHolder.body = (TextView) view.findViewById(R.id.body);

			view.setTag(R.id.picture, viewHolder);
		}

		ViewHolder viewHolder = (ViewHolder) view.getTag(R.id.picture);

		viewHolder.profileImage.startLoading(profileImageURL, imagePool);
		viewHolder.username.setText(senderProfileName);
		// convert createdTime in second to millionsecond
		Timestamp timestamp = new Timestamp(Long.parseLong(createdTime) * 1000);
		Date createdTimeDate = new Date(timestamp.getTime());
		viewHolder.postDate.setText(createdTimeDate.toLocaleString());
		viewHolder.title.setText(title);
		viewHolder.body.setText(body);
		view.setTag(type);
		return view;
	}

	@Override
	public void parseJSONObject(JSONObject post) throws FacebookException {
		notificationId = FacebookUtil
				.getJSONString(post, "notification_id", "");
		senderId = FacebookUtil.getJSONString(post, "sender_id", "");
		createdTime = FacebookUtil.getJSONString(post, "created_time", "");
		title = FacebookUtil.getJSONString(post, "title_text", "");
		body = FacebookUtil.getJSONString(post, "body_text", "");
		href = FacebookUtil.getJSONString(post, "href", "");

		objectId = FacebookUtil.getJSONString(post, "object_id", "");

		String responseString;
		responseString = FacebookAPI.getProfile(senderId);

		if (profileImageURL == null) {
			try {
				Profile senderProfile = new Profile(new JSONObject(
						responseString));
				profileImageURL = FacebookUtil
						.getUserProfileImage(senderProfile.id,
								FacebookUtil.USER_PROFILE_IMAGE_LARGE);

				senderProfileName = senderProfile.name;

			} catch (JSONException e) {
			}
		}
	}

	@Override
	public void addCustomAction() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean handleCustomAction(Context context, int actionIndex) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * Parcelable
	 */

	public static final Parcelable.Creator<FacebookPost> CREATOR = new Parcelable.Creator<FacebookPost>() {
		public FacebookPost createFromParcel(Parcel in) {
			try {
				return new Notification(in);
			} catch (FacebookException e) {
				// TODO Auto-generated catch block
				return null;
			}
		}

		public FacebookPost[] newArray(int size) {
			return new Notification[size];
		}
	};

	@Override
	public FacebookPost factory() {
		// TODO Auto-generated method stub
		return null;
	}

}
