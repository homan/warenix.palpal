package org.dyndns.warenix.palpal.social.facebook.vo;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

/**
 * sharable via facebook
 * 
 * @author warenix
 * 
 */
public interface FacebookEditable {

	/**
	 * make facebook feed become editable
	 * 
	 * @return
	 */
	public View getFacebookEditable(final FacebookPost feed, final View view,
			final Context context,
			final HashMap<String, SoftReference<Bitmap>> imagePool);

	/**
	 * extract properties from views
	 */
	public FacebookEditable getFacebookFeed(final View view);

	/**
	 * update corresponding value
	 * @param value TODO
	 */
	public void updateAttributeByKey(final String key, String value);
}
