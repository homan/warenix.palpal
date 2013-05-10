package org.dyndns.warenix.mission.twitter.util;

import java.util.HashMap;

public class TwitterLinkCache {

	public static final String NO_PERVIEW = "NO_PREVIEW";
	private static HashMap<String, String> sLinkCache = new HashMap<String, String>();

	public static synchronized void addPreview(String link,
			String previewImageUrl) {
		sLinkCache.put(link, previewImageUrl);
	}

	public static synchronized String getPreview(String link) {
		return sLinkCache.get(link);
	}

}
