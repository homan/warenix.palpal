package org.dyndns.warenix.lab.compat1.app;

import java.io.IOException;
import java.net.MalformedURLException;

import org.dyndns.warenix.image.CachedWebImage;
import org.dyndns.warenix.lab.compat1.R;
import org.dyndns.warenix.lab.compat1.app.timeline.TimelineFactory;
import org.dyndns.warenix.lab.compat1.app.timeline.TimelineFactory.TimelineConfig;
import org.dyndns.warenix.lab.compat1.util.Memory;
import org.dyndns.warenix.mission.facebook.FacebookAlbumCoverAdapter;
import org.dyndns.warenix.mission.facebook.FacebookAlbumPhotoAdapter;
import org.dyndns.warenix.mission.facebook.FacebookObject;
import org.dyndns.warenix.mission.facebook.util.FacebookMaster;
import org.dyndns.warenix.util.WLog;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.facebook.android.Facebook;

public class PhotoActivity extends ActionBarActivity implements
		OnPageChangeListener, OnItemClickListener {
	private static final String TAG = "PhotoActivity";

	public static final String BUNDLE_GRAPH_ID = "graph_id";
	public static final String BUNDLE_PAGE_COUNT = "page_count";

	ViewPager mPager;
	PhotoFragmentPagerAdapter mPagerAdapter;

	TextView mAlbumTitle;
	TextView mAlbumPaging;

	ViewPager mGallery;
	FacebookAlbumCoverAdapter mImageQueueAdapter;
	OnPageChangeListener mGalleryPageChangeListener;
	int mSelectedAlbumPosition;

	protected String mGraphId = "me";
	protected int mPageCount = 10;

	protected FacebookObject mAlbum;
	/**
	 * number of page user can flip
	 */
	protected int mPagingCount = 1;

	private Handler mUpdateTitleHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mAlbum = (FacebookObject) msg.obj;
			mAlbumTitle.setText(mAlbum.name);
			// update page as album info may not ready when the photo list is
			// loaded.
			updateAlbumTitle(mSelectedAlbumPosition);
			updateAlbumPager();
		}
	};

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

		Bundle intent = getIntent().getExtras();
		mGraphId = intent.getString(BUNDLE_GRAPH_ID);
		mPageCount = intent.getInt(BUNDLE_PAGE_COUNT);

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
		mAlbumPaging = (TextView) findViewById(R.id.album_paging);
		mGallery = (ViewPager) findViewById(R.id.album_gallery);
		mImageQueueAdapter = new FacebookAlbumCoverAdapter(this);
		mGallery.setAdapter(new FacebookAlbumCoverPageAdapter());

		// update title
		new Thread() {
			public void run() {
				Facebook facebook = Memory.getFacebookClient();
				if (facebook != null) {
					try {
						String graphPath = mGraphId;
						Bundle parameters = new Bundle();
						// parameters.putString("limit", pageLimit);
						// parameters.putString("offset", offset);
						String responseString = facebook.request(graphPath,
								parameters);

						FacebookObject facebookObject = new FacebookObject(
								new JSONObject(responseString));
						Message msg = new Message();
						msg.obj = facebookObject;
						mUpdateTitleHandler.sendMessage(msg);

					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();

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
		int end = start + mPageCount;
		if (mAlbum != null) {
			if (end > mAlbum.count) {
				end = (int) mAlbum.count;
			}
		}
		mAlbumPaging.setText(String.format("#%d - #%d", start, end));
	}

	void updateAlbumPager() {
		mPagingCount = (int) Math.ceil(mAlbum.count * 1.0 / mPageCount);
		mPagerAdapter.notifyDataSetChanged();
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
			// Fragment f = TimelineListFragment.newInstance(4);
			Fragment f = TimelineFactory.factory(new TimelineConfig(
					TimelineConfig.Type.Photo, "photo " + position));

			Bundle extra = FacebookAlbumPhotoAdapter.getExtra(mGraphId,
					position, mPageCount);
			f.setArguments(extra);
			return f;
		}

		@Override
		public int getCount() {
			return mPagingCount;
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
		updateAlbumTitle(position);
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