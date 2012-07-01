package org.dyndns.warenix.lab.compat1.app;

import java.util.ArrayList;

import org.dyndns.warenix.actionbar.R;
import org.dyndns.warenix.image.CachedWebImage;
import org.dyndns.warenix.lab.compat1.app.facebook.AuthenFacebookActivity;
import org.dyndns.warenix.lab.compat1.app.timeline.TimelineFactory;
import org.dyndns.warenix.lab.compat1.app.timeline.TimelineFactory.TimelineConfig;
import org.dyndns.warenix.lab.compat1.app.twitter.AuthenTwitterActivity;
import org.dyndns.warenix.lab.compat1.util.AndroidUtil;
import org.dyndns.warenix.lab.taskservice.BackgroundTask;
import org.dyndns.warenix.lab.taskservice.TaskService;
import org.dyndns.warenix.lab.taskservice.TaskServiceStateListener;
import org.dyndns.warenix.mission.facebook.backgroundtask.CommentPostBackgroundTask;
import org.dyndns.warenix.mission.facebook.backgroundtask.LikePostBackgroundTask;
import org.dyndns.warenix.mission.facebook.util.FacebookMaster;
import org.dyndns.warenix.mission.timeline.TimelineListFragment;
import org.dyndns.warenix.mission.twitter.backgroundtask.ReplyStatusBackgroundTask;
import org.dyndns.warenix.mission.twitter.util.TwitterMaster;
import org.dyndns.warenix.util.WLog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.actionbarcompat.ActionBarActivity;

public class MainActivity extends ActionBarActivity {
	private static final String TAG = "MainActivity";
	TabsAdapter mTabsAdapter;
	ViewPager mViewPager;

	static ArrayList<TimelineConfig> sTimelineConfigList = new ArrayList<TimelineConfig>();
	static {
		sTimelineConfigList.add(new TimelineConfig(TimelineConfig.Type.Stream,
				"Stream"));
		sTimelineConfigList.add(new TimelineConfig(
				TimelineConfig.Type.Notifications, "Notifications"));
		sTimelineConfigList.add(new TimelineConfig(
				TimelineConfig.Type.Messages, "Messages"));
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		AndroidUtil.hideSoftwareKeyboard(this);

		// restoreClients();

		if (savedInstanceState == null) {
			restoreClientsToMemory();
		}
		onReady();
	}

