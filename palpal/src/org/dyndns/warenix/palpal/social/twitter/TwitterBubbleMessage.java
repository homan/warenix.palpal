package org.dyndns.warenix.palpal.social.twitter;

import java.sql.Date;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;

import org.dyndns.warenix.db.SimpleStorable;
import org.dyndns.warenix.db.SimpleStorableManager;
import org.dyndns.warenix.google.translate.TranslationMaster;
import org.dyndns.warenix.palpal.bubbleMessage.BubbleMessage;
import org.dyndns.warenix.palpal.service.CommonTaskService;
import org.dyndns.warenix.palpal.social.twitter.activity.ComposeMessageActivity;
import org.dyndns.warenix.palpal.social.twitter.activity.ConversationActivity;
import org.dyndns.warenix.palpal.social.twitter.activity.PersonActivity;
import org.dyndns.warenix.palpal.social.twitter.activity.SearchActivity;
import org.dyndns.warenix.palpal.social.twitter.commonTask.FavouriteTwitterTask;
import org.dyndns.warenix.palpal.social.twitter.commonTask.FollowUserTask;
import org.dyndns.warenix.palpal.social.twitter.storable.ConversationStorable;
import org.dyndns.warenix.palpal.social.twitter.storable.FavouriteStorable;
import org.dyndns.warenix.palpal.social.twitter.storable.FriendStorable;
import org.dyndns.warenix.palpaltwitter.R;
import org.dyndns.warenix.widget.WebImage;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.text.util.Linkify;
import android.text.util.Linkify.TransformFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TwitterBubbleMessage extends BubbleMessage {
	public TwitterBubbleMessage(String message) {
		super(message);
	}

	public TwitterBubbleMessage(String username, String message,
			String profileImageUrl, Date postDate, String socialNetwork,
			String socialNetworkMessageId) {
		super(username, message, profileImageUrl, postDate, socialNetwork,
				socialNetworkMessageId);
	}

	// static LinkedHashMap<String, SoftReference<Bitmap>> bitmapCache = new
	// LinkedHashMap<String, SoftReference<Bitmap>>();

	public String inReplyToStatusId;
	public boolean hasConversation = true;
	public Boolean isFavourite;

	public static class ViewHolder {
		TextView username;
		TextView message;
		TextView postDate;
		WebImage profileImage;
		LinearLayout bubble;
		ImageView heart;
	}

	@Override
	protected View createEmptyView(Context context) {
		View view = inflater.inflate(R.layout.bubble_message, null);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.username = (TextView) view.findViewById(R.id.username);
		viewHolder.message = (TextView) view.findViewById(R.id.message);
		viewHolder.postDate = (TextView) view.findViewById(R.id.postDate);
		viewHolder.profileImage = (WebImage) view
				.findViewById(R.id.profileImage);
		viewHolder.bubble = (LinearLayout) view
				.findViewById(R.id.balloon_main_layout);
		viewHolder.heart = (ImageView) view.findViewById(R.id.heart);
		view.setTag(viewHolder);
		return view;
	}

	@Override
	protected View fillViewWithContent(final Context context, View view) {
		ViewHolder viewHolder;
		if (!(view.getTag() instanceof ViewHolder)) {
			view = createEmptyView(context);
		}
		viewHolder = (ViewHolder) view.getTag();
		viewHolder.username.setText(username);
		viewHolder.postDate.setText(postDate.toLocaleString());
		viewHolder.message.setText(message);
		Linkify.addLinks(viewHolder.message, Linkify.WEB_URLS);

		// A transform filter that simply returns just the text captured by the
		// first regular expression group.
		TransformFilter mentionFilter = new TransformFilter() {
			public final String transformUrl(final Matcher match, String url) {
				return match.group(1);
			}
		};

		// Match @mentions and capture just the username portion of the text.
		Pattern pattern = Pattern.compile("@([A-Za-z0-9_-]+)");
		// String scheme = "http://twitter.com/";
		String scheme = "user://";
		Linkify.addLinks(viewHolder.message, pattern, scheme, null,
				mentionFilter);

		// A transform filter that simply returns just the text captured by the
		// first regular expression group.
		TransformFilter hashtagFilter = new TransformFilter() {
			public final String transformUrl(final Matcher match, String url) {
				return match.group(0);
			}
		};

		// Match @mentions and capture just the username portion of the text.
		Pattern hashtagPattern = Pattern.compile("#(\\S+)");
		// String scheme = "http://twitter.com/";
		String hashtagScheme = "hashtag://";
		Linkify.addLinks(viewHolder.message, hashtagPattern, hashtagScheme,
				null, mentionFilter);

		viewHolder.bubble.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				QuickAction qa = new QuickAction(v);
				qa.addActionItem(factoryActionItem(context,
						QUICI_ACTION_ITEM_REPLY, qa));
				qa.addActionItem(factoryActionItem(context,
						QUICI_ACTION_ITEM_RETWEET_RT, qa));
				SimpleStorableManager manager = new SimpleStorableManager(
						context);
				SimpleStorable item = manager.getItemByKey(
						ConversationStorable.TYPE,
						ConversationStorable.getKey(socialNetworkMessageId));
				if (item != null) {
					qa.addActionItem(factoryActionItem(context,
							QUICI_ACTION_ITEM_CONVERSATION, qa));
				}
				qa.addActionItem(factoryActionItem(context,
						QUICI_ACTION_ITEM_FAVOURITE, qa));
				qa.addActionItem(factoryActionItem(context,
						QUICI_ACTION_ITEM_TRANSLATE, qa));
				qa.addActionItem(factoryActionItem(context,
						QUICI_ACTION_MONITOR_CONVERSATION, qa));
				qa.show();
			}

		});

		if (profileImageUrl != null) {
			String profileImageUrlBig = profileImageUrl.replace("_normal", "");

			viewHolder.profileImage.startLoading(profileImageUrlBig);
		}
		viewHolder.profileImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				QuickAction qa = new QuickAction(v);
				qa.addActionItem(factoryActionItem(context,
						QUICI_ACTION_ITEM_VIEW_PROFILE, qa));
				// boolean isUserfollowingMe = PalPal.getTwitterClient()
				// .existsFriendship(username, "warenix");

				SimpleStorableManager db = new SimpleStorableManager(context);

				SimpleStorable isUserFoolowingMe = db.getItemByKey(
						FriendStorable.TYPE, FriendStorable.getKey(username));

				if (isUserFoolowingMe != null) {
					qa.addActionItem(factoryActionItem(context,
							QUICI_ACTION_ITEM_DM, qa));
				} else {
				}
				// qa.addActionItem(factoryActionItem(context,
				// QUICI_ACTION_ITEM_DM, qa));
				qa.addActionItem(factoryActionItem(context,
						QUICI_ACTION_ITEM_FOLLOW, qa));
				// qa.addActionItem(factoryActionItem(context,
				// QUICI_ACTION_ITEM_BLOCK, qa));
				// qa.addActionItem(factoryActionItem(context,
				// QUICI_ACTION_ITEM_LIST, qa));
				qa.addActionItem(factoryActionItem(context,
						QUICI_ACTION_ITEM_USER_TIMELINE, qa));
				qa.show();
			}

		});

		// TODO lookup favourite in memory instead of reading database every
		// time
		if (isFavourite == null) {
			SimpleStorableManager db = new SimpleStorableManager(context);
			SimpleStorable item = db.getItemByKey(FavouriteStorable.TYPE,
					FavouriteStorable.getKey(socialNetworkMessageId));
			isFavourite = item != null;
		}
		viewHolder.heart.setVisibility(isFavourite ? View.VISIBLE : View.GONE);

		return view;
	}

	final int QUICI_ACTION_ITEM_REPLY = 10;
	final int QUICI_ACTION_ITEM_RETWEET_RT = 20;
	final int QUICI_ACTION_ITEM_FAVOURITE = 30;
	final int QUICI_ACTION_ITEM_VIEW_PROFILE = 40;
	final int QUICI_ACTION_ITEM_CONVERSATION = 50;
	final int QUICI_ACTION_ITEM_DM = 60;
	final int QUICI_ACTION_ITEM_FOLLOW = 70;
	final int QUICI_ACTION_ITEM_LIST = 80;
	final int QUICI_ACTION_ITEM_TRANSLATE = 90;
	final int QUICI_ACTION_ITEM_BLOCK = 100;
	final int QUICI_ACTION_ITEM_USER_TIMELINE = 110;
	final int QUICI_ACTION_MONITOR_CONVERSATION = 120;

	private ActionItem factoryActionItem(final Context context,
			int quickActionItemType, final QuickAction quickAction) {
		final ActionItem actionItem = new ActionItem();

		SimpleStorableManager manager = new SimpleStorableManager(context);
		SimpleStorable item;

		switch (quickActionItemType) {
		case QUICI_ACTION_ITEM_REPLY:
			actionItem.setTitle("Reply");
			actionItem.setIcon(context.getResources().getDrawable(
					R.drawable.reply));
			actionItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context,
							ComposeMessageActivity.class);
					intent.putExtra(ComposeMessageActivity.BUNDLE_MODE,
							ComposeMessageActivity.MODE_RETWEET_RT);

					ArrayList<String> participants = findParticipantsInMessage(message);
					String replyAllString = "";
					for (String participant : participants) {
						replyAllString += String.format("%s ", participant);
					}

					intent.putExtra(ComposeMessageActivity.BUNDLE_STATUS,
							String.format("%s", replyAllString));
					intent.putExtra(
							ComposeMessageActivity.BUNDLE_SOCIAL_NETWORK_ID,
							socialNetworkMessageId);

					// reply to status
					intent.putExtra(
							ComposeMessageActivity.BUNDLE_REPLY_TO_USERNAME,
							username);
					intent.putExtra(
							ComposeMessageActivity.BUNDLE_REPLY_TO_STATUS,
							message);
					intent.putExtra(
							ComposeMessageActivity.BUNDLE_REPLY_TO_POST_DATE,
							postDate.toLocaleString());
					intent.putExtra(
							ComposeMessageActivity.BUNDLE_REPLY_TO_PROFILE_IMAGE_URL,
							profileImageUrl);

					context.startActivity(intent);
					quickAction.dismiss();
				}
			});
			break;
		case QUICI_ACTION_ITEM_RETWEET_RT:
			actionItem.setTitle("Retweet (RT)");
			actionItem.setIcon(context.getResources().getDrawable(
					R.drawable.retweet));
			actionItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context,
							ComposeMessageActivity.class);
					intent.putExtra(ComposeMessageActivity.BUNDLE_MODE,
							ComposeMessageActivity.MODE_RETWEET_RT);

					intent.putExtra(ComposeMessageActivity.BUNDLE_STATUS,
							String.format("RT @%s: %s", username, message));
					intent.putExtra(
							ComposeMessageActivity.BUNDLE_SOCIAL_NETWORK_ID,
							socialNetworkMessageId);

					// reply to status
					intent.putExtra(
							ComposeMessageActivity.BUNDLE_REPLY_TO_USERNAME,
							username);
					intent.putExtra(
							ComposeMessageActivity.BUNDLE_REPLY_TO_STATUS,
							message);
					intent.putExtra(
							ComposeMessageActivity.BUNDLE_REPLY_TO_POST_DATE,
							postDate.toLocaleString());
					intent.putExtra(
							ComposeMessageActivity.BUNDLE_REPLY_TO_PROFILE_IMAGE_URL,
							profileImageUrl);

					context.startActivity(intent);
					quickAction.dismiss();
				}
			});
			break;
		case QUICI_ACTION_ITEM_FAVOURITE:
			item = manager.getItemByKey(FavouriteStorable.TYPE,
					FavouriteStorable.getKey(socialNetworkMessageId));

			actionItem.setTitle(item == null ? "Favourite" : "Unfavourite");
			actionItem.setIcon(context.getResources().getDrawable(
					R.drawable.heart));
			actionItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SimpleStorableManager manager = new SimpleStorableManager(
							context);
					SimpleStorable item = manager.getItemByKey(
							FavouriteStorable.TYPE,
							FavouriteStorable.getKey(socialNetworkMessageId));

					FavouriteTwitterTask task = new FavouriteTwitterTask(
							socialNetworkMessageId, item == null);

					Intent taskIntent = new Intent(context,
							CommonTaskService.class);
					taskIntent.putExtra(CommonTaskService.BUNDLE_TASK, task);
					context.startService(taskIntent);
					quickAction.dismiss();

				}
			});
			break;
		case QUICI_ACTION_ITEM_VIEW_PROFILE:
			actionItem.setTitle("Profile");
			actionItem.setIcon(context.getResources().getDrawable(
					R.drawable.profile));
			actionItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, "username:" + username,
							Toast.LENGTH_SHORT).show();

					Intent intent = new Intent(context, PersonActivity.class);
					intent.putExtra(PersonActivity.BUNDLE_SCREEN_NAME, username);
					context.startActivity(intent);
					quickAction.dismiss();
				}
			});
			break;
		case QUICI_ACTION_ITEM_CONVERSATION:
			actionItem.setTitle("Conversation");
			actionItem.setIcon(context.getResources().getDrawable(
					R.drawable.conversation));
			actionItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context,
							ConversationActivity.class);
					intent.putExtra(ConversationActivity.BUNDLE_STATUS_ID,
							socialNetworkMessageId);
					context.startActivity(intent);
					quickAction.dismiss();
				}
			});
			break;
		case QUICI_ACTION_ITEM_DM:
			actionItem.setTitle("DM");
			actionItem.setIcon(context.getResources().getDrawable(
					R.drawable.reply));
			actionItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, "Dashboard", Toast.LENGTH_SHORT)
							.show();
					quickAction.dismiss();
				}
			});
			break;
		case QUICI_ACTION_ITEM_FOLLOW:
			item = manager.getItemByKey(FriendStorable.TYPE,
					FriendStorable.getKey(username));

			actionItem.setTitle(item == null ? "Follow" : "Unfollow");
			actionItem.setIcon(context.getResources().getDrawable(
					R.drawable.follow));
			actionItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SimpleStorableManager manager = new SimpleStorableManager(
							context);
					SimpleStorable item = manager.getItemByKey(
							FriendStorable.TYPE,
							FriendStorable.getKey(username));
					FollowUserTask task = new FollowUserTask(username,
							item == null);

					Intent intent = new Intent(context, CommonTaskService.class);
					intent.putExtra(CommonTaskService.BUNDLE_TASK, task);
					context.startService(intent);
					quickAction.dismiss();
				}
			});
			break;
		case QUICI_ACTION_ITEM_BLOCK:
			actionItem.setTitle("Block");
			actionItem.setIcon(context.getResources().getDrawable(
					R.drawable.block));
			actionItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, "Dashboard", Toast.LENGTH_SHORT)
							.show();
					quickAction.dismiss();
				}
			});
			break;
		case QUICI_ACTION_ITEM_LIST:
			actionItem.setTitle("List");
			actionItem.setIcon(context.getResources().getDrawable(
					R.drawable.list));
			actionItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, "Dashboard", Toast.LENGTH_SHORT)
							.show();
					quickAction.dismiss();
				}
			});
			break;
		case QUICI_ACTION_ITEM_TRANSLATE:
			actionItem.setTitle("Translate");
			actionItem.setIcon(context.getResources().getDrawable(
					R.drawable.translate));
			actionItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					String translatedMessage = TranslationMaster
							.translate(message);
					// show dialog
					AlertDialog.Builder builder;
					AlertDialog alertDialog;

					builder = new AlertDialog.Builder(context);
					BubbleMessage bubbleMessage = new BubbleMessage(username,
							translatedMessage, profileImageUrl, postDate,
							socialNetwork, socialNetworkMessageId);
					View view = factory(context, bubbleMessage);
					builder.setView(view);
					alertDialog = builder.create();
					alertDialog.show();
					quickAction.dismiss();
				}
			});
			break;
		case QUICI_ACTION_ITEM_USER_TIMELINE:
			actionItem.setTitle("User Timeline");
			actionItem.setIcon(context.getResources().getDrawable(
					R.drawable.profile));
			actionItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, SearchActivity.class);
					intent.setAction(Intent.ACTION_SEARCH);
					intent.putExtra(SearchManager.EXTRA_DATA_KEY,
							SearchActivity.SEARCH_TYPE_USER_HOME_TIMELINE);
					intent.putExtra(SearchManager.QUERY, username);
					context.startActivity(intent);
					quickAction.dismiss();
				}
			});
			break;
