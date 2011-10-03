package org.dyndns.warenix.palpal.social.facebook;

import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class TimelineGestureListener extends SimpleOnGestureListener {

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	TimelineSwipeListener swipeListener;

	public TimelineGestureListener(TimelineSwipeListener swipeListener) {
		this.swipeListener = swipeListener;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		try {
			if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
				return false;
			// right to left swipe
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				// Toast.makeText(SelectFilterActivity.this, "Left Swipe",
				// Toast.LENGTH_SHORT`enter code here`).show();

				Log.v("palpal", "left swipe");

				swipeListener.onLeftSwipe();
			} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				// Toast.makeText(SelectFilterActivity.this, "Right Swipe",
				// Toast.LENGTH_SHORT).show();
				Log.v("palpal", "right swipe");
				swipeListener.onRightSwipe();
			}
		} catch (Exception e) {
			// nothing
		}
		return false;
	}
}
