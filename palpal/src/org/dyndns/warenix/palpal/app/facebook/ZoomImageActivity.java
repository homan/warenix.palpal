package org.dyndns.warenix.palpal.app.facebook;

import java.io.IOException;
import java.net.URL;

import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.util.DownloadUtil;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;

import com.sonyericsson.zoom.DynamicZoomControl;
import com.sonyericsson.zoom.ImageZoomView;
import com.sonyericsson.zoom.PinchZoomListener;

public class ZoomImageActivity extends Activity {
	public static final String BUNDLE_IMAGE_URL = "image_url";

	/** Zoom control */
	private DynamicZoomControl mZoomControl;
	/** Image zoom view */
	private ImageZoomView mZoomView;

	private PinchZoomListener mPinchZoomListener;

	/** Decoded bitmap image */
	private Bitmap mBitmap;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.obj != null) {
				if (mBitmap != null && !mBitmap.isRecycled()) {
					mBitmap.recycle();
				}

				mBitmap = (Bitmap) msg.obj;
				mZoomView.setImage(mBitmap);
				mZoomView.setVisibility(View.VISIBLE);
				resetZoomState();
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zoom_image);

		initUI();
		loadImage();
	}

	public void onDestroy() {
		if (mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
		}

		super.onDestroy();
	}

	void initUI() {
		mZoomControl = new DynamicZoomControl();

		mPinchZoomListener = new PinchZoomListener(getApplicationContext());
		mPinchZoomListener.setZoomControl(mZoomControl);

		mZoomView = (ImageZoomView) findViewById(R.id.zoomview);
		mZoomView.setZoomState(mZoomControl.getZoomState());
		mZoomView.setOnTouchListener(mPinchZoomListener);

		mZoomControl.setAspectQuotient(mZoomView.getAspectQuotient());
		
		mZoomView.setVisibility(View.GONE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getSupportMenuInflater().inflate(R.menu.activity_image, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Reset zoom state and notify observers
	 */
	private void resetZoomState() {
		mZoomControl.getZoomState().setPanX(0.5f);
		mZoomControl.getZoomState().setPanY(0.5f);
		mZoomControl.getZoomState().setZoom(1f);
		mZoomControl.getZoomState().notifyObservers();
	}

	void loadImage() {
		final String imageUrl = getIntent().getStringExtra(BUNDLE_IMAGE_URL);
		if (imageUrl != null) {
			new Thread() {
				public void run() {
					URL url = null;
					try {
						url = new URL(imageUrl);
						Bitmap result = null;

						byte[] imageData = DownloadUtil.getData(url, null);
						result = BitmapFactory.decodeByteArray(imageData, 0,
								imageData.length);
						if (result != null) {
							Message msg = new Message();
							msg.obj = result;
							mHandler.sendMessage(msg);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
	}
}