//		case QUICI_ACTION_MONITOR_CONVERSATION:
//			actionItem.setTitle("Monitor Conversation");
//			actionItem.setIcon(context.getResources().getDrawable(
//					R.drawable.icon));
//			actionItem.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Intent intent = new Intent(context, SearchActivity.class);
//					intent.setAction(Intent.ACTION_SEARCH);
//					intent.putExtra(SearchManager.EXTRA_DATA_KEY,
//							SearchActivity.SEARCH_TYPE_MONITOR_CONVERSATION);
//					intent.putExtra(SearchManager.QUERY, username);
//					intent.putExtra(
//							ComposeMessageActivity.BUNDLE_SOCIAL_NETWORK_ID,
//							socialNetworkMessageId);
//					context.startActivity(intent);
//					quickAction.dismiss();
//				}
//			});
//			break;
		}

		return actionItem;
	}

	ArrayList<String> findParticipantsInMessage(String message) {
		ArrayList<String> participants = new ArrayList<String>();
		participants.add("@" + username);

		Pattern pattern = Pattern.compile("@([A-Za-z0-9_-]+)");
		Matcher matcher = pattern.matcher(message);
		while (matcher.find()) {
			participants.add(matcher.group());
		}

		// String[] tokens = message.split(" ");
		// for (String token : tokens) {
		// if (token.startsWith("@")) {
		// participants.add(token.split("\\p{Punct}")[1]);
		// }
		// }

		return participants;
	}

	public void setInReplyToStatusId(long inReplyToStatusId) {
		this.inReplyToStatusId = this.inReplyToStatusId + "";
		// hasConversation=true;
	}

}
