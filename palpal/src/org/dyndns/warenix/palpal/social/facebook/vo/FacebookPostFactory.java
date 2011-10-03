package org.dyndns.warenix.palpal.social.facebook.vo;

import org.dyndns.warenix.palpal.social.facebook.FacebookException;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.CommentFeed;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.LinkFeed;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.NoteFeed;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.PhotoFeed;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.StatusFeed;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.StoryFeed;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.VideoFeed;
import org.json.JSONObject;

/**
 * create FacebookPost instance
 * 
 * @author warenix
 * 
 */
public class FacebookPostFactory {

	/**
	 * factory an Facebook object
	 * 
	 * @param type
	 *            FacebookPost type
	 * @param json
	 *            representation of object in json
	 * @param imagePool
	 *            imagepool for retrieving bitmap
	 * @return null if that object can't be created.
	 */
	public static FacebookPost factory(String type, JSONObject json) {
		try {
			if (type == null) {
				return new StoryFeed(json);
			} else if (type.equals(StatusFeed.TYPE)) {
				return new StatusFeed(json);
			} else if (type.equals(VideoFeed.TYPE) || type.equals("swf")) {
				return new VideoFeed(json);
			} else if (type.equals(PhotoFeed.TYPE)) {
				return new PhotoFeed(json);
			} else if (type.equals(LinkFeed.TYPE) ||
			// when post to album
					type.equals("normal")) {
				return new LinkFeed(json);
			} else if (type.equals(CommentFeed.TYPE)) {
				return new CommentFeed(json);
			} else if (type.equals(NoteFeed.TYPE)) {
				return new NoteFeed(json);
			}
		} catch (FacebookException e) {
			e.printStackTrace();
		}
		return null;
	}
}
