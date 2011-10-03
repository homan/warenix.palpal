package mapviewballoons.example;

import org.dyndns.warenix.palpal.social.facebook.vo.Checkin;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class FacebookCheckinOverlayItem extends OverlayItem {
	public Checkin checkin;

	public FacebookCheckinOverlayItem(GeoPoint point, String title,
			String snippet) {
		super(point, title, snippet);
	}

	public FacebookCheckinOverlayItem(GeoPoint point, String title,
			String snippet, Checkin checkin) {
		super(point, title, snippet);

		this.checkin = checkin;
	}

}
