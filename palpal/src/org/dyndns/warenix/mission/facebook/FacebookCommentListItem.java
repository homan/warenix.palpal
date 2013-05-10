package org.dyndns.warenix.mission.facebook;

import java.util.Date;

import org.dyndns.warenix.image.CachedWebImage;
import org.dyndns.warenix.image.WebImage.WebImageListener;
import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.mission.timeline.StreamAdapter;
import org.dyndns.warenix.mission.timeline.TimelineMessageListViewItem;
import org.dyndns.warenix.mission.twitter.util.TwitterLinkify;
import org.dyndns.warenix.mission.ui.IconListView;
import org.dyndns.warenix.pattern.baseListView.IViewHolder;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;
import org.dyndns.warenix.util.ImageUtil;
import org.dyndns.warenix.util.WLog;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ContextMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FacebookCommentListItem extends TimelineMessageListViewItem {
	private static final String TAG = "FacebookCommentListItem";
	FacebookObject.Comment messageObject;

	static class ViewHolder implements IViewHolder {
		ImageView profileImage;
		TextView username;
		TextView message;
		TextView postDate;
		IconListView iconList;
		ImageView conversationMode;
		ImageView profileImage2;
		TextView username2;
		ImageView image;

		@Override
		public void releaseMemory() {

		}

		public void showUser2(boolean visible) {
			if (visible) {
				conversationMode.setVisibility(View.VISIBLE);
				profileImage2.setVisibility(View.VISIBLE);
				username2.setVisibility(View.VISIBLE);
			} else {
				conversationMode.setVisibility(View.GONE);
				profileImage2.setVisibility(View.GONE);
				username2.setVisibility(View.GONE);
			}

		}
	}

	public FacebookCommentListItem(Object messageObject, ListViewAdapter adapter) {
		super(adapter);
		this.messageObject = (FacebookObject.Comment) messageObject;
	}

	@Override
	protected View createEmptyView(Context context, int position, int type) {
		View view = null;
		view = inflater.inflate(R.layout.sample_message, null);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.username = (TextView) view.findViewById(R.id.username);
		viewHolder.profileImage = (ImageView) view
				.findViewById(R.id.profileImage);
		viewHolder.message = (TextView) view.findViewById(R.id.message);
		viewHolder.postDate = (TextView) view.findViewById(R.id.postDate);

		viewHolder.iconList = (IconListView) view.findViewById(R.id.iconList);
		viewHolder.conversationMode = (ImageView) view
				.findViewById(R.id.conversationMode);
		viewHolder.image = (ImageView) view.findViewById(R.id.image);

		RelativeLayout user2 = (RelativeLayout) view.findViewById(R.id.user2);
		viewHolder.username2 = (TextView) user2.findViewById(R.id.username);
		viewHolder.profileImage2 = (ImageView) user2
				.findViewById(R.id.profileImage);

		view.setTag(viewHolder);
		return view;
	}

	@Override
	protected View fillViewWithContent(Context context, View view,
			final int position, int type) {
		WLog.d(TAG, "fillViewWithContent " + position);
		final ViewHolder viewHolder = (ViewHolder) view.getTag();
		// init
		viewHolder.iconList.hideAll();
		viewHolder.showUser2(false);

		viewHolder.username.setText(messageObject.fromUser.name);

		String message = "";
		if (messageObject.message != null) {
			message += messageObject.message + "\n";
		}
		viewHolder.message.setText(message);
		TwitterLinkify.addTwitterLinkify(viewHolder.message);
		viewHolder.postDate
				.setText(messageObject.created_time.toLocaleString());

		if (messageObject.likeTotalCount > 0) {
			viewHolder.iconList.showLike(true, messageObject.likeTotalCount);
		}

		String profileImageUrlBig = String.format(
				"https://graph.facebook.com/%s/picture?type=normal",
				messageObject.fromUser.id);
		setProfileImage(viewHolder.profileImage, position, profileImageUrlBig);

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				WLog.d(TAG, "onClick " + messageObject.id);
			}
		});
		return view;
	}

	@Override
	public void showContextMenu(ContextMenu menu) {
		// TODO Auto-generated method stub
		WLog.d(TAG, "showContextMenu()");

	}


	@Override
	public Date getDate() {
		return messageObject.created_time;
	}

	@Override
	public int setMessageType() {
		return StreamAdapter.MESSAGE_TYPE_FACEBOOK;
	}
}
