package org.dyndns.warenix.gesture;

import org.dyndns.warenix.palpal.social.facebook.TimelineSwipeListener;

import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

/**
 * let classes implemented this interface be able to response to swipe gesture
 * detected.
 * 
 * @author warenix
 * 
 */
// SwipeGestureDetector swipeGestureDetector = new SwipeGestureDetector(this,
// timelineListView);
public class SwipeGestureDetector implements TimelineSwipeListener {

	TimelineSwipeListener delegate;

	public SwipeGestureDetector(TimelineSwipeListener delegate, View gestureView) {
		this.delegate = delegate;

		GestureDetector gestureDetector = new GestureDetector(
				new TimelineGestureListener(delegate));

		gestureView.setOnTouchListener(new TimelineTouchListener(
				gestureDetector));
	}

	@Override
	public void onLeftSwipe() {
		if (delegate != null) {
			delegate.onLeftSwipe();
		}
	}

	@Override
	public void onRightSwipe() {
		if (delegate != null) {
			delegate.onRightSwipe();
		}
	}

	static class TimelineGestureListener extends SimpleOnGestureListener {

		private static final int SWIPE_MIN_DISTANCE = 180;
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

	static class TimelineTouchListener implements View.OnTouchListener {

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
	}

	/**
	 * callback for swipe gesture listener
	 * 
	 * @author warenix
	 * 
	 */
	public interface TimelineSwipeListener {

		public void onLeftSwipe();

		public void onRightSwipe();

	}
}
