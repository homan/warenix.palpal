package org.dyndns.warenix.palpal.bubbleMessage;

import java.sql.Date;

import org.dyndns.warenix.palpaltwitter.R;
import org.dyndns.warenix.pattern.baseListView.ListViewItem;
import org.dyndns.warenix.widget.WebImage;

import android.content.Context;
import android.text.Html;
import android.text.util.Linkify;
import android.view.ContextMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BubbleMessage extends ListViewItem {

	public String username;
	public String message;
	public Date postDate;
	public String profileImageUrl;
	public String socialNetwork;
	public String socialNetworkMessageId;

	public static class ViewHolder {
		TextView username;
		TextView message;
		TextView postDate;
		WebImage profileImage;
		LinearLayout bubble;
	}

	public BubbleMessage(String message) {
		this.message = message;
	}

	public BubbleMessage(String username, String message,
			String profileImageUrl, Date postDate, String socialNetwork,
			String socialNetworkMessageId) {
		this.username = username;
		this.message = message;
		this.profileImageUrl = profileImageUrl;
		this.postDate = postDate;
		this.socialNetwork = socialNetwork;
		this.socialNetworkMessageId = socialNetworkMessageId;
	}

	@Override
	protected View createEmptyView(Context context) {
		View view = inflater.inflate(R.layout.test_bubble_message, null);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.username = (TextView) view.findViewById(R.id.username);
		viewHolder.message = (TextView) view.findViewById(R.id.message);
		viewHolder.postDate = (TextView) view.findViewById(R.id.postDate);
		viewHolder.profileImage = (WebImage) view
				.findViewById(R.id.profileImage);
		viewHolder.bubble = (LinearLayout) view
				.findViewById(R.id.balloon_main_layout);
		// viewHolder.heart = (ImageView) view.findViewById(R.id.heart);
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
		viewHolder.postDate.setText(postDate != null ? postDate
				.toLocaleString() : "");
		viewHolder.message.setText(Html.fromHtml(message));
		Linkify.addLinks(viewHolder.message, Linkify.WEB_URLS);

		// // A transform filter that simply returns just the text captured by
		// the
		// // first regular expression group.
		// TransformFilter mentionFilter = new TransformFilter() {
		// public final String transformUrl(final Matcher match, String url) {
		// return match.group(1);
		// }
		// };
		//
		// // Match @mentions and capture just the username portion of the text.
		// Pattern pattern = Pattern.compile("@([A-Za-z0-9_-]+)");
		// // String scheme = "http://twitter.com/";
		// String scheme = "user://";
		// Linkify.addLinks(viewHolder.message, pattern, scheme, null,
		// mentionFilter);
		//
		// // A transform filter that simply returns just the text captured by
		// the
		// // first regular expression group.
		// TransformFilter hashtagFilter = new TransformFilter() {
		// public final String transformUrl(final Matcher match, String url) {
		// return match.group(0);
		// }
		// };
		//
		// // Match @mentions and capture just the username portion of the text.
		// Pattern hashtagPattern = Pattern.compile("#(\\S+)");
		// // String scheme = "http://twitter.com/";
		// String hashtagScheme = "hashtag://";
		// Linkify.addLinks(viewHolder.message, hashtagPattern, hashtagScheme,
		// null, mentionFilter);

		viewHolder.bubble.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBubbleClicked(v);
			}

		});

		if (profileImageUrl != null) {
			String profileImageUrlBig = profileImageUrl.replace("_normal", "");

			viewHolder.profileImage.startLoading(profileImageUrlBig);
		}
		viewHolder.profileImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				onProfileImageClicked(v);
			}

		});

		return view;
	}

	public static View factory(Context context, BubbleMessage bubbleMessage) {
		View view = bubbleMessage.createEmptyView(context);
		view = bubbleMessage.fillViewWithContent(context, view);
		return view;
	}

	protected void onBubbleClicked(View v) {

	}

	protected void onProfileImageClicked(View v) {

	}

	@Override
	public void showContextMenu(ContextMenu menu) {

	}
}
