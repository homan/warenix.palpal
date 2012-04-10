package org.dyndns.warenix.mission.facebook;

import java.util.Date;

import org.dyndns.warenix.image.CachedWebImage;
import org.dyndns.warenix.image.WebImage.WebImageListener;
import org.dyndns.warenix.lab.compat1.R;
import org.dyndns.warenix.lab.compat1.app.PhotoActivity;
import org.dyndns.warenix.lab.compat1.app.ReplyActivity;
import org.dyndns.warenix.lab.compat1.util.Memory;
import org.dyndns.warenix.lab.taskservice.TaskService;
import org.dyndns.warenix.mission.facebook.backgroundtask.LikePostBackgroundTask;
import org.dyndns.warenix.mission.facebook.util.FacebookMaster;
import org.dyndns.warenix.mission.timeline.StreamAdapter;
import org.dyndns.warenix.mission.timeline.TimelineMessageListViewItem;
import org.dyndns.warenix.mission.twitter.util.TwitterLinkify;
import org.dyndns.warenix.mission.ui.IconListView;
import org.dyndns.warenix.palpal.intent.PalPalIntent;
import org.dyndns.warenix.pattern.baseListView.IViewHolder;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;
import org.dyndns.warenix.util.ImageUtil;
import org.dyndns.warenix.util.WLog;
import org.dyndns.warenix.widget.actionpopup.ActionPopup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FacebookMessageListItem extends TimelineMessageListViewItem {
	private static final String TAG = "FacebookMessageListItem";
	ListViewAdapter adapter;
	FacebookObject messageObject;

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

	public FacebookMessageListItem(Object messageObject, ListViewAdapter adapter) {
		this.messageObject = (FacebookObject) messageObject;
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
		viewHolder.image = (ImageView) view.findViewById(R.id.image);

		RelativeLayout user2 = (RelativeLayout) view.findViewById(R.id.user2);
		viewHolder.username2 = (TextView) user2.findViewById(R.id.username);
		viewHolder.profileImage2 = (ImageView) user2
				.findViewById(R.id.profileImage);

		view.setTag(viewHolder);
		return view;
	}

	@Override
	protected View fillViewWithContent(Context context, final View view,
			final int position, int type) {
		WLog.d(TAG, "fillViewWithContent " + position);
		final ViewHolder viewHolder = (ViewHolder) view.getTag();
		// init
		viewHolder.iconList.hideAll();
		viewHolder.showUser2(false);

		if (messageObject.error != null) {
			// display error
			viewHolder.username.setText("Error");
			viewHolder.message.setText(messageObject.error.message);
			viewHolder.postDate.setText(new Date().toLocaleString());
			return view;
		}

		if (messageObject.fromUser != null) {
			viewHolder.username.setText(messageObject.fromUser.name);
		}

		String message = "";
		if (messageObject.message != null) {
			message += messageObject.message + "\n";
		}
		if (messageObject.story != null) {
			message += messageObject.story + "\n";
		}
		if (messageObject.title != null) {
			message += messageObject.title + "\n";
		}
		if (messageObject.name != null) {
			message += messageObject.name + "\n";
		}
		if (messageObject.caption != null) {
			message += messageObject.caption + "\n";
		}
		if (messageObject.count != 0) {
			message += "x" + messageObject.count + "\n";
		}
		viewHolder.message.setText(message);
		TwitterLinkify.addTwitterLinkify(viewHolder.message);
		if (messageObject.updated_time != null) {
			viewHolder.postDate.setText(messageObject.updated_time
					.toLocaleString());
		}

		if (messageObject.commentTotalCount > 0) {
			viewHolder.iconList
					.showReply(true, messageObject.commentTotalCount);
		}
		if (messageObject.likeTotalCount > 0) {
			viewHolder.iconList.showLike(true, messageObject.likeTotalCount);
		}
		if (messageObject.place != null) {
			viewHolder.iconList.showMap(true);
		}

		viewHolder.iconList.showAlert(messageObject.unread == 1);

		if (messageObject.picture != null) {
			viewHolder.image.setVisibility(View.VISIBLE);
			setProfileImage(viewHolder.image, position,
					FacebookMaster.getLargeImage(messageObject.picture));
		} else if (messageObject.coverPhoto != null) {
			viewHolder.image.setVisibility(View.VISIBLE);
			setProfileImage(
					viewHolder.image,
					position,
					FacebookMaster.getLargeImage(String
							.format("https://graph.facebook.com/%s/picture?access_token=%s",
									messageObject.coverPhoto, Memory
											.getFacebookClient()
											.getAccessToken())));
		} else {
			viewHolder.image.setVisibility(View.GONE);
		}

		if (messageObject.toUserList != null) {
			viewHolder.showUser2(true);
			String toUser = messageObject.toUserList.get(0).name;
			if (messageObject.toUserList.size() > 1) {
				toUser += " (" + messageObject.toUserList.size() + ")";
			}
			String profileImageUrlBig = String.format(
					"https://graph.facebook.com/%s/picture?type=normal",
					messageObject.toUserList.get(0).id);

			viewHolder.username2.setText(toUser);
			setProfileImage(viewHolder.profileImage2, position,
					profileImageUrlBig);
		} else if (messageObject.storyTagList != null) {
			// if (!messageObject.storyTagList.get(0).id
			// .equals(messageObject.fromUser.id)) {
			viewHolder.showUser2(true);
			String toUser = messageObject.storyTagList.get(0).name;
			if (messageObject.storyTagList.size() > 1) {
				toUser += " (" + messageObject.storyTagList.size() + ")";
			}
			String profileImageUrlBig = String.format(
					"https://graph.facebook.com/%s/picture?type=normal",
					messageObject.storyTagList.get(0).id);

			viewHolder.username2.setText(toUser);
			setProfileImage(viewHolder.profileImage2, position,
					profileImageUrlBig);
			// }
		}

		// if (messageObject.getInReplyToStatusId() != -1) {
		// viewHolder.iconList.showReply(true, 1);
		// }
		//
		// if (messageObject.getPlace() != null) {
		// viewHolder.iconList.showMap(true);
		// }
		//
		// if (messageObject.isRetweet()) {
		// viewHolder.showUser2(true);
		// Status retweetedStatus = messageObject.getRetweetedStatus();
		// viewHolder.username2.setText(retweetedStatus.getUser()
		// .getScreenName());
		//
		// String profileImageUrlBig = messageObject.getRetweetedStatus()
		// .getUser().getProfileImageURL().toString()
		// .replace("_normal", "");
		// setProfileImage(viewHolder.profileImage2, position,
		// profileImageUrlBig);
		// }
		//
		if (messageObject.fromUser != null) {
			String profileImageUrlBig = String.format(
					"https://graph.facebook.com/%s/picture?type=normal",
					messageObject.fromUser.id);
			setProfileImage(viewHolder.profileImage, position,
					profileImageUrlBig);
		}

		view.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				final Context context = v.getContext();

				final Handler handler = new Handler() {
					@Override
					public void handleMessage(Message message) {
						ActionPopup actionPopup = new ActionPopup(inflater,
								false);
						if (messageObject.fromUser != null) {
							actionPopup.addAction(context, "Open",
									new View.OnClickListener() {

										@Override
										public void onClick(View v) {
											Intent intent = new Intent(context,
													ReplyActivity.class);
											intent.putExtra(
													ReplyActivity.BUNDLE_LIST_VIEW_ADAPTER,
													ReplyActivity.PARAM_FACEBOOK_POST_ADAPTER);
											intent.putExtra(
													ReplyActivity.BUNDLE_FACEBOOK_GRAPH_ID,
													messageObject.id);

											context.startActivity(intent);
										}
									});
						}

						final String graphId = (String) message.obj;

						if (graphId != null) {
							actionPopup.addAction(context, "Read Post",
									new View.OnClickListener() {

										@Override
										public void onClick(View v) {
											Intent intent = new Intent(context,
													ReplyActivity.class);
											intent.putExtra(
													ReplyActivity.BUNDLE_LIST_VIEW_ADAPTER,
													ReplyActivity.PARAM_FACEBOOK_POST_ADAPTER);
											intent.putExtra(
													ReplyActivity.BUNDLE_FACEBOOK_GRAPH_ID,
													graphId);

											context.startActivity(intent);
										}
									});
						}

						if (messageObject.link != null) {
							actionPopup.addAction(context,
									"Open Link in Browser",
									new View.OnClickListener() {

										@Override
										public void onClick(View v) {
											String url = messageObject.link;
											Intent intent = new Intent(
													Intent.ACTION_VIEW);
											intent.setData(Uri.parse(url));

											context.startActivity(intent);
										}
									});

							actionPopup.addAction(context, "View Album",
									new View.OnClickListener() {

										@Override
										public void onClick(View v) {
											String albumGraphId = FacebookMaster
													.determineAlbumGraphIdFromLink(messageObject);

											Intent intent = new Intent(context,
													PhotoActivity.class);
											intent.putExtra(
													PhotoActivity.BUNDLE_GRAPH_ID,
													albumGraphId);
											intent.putExtra(
													PhotoActivity.BUNDLE_PAGE_COUNT,
													10);

											context.startActivity(intent);
										}
									});
						}
						actionPopup.addAction(context, "Like",
								new View.OnClickListener() {
									public void onClick(View v) {
										// viewHolder.iconList.showLike(true,
										// 1);
										TaskService.addBackgroundTask(context,
												new LikePostBackgroundTask(
														messageObject.id));
									}
								});
						actionPopup.addAction(context, "Comment", null);
						if (messageObject.toUserList != null) {
							actionPopup.addAction(context,
									"View Tagged People", null);
						} else if (messageObject.storyTagList != null) {
							actionPopup.addAction(context,
									"View Tagged People", null);
						}
						if (messageObject.place != null) {
							actionPopup.addAction(context, "View Location",
									null);
						}
						actionPopup.addAction(context, "Share",
								new View.OnClickListener() {
									public void onClick(View v) {
										Intent intent = new Intent(
												PalPalIntent.ACTION_FACEBOOK_RESHARE_POST);
										intent.putExtra("message",
												messageObject);
										context.startActivity(intent);
									}
								});

						actionPopup.showPopupInScreenCenter(v);
					}
				};

				(new Thread(new Runnable() {

					@Override
					public void run() {
						Message msg = handler.obtainMessage();

						String graphId = FacebookMaster
								.determineGraphId(messageObject);

						msg.obj = graphId;

						handler.sendMessage(msg);
					}
				})).start();

			}
		});

		view.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				return false;
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
		return messageObject.created_time;
	}

	@Override
	public int setMessageType() {
		return StreamAdapter.MESSAGE_TYPE_FACEBOOK;
	}
}
