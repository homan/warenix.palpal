package org.dyndns.warenix.palpal.social.twitter.activity;

import java.util.List;

import org.dyndns.warenix.palpal.PalPal;
import org.dyndns.warenix.palpal.selectableList.SelectableItem;
import org.dyndns.warenix.palpal.twitter.userList.SelectableUserItem;
import org.dyndns.warenix.palpal.twitter.userList.SelectableUserItemListController;
import org.dyndns.warenix.palpaltwitter.R;

import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.UserList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class UserListActivity extends Activity {

	public static final int REQUEST_CODE_LIST_FOLLOWERS = 1234;
	public static final int REQUEST_CODE_LIST_TWITTER_LISTS = 1235;
	SelectableUserItemListController controller;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selection_list_activity);

		int requestCode = getIntent().getIntExtra("requestCode", 0);
		switch (requestCode) {
		case REQUEST_CODE_LIST_FOLLOWERS:
			controller = new SelectableUserItemListController(this,
					R.id.listView1);
			break;
		case REQUEST_CODE_LIST_TWITTER_LISTS:
			Twitter twitter = PalPal.getTwitterClient();
			long cursor = -1;
			PagableResponseList<UserList> lists;
			try {
				do {
					lists = twitter.getUserLists(twitter.getScreenName(),
							cursor);
					for (UserList list : lists) {
						Log.d("palpal",
								"id:" + list.getId() + ", name:"
										+ list.getName() + ", description:"
										+ list.getDescription() + ", slug:"
										+ list.getSlug() + ""
										+ list.getMemberCount());
					}
				} while ((cursor = lists.getNextCursor()) != 0);
				return;
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}

		controller.refresh();

		Button add = (Button) findViewById(R.id.add);
		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String mentionString = "";
				List<SelectableItem> list = controller.getSelectedList();
				for (SelectableItem item : list) {
					mentionString += "@"
							+ ((SelectableUserItem) item).getUsername() + " ";
				}

				Log.d("palpal", "mention: " + mentionString);

				Intent data = new Intent();
				data.putExtra("selected_user", mentionString);
				setResult(RESULT_OK, data);
				finish();
			}
		});

	}
}
