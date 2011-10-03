package org.dyndns.warenix.gesture;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class ShakeDetector implements SensorEventListener {

	private static final int SHAKE_THRESHOLD = 2000;
	long lastUpdate;
	private Sensor sensor;
	private SensorManager sensorManager;
	ShakeListener listener;

	float x, y, z;
	float last_x, last_y, last_z;

	public ShakeDetector(Context context, ShakeListener listener) {
		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		this.listener = listener;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int value) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		// if (event.sensor.getType() == SensorManager.SENSOR_ACCELEROMETER) {
		long curTime = System.currentTimeMillis();
		// only allow one update every 100ms.
		if ((curTime - lastUpdate) > 100) {
			long diffTime = (curTime - lastUpdate);
			lastUpdate = curTime;

			x = event.values[SensorManager.DATA_X];
			y = event.values[SensorManager.DATA_Y];
			z = event.values[SensorManager.DATA_Z];

			float speed = Math.abs(x + y + z - last_x - last_y - last_z)
					/ diffTime * 10000;

			if (speed > SHAKE_THRESHOLD) {
				Log.d("sensor", "shake detected w/ speed: " + speed);
				if (listener != null) {
					Log.v("palpal", "involk onShake()");
					listener.onShake();
				}
			}
			last_x = x;
			last_y = y;
			last_z = z;
		}
		// }
	}

	public void start() {
		Log.v("palpal", "shakeDetector start");
		if (sensor == null) {
			List<Sensor> sensors = sensorManager
					.getSensorList(Sensor.TYPE_ACCELEROMETER);
			if (sensors.size() > 0) {
				sensor = sensors.get(0);
				sensorManager.registerListener(this, sensor,
						SensorManager.SENSOR_DELAY_GAME);
				Log.v("palpal", "shakeDetector registered");
			}
		} else {
			sensorManager.registerListener(this, sensor,
					SensorManager.SENSOR_DELAY_GAME);
			Log.v("palpal", "shakeDetector registered");
		}

	}

	public void stop() {
		Log.v("palpal", "shakeDetector stop");
		sensorManager.unregisterListener(this, sensor);
	}

	public interface ShakeListener {
		public void onShake();
	}
}
