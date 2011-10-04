package org.dyndns.warenix.palpal.map.activity;

import java.util.List;

import org.dyndns.warenix.location.LocationDetector;
import org.dyndns.warenix.location.LocationDetector.LocationDetectorListener;
import org.dyndns.warenix.palpaltwitter.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * double tape on the map to pick a location
 * 
 * @author warenix
 * 
 */
public class LocationPickerActivity extends MapActivity implements
		LocationDetectorListener {

	public static final int REQUEST_CODE_PICK_LOCATION = 10010;
	public static String BUNDLE_LATE6 = "LATE6";
	public static String BUNDLE_LNGE6 = "LNGE6";

	MapView mapView;
	MapController mapController;

	protected static LayoutInflater inflater;

	// current selected point
	GeoPoint pickedGeoPoint;

	// location
	LocationDetector locationDetector;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupUI();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onPause() {
		super.onPause();
		if (locationDetector != null) {
			locationDetector.stop();
		}
	}

	void setupUI() {
		setContentView(R.layout.location_picker_activity);
		if (inflater == null) {
			inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.displayZoomControls(true);

		mapController = mapView.getController();

		// center map if passed default point
		String late6String = getIntent().getStringExtra(BUNDLE_LATE6);
		String lnge6String = getIntent().getStringExtra(BUNDLE_LNGE6);

		if (late6String != null && lnge6String != null) {
			pickedGeoPoint = new GeoPoint(
					(int) (Double.parseDouble(late6String)),
					(int) (Double.parseDouble(lnge6String)));
			centerMapAtGeoPoint(pickedGeoPoint);
		}

		// marker---
		MapOverlay mapOverlay = new MapOverlay();
		List<Overlay> listOfOverlays = mapView.getOverlays();
		listOfOverlays.clear();
		listOfOverlays.add(mapOverlay);

		mapView.invalidate();

		// button controller
		ToggleButton gps = (ToggleButton) findViewById(R.id.gps);
		gps.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (locationDetector == null) {
					locationDetector = new LocationDetector(
							getApplicationContext(),
							LocationPickerActivity.this);
				}
				if (isChecked) {
					locationDetector.start();
				} else {
					locationDetector.stop();
				}
			}

		});

		Button pick = (Button) findViewById(R.id.pick);
		pick.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (pickedGeoPoint != null) {
					Intent data = new Intent();
					data.putExtra(BUNDLE_LATE6, pickedGeoPoint.getLatitudeE6());
					data.putExtra(BUNDLE_LNGE6, pickedGeoPoint.getLongitudeE6());
					setResult(RESULT_OK, data);
				} else {
					setResult(RESULT_CANCELED);
				}
				finish();
			}

		});

		Button cancel = (Button) findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}

		});
	}

	/**
	 * center mapview at specified point
	 * 
	 * @param at
	 */
	public void centerMapAtGeoPoint(GeoPoint at) {
		pickedGeoPoint = at;
		mapController.animateTo(pickedGeoPoint);
	}

	// overlay
	class MapOverlay extends Overlay {

		private long lastTapeTime;
		private static final long DOUBLE_TAP_THRESHOLD = 200;

		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {
			super.draw(canvas, mapView, shadow);

			if (pickedGeoPoint != null) {
				// ---translate the GeoPoint to screen pixels---
				Point screenPts = new Point();
				mapView.getProjection().toPixels(pickedGeoPoint, screenPts);

				// ---add the marker---
				Bitmap bmp = BitmapFactory.decodeResource(getResources(),
						R.drawable.marker);
				canvas.drawBitmap(bmp, screenPts.x - bmp.getWidth() / 2,
						screenPts.y - bmp.getHeight() / 2, null);
				bmp.recycle();
				return true;
			}
			return false;
		}

		@Override
		public boolean onTouchEvent(MotionEvent event, MapView mapView) {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				long currentTapeTime = System.currentTimeMillis();
				long tapeTimeDiff = currentTapeTime - lastTapeTime;
				if (tapeTimeDiff < DOUBLE_TAP_THRESHOLD) {

					GeoPoint p = mapView.getProjection().fromPixels(
							(int) event.getX(), (int) event.getY());
					Toast.makeText(
							getBaseContext(),
							p.getLatitudeE6() / 1E6 + "," + p.getLongitudeE6()
									/ 1E6, Toast.LENGTH_SHORT).show();

					centerMapAtGeoPoint(p);
				}

				lastTapeTime = currentTapeTime;
			}
			return false;
		}
	}

	@Override
	public void onLocationDetected(Location loc) {
		GeoPoint point = new GeoPoint((int) (loc.getLatitude() * 1e6),
				(int) (loc.getLongitude() * 1e6));
		centerMapAtGeoPoint(point);
		mapController.setZoom(20);
	}
}
