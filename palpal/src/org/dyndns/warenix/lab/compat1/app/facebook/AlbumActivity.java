package org.dyndns.warenix.lab.compat1.app.facebook;

import org.dyndns.warenix.image.CachedWebImage;
import org.dyndns.warenix.lab.compat1.R;
import org.dyndns.warenix.lab.compat1.util.AndroidUtil;
import org.dyndns.warenix.mission.facebook.FacebookAlbumPhotoAdapter;
import org.dyndns.warenix.mission.facebook.util.FacebookMaster;
import org.dyndns.warenix.mission.timeline.TimelineListFragment;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;
import org.dyndns.warenix.util.WLog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.example.android.actionbarcompat.ActionBarActivity;

public class AlbumActivity extends ActionBarActivity {
	private static final String TAG = "AlbumActivity";

	static {
		CachedWebImage.setCacheDir("palpal");
		WLog.setAppName("palpal");
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_album);

		FacebookMaster.restoreFacebook(getApplicationContext());

		setTitle("Albums");
		AndroidUtil.hideSoftwareKeyboard(this);

		Fragment f = TimelineListFragment.newInstance(5);
		Bundle extras = FacebookAlbumPhotoAdapter.getExtra("me", 0, 50);
		f.setArguments(extras);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_container, f).commitAllowingStateLoss();
	}

	public void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.reply_menu, menu);

		// Calling super after populating the menu is necessary here to ensure
		// that the
		// action bar helpers have a chance to handle this event.
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.menu_compose:
			// construct and send message
			Toast.makeText(this, "(fake) queued message for posting",
					Toast.LENGTH_SHORT).show();

			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
