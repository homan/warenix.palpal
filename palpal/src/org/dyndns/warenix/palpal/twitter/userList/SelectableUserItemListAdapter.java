package org.dyndns.warenix.palpal.twitter.userList;

import java.util.ArrayList;

import org.dyndns.warenix.palpal.message.TwitterDBManager;
import org.dyndns.warenix.palpal.selectableList.SelectableListAdapter;
import org.dyndns.warenix.palpal.social.twitter.Friend;

import android.content.Context;

public class SelectableUserItemListAdapter extends SelectableListAdapter {

	public SelectableUserItemListAdapter(Context context) {
		super(context);
		refresh();
	}

	public void refresh() {
		loadMessageFromDB();
	}

	void loadMessageFromDB() {
		clear();

		// fetch friends
		TwitterDBManager db = new TwitterDBManager(context);

		ArrayList<Friend> result = db
				.getFriendList(Friend.FRIEND_RELATIONSHIP_FOLLOWING);
		int size = 0;
		for (Friend friend : result) {
			addSelectableItem(new SelectableUserItem(size++, false, this,
					friend));
		}

		runNotifyDataSetInvalidated();
		initSelectedIndex(size);
	}

}
