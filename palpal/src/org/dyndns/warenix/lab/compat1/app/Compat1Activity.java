package org.dyndns.warenix.lab.compat1.app;

import java.util.ArrayList;

import org.dyndns.warenix.lab.compat1.R;
import org.dyndns.warenix.lab.compat1.app.facebook.AuthenFacebookActivity;
import org.dyndns.warenix.lab.compat1.app.twitter.AuthenTwitterActivity;
import org.dyndns.warenix.lab.compat1.fragment.BFragment;
import org.dyndns.warenix.lab.compat1.util.AndroidUtil;
import org.dyndns.warenix.lab.taskservice.TaskService;
import org.dyndns.warenix.mission.facebook.util.FacebookMaster;
import org.dyndns.warenix.mission.timeline.TimelineListFragment;
import org.dyndns.warenix.mission.twitter.util.TwitterMaster;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.actionbarcompat.ActionBarActivity;

public class Compat1Activity extends ActionBarActivity {
	TabHost mTabHost;
	TabsAdapter mTabsAdapter;
	ViewPager mViewPager;
	Toast toast;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		AndroidUtil.hideSoftwareKeyboard(this);

		restoreClients();
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
		Log.d("warenix", "restoreClients() restoreState:" + restoreState);
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
			break;

		case R.id.menu_refresh:
			Toast.makeText(this, "Fake refreshing...", Toast.LENGTH_SHORT)
					.show();
			getActionBarHelper().setRefreshActionItemState(true);
			getWindow().getDecorView().postDelayed(new Runnable() {
				@Override
				public void run() {
					getActionBarHelper().setRefreshActionItemState(false);
				}
			}, 1000);
			break;

		case R.id.menu_search:
			Toast.makeText(this, "Tapped search", Toast.LENGTH_SHORT).show();
			onSearchRequested();
			break;

		case R.id.menu_share:
			intent = new Intent(this, ComposeActivity.class);
			startActivity(intent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setupTab(final TabsAdapter adapter, final View view,
			final String tag, Class<?> cls, Bundle args) {
		View tabview = createTabView(mTabHost.getContext(), tag);

		adapter.addTab(mTabHost.newTabSpec(tag).setIndicator(tabview)
				.setContent(new TabContentFactory() {
					public View createTabContent(String tag) {
						return view;
					}
				}), cls, args);
	}

	void onReady() {
		mViewPager = (ViewPager) this.findViewById(R.id.pager);

		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();

		mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);

		setupTab(mTabsAdapter, new TextView(this), "Stream", BFragment.class,
				null);
		setupTab(mTabsAdapter, new TextView(this), "Mentions", BFragment.class,
				null);
		// setupTab(mTabsAdapter, new TextView(this), "Stream", BFragment.class,
		// null);

		setupTab(mTabsAdapter, new TextView(this), "Messages", BFragment.class,
				null);

		for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); ++i) {
			final int childIndex = i;
			mTabHost.getTabWidget().getChildAt(childIndex)
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							int currentTabIndex = mTabHost.getCurrentTab();

							boolean needRefresh = currentTabIndex == childIndex;
							mTabHost.setCurrentTab(childIndex);
							if (needRefresh) {
								if (toast == null) {
									toast = Toast.makeText(
											Compat1Activity.this, "",
											Toast.LENGTH_SHORT);
								}
								toast.setText("refresh tab " + currentTabIndex);
								toast.cancel();
								toast.show();

								TimelineListFragment adapter = ((TimelineListFragment) (mTabsAdapter
										.getItem(currentTabIndex)));
								adapter.refresh();
							}
						}
					});
		}

		TaskService.setRunning(true);

	}

	public static class TabsAdapter extends FragmentStatePagerAdapter implements
			TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
		private final Context mContext;
		private final TabHost mTabHost;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		static final class TabInfo {
			private final String tag;
			private final Class<?> clss;
			private final Bundle args;

			TabInfo(String _tag, Class<?> _class, Bundle _args) {
				tag = _tag;
				clss = _class;
				args = _args;
			}
		}

		static class DummyTabFactory implements TabHost.TabContentFactory {
			private final Context mContext;

			public DummyTabFactory(Context context) {
				mContext = context;
			}

			@Override
			public View createTabContent(String tag) {
				View v = new View(mContext);
				v.setMinimumWidth(0);
				v.setMinimumHeight(0);
				return v;
			}
		}

		public TabsAdapter(FragmentActivity activity, TabHost tabHost,
				ViewPager pager) {
			super(activity.getSupportFragmentManager());
			mContext = activity;
			mTabHost = tabHost;
			mViewPager = pager;
			mTabHost.setOnTabChangedListener(this);
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}

		public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
			tabSpec.setContent(new DummyTabFactory(mContext));
			String tag = tabSpec.getTag();

			TabInfo info = new TabInfo(tag, clss, args);
			mTabs.add(info);
			mTabHost.addTab(tabSpec);
			notifyDataSetChanged();

			// fragmentList.add(Fragment.instantiate(mContext,
			// info.clss.getName(), info.args));
			fragmentList.add(TimelineListFragment.newInstance(fragmentList
					.size()));
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}

		ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();

		@Override
		public Fragment getItem(int position) {
			// if (fragmentList.size() <= position
			// || fragmentList.get(position) == null) {
			// TabInfo info = mTabs.get(position);
			// fragmentList.add(Fragment.instantiate(mContext,
			// info.clss.getName(), info.args));
			// }
			return fragmentList.get(position);
			// return TwitterTimelineListFragment.newInstance(position);
		}

		@Override
		public void onTabChanged(String tabId) {
			int position = mTabHost.getCurrentTab();
			mViewPager.setCurrentItem(position);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			Log.d("warenix", "page selceted at " + position);
			mTabHost.setCurrentTab(position);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
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

}