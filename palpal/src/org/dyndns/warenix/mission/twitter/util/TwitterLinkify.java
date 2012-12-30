package org.dyndns.warenix.mission.twitter.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.util.Linkify;
import android.text.util.Linkify.TransformFilter;
import android.widget.TextView;

public class TwitterLinkify {
	public static void addTwitterLinkify(TextView textView) {
		Linkify.addLinks(textView, Linkify.WEB_URLS);

//		// A transform filter that simply returns just the text captured by the
//		// first regular expression group.
//		TransformFilter mentionFilter = new TransformFilter() {
//			public final String transformUrl(final Matcher match, String url) {
//				return match.group(1);
//			}
//		};
//
//		// Match @mentions and capture just the username portion of the text.
//		Pattern pattern = Pattern.compile("@([A-Za-z0-9_-]+)");
//		// String scheme = "http://twitter.com/";
//		String scheme = "user://";
//		Linkify.addLinks(textView, pattern, scheme, null, mentionFilter);
//
//		// A transform filter that simply returns just the text captured by the
//		// first regular expression group.
//		TransformFilter hashtagFilter = new TransformFilter() {
//			public final String transformUrl(final Matcher match, String url) {
//				return match.group(0);
//			}
//		};
//
//		// Match @mentions and capture just the username portion of the text.
//		Pattern hashtagPattern = Pattern.compile("#(\\S+)");
//		// String scheme = "http://twitter.com/";
//		String hashtagScheme = "hashtag://";
//		Linkify.addLinks(textView, hashtagPattern, hashtagScheme, null,
//				hashtagFilter);
	}
}
