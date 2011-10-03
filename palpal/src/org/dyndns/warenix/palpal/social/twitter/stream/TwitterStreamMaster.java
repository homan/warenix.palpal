package org.dyndns.warenix.palpal.social.twitter.stream;

import twitter4j.FilterQuery;
import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;

public class TwitterStreamMaster {

	public static long[] getUserFriends(final Twitter twitter)
			throws TwitterException {
		IDs followerIDs = twitter.getFriendsIDs(-1);
		long[] followArray = followerIDs.getIDs();
		System.out.println("follower count: " + followArray.length);
		return followArray;
	}

	public static void queryStream(final TwitterStream twitterStream,
			final String[] trackArray, final long[] followArray,
			final double[][] locations) {

		FilterQuery filterQuery = new FilterQuery();
		filterQuery.track(trackArray);
		filterQuery.follow(followArray);
		filterQuery.locations(locations);

		twitterStream.filter(filterQuery);
	}
}
