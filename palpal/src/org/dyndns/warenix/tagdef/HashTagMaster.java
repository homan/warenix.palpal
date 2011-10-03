package org.dyndns.warenix.tagdef;

import org.dyndns.warenix.util.WebContent;

import android.util.Log;

public class HashTagMaster {

	/**
	 * query hash tag definition from tagdef
	 * 
	 * @param hashtag
	 *            tag without # sign, e.g. palpal
	 * @return null if not found or error
	 */
	public static HashTagDef queryHashtag(String hashtag) {
		String url = String
				.format("http://api.tagdef.com/one.%s.json", hashtag);
		try {
			String responseJSON = WebContent.getContent(url);
			HashTagDef hashtagDef = HashTagDef.factory(responseJSON);
			Log.d("warenix", hashtagDef.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
