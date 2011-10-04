package org.dyndns.warenix.palpal.social.twitter.commonTask;

import org.dyndns.warenix.db.SimpleStorableManager;
import org.dyndns.warenix.palpal.PalPal;
import org.dyndns.warenix.palpal.social.twitter.storable.FriendStorable;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.content.Context;
import android.util.Log;

public class FollowUserTask extends CommonTask {

	/**
	 * 
	 */
	private static final long serialVersionUID = -625823305193402176L;
	public String username;
	boolean setFollow;

	public FollowUserTask(String username, boolean setFollow) {
		this.username = username;
		this.setFollow = setFollow;
	}

	@Override
	public Object execute(Context context) {

		try {
			SimpleStorableManager db = new SimpleStorableManager(context);
			Twitter twitter = PalPal.getTwitterClient();

			Log.d("palpal", "try to " + (setFollow ? "follow " : "unfollow ")
					+ username);
			if (setFollow) {
				twitter.createFriendship(username);
				Log.d("palpal", "followed " + username);
				db.insertItem(new FriendStorable(username));
				Log.d("palpal", "stored on db " + username);
			} else {
				twitter.destroyFriendship(username);
				Log.d("palpal", "unfollowed " + username);
				db.deleteItemByKey(new FriendStorable(username));
				Log.d("palpal", "removed from db " + username);
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
