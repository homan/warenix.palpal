package org.dyndns.warenix.palpal.social.facebook;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class TimelineTouchListener implements View.OnTouchListener {

	private GestureDetector gestureDetector;

	public TimelineTouchListener(GestureDetector gestureDetector) {
		this.gestureDetector = gestureDetector;
	}

	public boolean onTouch(View v, MotionEvent event) {
		if (gestureDetector.onTouchEvent(event)) {
			return true;
		}
		return false;
	}
};
