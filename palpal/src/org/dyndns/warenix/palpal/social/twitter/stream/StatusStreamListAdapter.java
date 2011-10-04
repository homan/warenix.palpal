package org.dyndns.warenix.palpal.social.twitter.stream;

import java.util.List;

import org.dyndns.warenix.palpal.bubbleMessage.BubbleMessage;
import org.dyndns.warenix.palpal.social.twitter.TwitterBubbleMessage;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;

import twitter4j.Status;
import android.app.Activity;
import android.content.Context;

public class StatusStreamListAdapter extends ListViewAdapter {

	public StatusStreamListAdapter(Context context) {
		super(context);
	}

	// adapter methods
	public void addStatus(Status tweet) {
		TwitterBubbleMessage message = new TwitterBubbleMessage(tweet.getUser()
				.getScreenName(), tweet.getText(), tweet.getUser()
				.getProfileImageURL().toString(), new java.sql.Date(tweet
				.getCreatedAt().getTime()), "twitter", tweet.getId() + "");

		itemList.add(message);
		((Activity) context).runOnUiThread(new Runnable() {
			public void run() {
				notifyDataSetChanged();
			}
		});

	}

	public void addAllStatus(List<BubbleMessage> statusList) {
		itemList.addAll(statusList);
		((Activity) context).runOnUiThread(new Runnable() {
			public void run() {
				notifyDataSetChanged();
			}
		});

	}

	public void clearStatus() {
		itemList.clear();
		((Activity) context).runOnUiThread(new Runnable() {
			public void run() {
				notifyDataSetChanged();
			}
		});
	}
}
