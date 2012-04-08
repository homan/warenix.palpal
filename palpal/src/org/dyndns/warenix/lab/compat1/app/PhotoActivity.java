package org.dyndns.warenix.lab.compat1.app;

import org.dyndns.warenix.image.CachedWebImage;
import org.dyndns.warenix.lab.compat1.R;
import org.dyndns.warenix.mission.facebook.FacebookAlbumPhotoAdapter;
import org.dyndns.warenix.mission.facebook.util.FacebookMaster;
import org.dyndns.warenix.mission.timeline.TimelineListFragment;
import org.dyndns.warenix.util.WLog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.example.android.actionbarcompat.ActionBarActivity;

public class PhotoActivity extends ActionBarActivity implements
		OnPageChangeListener {
	private static final String TAG = "PhotoActivity";

	ViewPager mPager;
	PhotoFragmentPagerAdapter mPagerAdapter;

	protected String mGraphId = "me";
	protected int mPageCount = 10;

	static {
		CachedWebImage.setCacheDir("palpal");
		WLog.setAppName("palpal");
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_main);

		FacebookMaster.restoreFacebook(getApplicationContext());

		setTitle("Photo");

		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new PhotoFragmentPagerAdapter(
				getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		mPager.setOnPageChangeListener(this);
	}

	class PhotoFragmentPagerAdapter extends FragmentStatePagerAdapter {

		public PhotoFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			WLog.d(TAG, "get item" + position);
			Fragment f = TimelineListFragment.newInstance(4);

			Bundle extra = FacebookAlbumPhotoAdapter.getExtra(mGraphId,
					position, mPageCount);
			f.setArguments(extra);
			return f;
		}

		@Override
		public int getCount() {
			return 20;
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {

	}

}