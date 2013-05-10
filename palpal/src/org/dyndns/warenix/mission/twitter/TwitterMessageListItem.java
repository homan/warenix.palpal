package org.dyndns.warenix.mission.twitter;

import java.util.Date;

import org.dyndns.warenix.image.CachedWebImage;
import org.dyndns.warenix.image.WebImage.WebImageListener;
import org.dyndns.warenix.mission.facebook.LinkPreview;
import org.dyndns.warenix.mission.facebook.util.FacebookMaster;
import org.dyndns.warenix.mission.timeline.StreamAdapter;
import org.dyndns.warenix.mission.timeline.TimelineMessageListViewItem;
import org.dyndns.warenix.mission.twitter.util.TwitterLinkify;
import org.dyndns.warenix.mission.ui.IconListView;
import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.palpal.app.ReplyActivity;
import org.dyndns.warenix.palpal.intent.PalPalIntent;
import org.dyndns.warenix.pattern.baseListView.IViewHolder;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;
import org.dyndns.warenix.util.WLog;
import org.dyndns.warenix.widget.actionpopup.ActionPopup;

import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.URLEntity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TwitterMessageListItem extends TimelineMessageListViewItem {
	private static final String TAG = "TwitterMessageListItem";

	ListViewAdapter adapter;
	twitter4j.Status messageObject;

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
		ImageView coverImage;

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

	public TwitterMessageListItem(Object messageObject, ListViewAdapter adapter) {
		this.messageObject = (Status) messageObject;
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
		viewHolder.coverImage = (ImageView) view.findViewById(R.id.coverImage);
		viewHolder.message = (TextView) view.findViewById(R.id.message);
		viewHolder.postDate = (TextView) view.findViewById(R.id.postDate);

		viewHolder.iconList = (IconListView) view.findViewById(R.id.iconList);
		viewHolder.conversationMode = (ImageView) view
				.findViewById(R.id.conversationMode);
		RelativeLayout user2 = (RelativeLayout) view.findViewById(R.id.user2);
		viewHolder.username2 = (TextView) user2.findViewById(R.id.username);
		viewHolder.profileImage2 = (ImageView) user2
				.findViewById(R.id.profileImage);
		viewHolder.image = (ImageView) view.findViewById(R.id.image);
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

		viewHolder.username.setText(messageObject.getUser().getScreenName());
		viewHolder.message.setText(messageObject.getText());
		TwitterLinkify.addTwitterLinkify(viewHolder.message);
		viewHolder.postDate.setText(messageObject.getCreatedAt()
				.toLocaleString());

		if (messageObject.getInReplyToStatusId() != -1) {
			viewHolder.iconList.showReply(true, 1);
		}

		if (messageObject.getPlace() != null) {
			viewHolder.iconList.showMap(true);
		}

		if (messageObject.isRetweet()) {
			viewHolder.showUser2(true);
			Status retweetedStatus = messageObject.getRetweetedStatus();
			viewHolder.username2.setText(retweetedStatus.getUser()
					.getScreenName());

			String profileImageUrlBig = messageObject.getRetweetedStatus()
					.getUser().getProfileImageURL().toString()
					.replace("_normal", "");
			setProfileImage(viewHolder.profileImage2, position,
					profileImageUrlBig);
		}

		MediaEntity[] mediaEntities = messageObject.getMediaEntities();
		if (mediaEntities != null && mediaEntities.length > 0) {
			viewHolder.image.setVisibility(View.VISIBLE);
			setProfileImage(viewHolder.image, position, mediaEntities[0]
					.getMediaURL().toString());
		} else {
			// use facebook to get preview
			URLEntity[] urlEntities = messageObject.getURLEntities();
			if (urlEntities != null && urlEntities.length > 0) {
				viewHolder.image.setVisibility(View.VISIBLE);
				final String linkUrl = urlEntities[0].getDisplayURL()
						.toString();

				new Thread() {
					public void run() {
						String response = FacebookMaster
								.getLinkpreview(linkUrl);
						final LinkPreview linkPreview = new LinkPreview(
								response);
						viewHolder.image.post(new Runnable() {
							public void run() {
								if (linkPreview.previewImageList != null) {
									setProfileImage(viewHolder.image, position,
											linkPreview.previewImageList.get(0));
								} else {
									viewHolder.image.setVisibility(View.GONE);
								}
							}
						});

					}
				}.start();
				viewHolder.image.setImageResource(R.drawable.ic_launcher);
			} else {
				viewHolder.image.setVisibility(View.GONE);
			}
		}

		String profileImageUrlBig = messageObject.getUser()
				.getProfileImageURL().toString().replace("_normal", "");
		setProfileImage(viewHolder.profileImage, position, profileImageUrlBig);
		setProfileImage(viewHolder.coverImage, position, messageObject
				.getUser().getProfileBackgroundImageUrl());

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
										ReplyActivity.PARAM_TWITTER_CONVERSATION_ADAPTER);
								intent.putExtra(
										ReplyActivity.BUNDLE_MESSAGE_OBJECT,
										messageObject);

								context.startActivity(intent);
							}
						});

				actionPopup.addAction(context, "Retweet", null);
				if (messageObject.isFavorited()) {
					actionPopup.addAction(context, "Unfavourite", null);
				} else {
					actionPopup.addAction(context, "Favourite", null);
				}
				actionPopup.addAction(context, "Quote Tweet",
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								// Intent intent = new Intent(context,
								// ComposeActivity.class);
								Intent intent = new Intent(
										PalPalIntent.ACTION_TWITTER_QUOTE_TWEET);
								intent.putExtra("message", messageObject);

								context.startActivity(intent);
							}
						});

				if (messageObject.getInReplyToStatusId() != -1) {
					actionPopup.addAction(context, "View Conversation",
							new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									Intent intent = new Intent(context,
											ReplyActivity.class);
									intent.putExtra(
											ReplyActivity.BUNDLE_LIST_VIEW_ADAPTER,
											ReplyActivity.PARAM_TWITTER_CONVERSATION_ADAPTER);
									intent.putExtra(
											ReplyActivity.BUNDLE_MESSAGE_OBJECT,
											messageObject);

									context.startActivity(intent);
								}
							});
				}

				if (messageObject.getPlace() != null) {
					actionPopup.addAction(context, "View location", null);
				}

				actionPopup.addAction(context, "Open in Browser",
						new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								String url = String
										.format("https://twitter.com/#!/%s/status/%d",
												messageObject.getUser()
														.getScreenName(),
												messageObject.getId());
								Intent intent = new Intent(Intent.ACTION_VIEW);
								intent.setData(Uri.parse(url));

								context.startActivity(intent);
							}
						});

				actionPopup.showPopupInScreenCenter(v);
			}
		});
		return view;
	}

	@Override
	public void showContextMenu(ContextMenu menu) {
		// TODO Auto-generated method stub
		WLog.d(TAG, "showContextMenu()");

	}

	public void setProfileImage(final ImageView imageView, final int position,
			String imageUrl) {

		if (!adapter.isIdle()) {
			imageView.setImageResource(R.drawable.ic_launcher);
			WLog.d(TAG, "warenix, list is not ready, skip " + position);
			return;
		}
		WLog.d(TAG, "warenix, setProfile " + position);
		imageView.setImageResource(R.drawable.ic_launcher);
		CachedWebImage webImage2 = new CachedWebImage();
		webImage2.setWebImageListener(new WebImageListener() {

			@Override
			public void onImageSet(ImageView image, Bitmap bitmap) {
				// if (adapter.isChildVisible(position)) {
				WLog.d(TAG, "onImageSet for position " + position
						+ " set bitmap");
				imageView.setImageBitmap(bitmap);
				// } else {
				// WLog.d(TAG, "onImageSet for position " + position
				// + " recycle bitmap");
				// ImageUtil.recycleBitmap(bitmap);
				// }
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
