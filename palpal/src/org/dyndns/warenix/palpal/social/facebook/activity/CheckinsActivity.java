package org.dyndns.warenix.palpal.social.facebook.activity;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mapviewballoons.example.FacebookCheckinOverlayItem;
import mapviewballoons.example.MyItemizedOverlay;

import org.dyndns.warenix.location.LocationDetector;
import org.dyndns.warenix.location.LocationDetector.LocationDetectorListener;
import org.dyndns.warenix.palpal.PalPalPreference;
import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookMaster;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookUtil;
import org.dyndns.warenix.palpal.social.facebook.vo.Checkin;
import org.dyndns.warenix.util.DownloadDrawable;
import org.dyndns.warenix.util.ToastUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.facebook.android.Facebook;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class CheckinsActivity extends PalPalFacebookActivity implements
		LocationDetectorListener {
	MapView mapView;
	ArrayList<Checkin> checkinList;
	// private PlacesItemizedOverlay placesItemizedOverlay;

	int currentIndex;

	private View popView;
	private ImageButton nextButton, previousButton;

	protected static LayoutInflater inflater;

	List<Overlay> mapOverlays;
	Drawable drawable;

	static LocationDetector locationDetector;

	@Override
	void onFacebookReady(Facebook facebook,
			HashMap<String, SoftReference<Bitmap>> imagePool) {

		ToastUtil.showQuickToast(this, "detecting your current location");
		locationDetector = new LocationDetector(this, this);
		locationDetector.start();
	}

	@Override
	void setupUI() {
		setContentView(R.layout.facebook_checkins);

		if (inflater == null) {
			inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.displayZoomControls(true);
		mapOverlays = mapView.getOverlays();

		nextButton = (ImageButton) findViewById(R.id.nextButton);
		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (checkinList == null || checkinList.size() == 0) {
					return;
				}

				if ((currentIndex + 1) == checkinList.size()) {
					currentIndex = 0;
				} else {
					++currentIndex;
				}
				Checkin checkin = checkinList.get(currentIndex);
				zoomToCheckin(checkin);
			}

		});

		previousButton = (ImageButton) findViewById(R.id.previousButton);
		previousButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (checkinList == null || checkinList.size() == 0) {
					return;
				}

				if ((currentIndex - 1) < 0) {
					currentIndex = checkinList.size() - 1;
				} else {
					--currentIndex;
				}
				Checkin checkin = checkinList.get(currentIndex);
				zoomToCheckin(checkin);
			}

		});

		ImageButton refreshButton = (ImageButton) findViewById(R.id.refreshButton);
		refreshButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ToastUtil.showQuickToast(getApplicationContext(),
						"loading friends' recent checkins...");

				new LoadFriendsRecentCheckinsAsyncTask().execute();

			}
		});
	}

	protected void onPause() {
		if (locationDetector != null) {
			locationDetector.stop();
		}
		super.onPause();
	}

	private void zoomToCheckin(Checkin checkin) {
		GeoPoint point = new GeoPoint((int) checkin.coords[Checkin.LATITUDE],
				(int) checkin.coords[Checkin.LONGITUDE]);

		mapView.getController().animateTo(point);
		mapView.getController().setZoom(18);
	}

	private void loadFriendsRecentCheckins() {
		String limit = PalPalPreference.loadPreferenceValue(
				this,
				getResources().getString(
						R.string.KEY_FACEBOOK_NUMBER_OF_RECENT_CHECKINS), "5");
		checkinList = FacebookMaster.User.getFriendsRecentCheckins(Integer
				.parseInt(limit));
	}

	void onFriendsRecentCheckinsLoaded() {
		if (checkinList != null) {
			mapOverlays.clear();
			mapView.invalidate();

			for (final Checkin checkin : checkinList) {
				Log.d("palpal", String.format("checkin at (%f, %f)",
						checkin.coords[Checkin.LATITUDE],
						checkin.coords[Checkin.LONGITUDE]));

				// place overlay
				// Drawable drawable = getResources().getDrawable(
				// R.drawable.bubble_background);
				Drawable drawable = new DownloadDrawable(
						FacebookUtil.getUserProfileImage(checkin.author_uid,
								FacebookUtil.USER_PROFILE_IMAGE_NORMAL));
				MyItemizedOverlay itemizedOverlay = new MyItemizedOverlay(
						drawable, mapView);

				GeoPoint point = new GeoPoint(
						(int) checkin.coords[Checkin.LATITUDE],
						(int) checkin.coords[Checkin.LONGITUDE]);
				// OverlayItem overlayItem = new OverlayItem(point,
				// checkin.author_uid, checkin.message);
				OverlayItem overlayItem = new FacebookCheckinOverlayItem(point,
						checkin.author_uid, checkin.message, checkin);
				itemizedOverlay.addOverlay(overlayItem);

				mapOverlays.add(itemizedOverlay);
			}

			// zoom to first
			currentIndex = 0;
			Checkin checkin = checkinList.get(currentIndex);
			zoomToCheckin(checkin);

			// initialiseOverlays();
		}
	}

	//
	// private final ItemizedOverlay.OnFocusChangeListener onFocusChangeListener
	// = new ItemizedOverlay.OnFocusChangeListener() {
	//
	// @Override
	// public void onFocusChanged(ItemizedOverlay overlay, OverlayItem newFocus)
	// {
	//
	// if (popView != null) {
	// popView.setVisibility(View.GONE);
	// }
	//
	// if (newFocus != null) {
	//
	// MapView.LayoutParams geoLP = (MapView.LayoutParams) popView
	// .getLayoutParams();
	// geoLP.point = newFocus.getPoint();
	// TextView title = (TextView) popView
	// .findViewById(R.id.map_bubbleTitle);
	// title.setText(newFocus.getTitle());
	//
	// TextView desc = (TextView) popView
	// .findViewById(R.id.map_bubbleText);
	//
	// WebImage profileImage = (WebImage) findViewById(R.id.profileImage);
	// profileImage.startLoading(FacebookUtil.getUserProfileImage(
	// newFocus.getTitle(),
	// FacebookUtil.USER_PROFILE_IMAGE_SQUARE));
	//
	// if (newFocus.getSnippet() == null
	// || newFocus.getSnippet().length() == 0) {
	// desc.setVisibility(View.GONE);
	// } else {
	// desc.setVisibility(View.VISIBLE);
	// desc.setText(newFocus.getSnippet());
	// }
	// mapView.updateViewLayout(popView, geoLP);
	// popView.setVisibility(View.VISIBLE);
	//
	// }
	// }
	// };
	//
	// private void initialiseOverlays() {
	// // // Create an ItemizedOverlay to display a list of markers
	// // Drawable defaultMarker = getResources().getDrawable(R.drawable.w);
	// // placesItemizedOverlay = new PlacesItemizedOverlay(this,
	// // defaultMarker);
	// //
	// // for (Checkin checkin : checkinList) {
	// // placesItemizedOverlay.addOverlayItem(new OverlayItem(new GeoPoint(
	// // (int) (checkin.coords[Checkin.LATITUDE] * 1E6),
	// // (int) (checkin.coords[Checkin.LONGITUDE] * 1E6)),
	// // checkin.author_uid, checkin.message));
	// //
	// // }
	// // // Add the overlays to the map
	// // mapView.getOverlays().add(placesItemizedOverlay);
	//
	// // // create my overlay and show it
	// // for (Checkin checkin : checkinList) {
	// // GeoPoint geopoint = new GeoPoint(
	// // (int) (checkin.coords[Checkin.LATITUDE] * 1E6),
	// // (int) (checkin.coords[Checkin.LONGITUDE] * 1E6));
	// //
	// // CheckinOverlay overlay = new CheckinOverlay(geopoint, checkin);
	// // mapView.getOverlays().add(overlay);
	// // mapView.getController().animateTo(geopoint);
	// // }
	// // // move to location
	// // // mapView.getOverlays.getController().animateTo(geopoint);
	// //
	// // // redraw map
	// // mapView.postInvalidate();
	//
	// popView = inflater.inflate(R.layout.overlay_popup, null);
	// mapView.addView(popView, new MapView.LayoutParams(
	// MapView.LayoutParams.WRAP_CONTENT,
	// MapView.LayoutParams.WRAP_CONTENT, null,
	// MapView.LayoutParams.BOTTOM_CENTER));
	//
	// popView.setVisibility(View.GONE);
	//
	// // PointItemizedOverlay overlay = new PointItemizedOverlay(drawable);
	//
	// // Create an ItemizedOverlay to display a list of markers
	// Drawable defaultMarker = getResources().getDrawable(R.drawable.w);
	// placesItemizedOverlay = new PlacesItemizedOverlay(this, defaultMarker);
	//
	// for (Checkin checkin : checkinList) {
	// GeoPoint geopoint = new GeoPoint(
	// (int) (checkin.coords[Checkin.LATITUDE] * 1E6),
	// (int) (checkin.coords[Checkin.LONGITUDE] * 1E6));
	// placesItemizedOverlay.addOverlayItem(new OverlayItem(geopoint,
	// checkin.author_uid, checkin.message));
	//
	// mapView.getController().animateTo(geopoint);
	// }
	// // Add the overlays to the map
	// mapView.getOverlays().add(placesItemizedOverlay);
	// placesItemizedOverlay.setOnFocusChangeListener(onFocusChangeListener);
	//
	// }
	//
	// class PlacesItemizedOverlay extends ItemizedOverlay {
	// private Context context;
	// private ArrayList items = new ArrayList();
	//
	// public PlacesItemizedOverlay(Context aContext, Drawable marker) {
	// super(boundCenterBottom(marker));
	// context = aContext;
	// }
	//
	// public void addOverlayItem(OverlayItem item) {
	// items.add(item);
	// populate();
	// }
	//
	// @Override
	// protected OverlayItem createItem(int i) {
	// return (OverlayItem) items.get(i);
	// }
	//
	// @Override
	// public int size() {
	// return items.size();
	// }
	//
	// // @Override
	// // protected boolean onTap(int index) {
	// // OverlayItem item = (OverlayItem) items.get(index);
	// // AlertDialog.Builder dialog = new AlertDialog.Builder(context);
	// // dialog.setTitle(item.getTitle());
	// // dialog.setMessage(item.getSnippet());
	// // dialog.show();
	// //
	// // return true;
	// // }
	// }

	@Override
	public void onLocationDetected(Location loc) {
		// stop location detection
		locationDetector.stop();

		GeoPoint point = new GeoPoint((int) (loc.getLatitude() * 1e6),
				(int) (loc.getLongitude() * 1e6));
		mapView.getController().animateTo(point);
		mapView.getController().setZoom(18);
	}

	private class LoadFriendsRecentCheckinsAsyncTask extends
			AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			loadFriendsRecentCheckins();
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			nextButton.setVisibility(View.VISIBLE);
			previousButton.setVisibility(View.VISIBLE);
			onFriendsRecentCheckinsLoaded();
		}

	}
}
