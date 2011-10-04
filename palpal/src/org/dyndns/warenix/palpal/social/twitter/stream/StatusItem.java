package org.dyndns.warenix.palpal.social.twitter.stream;

import org.dyndns.warenix.palpaltwitter.R;
import org.dyndns.warenix.pattern.baseListView.ListViewItem;
import org.dyndns.warenix.widget.WebImage;

import twitter4j.Status;
import android.content.Context;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

public class StatusItem extends ListViewItem {

	@Override
	protected View createEmptyView(Context context) {
		View view = inflater.inflate(R.layout.bubble_message, null);

		ViewHolder viewHolder = new ViewHolder();
		viewHolder.username = (TextView) view.findViewById(R.id.username);
		viewHolder.postDate = (TextView) view.findViewById(R.id.postDate);
		viewHolder.message = (TextView) view.findViewById(R.id.message);
		viewHolder.profileImage = (WebImage) view
				.findViewById(R.id.profileImage);

		view.setTag(viewHolder);
		return view;
	}

	@Override
	protected View fillViewWithContent(Context context, View view) {
		ViewHolder viewHolder = (ViewHolder) view.getTag();

		viewHolder.username.setText(tweet.getUser().getScreenName());
		viewHolder.postDate.setText(tweet.getCreatedAt().toLocaleString());
		viewHolder.message.setText(tweet.getText());

		String normalProfileImageUrl = tweet.getUser().getProfileImageURL()
				.toString();
		String bigProfileImageUrl = normalProfileImageUrl
				.replace("_normal", "");
		viewHolder.profileImage.startLoading(bigProfileImageUrl);

		return view;
	}

	@Override
	public void showContextMenu(ContextMenu menu) {
		menu.setHeaderTitle("Actions");
		menu.add(0, MENU_REPLY_ALL, 0, "Reply All");
		menu.add(0, MENU_RETWEET_RT, 0, "Retweet (RT)");
		if (tweet.getInReplyToStatusId() != -1) {
			menu.add(0, MENU_CONVERSATION, 0, "Conversation");
		}
	}

	class ViewHolder {
		WebImage profileImage;
		TextView username;
		TextView postDate;
		TextView message;
	}

	Status tweet;

	public StatusItem(Status tweet) {
		this.tweet = tweet;
	}

	final int MENU_REPLY_ALL = 1;
	final int MENU_RETWEET_RT = 2;
	final int MENU_CONVERSATION = 3;
	final int MENU_VIEW_USER = 11;
	final int MENU_DIRECT_MESSAGE = 21;
	final int MENU_VIEW_USER_PROFILE = 31;

}
