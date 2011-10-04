package org.dyndns.warenix.palpal;

import java.util.Date;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;

import org.dyndns.warenix.palpal.bubbleMessage.BubbleMessageListController;
import org.dyndns.warenix.palpal.service.UpdateMessageService;
import org.dyndns.warenix.palpal.social.twitter.activity.AccountActivity;
import org.dyndns.warenix.palpal.social.twitter.activity.ComposeMessageActivity;
import org.dyndns.warenix.palpal.social.twitter.activity.DirectMessageActivity;
import org.dyndns.warenix.palpal.social.twitter.activity.ImageAlbumActivity;
import org.dyndns.warenix.palpal.social.twitter.activity.ImageGalleryActivity;
import org.dyndns.warenix.palpal.social.twitter.activity.MentionMessageActivity;
import org.dyndns.warenix.palpaltwitter.R;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.readystatesoftware.viewbadger.BadgeView;

public class BubbleMessageListActivity extends Activity {

	BubbleMessageListController controller;
	Button refresh;
	BadgeView badge;

	// BubbleMessageListAdapter adapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_list_activity);

		Log.d("palpal", "onCreate()");
		setupAction();

		controller = new BubbleMessageListController(this, R.id.listView1);

		refresh = (Button) findViewById(R.id.refresh);
		refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// refresh.setVisibility(View.GONE);
				if (PalPal.unreadMessageCount > 0) {
					refresh();
				}
			}

		});

		Button add = (Button) findViewById(R.id.add);
		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						ComposeMessageActivity.class));
			}
		});

		restoreTwitterClient();
		startUpdateMessageService();
		onReady();
	}

	protected void onResume() {
		super.onResume();
		Log.d("palpal", "onResume()");

		if (PalPal.unreadMessageCount > 0) {
			View activityTitle = findViewById(R.id.refresh);
			badge = new BadgeView(this, activityTitle);
			badge.setText("" + PalPal.unreadMessageCount);
			badge.show();
		}
	}

	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.d("palpal", "onNewIntent()");
		if (PalPal.unreadMessageCount > 0) {
			refresh();
		}
	}

	void onReady() {
		Log.d("palpal", "onReady()");
		refresh();
	}

	void refresh() {
		controller.refresh(getApplicationContext());
		PalPal.unreadMessageCount = 0;
		if (badge != null) {
			badge.hide();
		}
	}

	void restoreTwitterClient() {
		String[] storedAccessTokens = PalPalPreference
				.loadAccessTokenPreference(getApplicationContext());
		if (storedAccessTokens[0] == null) {
			startActivity(new Intent(this, AccountActivity.class));
		} else {

			Twitter twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(PalPal.JTWITTER_OAUTH_KEY,
					PalPal.JTWITTER_OAUTH_SECRET);

			AccessToken accessToken = new AccessToken(storedAccessTokens[0],
					storedAccessTokens[1]);
			twitter.setOAuthAccessToken(accessToken);

			PalPal.setTwitter(twitter);
			Log.d("palpal", "setTwitter()");
		}
	}

	void startUpdateMessageService() {
		Context applicationContext = getApplicationContext();
		Intent intent = new Intent(applicationContext,
				UpdateMessageService.class);
		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		PendingIntent pintent = PendingIntent.getService(applicationContext, 0,
				intent, 0);
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, new Date().getTime(),
				60 * 1000, pintent);
	}

	protected void onPause() {
		super.onPause();
		Log.d("palpal", "onPause()");
		// UpdateMessageService.stop(getApplicationContext());
	}

	void setupAction() {
		final ActionItem albumActionItem = new ActionItem();

		TextView activityTitle = (TextView) findViewById(R.id.activityTitle);
		activityTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final QuickAction qa = new QuickAction(v);

				albumActionItem.setTitle("Album");
				albumActionItem.setIcon(getResources().getDrawable(
						android.R.drawable.ic_menu_gallery));
				albumActionItem.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(
								BubbleMessageListActivity.this,
								ImageAlbumActivity.class);
						startActivity(intent);
						qa.dismiss();
					}
				});

				final ActionItem directMessageActionItem = new ActionItem();

				directMessageActionItem.setTitle("Messages");
				directMessageActionItem.setIcon(getResources().getDrawable(
						android.R.drawable.ic_menu_send));
				directMessageActionItem
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent intent = new Intent(
										BubbleMessageListActivity.this,
										DirectMessageActivity.class);
								startActivity(intent);
								qa.dismiss();
							}
						});

				final ActionItem mentionMessageActionItem = new ActionItem();

				mentionMessageActionItem.setTitle("Mentions");
				mentionMessageActionItem.setIcon(getResources().getDrawable(
						R.drawable.ic_menu_notifications));
				mentionMessageActionItem
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent intent = new Intent(
										BubbleMessageListActivity.this,
										MentionMessageActivity.class);
								startActivity(intent);
								qa.dismiss();
							}
						});

				final ActionItem imageGalleryActionItem = new ActionItem();

				imageGalleryActionItem.setTitle("Gallery");
				imageGalleryActionItem.setIcon(getResources().getDrawable(
						android.R.drawable.ic_menu_gallery));
				imageGalleryActionItem
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent intent = new Intent(
										BubbleMessageListActivity.this,
										ImageGalleryActivity.class);

								startActivity(intent);
								qa.dismiss();
							}
						});

				qa.addActionItem(albumActionItem);
				qa.addActionItem(directMessageActionItem);
				qa.addActionItem(mentionMessageActionItem);
				qa.addActionItem(imageGalleryActionItem);
				qa.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
				qa.show();
			}
		});

	}
}