	public void onResume() {
		super.onResume();

		TaskService.setStateListener(new TaskServiceStateListener() {

			@Override
			public void onQueueSizeChanged(int newQueueSize) {

			}

			@Override
			public void onBackgroundTaskRemoved(final BackgroundTask task) {
				runOnUiThread(new Runnable() {
					public void run() {
						if (task instanceof LikePostBackgroundTask) {
							Toast.makeText(getApplicationContext(), "Like +1",
									Toast.LENGTH_SHORT).show();
						} else if (task instanceof CommentPostBackgroundTask) {
							Toast.makeText(getApplicationContext(),
									"Comment posted", Toast.LENGTH_SHORT)
									.show();
						} else if (task instanceof ReplyStatusBackgroundTask) {
							Toast.makeText(getApplicationContext(),
									"Status replied", Toast.LENGTH_SHORT)
									.show();
						}
					}
				});
			}

			@Override
			public void onBackgroundTaskExecuted(final BackgroundTask task) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getApplicationContext(),
								"executing " + task.toString(),
								Toast.LENGTH_SHORT).show();
					}
				});

			}

			@Override
			public void onBackgroundTaskAdded(BackgroundTask task) {

			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESTORE_STATE_TWITTER
				|| requestCode == RESTORE_STATE_FACEBOOK) {
			switch (requestCode) {
			case RESTORE_STATE_TWITTER:
				if (resultCode == RESULT_OK) {
					restoreState = RESTORE_STATE_FACEBOOK;
				} else if (resultCode == RESULT_CANCELED) {
					restoreState = RESTORE_STATE_CANCELLED;
				}
				break;
			case RESTORE_STATE_FACEBOOK:
				if (resultCode == RESULT_OK) {
					restoreState = RESTORE_STATE_COMPLETED;
				} else if (resultCode == RESULT_CANCELED) {
					restoreState = RESTORE_STATE_CANCELLED;
				}
				break;
			}

			if (restoreState >= 0) {
				restoreClients();
			} else {
				onReady();
			}
		}
	}

	final int RESTORE_STATE_TWITTER = 1;
	final int RESTORE_STATE_FACEBOOK = 2;
	final int RESTORE_STATE_COMPLETED = -1;
	final int RESTORE_STATE_CANCELLED = -2;
	int restoreState = RESTORE_STATE_TWITTER;

	void restoreClients() {
		WLog.d(TAG, "restoreClients() restoreState:" + restoreState);
		Intent intent = null;
		if (restoreState >= 0) {
			switch (restoreState) {
			case RESTORE_STATE_TWITTER:
				if (!TwitterMaster
						.restoreTwitterClient(getApplicationContext())) {
					intent = new Intent(this, AuthenTwitterActivity.class);
					startActivityForResult(intent, RESTORE_STATE_TWITTER);
					break;
				} else {
					restoreState = RESTORE_STATE_FACEBOOK;
				}
			case RESTORE_STATE_FACEBOOK:
				if (!FacebookMaster.restoreFacebook(getApplicationContext())) {
					intent = new Intent(this, AuthenFacebookActivity.class);

					startActivityForResult(intent, RESTORE_STATE_FACEBOOK);
					break;
				} else {
					restoreState = RESTORE_STATE_COMPLETED;
				}

			}
		}

		if (restoreState == RESTORE_STATE_COMPLETED) {
			onReady();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main, menu);

		// Calling super after populating the menu is necessary here to ensure
		// that the
		// action bar helpers have a chance to handle this event.
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		switch (item.getItemId()) {
		case android.R.id.home:
			Toast.makeText(this, "Tapped home", Toast.LENGTH_SHORT).show();
			finish();
			break;

		case R.id.menu_refresh:
			// Toast.makeText(this, "Fake refreshing...", Toast.LENGTH_SHORT)
			// .show();
			// getActionBarHelper().setRefreshActionItemState(true);
			// getWindow().getDecorView().postDelayed(new Runnable() {
			// @Override
			// public void run() {
			// getActionBarHelper().setRefreshActionItemState(false);
			// }
			// }, 1000);
			// refreshTab(mTabHost.getCurrentTab());
			refreshTab(mViewPager.getCurrentItem());
			break;

		case R.id.menu_search:
			Toast.makeText(this, "Tapped search", Toast.LENGTH_SHORT).show();
			onSearchRequested();
			break;

		case R.id.menu_share:
			intent = new Intent(this, ComposeActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_accounts:
			intent = new Intent(this, AccountsActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_clear_cache:
			new Thread() {
				public void run() {
					CachedWebImage.removeCacheDir("palpal");
				}
			}.start();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	void onReady() {
		mViewPager = (ViewPager) this.findViewById(R.id.pager);
		mTabsAdapter = new TabsAdapter(this, null, mViewPager);
		mViewPager.setAdapter(mTabsAdapter);
	}

	private void refreshTab(int currentTabIndex) {
		TimelineListFragment f = (TimelineListFragment) mTabsAdapter
				.instantiateItem(mViewPager, currentTabIndex);
		((TimelineListFragment) f).refresh();
	}

	public static class TabsAdapter extends FragmentStatePagerAdapter implements
			ViewPager.OnPageChangeListener {
		private final Context mContext;

		public TabsAdapter(FragmentActivity activity, TabHost tabHost,
				ViewPager pager) {
			super(activity.getSupportFragmentManager());
			mContext = activity;
		}

		@Override
		public int getCount() {
			return sTimelineConfigList.size();
		}

		@Override
		public Fragment getItem(int position) {
			return TimelineFactory.factory(sTimelineConfigList.get(position));
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			WLog.d(TAG, "page selceted at " + position);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return sTimelineConfigList.get(position).mTitle;
		}
	}

	/**
	 * create custom view for "tab"
	 * 
	 * @param context
	 * @param text
	 * @return
	 */
	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context)
				.inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		// tv.setPadding(2, 10, 2, 10);
		return view;
	}

	void restoreClientsToMemory() {
		WLog.d(TAG, "restoreClientsToMemory");
		boolean isTwitterOk = TwitterMaster
				.restoreTwitterClient(getApplicationContext());
		boolean isFacebookOk = FacebookMaster
				.restoreFacebook(getApplicationContext());
	}

	// static class FragmentConfig {
	// private static String titles[] = { "Stream", "Mentions", "Messages" };
	//
	// static public String getTitle(int position) {
	// if (position < titles.length) {
	// return titles[position];
	// }
	// return null;
	// }
	// }
}