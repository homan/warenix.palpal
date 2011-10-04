package org.dyndns.warenix.embedly;

import java.net.URLEncoder;

import org.dyndns.warenix.palpal.social.twitter.imageURLParser.CamplusParser;
import org.dyndns.warenix.palpal.social.twitter.imageURLParser.InstagramParser;
import org.dyndns.warenix.palpal.social.twitter.imageURLParser.TwitpicParser;
import org.dyndns.warenix.palpal.social.twitter.imageURLParser.YfrogParser;
import org.dyndns.warenix.util.WebContent;

public class EmbedlyMaster {
	public static Embedable getEmbedable(String urlString) {
		try {
			if (
			// urlString.contains("twitpic.com")
			// urlString.contains("instagr.am") ||
			urlString.contains("campl.us")
			// || urlString.contains("yfrog.com")
			) {
				String imageURL = CamplusParser.extractImageUrl(urlString);
				if (imageURL != null) {
					return new Embedable(Embedable.TYPE_PHOTO, imageURL, "",
							"", "");
				} else {
					imageURL = InstagramParser.extractImageUrl(urlString);
					if (imageURL != null) {
						return new Embedable(Embedable.TYPE_PHOTO, imageURL,
								"", "", "");
					} else {
						imageURL = TwitpicParser.extractImageUrl(urlString);
						if (imageURL != null) {
							return new Embedable(Embedable.TYPE_PHOTO,
									imageURL, "", "", "");
						} else {
							imageURL = YfrogParser.extractImageUrl(urlString);
							if (imageURL != null) {
								return new Embedable(Embedable.TYPE_PHOTO,
										imageURL, "", "", "");
							}
						}
					}
				}
			} else {
				String embedlyUrl = String
						.format("http://api.embed.ly/1/oembed?url=%s&maxwidth=600&format=json",
								URLEncoder.encode(urlString));

				String jsonResponse = WebContent.getContent(embedlyUrl);
				return new Embedable(jsonResponse);
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		return null;
	}
}
