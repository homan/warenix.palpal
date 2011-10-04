package org.dyndns.warenix.palpal.social.twitter.commonTask;

import org.dyndns.warenix.db.SimpleStorableManager;
import org.dyndns.warenix.palpal.PalPal;
import org.dyndns.warenix.palpal.social.twitter.storable.FavouriteStorable;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.content.Context;

public class FavouriteTwitterTask extends CommonTask {

	/**
	 * 
	 */
	private static final long serialVersionUID = -625823305193402176L;
	public String socialNetworkMessageId;
	boolean setFollow;

	public FavouriteTwitterTask(String socialNetworkMessageId, boolean setFollow) {
		this.socialNetworkMessageId = socialNetworkMessageId;
		this.setFollow = setFollow;
	}

	@Override
	public Object execute(Context context) {

		try {
			SimpleStorableManager db = new SimpleStorableManager(context);
			Twitter twitter = PalPal.getTwitterClient();
			long messageId = Long.parseLong(socialNetworkMessageId);
			if (setFollow) {
				twitter.createFavorite(messageId);
				db.insertItem(new FavouriteStorable(socialNetworkMessageId));
			} else {
				twitter.destroyFavorite(messageId);
				db.deleteItemByKey(new FavouriteStorable(socialNetworkMessageId));
			}

			return true;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (TwitterException e) {
			e.printStackTrace();
		}

		return false;
	}

}
