package org.dyndns.warenix.palpal.social.twitter.imageURLParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dyndns.warenix.util.WebContent;

import android.util.Log;

public class YfrogParser {

	/**
	 * example: http://campl.us/Hvz
	 * 
	 * @param imageSource
	 * @return
	 */
	public static String extractImageUrl(String imageSource) {
		try {
			String twitpicHtml = WebContent.getContent(imageSource);
			Pattern p = Pattern.compile(
					"<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>",
					Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(twitpicHtml);
			String twitpitUrl = "";
			while (m.find()) {
				twitpitUrl = m.start(1) != -1 ? m.group(1) : m.group(2);
				if (twitpitUrl.contains("yfrog")) {
					Log.d("warenix", twitpitUrl);
					return twitpitUrl;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
