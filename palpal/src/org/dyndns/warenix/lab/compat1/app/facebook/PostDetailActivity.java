package org.dyndns.warenix.lab.compat1.app.facebook;

import org.dyndns.warenix.lab.compat1.R;
import org.dyndns.warenix.lab.compat1.util.AndroidUtil;
import org.dyndns.warenix.mission.facebook.FacebookPostAdapter;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import com.example.android.actionbarcompat.ActionBarActivity;

public class PostDetailActivity extends ActionBarActivity {

	public static final String BUNDLE_GRAPH_ID = "graph_id";

	protected ListView listView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.facebook_post_detail);

		// hide software keyboard
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		setupUI();

		View root = findViewById(R.id.root);
		AndroidUtil.playListAnimation(root);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.post_menu, menu);

		// Calling super after populating the menu is necessary here to ensure
		// that the
		// action bar helpers have a chance to handle this event.
		return super.onCreateOptionsMenu(menu);
	}

	void setupUI() {
		listView = (ListView) findViewById(android.R.id.list);

		String graphId = getIntent().getStringExtra(BUNDLE_GRAPH_ID);
		ListViewAdapter adapter = new FacebookPostAdapter(this, listView,
				graphId);
		listView.setAdapter(adapter);

		adapter.asyncRefresh();
	}
}
