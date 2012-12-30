package org.dyndns.warenix.mission.twitter;

import java.util.Date;

import org.dyndns.warenix.image.CachedWebImage;
import org.dyndns.warenix.image.WebImage.WebImageListener;
import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.palpal.app.ReplyActivity;
import org.dyndns.warenix.mission.timeline.StreamAdapter;
import org.dyndns.warenix.mission.timeline.TimelineMessageListViewItem;
import org.dyndns.warenix.mission.twitter.util.TwitterLinkify;
import org.dyndns.warenix.mission.ui.IconListView;
import org.dyndns.warenix.pattern.baseListView.IViewHolder;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;
import org.dyndns.warenix.util.ImageUtil;
import org.dyndns.warenix.util.WLog;
import org.dyndns.warenix.widget.actionpopup.ActionPopup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TwitterDirectMessageListItem extends TimelineMessageListViewItem {
	private static final String TAG = "TwitterDirectMessageListItem";

	ListViewAdapter adapter;
	twitter4j.DirectMessage messageObject;

	static class ViewHolder implements IViewHolder {
		ImageView profileImage;
		TextView username;
		TextView message;
		TextView postDate;
		IconListView iconList;
		ImageView conversationMode;
		ImageView profileImage2;
		TextView username2;

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

	public TwitterDirectMessageListItem(Object messageObject,
			ListViewAdapter adapter) {
		this.messageObject = (twitter4j.DirectMessage) messageObject;
		this.adapter = adapter;
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
		final ViewHolder viewHolder = (ViewHolder) view.getTag();
		// init
		viewHolder.iconList.hideAll();
		viewHolder.showUser2(false);

		viewHolder.username.setText(messageObject.getSenderScreenName());
		viewHolder.message.setText(messageObject.getText());
		TwitterLinkify.addTwitterLinkify(viewHolder.message);
		viewHolder.postDate.setText(messageObject.getCreatedAt()
				.toLocaleString());

		String profileImageUrlBig = messageObject.getSender()
				.getProfileImageURL().toString().replace("_normal", "");
		setProfileImage(viewHolder.profileImage, position, profileImageUrlBig);

		view.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final Context context = v.getContext();
				ActionPopup actionPopup = new ActionPopup(inflater);
				actionPopup.addAction(context, "Reply",
						new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent(context,
										ReplyActivity.class);
								intent.putExtra(
										ReplyActivity.BUNDLE_LIST_VIEW_ADAPTER,
										ReplyActivity.PARAM_TWITTER_DIRECT_MESSAGE_ADAPTER);
								intent.putExtra(
										ReplyActivity.BUNDLE_MESSAGE_OBJECT,
										messageObject);

								context.startActivity(intent);
							}
						});

				actionPopup.addAction(context, "Delete",
						new View.OnClickListener() {

							@Override
							public void onClick(View v) {
							}
						});

				actionPopup.showPopupInScreenCenter(v);
			}
		});
		return view;
	}

	@Override
	public void showContextMenu(ContextMenu menu) {
		WLog.d(TAG, "showContextMenu()");

	}

	public void setProfileImage(final ImageView imageView, final int position,
			String imageUrl) {
		imageView.setImageResource(R.drawable.ic_launcher);
		CachedWebImage webImage2 = new CachedWebImage();
		webImage2.setWebImageListener(new WebImageListener() {

			@Override
			public void onImageSet(ImageView image, Bitmap bitmap) {
				if (adapter.isChildVisible(position)) {
					WLog.d(TAG, "onImageSet for position " + position
							+ " set bitmap");
					imageView.setImageBitmap(bitmap);
				} else {
					WLog.d(TAG, "onImageSet for position " + position
							+ " recycle bitmap");
					ImageUtil.recycleBitmap(bitmap);
				}
			}

			@Override
			public void onImageSet(ImageView image) {
			}
		});

		webImage2.startDownloadImage("" + position, imageUrl, imageView, null);
	}

	@Override
	public Date getDate() {
		return messageObject.getCreatedAt();
	}

	@Override
	public int setMessageType() {
		return StreamAdapter.MESSAGE_TYPE_TWITTER;
	}
}
