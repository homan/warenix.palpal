package org.dyndns.warenix.lab.compat1.app;

import org.dyndns.warenix.image.CachedWebImage;
import org.dyndns.warenix.lab.compat1.R;
import org.dyndns.warenix.mission.facebook.FacebookAlbumCoverAdapter;
import org.dyndns.warenix.mission.facebook.FacebookAlbumPhotoAdapter;
import org.dyndns.warenix.mission.facebook.util.FacebookMaster;
import org.dyndns.warenix.mission.timeline.TimelineListFragment;
import org.dyndns.warenix.util.WLog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.example.android.actionbarcompat.ActionBarActivity;

public class PhotoActivity extends ActionBarActivity implements
		OnPageChangeListener, OnItemClickListener {
	private static final String TAG = "PhotoActivity";

	public static final String BUNDLE_GRAPH_ID = "graph_id";
	public static final String BUNDLE_PAGE_COUNT = "page_count";

	ViewPager mPager;
	PhotoFragmentPagerAdapter mPagerAdapter;

	TextView mAlbumTitle;

	ViewPager mGallery;
	FacebookAlbumCoverAdapter mImageQueueAdapter;
	OnPageChangeListener mGalleryPageChangeListener;
	int mSelectedAlbumPosition;

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

		setupAlbumGallery();

		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new PhotoFragmentPagerAdapter(
				getSupportFragmentManager());
		mPager.setOffscreenPageLimit(0);
		mPager.setAdapter(mPagerAdapter);
		mPager.setOnPageChangeListener(this);
		onPageSelected(0);
	}

	void setupAlbumGallery() {
		mAlbumTitle = (TextView) findViewById(R.id.album_title);
		mGallery = (ViewPager) findViewById(R.id.album_gallery);
		mImageQueueAdapter = new FacebookAlbumCoverAdapter(this);
		mGallery.setAdapter(new FacebookAlbumCoverPageAdapter());

		mGalleryPageChangeListener = new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				mSelectedAlbumPosition = position;

				updateAlbumTitle(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int position) {
			}
		};
		mGallery.setOnPageChangeListener(mGalleryPageChangeListener);
		mGallery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				WLog.d(TAG, "selected album index" + mSelectedAlbumPosition);
			}
		});
	}

	void updateAlbumTitle(int position) {
		int start = (position * mPageCount) + 1;
		mAlbumTitle.setText(String.format("#%d - #%d", start, start
				+ mPageCount));
	}

	class FacebookAlbumCoverPageAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			int c = mImageQueueAdapter.getCount();
			WLog.d(TAG, String.format("count[%d]", c));
			return c;
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == ((View) arg1);
		}

		@Override
		public Object instantiateItem(View collection, int position) {
			View view = mImageQueueAdapter.getView(position, null, mGallery);
			((ViewPager) collection).addView(view, position);
			return view;
		}

	}

	class PhotoFragmentPagerAdapter extends FragmentStatePagerAdapter {

		public PhotoFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			WLog.d(TAG, "get item" + position);
			Fragment f = TimelineListFragment.newInstance(4);

			Bundle intent = getIntent().getExtras();
			mGraphId = intent.getString(BUNDLE_GRAPH_ID);
			mPageCount = intent.getInt(BUNDLE_PAGE_COUNT);

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
		int start = (position * mPageCount);
		mAlbumTitle.setText(String.format("#%d - #%d", start + 1, start
				+ mPageCount));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		WLog.d(TAG, String.format("position[%d]", position));

		switch (parent.getId()) {
		case R.id.album_gallery:
			// onGalleryItemClicked(position);
		}
	}

	void onGalleryItemClicked(int position) {
		mPager.setCurrentItem(0);
	}

}