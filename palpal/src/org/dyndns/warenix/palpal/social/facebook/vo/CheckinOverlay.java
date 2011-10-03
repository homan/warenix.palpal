package org.dyndns.warenix.palpal.social.facebook.vo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class CheckinOverlay extends Overlay {
	private static final int CIRCLERADIUS = 2;

	private GeoPoint geopoint;
	private Checkin checkin;

	public CheckinOverlay(GeoPoint point, Checkin checkin) {
		geopoint = point;
		this.checkin = checkin;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		// Transfrom geoposition to Point on canvas
		Projection projection = mapView.getProjection();
		Point point = new Point();
		projection.toPixels(geopoint, point);

		// background
		Paint background = new Paint();
		background.setColor(Color.WHITE);
		RectF rect = new RectF();
		rect.set(point.x + 2 * CIRCLERADIUS, point.y - 4 * CIRCLERADIUS,
				point.x + 90, point.y + 12);

		// text "My Location"
		Paint text = new Paint();
		text.setAntiAlias(true);
		text.setColor(Color.BLUE);
		text.setTextSize(12);
		text.setTypeface(Typeface.MONOSPACE);

		// the circle to mark the spot
		Paint circle = new Paint();
		circle.setColor(Color.BLUE);
		circle.setAntiAlias(true);

		canvas.drawRoundRect(rect, 2, 2, background);
		canvas.drawCircle(point.x, point.y, CIRCLERADIUS, circle);
		canvas.drawText(checkin.message, point.x + 3 * CIRCLERADIUS, point.y
				+ 3 * CIRCLERADIUS, text);

	}
}
