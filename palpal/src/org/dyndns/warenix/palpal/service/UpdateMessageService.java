package org.dyndns.warenix.palpal.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dyndns.warenix.db.SimpleStorableManager;
import org.dyndns.warenix.embedly.Embedable;
import org.dyndns.warenix.embedly.EmbedlyMaster;
import org.dyndns.warenix.palpal.BubbleMessageListActivity;
import org.dyndns.warenix.palpal.PalPal;
import org.dyndns.warenix.palpal.bubbleMessage.BubbleMessage;
import org.dyndns.warenix.palpal.message.MessageDBManager;
import org.dyndns.warenix.palpal.social.twitter.TwitterBubbleMessage;
import org.dyndns.warenix.palpal.social.twitter.storable.ConversationStorable;
import org.dyndns.warenix.palpal.social.twitter.storable.EmbedableMessageStorable;
import org.dyndns.warenix.util.DownloadImageTask;
import org.dyndns.warenix.util.ToastUtil;
import org.dyndns.warenix.util.WebContent;
import org.dyndns.warenix.widget.WebImage;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

public class UpdateMessageService extends IntentService {

	public static final int NOTIFICATION_UNREAD_ID = 123456;

	public UpdateMessageService() {
		super("Update Message Service");
	}

	public UpdateMessageService(String name) {
		super(name);
	}

	static boolean isRunning = false;

	static ArrayList<BubbleMessage> messageBufferList;

	public boolean doInBackground() {
		Log.d("palpal", "doInBackground()");

		int unreadMessageCount = 0;
		Twitter twitter = PalPal.getTwitterClient();
		if (twitter == null) {
			Log.d("palpal", "no active twitter client");
			return false;
		}

		Context context = getApplicationContext();

		MessageDBManager db = new MessageDBManager(context);
		String latestPostId = db
				.getLatestSocialNetworkIdBySocialNetwork("twitter");

		ResponseList<Status> statusList;
		Paging page = new Paging();
		page.setCount(200);
		page.setPage(1);
		if (latestPostId != null) {
			page.setSinceId(Long.parseLong(latestPostId));
		}

		try {

			statusList = twitter.getHomeTimeline(page);
			for (Status status : statusList) {

				// if (latestPostDate != null &&
				// !postDate.after(latestPostDate)) {
				// break;
				// }

				String message = status.isRetweet() ? status
						.getRetweetedStatus().getText() : status.getText();
				TwitterBubbleMessage bubbleMessage = new TwitterBubbleMessage(
						status.getUser().getScreenName(), message, status
								.getUser().getProfileImageURL().toString(),
						new java.sql.Date(status.getCreatedAt().getTime()),
						"twitter", status.getId() + "");
				bubbleMessage.setInReplyToStatusId(status
						.getInReplyToStatusId());

				if (status.getInReplyToStatusId() != -1) {
					SimpleStorableManager manager = new SimpleStorableManager(
							context);
					ConversationStorable item = new ConversationStorable(
							status.getId() + "");
					manager.insertItem(item);
				}

				// check embedly
				Pattern pattern = Pattern.compile("http://(\\S+)");
				Matcher matcher = pattern.matcher(message);

				while (matcher.find()) {
					String mediaUrl = matcher.group();
					Embedable embedable = EmbedlyMaster.getEmbedable(mediaUrl);
					if (embedable != null
							&& embedable.type.equals(Embedable.TYPE_PHOTO)) {
						Log.d("warenix", String.format(
								"embedly title %s url %s", embedable.title,
								embedable.url));

						storeEmbedableMessage("" + status.getId(), embedable);

						String saveAsFileName = WebImage.hashUrl(embedable.url);
						Bitmap bitmap = null;
						try {
							Log.d("palpal", "download embedable image");
							DownloadImageTask task = new DownloadImageTask(
									null, embedable.url);
							task.execute(saveAsFileName);

							bitmap = WebContent
									.loadPhotoBitmap(new URL(embedable.url),
											DownloadImageTask.CACHE_DIR,
											saveAsFileName);

							if (bitmap != null) {
								bitmap.recycle();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				// messageBufferList.add(bubbleMessage);
				db.insertMessage(bubbleMessage);
				++unreadMessageCount;
			}

			Log.d("palpal", "fetched " + unreadMessageCount
					+ " unread messages");
			PalPal.unreadMessageCount += unreadMessageCount;

			// show notification
			if (unreadMessageCount > 0) {

				ToastUtil.showNotification(context, PalPal.unreadMessageCount
						+ " new messages", "PalPal", PalPal.unreadMessageCount
						+ " new messages", new Intent(context,
						BubbleMessageListActivity.class),
						NOTIFICATION_UNREAD_ID);
			}

		} catch (TwitterException e) {
			e.printStackTrace();
		}

		// setPeriodInSecond(getPeriodInSecond() + 60000);
		return false;
	}

	public static ArrayList<BubbleMessage> getMessageBuffererList() {
		@SuppressWarnings("unchecked")
		ArrayList<BubbleMessage> clone = (ArrayList<BubbleMessage>) messageBufferList
				.clone();
		messageBufferList.clear();
		return clone;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		doInBackground();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("palpal", "UpdateMessageService onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	void storeEmbedableMessage(String socialNetworkMessageId,
			Embedable embedable) {
		SimpleStorableManager db = new SimpleStorableManager(
				getApplicationContext());
		db.insertItem(new EmbedableMessageStorable(socialNetworkMessageId,
				embedable));
	}

}
