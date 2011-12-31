package org.dyndns.warenix.mission.ui;

import org.dyndns.warenix.lab.compat1.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IconListView extends LinearLayout {

	ImageView favourite;
	ImageView map;
	ImageView reply;
	TextView replyCount;
	ImageView like;
	TextView likeCount;
	ImageView alert;

	public IconListView(Context context) {
		super(context);
	}

	public IconListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public IconListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	protected void onFinishInflate() {
		super.onFinishInflate();

		setupElements();
	}

	void setupElements() {
		favourite = (ImageView) findViewById(R.id.favourite);
		map = (ImageView) findViewById(R.id.map);
		reply = (ImageView) findViewById(R.id.reply);
		replyCount = (TextView) findViewById(R.id.replyCount);
		like = (ImageView) findViewById(R.id.like);
		likeCount = (TextView) findViewById(R.id.likeCount);
		alert = (ImageView) findViewById(R.id.alert);

		hideAll();
	}

	public void showFavourite(boolean visible) {
		favourite.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	public void showMap(boolean visible) {
		map.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	public void showLike(boolean visible, long count) {
		like.setVisibility(visible ? View.VISIBLE : View.GONE);
		likeCount.setVisibility(visible ? View.VISIBLE : View.GONE);
		if (visible) {
			likeCount.setText("" + count);
		}
	}

	public void showReply(boolean visible, long count) {
		reply.setVisibility(visible ? View.VISIBLE : View.GONE);
		replyCount.setVisibility(visible ? View.VISIBLE : View.GONE);
		if (visible) {
			replyCount.setText("" + count);
		}
	}

	public void showAlert(boolean visible) {
		alert.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	public void hideAll() {
		showLike(false, 0);
		showReply(false, 0);
		showFavourite(false);
		showMap(false);
		showAlert(false);
	}

}
