package org.dyndns.warenix.location;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

public class LocationDetector {
	private Context context;
	private static final int TWO_MINUTES = 1000 * 60 * 2;

	private Location currentLocation;

	LocationDetectorListener listener;

	private LocationManager locationManager;
	private LocationListener listenerCoarse;
	private LocationListener listenerFine;

	// Set to false when location services are
	// unavailable.
	private boolean locationAvailable = true;

	public LocationDetector(Context context, LocationDetectorListener listener) {
		this.context = context;
		this.listener = listener;
	}

	public void start() {
		/* Use the LocationManager class to obtain GPS locations */

		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		Criteria fineCriteria = new Criteria();
		fineCriteria.setAccuracy(Criteria.ACCURACY_FINE);

		Criteria coarseCriteria = new Criteria();
		coarseCriteria.setAccuracy(Criteria.ACCURACY_COARSE);

		currentLocation = locationManager.getLastKnownLocation(locationManager
				.getBestProvider(fineCriteria, true));

		if (listenerFine == null || listenerCoarse == null)
			createLocationListeners();

		// Will keep updating about every 500 ms until
		// accuracy is about 1000 meters to get quick fix.
		locationManager.requestLocationUpdates(
				locationManager.getBestProvider(coarseCriteria, true), 500,
				1000, listenerCoarse);
		// Will keep updating about every 500 ms until
		// accuracy is about 50 meters to get accurate fix.
		locationManager.requestLocationUpdates(
				locationManager.getBestProvider(fineCriteria, true), 500, 50,
				listenerFine);

		// locationManager = (LocationManager) context
		// .getSystemService(Context.LOCATION_SERVICE);
		// String bestProvider = locationManager.getBestProvider(criteria,
		// true);
		//
		// Log.d("palpal", String.format("best provider %s", bestProvider));

		// Will keep updating about every 500 ms until
		// accuracy is about 1000 meters to get quick fix.
		locationManager.requestLocationUpdates(
				locationManager.getBestProvider(coarseCriteria, true), 1000,
				1000, listenerCoarse);
		// Will keep updating about every 500 ms until
		// accuracy is about 50 meters to get accurate fix.
		locationManager.requestLocationUpdates(
				locationManager.getBestProvider(fineCriteria, true), 500, 500,
				listenerFine);
	}

	public void stop() {
		locationManager.removeUpdates(listenerCoarse);
		locationManager.removeUpdates(listenerFine);
	}

	/**
	 * Creates LocationListeners
	 */
	private void createLocationListeners() {
		listenerCoarse = new LocationListener() {
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				switch (status) {
				case LocationProvider.OUT_OF_SERVICE:
				case LocationProvider.TEMPORARILY_UNAVAILABLE:
					locationAvailable = false;
					break;
				case LocationProvider.AVAILABLE:
					locationAvailable = true;
				}
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}

			public void onLocationChanged(Location location) {
				currentLocation = location;
				listener.onLocationDetected(currentLocation);
				if (location.getAccuracy() > 1000 && location.hasAccuracy())
					locationManager.removeUpdates(this);
			}
		};
		listenerFine = new LocationListener() {
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				switch (status) {
				case LocationProvider.OUT_OF_SERVICE:
				case LocationProvider.TEMPORARILY_UNAVAILABLE:
					locationAvailable = false;
					break;
				case LocationProvider.AVAILABLE:
					locationAvailable = true;
				}
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}

			public void onLocationChanged(Location location) {
				currentLocation = location;
				listener.onLocationDetected(currentLocation);
				if (location.getAccuracy() > 1000 && location.hasAccuracy())
					locationManager.removeUpdates(this);
			}
		};
	}

	public interface LocationDetectorListener {
		public void onLocationDetected(Location loc);
	}

	// listener
	/* Class My Location Listener */

	public class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location loc) {
			if (listener != null) {
				listener.onLocationDetected(loc);
			}
		}

		@Override
		public void onProviderDisabled(String provider) {

		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

	}/* End of Class MyLocationListener */

	/**
	 * Determines whether one Location reading is better than the current
	 * Location fix
	 * 
	 * @param location
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The current Location fix, to which you want to compare the new
	 *            one
	 */
	protected boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}
}
